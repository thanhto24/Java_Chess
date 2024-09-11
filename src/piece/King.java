package piece;

import main.GamePanel;

public class King extends Piece{
	boolean moved = false;
	public King(int col, int row, int color) {
		super(col, row, color);
		if (color == GamePanel.WHITE) {
			image = getImage("/piece/w-king.png");
		} else {
			image = getImage("/piece/b-king.png");
		}
	}
	
	@Override
	public int checkMove(int targetCol, int targetRow) {
		int primary_check = super.checkMove(targetCol, targetRow);
		if (primary_check < 0)
			return primary_check;
		Piece target = GamePanel.getPiece(targetCol, targetRow);
		if (Math.abs(targetCol - prevCol) <= 1 && Math.abs(targetRow - prevRow) <= 1) {
			moved = true;
			if (target == null)
				return 1;
			return 2;
		}
		if (moved) return -4; // No castling if the piece has already moved

	    int rookCol = (targetCol == 2) ? 0 : 7;
	    int emptyColStart = (targetCol == 2) ? 1 : 5;
	    int emptyColEnd = (targetCol == 2) ? 3 : 6;
	    int returnCode = (color == GamePanel.WHITE) ? (targetCol == 2 ? 5 : 6) : (targetCol == 2 ? 7 : 8);

	    if (targetRow != 0 && targetRow != 7) return -1; // Invalid row for castling

	    Piece rook = GamePanel.getPiece(rookCol, targetRow);
	    if (rook instanceof Rook && !((Rook) rook).moved) {
	        boolean pathClear = true;
	        for (int col = emptyColStart; col <= emptyColEnd; col++) {
	            if (GamePanel.getPiece(col, targetRow) != null) {
	                pathClear = false;
	                break;
	            }
	        }
	        if (pathClear) {
	        	moved = true;
	            return returnCode;
	        }
	    }
		return 0;
	}
}
