import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;

public class MinesweeperGUI implements GameListener{
    private JFrame frame;
    private JPanel boardPanel;
    private JLabel titleLabel;
    private JLabel minesLabel;
    private JLabel timeLabel;
    private JComboBox<Difficulty> difficultyCombo;
    private JButton retryButton;

    private Difficulty currentDifficulty = Difficulty.EASY;
    private Game game;
    private Tile[][] tiles;

    public MinesweeperGUI(){
        SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    private void createAndShowGUI(){
        frame = new JFrame("Minesweeper (MVC)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        titleLabel = new JLabel("Minesweeper", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(titleLabel, BorderLayout.NORTH);

        JPanel info = new JPanel(new FlowLayout(FlowLayout.CENTER));
        difficultyCombo = new JComboBox<>(Difficulty.values());
        difficultyCombo.setSelectedItem(currentDifficulty);

        difficultyCombo.addActionListener(e -> {
            Difficulty chosenDifficulty = (Difficulty) difficultyCombo.getSelectedItem();
            if(chosenDifficulty != currentDifficulty){
                currentDifficulty = chosenDifficulty;
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

        game = new Game(currentDifficulty, this);
        buildBoardUI();

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private void buildBoardUI(){
        int rows = currentDifficulty.rows;
        int cols = currentDifficulty.cols;
        int tileSize = currentDifficulty.tileSize;
        int fontSize = currentDifficulty.fontSize;

        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout(rows, cols));
        tiles = new Tile[rows][cols];

        for(int r=0; r<rows; r++){
            for(int c=0; c<cols; c++){
                Tile currentTile = new Tile(r, c, fontSize);
                currentTile.setPreferredSize(new Dimension(tileSize, tileSize));
                final Tile finalTile = currentTile;
                currentTile.addMouseListener(new MouseAdapter(){
            
                @Override
                public void mousePressed(MouseEvent e){
                    if(SwingUtilities.isLeftMouseButton(e)){
                        game.handleLeftClick(finalTile);
                    } else if(SwingUtilities.isRightMouseButton(e)){
                        game.handleRightClick(finalTile);
                        updateTileUI(finalTile); 
                    }
                }
            });

            tiles[r][c] = currentTile;
            boardPanel.add(currentTile);

            }
        }

        game.setTilesArray(tiles);
        game.reset(currentDifficulty); 
        frame.pack();
    }

    private void resetGame(){
        game.stopTimer();
        game = new Game(currentDifficulty, this);
        buildBoardUI();
    }

    private void updateTileUI(Tile tile){
        if(tile.isFlagged()){
            tile.setText("🚩");
            tile.setForeground(Color.RED);
        } else {
            tile.setText("");
        }
    }

    // --- IMPLEMENTASI GAME LISTENER --- 

    @Override
        public void onTilesRevealed(List<Tile> revealed){
        for(Tile revealedTile : revealed) {
            int r = revealedTile.r, c = revealedTile.c;
            int adjMines = game == null ? 0 : game.getBoard().countAdjacentMines(r, c); 
            revealedTile.setEnabled(false);
            revealedTile.setBackground(new Color(225,225,225));
            revealedTile.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            if(game.getBoard().isMine(r,c)){
                revealedTile.setText("💣");
                revealedTile.setForeground(Color.BLACK);
            } else {
                if(adjMines > 0){
                revealedTile.setText(Integer.toString(adjMines));
                switch(adjMines){
                    case 1: revealedTile.setForeground(Color.BLUE); break;
                    case 2: revealedTile.setForeground(new Color(0,128,0)); break;
                    case 3: revealedTile.setForeground(Color.RED); break;
                    case 4: revealedTile.setForeground(new Color(128,0,128)); break;
                    case 5: revealedTile.setForeground(new Color(128,0,0)); break;
                    case 6: revealedTile.setForeground(new Color(0,128,128)); break;
                    case 7: revealedTile.setForeground(Color.BLACK); break;
                    case 8: revealedTile.setForeground(Color.GRAY); break;
                    }
                } else {
                    revealedTile.setText("");
                }
            }
        }
        
        frame.revalidate();
        frame.repaint();
    }



    @Override
        public void onUpdateMinesLeft(int minesLeft){
        minesLabel.setText("Mines: " + minesLeft);
    }


    @Override
        public void onUpdateTime(int seconds){
        timeLabel.setText("Time: " + seconds + "s");
    }


    @Override
    public void onGameWon(int seconds){
        titleLabel.setText("You Win! Time: " + seconds + "s");
        JOptionPane.showMessageDialog(frame, "Congratulations! You win. Time: " + seconds + " seconds", "Victory", JOptionPane.INFORMATION_MESSAGE);
    }


    @Override
    public void onGameLost(){
        titleLabel.setText("Game Over");
        for(int r=0; r<tiles.length; r++){
            for(int c=0; c<tiles[0].length; c++){
                if(game.getBoard().isMine(r,c)){
                    Tile t = tiles[r][c];
                    t.setText("💣");
                    t.setEnabled(false);
                }
            }   
        }
        JOptionPane.showMessageDialog(frame, "Game Over!", "Lost", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void onReset(){
        titleLabel.setText("Minesweeper");
        minesLabel.setText("Mines: " + currentDifficulty.mines);
        timeLabel.setText("Time: 0s");

        if(tiles != null){
            for(int r=0; r<tiles.length; r++){
                for(int c=0;c<tiles[0].length;c++){
                    Tile tile = tiles[r][c];
                    tile.setEnabled(true);
                    tile.setText("");
                    tile.setBackground(null);
                    tile.setBorder(UIManager.getBorder("Button.border"));
                    tile.setFlagged(false);
                    tile.setRevealed(false);
                }
            }
        }

        frame.revalidate();
        frame.repaint();
    }
}