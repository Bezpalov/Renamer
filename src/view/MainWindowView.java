package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MainWindowView extends JFrame {
    private static  File PATH_TO_DIRECTORIES;
    private MainWindowView window;

    public MainWindowView(){
        super("Main Window");
        window = this;
        setSize(270, 100);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        JPanel upPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,50, 15));
        upPanel.setSize(300,100);
        Button rename = new Button("RenamerView");
        Button changeTime = new Button("Change time");
        upPanel.add(rename);
        upPanel.add(changeTime);

        rename.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Close first window
                window.setVisible(false);
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("g:/users/wrath/desktop"));
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int result = chooser.showOpenDialog(rename);

                if(result == JFileChooser.APPROVE_OPTION)
                    PATH_TO_DIRECTORIES = chooser.getSelectedFile();
                else
                    System.exit(0);


                new RenamerView(PATH_TO_DIRECTORIES);
            }
        });

        setLayout(new FlowLayout());
        add(upPanel);
        setVisible(true);


    }

    //Check on throwing  a NullPointerException
    public File getPathToDirectories() throws NullPointerException{
        if(PATH_TO_DIRECTORIES != null)
            return PATH_TO_DIRECTORIES;
        else
            throw new NullPointerException("PATH_TO_DIRECTORIES == null");

    }
    public static void main(String[] args) {
        new MainWindowView();
    }
}
