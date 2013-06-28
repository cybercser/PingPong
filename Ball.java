import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static javax.sound.sampled.AudioSystem.*;

/**
 * Created with IntelliJ IDEA.
 * User: cybercser
 * Date: 13-6-26
 * Time: 20:48:42
 * TODO: spin effect,
 */

public class Ball extends JPanel implements Runnable, PingPongConstants {
    public enum Direction { UP, DOWN };

    private int x = DESK_LENGTH / 2;
    private int y = DESK_WIDTH / 2;

    private int dx = 2;
    private int dy = 2;
    private Integer score1 = 0;
    private Integer score2 = 0;

    // the pos stands for y value of the upper-left corner of the bat rectangle
    private int bat1Y = (DESK_WIDTH - BAT_LENGTH) / 2;
    private int bat2Y = (DESK_WIDTH - BAT_LENGTH) / 2;

    private int bat1Center = DESK_WIDTH - BAT_LENGTH / 2;
    private int bat2Center = DESK_WIDTH - BAT_LENGTH / 2;

    // key pressed/released
    private boolean wPressed = false;
    private boolean sPressed = false;
    private boolean upArrowPressed = false;
    private boolean downArrowPressed = false;

    // for random ball direction
    private int jitter = 0;
    // game result
    private int result = 0;

    public Ball() {
        new Thread(this).start();

        setFont(new Font("Georgia", Font.BOLD, 30));

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W :
                        wPressed = true;
                        break;
                    case KeyEvent.VK_S :
                        sPressed = true;
                        break;
                    case KeyEvent.VK_UP :
                        upArrowPressed = true;
                        break;
                    case KeyEvent.VK_DOWN :
                        downArrowPressed = true;
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W :
                        wPressed = false;
                        break;
                    case KeyEvent.VK_S :
                        sPressed = false;
                        break;
                    case KeyEvent.VK_UP :
                        upArrowPressed = false;
                        break;
                    case KeyEvent.VK_DOWN :
                        downArrowPressed = false;
                        break;
                }
                }
            });
    }

    @Override
    public void run() {
        try {
            while (result == 0) {
                processKeyEvent();
                repaint();
                Thread.sleep(UPDATE_INTERVAL);
            }
        }
        catch (InterruptedException ex) {
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(DESK_LENGTH, DESK_WIDTH);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.RED);

        // check game status
        if (score1 == 11)
            result = PLAYER1_WIN;
        else if (score2 == 11)
            result = PLAYER2_WIN;

        bounce();

        // Adjust ball position
        x += dx;
        y += dy;
        g.fillOval(x - BALL_RADIUS, y - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
        // draw the net
        g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
        // draw the bats
        g.setColor(Color.BLUE);
        g.fillRect(0, bat1Y, BAT_THICKNESS, BAT_LENGTH);
        g.fillRect(getWidth() - BAT_THICKNESS, bat2Y, BAT_THICKNESS, BAT_LENGTH);
        // draw the score
        g.drawString(score1.toString(), DESK_LENGTH / 2 - 50, 30);
        g.drawString(score2.toString(), DESK_LENGTH / 2 + 30, 30);

        // draw game over info
        FontMetrics fm = g.getFontMetrics();
        int stringWidth = fm.stringWidth("Player X wins!");
        int stringAscent = fm.getAscent();
        int xCoord = getWidth() / 2 - stringWidth / 2;
        int yCoord = getHeight() / 2 - stringAscent / 2;
        if (PLAYER1_WIN == result)
            g.drawString("Player 1 wins!", xCoord, yCoord);
        else if (PLAYER2_WIN == result)
            g.drawString("Player 2 wins!", xCoord, yCoord);
    }

    private void processKeyEvent() {
        if (wPressed && !sPressed)
            moveBat(PLAYER1, Direction.UP);
        else if (sPressed && !wPressed)
            moveBat(PLAYER1, Direction.DOWN);

        if (upArrowPressed && !downArrowPressed)
            moveBat(PLAYER2, Direction.UP);
        else if (downArrowPressed && !upArrowPressed)
            moveBat(PLAYER2, Direction.DOWN);
    }

    public static synchronized void playSound(final String url) {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            public void run() {
                try {
                    Clip clip = getClip();
                    AudioInputStream inputStream = getAudioInputStream(
                            this.getClass().getResource("sound/" + url));
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }

    private void bounce() {
        if (Math.random() > 0.5)
            jitter = 1;
        else
            jitter = -1;
        // Check boundaries
        if (x < BALL_RADIUS + BAT_THICKNESS) {
            if ((y >= bat1Y) && (y <= bat1Y + BAT_LENGTH)) {
                playSound("ball_bat.wav");
                // spin effect
                int distToBatCenter = Math.abs(y-bat1Center);
                if ( distToBatCenter <= 5 ) {
                    dx = 2;
                }
                else if ( distToBatCenter > 5 && distToBatCenter <= 15) {
                    dx = 3;
                }
                else if ( distToBatCenter > 15)  {
                    dx = 4;
                }
            }
            else { // player 1 lose
                score2++;
                x = DESK_LENGTH / 2;
                y = DESK_WIDTH / 2;
                dx = 2;
                dy = 2 * jitter;
            }
        }

        if (x > getWidth() - BALL_RADIUS - BAT_THICKNESS)
        {
            if ((y >= bat2Y) && (y <= bat2Y + BAT_LENGTH)) {
                playSound("ball_bat.wav");
                // spin effect
                int distToBatCenter = Math.abs(y-bat1Center);
                if ( distToBatCenter <= 5 ) {
                    dx = -2;
                }
                else if ( distToBatCenter > 5 && distToBatCenter <= 15) {
                    dx = -3;
                }
                else if ( distToBatCenter > 15)  {
                    dx = -4;
                }
            }
            else { //player 2 lose
                score1++;
                x = DESK_LENGTH / 2;
                y = DESK_WIDTH / 2;
                 dx = 2;
                 dy = 2 * jitter;
            }
        }

        if (y < BALL_RADIUS) {
            dy = Math.abs(dy);
            playSound("ball_desk.wav");
        }
        if (y > getHeight() - BALL_RADIUS) {
            dy = -Math.abs(dy);
            playSound("ball_desk.wav");
        }
    }

    private void moveBat(int player, Direction direction) {
        if (PLAYER1 == player) {
            if (Direction.UP == direction) {
                bat1Y -= BAT_SPEED;
                if (bat1Y < 0)
                    bat1Y = 0;
            }
            else if (Direction.DOWN == direction) {
                bat1Y += BAT_SPEED;
                if (bat1Y > getHeight() - BAT_LENGTH)
                    bat1Y = getHeight() - BAT_LENGTH;
            }
        }
        else if (PLAYER2 == player) {
            if (Direction.UP == direction) {
                bat2Y -= BAT_SPEED;
                if (bat2Y < 0)
                    bat2Y = 0;
            }
            else if (Direction.DOWN == direction) {
                bat2Y += BAT_SPEED;
                if (bat2Y > getHeight() - BAT_LENGTH)
                    bat2Y = getHeight() - BAT_LENGTH;
            }
        }
    }
}
