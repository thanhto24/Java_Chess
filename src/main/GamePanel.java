package main;

import java.awt.Dimension;
import java.util.ArrayList;
import java.awt.Color;

import javax.swing.JPanel;

import piece.Piece;

public class GamePanel extends JPanel implements Runnable{
	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 900;
	public static final int HEIGHT = 720;
	final int FPS = 60;
	Thread gameThread;
	Board board = new Board();
	Mouse mouse = new Mouse();
	public static Piece selectedPiece = null;
	
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	int currentPlayer = WHITE;
	
	public static ArrayList<Piece> pieces = new ArrayList<Piece>();
	public static ArrayList<Piece> states = new ArrayList<Piece>();
//	public static ArrayList<Piece> simPieces = new ArrayList<Piece>();
	
	
	public GamePanel() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.BLACK);
		
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		init_Pieces();
	}
	
	public void start() {
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	public void init_Pieces() {
		for (int i = 0; i < 8; i++) {
			pieces.add(new piece.Pawn(i, 1, BLACK));
			pieces.add(new piece.Pawn(i, 6, WHITE));
		}

		pieces.add(new piece.Rook(0, 0, BLACK));
		pieces.add(new piece.Rook(7, 0, BLACK));
		pieces.add(new piece.Rook(0, 7, WHITE));
		pieces.add(new piece.Rook(7, 7, WHITE));

//		pieces.add(new piece.Knight(1, 0, BLACK));
//		pieces.add(new piece.Knight(6, 0, BLACK));
//		pieces.add(new piece.Knight(1, 7, WHITE));
//		pieces.add(new piece.Knight(6, 7, WHITE));
//
//		pieces.add(new piece.Bishop(2, 0, BLACK));
//		pieces.add(new piece.Bishop(5, 0, BLACK));
//		pieces.add(new piece.Bishop(2, 7, WHITE));
//		pieces.add(new piece.Bishop(5, 7, WHITE));
//
//		pieces.add(new piece.Queen(3, 0, BLACK));
//		pieces.add(new piece.Queen(3, 7, WHITE));

		pieces.add(new piece.King(4, 0, BLACK));
		pieces.add(new piece.King(4, 7, WHITE));
	}
	
	@Override
	public void run() {
		//Game Loop
		double drawInterval = 1000000000 / FPS;
		double delta = 0;
		long now;
		long lastTime = System.nanoTime();
		
		while (gameThread != null) {
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

	public void update() {
		if (mouse.pressed) {
			if (selectedPiece == null) {
				for (Piece p : pieces) {
					if (p.color == currentPlayer && p.col == mouse.x / Board.SQUARE_SIZE && p.row == mouse.y / Board.SQUARE_SIZE) {
						selectedPiece = p;
						break;
					}
				}
			} else {
				simulate();
			}
		}     
		else {
            if (selectedPiece != null) {
            	int checkMove = selectedPiece.checkMove(selectedPiece.getCol(), selectedPiece.getRow());
				if (checkMove > 0) {
					Piece target = null;
					System.out.println("Moved " + checkMove);
					if (checkMove == 2) {
						target = getPiece(selectedPiece.getCol(), selectedPiece.getRow());
						target.col = -1;
						target.row = -1;
						states.add(target.clone());
						pieces.remove(target);
					}
					if (checkMove == 3) {
						target = getPiece(selectedPiece.getCol(), selectedPiece.getRow() + (currentPlayer == WHITE ? 1: -1));
						System.out.println(selectedPiece.getCol() + " " + (selectedPiece.getRow() + (currentPlayer == WHITE ? 1: -1)));
						target.col = -1;
						target.row = -1;
						states.add(target.clone());
						pieces.remove(target);
					}
					if (checkMove == 5 || checkMove == 7) {
					    target = getPiece(0, selectedPiece.getRow());
					    target.col = 3;
						target.row = selectedPiece.getRow();
						target.update_by_row_col();
						states.add(target.clone());
					} else if (checkMove == 6 || checkMove == 8) {
					    target = getPiece(7, selectedPiece.getRow());
					    target.col = 5;
						target.row = selectedPiece.getRow();
						target.update_by_row_col();
						states.add(target.clone());
					}

					states.add(selectedPiece.clone());
					for (Piece p : states) {
						System.out.println(p + " " + p.col + " " + p.row + " " + p.prevCol + " " + p.prevRow);
					}
					if (target != null) {
						System.out.println("Arr piece: ");
						for (Piece p : pieces) {
							System.out.println(p + " " + p.col + " " + p.row + " " + p.prevCol + " " + p.prevRow + " " + p.x + " " + p.y);
						}
					}
					selectedPiece.update();
					currentPlayer = (currentPlayer == WHITE) ? BLACK : WHITE;

				} else {
					selectedPiece.resetPosition();
					System.out.println("Can't move " + checkMove);
				}
				selectedPiece = null;
			}
        }
	}
	
	private void simulate() {
		selectedPiece.x = mouse.x - Board.HALF_SQUARE_SIZE;
		selectedPiece.y = mouse.y - Board.HALF_SQUARE_SIZE;
		selectedPiece.col = selectedPiece.getCol();
		selectedPiece.row = selectedPiece.getRow();
	}
	
	public void paintComponent(java.awt.Graphics g) {
		super.paintComponent(g);
		board.draw(g);
		
		for (Piece p : pieces) {
			p.draw(g);
		}
		
		if (selectedPiece != null) {
			g.setColor(new Color(0, 0, 255, 100));
			g.fillRect(selectedPiece.col * Board.SQUARE_SIZE, selectedPiece.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
			selectedPiece.draw(g);
		}
		
	}
}
