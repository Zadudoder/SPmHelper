package zadudoder.spmhelper.utils.types;

import zadudoder.spmhelper.utils.SPWorldsApi;

public class Card {
    public String name;
    public String id;
    public String token;
    public Card(String id,String token){
        this.id = id;
        this.token = token;
        // Здесь надо реализацию добавления имени карты(потом)
    }
    public String getBase64Key() {
        return java.util.Base64.getEncoder().encodeToString((id + ":" + token).getBytes());
    }
}