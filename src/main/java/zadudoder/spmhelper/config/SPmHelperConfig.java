package zadudoder.spmhelper.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import zadudoder.spmhelper.utils.types.Card;

import java.util.HashMap;
import java.util.Map;

@Config(name = "spmhelper")
public class SPmHelperConfig implements ConfigData {
    String API_TOKEN = null;
    Map<String, Card> cards = new HashMap<>();
    String mainCardName;

    public String getAPI_TOKEN() {
        return API_TOKEN;
    }

    public void setAPI_TOKEN(String TOKEN) {
        this.API_TOKEN = TOKEN;
    }

    public Card getCard(String name) {
        return cards.get(name);
    }

    public Map<String, Card> getCards() {
        return cards;
    }

    public void addCard(String id, String token, String cardName) {

        for (String name : cards.keySet()) {
            if (cards.get(name).token == token) {
                return;
            }
        }
        Card card = new Card(id, token);
        cards.put(cardName, card);
        if (getMainCard() == null) {
            setMainCard(cardName);
        }
        AutoConfig.getConfigHolder(SPmHelperConfig.class).save();
    }

    public void renameCard(String cardName, String newCardName) {
        Card cardInfo = getCard(cardName);
        cards.remove(cardName);
        cards.put(newCardName, cardInfo);
        if (mainCardName == cardName) {
            mainCardName = newCardName;
        }
        AutoConfig.getConfigHolder(SPmHelperConfig.class).save();
    }

    public void removeCard(String cardName) {
        if (mainCardName == cardName) {
            mainCardName = null;
        }
        cards.remove(cardName);
        AutoConfig.getConfigHolder(SPmHelperConfig.class).save();
    }

    public Card getMainCard() {
        if (!(mainCardName == null)) {
            return cards.get(mainCardName);
        } else {
            return null;
        }

    }

    public void setMainCard(String cardName) {
        this.mainCardName = cardName;
        AutoConfig.getConfigHolder(SPmHelperConfig.class).save();
    }

}
