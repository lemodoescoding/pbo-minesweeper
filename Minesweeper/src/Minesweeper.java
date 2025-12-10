import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Minesweeper {
    private class MineTile extends JButton{
        int r;
        int c;
        boolean revealed = false;
        
        public MineTile(int r, int c){
            this.r = r;
            this.c = c;
        }
    }
    
    final int EASY_ROWS = 8, EASY_COLS = 8, EASY_MINES = 10, EASY_TILE_SIZE = 50, EASY_FONT_SIZE = 20;
    final int MEDIUM_ROWS = 14, MEDIUM_COLS = 14, MEDIUM_MINES = 40, MEDIUM_TILE_SIZE = 40, MEDIUM_FONT_SIZE = 18;
    final int HARD_ROWS = 20, HARD_COLS = 20, HARD_MINES = 99, HARD_TILE_SIZE = 32, HARD_FONT_SIZE = 16;
    
    int tileSize = EASY_TILE_SIZE;
    int rows = EASY_ROWS;
    int cols = EASY_COLS;
    int fontSize = EASY_FONT_SIZE;
    int boardsWidth = cols * tileSize;
    int boardsHeight = rows * tileSize + fontSize*4; // extra space for text panel
    int tilesClicked = 0;
    int minesToSet = EASY_MINES;
    int minesLeft = minesToSet;
    int seconds = 0;
    Timer timer;
    boolean firstClick = true;
    boolean isGameOver = false;
    
    JFrame frame = new JFrame("Minesweeper");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();

    JPanel infoPanel = new JPanel();
    JLabel minesLabel = new JLabel();
    JLabel timeLabel = new JLabel();
    JButton retryButton = new JButton("Retry");

    JComboBox<String> difficultyComboBox;

    MineTile[][] boardTiles;
    ArrayList<MineTile> mineList;

    
    Minesweeper(){
        // frame.setSize(boardsWidth, boardsHeight);  // size is set in resetGame()
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        textLabel.setFont(new Font("Arial", Font.BOLD, 16));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper");
        textLabel.setOpaque(true);

        minesLabel.setText("Mines: " + minesLeft);
        minesLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        timeLabel.setText("Time: 0s");
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        retryButton.setFocusable(false);
        retryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                resetGame();
            }
        });

        String[] difficulties = {"Easy", "Medium", "Hard"};
        difficultyComboBox = new JComboBox<>(difficulties);
        difficultyComboBox.setFocusable(false);
        difficultyComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                resetGame();
            }
        });

        infoPanel.setLayout(new FlowLayout());
        infoPanel.add(difficultyComboBox);
        infoPanel.add(minesLabel);
        infoPanel.add(timeLabel);
        infoPanel.add(retryButton);
        
        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        textPanel.add(infoPanel, BorderLayout.SOUTH);

        frame.add(textPanel, BorderLayout.NORTH);

        timer = new Timer(1000, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                seconds++;
                timeLabel.setText("Time: " + seconds + "s");
            }
        });
        
        // boardPanel.setLayout(new GridLayout(rows, cols)); // set in resetGame()
        frame.add(boardPanel);
        
        // for loop to create buttons is in resetGame()
        resetGame();

        frame.setVisible(true);
        // setMines(); set in resetGame()
    }

    void setMines(){
        mineList = new ArrayList<MineTile>();
        Random rand = new Random();
        
        while(mineList.size() < minesToSet){
            int r = rand.nextInt(rows);
            int c = rand.nextInt(cols);
            MineTile tile = boardTiles[r][c];
            if(!mineList.contains(tile)){
                mineList.add(tile);
            }
        }
    }

    void gameOver(){
        timer.stop();
        for(MineTile tile : mineList){
            tile.setText("💣");
            tile.setForeground(Color.BLACK);
        }
        isGameOver = true;
        textLabel.setText("Game Over!");
    }

    void checkMine(int r, int c){
        if(r < 0 || r >= rows || c < 0 || c >= cols) return;
        MineTile tile = boardTiles[r][c];
        if(tile.revealed) return;
        tile.revealed = true;
        tile.setBackground(new Color(225, 225, 225));
        tile.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        tilesClicked++;
        int minesCount = 0;

        minesCount += countMine(r-1, c-1);  // top-left
        minesCount += countMine(r-1, c);    // top
        minesCount += countMine(r-1, c+1);  // top-right
        minesCount += countMine(r, c-1);    // left
        minesCount += countMine(r, c+1);    // right
        minesCount += countMine(r+1, c-1);  // bottom-left
        minesCount += countMine(r+1, c);    // bottom
        minesCount += countMine(r+1, c+1);  // bottom-right

        if(minesCount > 0){
            tile.setText(Integer.toString(minesCount));
            switch(minesCount) {
                case 1: tile.setForeground(Color.BLUE); break;
                case 2: tile.setForeground(new Color(0, 128, 0)); break; // Dark Green
                case 3: tile.setForeground(Color.RED); break;
                case 4: tile.setForeground(new Color(128, 0, 128)); break; // Purple
                case 5: tile.setForeground(new Color(128, 0, 0)); break;   // Maroon
                case 6: tile.setForeground(new Color(0, 128, 128)); break; // Teal
                case 7: tile.setForeground(Color.BLACK); break;
                case 8: tile.setForeground(Color.GRAY); break;
            }
        }
        else{
            tile.setText("");
            checkMine(r-1, c-1);  // top-left
            checkMine(r-1, c);    // top
            checkMine(r-1, c+1);  // top-right
            checkMine(r, c-1);    // left
            checkMine(r, c+1);    // right
            checkMine(r+1, c-1);  // bottom-left
            checkMine(r+1, c);    // bottom
            checkMine(r+1, c+1);  // bottom-right
        }
        
        if(tilesClicked == (rows * cols) - mineList.size()){
            timer.stop();
            isGameOver = true;
            textLabel.setText("You Win!");

            String message = "Congratulation, Player. You win!\nTime: " + seconds + " seconds";
            Object[] options = {"Play Again", "Exit"};
            int choice = JOptionPane.showOptionDialog(frame,
                message,
                "Victory!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);
            
            if(choice == JOptionPane.YES_OPTION){
                resetGame();
            }
            else{
                System.exit(0);
            }
        }
    }  
    
    int countMine(int r, int c){
        if(r < 0 || r >= rows || c < 0 || c >= cols) return 0;
        if(mineList.contains(boardTiles[r][c])) return 1;
        return 0;
    }

    void resetGame(){
        timer.stop();
        seconds = 0;
        firstClick = true;
        isGameOver = false;
        tilesClicked = 0;

        String selectedDifficulty = (String)difficultyComboBox.getSelectedItem();
        if(selectedDifficulty.equals("Easy")){
            rows = EASY_ROWS;
            cols = EASY_COLS;
            minesToSet = EASY_MINES;
            fontSize = EASY_FONT_SIZE;
            tileSize = EASY_TILE_SIZE;
        }
        else if(selectedDifficulty.equals("Medium")){
            rows = MEDIUM_ROWS;
            cols = MEDIUM_COLS;
            minesToSet = MEDIUM_MINES;
            fontSize = MEDIUM_FONT_SIZE;
            tileSize = MEDIUM_TILE_SIZE;
        }
        else if(selectedDifficulty.equals("Hard")){
            rows = HARD_ROWS;
            cols = HARD_COLS;
            minesToSet = HARD_MINES;
            fontSize = HARD_FONT_SIZE;
            tileSize = HARD_TILE_SIZE;
        }
        minesLeft = minesToSet;


        boardsWidth = cols * tileSize;
        boardsHeight = rows * tileSize + fontSize*4; // extra space for text panel
        frame.setSize(boardsWidth, boardsHeight);

        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout(rows, cols));
        boardTiles = new MineTile[rows][cols];

        for(int r = 0; r < rows; r++){
            for(int c = 0; c < cols; c++){
                MineTile tile = new MineTile(r, c);
                boardTiles[r][c] = tile;
                
                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(tile.getFont().deriveFont(Font.BOLD, fontSize));
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e){
                        if(isGameOver) return;
                        MineTile tile = (MineTile)e.getSource();

                        if(tile.revealed) return;

                        if(firstClick){
                            timer.start();
                            firstClick = false;
                        }

                        // Left click
                        if(e.getButton() == MouseEvent.BUTTON1){
                            if(tile.getText() == ""){
                                if(mineList.contains(tile)){
                                    gameOver();
                                }
                                else{
                                    checkMine(tile.r, tile.c);
                                }
                            }
                        }
                        // Right click
                        else if(e.getButton() == MouseEvent.BUTTON3){
                            if(tile.getText() == "" && !tile.revealed){
                                tile.setText("🚩");
                                tile.setForeground(Color.RED);
                                minesLeft--;
                                minesLabel.setText("Mines: " + minesLeft);
                            }
                            else if(tile.getText() == "🚩"){
                                tile.setText("");
                                minesLeft++;
                                minesLabel.setText("Mines: " + minesLeft);
                            }
                        }
                    }
                });
                
                boardPanel.add(tile);
            }
        }
        
        textLabel.setText("Minesweeper");
        minesLabel.setText("Mines: " + minesLeft);
        timeLabel.setText("Time: 0s");

        // for(int r = 0; r < rows; r++){
        //     for(int c = 0; c < cols; c++){
        //         MineTile tile = boardTiles[r][c];
        //         tile.setEnabled(true);
        //         tile.setText("");
        //     }
        // }

        setMines();
        frame.revalidate();
        frame.repaint();
    }
}
