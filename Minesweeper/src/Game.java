import javax.swing.*;

public abstract class Game {
    protected Board board;
    protected GameListener listener;
    protected Difficulty difficulty;
    protected Cell[][] cells;
    protected boolean firstClick = true;
    protected boolean isGameOver = false;
    protected int tilesRevealed = 0;
    protected int mineLeft;
    protected int seconds = 0;
    protected Timer timer;

    public Game(Difficulty difficulty, GameListener listener){
        this.difficulty = difficulty;
        this.listener = listener;
        this.board = new Board(difficulty.rows, difficulty.cols, difficulty.mines);
        this.mineLeft = difficulty.mines;
    }

    public void setCellsArray(Cell[][] cells){
        this.cells = cells;
        board.setCell(cells);
    }

    public void reset(Difficulty difficulty){
        this.difficulty = difficulty;
        this.board = new Board(difficulty.rows, difficulty.cols, difficulty.mines);
        this.mineLeft = difficulty.mines;
        this.tilesRevealed = 0;
        this.firstClick = true;
        this.isGameOver = false;
        listener.onReset();
    }

    public Board getBoard(){
        return board;
    }

    public void stopTimer(){
        if(timer != null){
            timer.stop();
        }
    }

    protected void startTimer(){
        if(timer != null){
            timer.stop();
        }
        timer = new Timer(1000, e -> {
            seconds++;
            listener.onUpdateTime(seconds);
        });
        timer.start();
    }

    public abstract void handleLeftClick(Cell cell);
    public abstract void handleRightClick(Cell cell);
}
