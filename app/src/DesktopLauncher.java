import gui.MyJTabbedPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Andrey on 7/14/2014.
 */
public class DesktopLauncher {
    public static void main(String[] args) {
        //terrible swing masterpiece
        createGUI();
    }

    private static void createGUI() {
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        JFrame frame = new JFrame("VK Downloader");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(new MyJTabbedPanel(), BorderLayout.CENTER);
     //   frame.setSize(800, 400);
        frame.pack();
        frame.setVisible(true);
    }
}
