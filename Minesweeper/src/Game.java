import java.util.List;
import javax.swing.*;

public class Game {
    private final Board board;
    private final GameListener listener;
    private Difficulty difficulty;


    private int rows, cols, totalMines;
    private Tile[][] tiles;


    private boolean firstClick = true;
    private boolean isGameOver = false;
    private int tilesRevealed = 0;


    private int minesLeft;
    private int seconds = 0;
    private Timer timer;

    public Game(Difficulty difficulty, GameListener listener){
        this.difficulty = difficulty;
        this.listener = listener;
        this.rows = difficulty.rows;
        this.cols = difficulty.cols;
        this.totalMines = difficulty.mines;
        this.board = new Board(rows, cols, totalMines);
        setupTimer();
    }

    private void setupTimer(){
        timer = new Timer(1000, e -> {
        seconds++;
        listener.onUpdateTime(seconds);
        });
    }
    public void setTilesArray(Tile[][] tiles){
        this.tiles = tiles;
        board.setTiles(tiles);
    }

    public void startTimer(){
        seconds = 0;
        timer.start();
    }

    public void stopTimer(){
        timer.stop();
    }

    public void reset(Difficulty newDifficulty){
        stopTimer();
        this.difficulty = newDifficulty;
        this.rows = newDifficulty.rows;
        this.cols = newDifficulty.cols;
        this.totalMines = newDifficulty.mines;

        firstClick = true;
        isGameOver = false;
        tilesRevealed = 0;
        minesLeft = totalMines;
        seconds = 0;
        listener.onUpdateMinesLeft(minesLeft);
        listener.onUpdateTime(seconds);
        listener.onReset();
    }    

    public void handleLeftClick(Tile tile){
        if(isGameOver || tile.isRevealed() || tile.isFlagged()) return;
        if(firstClick){
            board.placeMines(tile.r, tile.c);
            firstClick = false;
            startTimer();
        }

        if(board.isMine(tile.r, tile.c)){
            board.revealAllMines();
            listener.onTilesRevealed(List.of(tile));
            isGameOver = true;
            stopTimer();
            listener.onGameLost();
            return;
        }

        List<Tile> newRevealed = board.reveal(tile.r, tile.c);
        listener.onTilesRevealed(newRevealed);
        tilesRevealed += newRevealed.size();
        checkWin();
    }

    public void handleRightClick(Tile tile){
        if(isGameOver || tile.isRevealed()) return;
        
        boolean isNotFlagged = !tile.isFlagged();
        tile.setFlagged(isNotFlagged);
        
        if(isNotFlagged) minesLeft--; 
        else minesLeft++;
        
        listener.onUpdateMinesLeft(minesLeft);
        checkWin();
    }


    private void checkWin(){
        int tilesTotal = rows * cols;
        int needed = tilesTotal - totalMines;
        int revealedCount = 0;

        for(int r=0;r<rows;r++){
            for(int c=0;c<cols;c++){
                if(tiles[r][c].isRevealed()) revealedCount++;
            }
        }

        if(revealedCount == needed && !isGameOver){
            isGameOver = true;
            stopTimer();
            listener.onGameWon(seconds);
        }
    }

    public Board getBoard(){
        return board;
    }
}