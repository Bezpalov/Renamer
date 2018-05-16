package view;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

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
    JButton returnButton;
    JPanel leftPanel;
    JPanel rightPanel;
    File file;

    LinkedList<HashMap<String, String>> renameList = new LinkedList<>();

    // Creating a Treenode with directories and files
    static DefaultMutableTreeNode getNodes(File file, DefaultMutableTreeNode node){
        for(File paths: file.listFiles() ){
            DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(paths.getName());
            countOfRows++;
            if(paths.isDirectory()){
                node.add(newChild);
                getNodes(paths, newChild);
            }else{
                node.add(newChild);
            }

        }
        return node;
    }

    //Expand tree
    static JTree expandJtree (JTree tree){


        System.out.println(countOfRows);
        for (int i = 0; i <= countOfRows; i++) {
            tree.expandRow(i);
        }
        //go to zero for next tree;
        countOfRows = 0;
        return tree;
    }

    File[] treepathToFile(TreePath[] paths){
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

    //if true numbers, if false - new names
    String rename(File[] files, boolean flag){
        int positive = 0;
        int negative = 0;
        HashMap<String, String> map = new HashMap<>();

        for (int i = 0; i < files.length; i++) {
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
        renameList.add(map);
    return "Quantity of renamed files is " + positive + "\n"
            + "Quantity of unrenamed files is " + negative;

    }

    String toUpper(File[] file){
        String path
        for (int i = 0; i < file.length; i++) {

        }
        return null;
    }

    void setButonsBool(boolean bln, JButton... buttons){
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setEnabled(bln);
        }
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
                    case "returnButton":
                        System.out.println("return");
                        break;
                    case "up":
                        System.out.println("up");
                        break;
                    case "down":
                        System.out.println("down");
                        break;
                    case "undo":
                        System.out.println("undo");
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
                setButonsBool(false, rename, up, down, renameWithNames);
    }

    public RenamerView(File file) {
        super("RenamerView");
        window = this;
        this.file = file;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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
        returnButton = new JButton("return");
        setButonsBool(false, rename, up, down, renameWithNames);
        setToolTips(false);

        //Panels
        leftPanel = new JPanel();
        rightPanel = new JPanel();

        setLayout(new FlowLayout(FlowLayout.LEFT));
        GridLayout grid = new GridLayout(6, 1);
        grid.setHgap(10);
        grid.setVgap(15);
        rightPanel.setLayout(grid);

        pane = new JScrollPane(tree);
        leftPanel.add(pane);

        rightPanel.add(renameWithNames);
        rightPanel.add(rename);
        rightPanel.add(up);
        rightPanel.add(down);
        rightPanel.add(undo);
        rightPanel.add(returnButton);

        add(leftPanel);
        add(rightPanel);

        rename.addActionListener(this);
        renameWithNames.addActionListener(this);
        up.addActionListener(this);
        down.addActionListener(this);








//        addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//
//                TreePath[] paths = tree.getSelectionPaths();
//                String result = rename(treepathToFile(paths), false);
//                JOptionPane.showMessageDialog(window, result);
//
//                leftPanel.remove(pane);
//
//                DefaultMutableTreeNode nodes = getNodes(file, new DefaultMutableTreeNode(file));
//                TreeModel treeModel = new DefaultTreeModel(nodes);
//                tree = new JTree(treeModel);
//                tree = expandJtree(tree);
//                pane = new JScrollPane(tree);
//                leftPanel.add(pane);
//                leftPanel.repaint();
//                window.repaint();
//                leftPanel.updateUI();
//
//                addSelectListener(tree);
//                setToolTips(false);
//                setButonsBool(false, rename, up, down);
////
//            }
//        });

        //Custom listener for tree. Do it because it repeat in rename.actionListener
        addSelectListener(tree);





        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        new RenamerView(new File("C:\\Users\\Au\\Desktop\\glassfish 4.1.2"));
    }


}
