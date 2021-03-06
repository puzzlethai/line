
package com.example.bot.spring.echo;

import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import java.io.IOException;
import java.util.ArrayList;
import lombok.NonNull;
import retrofit2.Response;

/**
 * <p>A Hand of Uno cards, held by a particular player. A Hand object is
 * responsible for playing a Card (<i>i.e.</i>, actually choosing a card to
 * play) when the player's turn comes up. To do this, it implements the
 * strategy pattern by which this choice can be delegated to an arbitrary
 * implementer of the UnoPlayer class.</p>
 * @since 1.0
 */
public class Hand {

    private ArrayList<Card> cards;
    private UnoPlayer player;
    private String playerName;
    public String userId;

    /**
     * Instantiate a Hand object to be played by the UnoPlayer class, and
     * the player name, passed as arguments. This implements a strategy
     * pattern whereby the constructor accepts various strategies that
     * implement the UnoPlayer interface.
     */
    public Hand(String unoPlayerClassName, String playerName, String userId) throws IOException, InstantiationException, IllegalAccessException {
        this.userId = userId;
        try {
            player = (UnoPlayer)
                Class.forName(unoPlayerClassName).newInstance();
        }
        catch (ClassNotFoundException e) {
            //System.out.println("Problem with " + unoPlayerClassName + ".");
            this.pushText(userId, "error try "+e.getMessage());
            e.printStackTrace(); 
            System.exit(1);
        }
        this.playerName = playerName;
        cards = new ArrayList<Card>();
    }

     private void pushText(@NonNull String userId, @NonNull String messages) throws IOException {
       TextMessage textMessage = new TextMessage(messages);
PushMessage pushMessage = new PushMessage(
        userId,
        textMessage
);

Response<BotApiResponse> response =
        LineMessagingServiceBuilder
                .create("EUMai2WNIC2Qu7jgkGqcCJ/D1BGXlQQmmHKxMaNSnkLq5NKWYMEMaD7wHScPrMPTQdSAnB/zslXaGHg7+EsuzRvmIL7AoSqiWfkqkFUKfCO4LGlUyeHXuv97gDb9DwwnuMrpWFiqqJiGY0lrVjfgzwdB04t89/1O/w1cDnyilFU=")
                .build()
                .pushMessage(pushMessage)
                .execute();
System.out.println(response.code() + " " + response.message());
    }
    /**
     * Add (draw) a card to the hand.
     */
    void addCard(Card c) {
        cards.add(c);
    }

    /**
     * Return the number of cards in the hand.
     */
    public int size() {
        return cards.size();
    }

    /**
     * It's your turn: play a card. When this method is called, the Hand
     * object choose a Card from the Hand based on the strategy that is
     * controlling it (<i>i.e.</i>, whose code was passed to the Hand
     * constructor.) If the player cannot legally play any of his/her
     * cards, null is returned.
     * @return The Card object to be played (which has been removed from
     * this Hand as a side effect), or null if no such Card can be played.
     */
    Card play(Game game) {
        int playedCard;
        playedCard = player.play(cards, game.getUpCard(), game.calledColor,
            game.getGameState(),userId);
        if (playedCard == -1) {
            return null;
        }
        else {
            Card toPlay = cards.remove(playedCard);
            return toPlay;
        }
    }

    /**
     * Designed to be called in response to a wild card having been played
     * on the previous call to this object's play() method. This method
     * will choose one of the four colors based on the strategy controlling
     * it (<i>i.e.</i>, the class whose code was passed to the Hand
     * constructor.)
     * @return A Color value, <i>not</i> Color.NONE.
     */
    UnoPlayer.Color callColor(Game game) {
        return player.callColor(cards);
    }

    /**
     * Return true only if this Hand has no cards, which should trigger a
     * winning condition.
     */
    public boolean isEmpty() {
        return cards.size() == 0;
    }

    /**
     * Return a string rendering of this Hand. See Card::toString() for
     * notes about how individual cards are rendered.
     */
    public String toString() {
        String retval = "";
        for (int i=0; i<cards.size(); i++) {
            retval += cards.get(i);
            if (i<cards.size()-1) {
                retval += ",";
            }
        }
        return retval;
    }

    /**
     * Return the forfeit value of this Hand, as it now stands (in other
     * words, the sum of all the forfeit values of cards still possessed.)
     */
    public int countCards() {
        int total = 0;
        for (int i=0; i<cards.size(); i++) {
            total += cards.get(i).forfeitCost();
        }
        return total;
    }

    /**
     * Return the name of the contestant.
     */
    public String getPlayerName() {
        return playerName;
    }
}
