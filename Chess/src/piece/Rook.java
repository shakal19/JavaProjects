package piece;

import main.GamePanel;
import main.Type;

public class Rook extends Piece{

    public Rook(int color, int col, int row) {
        super(color, col, row);
        type = Type.ROOK;


        if(color == GamePanel.WHITE){
            image = getImage("\\icons\\w-rook");
        } else {
            image = getImage("\\icons\\b-rook");
            
        }
    }
    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol, targetRow)){

            if(targetCol == preCol || targetRow == preRow && isSameSquare(targetCol, targetRow) == false){
                if(isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol,targetRow) == false){
                    return true;
                }
            }
        }
        return false;
    }
    
}
