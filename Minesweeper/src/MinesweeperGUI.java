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
        frame.setResizable(false);
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

  private void resetGame() {
    game.stopTimer();
    if (multiplayer) {
      MultiplayerGame mg = (MultiplayerGame)game;
      List<String> names = mg.getAllPlayers();
      game = new MultiplayerGame(currentDifficulty, this, this, names);
    } else
      game = new SingleplayerGame(currentDifficulty, this);
    buildBoardUI();
  }

  private void updateTileUI(Cell cell) {
    if (cell.isFlagged()) {
      tiles[cell.r][cell.c].setText("🚩");
    } else if (cell.getAdjacentMine() < 1 ||
               (!cell.isFlagged() && !cell.isRevealed())) {
      tiles[cell.r][cell.c].setText("");
    }

    // showAllBombs();
  }

  @Override
  public void onCellsRevealed(List<Cell> revealed) {
    for (Cell cell : revealed) {
      int row = cell.r, col = cell.c;

      tiles[row][col].setEnabled(false);
      tiles[row][col].setBackground(new Color(225, 225, 225));
      tiles[row][col].setBorder(BorderFactory.createLineBorder(Color.GRAY));

      if (cell.isMine()) {
        tiles[row][col].setText("💣");
      } else {
        int adj = cell.getAdjacentMine();
        if (adj > 0) {
          tiles[row][col].setText(Integer.toString(adj));
        }
      }
    }
    frame.revalidate();
    frame.repaint();
  }

  @Override
  public void onUpdateRemainingMine(int minesLeft) {
    minesLabel.setText("Mines: " + minesLeft);
  }

  @Override
  public void onUpdateTime(int seconds) {
    timeLabel.setText("Time: " + seconds + "s");
  }

  @Override
  public void onGameWon(int seconds) {
    JOptionPane.showMessageDialog(frame,
                                  "You win! Time: " + seconds + " seconds");
  }

  @Override
  public void onGameLost() {
    JOptionPane.showMessageDialog(frame, "Game Over");
  }

  @Override
  public void onReset() {
    titleLabel.setText("Minesweeper");
    minesLabel.setText("Mines: " + currentDifficulty.mines);
    timeLabel.setText("Time: 0s");

    if (tiles != null) {
      for (TileUI[] row : tiles) {
        for (TileUI t : row) {
          t.setEnabled(true);
          t.setText("");
          t.setBackground(null);
        }
      }
    }
  }

    private void updateTileUI(Cell cell){
        if(cell.isFlagged()){
            tiles[cell.r][cell.c].setText("🚩");
            tiles[cell.r][cell.c].setForeground(Color.RED);
        } else if(cell.getAdjacentMine() < 1 || (!cell.isFlagged() && !cell.isRevealed())){
            tiles[cell.r][cell.c].setText("");
            tiles[cell.r][cell.c].setForeground(Color.BLACK);
        } 
        
        // showAllBombs();
    }

    // @Override
    // public void onCellsRevealed(List<Cell> revealed){
    //     for(Cell cell : revealed){
    //         int row = cell.r, col = cell.c;

    //         tiles[row][col].setEnabled(false);
    //         tiles[row][col].setBackground(new Color(225,225,225));
    //         tiles[row][col].setBorder(BorderFactory.createLineBorder(Color.GRAY));

    //         if(cell.isMine()){
    //             tiles[row][col].setText("💣");
    //         } else {
    //             int adj = cell.getAdjacentMine();
    //             if(adj > 0){
    //                 // tiles[row][col].setText(Integer.toString(adj));
    //                 setColoredNumber(tiles[row][col], adj);
    //             }
    //         }
    //     }
    //     frame.revalidate();
    //     frame.repaint();
    // }

    @Override
    public void onCellsRevealed(List<Cell> revealed){
        for(Cell cell : revealed){
            int row = cell.r, col = cell.c;

            // 1. Remove listeners so it acts disabled (cannot be clicked)
            for (MouseListener ml : tiles[row][col].getMouseListeners()) {
                tiles[row][col].removeMouseListener(ml);
            }

            // 2. [NEW FIX] Force the button to be flat and show the background color
            tiles[row][col].setOpaque(true);               // Force background paint
            tiles[row][col].setContentAreaFilled(false);   // Remove shiny 3D button skin
            tiles[row][col].setBackground(Color.LIGHT_GRAY); // Sets the gray color
            tiles[row][col].setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Keep the border

            if(cell.isMine()){
                tiles[row][col].setText("💣");
                tiles[row][col].setBackground(Color.RED); // Optional: Make bombs stand out
            } else {
                int adj = cell.getAdjacentMine();
                if(adj > 0){
                    setColoredNumber(tiles[row][col], adj);
                } else {
                    // Empty tile: ensure text is empty
                    tiles[row][col].setText("");
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
  public void onMultiplayerGameEnded(String winner) {
    JOptionPane.showMessageDialog(frame, "Winner: " + winner);
  }

  private void showAllBombs() { // debugging function
    System.out.println("bombcall");
    for (int i = 0; i < currentDifficulty.rows; i++) {
      for (int j = 0; j < currentDifficulty.cols; j++) {
        if (cells[i][j].isMine()) {
          tiles[i][j].setText("💣");
        }
      }
    }

    // [FIXED] Simpler version using standard setForeground
    private void setColoredNumber(TileUI tile, int number) {
        tile.setText(String.valueOf(number));

        switch (number) {
            case 1: 
                tile.setForeground(Color.BLUE); 
                break;
            case 2: 
                tile.setForeground(new Color(0, 128, 0)); // Darker Green
                break;
            case 3: 
                tile.setForeground(Color.RED); 
                break;
            case 4: 
                tile.setForeground(new Color(128, 0, 128)); // Purple
                break;
            case 5: 
                tile.setForeground(new Color(128, 0, 0)); // Maroon
                break;
            case 6: 
                tile.setForeground(new Color(0, 128, 128)); // Turquoise
                break;
            case 7: 
                tile.setForeground(Color.BLACK); 
                break;
            case 8: 
                tile.setForeground(Color.GRAY); 
                break;
        }
    }
}
