package zadudoder.spmhelper.utils.types;

public record Card(String name, String id, String token) {
    public String getBase64Key() {
        return java.util.Base64.getEncoder().encodeToString((id + ":" + token).getBytes());
    }
}