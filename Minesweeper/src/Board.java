import java.awt.Point;
import java.util.*;

public class Board {
    private final int rows;
    private final int cols;
    private final int totalMines;
    private Tile[][] tiles;
    private Set<Point> mineSet = new HashSet<>();

    public Board(int rows, int cols, int mines){
        this.rows = rows;
        this.cols = cols;
        this.totalMines = mines;
        tiles = new Tile[rows][cols];
    }

    public void setTiles(Tile[][] tiles){
        this.tiles = tiles;
    }

    public int getRows(){ return rows; }
    public int getCols(){ return cols; }
    public int getTotalMines(){ return totalMines; }

    public void placeMines(int excludeR, int excludeC){
        mineSet.clear();
        Random rand = new Random();
        while(mineSet.size() < totalMines){
        int r = rand.nextInt(rows);
        int c = rand.nextInt(cols);
        if(r == excludeR && c == excludeC) continue; 
            mineSet.add(new Point(r, c));
        }
    }

    public boolean isMine(int r, int c){
        return mineSet.contains(new Point(r, c));
    }

    public int countAdjacentMines(int r, int c){
        int count = 0;
        for(int rowIndex = -1; rowIndex <= 1; rowIndex++){ // -1: bawah, 0: tile sekarang, 1: atas
            for(int colIndex = -1; colIndex <= 1; colIndex++){ // -1: kiri, 0: tile sekarang, 1: tile kanan
                if(rowIndex == 0 && colIndex == 0) continue;
                int nextRow = r + rowIndex;
                int nextCol = c + colIndex;
                if(nextRow < 0 || nextRow >= rows || nextCol < 0 || nextCol >= cols) continue;
                if(isMine(nextRow, nextCol)) count++;
            }
        }
        return count;
    }

    public List<Tile> reveal(int r, int c){
        List<Tile> revealed = new ArrayList<>();
        if(tiles[r][c].isRevealed() || tiles[r][c].isFlagged()) return revealed;

        if(isMine(r,c)){
            tiles[r][c].setRevealed(true);
            revealed.add(tiles[r][c]);
            return revealed;
        }

        Deque<Point> stack = new ArrayDeque<>();
        stack.push(new Point(r,c));

        while(!stack.isEmpty()){
            Point coordinate = stack.pop();
            int row = coordinate.x, col = coordinate.y;
            Tile t = tiles[row][col];

            if(t.isRevealed() || t.isFlagged()) continue;
            t.setRevealed(true);
            revealed.add(t);

            int adjacentAmount = countAdjacentMines(row, col);
            if(adjacentAmount == 0){ //pop semua yang bukan mine
            for(int rowIndex=-1; rowIndex<=1; rowIndex++){
                for(int countIndex=-1; countIndex<=1; countIndex++){
                    if(rowIndex==0 && countIndex==0) continue;

                    int nextRow = row + rowIndex, nextCol = col + countIndex;
                    if(nextRow < 0 || nextRow >= rows || nextCol < 0 || nextCol >= cols) continue;

                    Tile nextTile = tiles[nextRow][nextCol];
                    if(!nextTile.isRevealed() && !nextTile.isFlagged() && !isMine(nextRow,nextCol)){
                        stack.push(new Point(nextRow,nextCol));
                        }
                    }
                }
            }
        }
    return revealed;
    }

    public Set<Point> getMineSet(){ return Collections.unmodifiableSet(mineSet); }

    public void revealAllMines(){
        for(Point p : mineSet){
            Tile t = tiles[p.x][p.y];
            t.setRevealed(true);
        }
    }
}