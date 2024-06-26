package org.snakeIt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;


public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600; // In pixels.
    static final int SCREEN_HEIGHT = 600; // In Pixels
    static final int UNIT_SIZE = 25; // In Pixels.
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT / UNIT_SIZE);
    static int GAME_SPEED = 85; // THe lower the number, the faster the game, and vice versa.

    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6; // The body parts on the snake.
    int applesEaten = 0;
    int appleX; // The X co-ordinate of where the apple is placed. will appear randomly each time snake eats apple.
    int appleY; // Ditto.
    char direction = 'R';
    boolean snakeRunning = false;
    Timer timer;
    Random random;
    private final Audio audio;
    private int lives = 3;

    GamePanel() {
        audio = new Audio();
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        chooseSpeed();
    }
    public void chooseSpeed() {
        String[] options = {"Hard", "Medium", "Easy"};
        int choice = JOptionPane.showOptionDialog(null, "Choose game speed:", "Speed Selection", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

        switch (choice) {
            case 0: // Hard
                GAME_SPEED = 75;
                break;
            case 1: // Medium
                GAME_SPEED = 100;
                break;
            case 2: // East
                GAME_SPEED = 150;
                break;
            default:
                // Default to medium speed
                GAME_SPEED = 100;
                break;
        }

        startGame();
    }
    public void startGame() {
        newApple();
        snakeRunning = true;
        bodyParts = 6; // Reset body parts
        applesEaten = 0; // Reset score
        direction = 'R'; // Reset direction
        timer = new Timer(GAME_SPEED, this);
        timer.start();
        // Reset snake position
        x[0] = SCREEN_WIDTH / 2; // Start at the center of the screen
        y[0] = SCREEN_HEIGHT / 2;
        for (int i = 1; i < bodyParts; i++) {
            x[i] = x[0] - i * UNIT_SIZE;
            y[i] = y[0];
        }
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    // *** GRID (Visual depiction of the grid. Can make user commnnds to turn on or off ***


    public void draw(Graphics g) {
        if (snakeRunning) {
            g.setColor(Color.darkGray);
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);

                } else {
                    g.setColor(new Color(45, 180, 0)); //RGB Colours
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            // SCORE BOARD
            g.setColor(Color.red);
            g.setFont(new Font("Impact",Font.BOLD,40));
            FontMetrics fontMetrics = getFontMetrics(g.getFont());
            g.drawString("Score: "+ applesEaten, (SCREEN_WIDTH - fontMetrics.stringWidth("Score: "+ applesEaten)) / 2, g.getFont().getSize());

        } else {
            getGraphics();
        }

    }

    public void newApple() {
        appleX = random.nextInt((int) SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE; // we're going to have the apple appear somewhere along the x access.
        appleY = random.nextInt((int) SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE; // we're going to have the apple appear somewhere along the y access.


    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        // Update the head position based on the direction
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }

    }

    public void checkAPple() {
        if ((x[0] == appleX) && (y[0] == appleY)) { // x[0]  and y[0] are the x and y positions of the HEAD of the snake.
            applesEaten++;
            newApple();
            bodyParts++;
           audio.audioEatApple();


        }

    }

    public void checkCollisions() {

        // This checks if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) { // x[0]  and y[0] are the x and y positions of the HEAD of the snake.
                snakeRunning = false;
            }
        }
        // This checks if head touches LEFT border
        if (x[0] < 0) {
            snakeRunning = false; // Basically, if it touches itself, game over.
        }
        // This checks if head touches RIGHT border
        if (x[0] > SCREEN_WIDTH) {
            snakeRunning = false;
        }

        // This checks if head touches TOP border
        if (y[0] < 0) {
            snakeRunning = false;
        }
        // This checks if head touches BOTTOM border
        if (y[0] > SCREEN_HEIGHT) {
            snakeRunning = false;
        }
        if (!snakeRunning) {
            timer.stop();
        }
        // Checking collisions...
        if (!snakeRunning) {
            lives--;
            if (lives > 0) {
                startGame();
            } else {
                gameOver();
            }
        }
    }

    public void gameOver() {
        // Game Over Text
        Graphics g = getGraphics();
        g.setColor(Color.green);
        g.setFont(new Font("Impact", Font.BOLD, 40));
        FontMetrics fontMetrics1 = getFontMetrics(g.getFont());
        g.drawString("Final Score: " + applesEaten, (SCREEN_WIDTH - fontMetrics1.stringWidth("Final Score: " + applesEaten)) / 2, g.getFont().getSize());

        g.setColor(Color.red);
        g.setFont(new Font("Impact", Font.BOLD, 75));
        FontMetrics fontMetrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - fontMetrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

        g.setColor(Color.green);
        g.setFont(new Font("Impact", Font.BOLD, 20));
        FontMetrics fontMetrics3 = getFontMetrics(g.getFont());
//        g.drawString("Play Again? Y/N: ", (SCREEN_WIDTH - fontMetrics3.stringWidth("Play Again? Y/N: ")) / 2, 420);
        int choice = JOptionPane.showConfirmDialog(null, "Do you want to play again?", "Game Over", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            chooseSpeed();
        } else {
            // Perform actions for No option, like exit or display farewell message
            System.exit(0); // For example, exiting the game
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (snakeRunning) {
            move();
            checkAPple();
            checkCollisions();

        }
        repaint();

    }

    public class MyKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
            break;

            case KeyEvent.VK_RIGHT:
            if (direction != 'L') {
                direction = 'R';
            }
            break;

            case KeyEvent.VK_UP:
                if(direction !='D') {

            direction = 'U';
        }   break;

            case KeyEvent.VK_DOWN:
            if(direction !='U')

    {
        direction = 'D';
    }
                break;


}
}}}




