package zadudoder.spmhelper.utils;

import net.minecraft.text.Text;

public enum ScreenType {
    MAIN("text.spmhelper.screen_type.main"),
    SETTINGS("spmhelper.screen.settings"),
    PAY("spmhelper.screen.settings"),
    CALLS("spmhelper.screen.settings"),
    MAP("spmhelper.screen.settings"),
    LAWS("spmhelper.screen.settings");


    private final String translationKey;

    ScreenType(String translationKey) {
        this.translationKey = translationKey;
    }

}
