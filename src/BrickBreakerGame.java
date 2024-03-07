import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class BrickBreakerGame extends JPanel implements ActionListener {
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final int PADDLE_WIDTH = 100;
    private final int PADDLE_HEIGHT = 20;
    private final int BALL_DIAMETER = 20;
    private final int BRICK_ROWS = 5;
    private final int BRICK_COLUMNS = 10;
    private final int BRICK_WIDTH = 75;
    private final int BRICK_HEIGHT = 30;
    private final int BRICK_GAP = 10;
    private final int PADDLE_SPEED = 10;
    private final int BALL_SPEED = 3;

    private Timer timer;
    private boolean gameOver;
    private boolean gameWon;
    private Paddle paddle;
    private Ball ball;
    private Brick[][] bricks;

    public BrickBreakerGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        paddle = new Paddle(WIDTH / 2 - PADDLE_WIDTH / 2, HEIGHT - PADDLE_HEIGHT - 20, PADDLE_WIDTH, PADDLE_HEIGHT);
        ball = new Ball(WIDTH / 2 - BALL_DIAMETER / 2, HEIGHT / 2 - BALL_DIAMETER / 2, BALL_DIAMETER, BALL_SPEED, BALL_SPEED);
        bricks = new Brick[BRICK_ROWS][BRICK_COLUMNS];
        for (int i = 0; i < BRICK_ROWS; i++) {
            for (int j = 0; j < BRICK_COLUMNS; j++) {
                bricks[i][j] = new Brick(j * (BRICK_WIDTH + BRICK_GAP), i * (BRICK_HEIGHT + BRICK_GAP), BRICK_WIDTH, BRICK_HEIGHT);
            }
        }

        timer = new Timer(1000 / 60, this);
        timer.start();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT) {
                    paddle.moveLeft(PADDLE_SPEED);
                } else if (key == KeyEvent.VK_RIGHT) {
                    paddle.moveRight(PADDLE_SPEED);
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver && !gameWon) {
            ball.move();
            checkCollisions();
            checkGameStatus();
        }
        repaint();
    }

    private void checkCollisions() {
        // Ball-Paddle collision
        if (ball.getBounds().intersects(paddle.getBounds())) {
            ball.reverseYDirection();
        }

        // Ball-Brick collisions
        for (int i = 0; i < BRICK_ROWS; i++) {
            for (int j = 0; j < BRICK_COLUMNS; j++) {
                if (bricks[i][j] != null && ball.getBounds().intersects(bricks[i][j].getBounds())) {
                    bricks[i][j] = null;
                    ball.reverseYDirection();
                }
            }
        }

        // Ball-Wall collisions
        if (ball.getX() <= 0 || ball.getX() >= WIDTH - BALL_DIAMETER) {
            ball.reverseXDirection();
        }
        if (ball.getY() <= 0) {
            ball.reverseYDirection();
        }

        // Ball falls below paddle
        if (ball.getY() >= HEIGHT - BALL_DIAMETER) {
            gameOver = true;
        }
    }

    private void checkGameStatus() {
        boolean allBricksDestroyed = true;
        for (int i = 0; i < BRICK_ROWS; i++) {
            for (int j = 0; j < BRICK_COLUMNS; j++) {
                if (bricks[i][j] != null) {
                    allBricksDestroyed = false;
                    break;
                }
            }
        }
        if (allBricksDestroyed) {
            gameWon = true;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!gameOver && !gameWon) {
            paddle.draw(g);
            ball.draw(g);
            for (int i = 0; i < BRICK_ROWS; i++) {
                for (int j = 0; j < BRICK_COLUMNS; j++) {
                    if (bricks[i][j] != null) {
                        bricks[i][j].draw(g);
                    }
                }
            }
        } else {
            String message = gameOver ? "Game Over!" : "You Win!";
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString(message, WIDTH / 2 - g.getFontMetrics().stringWidth(message) / 2, HEIGHT / 2);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Brick Breaker Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new BrickBreakerGame(), BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class Paddle {
    private int x, y, width, height;

    public Paddle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void moveLeft(int speed) {
        x -= speed;
    }

    public void moveRight(int speed) {
        x += speed;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, width, height);
    }
}

class Ball {
    private int x, y, diameter, dx, dy;

    public Ball(int x, int y, int diameter, int dx, int dy) {
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        this.dx = dx;
        this.dy = dy;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, diameter, diameter);
    }

    public void move() {
        x += dx;
        y += dy;
    }

    public void reverseXDirection() {
        dx = -dx;
    }

    public void reverseYDirection() {
        dy = -dy;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillOval(x, y, diameter, diameter);
    }
}

class Brick {
    private int x, y, width, height;

    public Brick(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, width, height);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, height);
    }
}
