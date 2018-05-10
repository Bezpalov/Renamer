package View;

import com.sun.crypto.provider.JceKeyStore;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

public class Renamer extends JFrame {

    private static int countOfRows;
    JTree tree;
    final JFrame window;
    JScrollPane pane;

//    List<HashMap<File, File> list = new LinkedList<HashMap<File, File>>();

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
        return tree;
    }

    public Renamer(File file) {
        super("Renamer");
        window = this;
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        DefaultMutableTreeNode nodes = getNodes(file, new DefaultMutableTreeNode(file));
        TreeModel treeModel = new DefaultTreeModel(nodes);
        tree = new JTree(treeModel);
        tree = expandJtree(tree);
        System.out.println(tree.getRowCount());

        JButton rename = new JButton("rename");
        JButton up = new JButton("up");
        JButton down = new JButton("down");


        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();
//        leftPanel.setSize(400, 400);
//        rightPanel.setSize(50, 400);

        setLayout(new FlowLayout(FlowLayout.LEFT));
        rightPanel.setLayout(new BorderLayout(10, 15));

        pane = new JScrollPane(tree);
        leftPanel.add(pane);

        rightPanel.add(rename, BorderLayout.NORTH);
        rightPanel.add(up, BorderLayout.CENTER);
        rightPanel.add(down, BorderLayout.SOUTH);

        add(leftPanel);
        add(rightPanel);



    rename.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {


           leftPanel.remove(pane);
//
//
            DefaultMutableTreeNode nodes = getNodes(file, new DefaultMutableTreeNode(file));
            TreeModel treeModel = new DefaultTreeModel(nodes);
            tree = new JTree(treeModel);
            tree = expandJtree(tree);
            pane = new JScrollPane(tree);
            leftPanel.add(pane);
            leftPanel.repaint();
            window.repaint();

            leftPanel.updateUI();
//
        }
    });




        setVisible(true);

        //Think about better way to do this


        up.setEnabled(false);
        rename.setEnabled(false);
        down.setEnabled(false);


        rename.setToolTipText("Choose at least one file or directory");

        while(tree.getSelectionCount() == 0){

        }
        up.setEnabled(true);
        rename.setEnabled(true);
        down.setEnabled(true);
        rename.setToolTipText("rename choosen files and directories");



    }

    public static void main(String[] args) {
        new Renamer(new File("c:/users/au/desktop/Hearthstone"));
    }
}
