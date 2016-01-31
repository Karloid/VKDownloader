package gui;

import javafx.geometry.VerticalDirection;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Andrey on 7/14/2014.
 */
public class MyJTabbedPanel extends JPanel {
    public MyJTabbedPanel() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Vk",new VkontaktePanel());
        tabbedPane.setPreferredSize(new Dimension(250, 500));
        add(tabbedPane);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

    }
}
