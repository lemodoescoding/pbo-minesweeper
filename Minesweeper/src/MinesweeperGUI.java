import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;

public class MinesweeperGUI implements GameListener, MultiplayerListener {

    private JFrame frame;
    private JPanel boardPanel;
    private JLabel titleLabel;
    private JLabel minesLabel;
    private JLabel timeLabel;
    private JComboBox<Difficulty> difficultyCombo;
    private JButton retryButton;

    private Difficulty currentDifficulty = Difficulty.EASY;
    private Game game;
    private Cell[][] cells;
    private TileUI[][] tiles;
    private List<TileUI> numberedTiles;
    private boolean multiplayer = false;

    public MinesweeperGUI(){
        SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    public MinesweeperGUI(List<String> multiplayerNames){
        this.multiplayer = true;
        SwingUtilities.invokeLater(() -> createAndShowGUI(multiplayerNames));
    }

    private void createAndShowGUI(){
        setupFrame();
        game = new SingleplayerGame(currentDifficulty, this);
        buildBoardUI();
        frame.setVisible(true);
    }

    private void createAndShowGUI(List<String> names){
        setupFrame();
        game = new MultiplayerGame(currentDifficulty, this, this, names);
        buildBoardUI();
        frame.setVisible(true);
    }

    private void setupFrame(){
        frame = new JFrame("Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        titleLabel = new JLabel("Minesweeper", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(titleLabel, BorderLayout.NORTH);

        JPanel info = new JPanel(new FlowLayout(FlowLayout.CENTER));
        difficultyCombo = new JComboBox<>(Difficulty.values());
        difficultyCombo.setSelectedItem(currentDifficulty);

        difficultyCombo.addActionListener(e -> {
            Difficulty chosen = (Difficulty) difficultyCombo.getSelectedItem();
            if(chosen != currentDifficulty){
                currentDifficulty = chosen;
                resetGame();
            }
        });

        minesLabel = new JLabel("Mines: 0");
        timeLabel = new JLabel("Time: 0s");
        retryButton = new JButton("Retry");
        retryButton.addActionListener(e -> resetGame());

        info.add(difficultyCombo);
        info.add(minesLabel);
        info.add(timeLabel);
        info.add(retryButton);
        frame.add(info, BorderLayout.SOUTH);

        boardPanel = new JPanel();
        frame.add(boardPanel, BorderLayout.CENTER);
    }

    private void buildBoardUI(){
        int rows = currentDifficulty.rows;
        int cols = currentDifficulty.cols;
        int tileSize = currentDifficulty.tileSize;
        int fontSize = currentDifficulty.fontSize;

        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout(rows, cols));
        
        cells = new Cell[rows][cols];
        tiles = new TileUI[rows][cols];

        for(int r=0; r<rows; r++){
            for(int c=0; c<cols; c++){
                TileUI t = new TileUI(r, c, fontSize);
                Cell cell = new Cell(r, c);
                t.setPreferredSize(new Dimension(tileSize, tileSize));
                t.addMouseListener(new MouseAdapter(){
                    @Override
                    public void mousePressed(MouseEvent e){
                        if(SwingUtilities.isLeftMouseButton(e)){
                            game.handleLeftClick(cell);
                        } else if(SwingUtilities.isRightMouseButton(e)){
                            game.handleRightClick(cell);
                            updateTileUI(cell);
                        }
                    }
                });

                tiles[r][c] = t;
                cells[r][c] = cell;
                boardPanel.add(t);
            }
        }

        game.setCellsArray(cells);
        minesLabel.setText("Mines: " + currentDifficulty.mines);

        frame.pack();
    }

    private void resetGame(){
        game.stopTimer();
        if(multiplayer){
            MultiplayerGame mg = (MultiplayerGame) game;
            List<String> names = mg.getAllPlayers();
            game = new MultiplayerGame(currentDifficulty, this, this, names);
        }
        else game = new SingleplayerGame(currentDifficulty, this);
        buildBoardUI();
    }

    private void updateTileUI(Cell cell){
        if(cell.isFlagged()){
            tiles[cell.r][cell.c].setText("🚩");
        } else if(cell.getAdjacentMine() < 1 || (!cell.isFlagged() && !cell.isRevealed())){
            tiles[cell.r][cell.c].setText("");
        } 
    }

    @Override
    public void onCellsRevealed(List<Cell> revealed){
        for(Cell cell : revealed){
            int row = cell.r, col = cell.c;

            tiles[row][col].setEnabled(false);
            tiles[row][col].setBackground(new Color(225,225,225));
            tiles[row][col].setBorder(BorderFactory.createLineBorder(Color.GRAY));

            if(cell.isMine()){
                tiles[row][col].setText("💣");
            } else {
                int adj = cell.getAdjacentMine();
                if(adj > 0){
                    tiles[row][col].setText(Integer.toString(adj));
                }
            }
        }
        frame.revalidate();
        frame.repaint();
    }

    @Override
    public void onUpdateRemainingMine(int minesLeft){
        minesLabel.setText("Mines: " + minesLeft);
    }

    @Override
    public void onUpdateTime(int seconds){
        timeLabel.setText("Time: " + seconds + "s");
    }

    @Override
    public void onGameWon(int seconds){
        JOptionPane.showMessageDialog(frame, "You win! Time: " + seconds + " seconds");
    }

    @Override
    public void onGameLost(){
        JOptionPane.showMessageDialog(frame, "Game Over");
    }

    @Override
    public void onReset(){
        titleLabel.setText("Minesweeper");
        minesLabel.setText("Mines: " + currentDifficulty.mines);
        timeLabel.setText("Time: 0s");

        if(tiles != null){
            for(TileUI[] row : tiles){
                for(TileUI t : row){
                    t.setEnabled(true);
                    t.setText("");
                    t.setBackground(null);
                }
            }
        }
    }

    @Override
    public void onPlayerTurnChanged(int index, String name){
        titleLabel.setText("Turn: " + name);
    }

    @Override
    public void onPlayerEliminated(int index, String name){
        JOptionPane.showMessageDialog(frame, name + " is eliminated!");
    }

    @Override
    public void onMultiplayerGameEnded(String winner){
        JOptionPane.showMessageDialog(frame, "Winner: " + winner);
    }
}
