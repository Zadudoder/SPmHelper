package zadudoder.spmhelper.utils.types;

public class BranchCoords {
    public HubBranch branch;
    public int pos;

    public BranchCoords(HubBranch branch, int pos) {
        this.branch = branch;
        this.pos = pos;
    }

    public int getBranchColor() {
        int color = 0xFFFFFF;
        switch (branch) {
            case RED -> color = 0xFF3232;
            case BLUE -> color = 0x3232FF;
            case GREEN -> color = 0x32FF32;
            case YELLOW -> color = 0xFFFF32;
            case PURPLE -> color = 0xA000FF;     // Фиолетовый
            case ORANGE -> color = 0xFFAA32;     // Оранжевый
            case LIME -> color = 0xAAFF32;     // Лаймовый
            case CYAN -> color = 0x32FFFF;     // Голубой
        }
        return color;
    }
}
