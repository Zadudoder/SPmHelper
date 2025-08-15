package zadudoder.spmhelper.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import zadudoder.spmhelper.utils.ScreenType;
import zadudoder.spmhelper.utils.types.Card;
import zadudoder.spmhelper.utils.types.VoiceType;

import java.util.HashMap;
import java.util.Map;

@Config(name = "spmhelper")
public class SPmHelperConfig implements ConfigData {

    public Boolean enableMenuButton = true;
    public ScreenType defaultScreen = ScreenType.MAIN;
    public Boolean paymentWithNick = false; // true - ник, false - по номеру
    public Boolean numberOfCardInComment = false; // true - включено, false - выключено в переводе будет указывать карту куда вы переводите
    public Boolean enableSPmNav = true;
    public Boolean EnableVoiceGuide = true;
    public VoiceType voiceType = null;
    public Boolean isFirstRun = true;
    public int SPmNavX = 50;
    public int SPmNavY = 2;
    public int SPmNavScale = 100;
    String API_TOKEN = "";
    Map<String, Card> cards = new HashMap<>();
    String mainCardName = "";

    public static SPmHelperConfig get() {
        return AutoConfig.getConfigHolder(SPmHelperConfig.class).getConfig();
    }

    public String getAPI_TOKEN() {
        return API_TOKEN;
    }

    public void setToken(String TOKEN) {
        this.API_TOKEN = TOKEN;
    }

    public Card getCard(String name) {
        return cards.get(name);
    }

    public Map<String, Card> getCards() {
        return cards;
    }

    public void addCard(String id, String token, String cardName) {
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
        if (mainCardName.equals(cardName)) {
            mainCardName = newCardName;
        }
        AutoConfig.getConfigHolder(SPmHelperConfig.class).save();
    }

    public void removeCard(String cardName) {
        if (mainCardName.equals(cardName)) {
            mainCardName = "";
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

    public String getMainCardName() {
        if (mainCardName != null) {
            return mainCardName;
        } else {
            return null;
        }
    }

    public void setPayWithNick(boolean value) {
        paymentWithNick = value;
        AutoConfig.getConfigHolder(SPmHelperConfig.class).save();
    }

}
