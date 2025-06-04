package zadudoder.spmhelper.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import zadudoder.spmhelper.utils.ScreenType;
import zadudoder.spmhelper.utils.types.Card;

import java.util.HashMap;
import java.util.Map;

@Config(name = "spmhelper")
public class SPmHelperConfig implements ConfigData {

    public Boolean enableMenuButton = true;
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public ScreenType defaultScreen = ScreenType.MAIN;

    String API_TOKEN = "";
    Map<String, Card> cards = new HashMap<>();
    String mainCardName = "";

    public static SPmHelperConfig get() {
        return AutoConfig.getConfigHolder(SPmHelperConfig.class).getConfig();
    }

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

    public String getMainCardName() {
        if (!(mainCardName == null)) {
            return mainCardName;
        } else {
            return null;
        }
    }

}
