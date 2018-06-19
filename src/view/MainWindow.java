package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MainWindow extends JFrame {
    private static  File PATH_TO_DIRECTORIES;
    private MainWindow window;

    public MainWindow(){
        super("Main Window");
        window = this;
        setSize(270, 100);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Renamer.setLookAndFeelForProgram();
        Button rename = new Button("Choose a file");

        rename.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Close first window
                window.setVisible(false);
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int result = chooser.showOpenDialog(rename);

                if(result == JFileChooser.APPROVE_OPTION)
                    PATH_TO_DIRECTORIES = chooser.getSelectedFile();
                else
                    System.exit(0);

                new Renamer(PATH_TO_DIRECTORIES);
                window.dispose();
            }
        });

        setLayout(new FlowLayout());
        add(rename);
        setVisible(true);

    }

    /**
     * Проверка на наличие ошибки
     * @return возвращает обьект File
     * @throws NullPointerException
     */
    public File getPathToDirectories() throws NullPointerException{
        if(PATH_TO_DIRECTORIES != null)
            return PATH_TO_DIRECTORIES;
        else
            throw new NullPointerException("PATH_TO_DIRECTORIES == null");

    }

    /**
     * Точка входа в приложение
     * @param args
     */
    public static void main(String[] args) {
        new MainWindow();
    }
}
