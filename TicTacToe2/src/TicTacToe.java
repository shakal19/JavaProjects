import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;



public class TicTacToe {
    int boardWidth = 860;
    int boardHeight = 900;

    JFrame frame = new JFrame("TicTacToe");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    
    JButton[][] board = new JButton[3][3];
    String playerX = "X";
    String playerO = "O";
    String currentPlayer = playerX;
    boolean gameOver = false;
    int turns = 0;

    boolean againstPC = false;

    TicTacToe(){
        createModeSelection();
    }

    void createModeSelection(){
        JFrame modeFrame = new JFrame("Main menu");
        modeFrame.setSize(300,300);
        modeFrame.setLocationRelativeTo(null);
        modeFrame.setResizable(false);
        modeFrame.setLayout(new GridLayout(3,1));
        modeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton twoPlayerButton = new JButton("Two players");
        twoPlayerButton.setFont(new Font("Arial", Font.BOLD, 20));
        twoPlayerButton.setFocusable(false);
        twoPlayerButton.setForeground(Color.white);
        twoPlayerButton.setBackground(Color.black);
        twoPlayerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                againstPC = false;
                modeFrame.dispose();
                initializeGame();
            }
        });

        JButton againstPCButton = new JButton("Against PC");
        againstPCButton.setFont(new Font("Arial", Font.BOLD, 20));
        againstPCButton.setFocusable(false);
        againstPCButton.setForeground(Color.white);
        againstPCButton.setBackground(Color.black);
        againstPCButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                againstPC = true;
                modeFrame.dispose();
                initializeGame();

            }
        });
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 20));
        exitButton.setFocusable(false);
        exitButton.setForeground(Color.red);
        exitButton.setBackground(Color.black);
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                
                System.exit(0);
                
                

            }
        });

        modeFrame.add(twoPlayerButton);
        modeFrame.add(againstPCButton);
        modeFrame.add(exitButton);
        modeFrame.setVisible(true);

    }

    void initializeGame(){
        frame.setVisible(true);
        frame.setSize(boardWidth,boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setBackground(Color.gray);
        textLabel.setForeground(Color.white);
        textLabel.setFont(new Font("Arial",Font.BOLD,20));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("TicTacToe");
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel,BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(3,3));
        boardPanel.setBackground(Color.darkGray);
        frame.add(boardPanel);

        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                JButton tile = new JButton();
                board[i][j] = tile;
                boardPanel.add(tile);

                tile.setBackground(Color.darkGray);
                tile.setForeground(Color.white);
                tile.setFont(new Font("Arial", Font.BOLD, 120));
                tile.setFocusable(false);
                if (!againstPC){
                    twoPlayersGame(tile);
                } else {
                    againstPCGame(tile);
                }
                
            }
        }
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e){
                if (e.getKeyChar() == 'y' || e.getKeyChar() == 'Y'){
                    restartGame();
                } else if(e.getKeyChar() == 'b' || e.getKeyChar() == 'B'){
                    boardPanel.removeAll();    // Clear the board for future use
                    frame.remove(boardPanel);  // Remove the game board panel
                    createModeSelection();     // Recreate the mode selection panel
                    // frame.revalidate();        // Refresh the frame to apply changes
                    // frame.repaint();  
                    restartGame();
                    
                    
                }
            }
        });
        frame.setFocusable(true);
        frame.requestFocusInWindow();
    }
    void checkWinner(){
        // horizontally
        for (int r = 0; r < 3; r++) {
            if (board[r][0].getText().equals("")) continue;
            if (board[r][0].getText().equals(board[r][1].getText()) &&
                board[r][1].getText().equals(board[r][2].getText())) {
                for (int i = 0; i < 3; i++) {
                    setWinner(board[r][i]);
                }
                gameOver = true;
                return;
            }
        }
        // vertically
        for (int c = 0; c < 3; c++) {
            if (board[0][c].getText().equals("")) continue;
            if (board[0][c].getText().equals(board[1][c].getText()) &&
                board[1][c].getText().equals(board[2][c].getText())) {
                for (int i = 0; i < 3; i++) {
                    setWinner(board[i][c]);
                }
                gameOver = true;
                return;
            }
        }
        // diagonally
        if (board[0][0].getText().equals(board[1][1].getText()) &&
            board[1][1].getText().equals(board[2][2].getText()) &&
            !board[0][0].getText().equals("")) {
            for (int i = 0; i < 3; i++) {
                setWinner(board[i][i]);
            }
            gameOver = true;
            return;
        }
        // anti-diagonally
        if (board[0][2].getText().equals(board[1][1].getText()) &&
            board[1][1].getText().equals(board[2][0].getText()) &&
            !board[0][2].getText().equals("")) {
            setWinner(board[0][2]);
            setWinner(board[1][1]);
            setWinner(board[2][0]);
            gameOver = true;
            return;
        }
        if (turns == 9) {
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    setTie(board[r][c]);
                }
            }
            gameOver = false;
        }
    }

    void setWinner(JButton tile){
        tile.setForeground(Color.green);
        tile.setBackground(Color.gray);
        textLabel.setText(currentPlayer + " is the winner. Press 'Y' to play again. Or press B to return to main menu");
    }

    void setTie(JButton tile){
        
        tile.setBackground(Color.orange);
        tile.setForeground(Color.gray);
        
        
    }

    void restartGame(){
        currentPlayer = playerX;
        gameOver = false;
        turns = 0;
        textLabel.setText("TicTacToe");
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                board[r][c].setText("");
                board[r][c].setForeground(Color.white);
                board[r][c].setBackground(Color.darkGray);
            }
        }
    }
    void twoPlayersGame(JButton tile){
        tile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                if (gameOver) return;
                JButton tile = (JButton) e.getSource();
                if(tile.getText().equals("")){
                    tile.setText(currentPlayer);
                    turns++;
                    checkWinner();
                    if (!gameOver && turns < 9){
                        currentPlayer = currentPlayer.equals(playerX) ? playerO : playerX;
                        textLabel.setText(currentPlayer + " 's turn");

                    } else if(!gameOver && turns == 9){
                        textLabel.setText("Tie game! Play again 'Y/y'? Or press B to return to main menu.");
                    }
                }
            }
        });
    }
    void againstPCGame(JButton tile){
        tile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                if (gameOver) return;
                JButton tile = (JButton) e.getSource();
                if(tile.getText().equals("")){
                    tile.setText(currentPlayer);
                    turns++;
                    checkWinner();
                    if (!gameOver && turns < 9){
                        currentPlayer = playerO;
                        textLabel.setText(currentPlayer + " 's turn");
                        PCMove();
                        
                        

                    } else if(!gameOver && turns == 9){
                        textLabel.setText("Tie game! Play again 'Y/y'?");
                    }
                }
            }
        });
    }
    void PCMove(){
        if (gameOver) return;

        Timer timer = new Timer(5000, new ActionListener() {
            public void actionPerformed(ActionEvent e){

                Random random = new Random();
                int row, col;

        do {
            row = random.nextInt(3);
            col = random.nextInt(3);

        } while (!board[row][col].getText().equals(""));

        board[row][col].setText(playerO);
        turns++;

        checkWinner();
        if (!gameOver){
            currentPlayer = playerX;
            textLabel.setText(currentPlayer + " 's turn");
        }
        ((Timer)e.getSource()).stop();
            }
        });
        timer.setRepeats(false);
        timer.start();
        
    }
}
