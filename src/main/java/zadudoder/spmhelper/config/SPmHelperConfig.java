package zadudoder.spmhelper.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import zadudoder.spmhelper.utils.types.Card;

import java.util.HashMap;
import java.util.Map;

@Config(name = "spmhelper")
public class SPmHelperConfig implements ConfigData {
    String API_TOKEN = null;
    Map<Integer, Card> cards = new HashMap<>();
    int mainCard;

    public String getAPI_TOKEN() {
        return API_TOKEN;
    }

    public void setAPI_TOKEN(String TOKEN) {
        this.API_TOKEN = TOKEN;
    }

    public Card getCard(int index) {
        return cards.get(index);
    }

    public void setCard(int index, Card newCard) {
        this.cards.put(index, newCard);
    }

    public Card getMainCar() {
        return cards.get(mainCard);
    }

    public void setMainCard(int number) {
        this.mainCard = number;
    }

}
