import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GamePanel extends JPanel implements ActionListener {

    private static final int SCREEN_WIDTH = 900;
    private static final int SCREEN_HEIGHT = 600;
    private static final int UNIT_SIZE = 25;
    private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    private static final int DELAY = 75;
    private boolean running;

    /////////////////////////////////////// paddles/////////////////////////////////////////
    private final int leftBarY[] = new int[GAME_UNITS]; // Left paddle
    private final int rightBarY[] = new int[GAME_UNITS]; // Right paddle
    private static final int PADDLE_HEIGHT = 5;
    private char direction;
    /////////////////////////////////////// paddles/////////////////////////////////////////

    private Random random;
    private Timer timer;

    ////////////////////////////////// Ball//////////////////////////////////
    private static final int BALL_SIZE = 15;
    private int ballX;
    private int ballY;
    private char ballDirection;
    private int ballDiagnolDirection = 0;
    private int ballHorizontalDirection = UNIT_SIZE;
    ////////////////////////////////// Ball//////////////////////////////////

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();

        newBall();
        ballDirection = 'L'; // Start Ball direction
        ballDiagnolDirection = 0; // No inital diagnol
        ballHorizontalDirection = UNIT_SIZE; // Inital Speed

        resetPaddles(); // Move Paddles to center
    }

    public void resetPaddles() {
        // Set paddles to top of screen
        Arrays.fill(leftBarY, 0); // Set vals to 0
        Arrays.fill(rightBarY, 0);

        direction = 'D'; // Make sure they move down and not up

        // Start with paddles in the middle of the screen
        for (int i = 0; i < (int) ((SCREEN_HEIGHT / UNIT_SIZE) / 1.5); i++) {
            move(leftBarY);
            move(rightBarY);
        }
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            g.drawLine(SCREEN_WIDTH / 2, 0, SCREEN_WIDTH / 2, SCREEN_HEIGHT); // Draw Midde Line

            /////////////////////////////// Paddle//////////////////////////////////
            g.setColor(Color.white);
            for (int i = 0; i < PADDLE_HEIGHT; i++) { /// Draw Left Paddle
                g.fillRect(0, leftBarY[i], UNIT_SIZE, UNIT_SIZE);
            }

            for (int i = 0; i < PADDLE_HEIGHT; i++) { // Draw right paddle
                g.fillRect(SCREEN_WIDTH - UNIT_SIZE, rightBarY[i], UNIT_SIZE, UNIT_SIZE);
            }

            ///////////////////////////// Ball//////////////////////////////////////
            g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE); /// Draw ball

        }
    }

    public void newBall() {
        ballHorizontalDirection = 0; // UNIT SIZE
        ballDiagnolDirection = 0;

        ballX = SCREEN_WIDTH / 2 - BALL_SIZE / 2; // Start in middle (and the ball in the middle)
        ballY = SCREEN_HEIGHT / 2; // Middle of screen
    }

    public void move(int[] paddle) {

        switch (direction) {
            case 'U':
                if (paddle[PADDLE_HEIGHT] > 0) { // If going up draw from top of the arrat downwards
                    for (int i = 0; i < PADDLE_HEIGHT; i++) {
                        paddle[i] = paddle[i + 1];
                    }
                }
                paddle[PADDLE_HEIGHT] = keepInRange(paddle[PADDLE_HEIGHT] - UNIT_SIZE); // Change last value
                break;
            case 'D':
                if (paddle[0] < SCREEN_HEIGHT - UNIT_SIZE) { // Unit size?
                    for (int i = PADDLE_HEIGHT; i > 0; i--) { // if going down draw from
                        paddle[i] = paddle[i - 1];
                    }
                }
                paddle[0] = keepInRange(paddle[0] + UNIT_SIZE); // Keep the paddle in range
                break;
        }

    }

    public int keepInRange(int y) {
        if (y > SCREEN_HEIGHT - UNIT_SIZE) // if y value is passing the bottom of the screen return the screen height
            return SCREEN_HEIGHT - UNIT_SIZE;
        else if (y < 0) // If y value is passing top of screen return top of screen
            return 0;
        else
            return y; // Otherwise its a valid y 
    }

    public void moveBall() {
        switch (ballDirection) {
            case 'L':
                ballX = keepBallXInRange(ballX - ballHorizontalDirection);
                ballY += ballDiagnolDirection;
                break;
            case 'R':
                ballX = keepBallXInRange(ballX + ballHorizontalDirection);
                ballY += ballDiagnolDirection;
                break;
        }
    }

    public int keepBallXInRange(int x) {
        if (x <= UNIT_SIZE)
            return 0; // Left wall
        // right wall not working
        else
            return x;
    }

    public void changeBallSign() {
        ballDiagnolDirection = 0;
        // Decide the angle of the bounce
        int angle = random.nextInt(6) + 1;
        angle = BALL_SIZE / angle;
        // Decide if its positive or negative
        if ((int) random.nextInt(2) + 1 == 1) {
            ballDiagnolDirection -= angle;
        } else {
            ballDiagnolDirection += angle;
        }

        // Change ball speed
        ballHorizontalDirection += 10;
    }

    public void checkCollision() {

        // Case 5 hits leftPaddle
        if (ballX <= UNIT_SIZE) {
            for (int i = 0; i <= PADDLE_HEIGHT; i++) {
                if (leftBarY[i] == ballY) {
                    ballDirection = 'R';
                    changeBallSign();
                }
            }
        }

        // Case 6 hits rightPaddle
        if (ballX >= SCREEN_WIDTH - UNIT_SIZE) {
            for (int i = 0; i <= PADDLE_HEIGHT; i++) {
                if (rightBarY[i] == ballY) {
                    ballDirection = 'L';
                    changeBallSign();
                }
            }

        }

        // Case 1 hits left wall
        if (ballX <= 0) {
            ballHorizontalDirection = 0;
            resetPaddles();
            timeSleep(2);
            newBall();
        }
        // Case 2 hits right wall
        if (ballX >= SCREEN_WIDTH) {
            ballHorizontalDirection = 0;
            resetPaddles();
            timeSleep(2);
            newBall();
        }

        // Case 3 hits ceiling
        if (ballY <= 0) {
            // Change diagnol sign
            ballDiagnolDirection = -ballDiagnolDirection;
        }
        // Case 4 hits floor
        if (ballY >= SCREEN_HEIGHT) {
            // Change diagnol sign
            ballDiagnolDirection = -ballDiagnolDirection;
        }
    }

    public void timeSleep(int sleep) {
        try {
            Thread.sleep(sleep * 1000);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            checkCollision();
            // moveBall();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case 'W':
                    direction = 'U';
                    move(leftBarY);
                    break;
                case 'S':
                    direction = 'D';
                    move(leftBarY);
                    break;
                case KeyEvent.VK_UP:
                    direction = 'U';
                    move(rightBarY);
                    break;
                case KeyEvent.VK_DOWN:
                    direction = 'D';
                    move(rightBarY);
                    break;
            }
            repaint();
        }
    }

}
