package zadudoder.spmhelper.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "spmhelper")
public class SPmHelperConfig implements ConfigData {
    String ID = "";
    String TOKEN = "";

    public String getID() {
        return ID;
    }

    public String getTOKEN() {
        return TOKEN;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setTOKEN(String TOKEN) {
        this.TOKEN = TOKEN;
    }
}
