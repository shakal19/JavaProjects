package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.AlphaComposite;
import java.util.ArrayList;

import javax.swing.JPanel;

import piece.Bishop;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Piece;
import piece.Queen;
import piece.Rook;

public class GamePanel extends JPanel implements Runnable{

    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    //PieCES
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    public static ArrayList<Piece> promoPieces = new ArrayList<>();
    Piece activeP, checkignP;
    public static  Piece castlingP;

    //COLOR
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    // booleans
    boolean canMove;
    boolean validSquare;
    boolean promoted;
    boolean gameOver; 
    boolean stalemate;

    public GamePanel(){
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        setBackground(Color.black);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        setPieces();
        // testIllegal();
        copyPieces(pieces, simPieces);
    }

    public void launchGame(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setPieces(){
        // white team
        pieces.add(new Pawn(WHITE, 0, 6));
        pieces.add(new Pawn(WHITE, 1, 6));
        pieces.add(new Pawn(WHITE, 2, 6));
        pieces.add(new Pawn(WHITE, 3, 6));
        pieces.add(new Pawn(WHITE, 4, 6));
        pieces.add(new Pawn(WHITE, 5, 6));
        pieces.add(new Pawn(WHITE, 6, 6));
        pieces.add(new Pawn(WHITE, 7, 6));
        pieces.add(new Rook(WHITE, 0, 7));
        pieces.add(new Rook(WHITE, 7, 7));
        pieces.add(new Knight(WHITE, 1, 7));
        pieces.add(new Knight(WHITE, 6, 7));
        pieces.add(new Bishop(WHITE, 2, 7));
        pieces.add(new Bishop(WHITE, 5, 7));
        pieces.add(new Queen(WHITE, 3, 7));
        pieces.add(new King(WHITE, 4, 7));

        // black team
        pieces.add(new Pawn(BLACK, 0, 1));
        pieces.add(new Pawn(BLACK, 1, 1));
        pieces.add(new Pawn(BLACK, 2, 1));
        pieces.add(new Pawn(BLACK, 3, 1));
        pieces.add(new Pawn(BLACK, 4, 1));
        pieces.add(new Pawn(BLACK, 5, 1));
        pieces.add(new Pawn(BLACK, 6, 1));
        pieces.add(new Pawn(BLACK, 7, 1));
        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Rook(BLACK, 7, 0));
        pieces.add(new Knight(BLACK, 1, 0));
        pieces.add(new Knight(BLACK, 6, 0));
        pieces.add(new Bishop(BLACK, 2, 0));
        pieces.add(new Bishop(BLACK, 5, 0));
        pieces.add(new Queen(BLACK, 3, 0));
        pieces.add(new King(BLACK, 4, 0));
    }
    public void testPromotion(){
        pieces.add(new Pawn(WHITE,0,3));
        pieces.add(new Pawn(BLACK,5,5));
    }
    public void testIllegal(){
        pieces.add(new Pawn(WHITE,7,6));
        pieces.add(new King(WHITE,3,7));
        pieces.add(new King(BLACK, 0, 5));
        pieces.add(new Queen(BLACK, 4, 5));
        pieces.add(new Bishop(BLACK, 4, 3));
        pieces.add(new Queen(WHITE, 2, 3));

    }

    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target){
        target.clear();
        for(int i = 0; i < source.size(); i++){
            target.add(source.get(i));
        }
    }

    @Override
    public void run() {
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null){

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1){
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update(){
        if(promoted){
            promoting();

        } else if(gameOver == false && stalemate == false){
             // MOUSE BUTTON PRESSED
            if(mouse.pressed){
                if (activeP == null){
                    // If activeP is null, check if you can move a piece
                    for(Piece piece: simPieces){
                        if(piece.color == currentColor && piece.col == mouse.x/Board.SQUARE_SIZE &&
                            piece.row == mouse.y/Board.SQUARE_SIZE){

                                activeP = piece;
                            }
                    }
                } else {
                    simulate();
                }

            
        }
        if(mouse.pressed == false){

            if(activeP != null){

                if(validSquare){

                    // move confirmed

                    // update the piece list in case that piece has been captured and removed from the board
                    copyPieces(simPieces, pieces);
                    activeP.updatePosition();
                    if(castlingP != null){
                        castlingP.updatePosition();
                    }

                    if(isKingInCheck() && isCheckMate()){
                        // possibly gameover
                        gameOver = true;
                    } else if(isStaleMate() && isKingInCheck() == false){
                        stalemate = true;
                    }
                    else { // game keeps going
                        if(canPromote()){
                            promoted = true;
                        } else {
    
                            changePlayer();
                        }

                    }
                   
                }
                else {
                    // move is not valid, reset everything
                    copyPieces(simPieces, pieces);
                    activeP.resetPosition();
                    activeP = null;
                }
            }
        }
    }

}
       
    private void simulate(){
        canMove = false;
        validSquare = false;
        // if piece is held, update its position

        // reset piece list every time
        copyPieces(pieces, simPieces);
        // reset castling piece's position
        if(castlingP != null){
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col); 
            castlingP = null;

        }

        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);

        if(activeP.canMove(activeP.col, activeP.row)){
            canMove = true;
            // if hitting piece, remove it from the board
            if(activeP.hittingP != null){
                simPieces.remove(activeP.hittingP.getIndex());
            }
            checkCastling();
            if(isIlegal(activeP) == false && opponentCanCaptureKing() == false){
                validSquare  = true;
            } 
                
        }

    }
    private boolean isIlegal(Piece king){

        if(king.type == Type.KING){
            for(Piece piece : simPieces){
                if(piece != king && piece.color != king.color && piece.canMove(king.col, king.row)){
                    return true;
                }
            }
        }

        return false;
    }
    private boolean opponentCanCaptureKing(){
        Piece king = getKing(false);

        for(Piece piece : simPieces){
            if(piece.color != king.color  && piece.canMove(king.col, king.row)){
                return true;
            }
        }
        
        return false;


    }
    private boolean isStaleMate(){

        int count = 0;
        // Count number of pieces
        for(Piece piece : simPieces){
            if(piece.color != currentColor){
                count++;
            }
        }
        if(count == 1){
            if(kingCanMove(getKing(true)) == false){
                return true;
            }
        }

        return false;
    }

    private void checkCastling(){
        if(castlingP != null){
            if(castlingP.col == 0){
                castlingP.col +=3;
            } 
            else if(castlingP.col == 7){
                castlingP.col -=2;
            }
            castlingP.x = castlingP.getX(castlingP.col);
        }
    }
    private void changePlayer(){
        if(currentColor == WHITE){
            currentColor = BLACK;
            // reset black twostepped variable
            for(Piece piece : pieces){
                if(piece.color == BLACK){
                    piece.twoStepped = false;
                }
            }
        }
        else {
            currentColor =WHITE;
            // reset black twostepped variable

            for(Piece piece : pieces){
                if(piece.color == WHITE){
                    piece.twoStepped = false;
                }
            }

        }
        activeP = null;
    }
    private boolean canPromote(){
        
        if(activeP.type == Type.PAWN){
            if(currentColor == WHITE && activeP.row == 0 || currentColor == BLACK && activeP.row == 7){
                promoPieces.clear();
                promoPieces.add(new Rook(currentColor,9,2));
                promoPieces.add(new Knight(currentColor,9,3));
                promoPieces.add(new Bishop(currentColor,9,4));
                promoPieces.add(new Queen(currentColor, 9, 5));
                return true;


            }
        }

        return false;
    }
    private void promoting(){
        if(mouse.pressed){
            for(Piece piece : promoPieces){
                if(piece.col == mouse.x / Board.SQUARE_SIZE && piece.row == mouse.y / Board.SQUARE_SIZE){
                    switch (piece.type) {
                        case ROOK: simPieces.add(new Rook(currentColor,activeP.col,activeP.row)); break;
                        case KNIGHT: simPieces.add(new Knight(currentColor,activeP.col,activeP.row)); break;
                        case BISHOP: simPieces.add(new Bishop(currentColor,activeP.col,activeP.row)); break;
                        case QUEEN: simPieces.add(new Queen(currentColor,activeP.col,activeP.row)); break;
                        default: break;
                    }
                    simPieces.remove(activeP.getIndex());
                    copyPieces(simPieces, pieces);
                    activeP = null;
                    promoted = false;
                    changePlayer();
                }
            }
        }
    }

    private Piece getKing(boolean opponent){
        Piece king = null;
        for(Piece piece : simPieces){
            if(opponent){
                if(piece.type == Type.KING && piece.color != currentColor){
                    king = piece;
                }
            } else {
               if(piece.type == Type.KING && piece.color == currentColor){
                king = piece;
               } 
            }
        }
        return king;
    }

    public boolean isKingInCheck(){

        Piece king = getKing(true);
        if(activeP.canMove(king.col, king.row)){
            checkignP = activeP;
            return true;
        } else {
            checkignP = null;
        }

        return false;
    }
    private boolean isCheckMate(){
        Piece king = getKing(true);
        if(kingCanMove(king)){
            return false;
        }
        else {
            // King can not move but you can block the attack
            // check position of opponent check piece and king in check
            int colDiff = Math.abs(checkignP.col - king.col);
            int rowDiff = Math.abs(checkignP.row - king.row);

            if(colDiff == 0){
                // vertical attack
                if(checkignP.row < king.row){
                    // attack from above
                    for(int row = checkignP.row; row < king.row; row++){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.color != currentColor && piece.canMove(checkignP.col, row)){
                                return false;
                            }
                        }
                    }

                }
                if(checkignP.row > king.row){
                    // attack from below
                    for(int row = checkignP.row; row > king.row; row--){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.color != currentColor && piece.canMove(checkignP.col, row)){
                                return false;
                            }
                        }
                    }

                }

            }
            else if(rowDiff == 0){
                // horizontal attack
                if(checkignP.col < king.col){
                    // leftside attack
                    for(int col = checkignP.col; col < king.row; col++){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.color != currentColor && piece.canMove(col, checkignP.row)){
                                return false;
                            }
                        }
                    }
                }
                if(checkignP.col > king.col){
                    // rightside attack
                    for(int col = checkignP.col; col > king.row; col--){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.color != currentColor && piece.canMove(col, checkignP.row)){
                                return false;
                            }
                        }
                    }
                }

            }
            else if(rowDiff == colDiff){
                // diagonal attack 
                if(checkignP.row < king.row){
                    // attack from above
                    if(checkignP.col < king.col){
                        // left upper attack
                        for(int col = checkignP.col, row = checkignP.row; col < king.col;col++, row++){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                    if(checkignP.col > king.col){
                        // right upper attack
                        for(int col = checkignP.col, row = checkignP.row; col > king.col;col--, row++){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }

                }
                if(checkignP.row > king.row){
                    // attack from below
                    if(checkignP.col < king.col){
                        // left below attack
                        for(int col = checkignP.col, row = checkignP.row; col < king.col;col++, row--){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                    if(checkignP.col > king.col){
                        // right below attack
                        for(int col = checkignP.col, row = checkignP.row; col > king.col;col--, row--){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }

                }

            } 
            else {
                // attack by knight
            }

            
        }

        return true;
        

    }
    private boolean kingCanMove(Piece king){
        // If king can move to any square
        if(isValidMove(king, -1, -1)) return true;
        if(isValidMove(king, 0, -1)) return true;
        if(isValidMove(king, 1, -1)) return true;
        if(isValidMove(king, -1, 0)) return true;
        if(isValidMove(king, 1, 0)) return true;
        if(isValidMove(king, -1, 1)) return true;
        if(isValidMove(king, 0, 1)) return true;
        if(isValidMove(king, 1, 1)) return true;

        return false;
    }
    private boolean isValidMove(Piece king, int colPlus, int rowPlus){
            boolean isValidMove = false;
            // update the king's position for second
            king.col += colPlus;
            king.row += rowPlus;

            if(king.canMove(king.col, king.row)){

                if(king.hittingP != null){
                    simPieces.remove(king.hittingP.getIndex());
                }
                if(isIlegal(king) == false){
                    isValidMove = true;
                }

            }
            king.resetPosition();
            copyPieces(pieces, simPieces);

            return isValidMove;
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        // board
        board.draw(g2);

        //pieces
        for(Piece p: simPieces){
            p.draw(g2);
        }
        if(activeP != null){
            if(canMove){
                if(isIlegal(activeP) || opponentCanCaptureKing()){
                    g2.setColor(Color.gray);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.7f));
                    g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE,
                        Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
                } else {

                    g2.setColor(Color.white);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.7f));
                    g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE,
                                Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
                }
            }
            

            activeP.draw(g2);

            
        }
        // STATUS MESSAGE
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Arial",Font.PLAIN,40));
        g2.setColor(Color.white);

        if(promoted){
            g2.drawString("Promotion", 840,150);
            for(Piece piece : promoPieces){
                g2.drawImage(piece.image,piece.getX(piece.col), piece.getY(piece.row),Board.SQUARE_SIZE,
                Board.SQUARE_SIZE,null);
                
            }
        } else {
            if(currentColor == WHITE){
                g2.drawString("White's turn", 840, 550);
                if(checkignP != null && checkignP.color == BLACK){
                    g2.setColor(Color.red);
                    g2.drawString("THE KING",840 , 650);
                    g2.drawString("is in check",840  , 700);
                }
            } else {
                g2.drawString("Black's turn", 840, 250);
                if(checkignP != null && checkignP.color == WHITE){
                    g2.setColor(Color.red);
                    g2.drawString("THE KING",840 , 100);
                    g2.drawString("is in check",840  , 150);
                }
    
            }
        }
        if(gameOver){
            String s = "";
            if(currentColor == WHITE){
                s = "White wins";
            }
            else {
                s = "Black wins";
            }
            g2.setFont(new Font("Arial",Font.PLAIN,90));
            g2.setColor(Color.green);
            g2.drawString(s, 200, 420);
        }
        if(stalemate){
            g2.setFont(new Font("Arial",Font.PLAIN,90));
            g2.setColor(Color.blue);
            g2.drawString("Stalemate", 200, 420);
        }

    }
}
