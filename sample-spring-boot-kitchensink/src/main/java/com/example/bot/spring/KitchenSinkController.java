/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.example.bot.spring;

import java.io.IOException;

import java.io.UncheckedIOException;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import com.linecorp.bot.client.LineMessagingService;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.BeaconEvent;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.UnfollowEvent;


import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;

import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;

import com.linecorp.bot.model.message.ImageMessage;

import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;


import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import java.util.ArrayList;
import java.util.HashMap;
import javax.print.DocFlavor;

import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import retrofit2.Response;



@Slf4j
@LineMessageHandler
public class KitchenSinkController {
    
    static boolean PRINT_VERBOSE = false;
    final String channalKey ="xlHZZWi0tluGrr9/pPGtO6WK4h6Sbs8Uw9VdILnynXrv7QyRgCgBPHc6/LQma3LlDMOr5nsp9C88HUY0omCxnQoUTUlztfcWE93h2/ro05fZMWT72MzNqsBYXX80ZnehBPHXEtfXdiyYMjlK2RmTMgdB04t89/1O/w1cDnyilFU=";

static String  gameStatus = "notPlayYet";
static HashMap<String,Boolean> eventPressed=new HashMap<String,Boolean>();
boolean joined = false;
boolean playing = false;
int round = 0;
    @Autowired
    private LineMessagingService lineMessagingService;

    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws IOException {
        TextMessageContent message = event.getMessage();
        handleTextContent(event.getReplyToken(), event, message);
    }

    @EventMapping
    public void handleStickerMessageEvent(MessageEvent<StickerMessageContent> event) {
        handleSticker(event.getReplyToken(), event.getMessage());
    }

    @EventMapping
    public void handleUnfollowEvent(UnfollowEvent event) {
        log.info("unfollowed this bot: {}", event);
    }

    @EventMapping
    public void handleFollowEvent(FollowEvent event) {
        String replyToken = event.getReplyToken();
        this.replyText(replyToken, "Got followed event");
    }

    @EventMapping
    public void handleJoinEvent(JoinEvent event) {
        String replyToken = event.getReplyToken();
        this.replyText(replyToken, "Joined " + event.getSource());
    }
    private void pushText(@NonNull String userId, @NonNull String messages) throws IOException {
       TextMessage textMessage = new TextMessage(messages);
PushMessage pushMessage = new PushMessage(
        userId,
        textMessage
);

Response<BotApiResponse> response =
        LineMessagingServiceBuilder
                .create(channalKey)
                .build()
                .pushMessage(pushMessage)
                .execute();
System.out.println(response.code() + " " + response.message());
    }
    
    private void pushImage(@NonNull String userId, @NonNull String imageUrl) throws IOException {
      // TextMessage textMessage = new TextMessage(messages);
      ImageMessage imageMessage = new ImageMessage(imageUrl, imageUrl);
PushMessage pushMessage = new PushMessage(
        userId,
        imageMessage
);

Response<BotApiResponse> response =
        LineMessagingServiceBuilder
                .create(channalKey)
                .build()
                .pushMessage(pushMessage)
                .execute();
System.out.println(response.code() + " " + response.message());
    }
    
    @EventMapping
    public void handlePostbackEvent(PostbackEvent event) throws IOException {
        
        String replyToken = event.getReplyToken();
        KitchenSinkController.gameStatus = event.getPostbackContent().getData(); // JoinGroup,Card,Color
        
        String userId = event.getSource().getUserId();
        String userName ="";  
                if (userId != null) {
                    Response<UserProfileResponse> response = lineMessagingService
                            .getProfile(userId)
                            .execute();
                    if (response.isSuccessful()) {
                        UserProfileResponse profiles = response.body();
                        userName = profiles.getDisplayName();
                        
                    } else {
                        this.replyText(replyToken, response.errorBody().string());
                    }
                } else {
                    this.replyText(replyToken, "Bot can't use profile API without user ID");
                }
        
        //this.replyText(replyToken, "before Scoreboard");
        if ((KitchenSinkController.gameStatus.startsWith("JoinGroup"))&&(!joined)) {
            joined = true;
            this.replyText(replyToken, userName+ " : You have joined Uno " + KitchenSinkController.gameStatus.substring(4));
        ArrayList<String> playerNames = new ArrayList<String>();
     ArrayList<String> playerClasses = new ArrayList<String>();
        //this.pushText(userId, "before Scoreboard");
        KitchenSinkController.eventPressed.put(userId,false);
        playerNames.add("BOT1");
        playerNames.add("BOT2");
        playerNames.add("BOT3");
        playerNames.add(userName);
        playerClasses.add("com.example.bot.spring.dummy3_UnoPlayer");
        playerClasses.add("com.example.bot.spring.nds63_UnoPlayer"); 
        playerClasses.add("com.example.bot.spring.dummy2_UnoPlayer");
        
        playerClasses.add("com.example.bot.spring.dummy1_UnoPlayer");
      
       try {
            
            Scoreboard s = new Scoreboard(playerNames.toArray(new String[0]));
            this.pushText(userId, "after Scoreboard");
                Game g = new Game(s,playerClasses,userId);
                this.pushText(userId, "before play");
              KitchenSinkController.gameStatus = "Card0"; // Start Valueof PostBackEvent Select Card
              playing = true;
                g.play();
            playerNames.clear();
            playerClasses.clear();
            round = 0;
            joined = false;
            playing = false;
            //KitchenSinkController.gameStatus = "notPlayYet";
        }
        catch (Exception e) {
            this.pushText(userId,e.getMessage());
        }
        }  else{
            if (KitchenSinkController.gameStatus.startsWith("Card")){
            round = round +1;
            int temp = Integer.parseInt(KitchenSinkController.gameStatus.substring(4));
            if (temp ==round){
                // if ((KitchenSinkController.gameStatus.substring(2,6).equals("Card"))&&(temp ==round)){
                KitchenSinkController.eventPressed.replace(userId,true);
    //this.pushText(userId,status);
            } else {
                round = round -1;
            }
            
        } 
        }
    
    }
//    public void handlePostbackEvent(PostbackEvent event) throws IOException {
//         ArrayList<String> playerNames = new ArrayList<String>();
//     ArrayList<String> playerClasses = new ArrayList<String>();
//        String replyToken = event.getReplyToken();
//        String groupJoin = event.getPostbackContent().getData();
//        String userId = event.getSource().getUserId();
//        String userName ="";  
//                if (userId != null) {
//                    Response<UserProfileResponse> response = lineMessagingService
//                            .getProfile(userId)
//                            .execute();
//                    if (response.isSuccessful()) {
//                        UserProfileResponse profiles = response.body();
//                        userName = profiles.getDisplayName();
//                        
//                    } else {
//                        this.replyText(replyToken, response.errorBody().string());
//                    }
//                } else {
//                    this.replyText(replyToken, "Bot can't use profile API without user ID");
//                }
//        this.replyText(replyToken, userName+ " : You have joined Uno " + groupJoin.substring(4));
//        //this.replyText(replyToken, "before Scoreboard");
//        
//                
//        
//        this.pushText(userId, "before Scoreboard");
//        playerNames.add("BOT1");
//        playerNames.add("BOT2");
//        playerNames.add("BOT3");
//        playerNames.add(userName);
//        playerClasses.add("com.example.bot.spring.dummy3_UnoPlayer");
//        playerClasses.add("com.example.bot.spring.nds63_UnoPlayer"); 
//        playerClasses.add("com.example.bot.spring.dummy2_UnoPlayer");
//        
//        playerClasses.add("com.example.bot.spring.dummy1_UnoPlayer");
//        
//       try {
//            
//            Scoreboard s = new Scoreboard(playerNames.toArray(new String[0]));
//            this.pushText(userId, "after Scoreboard");
//                Game g = new Game(s,playerClasses,userId);
//                this.pushText(userId, "before play");
//                g.play();
//            playerNames.clear();
//            playerClasses.clear();
//           
//        }
//        catch (Exception e) {
//            this.pushText(userId,e.getMessage());
//        }
//        
//                
//    }

    @EventMapping
    public void handleBeaconEvent(BeaconEvent event) {
        String replyToken = event.getReplyToken();
        this.replyText(replyToken, "Got beacon message " + event.getBeacon().getHwid());
    }

    @EventMapping
    public void handleOtherEvent(Event event) {
        log.info("Received message(Ignored): {}", event);
    }

    private void reply(@NonNull String replyToken, @NonNull Message message) {
        reply(replyToken, Collections.singletonList(message));
    }

    private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
        try {
            Response<BotApiResponse> apiResponse = lineMessagingService
                    .replyMessage(new ReplyMessage(replyToken, messages))
                    .execute();
            log.info("Sent messages: {}", apiResponse);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void replyText(@NonNull String replyToken, @NonNull String message) {
        if (replyToken.isEmpty()) {
            throw new IllegalArgumentException("replyToken must not be empty");
        }
        if (message.length() > 1000) {
            message = message.substring(0, 1000 - 2) + "……";
        }
        this.reply(replyToken, new TextMessage(message));
    }

    private void handleSticker(String replyToken, StickerMessageContent content) {
        reply(replyToken, new StickerMessage(
                content.getPackageId(), content.getStickerId())
        );
    }

    private void handleTextContent(String replyToken, Event event, TextMessageContent content)
            throws IOException {
        String text = content.getText();

        log.info("Got text message from {}: {}", replyToken, text);
        switch (text) {
            case "play uno": {  // อย่าลืมว่า ต้องมีตัว check ไม่ให้ พิมพ์ play uno ซ้ำ notPlayyet
                if (!playing){
                joined = false;
                String imageUrl = createUri("/static/buttons/1040.jpg");
                CarouselTemplate carouselTemplate = new CarouselTemplate(
                        Arrays.asList(
                                new CarouselColumn(null, "GROUP1", "BOT1 : Conservative\nBOT2 : Greedy\nBOT3 : Witty", Arrays.asList(
                                        
                                        new PostbackAction("Join Group1",
                                                           "JoinGroup1")
                                )),
                                new CarouselColumn(null,"GROUP2", "BOT2 : Greedy\nBOT3: Witty\nBOT4 : Carefully", Arrays.asList(
                                        new PostbackAction("Join Group2",
                                                           "JoinGroup2")
                                        
                                )),
                                new CarouselColumn(null,"GROUP3", "BOT3 : Witty\nBOT4 : Carefully\nBOT1 : Conservative", Arrays.asList(
                                        new PostbackAction("Join Group3",
                                                           "JoinGroup3")
                                        
                                )),
                                new CarouselColumn(null, "GROUP1", "BOT4 : Carefully\nBOT1 : Conservative\nBOT2 : Greedy", Arrays.asList(
                                        
                                        new PostbackAction("Join Group4",
                                                           "JoinGroup4")
                                ))
                                        
                        ));
                TemplateMessage templateMessage = new TemplateMessage("Your Line App is not support Please Update", carouselTemplate);
                this.reply(replyToken, templateMessage);
                break;
            } 
                break;
            }
            case "profile": {
                String userId = event.getSource().getUserId();
                if (userId != null) {
                    Response<UserProfileResponse> response = lineMessagingService
                            .getProfile(userId)
                            .execute();
                    if (response.isSuccessful()) {
                        UserProfileResponse profiles = response.body();
                        this.reply(
                                replyToken,
                                Arrays.asList(new TextMessage(
                                                      "Display name: " + profiles.getDisplayName()),
                                              new TextMessage("Status message: "
                                                              + profiles.getStatusMessage()))
                        );
                    } else {
                        this.replyText(replyToken, response.errorBody().string());
                    }
                } else {
                    this.replyText(replyToken, "Bot can't use profile API without user ID");
                }
                break;
            }
            case "bye": {
                Source source = event.getSource();
                if (source instanceof GroupSource) {
                    this.replyText(replyToken, "Leaving group");
                    lineMessagingService.leaveGroup(((GroupSource) source).getGroupId())
                                        .execute();
                } else if (source instanceof RoomSource) {
                    this.replyText(replyToken, "Leaving room");
                    lineMessagingService.leaveRoom(((RoomSource) source).getRoomId())
                                        .execute();
                } else {
                    this.replyText(replyToken, "Bot can't leave from 1:1 chat");
                }
                break;
            }
            case "confirm": {
                ConfirmTemplate confirmTemplate = new ConfirmTemplate(
                        "Do it?",
                        new MessageAction("Yes", "Yes!"),
                        new MessageAction("No", "No!")
                );
                TemplateMessage templateMessage = new TemplateMessage("Confirm alt text", confirmTemplate);
                this.reply(replyToken, templateMessage);
                break;
            }
            
            case "buttons": {
                String imageUrl = createUri("/static/buttons/1040.jpg");
                ButtonsTemplate buttonsTemplate = new ButtonsTemplate(
                        imageUrl,
                        "My button sample",
                        "Hello, my button",
                        Arrays.asList(
                                new URIAction("Go to line.me",
                                              "https://line.me"),
                                new PostbackAction("Say hello1",
                                                   "hello こんにちは"),
                                new PostbackAction("言 hello2",
                                                   "hello こんにちは",
                                                   "hello こんにちは"),
                                new MessageAction("Say message",
                                                  "Rice=米")
                        ));
                TemplateMessage templateMessage = new TemplateMessage("Button alt text", buttonsTemplate);
                this.reply(replyToken, templateMessage);
                break;
            }
            case "carousel": {
                String imageUrl = createUri("/static/buttons/1040.jpg");
                CarouselTemplate carouselTemplate = new CarouselTemplate(
                        Arrays.asList(
                                new CarouselColumn(imageUrl, "hoge", "fuga", Arrays.asList(
                                        new URIAction("Go to line.me",
                                                      "https://line.me"),
                                        new PostbackAction("Say hello1",
                                                           "hello こんにちは")
                                )),
                                new CarouselColumn(imageUrl, "hoge", "fuga", Arrays.asList(
                                        new PostbackAction("言 hello2",
                                                           "hello こんにちは",
                                                           "hello こんにちは"),
                                        new MessageAction("Say message",
                                                          "Rice=米")
                                ))
                        ));
                TemplateMessage templateMessage = new TemplateMessage("Carousel alt text", carouselTemplate);
                this.reply(replyToken, templateMessage);
                break;
            }
            default:
                log.info("Returns echo message {}: {}", replyToken, text);
                this.replyText(
                        replyToken,
                        text
                );
                break;
        }
    }

    private static String createUri(String path) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                                          .path(path).build()
                                          .toUriString();
    }

   
}

