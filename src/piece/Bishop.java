package piece;

import main.GamePanel;

public class Bishop extends Piece{
	public Bishop(int col, int row, int color) {
		super(col, row, color);
		if (color == GamePanel.WHITE) {
			image = getImage("/piece/w-bishop.png");
		} else {
			image = getImage("/piece/b-bishop.png");
		}
	}
	    
    @Override
    public int checkMove(int targetCol, int targetRow) {
    	int primary_check = super.checkMove(targetCol, targetRow);
    	if (primary_check < 0)
    		return primary_check;
    	Piece target = GamePanel.getPiece(targetCol, targetRow);
    	if (Math.abs(targetCol - prevCol) == Math.abs(targetRow - prevRow)) {
    		int stepX = (targetCol - prevCol) > 0 ? 1 : -1;
    		int stepY = (targetRow - prevRow) > 0 ? 1 : -1;
    		int tmpCol = prevCol;
    		int tmpRow = prevRow;
			while (tmpCol != targetCol && tmpRow != targetRow) {
				tmpCol += stepX;
				tmpRow += stepY;
				Piece obstacle = GamePanel.getPiece(tmpCol, tmpRow);
				if (obstacle != null && obstacle != target)
					return 0;
			}
    		if (target == null)
    			return 1;
			return 2;
    	}
    	return 0;
    }
}
