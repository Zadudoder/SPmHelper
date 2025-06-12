package zadudoder.spmhelper.utils.types;

import com.google.gson.annotations.SerializedName;

public class BaseCard {
    @SerializedName("name")
    private String name;

    @SerializedName("number")
    private String number;

    public String getName(){
        return name;
    }

    public String getNumber(){
        return number;
    }
}
