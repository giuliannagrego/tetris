package src.environment;
import src.application.Tetris;
import src.design.Drawing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;


public class Board extends JPanel implements ActionListener {

    // Interface size
    // For a 250 x 400 screen, we use 5% of the values

    final int BoardWidth = 12;
    final int BoardHeight = 20;

    // Sets the time for the game
    Timer time;

    // Game states
    boolean isFallingOver = false;
    boolean isStarted = false;
    boolean isPaused = false;

    // Defines how many lines were removed
    int RemovedLines = 0;

    // Sets the coordinates for the current part
    int currentX = 0;
    int currentY = 0;

    // Sets the game status
    JLabel status;

    // Defines the design of the current part
    Drawing currentPiece;

    // Defines the types of frames to be drawn
    Drawing.TetrisShape[] board;

    // Class constructor method
    // receives JFrame as main parameter
    public Board(Tetris father) {

        // Sets the focus to this JPanel
        setFocusable(true);
        // Instantiates a new drawing for the current part
        currentPiece = new Drawing();

        // Sets a timer with speed 400 to this JPanel
        time = new Timer(400, this);
        time.start();

        // Get game status with parameter JFrame
        status = father.getStatus();

        // Sets how many pieces there are in environment
        board = new Drawing.TetrisShape[BoardWidth * BoardHeight];

        // Create a new listener
        addKeyListener(new TAdapter());

        // Clean JPanel
        clearBoard();
    }

    // Defines the pattern of the piece falling in the game
    @Override
    public void actionPerformed (ActionEvent e) {

        // If it just fell, crate a new drawing
        // If not, the piece moves down one line
        if (isFallingOver) {
            isFallingOver = false;
            newPiece();
        } else {
            dropALine();
        }
    }

    // Defines the size of each square in width x height
    int squareWidth() {
        return (int) getSize().getWidth()/BoardWidth;
    }
    int squareHeigth() {
        return (int) getSize().getHeight()/BoardHeight;
    }

    // Defines in which position (x, y) the piece will be drawn
    Drawing.TetrisShape drawingIn(int x, int y) {
        return board[(y * BoardWidth) + x];
    }

    // Game events begin
    public void start() {
        // If the game is paused, it prevented from starting
        if (isPaused) {
            return;
        }

        // Defines that the game has started
        // That the piece has not finished falling
        // And that there is no line removed
        isStarted = true;
        isFallingOver = false;
        RemovedLines = 0;

        // Clean JPanel
        clearBoard();

        // Creates a new piece
        newPiece();

        // Game runtime begins
        time.start();
    }

    // Pause game events
    public void pause() {

        // If the game is paused, it does not continue
        if(!isStarted){
            return;
        }

        // Reverse the pause
        isPaused = !isPaused;

        // If it continues to be paused, it stops the time and announces the pause
        // If not, the timer continues
        if(isPaused) {
            time.stop();
            status.setText("Jogo pausado!");
        } else {
            time.start();
            status.setText("Pontuação: " + String.valueOf(RemovedLines));
        }

        // Redesign the screen
        repaint();
    }

    // Draw the screen
    @Override
    public void paint (Graphics g) {

        // Defines the method as Graphics
        super.paint(g);

        // Get Jpanel size
        Dimension size = getSize();

        // Sets the top of Jpanel
        int squareTop = (int) size.getHeight() - BoardHeight * squareHeigth();

        // Creates a Tetris square at the top of the Jpanel
        for (int h = 0; h < BoardHeight; h++) {

            for (int w = 0; w < BoardWidth; w++) {

                // Tells where it will be drawn in (w, h)
                Drawing.TetrisShape drawing = drawingIn(w, BoardHeight - h - 1);

                // If there is a drawing, draw the Tetris square on the screen
                if (drawing != Drawing.TetrisShape.NoPiece) {
                    drawingSquare(g, 0 + w * squareWidth(), squareTop + h * squareHeigth(), drawing);
                }
            }
        }

        // If a drawing exists on the current part
        if(currentPiece.getDrawing() != Drawing.TetrisShape.NoPiece){

            // For each frame defines the position in (x, y) and draws on the screen
            for (int i = 0; i < 4; i++){

                int x = currentX + currentPiece.x(i);
                int y = currentY + currentPiece.y(i);

                drawingSquare(g, 0 + x * squareWidth(), squareTop + (BoardHeight - y - 1) * squareHeigth(), currentPiece.getDrawing());

            }
        }
    }

    // Skip the entire screen
    private void jumpDown(){

        // Sets a new Y as the Y of the current piece
        int newY = currentY;

        // As long as the new Y is greater than 0
        while (newY > 0){

            // If he cannot move the piece down, then stop
            if (!tryMove(currentPiece, currentX, newY - 1)){
                break;
            }

            // Decrement the new Y
            --newY;
        }

        // Cut the piece
        cutPiece();
    }

    // Move down one line in Jpanel
    private void dropALine() {

        // For each pixel on the screen, defines no drawing
        if(!tryMove(currentPiece, currentX, currentY - 1)) {
            cutPiece();
        }
    }

    private void clearBoard() {
        for (int i = 0; i < BoardHeight * BoardWidth; i++) {
            board[i] = Drawing.TetrisShape.NoPiece;
        }
    }

    private void cutPiece() {

        for (int i = 0; i < 4; i++) {

            int x = currentX + currentPiece.x(i);
            int y = currentY + currentPiece.y(i);

            board[(y * BoardWidth) + x] = currentPiece.getDrawing();
        }

        removeFullLine();

        if (!isFallingOver) {
            newPiece();
        }
    }

    private void newPiece() {

        currentPiece.setRandomDesign();

        currentX = BoardWidth / 2 + 1;
        currentY = BoardHeight - 1 + currentPiece.minY();

        if (!tryMove(currentPiece, currentX, currentY)){

            currentPiece.setDraw(Drawing.TetrisShape.NoPiece);

            time.stop();
            isStarted = false;

            status.setText(("FIM DE JOGO!"));
        }
    }

    private boolean tryMove(Drawing newPiece, int newX, int newY) {

        for ( int i = 0; i < 4; i++){

            int x = newX + newPiece.x(i);
            int y = newY + newPiece.y(i);

            if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight) {
                return  false;
            }
            if (drawingIn(x, y) != Drawing.TetrisShape.NoPiece) {
                return false;
            }
        }

        currentPiece = newPiece;
        currentX = newX;
        currentY = newY;

        repaint();

        return true;
    }

    private void removeFullLine() {

        int FullLines = 0;

        for (int i = BoardHeight - 1; i >= 0; i--){

            boolean lineIsFull = true;

            for (int j = 0; j < BoardHeight; j++) {

                if (drawingIn(j, i) == Drawing.TetrisShape.NoPiece) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {

                ++FullLines;

                for (int k = i; k < BoardHeight - 1; k++) {
                    for (int j = 0; j < BoardWidth; j++) {
                        board[(k * BoardWidth) + j] = drawingIn(j, k + 1);
                    }
                }
            }
        }

        if (FullLines > 0) {

            RemovedLines += FullLines;

            status.setText(("Pontuação" + String.valueOf(RemovedLines)));

            isFallingOver = true;
            currentPiece.setDraw(Drawing.TetrisShape.NoPiece);

            repaint();
        }
    }

    private void drawingSquare(Graphics g, int x, int y, Drawing.TetrisShape drawing) {

        Color colors[] = {
                new Color(0, 0, 0), new Color(204, 102, 102),
                new Color(102, 204, 102), new Color(102, 102, 201),
                new Color(204, 204, 102), new Color(204, 102, 204),
                new Color(102 ,204, 204), new Color(218, 170, 0)
        };

        Color color = colors[drawing.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeigth() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeigth() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeigth() - 1, x + squareWidth() - 1, squareHeigth() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeigth() - 1, x + squareWidth() - 1, y + 1);

    }

    public class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed (KeyEvent e){

            if ((!isStarted || currentPiece.getDrawing() == Drawing.TetrisShape.NoPiece)) {
                return;
            }

            int keycode = e.getKeyCode();

            if (keycode == 'p' || keycode == 'P') {
                pause();
                return;
            }

            if (isPaused) {
                return;
            }

            switch (keycode){
                case KeyEvent.VK_LEFT:
                    tryMove(currentPiece, currentX - 1, currentY);
                    break;
                case KeyEvent.VK_RIGHT:
                    tryMove(currentPiece, currentX + 1, currentY);
                    break;
                case KeyEvent.VK_DOWN:
                    dropALine();
                    break;
                case KeyEvent.VK_C:
                    tryMove(currentPiece.rotateRight(), currentX, currentY);
                    break;
                case KeyEvent.VK_Z:
                    tryMove(currentPiece.rotateLeft(), currentX, currentY);
                    break;
                case KeyEvent.VK_SPACE:
                    jumpDown();
                    break;
            }
        }
    }
}
