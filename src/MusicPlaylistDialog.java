import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class MusicPlaylistDialog extends JDialog {
    private MusicPlayerGUI musicPlayerGUI;
    private ArrayList<String> songPaths;

    public MusicPlaylistDialog(MusicPlayerGUI musicPlayerGUI ) {
        this.musicPlayerGUI = musicPlayerGUI;
        songPaths = new ArrayList<>();

        setTitle("Create");
        setSize(600, 400);
        setResizable(false);
        getContentPane().setBackground(MusicPlayerGUI.FRAME_COLOR);
        setLayout(null);
        setModal(true);
        setLocationRelativeTo(musicPlayerGUI);

        addDialogComponents();

    }

    private void addDialogComponents() {
            // container to hold each song path
            JPanel songContainer = new JPanel();
            songContainer.setLayout(new BoxLayout(songContainer, BoxLayout.Y_AXIS));
            songContainer.setBounds((int)(getWidth() * 0.025), 10, (int)(getWidth() * 0.90), (int) (getHeight() * 0.75));
            add(songContainer);
            // add button
            JButton addSongButton = new JButton("Add");
            addSongButton.setBounds(60, (int) (getHeight() * 0.80), 100, 25);
            addSongButton.setFont(new Font("Dialog", Font.BOLD, 14));
            addSongButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // open file explorer
                    JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));
                    jFileChooser.setCurrentDirectory(new File("src/assets"));
                    int result = jFileChooser.showOpenDialog(MusicPlaylistDialog.this);

                    File selectedFile = jFileChooser.getSelectedFile();
                    if(result == JFileChooser.APPROVE_OPTION && selectedFile != null){
                    JLabel filePathLabel = new JLabel(selectedFile.getPath());
                    filePathLabel.setFont(new Font("Dialog", Font.BOLD, 12));
                    filePathLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                    // add to the list
                    songPaths.add(filePathLabel.getText());

                    // add to container
                    songContainer.add(filePathLabel);

                    // refreshes dialog to show newly added JLabel
                    songContainer.revalidate();
                }
            }
        });
        add(addSongButton);

            // save
            JButton savePlaylistButton = new JButton("Save");
            savePlaylistButton.setBounds(215, (int) (getHeight() * 0.80), 100, 25);
            savePlaylistButton.setFont(new Font("Dialog", Font.BOLD, 14));
    }
}














