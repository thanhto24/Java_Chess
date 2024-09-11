package piece;

import main.GamePanel;

public class Pawn extends Piece{
	public Pawn(int col, int row, int color) {
		super(col, row, color);
		if (color == GamePanel.WHITE) {
			image = getImage("/piece/w-pawn.png");
		} else {
			image = getImage("/piece/b-pawn.png");
		}
	}
	
    @Override
	public int checkMove(int targetCol, int targetRow) {
    	int primary_check = super.checkMove(targetCol, targetRow);
    	if (primary_check < 0)
    		return primary_check;
    	Piece target = GamePanel.getPiece(targetCol, targetRow);
		if (color == GamePanel.WHITE) {
			if (targetRow == prevRow - 1 && targetCol == prevCol && target == null) {
				return 1;
			}
			if (prevRow == 6 && targetRow == prevRow - 2 && targetCol == prevCol && target == null) {
				return 1;
			}
			if (targetRow == prevRow - 1 && (targetCol == prevCol - 1 || targetCol == prevCol + 1) && target != null) {
				return 2;
			}
			if (prevRow == 3 && targetRow == prevRow - 1 && (targetCol == prevCol - 1 || targetCol == prevCol + 1) && target == null) {
				
				Piece lastPieceMove = new Piece(-1, -1, -1);
				for (Piece p : GamePanel.states) {
					lastPieceMove = p;
				}
				if (lastPieceMove instanceof Pawn && lastPieceMove.color == GamePanel.BLACK && lastPieceMove.prevRow == 1) {
					return 3;
				}
			}
		} else {
			if (targetRow == prevRow + 1 && targetCol == prevCol && target == null) {
				return 1;
			}
			if (prevRow == 1 && targetRow == prevRow + 2 && targetCol == prevCol && target == null) {
				return 1;
			}
			if (targetRow == prevRow + 1 && (targetCol == prevCol - 1 || targetCol == prevCol + 1) && target != null) {
				return 2;
			}
			if (prevRow == 4 && targetRow == prevRow + 1 && (targetCol == prevCol - 1 || targetCol == prevCol + 1)
					&& target == null) {
				Piece lastPieceMove = new Piece(-1, -1, -1);
				for (Piece p : GamePanel.states) {
					lastPieceMove = p;
				}
				if (lastPieceMove instanceof Pawn && lastPieceMove.color == GamePanel.WHITE
						&& lastPieceMove.prevRow == 6) {
					return 3;
				}
			}
		}
		System.out.println("Pawn can't Move from" + prevCol + ", " + prevRow + " to "  + targetCol + ", " + targetRow);
		return 0;
	}
    
}
