import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Minesweeper {
    private class MineTile extends JButton {
        int r;
        int c;

        public MineTile(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    int tileSize = 70;
    int numRows = 8;
    int numCols = 8;
    int boardWidth;
    int boardHeight;
    int mineCount = 10;
    
    JFrame frame = new JFrame("Minesweeper");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel menuPanel = new JPanel();
    
    MineTile[][] board;
    ArrayList<MineTile> mineList;
    Random random = new Random();
    int tilesClicked = 0;
    boolean gameOver = false;

    public Minesweeper() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        showMenu();
    }

    void showMenu() {
        frame.getContentPane().removeAll();
        
        menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(3, 1, 10, 10));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        JLabel titleLabel = new JLabel("Minesweeper", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JButton button8x8 = new JButton("8x8 Grid");
        JButton button9x9 = new JButton("9x9 Grid");
        
        button8x8.setFont(new Font("Arial", Font.PLAIN, 18));
        button9x9.setFont(new Font("Arial", Font.PLAIN, 18));
        
        button8x8.addActionListener(e -> startGame(8, 8, 10));
        button9x9.addActionListener(e -> startGame(9, 9, 10));
        
        menuPanel.add(titleLabel);
        menuPanel.add(button8x8);
        menuPanel.add(button9x9);
        
        frame.add(menuPanel);
        frame.setSize(8 * tileSize, 8 * tileSize);
        frame.setVisible(true);
    }

    void startGame(int rows, int cols, int mines) {
        this.numRows = rows;
        this.numCols = cols;
        this.mineCount = mines;
        this.boardWidth = numCols * tileSize;
        this.boardHeight = numRows * tileSize;
        this.tilesClicked = 0;
        this.gameOver = false;
        
        frame.getContentPane().removeAll();
        setupGame();
    }

    void setupGame() {
        frame.setLayout(new BorderLayout());
        
        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper: " + Integer.toString(mineCount));
        textLabel.setOpaque(true);

        textPanel = new JPanel(new BorderLayout());
        textPanel.add(textLabel, BorderLayout.CENTER);
        
        JButton menuButton = new JButton("Menu");
        menuButton.addActionListener(e -> showMenu());
        textPanel.add(menuButton, BorderLayout.EAST);
        
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel = new JPanel(new GridLayout(numRows, numCols));
        frame.add(boardPanel, BorderLayout.CENTER);

        board = new MineTile[numRows][numCols];
        
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;
                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));
                
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) {
                            return;
                        }
                        MineTile tile = (MineTile) e.getSource();

                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (tile.getText() == "") {
                                if (mineList.contains(tile)) {
                                    revealMines();
                                } else {
                                    checkMine(tile.r, tile.c);
                                }
                            }
                        }
                        else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (tile.getText() == "" && tile.isEnabled()) {
                                tile.setText("🚩");
                            } else if (tile.getText() == "🚩") {
                                tile.setText("");
                            }
                        }
                    }
                });

                boardPanel.add(tile);
            }
        }

        frame.setSize(boardWidth, boardHeight + 80);
        frame.setVisible(true);
        setMines();
    }

    void setMines() {
        mineList = new ArrayList<MineTile>();
        int mineLeft = mineCount;
        
        while (mineLeft > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);

            MineTile tile = board[r][c];
            if (!mineList.contains(tile)) {
                mineList.add(tile);
                mineLeft -= 1;
            }
        }
    }

    void revealMines() {
        for (int i = 0; i < mineList.size(); i++) {
            MineTile tile = mineList.get(i);
            tile.setText("💣");
        }

        gameOver = true;
        textLabel.setText("Game Over!");
    }

    void checkMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return;
        }

        MineTile tile = board[r][c];
        
        if (!tile.isEnabled()) {
            return;
        }
        tile.setEnabled(false);
        tilesClicked += 1;

        int minesFound = 0;

        minesFound += countMine(r-1, c-1);
        minesFound += countMine(r-1, c);
        minesFound += countMine(r-1, c+1);
        minesFound += countMine(r, c-1);
        minesFound += countMine(r, c+1);
        minesFound += countMine(r+1, c-1);
        minesFound += countMine(r+1, c);
        minesFound += countMine(r+1, c+1);

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound));
        } else {
            tile.setText("");

            checkMine(r-1, c-1);
            checkMine(r-1, c);
            checkMine(r-1, c+1);
            checkMine(r, c-1);
            checkMine(r, c+1);
            checkMine(r+1, c-1);
            checkMine(r+1, c);
            checkMine(r+1, c+1);
        }

        if (tilesClicked == numRows * numCols - mineList.size()) {
            gameOver = true;
            textLabel.setText("Mines Cleared! you won !!");
        }
    }

    int countMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return 0;
        }
        if (mineList.contains(board[r][c])) {
            return 1;
        }
        return 0;
    }

    public static void main(String[] args) {
        new Minesweeper();
    }
}