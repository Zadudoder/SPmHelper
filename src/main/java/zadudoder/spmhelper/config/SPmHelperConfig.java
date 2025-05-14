package zadudoder.spmhelper.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "spmhelper")
public class SPmHelperConfig implements ConfigData {
    String API_TOKEN = null;
    String SP_TOKEN = null;
    String SP_ID = null;

    public String getAPI_TOKEN() {
        return API_TOKEN;
    }

    public void setAPI_TOKEN(String TOKEN) {
        this.API_TOKEN = TOKEN;
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
