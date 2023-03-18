import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 900;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 75;

    int PADDLE_HEIGHT = 5;

    final int[] leftBarY = new int[GAME_UNITS]; // Left paddle

    final int[] rightBarY = new int[GAME_UNITS]; // Right paddle

    boolean running = false;

    Random random;
    Timer timer;

    char direction = 'D'; // Starting Position to move paddles to center

    static final int BALL_SIZE = 15;
    int ballX;
    int ballY;
    int ballSpeed;
    char ballDirection = 'R';
    int ballSign = BALL_SIZE/6;

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        newBall();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();

        // Start with paddles in the middle of the screen
        for (int i = 0; i < (SCREEN_HEIGHT / UNIT_SIZE) / 2; i++) {
            move(leftBarY);
            move(rightBarY);
        }
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
            g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);

        }
    }

    public void newBall() {
        ballX = SCREEN_WIDTH / 2 - BALL_SIZE / 2;
        ballY = random.nextInt(SCREEN_HEIGHT);
    }

    public void move(int[] paddle) {

        switch (direction) {
            case 'U':
                for (int i = 0; i < PADDLE_HEIGHT; i++) {
                    paddle[i] = paddle[i + 1];
                }
                paddle[PADDLE_HEIGHT] = keepInRange(paddle[PADDLE_HEIGHT] - UNIT_SIZE);
                break;
            case 'D':
                for (int i = PADDLE_HEIGHT; i > 0; i--) {
                    paddle[i] = paddle[i - 1];
                }
                paddle[0] = keepInRange(paddle[0] + UNIT_SIZE);
                break;
        }

    }

    public int keepInRange(int y) {
        if (y >= SCREEN_HEIGHT - UNIT_SIZE)
            return SCREEN_HEIGHT - UNIT_SIZE;
        else if (y <= 0)
            return 0;
        else
            return y;
    }

    public void moveBall() {
        switch (ballDirection) { 
            case 'L':
                ballX -= UNIT_SIZE;
                ballY += ballSign;
                break;
            case 'R':
                ballX += UNIT_SIZE;
                ballY += ballSign;
                break;
        }
    }

    
    public void changeBallSign(){
        int unit = BALL_SIZE/6;
        int yDir = random.nextInt(2);
        if(yDir == 1){
            ballSign = -1*unit;
        }
    }

    public void checkCollision() {

        // Case 5 hits leftPaddle
        for (int i = 0; i < PADDLE_HEIGHT; i++) {
            if (ballX < 0) {
                ballDirection = 'R';
                changeBallSign();
            }
        }
        // Case 6 hits rightPaddle
        for (int i = 0; i < PADDLE_HEIGHT; i++) {
            if (ballX > SCREEN_WIDTH) {
                ballDirection = 'L';
                changeBallSign();
                
            }
        }
       

        // Case 1 hits left wall
        // Case 2 hits right wall

        // Case 3 hits ceiling
        // Case 4 hits floor

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            checkCollision();
            moveBall();

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
