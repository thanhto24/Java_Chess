package main;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JPanel;
import piece.Piece;
import stockfish.*;

public class GamePanel extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 900, HEIGHT = 720, WHITE = 0, BLACK = 1;
	private static final int FPS = 60;
	private boolean gameOver = false;
	private Thread gameThread;
	private Board board = new Board();
	private Mouse mouse = new Mouse();
	public static Piece selectedPiece = null;
	private int currentPlayer = WHITE;
	public static boolean promotion = false;
	public static ArrayList<Piece> pieces = new ArrayList<>(), states = new ArrayList<>();
	private final String[] promotionOptions = {"Queen", "Rook", "Bishop", "Knight"};
	private String fen = "";
	private String bestMove = "";
	private Stockfish stockfish_module;
	
	
	public GamePanel() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.BLACK);
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		initPieces();
		try {
            stockfish_module = new Stockfish("res/stockfish/stockfish-windows-x86-64-avx2.exe"); // Khởi tạo Stockfish trong phương thức khởi tạo
        } catch (IOException e) {
        	 e.printStackTrace(); // Xử lý lỗi khi không thể khởi tạo Stockfish
        }
		fen = stockfish.Fen_Gen.getFen(create_board(), (currentPlayer == WHITE) ? BLACK : WHITE, "");
//		System.out.println(fen);
		try {
			bestMove = stockfish_module.getBestMove(fen);
			System.out.println(bestMove);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initPieces() {
		for (int i = 0; i < 8; i++) {
//			pieces.add(new piece.Pawn(i, 1, BLACK));
//			pieces.add(new piece.Pawn(i, 6, WHITE));
		}
		pieces.add(new piece.Rook(0, 0, BLACK));
		pieces.add(new piece.Rook(7, 0, BLACK));
		pieces.add(new piece.Rook(0, 7, WHITE));
		pieces.add(new piece.Rook(7, 7, WHITE));
		pieces.add(new piece.Knight(1, 0, BLACK));
		pieces.add(new piece.Knight(6, 0, BLACK));
		pieces.add(new piece.Knight(1, 7, WHITE));
		pieces.add(new piece.Knight(6, 7, WHITE));
		pieces.add(new piece.Bishop(2, 0, BLACK));
		pieces.add(new piece.Bishop(5, 0, BLACK));
		pieces.add(new piece.Bishop(2, 7, WHITE));
		pieces.add(new piece.Bishop(5, 7, WHITE));
		pieces.add(new piece.Queen(3, 0, BLACK));
		pieces.add(new piece.Queen(3, 7, WHITE));
		pieces.add(new piece.King(4, 0, BLACK));
		pieces.add(new piece.King(4, 7, WHITE));
		
		pieces.add(new piece.Pawn(1, 1, WHITE));
		pieces.add(new piece.Pawn(1, 6, BLACK));
		pieces.add(new piece.Pawn(2, 6, BLACK));

	}

	public void start() {
		gameThread = new Thread(this);
		gameThread.start();
	}

	@Override
	public void run() {
		double drawInterval = 1000000000 / FPS, delta = 0;
		long now, lastTime = System.nanoTime();
		while (gameThread != null) {
			if (gameOver) 
                break;
			now = System.nanoTime();
			delta += (now - lastTime) / drawInterval;
			lastTime = now;
			if (delta >= 1) {
				update();
				repaint();
				delta--;
			}
		}
	}

	public static Piece getPiece(int col, int row) {
		for (Piece p : pieces) {
			if (p.col == col && p.row == row && p != selectedPiece) {
				return p;
			}
		}
		return null;
	}

	private void update() {
		if (promotion) {
			handlePromotion();
		} else if (mouse.pressed) {
			if (selectedPiece == null) {
				selectPiece();
			} else {
				simulateMove();
			}
		} else if (selectedPiece != null) {
			processMove();
		}
	}

	private void handlePromotion() {
		Piece target = selectPromotion(selectedPiece.getCol(), selectedPiece.getRow(), selectedPiece.color);
		if (target != null) {
			applyPromotion(target);
		}
	}

	private void applyPromotion(Piece target) {
		target.update_by_row_col();
		states.add(target.clone());
		pieces.add(target);
		pieces.remove(selectedPiece);
		selectedPiece = null;
		promotion = false;
		if (bestMove != null) {
			auto_move(bestMove);
		}
	}

	private void selectPiece() {
		for (Piece p : pieces) {
			if (p.color == currentPlayer && p.col == mouse.x / Board.SQUARE_SIZE && p.row == mouse.y / Board.SQUARE_SIZE) {
				selectedPiece = p;
				break;
			}
		}
	}

	private void simulateMove() {
		selectedPiece.x = mouse.x - Board.HALF_SQUARE_SIZE;
		selectedPiece.y = mouse.y - Board.HALF_SQUARE_SIZE;
		selectedPiece.col = selectedPiece.getCol();
		selectedPiece.row = selectedPiece.getRow();
	}

	private void processMove() {
		int moveType = selectedPiece.checkMove(selectedPiece.getCol(), selectedPiece.getRow());
//		System.out.println(moveType);
		if (moveType > 0) {
			handleValidMove(moveType);
			currentPlayer = (currentPlayer == WHITE) ? BLACK : WHITE;
		} else {
			selectedPiece.resetPosition();
		}
		if (!promotion) {
			selectedPiece = null;
		}
	}

	private void handleValidMove(int moveType) {
		Piece target = null;
		if (moveType == 2 || moveType == 3) {
			target = getPieceForCapture(moveType);
			if (target instanceof piece.King) {
				System.out.println("Game Over");
				gameOver = true;
			}
			Fen_Gen.halfmoveClock = 0;
		} 
		else 
		{
			if (selectedPiece instanceof piece.Pawn) {
				Fen_Gen.halfmoveClock = 0;
			} else {
				Fen_Gen.halfmoveClock++;
			}
			if (moveType == 9 || moveType == 10) {
				promotion = true;
				handlePawnPromotion(moveType);
			} else if (moveType == 5 || moveType == 7) {
				performCastling(0, 3);
			} else if (moveType == 6 || moveType == 8) {
				performCastling(7, 5);
			}
		}
		updateGameState(target);
	}

	private Piece getPieceForCapture(int moveType) {
		Piece target = getPiece(selectedPiece.getCol(), selectedPiece.getRow());
		if (moveType == 3) {
			target = getPiece(selectedPiece.getCol(), selectedPiece.getRow() + (currentPlayer == WHITE ? 1 : -1));
		}
		if (target != null) {
			target.col = -1;
			target.row = -1;
			states.add(target.clone());
			pieces.remove(target);
		}
		return target;
	}

	private void handlePawnPromotion(int moveType) {
		Piece target = (moveType == 10) ? getPiece(selectedPiece.getCol(), selectedPiece.getRow()) : null;
		if (target != null) {
			target.col = -1;
			target.row = -1;
			states.add(target.clone());
			pieces.remove(target);
			if (target instanceof piece.King) {
				System.out.println("Game Over");
				gameOver = true;
			}
		}
	}

	private void performCastling(int rookCol, int newRookCol) {
		Piece rook = getPiece(rookCol, selectedPiece.getRow());
		rook.col = newRookCol;
		rook.update_by_row_col();
		states.add(rook.clone());
	}
	
	private char[][] create_board()
	{
		char[][] board = new char[8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				board[i][j] = '.';
			}
		}
		for (Piece p : pieces) {
            if (p.col != -1 && p.row != -1) {
                board[p.row][p.col] = p.getSymbol();
            }
		}
		return board;
	}
	
	private void updateGameState(Piece target) {
		states.add(selectedPiece.clone());
		selectedPiece.update();

		fen = stockfish.Fen_Gen.getFen(create_board(), currentPlayer, "");
//		System.out.println(fen);
		try {
			bestMove = stockfish_module.getBestMove(fen);
			System.out.println(bestMove);
			if(!promotion)
			{
				auto_move(bestMove);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Piece selectPromotion(int col, int row, int color) {
		if (mouse.pressed) {
			promotion = false;
			int offsetY = 100;
			for (String option : promotionOptions) {
				if (mouse.x >= 800 && mouse.x <= 800 + Board.SQUARE_SIZE && mouse.y >= offsetY && mouse.y <= offsetY + Board.SQUARE_SIZE) {
					return createPromotionPiece(option, col, row, color);
				}
				offsetY += 100;
			}
		}
		return null;
	}

	private Piece createPromotionPiece(String option, int col, int row, int color) {
		switch (option) {
			case "Queen": return new piece.Queen(col, row, color);
			case "Rook": return new piece.Rook(col, row, color);
			case "Bishop": return new piece.Bishop(col, row, color);
			case "Knight": return new piece.Knight(col, row, color);
			default: return null;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		board.draw(g);
		for (Piece p : pieces) {
			p.draw(g);
		}
		highlightSelectedPiece(g);
		drawPromotionOptions(g);
	}

	private void highlightSelectedPiece(Graphics g) {
		if (selectedPiece != null) {
			g.setColor(new Color(0, 0, 255, 100));
			g.fillRect(selectedPiece.col * Board.SQUARE_SIZE, selectedPiece.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
			selectedPiece.draw(g);
		}
	}

	private void drawPromotionOptions(Graphics g) {
		if (promotion && !gameOver) {
			int offsetY = 100;
			for (String option : promotionOptions) {
				Piece p = createPromotionPiece(option, -1, -1, currentPlayer);
				if (p != null) {
					p.x = 800;
					p.y = offsetY;
					p.draw(g);
				}
				offsetY += 100;
			}
		}
	}
	
	private void auto_move(String bestmove)
	{
		if(bestmove == "")
			return;
		if (bestmove == "No move found") {
			gameOver = true;
			return;
		}
//		System.out.println("Auto Move");
		String[] moves = bestmove.split(" ");
        int selected_col = moves[0].charAt(0) - 'a';
        int selected_row = 8 - (moves[0].charAt(1) - '0');
        int target_col = moves[0].charAt(2) - 'a';
        int target_row = 8 - (moves[0].charAt(3) - '0');
        selectedPiece = null;
        selectedPiece = getPiece(selected_col, selected_row);
//        System.out.println(selectedPiece);
        selectedPiece.x = target_col * Board.SQUARE_SIZE;
        selectedPiece.y = target_row * Board.SQUARE_SIZE;
        selectedPiece.col = target_col;
        selectedPiece.row = target_row;
        selectedPiece.update();
        Piece target = getPiece(target_col, target_row);
		if (target != null) {
			target.col = -1;
			target.row = -1;
			states.add(target.clone());
			pieces.remove(target);
		}
		fen = stockfish.Fen_Gen.getFen(create_board(), (currentPlayer == WHITE) ? BLACK : WHITE, "");
//		System.out.println(fen);
		try {
			String bestMove = stockfish_module.getBestMove(fen);
			System.out.println(bestMove);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bestMove = "";
		if((moves[0]).length()==5)
		{
//			promotion = true;
			Piece promotion_piece = null;
			switch(moves[0].charAt(4))
            {
			case 'q':
				promotion_piece = new piece.Queen(-1, -1, BLACK);
				break;
            case 'r':
				promotion_piece = new piece.Rook(-1, -1, BLACK);
				break;
            case 'b':
            	promotion_piece = new piece.Bishop(-1, -1, BLACK);
            	break;
        	case 'n':
				promotion_piece = new piece.Knight(-1, -1, BLACK);
				break;
            }
			if(promotion_piece != null)
			{
				promotion_piece.col = target_col;
				promotion_piece.row = target_row;
				applyPromotion(promotion_piece);
			}
		}
//		System.out.println("Promotion");
		selectedPiece = null;
		currentPlayer = (currentPlayer == WHITE) ? BLACK : WHITE;

	}
}
