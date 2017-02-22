package com.example.bot.spring;

import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
// Eak Newest import com.linecorp.bot.model.ReplyMessage;
// Eak Newest import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
// Eak Newest import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.response.BotApiResponse;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
// Eak Newest import java.util.Scanner;
// Eak Newest import java.util.logging.Level;
// Eak Newest import java.util.logging.Logger;
import lombok.NonNull;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import retrofit2.Response;


public class dummy1_UnoPlayer implements UnoPlayer {
	
    final String channalKey ="xlHZZWi0tluGrr9/pPGtO6WK4h6Sbs8Uw9VdILnynXrv7QyRgCgBPHc6/LQma3LlDMOr5nsp9C88HUY0omCxnQoUTUlztfcWE93h2/ro05fZMWT72MzNqsBYXX80ZnehBPHXEtfXdiyYMjlK2RmTMgdB04t89/1O/w1cDnyilFU=";
	UnoPlayer.Color blue = UnoPlayer.Color.BLUE;
	UnoPlayer.Color red = UnoPlayer.Color.RED;
	UnoPlayer.Color yellow = UnoPlayer.Color.YELLOW;
	UnoPlayer.Color green = UnoPlayer.Color.GREEN;
	UnoPlayer.Color wildColor = UnoPlayer.Color.NONE;
	
	UnoPlayer.Rank skip = UnoPlayer.Rank.SKIP;
	UnoPlayer.Rank reverse = UnoPlayer.Rank.REVERSE;
	UnoPlayer.Rank drawTwo = UnoPlayer.Rank.DRAW_TWO;
	UnoPlayer.Rank wildDrawFour = UnoPlayer.Rank.WILD_D4;
	UnoPlayer.Rank wild = UnoPlayer.Rank.WILD;
	
        int round = 0;
        boolean wait = true;
	
        
	public int play(List<Card> hand, Card upCard, Color calledColor, GameState state, String userId){
		
		/* ต้องมี handCanPlay ที่เป็น ArrayList ของ Card เพื่อเก็บ Card ที่สามารถเล่นบน
            UpCard ได้ โดย input จะมาจาก hand  แต่ต้องมี Hashtable เก็บค่า index ของ hand คู่กับ 
            index ของ handCanPlay ไว้  เพราะเมื่อ Show แต่ละ Card ใน handCanPlay ให้ ผู้เล่นเลือกแล้ว
            เราจะรับค่า index ของ handCanPlay แล้วใช้ Hashtable map หาค่า index ของ hand เพื่อ
            return ค่ากลับ เป็น int 
            
            */
                
                
                
		
		ArrayList<Card> handCanPlay,handNotPlay;
                handCanPlay = new ArrayList<Card>();
                handNotPlay = new ArrayList<Card>();
                HashMap<Integer,Integer> hashMap=new HashMap<Integer,Integer>();
                // HashMap hashMap=new HashMap();
                int k=0;
                int l=0;
                for (int i= 0; i< hand.size(); i++){
                    if (hand.get(i).canPlayOn(upCard, calledColor)){
                        
                        handCanPlay.add(hand.get(i));
                        hashMap.put(k,i);
                       
                        k=k+1;
                    }else {
                        handNotPlay.add(hand.get(i));
                        l=l+1;
                    }
                }
                if (handCanPlay.isEmpty()) {
                    return -1;
                    
                }else { // แสดง Card ที่สามารถเล่นได้ให้ ผู้เล่นดู 
                 //   if (wait) {
                    
                    round = round +1;
                    String cardName = "\u274C[" ;
                    for (int m=0; m< handNotPlay.size();m++){
                        
                        cardName = cardName+handNotPlay.get(m).toString()+" ";
                        //System.out.print("["+nameOfCard+"]");
                    
                }
                    cardName = cardName+"\b]\n\u2705[";
                    
                    for (int p=0; p< handCanPlay.size();p++){
                        
                        cardName = cardName+handCanPlay.get(p).toString()+" ";
                        //System.out.print("["+nameOfCard+"]");
                    
                }
                    cardName = cardName+"\b]\n";
                       
                    if (handCanPlay.size()<6){
                        CarouselColumn[] column = new CarouselColumn[handCanPlay.size()];
                    for (int j=0; j< handCanPlay.size();j++){
                        String nameOfCard;
                        nameOfCard = handCanPlay.get(j).toString();
                        String imageUrl = createUri("/static/buttons/"+nameOfCard+".jpg");
                        String cardNo;
                        if (j<10){
                            cardNo = "0"+Integer.toString(j);
                        } else {
                            cardNo = Integer.toString(j);
                       }
                        
                        column [j] = new CarouselColumn(imageUrl,"U have "+hand.size()+" card: can play "+handCanPlay.size()+" card",cardName+"Upcard is "+upCard,Arrays.asList(
                            new PostbackAction("Select "+nameOfCard,cardNo+"Card"+round)));
                        
                       
                    }  //  end for
                    CarouselTemplate carouselTemplate = new CarouselTemplate(Arrays.asList(column));
                        TemplateMessage templateMessage = new TemplateMessage("Carousel alt text", carouselTemplate); 
                        this.pushButton(userId,templateMessage);
                    } else {  // more than 5 column
                        int leftColumn = handCanPlay.size() - 5;
                        CarouselColumn[] column = new CarouselColumn[5];
                        for (int j=0; j< 5;j++){
                        String nameOfCard;
                        nameOfCard = handCanPlay.get(j).toString();
                        String imageUrl = createUri("/static/buttons/"+nameOfCard+".jpg");
                        String cardNo;
                        if (j<10){
                            cardNo = "0"+Integer.toString(j);
                        } else {
                            cardNo = Integer.toString(j);
                       }
                        
                        column [j] = new CarouselColumn(imageUrl,cardName,"("+hand.size()+" Card Left) "+"Upcard is "+upCard,Arrays.asList(
                            new PostbackAction("Select "+nameOfCard,cardNo+"Card"+round)));
                        
                           
                    }
                     CarouselTemplate carouselTemplate = new CarouselTemplate(Arrays.asList(column));
                        TemplateMessage templateMessage = new TemplateMessage("Carousel alt text", carouselTemplate); 
                        
                        CarouselColumn[] column2 = new CarouselColumn[leftColumn];
                        for (int j=0; j< leftColumn;j++){
                        String nameOfCard;
                        nameOfCard = handCanPlay.get(j).toString();
                        String imageUrl = createUri("/static/buttons/"+nameOfCard+".jpg");
                        String cardNo;
                        if (j<10){
                            cardNo = "0"+Integer.toString(j);
                        } else {
                            cardNo = Integer.toString(j);
                       }
                        
                        column2 [j] = new CarouselColumn(imageUrl,cardName,"("+hand.size()+" Card Left) "+"Upcard is "+upCard,Arrays.asList(
                            new PostbackAction("Select "+nameOfCard,cardNo+"Card"+round)));
                        
                        
                    }  
                      CarouselTemplate carouselTemplate2 = new CarouselTemplate(Arrays.asList(column2));
                        TemplateMessage templateMessage2 = new TemplateMessage("Carousel alt text", carouselTemplate2);   
                        List<Message> listTmessage = Arrays.asList(templateMessage,templateMessage2);
                        this.pushButton(userId,listTmessage);
                    }
                    /*  แสดงทั้ง unplayable card และ playable
                    CarouselColumn[] columnNotPlay = new CarouselColumn[handNotPlay.size()];
                    for (int n=0; n< handNotPlay.size();n++){
                        String nameOfCard;
                        nameOfCard = handNotPlay.get(n).toString();
                        String imageUrl = createUri("/static/buttons/"+nameOfCard+".jpg");                 
                        columnNotPlay [n] = new CarouselColumn(imageUrl,null,nameOfCard,Arrays.asList(
                            new PostbackAction("   ","00Can_not_Select")));
                    }
                    CarouselTemplate carouselTemplateNP = new CarouselTemplate(Arrays.asList(columnNotPlay));
                        TemplateMessage templateMessageNP = new TemplateMessage("Carousel alt text", carouselTemplateNP);
                        

                    CarouselColumn[] column = new CarouselColumn[handCanPlay.size()];
                    for (int j=0; j< handCanPlay.size();j++){
                        String nameOfCard;
                        nameOfCard = handCanPlay.get(j).toString();
                        String imageUrl = createUri("/static/buttons/"+nameOfCard+".jpg");
                        String cardNo;
                        if (j<10){
                            cardNo = "0"+Integer.toString(j);
                        } else {
                            cardNo = Integer.toString(j);
                       }
                        
                        column [j] = new CarouselColumn(imageUrl,null,cardName,Arrays.asList(
                            new PostbackAction("Select "+nameOfCard,cardNo+"Card"+round)));
                            
                    }
                                        
                        CarouselTemplate carouselTemplate = new CarouselTemplate(Arrays.asList(column));
                        TemplateMessage templateMessage = new TemplateMessage("Carousel alt text", carouselTemplate);
                        
                       List<Message> listTmessage = Arrays.asList(templateMessageNP,templateMessage);
                       
                        this.pushButton(userId,listTmessage);
                        */
                    
/*
                       for (int m=0; m< handNotPlay.size();m++){
                        String nameOfCard;
                        nameOfCard = handNotPlay.get(m).toString();
                        //System.out.print("["+nameOfCard+"]");
                    
                }
                       */
                       KitchenSinkController.eventPressed.replace(userId, false); // รอรับ input 
                      long startTime = System.currentTimeMillis(); //fetch starting time

while ((System.currentTimeMillis()-startTime)<60000)

{
                        try {
                            // do something
                            Thread.sleep(1000);} catch (InterruptedException ex) {
                            //Logger.getLogger(dummy1_UnoPlayer.class.getName()).log(Level.SEVERE, null, ex);
                        }
    if (KitchenSinkController.eventPressed.get(userId)){
        
        break;
    }
}  

               // }
                       
                    // รับ input จาก User ว่าจะเลือก Card ไหน
                    int number1 = 0;
                    String str;
                    
                    if (KitchenSinkController.eventPressed.get(userId)){
                        /*
                        try {                  
                        this.pushText(userId,"You Select: "+KitchenSinkController.gameStatus.get(userId));
                    } catch (IOException ex) {
                        //Logger.getLogger(dummy1_UnoPlayer.class.getName()).log(Level.SEVERE, null, ex);
                    
                            
                            }
                        */
                      
                    KitchenSinkController.eventPressed.replace(userId, false);
                      str = KitchenSinkController.gameStatus.get(userId).substring(0,2); // first 2 digit is CardNO.
                    number1 = Integer.valueOf(str);
                    } else { // didn't press 
                        // wait = false;
                        round = round -1;
                        number1 =0;
                        try {                  
                        this.pushText(userId,"TIME OUT: We select for U");
                    } catch (IOException ex) {
                        //Logger.getLogger(dummy1_UnoPlayer.class.getName()).log(Level.SEVERE, null, ex);
                    
                            
                            }
                    }
                    
                    
                    
                    /*
                    try {
                        this.pushText(userId,"CardNO = "+String.valueOf(number1));
                    } catch (IOException ex) {
                        //Logger.getLogger(dummy1_UnoPlayer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    */
                    
                        //String indexOfhand;
                        // indexOfhand = hashMap.get(Integer.toString(number1));
                        
                    //return  Integer.parseInt(indexOfhand);
                    if (number1 < hashMap.size()) {
                    return  hashMap.get(number1);    
                    } else {
                        return 0;
                    }
                    
                }
                   		
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
        private void pushButton(@NonNull String replyToken, @NonNull Message message) {
        pushButton(replyToken, Collections.singletonList(message));
    }
     
       private void pushButton(@NonNull String userId, @NonNull List<Message> templateMessage)  {
       
PushMessage pushMessage = new PushMessage(
        userId,
        templateMessage
);
 try {
Response<BotApiResponse> response =
        LineMessagingServiceBuilder
                .create(channalKey)
                .build()
                .pushMessage(pushMessage)
                .execute();
System.out.println(response.code() + " " + response.message());
} catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    } 
        

        private static String createUri(String path) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                                          .path(path).build()
                                          .toUriString();
    }
	public Color callColor(List<Card> hand,String userId){
		
		// Scanner Keyboard = new Scanner(System.in);
                //        System.out.print("Color : ");
                                ButtonsTemplate buttonsTemplate = new ButtonsTemplate(
                        null,
                        null,
                        "Please select COLOR",
                        Arrays.asList(
                                new PostbackAction("RED",
                                                   "ColorRed"),
                                new PostbackAction("GREEN",
                                                   "ColorGreen"),
                                new PostbackAction("BLUE",
                                                   "ColorBlue"),
                                new PostbackAction("YELLOW",
                                                   "ColorYellow")
                        ));
                TemplateMessage templateMessage = new TemplateMessage("Button alt text", buttonsTemplate);
                    
                        this.pushButton(userId,templateMessage);
                   
                    
                        KitchenSinkController.colorPressed.replace(userId, false); // รอรับ input 
                      long startTime = System.currentTimeMillis(); //fetch starting time

while ((System.currentTimeMillis()-startTime)<60000)

{
                        try {
                            // do something
                            Thread.sleep(1000);} catch (InterruptedException ex) {
                            //Logger.getLogger(dummy1_UnoPlayer.class.getName()).log(Level.SEVERE, null, ex);
                        }
    if (KitchenSinkController.colorPressed.get(userId)){
        
        break;
    }
}

       if (KitchenSinkController.colorPressed.get(userId)){
           /*
                        try {                  
                        this.pushText(userId,"You Select: "+KitchenSinkController.gameStatus.get(userId));
                    } catch (IOException ex) {
                        //Logger.getLogger(dummy1_UnoPlayer.class.getName()).log(Level.SEVERE, null, ex);
                    
                            
                            }
                        */
                    KitchenSinkController.colorPressed.replace(userId, false);
                    switch (KitchenSinkController.gameStatus.get(userId))
                    {
                        case "ColorRed": return red;
                        case "ColorGreen": return green;
                        case "ColorBlue": return blue;
                        case "ColorYellow": return yellow;
                        default: return green;
                    }
                    } else {
                        try {                  
                        this.pushText(userId,"TIME OUT: We select for U");
                    } catch (IOException ex) {
                        //Logger.getLogger(dummy1_UnoPlayer.class.getName()).log(Level.SEVERE, null, ex);
                    
                            
                            }
                       Random rand;
                rand = new Random();
                int number1;
                        number1 = rand.nextInt(4 - 1 + 1) + 1;
		
		switch (number1)		
                {
                    case 1 : 
                    return blue;
                    
                    case 2 : 
                     return yellow;
      
                    case 3 :
                    return red;
                    default:
                        return green;     
                     			
	} 
                    }                
                    
                

        }	
}




    /**
     * play - This method is called when it's your turn and you need to
     * choose what card to play.
     *
     * The hand parameter tells you what's in your hand. You can call
     * getColor(), getRank(), and getNumber() on each of the cards it
     * contains to see what it is. The color will be the color of the card,
     * or "Color.NONE" if the card is a wild card. The rank will be
     * "Rank.NUMBER" for all numbered cards, and another value (e.g.,
     * "Rank.SKIP," "Rank.REVERSE," etc.) for special cards. The value of
     * a card's "number" only has meaning if it is a number card. 
     * (Otherwise, it will be -1.)
     *
     * The upCard parameter works the same way, and tells you what the 
     * up card (in the middle of the table) is.
     *
     * The calledColor parameter only has meaning if the up card is a wild,
     * and tells you what color the player who played that wild card called.
     *
     * Finally, the state parameter is a GameState object on which you can 
     * invoke methods if you choose to access certain detailed information
     * about the game (like who is currently ahead, what colors each player
     * has recently called, etc.)
     *
     * You must return a value from this method indicating which card you
     * wish to play. If you return a number 0 or greater, that means you
     * want to play the card at that index. If you return -1, that means
     * that you cannot play any of your cards (none of them are legal plays)
     * in which case you will be forced to draw a card (this will happen
     * automatically for you.)
     */
   

    /**
     * callColor - This method will be called when you have just played a
     * wild card, and is your way of specifying which color you want to 
     * change it to.
     *
     * You must return a valid Color value from this method. You must not
     * return the value Color.NONE under any circumstances.
  **/