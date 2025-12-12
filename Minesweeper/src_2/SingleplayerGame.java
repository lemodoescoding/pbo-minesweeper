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

                Database.createPlayerIfNotExists("self_singleplayer");

                int playerId = Database.getPlayerId("self_singleplayer");

                int difficultyId = Database.getDifficultyId(difficulty.name());
                Database.saveSingleplayerGame(playerId, difficultyId, seconds, false);
                Database.updateGamesPlayed(playerId);

                Database.printSingleplayerLeaderboard(difficultyId);

                return;
            }
        }

        if(tilesRevealed == difficulty.rows * difficulty.cols - difficulty.mines){
            isGameOver = true;
            stopTimer();
            Database.createPlayerIfNotExists("self_singleplayer");

            int playerId = Database.getPlayerId("self_singleplayer");

            int difficultyId = Database.getDifficultyId(difficulty.name());
            Database.saveSingleplayerGame(playerId, difficultyId, seconds, true);
            Database.updateWin(playerId, seconds);

            Database.printSingleplayerLeaderboard(difficultyId);

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
