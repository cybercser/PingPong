import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: cybercser
 * Date: 13-6-26
 * Time: 上午10:57
 * To change this template use File | Settings | File Templates.
 */
public class PingPongApp extends JApplet implements PingPongConstants {
    private Ball ball = new Ball();

    public PingPongApp() {
        ball.setBorder(BorderFactory.createLineBorder(Color.RED));
        setLayout(new BorderLayout());
        add(ball, BorderLayout.CENTER);
    }

    private boolean isStandAlone = false;

    public void init() {
        setSize(new Dimension(DESK_LENGTH, DESK_WIDTH));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Ping Pong");

                PingPongApp applet = new PingPongApp();
                applet.isStandAlone = true;
                frame.add(applet, BorderLayout.CENTER);
                applet.init();
                applet.start();

                frame.pack();
                frame.setResizable(false);
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
     }
}
