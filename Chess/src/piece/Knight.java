package piece;

import main.GamePanel;
import main.Type;

public class Knight extends Piece{

    public Knight(int color, int col, int row) {
        super(color, col, row);
        type = Type.KNIGHT;

        
        if(color == GamePanel.WHITE){
            image = getImage("\\icons\\w-knight");
        } else {
            image = getImage("\\icons\\b-knight");
            
        }
    }
    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol, targetRow)){
            // knight movement ratio col and row is 1:2 or 2:1
            if(Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 2){
                if(isValidSquare(targetCol, targetRow)){
                    return true;
                }
            }
        }
        return false;
    }
    
}
