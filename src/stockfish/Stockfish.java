package stockfish;

import java.io.*;

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
                String[] parts = line.split(" ");
                return parts[1]; // bestmove move [ponder] - we only need the move
            }
        }
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
