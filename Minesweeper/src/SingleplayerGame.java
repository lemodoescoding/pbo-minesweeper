import java.util.List;

public class SingleplayerGame extends Game {

    public SingleplayerGame(Difficulty difficulty, GameListener listener){
        super(difficulty, listener);
    }

    @Override
    public void handleLeftClick(Cell cell){
        if(isGameOver || cell.isFlagged()) return;

        if(firstClick){
            board.placeMine(cell.r, cell.c);
            startTimer();
            firstClick = false;
        }

        List<Cell> revealedCells = board.reveal(cell.r, cell.c);

        if(revealedCells.isEmpty()) return;
        listener.onCellsRevealed(revealedCells);

        for(Cell c : revealedCells){
            tilesRevealed++;
            if(c.isMine()){
                isGameOver = true;
                stopTimer();
                board.revealAllMine();
                listener.onGameLost();
                return;
            }
        }

        if(tilesRevealed == difficulty.rows * difficulty.cols - difficulty.mines){
            isGameOver = true;
            stopTimer();
            listener.onGameWon(seconds);
        }
    }

    @Override
    public void handleRightClick(Cell cell){
        if(isGameOver || cell.isRevealed()) return;

        cell.setFlagged(!cell.isFlagged());

        mineLeft += cell.isFlagged() ? -1 : 1;
        listener.onUpdateRemainingMine(mineLeft);
    }
}
