public class Cell{
    public final int r;
    public final int c;

    private boolean revealed;
    private boolean flagged;
    private boolean isMine;
    private int adjacentMines;

    public Cell(int r, int c){
        this.r = r;
        this.c = c;;
    }

    public boolean isRevealed(){ return revealed; }
    public void setRevealed(boolean v){ revealed = v; }

    public boolean isFlagged(){ return flagged; }
    public void setFlagged(boolean v){ flagged = v; }

    public boolean isMine(){ return isMine; }
    public void setMine(){ isMine = true; }

    public int getAdjacentMine(){ return adjacentMines; }
    public void setAdjacentMine(int count){
        adjacentMines = count;
    }
}