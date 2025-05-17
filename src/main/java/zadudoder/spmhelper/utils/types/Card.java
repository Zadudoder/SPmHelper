package zadudoder.spmhelper.utils.types;

public class Card {
    public String name = null;
    public String id;
    public String token;

    public Card(String id, String token) {
        this.id = id;
        this.token = token;
    }

    public String getBase64Key() {
        return java.util.Base64.getEncoder().encodeToString((id + ":" + token).getBytes());
    }
}