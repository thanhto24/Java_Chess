package piece;

import main.GamePanel;
import stockfish.Fen_Gen;

public class King extends Piece{
	public boolean moved = false;
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
		System.out.println("King checkMove");
		int primary_check = super.checkMove(targetCol, targetRow);
		if (primary_check < 0)
			return primary_check;
		Piece target = GamePanel.getPiece(targetCol, targetRow);
		if (Math.abs(targetCol - prevCol) <= 1 && Math.abs(targetRow - prevRow) <= 1) {
			moved = true;
			if (color == GamePanel.WHITE) {
				Fen_Gen.whiteCanCastleKingside = false;
				Fen_Gen.whiteCanCastleQueenside = false;
			} else {
				Fen_Gen.blackCanCastleKingside = false;
				Fen_Gen.blackCanCastleQueenside = false;
			}
			if (target == null)
				return 1;
			return 2;
		}
		if (moved)
			return -4;
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
				if (color == GamePanel.WHITE) {
					Fen_Gen.whiteCanCastleKingside = false;
					Fen_Gen.whiteCanCastleQueenside = false;
				} else {
					Fen_Gen.blackCanCastleKingside = false;
					Fen_Gen.blackCanCastleQueenside = false;
				}
	            return returnCode;
	        }
	    }
		return 0;
	}
	@Override
	public char getSymbol() {
		if (color == GamePanel.WHITE) {
			return 'K';
		} else {
			return 'k';
		}
	}
}
