package stockfish;

public class Fen_Gen {
	public static int halfmoveClock = 0;
	public static int fullmoveNumber = 1;
	public static boolean whiteCanCastleKingside = true;
	public static boolean whiteCanCastleQueenside = true;
	public static boolean blackCanCastleKingside = true;
	public static boolean blackCanCastleQueenside = true;
	public static String getFen(char[][] board, int turn, String en_passant) {
		fullmoveNumber = (turn == 0) ? fullmoveNumber : fullmoveNumber + 1;
		String fen = "";
		for (int i = 0; i < 8; i++) {
			int empty = 0;
			for (int j = 0; j < 8; j++) {
				if (board[i][j] == '.') {
					empty++;
				} else {
					if (empty > 0) {
						fen += empty;
						empty = 0;
					}
					fen += board[i][j];
				}
			}
			if (empty > 0) {
				fen += empty;
			}
			if (i < 7) {
				fen += "/";
			}
		}
		fen += " ";
		fen += (turn == 0) ? "b" : "w";
		fen += " ";
		fen += (whiteCanCastleKingside ? "K" : "") + (whiteCanCastleQueenside ? "Q" : "") + (blackCanCastleKingside ? "k" : "") + (blackCanCastleQueenside ? "q" : "");
		fen += " ";
		fen += (en_passant != "") ? en_passant : "-";
		fen += " ";
		fen += halfmoveClock;
		fen += " ";
		fen += fullmoveNumber;
		return fen;
	}
}
