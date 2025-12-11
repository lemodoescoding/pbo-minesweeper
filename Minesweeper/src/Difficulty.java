public enum Difficulty {
    EASY(8, 8, 10, 50, 20),
    MEDIUM(14, 14, 40, 40, 18),
    HARD(20, 20, 99, 32, 16);

    public final int rows;
    public final int cols;
    public final int mines;
    public final int tileSize;
    public final int fontSize;


    Difficulty(int rows, int cols, int mines, int tileSize, int fontSize){
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
        this.tileSize = tileSize;
        this.fontSize = fontSize;
    }
}

