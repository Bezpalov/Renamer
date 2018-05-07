package View;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Renamer extends JFrame {
    private static int countOfRows;
    //Creating a Treenode with directories and files
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
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        DefaultMutableTreeNode nodes = getNodes(file, new DefaultMutableTreeNode(file));
        JTree tree = new JTree(nodes);
        tree = expandJtree(tree);
        System.out.println(tree.getRowCount());

        Button rename = new Button("rename");
        Button up = new Button("up");
        Button down = new Button("down");


        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();
//        leftPanel.setSize(400, 400);
//        rightPanel.setSize(50, 400);

        setLayout(new FlowLayout(FlowLayout.LEFT));
        rightPanel.setLayout(new BorderLayout(10, 15));

        leftPanel.add(new JScrollPane(tree));
        rightPanel.add(rename, BorderLayout.NORTH);
        rightPanel.add(up, BorderLayout.CENTER);
        rightPanel.add(down, BorderLayout.SOUTH);

        add(leftPanel);
        add(rightPanel);






        setVisible(true);
    }

    public static void main(String[] args) {
        new Renamer(new File("g:/users/wrath/desktop/java"));
    }
}
