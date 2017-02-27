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

import com.google.common.base.Splitter;
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
// Eak Newest import javax.print.DocFlavor;

import lombok.NonNull;
// Eak Newest import lombok.Value;
import lombok.extern.slf4j.Slf4j;
// Eak Newest import okhttp3.ResponseBody;
import retrofit2.Response;



@Slf4j
@LineMessageHandler
public class KitchenSinkController {
    
    static boolean PRINT_VERBOSE = false;  
     final String channalKey ="xlHZZWi0tluGrr9/pPGtO6WK4h6Sbs8Uw9VdILnynXrv7QyRgCgBPHc6/LQma3LlDMOr5nsp9C88HUY0omCxnQoUTUlztfcWE93h2/ro05fZMWT72MzNqsBYXX80ZnehBPHXEtfXdiyYMjlK2RmTMgdB04t89/1O/w1cDnyilFU=";

static HashMap<String,String> gameStatus = new HashMap<String,String>();
static HashMap<String,Boolean> eventPressed = new HashMap<String,Boolean>();
static HashMap<String,Boolean> colorPressed = new HashMap<String,Boolean>();
static HashMap<String,Boolean> joined = new HashMap<String,Boolean>();
static HashMap<String,Boolean> playing = new HashMap<String,Boolean>();
static HashMap<String,Integer> round = new HashMap<String,Integer>();

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
        //log.info("unfollowed this bot: {}", event);
    }

    @EventMapping
    public void handleFollowEvent(FollowEvent event) {
        String  welcome = "Welcome to UNO Card game Bot \n"
                + "To play a game please type \"menu\n";
        String replyToken = event.getReplyToken();
        this.replyText(replyToken, welcome);
    }

    @EventMapping
    public void handleJoinEvent(JoinEvent event) {
        
        //this.replyText(replyToken, "Joined " + event.getSource());
        String  welcome = "Welcome to UNO Card game Bot \n"
                + "To play a game please type \"menu\n";
        String replyToken = event.getReplyToken();
        this.replyText(replyToken, welcome);
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
    public void writeRow(String userId,String ID,String displayName,String status) throws IOException{
       // String fileName = "playerName.txt";
//String fileLocation = new File("static/buttons/playerName.txt").getAbsolutePath(); 
        //String fileLocation = createUri("/static/buttons/playerName.txt");
  //String fileLocation ="playerName.txt";
                           String imageUrl = "playerName.txt";
                Path inputPath = Paths.get(imageUrl);
                    Path fullPath = inputPath.toAbsolutePath();
                    this.pushText(userId,"full is:" +fullPath.toString());
         String fileLocation = fullPath.toString();
		File file = new File(fileLocation);
		
		FileWriter writer;
		try {
			
			writer = new FileWriter(file, true);  //True = Append to file, false = Overwrite
			writer.write(ID+","+displayName+","+status+"\n");
			writer.close();
			this.pushText(userId,"write file"+file.getAbsolutePath());
			//System.out.println("Write success!");
			
		} catch (IOException e) {
			// TODO Auto-generated tcatch block
		//	e.printStackTrace();
                this.pushText(userId,e.getMessage());
		}
    }
    
        private ArrayList<Customer>  readData(String userId) throws Exception {
            ArrayList<Customer> myArrList = new ArrayList<Customer>();
            Customer  customer = new Customer();
         //   String fileName = "playerName.txt";
//String fileLocation = new File("static/buttons/playerName.txt").getAbsolutePath();
    //        String fileLocation = createUri("/static/buttons/playerName.txt");
   // String fileLocation = "playerName.txt";
//        BufferedReader br = new BufferedReader(new FileReader(
//            fileLocation));
//                         String imageUrl = "/static/buttons/UNOback2.jpg";
//                Path inputPath = Paths.get(imageUrl);
//                    Path fullPath = inputPath.toAbsolutePath();
//                    this.pushText(userId,"full is:" +fullPath.toString());
                    String imageUrl = createUri("/static/buttons/UNOback2.jpg");
                    
                    Path inputPath = Paths.get(imageUrl);
                    Path fullPath = inputPath.toAbsolutePath();
                    Path fileName = inputPath.getFileName();
                    this.pushText(userId,"full is:" +fullPath.toString());
                    this.pushText(userId,"filename is:" +fileName.toString());
                    String tempStr = fullPath.toString();
                    String regex = "\\s*\\bUNOback2.jpg\\b\\s*";
tempStr = tempStr.replaceAll(regex, "");

         String fileLocation = tempStr+"playerName.txt";
        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileLocation))) {
        
        String playerLine = br.readLine();
        while (playerLine != null) {
            Scanner line = new Scanner(playerLine).useDelimiter(",");
            customer.setId(line.next());
            customer.setDisplayName(line.next());
            customer.setStatus(line.next());
            this.pushText(userId, customer.toString());
            myArrList.add(customer);
            playerLine = br.readLine();
        }
        } catch (IOException e) {
			this.pushText(userId,"error"+ e.getMessage());
		}
        return myArrList;
    }
        Function<String, Customer> mapLineToCustomer = new Function<String, Customer>() {

    public Customer apply(String line) {

        Customer customer = new Customer();

        List<String> customerPieces = Splitter.on(",").trimResults()
                .omitEmptyStrings().splitToList(line);

        customer.setId(customerPieces.get(0));
        customer.setDisplayName(customerPieces.get(1));
        customer.setStatus(customerPieces.get(2));

        return customer;
    }
};     
        
     private ArrayList<Customer>  readFile(String userId) throws Exception {
//                         String imageUrl = "/static/buttons/playerName.txt";
//                Path inputPath = Paths.get(imageUrl);
//                    Path fullPath = inputPath.toAbsolutePath();
//                    this.pushText(userId,"full is:" +fullPath.toString());
//         String fileLocation = fullPath.toString();
String imageUrl = createUri("/static/buttons/UNOback2.jpg");
                    
                    Path inputPath = Paths.get(imageUrl);
                    Path fullPath = inputPath.toAbsolutePath();
                    Path fileName = inputPath.getFileName();
                    this.pushText(userId,"full is:" +fullPath.toString());
                    this.pushText(userId,"filename is:" +fileName.toString());
                    String tempStr = fullPath.toString();
                    String regex = "\\s*\\bUNOback2.jpg\\b\\s*";
tempStr = tempStr.replaceAll(regex, "");

         String fileLocation = tempStr+"playerName.txt";
         List<Customer> customer = Files
            .lines(Paths
                    .get(fileLocation))
            .map(mapLineToCustomer).collect(Collectors.toList());
        return (ArrayList<Customer>) customer; 
     }   
        private void handleTextContent(String replyToken, Event event, TextMessageContent content)
            throws IOException {
        String text = content.getText();
String userId = event.getSource().getUserId();

         if (KitchenSinkController.playing.containsKey(userId)) {
            //KitchenSinkController.playing.replace(userId, false);
        } else {
            KitchenSinkController.playing.put(userId, false);
         }  // New
        switch (text.toLowerCase()) {
            case "menu": {
               // อย่าลืมว่า ต้องมีตัว check ไม่ให้ พิมพ์ play uno ซ้ำ notPlayyet
            
               if (!KitchenSinkController.playing.get(userId)){
                String imageUrl = createUri("/static/buttons/UNOback2.jpg");
                ButtonsTemplate buttonsTemplate = new ButtonsTemplate(
                        imageUrl,
                        "Welcome to UNO game",
                        "Select the following choices",
                        Arrays.asList(
                                new URIAction("How to play UNO",
                                              "http://www.wikihow.com/Play-UNO"),
                               
                                new PostbackAction("READ ME first",
                                        "00ReadME2"),
                                new PostbackAction("Play with BOT",
                                                   "00PlayBOT"),
                                new MessageAction("Play with friends",
                                                  "Coming Soon in the LINE near you")
                                
                        ));
                TemplateMessage templateMessage = new TemplateMessage("Please Select Menu", buttonsTemplate);
                this.reply(replyToken, templateMessage);
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
            case "writerow" : {
                writeRow(userId,"1", "EAK", "0");
                writeRow(userId,"2", "Ozone","1");
                //this.replyText(replyToken,"Finished write");
            break;
            }
            case "readdata" : {
                ArrayList<Customer> myArrList = new ArrayList<Customer>();
            try {
                myArrList = readData(userId);
            } catch (Exception ex) {
                this.replyText(replyToken,ex.getMessage());//Logger.getLogger(KitchenSinkController.class.getName()).log(Level.SEVERE, null, ex);
            }
                String tempStr ="";
                for (int i=0;i<myArrList.size();i++){
                    tempStr = tempStr + myArrList.get(i).toString()+";";
                }        
                this.replyText(replyToken,tempStr);
            break;
            }
            case "readfile" : {

                ArrayList<Customer> myArrList = new ArrayList<Customer>();
            try {
                myArrList = readFile(userId);
            } catch (Exception ex) {
                this.replyText(replyToken,"ERROR:"+ex.getMessage());//Logger.getLogger(KitchenSinkController.class.getName()).log(Level.SEVERE, null, ex);
            }
                String tempStr ="";
                for (int i=0;i<myArrList.size();i++){
                    tempStr = tempStr + myArrList.get(i).toString()+";";
                }        
                this.replyText(replyToken,tempStr);
            break;
            }
            case "path" : {
                    String imageUrl = createUri("/static/buttons/UNOback2.jpg");
                    
                    Path inputPath = Paths.get(imageUrl);
                    Path fullPath = inputPath.toAbsolutePath();
                    Path fileName = inputPath.getFileName();
                    this.pushText(userId,"full is:" +fullPath.toString());
                    this.pushText(userId,"filename is:" +fileName.toString());
                    String tempStr = fullPath.toString();
                    String regex = "\\s*\\bUNOback2.jpg\\b\\s*";
tempStr = tempStr.replaceAll(regex, "");
 this.pushText(userId,"only dir is:" +tempStr);
  this.pushText(userId,"new filename is:" +tempStr+"playerName.txt");
                   
                    
            break;
            }
            case "test" : this.replyText(replyToken,text);
            break;
                
            default:
                //log.info("Returns echo message {}: {}", replyToken, text);
                //this.replyText(replyToken,text);
                this.replyText(replyToken,"To play game just type \" menu \" ");
                break;
        }
    }
    @EventMapping
    public void handlePostbackEvent(PostbackEvent event) throws IOException {
        
        String replyToken = event.getReplyToken();
        String eventData = event.getPostbackContent().getData();  // New
        // KitchenSinkController.gameStatus = event.getPostbackContent().getData(); // JoinGroup,Card,Color
        
        String userId = event.getSource().getUserId();               // New
        if (KitchenSinkController.gameStatus.containsKey(userId)) {
            KitchenSinkController.gameStatus.replace(userId, eventData);
        } else {
            KitchenSinkController.gameStatus.put(userId, eventData);
        }  // New
            
// this.pushText(userId, KitchenSinkController.gameStatus.get(userId));
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
        
if (eventData.equals("00PlayBOT")){
    if (!KitchenSinkController.playing.get(userId)){
                    if (KitchenSinkController.round.containsKey(userId)) {
            KitchenSinkController.round.replace(userId, 0);
        } else {
            KitchenSinkController.round.put(userId, 0);
        }  // New
         
                    if (KitchenSinkController.joined.containsKey(userId)) {
            KitchenSinkController.joined.replace(userId, false);
        } else {
            KitchenSinkController.joined.put(userId, false);
        }  // New
                // joined = false;    // 267F คนพิการ  //263A หน้ายิ้ม  //2614 ร่ม  //2603 //26C4 หิมะ //\u26F9 นักบาส //2620  //26D1 Carefully
               // Eak Newest String imageUrl = createUri("/static/buttons/1040.jpg");    //2640 สีชมพู  /2642 สีฟ้า
               this.pushText(userId, "Please select GROUP to play");
                CarouselTemplate carouselTemplate = new CarouselTemplate(
                        Arrays.asList(
                                new CarouselColumn(null, "GROUP1", "\uD83D\uDC2F : Conservative BOT\n\uD83D\uDC37 : Greedy BOT\n\uD83D\uDC38 : Crafty BOGT", Arrays.asList(
                                        
                                        new PostbackAction("Join Group1",
                                                           "JoinGroup1")
                                )),
                                new CarouselColumn(null,"GROUP2", "\uD83D\uDC37 : Greedy BOT\n\uD83D\uDC38 : Crafty BOT\n\uD83D\uDC3C : Carefully BOT", Arrays.asList(
                                        new PostbackAction("Join Group2",
                                                           "JoinGroup2")
                                        
                                )),
                                new CarouselColumn(null,"GROUP3", "\uD83D\uDC38 : Crafty BOT\n\uD83D\uDC3C : Carefully BOT\n\uD83D\uDC2F : Conservative BOT", Arrays.asList(
                                        new PostbackAction("Join Group3",
                                                           "JoinGroup3")
                                        
                                )),
                                new CarouselColumn(null, "GROUP4", "\uD83D\uDC3C : Carefully BOT \n\uD83D\uDC2F : Conservative BOT \n\uD83D\uDC37 : Greedy BOT", Arrays.asList(
                                        
                                        new PostbackAction("Join Group4",
                                                           "JoinGroup4")
                                ))
                                        
                        ));
                TemplateMessage templateMessage = new TemplateMessage("Your Line App is not support Please Update", carouselTemplate);
                this.reply(replyToken, templateMessage);
                
            }
} else       
        if ((eventData.startsWith("JoinGroup"))&&(!joined.get(userId))) {
            String group = eventData.substring(9);
            KitchenSinkController.joined.replace(userId, true);
            this.replyText(replyToken, userName+ " : You have joined Group" +group);
        ArrayList<String> playerNames = new ArrayList<String>();
     ArrayList<String> playerClasses = new ArrayList<String>();
        //this.pushText(userId, "before Scoreboard");
        KitchenSinkController.eventPressed.put(userId,false);
        KitchenSinkController.colorPressed.put(userId,false);
        if (userName.length() > 6) {
                userName  = userName.substring(0,6);
        }
        
        switch (group) {
            case "1" :  playerNames.add("BOT1\uD83D\uDC2F");  // Tiger
                        playerNames.add("BOT2\uD83D\uDC37");  // Pig
                        playerNames.add("BOT3\uD83D\uDC38");  // Frog
                        playerNames.add(userName);
                        playerClasses.add("com.example.bot.spring.dummy3_UnoPlayer"); // Tiger
                        playerClasses.add("com.example.bot.spring.nds63_UnoPlayer");  // Pig
                        playerClasses.add("com.example.bot.spring.dummy2_UnoPlayer"); // Frog
                        playerClasses.add("com.example.bot.spring.dummy1_UnoPlayer"); // Human
                        break;
            case "2" :  
                        playerNames.add("BOT2\uD83D\uDC37"); // Pig
                        playerNames.add("BOT3\uD83D\uDC38"); // Frog
                        playerNames.add("BOT4\uD83D\uDC3C"); // Panda 
                        playerNames.add(userName);
                        
                        playerClasses.add("com.example.bot.spring.nds63_UnoPlayer");  // Pig
                        playerClasses.add("com.example.bot.spring.dummy2_UnoPlayer"); // Frog
                        playerClasses.add("com.example.bot.spring.dummy4_UnoPlayer"); // Panda
                        playerClasses.add("com.example.bot.spring.dummy1_UnoPlayer");
                        break;
            case "3" :  playerNames.add("BOT3\uD83D\uDC38"); // Frog
                        playerNames.add("BOT4\uD83D\uDC3C"); // Panda 
                        playerNames.add("BOT1\uD83D\uDC2F");  // Tiger
                        playerNames.add(userName);
                        playerClasses.add("com.example.bot.spring.dummy2_UnoPlayer"); // Frog
                        playerClasses.add("com.example.bot.spring.dummy4_UnoPlayer"); // Panda
                        playerClasses.add("com.example.bot.spring.dummy3_UnoPlayer"); // Tiger
                        playerClasses.add("com.example.bot.spring.dummy1_UnoPlayer");
                        break;
            case "4" :  playerNames.add("BOT4\uD83D\uDC3C"); // Panda 
                        playerNames.add("BOT1\uD83D\uDC2F");  // Tiger
                        playerNames.add("BOT2\uD83D\uDC37"); // Pig
                        playerNames.add(userName);
                        playerClasses.add("com.example.bot.spring.dummy4_UnoPlayer"); // Panda
                        playerClasses.add("com.example.bot.spring.dummy3_UnoPlayer"); // Tiger
                        playerClasses.add("com.example.bot.spring.nds63_UnoPlayer");  // Pig
                        playerClasses.add("com.example.bot.spring.dummy1_UnoPlayer");
                        break;
            default :   playerNames.add("BOT1\uD83D\uDC2F");  // Tiger
                        playerNames.add("BOT2\uD83D\uDC37");  // Pig
                        playerNames.add("BOT3\uD83D\uDC38");  // Frog
                        playerNames.add(userName);
                        playerClasses.add("com.example.bot.spring.dummy3_UnoPlayer"); // Tiger
                        playerClasses.add("com.example.bot.spring.nds63_UnoPlayer");  // Pig
                        playerClasses.add("com.example.bot.spring.dummy2_UnoPlayer"); // Frog
                        playerClasses.add("com.example.bot.spring.dummy1_UnoPlayer"); // Human
                        break;
        }
 try {
            
            Scoreboard s = new Scoreboard(playerNames.toArray(new String[0]));
           // this.pushText(userId, "after Scoreboard");
                Game g = new Game(s,playerClasses,userId);
              //  this.pushText(userId, "before play");
            eventData = "00Card0"; // Start Valueof PostBackEvent Select Card
            if (KitchenSinkController.playing.containsKey(userId)) {
            KitchenSinkController.playing.replace(userId, true);
        } else {  // New
            KitchenSinkController.playing.put(userId, true);
        } 
              
                g.play();
            playerNames.clear();
            playerClasses.clear();
            // round = 0;
            KitchenSinkController.round.replace(userId, 0);
                if (KitchenSinkController.playing.containsKey(userId)) {
            KitchenSinkController.playing.replace(userId, false);
        } else {
            KitchenSinkController.playing.put(userId, false);
                } 
//            KitchenSinkController.joined.replace(userId, false);
//            KitchenSinkController.colorPressed.replace(userId,false);  // Newest ดูให้ดีว่าถ้าเป็น multiuser แล้วส่งผลไหม
//            KitchenSinkController.eventPressed.clear();
//            KitchenSinkController.gameStatus.clear();
//            KitchenSinkController.playing.clear();
//            KitchenSinkController.joined.clear();
            //KitchenSinkController.gameStatus = "notPlayYet";
        }
        catch (Exception e) {
            this.pushText(userId,e.getMessage());
            if (KitchenSinkController.playing.containsKey(userId)) {
            KitchenSinkController.playing.replace(userId, false);
        } else {
            KitchenSinkController.playing.put(userId, false);
                } 
        }
        }
        else{   // not JoinGroup
            if ((eventData.substring(2,6).equals("Card"))){
                
                KitchenSinkController.round.replace(userId, KitchenSinkController.round.get(userId)+1);
            // round = round +1;
            
            int temp = Integer.parseInt(eventData.substring(6));
          //  this.pushText(userId,"round ="+round+" temp ="+temp);
            if (temp ==KitchenSinkController.round.get(userId)){
                // if ((KitchenSinkController.gameStatus.substring(2,6).equals("Card"))&&(temp ==round)){
                KitchenSinkController.eventPressed.replace(userId,true);
    //this.pushText(userId,status);
            } else {
              //  round = round -1;
            KitchenSinkController.round.replace(userId, KitchenSinkController.round.get(userId)-1);
            }
            
            
        } else { //not JoinGroup not Card
                if (eventData.equals("00nextPlay")||eventData.equals("ColorRed")||eventData.equals("ColorGreen")||eventData.equals("ColorBlue")||eventData.equals("ColorYellow")) {
                    KitchenSinkController.colorPressed.replace(userId, true);
                } else { //not next Play
                    if (eventData.equals("00ReadME2")) {
                                       String readme = "Due to small screen size,"
                      + "UNO bot use the abbrev. to represent the card. " 
                       + "R = Red, G=Green B=Blue Y=Yellow eg.\n"
                       + "R4 = Red Card Number 4 \n"
                      + "YS = Yellow Skip Card \n"
                       + "GR = Green Reverse Card \n"
                       + "Bplus2 = Blue Draw 2 Cards \n"
                       + "W = Wild Card. \n"
                       + "W4 = Wild Draw Four Cards.";
                                       this.replyText(replyToken, readme);
                        String imageUrl = createUri("/static/buttons/Uno_hint2.png");
                    this.pushImage(userId, imageUrl);
                } else {
                        this.pushText(userId, "Not Starts with Card or Color");
                    }
                
                }
            }
            
        }
    
    }


    @EventMapping
    public void handleBeaconEvent(BeaconEvent event) {
        String replyToken = event.getReplyToken();
        this.replyText(replyToken, "Got beacon message " + event.getBeacon().getHwid());
    }

    @EventMapping
    public void handleOtherEvent(Event event) {
        // log.info("Received message(Ignored): {}", event);
    }

    private void reply(@NonNull String replyToken, @NonNull Message message) {
        reply(replyToken, Collections.singletonList(message));
    }

    private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
        try {
            Response<BotApiResponse> apiResponse = lineMessagingService
                    .replyMessage(new ReplyMessage(replyToken, messages))
                    .execute();
           // log.info("Sent messages: {}", apiResponse);
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



    private static String createUri(String path) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                                          .path(path).build()
                                          .toUriString();
    }

   
}

