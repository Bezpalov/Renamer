package view;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

public class RenamerView extends JFrame implements ActionListener {

    private static int countOfRows;


    JTree tree;
    final JFrame window;
    JScrollPane pane;
    JButton rename;
    JButton up;
    JButton down;
    JButton renameWithNames;
    JButton undo;
    JButton redo;
    JButton changeCreationTime;
    JButton changeLastFormatTime;
    JPanel leftPanel;
    JPanel rightPanel;
    JPanel rightDownPanel;
    JPanel rightUpPanel;
    File file;

    LinkedList<HashMap<String, String>> renameList = new LinkedList<>();
    private int place = -1;

    // Creating a Treenode with directories and files
    static DefaultMutableTreeNode getNodes(File file, DefaultMutableTreeNode node) {
        for (File paths : file.listFiles()) {
            DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(paths.getName());
            countOfRows++;
            if (paths.isDirectory()) {
                node.add(newChild);
                getNodes(paths, newChild);
            } else {
                node.add(newChild);
            }

        }
        return node;
    }

    //Expand tree
    static JTree expandJtree(JTree tree) {


        System.out.println(countOfRows);
        for (int i = 0; i <= countOfRows; i++) {
            tree.expandRow(i);
        }
        //go to zero for next tree;
        countOfRows = 0;
        return tree;
    }

    File[] treepathToFile(TreePath[] paths) {
        File[] file = new File[paths.length];
        String path = "";
        for (int i = 0; i < paths.length; i++) {
            for (int j = 0; j < paths[i].getPathCount(); j++) {
                Object obj = paths[i].getPathComponent(j);
                path += obj.toString() + File.separator;
            }
            file[i] = new File(path);
            path = "";
        }
        return file;
    }

    void toUndo() {
        String renameTo;
        String renameFrom;
        HashMap<String, String> map = renameList.get(place);
        Set<Map.Entry<String, String>> entrySet = map.entrySet();

        for (Map.Entry<String, String> pair : entrySet) {
            renameTo = pair.getKey();
            renameFrom = pair.getValue();
            new File(renameFrom).renameTo(new File(renameTo));
        }
        place--;
    }

    void toRedo() {
        place++;
        String renameTo;
        String renameFrom;
        HashMap<String, String> map = renameList.get(place);
        Set<Map.Entry<String, String>> entrySet = map.entrySet();

        for (Map.Entry<String, String> pair : entrySet) {
            renameFrom = pair.getKey();
            renameTo = pair.getValue();
            new File(renameFrom).renameTo(new File(renameTo));
        }

    }



    //if true numbers, if false - new names
    String rename(File[] files, boolean flag){
        int positive = 0;
        int negative = 0;
        HashMap<String, String> map = new HashMap<>();

        //sort for rename deeper elements first
        Arrays.sort(files);
        for (int i = files.length-1; i >= 0; i--) {
            String path = files[i].getPath();
            String cutPath = path.substring(0, path.lastIndexOf(File.separator)+1);
            String oldName = null;

            if(flag)
                oldName = path.replace(cutPath, "");
            else
                oldName = JOptionPane.showInputDialog("Enter a new name");

            if(files[i].isDirectory() || oldName.charAt(0) == '.' || !oldName.contains(".")) {
                if (flag)
                    oldName = Integer.toString(i + 1);
            }else
                if(flag)
                    oldName = (i+1) + oldName.substring(oldName.lastIndexOf('.'));
            cutPath += oldName;

            boolean isRename = files[i].renameTo(new File(cutPath));

            if(isRename) {
                //add in collection to future responsibility undo the rename
                map.put(path, cutPath);

                positive++;
            }else
                negative++;
        }
        place++;
        renameList.add(map);
    return "Quantity of renamed files is " + positive + "\n"
            + "Quantity of unrenamed files is " + negative;

    }

    //if true - toUpperCase, if false - to lowerCase
    String toCase(File[] file, boolean bln){
        HashMap<String, String> map = new HashMap<>();
        String result = "";
        int negative = 0;
        int positive = 0;

        for (int i = 0; i < file.length; i++) {
            String path = file[i].getPath();
            String cutPath = path.substring(path.lastIndexOf(File.separator));

            if(file[i].isDirectory() || cutPath.contains("\\.") || !cutPath.contains(".")) {
                negative++;
                continue;
            }

            String extension="";
            if(bln)
                extension = cutPath.substring(cutPath.lastIndexOf(".")).toUpperCase();
            else
                extension = cutPath.substring(cutPath.lastIndexOf(".")).toLowerCase();

            cutPath = path.replace(cutPath.substring(cutPath.lastIndexOf(".")), extension);
            boolean flag = file[i].renameTo(new File(cutPath));

            //check on equals paths. if equals File.renameTo will return true, but name will be the same.
            // That's why positive--
            if(path.equals(cutPath))
                positive--;

            if(flag){
              map.put(path, cutPath);
              positive++;
            }
        }
        place++;
        renameList.add(map);
        result = "Quantity of renamed files is " + positive + "\n"
                + "Quantity of unrenamed files is " + negative;
        return result;
    }

    void setButonsBool(boolean bln, JButton... buttons){
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setEnabled(bln);
        }
    }



    void setToolTips(boolean bln){
        if(bln){
            rename.setToolTipText("rename a chosen files and directories. New names will change according to order of numbers");
            up.setToolTipText("Change the file Extension to upper case");
            down.setToolTipText("Change the file Extension to lower case");
            renameWithNames.setToolTipText("rename with using your own names");
        }else {
            rename.setToolTipText("Choose at least one directory or file");
            up.setToolTipText("Choose at least one directory or file");
            down.setToolTipText("Choose at least one directory or file");
            renameWithNames.setToolTipText("Choose at least one directory or file");
        }
    }

    private void setLookAndFeelForProgram() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    void addItemStateListener(){
            undo.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                }
            });
        undo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {


                if(place < 0 )
                    undo.setEnabled((false));
            }
        });

        redo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                redo.setEnabled(true);

                if(place >= renameList.size() || renameList.size() == 0)
                    redo.setEnabled(false);
            }
        });
    }

    void addSelectListener(JTree tree){
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                setButonsBool(true, rename, up, down, renameWithNames);
                setToolTips(true);
            }
        });

    }

    @Override
    public void actionPerformed(ActionEvent e) {

                TreePath[] paths = tree.getSelectionPaths();
                String result = "nothing to tell";

                //choosing a command
                String command = e.getActionCommand();
                switch (command){
                    case "rename":
                        result = rename(treepathToFile(paths), true);
                        break;
                    case "name rename":
                        result = rename(treepathToFile(paths), false);
                        break;
                    case "redo":
                        toRedo();
                        break;
                    case "up":
                        result = toCase(treepathToFile(paths), true);
                        break;
                    case "down":
                        result = toCase(treepathToFile(paths), false);
                        break;
                    case "undo":
                        toUndo();
                        break;
                }
                JOptionPane.showMessageDialog(window, result);

                leftPanel.remove(pane);

                DefaultMutableTreeNode nodes = getNodes(file, new DefaultMutableTreeNode(file));
                TreeModel treeModel = new DefaultTreeModel(nodes);
                tree = new JTree(treeModel);
                tree = expandJtree(tree);
                pane = new JScrollPane(tree);
                leftPanel.add(pane);
                leftPanel.repaint();
                window.repaint();
                leftPanel.updateUI();

                addSelectListener(tree);
                setToolTips(false);
                setButonsBool(false, rename, up, down, renameWithNames, undo, redo);
    }

    public RenamerView(File file) {
        super("RenamerView");
        window = this;
        this.file = file;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLookAndFeelForProgram();

        //Create Tree
        DefaultMutableTreeNode nodes = getNodes(file, new DefaultMutableTreeNode(file));
        TreeModel treeModel = new DefaultTreeModel(nodes);
        tree = new JTree(treeModel);
        tree = expandJtree(tree);

        //Buttons
        renameWithNames = new JButton("name rename");
        rename = new JButton("rename");
        up = new JButton("up");
        down = new JButton("down");
        undo = new JButton("undo");
        redo = new JButton("redo");
        changeCreationTime = new JButton("Change");
        changeLastFormatTime = new JButton("Change");
        setButonsBool(false, rename, up, down, renameWithNames, undo, redo);
        setToolTips(false);

        //Labels
        JLabel timeOFCreationName = new JLabel("Creation Time");
        JLabel lastChangingName = new JLabel("Last Changing");

        //TextFields
        JTextField fieldCreationTime = new JTextField("creationTime");
        JTextField fieldChangeTime = new JTextField("change time");

        //Panels
        leftPanel = new JPanel();
        rightPanel = new JPanel(new GridLayout(2, 1));
        //РАзбивка правой части на 2 панели
        rightUpPanel = new JPanel();
        rightDownPanel = new JPanel();
        rightPanel.add(rightUpPanel);
        rightPanel.add(rightDownPanel);

        //установка менеджеров компоновки и наполнение панелей
        setLayout(new FlowLayout(FlowLayout.LEFT));
        GridLayout grid = new GridLayout(3, 2);
        grid.setHgap(10);
        grid.setVgap(15);
        rightDownPanel.setLayout(grid);

        //GroupLayout для нормального отображения поля с
        GroupLayout groupLayout = new GroupLayout(rightUpPanel);
        rightUpPanel.setLayout(groupLayout);
        groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
                .addGroup(
                    groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(timeOFCreationName)
                        .addComponent(fieldCreationTime)
                        .addComponent(changeCreationTime)
                )
                .addGroup(
                        groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(lastChangingName)
                            .addComponent(fieldChangeTime)
                            .addComponent(changeLastFormatTime)
                )
        );

        groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
                .addGroup(
                        groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(timeOFCreationName)
                            .addComponent(lastChangingName)
                )
                .addGroup(
                        groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(fieldChangeTime)
                                .addComponent(fieldCreationTime)
                )
                .addGroup(
                        groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(changeCreationTime)
                                .addComponent(changeLastFormatTime)
                )
        );

        pane = new JScrollPane(tree);
        leftPanel.add(pane);

        rightDownPanel.add(renameWithNames);
        rightDownPanel.add(rename);
        rightDownPanel.add(up);
        rightDownPanel.add(down);
        rightDownPanel.add(undo);
        rightDownPanel.add(redo);







        add(leftPanel);
        add(rightPanel);

        //Добавление слушателей к кнопкам
        rename.addActionListener(this);
        renameWithNames.addActionListener(this);
        up.addActionListener(this);
        down.addActionListener(this);
        undo.addActionListener(this);
        redo.addActionListener(this);

        addItemStateListener();
        //Custom listener for tree. Do it because it repeat in rename.actionListener
        addSelectListener(tree);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        new RenamerView(new File("C:\\Users\\au\\Desktop\\REC"));
    }
}
