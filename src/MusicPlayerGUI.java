import javax.swing.*;

public class MusicPlayerGUI  extends JFrame {
    public MusicPlayerGUI() {
        super("Music Player");

        setSize(400, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Launch in center of screen
        setLocationRelativeTo(null);

        // prevent the app from being resized
        setResizable(false);

        // can controll x and y coordinates of component
        setLayout(null);
    }
}
