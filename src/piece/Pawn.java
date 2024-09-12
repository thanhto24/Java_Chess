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
        if (primary_check < 0) return primary_check;

        Piece target = GamePanel.getPiece(targetCol, targetRow);
        int direction = (color == GamePanel.WHITE) ? -1 : 1;
        int startRow = (color == GamePanel.WHITE) ? 6 : 1;
        int promotionRow = (color == GamePanel.WHITE) ? 0 : 7;
        int enPassantRow = (color == GamePanel.WHITE) ? 3 : 4;
        int lastStartRow = (color == GamePanel.WHITE) ? 1 : 6;

        if (targetRow == prevRow + direction && targetCol == prevCol && target == null) {
            return (targetRow == promotionRow) ? 9 : 1;
        }

        if (prevRow == startRow && targetRow == prevRow + 2 * direction && targetCol == prevCol && target == null) {
            return 1;
        }

        if (targetRow == prevRow + direction && Math.abs(targetCol - prevCol) == 1 && target != null) {
            return (targetRow == promotionRow) ? 10 : 2;
        }

        if (prevRow == enPassantRow && targetRow == prevRow + direction && Math.abs(targetCol - prevCol) == 1 && target == null) {
            Piece lastPieceMove = GamePanel.states.get(GamePanel.states.size() - 1);
            if (lastPieceMove instanceof Pawn && lastPieceMove.color != this.color && lastPieceMove.prevRow == lastStartRow) {
                return 3;
            }
        }

        return 0;
    }
    @Override
	public char getSymbol() {
    	if (color == GamePanel.WHITE) {
    		return 'P';
    	} else {
    		return 'p';
    	}
    }
}
