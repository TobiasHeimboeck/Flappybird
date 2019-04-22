package me.tobias.game;

import me.tobias.game.utils.GameUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Tobias Heimböck
 * @since 1.0
 */
public class FlappyBird implements ActionListener, MouseListener, KeyListener {

    public static FlappyBird game;

    private boolean gameOver, started;
    private Renderer renderer;
    private Rectangle bird;
    private int ticks, yMotion, score, highScore = 0, missingPoints;
    private List<Rectangle> columns;
    private Random random;
    private final int WIDTH = 1200, HEIGHT = 800;
    private Color randomColor;

    private FlappyBird() {
        final JFrame frame = new JFrame();

        renderer = new Renderer();
        random = new Random();
        randomColor = Renderer.colors.get(random.nextInt(Renderer.colors.size()));

        final Timer timer = new Timer(20, this);

        frame.add(renderer);
        frame.setTitle(GameUtilities.title);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.addMouseListener(this);
        frame.addKeyListener(this);
        frame.setResizable(false);
        frame.setVisible(true);

        bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);

        columns = new ArrayList<>();

        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true);

        timer.start();
    }

    private void addColumn(boolean start) {
        int space = 300;
        int width = 100;
        int height = 50 + random.nextInt(300);

        if (start) {

            columns.add(new Rectangle(WIDTH + width + columns.size() * 300,
                    HEIGHT - height - 120, width, height));
            columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * 300, 0, width, HEIGHT - height - space));

        } else {

            columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, HEIGHT - height - 120, width, height));
            columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space));

        }
    }

    private void paintColumn(Graphics g, Rectangle column) {
        g.setColor(randomColor);
        g.fillRect(column.x, column.y, column.width, column.height);
    }

    private void jump() {
        if (gameOver) {

            bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
            columns.clear();
            yMotion = 0;
            score = 0;

            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true);

            gameOver = false;
        }

        if (!started) {

            started = true;

        } else if (!gameOver) {

            if (yMotion > 0) {

                yMotion = 0;


            }

            yMotion -= 10;

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        int speed = 10;

        ticks++;
        missingPoints = highScore - score;

        if (started) {


            for (int i = 0; i < columns.size(); i++) {

                Rectangle column = columns.get(i);

                column.x -= speed;

            }

            if (ticks % 2 == 0 && yMotion < 15) {
                yMotion += 2;

            }

            for (int i = 0; i < columns.size(); i++) {

                Rectangle column = columns.get(i);

                if (column.x + column.width < 0) {

                    columns.remove(column);

                    if (column.y == 0) {
                        addColumn(false);

                    }
                }
            }


            bird.y += yMotion;

            for (Rectangle column : columns) {

                if (column.y == 0 && bird.x + bird.width / 2 > column.x + column.width / 2 - 10 && bird.x + bird.width / 2 < column.x + column.width / 2 + 10) { // checks if player is between a pipe
                    score++; // add a score to player
                    if (score > highScore) {
                        highScore = score;
                    }
                }

                if (column.intersects(bird)) {

                    if (highScore <= score)
                        highScore = score;

                    gameOver = true;

                    if (bird.x <= column.x) {
                        bird.x = column.x - bird.width; // to bind the bird to the column who's interacted
                    } else {
                        if (column.y != 0) {
                            bird.y = column.y - bird.height;
                        } else if (bird.y < column.height) {
                            bird.y = column.height;
                        }
                    }
                }
            }

            if (bird.y > HEIGHT - 120 || bird.y < 0) {


                if (highScore <= score)
                    highScore = score;

                gameOver = true;

            }

            if (bird.y + yMotion >= HEIGHT - 120) {
                bird.y = HEIGHT - 120 - bird.height;
            }

        }

        renderer.repaint();
    }

    /**
     * method to render objects in the game frame
     * here we render the screen, bird, bottom
     * and a grass layer on the bottom
     *
     * @param g represent the graphics to work with
     */
    public void repaint(Graphics g) {

        // screen
        g.setColor(Color.cyan);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // bottom
        g.setColor(Color.orange);
        g.fillRect(0, HEIGHT - 120, WIDTH, 150);

        // grass / HEIGHT = "20" because the 'y' coordinate starts on the position of the last rectangle
        g.setColor(Color.green);
        g.fillRect(0, HEIGHT - 120, WIDTH, 20);

        // bird
        g.setColor(Color.red);
        g.fillRect(bird.x, bird.y, bird.width, bird.height);

        for (Rectangle column : columns) {
            paintColumn(g, column);
        }

        g.setFont(new Font("Arial", Font.BOLD, 40));

        if (!started) {

            g.setColor(Color.green.darker());
            g.drawString("Click to start!", 75, HEIGHT / 2 - 50);
        }

        if (gameOver) {
            g.setColor(Color.red.darker());
            g.drawString("Died", WIDTH / 2 - 25, HEIGHT / 2 - 50);
            g.setColor(Color.white);
            g.drawString("High Score: " + highScore, 0, HEIGHT - 60);
        }

        if (!gameOver && started) {
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
            if (score < highScore){
                g.setColor(Color.WHITE);
                g.drawString("Missing to Highscore: ", 2, HEIGHT - 60);
                g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
                g.setColor(getMissingPointsColor());
                g.drawString(getMissingPoints(), 450, HEIGHT - 60);
            } else if(score >= highScore) {
                g.setColor(Color.white);
                g.drawString("High Score: " + highScore, 2, HEIGHT - 60);
            }
        }
    }

    private Color getMissingPointsColor() {
        Color color;

        switch (missingPoints) {
            case 3:
                color = Color.yellow;
                break;
            case 2:
                color = Color.green.darker().darker();
                break;
            case 1:
                color = Color.green.darker();
                break;
            case 0:
                color = Color.green;
                break;
            default:
                color = Color.white;
                break;
        }
        return color;
    }

    private String getMissingPoints() {
        if (missingPoints > 0) {
            return String.valueOf(missingPoints);
        } else {
            if (score == highScore && highScore != 0) {
                return "Reached";
            } else if (highScore == 0) {
                return "No Highscore!";
            }
        }
        return null;
    }

    public static void main(String[] args) {
        game = new FlappyBird();
    }

    /*============ MOUSE LISTENER ============ */

    @Override
    public void mouseClicked(MouseEvent e) {
        jump();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    /*============ KEY LISTENER ============ */

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE)
            jump();
        else if (e.getKeyCode() == KeyEvent.VK_ALT)
            jump();
        else if (e.getKeyCode() == KeyEvent.VK_CONTROL)
            jump();
        else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            System.exit(0);
    }
}
