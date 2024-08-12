package src.environment;
import src.application.Tetris;
import src.design.Drawing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        status = father.getStatus;

        // Sets how many pieces there are in environment
        board = new Drawing.TetrisShape[BoardWidth * BoardHeight];

        // Create a new listener
        addKeyListener(new TAdapter());

        // Clean JPanel
        clearBoard();
    }

    // Defines the pattern of the piece falling in the game
    public void actionPerformed (ActionEvent e) {

        // If it just fell, crate a new drawing
        // If not, the piece moves down one line
        if (isFallingOver) {
            isFallingOver = false;
            newPiece();
        } else {
            dropOneLine();
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
        int squareTop = (int) size.getHeight() = BoardHeight * squareHeigth();

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

    private void dropALine() {

        if(!tryMove(currentPiece, currentX, currentY - 1)) {
            cutPiece();
        }
    }







    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
