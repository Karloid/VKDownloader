package gui;

import core.VKDownloader;

import javax.swing.*;
import java.io.*;
import java.util.Properties;

/**
 * Created by Andrey on 7/14/2014.
 */
public class VkontaktePanel extends JPanel {
    public static final int HEIGHT = 20;
    public static final int WIDTH = 200;
    public static final int X_MARGIN = 20;
    private static final String PROP_NAME = "config.prop";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String APP_ID = "appId";
    private static final String ID = "id";
    public static final String ID_TYPE = "idType";
    public static final String DEST_PATH = "destFolder";
    private final JTextField accessTokenField;
    private final JTextField appField;
    private final JButton getAccessTokenButton;
    private final JComboBox idTypeComboBox;
    private final JTextField idField;
    private final JFileChooser fileChooser;
    private final JTextField destField;
    private final JButton destButton;
    private final JButton savePhotoButton;
    private final JButton saveAudioButton;

    public VkontaktePanel() {
        // setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setLayout(null);
        JLabel appIdLabel = new JLabel("AppId");
        appIdLabel.setBounds(X_MARGIN, HEIGHT * 0, WIDTH, HEIGHT);
        add(appIdLabel);
        appField = new JTextField("");
        appField.setAlignmentX(CENTER_ALIGNMENT);
        appField.setBounds(X_MARGIN, HEIGHT, WIDTH, HEIGHT);
        add(appField);
        getAccessTokenButton = new JButton("Get access token");
        getAccessTokenButton.setBounds(X_MARGIN, HEIGHT * 2, WIDTH, HEIGHT);
        add(getAccessTokenButton);
        getAccessTokenButton.addActionListener(e -> {
            saveDefaultsToFile();
            invokeAccessRequest();
        });

        JLabel accessTokenLabel = new JLabel("AccessToken");
        accessTokenLabel.setBounds(X_MARGIN, HEIGHT * 4, WIDTH, HEIGHT);
        add(accessTokenLabel);
        accessTokenField = new JTextField("");
        accessTokenField.setBounds(X_MARGIN, HEIGHT * 5, WIDTH, HEIGHT);
        add(accessTokenField);

        JLabel idTypeLabel = new JLabel("Id Type");
        idTypeLabel.setBounds(X_MARGIN, HEIGHT * 7, WIDTH, HEIGHT);
        add(idTypeLabel);
        idTypeComboBox = new JComboBox(IdType.values());
        idTypeComboBox.setBounds(X_MARGIN, HEIGHT * 8, WIDTH, HEIGHT);
        idTypeComboBox.setSelectedIndex(0);
        //   System.out.println(idTypeComboBox.getSelectedItem().equals(IdType.USER));
        add(idTypeComboBox);

        JLabel idLabel = new JLabel("Id");
        idLabel.setBounds(X_MARGIN, HEIGHT * 9, WIDTH, HEIGHT);
        add(idLabel);

        idField = new JTextField("");
        idField.setBounds(X_MARGIN, HEIGHT * 10, WIDTH, HEIGHT);
        add(idField);


        JLabel fileChooserLabel = new JLabel("Destination folder");
        fileChooserLabel.setBounds(X_MARGIN, HEIGHT * 11, WIDTH, HEIGHT);
        add(fileChooserLabel);

        destField = new JTextField("C:\\");
        destField.setBounds(X_MARGIN, HEIGHT * 12, WIDTH - 20, HEIGHT);
        add(destField);


        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);


        destButton = new JButton("...");
        destButton.addActionListener(e -> {
            int result = fileChooser.showOpenDialog(VkontaktePanel.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                destField.setText(fileChooser.getSelectedFile().toString() + "\\");
            }
            saveDefaultsToFile();
        });
        destButton.setBounds(X_MARGIN + WIDTH - 20, HEIGHT * 12, 20, HEIGHT);
        add(destButton);

        savePhotoButton = new JButton("Save photo");
        savePhotoButton.setBounds(X_MARGIN, HEIGHT * 13 + 5, WIDTH - 20, HEIGHT * 2);
        savePhotoButton.addActionListener(e -> {
            saveDefaultsToFile();
            enableButtons(false);
            savePhoto();
            enableButtons(true);
        });
        add(savePhotoButton);

        saveAudioButton = new JButton("Save audio");
        saveAudioButton.setBounds(X_MARGIN, HEIGHT * 15 + 10, WIDTH - 20, HEIGHT * 2);
        saveAudioButton.addActionListener(e -> {
            saveDefaultsToFile();
            enableButtons(false);
            saveAudio();
            enableButtons(true);
        });
        add(saveAudioButton);

        loadDefaultsFromFile();


    }

    private void saveDefaultsToFile() {
        try {
            Properties prop = new Properties();
            prop.setProperty(ACCESS_TOKEN, accessTokenField.getText());
            prop.setProperty(APP_ID, appField.getText());
            prop.setProperty(ID, idField.getText());
            prop.setProperty(ID_TYPE, idTypeComboBox.getSelectedItem().toString());
            prop.setProperty(DEST_PATH, destField.getText());
            File f = new File(PROP_NAME);
            OutputStream out = new FileOutputStream(f);
            prop.store(out, "nice comment");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void invokeAccessRequest() {
        try {                   // dark magic here
            String command = "cmd /c start \"\" " + "\"https://oauth.vk.com/authorize?client_id="
                    + appField.getText() +
                    "&scope=photos,audio&redirect_uri=https://oauth.vk.com/blank.html&display=page&v=5.23&" +
                    "response_type=token\"";
            System.out.println(command);
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDefaultsFromFile() {
        if (!new File(PROP_NAME).exists()) {
            return;
        }
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(PROP_NAME));

            accessTokenField.setText((String) properties.get(ACCESS_TOKEN));
            appField.setText((String) properties.get(APP_ID));
            idField.setText((String) properties.get(ID));
            destField.setText((String) properties.get(DEST_PATH));
            if (IdType.valueOf((String) properties.get(ID_TYPE)).equals(IdType.USER)) {
                idTypeComboBox.setSelectedIndex(0);
            } else {
                idTypeComboBox.setSelectedIndex(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enableButtons(boolean b) {
        savePhotoButton.setEnabled(b);
        saveAudioButton.setEnabled(b);
    }

    private void savePhoto() {
        System.out.println("save photo");
        if (!validateFields()) {
            System.out.println("Validate error");
            return;
        }
        VKDownloader vkDownloader = new VKDownloader();
        vkDownloader.init();
        vkDownloader.setAccessToken(accessTokenField.getText());
        String paramUid = null;
        String paramGid = null;

        String tmpId = idField.getText();
        if (idTypeComboBox.getSelectedItem().equals(IdType.USER)) {
            paramUid = tmpId;
        } else {
            idTypeComboBox.getSelectedItem().equals(IdType.GROUP);
            paramGid = tmpId;
        }
        String folderToSave = destField.getText();
        vkDownloader.downloadAllPhotos(paramUid, paramGid, folderToSave);
        JOptionPane.showMessageDialog(null, "Total photos: " + vkDownloader.getTotalPhotosCount() + "; Downloaded: " + vkDownloader.getDownloadedPhotosCount() + " files to " + destField.getText());
    }

    private boolean validateFields() {
        return true; //TODO?
    }

    private void saveAudio() {      /*
        VKDownloader vkDownloader = new VKDownloader();   //TODO remove?
        vkDownloader.init();
        vkDownloader.setAccessToken(accessTokenField.getText());
        String paramUid = null;
        String paramGid = null;

        String tmpId = idField.getText();
        if (idTypeComboBox.getSelectedItem().equals(IdType.USER)) {
            paramUid = tmpId;
        } else {
            idTypeComboBox.getSelectedItem().equals(IdType.GROUP);
            paramGid = tmpId;
        }
        String folderToSave = destField.getText();
        vkDownloader.downloadAllPhotos(paramUid, paramGid, folderToSave);
        JOptionPane.showMessageDialog(null, "Downloaded " + vkDownloader.getTotalPhotosCount() + " files to "+ destField.getText());
        */

        System.out.println("save audio");
        if (!validateFields()) {
            System.out.println("Validate error");
            return;
        }
        VKDownloader vkDownloader = new VKDownloader();
        vkDownloader.setAccessToken(accessTokenField.getText());
        String paramUid = null;
        String paramGid = null;

        String tmpId = idField.getText();
        if (idTypeComboBox.getSelectedItem().equals(IdType.USER)) {
            paramUid = tmpId;
        } else {
            idTypeComboBox.getSelectedItem().equals(IdType.GROUP);
            paramGid = tmpId;
        }
        vkDownloader.getTracks(paramUid, paramGid);
        String folderToSave = destField.getText();
        vkDownloader.saveTracks(folderToSave);
        JOptionPane.showMessageDialog(null, "Total count of tracks: " + vkDownloader.getTotalTracksCount() + "; Downloaded: " + vkDownloader.getDownloadedTracksCount() + " files to " + destField.getText());

    }
}
