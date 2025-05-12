package zadudoder.spmhelper.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "spmhelper")
public class SPmHelperConfig implements ConfigData {
    String TOKEN = null;
    String SP_TOKEN = null;
    String SP_ID = null;

    public String getTOKEN() {
        return TOKEN;
    }

    public void setTOKEN(String TOKEN) {
        this.TOKEN = TOKEN;
    }

    public String getSpTOKEN() {
        return SP_TOKEN;
    }

    public void setSpTOKEN(String TOKEN) {
        this.SP_TOKEN = TOKEN;
    }

    public String getSpID() {
        return SP_ID;
    }

    public void setSpID(String ID) {
        this.SP_ID = ID;
    }
}
