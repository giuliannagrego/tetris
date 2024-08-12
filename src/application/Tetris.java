package src.application;

import src.environment.Board;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class Tetris extends JFrame {

    JLabel status;

    public Tetris () {

        status = new JLabel("Pontuação 0");
        add(status,BorderLayout.SOUTH);

        Board game = new Board(this);
        game.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(game);

        game.start();


        setSize(250, 400);
        setTitle("TETRIS - TUTORIAL");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public JLabel getStatus() {
        return status;
    }

    public static void main(String[] args){

        Tetris game = new Tetris();

        game.setLocationRelativeTo(null);
        game.setVisible(true);
    }
}
