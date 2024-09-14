package stockfish;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import main.GamePanel;

public class Stockfish {
    private Process process;
    private BufferedReader input;
    private PrintWriter output;

    public Stockfish(String pathToStockfish) throws IOException {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(pathToStockfish);
            process = processBuilder.start();
            
            if (process == null) {
                throw new IllegalStateException("Process could not be started.");
            }
            
            input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            output = new PrintWriter(new OutputStreamWriter(process.getOutputStream()), true);
            
            if (input == null) {
                throw new IllegalStateException("BufferedReader could not be initialized.");
            }
            
            if (output == null) {
                throw new IllegalStateException("PrintWriter could not be initialized.");
            }
            
            System.out.println("Stockfish initialized successfully.");
        } catch (IOException e) {
            throw new IOException("Failed to initialize Stockfish", e);
        }
    }

    public void sendCommand(String command) {
        if (output != null) {
            output.println(command);
        } else {
            throw new IllegalStateException("Output stream is not initialized.");
        }
    }

    public String getBestMove(String fen) throws IOException {
        // Ensure Stockfish is ready
        sendCommand("ucinewgame");
        sendCommand("position fen " + fen);
        sendCommand("go depth 5"); // Adjust the depth as needed

        // Read the response
        String line;
        while ((line = input.readLine()) != null) {
            if (line.startsWith("bestmove")) {
                // Split the line and return only the move (second part)
            	if (line.equals("bestmove (none)")) {
            	    GamePanel.gameOver = true;
            	    return "No move found";
            	}
          
                String[] parts = line.split(" ");
                int col, row;
                col = parts[1].charAt(0) - 'a';
                row = 8 - (parts[1].charAt(1) - '0');
				if (GamePanel.getPiece(col, row) instanceof piece.King)
				{
	                if(parts[1].equals("e1h1"))
	                    return "e1g1";
	                if(parts[1].equals("e8h8"))
	                    return "e8g8";
					if (parts[1].equals("e1a1"))
						return "e1c1";
					if (parts[1].equals("e8a8"))
						return "e8c8";
				}
                return parts[1]; // bestmove move [ponder] - we only need the move
            }
        }
        GamePanel.gameOver = true;
        return "No move found";
    }
    
    public String analyzeStatus(String fen) throws IOException {
        sendCommand("position fen " + fen);
        sendCommand("go depth 1");

        String line;
        while ((line = input.readLine()) != null) {
            if (line.startsWith("info")) {
                if (line.contains("mate")) {
                    return "Checkmate";
                }
                if (line.contains("draw")) {
                    return "Draw";
                }
            }
        }
        return "In Progress";
    }
    
    public void close() {
        if (process != null) {
            process.destroy();
        }
    }
}
