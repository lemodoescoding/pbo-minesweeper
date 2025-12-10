import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Minesweeper {
    private class MineTile extends JButton{
        int r;
        int c;
        
        public MineTile(int r, int c){
            this.r = r;
            this.c = c;
        }
    }
    
    int tileSize = 64;
    int rows = 8;
    int cols = rows;
    int boardsWidth = cols * tileSize;
    int boardsHeight = rows * tileSize + 50; // extra space for text panel
    int tilesClicked = 0;
    int minesToSet = 10;
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

    MineTile[][] boardTiles = new MineTile[rows][cols];
    ArrayList<MineTile> mineList;

    
    Minesweeper(){
        frame.setSize(boardsWidth, boardsHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        textLabel.setFont(new Font("Arial", Font.BOLD, 32));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper");
        textLabel.setOpaque(true);

        minesLabel.setText("Mines: " + minesLeft);
        minesLabel.setFont(new Font("Arial", Font.PLAIN, 18));

        timeLabel.setText("Time: 0s");
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 18));

        retryButton.setFocusable(false);
        retryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                resetGame();
            }
        });

        infoPanel.setLayout(new FlowLayout());
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
        
        boardPanel.setLayout(new GridLayout(rows, cols));
        frame.add(boardPanel);
        
        for(int r = 0; r < rows; r++){
            for(int c = 0; c < cols; c++){
                MineTile tile = new MineTile(r, c);
                boardTiles[r][c] = tile;
                
                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                // tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 16)); // this doesnt work
                tile.setFont(tile.getFont().deriveFont(Font.PLAIN, 24)); // so i use this instead
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e){
                        if(isGameOver) return;
                        MineTile tile = (MineTile)e.getSource();

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
                            if(tile.getText() == "" && tile.isEnabled()){
                                tile.setText("🚩");
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

        frame.setVisible(true);
        setMines();
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
                // For testing purposes, show the mines
                // tile.setText("💣");
            }
        }
    }

    void gameOver(){
        timer.stop();
        for(MineTile tile : mineList){
            tile.setText("💣");
        }
        isGameOver = true;
        textLabel.setText("Game Over!");
    }

    void checkMine(int r, int c){
        if(r < 0 || r >= rows || c < 0 || c >= cols) return;
        MineTile tile = boardTiles[r][c];
        if(!tile.isEnabled()) return;
        tile.setEnabled(false);
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
        minesLeft = minesToSet;

        textLabel.setText("Minesweeper");
        minesLabel.setText("Mines: " + minesLeft);
        timeLabel.setText("Time: 0s");

        for(int r = 0; r < rows; r++){
            for(int c = 0; c < cols; c++){
                MineTile tile = boardTiles[r][c];
                tile.setEnabled(true);
                tile.setText("");
            }
        }

        setMines();
    }
}
