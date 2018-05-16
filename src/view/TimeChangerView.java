package view;

import javax.swing.*;
import java.awt.*;

public class TimeChangerView extends JFrame {
    public TimeChangerView(){
        super("RenamerView");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Button btn1 = new Button("Change");

        setVisible(true);
    }
}
