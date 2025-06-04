package zadudoder.spmhelper.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.text.Text;
import zadudoder.spmhelper.Screen.MainScreen;
import zadudoder.spmhelper.utils.types.Card;

import java.util.HashMap;
import java.util.Map;

@Config(name = "spmhelper")
public class SPmHelperConfig implements ConfigData {
    String API_TOKEN = null;
    public Boolean enableMenuButton = true;
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public ScreenType defaultScreen = ScreenType.SPmHelper;

    @ConfigEntry.Gui.Excluded
    Map<String, Card> cards = new HashMap<>();

    @ConfigEntry.Gui.Excluded
    String mainCardName;

    public enum ScreenType {
        SPmHelper(0, "screen.spmhelper.main"),
        Настройки(1, "screen.spmhelper.settings"),
        Оплата(2, "screen.spmhelper.pay"),
        Вызовы(3, "screen.spmhelper.calls"),
        Карта(4, "screen.spmhelper.map"),
        Законы(5, "screen.spmhelper.laws");

        private final int id;
        private final String translationKey;

        ScreenType(int id, String translationKey) {
            this.id = id;
            this.translationKey = translationKey;
        }

        public int getId() {
            return id;
        }

        public Text getTranslatedName() {
            switch(this) {
                case SPmHelper:
                default:
                    return Text.literal("Главный экран");
                case Настройки: return Text.literal("Настройки");
                case Оплата: return Text.literal("Оплата");
                case Вызовы: return Text.literal("Вызовы");
                case Карта: return Text.literal("Карта");
                case Законы: return Text.literal("Законы");
            }
        }

        public static ScreenType byId(int id) {
            for (ScreenType type : values()) {
                if (type.id == id) return type;
            }
            return SPmHelper;
        }
    }

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
