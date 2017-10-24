package com.example.bot.spring;


//import com.linecorp.bot.client.LineMessagingService;
//import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.response.BotApiResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import retrofit2.Response;

/**
 * <p>A Game object represents a single game of Uno in an overall match (of
 * possibly many games). Games are instantiated by providing them with a
 * roster of players, including a Scoreboard object through which scores
 * can be accumulated. The play() method then kicks off the game, which
 * will proceed from start to finish and update the Scoreboard. Various
 * aspects of the game's state (<i>e.g.</i>, whether the direction of play
 * is currently clockwise or counterclockwise, whose player's turn is next)
 * can be accessed and controlled through methods on this class.</p>
 * <p>A GameState object can be obtained through the getGameState() method,
 * which allows UnoPlayers to selectively and legally examine certain
 * aspects of the game's state.</p>
 * @since 1.0
 */
public class Game {

    /**
     * The number of cards each player will be dealt at start of game.
     */
    static final int INIT_HAND_SIZE = 7;
//final String channalKey ="xlHZZWi0tluGrr9/pPGtO6WK4h6Sbs8Uw9VdILnynXrv7QyRgCgBPHc6/LQma3LlDMOr5nsp9C88HUY0omCxnQoUTUlztfcWE93h2/ro05fZMWT72MzNqsBYXX80ZnehBPHXEtfXdiyYMjlK2RmTMgdB04t89/1O/w1cDnyilFU=";
final String channalKey ="EUMai2WNIC2Qu7jgkGqcCJ/D1BGXlQQmmHKxMaNSnkLq5NKWYMEMaD7wHScPrMPTQdSAnB/zslXaGHg7+EsuzRvmIL7AoSqiWfkqkFUKfCO4LGlUyeHXuv97gDb9DwwnuMrpWFiqqJiGY0lrVjfgzwdB04t89/1O/w1cDnyilFU=";
    

    public enum Direction { FORWARDS, BACKWARDS };

    /**
     * An object representing the state of the game at any point in time.
     * Note that much of the "state" is represented in the Game object
     * itself, and that this object provides a double-dispatch vehicle
     * through which select methods can access that state.
     */
    // Eak Newest private GameState state;

    /* package-visibility variables to simplify access between Game and
     * GameState classes */
    String userId;
    Deck deck;
    Hand h[];
    Card upCard;
    Direction direction;
    int currPlayer;
    UnoPlayer.Color calledColor;
    Scoreboard scoreboard;
    UnoPlayer.Color mostRecentColorCalled[];

    /**
     * Main constructor to instantiate a Game of Uno. Provided must be two
     * objects indicating the player roster: a Scoreboard, and a class
     * list. This constructor will deal hands to all players, select a
     * non-action up card, and reset all game settings so that play() can
     * be safely called.
     * @param scoreboard A fully-populated Scoreboard object that contains
     * the names of the contestants, in order.
     * @param playerClassList[] An array of Strings, each of which is a
     * fully-qualified package/class name of a class that implements the
     * UnoPlayer interface.
     */
    public Game(Scoreboard scoreboard, ArrayList<String> playerClassList,String userId) throws IOException, Exception {
        
        this.scoreboard = scoreboard;
        this.userId = userId;
        deck = new Deck();
        int no_of_play = scoreboard.getNumPlayers();
        // h = new Hand[scoreboard.getNumPlayers()];
        h = new Hand[no_of_play];
        // Ozone newest this.pushText(userId, "Number of Player : "+no_of_play );
        mostRecentColorCalled =
            new UnoPlayer.Color[scoreboard.getNumPlayers()];
//        this.pushText(userId, " playerClass0 :"+playerClassList.get(0));
//        this.pushText(userId, " playerClass1 :"+playerClassList.get(1));
//        this.pushText(userId, " playerClass2 :"+playerClassList.get(2));
//        this.pushText(userId, " playerClass3 :"+playerClassList.get(3));
        try {
            for (int i=0; i<scoreboard.getNumPlayers(); i++) {
                h[i] = new Hand(playerClassList.get(i),
                    scoreboard.getPlayerList()[i],userId);
                for (int j=0; j<INIT_HAND_SIZE; j++) {
                    h[i].addCard(deck.draw());
                }
            }
            upCard = deck.draw();
            while (upCard.isSpacialCard()) {
                deck.discard(upCard);
                deck.remix();
                upCard = deck.draw();
            }
        }
        catch (Exception e) {
            // Ozone newest this.pushText(userId,"Can't deal initial hands!"+e.getMessage());
           // Eak Newest System.exit(1);
           throw new Exception(e);
        }   
        direction = Direction.FORWARDS;
        
        currPlayer =
            new java.util.Random().nextInt(scoreboard.getNumPlayers());      
        
        // currPlayer = scoreboard.getNumPlayers() -1;
        calledColor = UnoPlayer.Color.NONE;
//        String playerTurn;
//        for (int j=0; j<scoreboard.getNumPlayers();j++){
//            
//        }
    }

 /*   private void printState() {
        for (int i=0; i<scoreboard.getNumPlayers(); i++) {
            System.out.println("Hand #" + i + ": " + h[i]);
        }
    }
*/
    /**
     * Return the number of the <i>next</i> player to play, provided the
     * current player doesn't jack that up by playing an action card.
     * @return An integer from 0 to scoreboard.getNumPlayers()-1.
     */
    public int getNextPlayer() {
        if (direction == Direction.FORWARDS) {
            return (currPlayer + 1) % scoreboard.getNumPlayers();
        }
        else {
            if (currPlayer == 0) {
                return scoreboard.getNumPlayers() - 1;
            }
            else {
                return currPlayer - 1;
            }
        }
    }
    private int getNextPlay(int current) {
        if (direction == Direction.FORWARDS) {
            return (current + 1) % scoreboard.getNumPlayers();
        }
        else {
            if (current== 0) {
                return scoreboard.getNumPlayers() - 1;
            }
            else {
                return current - 1;
            }
        }
    }
    /**
     * Go ahead and advance to the next player.
     */
    void advanceToNextPlayer() {
        currPlayer = getNextPlayer();
    }

    /**
     * Change the direction of the game from clockwise to counterclockwise
     * (or vice versa.)
     */
    void reverseDirection() {
        if (direction == Direction.FORWARDS) {
            direction = Direction.BACKWARDS;
        }
        else {
            direction = Direction.FORWARDS;
        }
    }
    @Autowired
    //  Eak Newest private LineMessagingService lineMessagingService;
    

    /* Ozone newest private void pushText(@NonNull String userId, @NonNull String messages) throws IOException {
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
    }*/
    /* Eak Newest
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
    */
/* Ozone newest   private void pushButton(@NonNull String userId, TemplateMessage templateMessage) throws IOException {
       
PushMessage pushMessage = new PushMessage(
        userId,
        templateMessage
);

Response<BotApiResponse> response =
        LineMessagingServiceBuilder
                .create(channalKey)
                .build()
                .pushMessage(pushMessage)
                .execute();
System.out.println(response.code() + " " + response.message());
    }*/
    
    /**
     * Play an entire Game of Uno from start to finish. Hands should have
     * already been dealt before this method is called, and a valid up card
     * turned up. When the method is completed, the Game's scoreboard object
     * will have been updated with new scoring favoring the winner.
     * 
     */
    public void play() throws IOException  {
        
        
        //  Newest Test
        String turnPlayer ="";
        String tempPlayer1,tempPlayer2,tempPlayer3,tempPlayer4;
        tempPlayer1 = h[currPlayer].getPlayerName();
        if (tempPlayer1.startsWith("BOT")) {
            turnPlayer = tempPlayer1.substring(4);
        } else {
            turnPlayer = tempPlayer1;
        }
        int tempInt = this.getNextPlay(currPlayer);
        tempPlayer2 = h[tempInt].getPlayerName();
        if (tempPlayer2.startsWith("BOT")) {
            turnPlayer = turnPlayer+"->"+tempPlayer2.substring(4);
        } else {
            turnPlayer = turnPlayer+"->"+tempPlayer2;
        }
        tempInt = this.getNextPlay(tempInt);
        tempPlayer3 = h[tempInt].getPlayerName();
        if (tempPlayer3.startsWith("BOT")) {
            turnPlayer = turnPlayer+"->"+tempPlayer3.substring(4);
        } else {
            turnPlayer = turnPlayer+"->"+tempPlayer3;
        }
        tempInt = this.getNextPlay(tempInt);
        tempPlayer4 = h[tempInt].getPlayerName();
        if (tempPlayer4.startsWith("BOT")) {
            turnPlayer = turnPlayer+"->"+tempPlayer4.substring(4);
        } else {
            turnPlayer = turnPlayer+"->"+tempPlayer4;
        }
       // this.pushText(userId,turnPlayer);
        
        //  Newest Test
        
       // this.pushText(userId, "Initial upcard is " + upCard + ".");
        String imageUrl = createUri("/static/buttons/"+upCard+".jpg");
        // pushImage(userId,imageUrl); 
        ButtonsTemplate upCardbuttonsTemplate = new ButtonsTemplate(
                        imageUrl,
                        "Initial upcard is " + upCard + ".",
                        turnPlayer,
                        Arrays.asList(
                                new PostbackAction("     ",
                                                   "nothingelse")
                        ));
                TemplateMessage upCardtemplateMessage = new TemplateMessage("Initial upcard is " + upCard + ".", upCardbuttonsTemplate);
                   // Ozone newest pushButton(userId, upCardtemplateMessage);
        
        try {
            while (true) {
                //print("Hand #" + currPlayer + " (" + h[currPlayer] + ")");
                /*print(h[currPlayer].getPlayerName() +
                    " (" + h[currPlayer] + ")"); */
                /*this.pushText(userId,h[currPlayer].getPlayerName() +
                    " (" + h[currPlayer] + ")");
                */
                Card playedCard = h[currPlayer].play(this);
                String playerName = h[currPlayer].getPlayerName();
                String pictureName;
                if (playerName.startsWith("BOT")) {
                    pictureName = playerName.substring(4);
                            } else {
                    pictureName = playerName;
                }
                String cardLeft = String.valueOf(h[currPlayer].size());
                if (playedCard == null) {
                    Card drawnCard;
                    try {
                        drawnCard = deck.draw();
                    }
                    catch (Exception e) {
                        //print("...deck exhausted, remixing...");
                        // Ozone newest  this.pushText(userId,"Deck exhausted, remixing");
                        deck.remix();
                        drawnCard = deck.draw(); // what if cann't draw()
                    }
                    h[currPlayer].addCard(drawnCard);
                    //this.pushText(userId,playerName+" has to draw (" + drawnCard + ").");
                    // Ozone newest this.pushText(userId,pictureName+" draw card. \uDBC0\uDC7E");
                    playedCard = h[currPlayer].play(this);
                }
                if (playedCard != null) {  
                    
                    // this.pushText(userId,playerName+" plays " + playedCard + " on " + upCard + ".");
                    // this.pushText(userId,playerName+" plays " + playedCard + ".");
                    if (playerName.startsWith("BOT")){ // ต้องข้าม Human Player ทำเฉพาะ BOT 
                    imageUrl = createUri("/static/buttons/"+playedCard+".jpg");
                     ButtonsTemplate buttonsTemplate = new ButtonsTemplate(
                        imageUrl,
                        null,
                        pictureName+" plays "+ playedCard + "."+"("+cardLeft+" Card Left)",
                        Arrays.asList(
                                new PostbackAction("NEXT",
                                                   "00nextPlay")
                        ));
                TemplateMessage templateMessage = new TemplateMessage(pictureName+" plays "+ playedCard + ".", buttonsTemplate);
                        // Ozone newest pushButton(userId, templateMessage);
                    
                    KitchenSinkController.colorPressed.replace(userId, false); // รอรับ input 
                    long startTime = System.currentTimeMillis(); //fetch starting time

                    while ((System.currentTimeMillis()-startTime)<30000)

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
                    }
                    
                    
                   
                    deck.discard(upCard);
                    upCard = playedCard;
                    if (upCard.followedByCall()) {
                        calledColor = h[currPlayer].callColor(this);
                        mostRecentColorCalled[currPlayer] = calledColor;
                        // Ozone newest this.pushText(userId,pictureName+" calls \'" + calledColor+"\'" );
                        
                    }
                    else {
                        calledColor = UnoPlayer.Color.NONE;
                    }
                }
                if (h[currPlayer].isEmpty()) {
                    int roundPoints = 0;
                    for (int j=0; j<scoreboard.getNumPlayers(); j++) {
                        roundPoints += h[j].countCards();
                    }
                    // Ozone newest this.pushText(userId,"\n" + pictureName +
                        " wins! (" + roundPoints + " points.)\uDBC0\uDC7F");
                    scoreboard.addToScore(currPlayer,roundPoints);
                    //this.pushText(userId,"---------------\n" + scoreboard);
                    return;
                }
                if (h[currPlayer].size() == 1) {
                    // Ozone newest this.pushText(userId,pictureName+ " says \' UNO! \'\uDBC0\uDC8A");
                }
                // this.pushText(userId,"\n");
                if (playedCard != null) {
                    int nextPlayer = currPlayer;
                     if (direction == Direction.FORWARDS) {
           nextPlayer = (currPlayer + 1) % scoreboard.getNumPlayers();
        }
        else {
            if (currPlayer == 0) {
                nextPlayer = scoreboard.getNumPlayers() - 1;
            }
            else {
                nextPlayer = currPlayer - 1;
            }
        }
                    
                    
                    String picName = h[nextPlayer].getPlayerName();
                    if (picName.startsWith("BOT")) {
                    picName = picName.substring(4);
                            } 
                    playedCard.performCardEffect(this,userId,picName);
                }
                else {
                    advanceToNextPlayer();
                }
            }
        }
        catch (EmptyDeckException e) {
            // Ozone newest this.pushText(userId,"Deck exhausted! This game is a draw.");
            //e.printStackTrace();
        }
        
    }

    void print(String s) {
        if (KitchenSinkController.PRINT_VERBOSE) {
            try {
                // Ozone newest this.pushText(userId,s);
            } catch (IOException ex) {
                //Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void println(String s) {
        if (KitchenSinkController.PRINT_VERBOSE) {
            try {
                // Ozone newest this.pushText(userId,s);
            } catch (IOException ex) {
                //Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Return the GameState object, through which the state of the game can
     * be accessed and safely manipulated.
     */
    public GameState getGameState() {

        return new GameState(this);
    }

    /**
     * Return the Card that is currently the "up card" in the game.
     */
    public Card getUpCard() {
        return upCard;
    }
      private static String createUri(String path) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                                          .path(path).build()
                                          .toUriString();
    }
}
