package piece;

import main.GamePanel;
import stockfish.Fen_Gen;

public class Rook extends Piece{
	public boolean moved = false;
	public Rook(int col, int row, int color) {
		super(col, row, color);
		if (color == GamePanel.WHITE) {
			image = getImage("/piece/w-rook.png");
		} else {
			image = getImage("/piece/b-rook.png");
		}
	}
	
	@Override
	public int checkMove(int targetCol, int targetRow) {
		int primary_check = super.checkMove(targetCol, targetRow);
		if (primary_check < 0)
			return primary_check;
		Piece target = GamePanel.getPiece(targetCol, targetRow);
		if (targetCol == prevCol || targetRow == prevRow) {
			int stepX = targetCol == prevCol ? 0 : (targetCol - prevCol) > 0 ? 1 : -1;
			int stepY = targetRow == prevRow ? 0 : (targetRow - prevRow) > 0 ? 1 : -1;
			int tmpCol = prevCol;
			int tmpRow = prevRow;
			while (tmpCol != targetCol || tmpRow != targetRow) {
				tmpCol += stepX;
				tmpRow += stepY;
				Piece obstacle = GamePanel.getPiece(tmpCol, tmpRow);
				if (obstacle != null && obstacle != target)
					return 0;
			}
			moved = true;
			if (color == GamePanel.WHITE) {
				if (prevCol == 0)
					Fen_Gen.whiteCanCastleQueenside = false;
				if (prevCol == 7)
					Fen_Gen.whiteCanCastleKingside = false;
			} else {
                if (prevCol == 0)
                    Fen_Gen.blackCanCastleQueenside = false;
                if (prevCol == 7)
                    Fen_Gen.blackCanCastleKingside = false;
			}
			if (target == null)
				return 1;
			return 2;
		}
		return 0;
	}
	@Override
	public char getSymbol() {
		if (color == GamePanel.WHITE) {
			return 'R';
		} else {
			return 'r';
		}
	}
}
