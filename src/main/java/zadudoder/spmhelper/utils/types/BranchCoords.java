package zadudoder.spmhelper.utils.types;

public class BranchCoords {
    public HubBranch branch;
    public int pos;

    public BranchCoords(HubBranch branch, int pos){
        this.branch = branch;
        this.pos = pos;
    }

    public BranchCoords(){
        this.branch = HubBranch.HUB;
    }

    public int getBranchColor(){
        int color = 0xFFFFFF;
        switch (branch){
            case RED ->  color = 0xFF0000;
            case BLUE -> color = 0x0000FF;
            case GREEN -> color = 0x00FF00;
            case YELLOW -> color = 0xFFFF00;
        }
        return color;
    }
}
