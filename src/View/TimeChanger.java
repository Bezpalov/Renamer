package View;

import javax.swing.*;
import java.awt.*;

public class TimeChanger extends JFrame {
    public TimeChanger(){
        super("Renamer");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Button btn1 = new Button("Change");

        setVisible(true);
    }
}
