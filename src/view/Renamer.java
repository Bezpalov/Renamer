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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Renamer extends JFrame implements ActionListener {

    private static int countOfRows;
    private JTree tree;
    final private JFrame window;
    private JScrollPane pane;
    private JButton rename;
    private JButton up;
    private JButton down;
    private JButton renameWithNames;
    private JButton undo;
    private JButton redo;
    private JButton CreationTime;
    private JButton LastFormatTime;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JPanel rightDownPanel;
    private JPanel rightUpPanel;
    private JLabel timeOFCreationName;
    private JLabel lastChangingName;
    private JTextField fieldCreationTime;
    private JTextField fieldChangeTime;
    private File file;
    private Position pos = new Position();
    private Path path;
    private int place;

    /**
     * Коллекция для хранения переименованных ранее элементов
     */
    LinkedList<HashMap<String, String>> renameList = new LinkedList<>();

    /**
     * Создание дерева из нод
     * @param file исходная директория или файл
     * @param node корневая нода для посторения дерева
     * @return заполненное TreeNode
     */
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

    /**
     * Развертка дерева на главном окне программы
     * @param tree исходное дерево
     * @return развернутое дерево
     */
    static JTree expandJtree(JTree tree) {


        System.out.println(countOfRows);
        for (int i = 0; i <= countOfRows; i++) {
            tree.expandRow(i);
        }
        //go to zero for next tree;
        countOfRows = 0;
        return tree;
    }

    /**
     * Преобразование TreePath к Path при сохранении пути
     * @param tPath Выделенный на дереве элемент
     * @return обьект типа Path с тем же путем что и исходный обьект
     */
    Path treePathtoPath(TreePath tPath){
        String path = "";
        for (int i = 0; i <tPath.getPathCount(); i++) {
            Object obj = tPath.getPathComponent(i);
            path += obj.toString() + File.separator;
        }
        return Paths.get(path);
    }

    /**
     * Преобразование массива (или нет) treePath к File
     * @param paths выделенные на дереве элементы
     * @return массив обьектов File для дальнейщей работы
     */
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

    /**
     * возвращение переименованных ранее элементов на 1 операцию вперед
     */
    private String toUndo() {
        int positive = 0;
        int negative = 0;
        boolean flag;
        String renameTo;
        String renameFrom;
        HashMap<String, String> map = renameList.get(pos.getPosition());
        Set<Map.Entry<String, String>> entrySet = map.entrySet();

        for (Map.Entry<String, String> pair : entrySet) {
            renameTo = pair.getKey();
            renameFrom = pair.getValue();
            flag = new File(renameFrom).renameTo(new File(renameTo));
            if(flag)
                positive++;
            else
                negative++;
        }
        place = pos.getPosition();
        pos.setPosition(--place);

        return "returned files : " + positive + "\n"
                + "not returned files : " + negative;
    }

    /**
     * возвращение к переименованным ранее элементам на 1 операцию назад
     */
    private String toRedo() {
        int positive = 0;
        int negative = 0;
        boolean flag;
        place = pos.getPosition();
        pos.setPosition(++place);
        String renameTo;
        String renameFrom;
        HashMap<String, String> map = renameList.get(pos.getPosition());
        Set<Map.Entry<String, String>> entrySet = map.entrySet();

        for (Map.Entry<String, String> pair : entrySet) {
            renameFrom = pair.getKey();
            renameTo = pair.getValue();
            flag = new File(renameFrom).renameTo(new File(renameTo));
            if(flag)
                positive++;
            else
                negative++;
        }

        return "returned files : " + positive + "\n"
                + "not returned files : " + negative;
    }

    /**
     * Переименование файлов и директорий
     * @param files ранее полученный массив типа File, содержащий пути
     *              к выделенным ранее элементам дерева
     * @param flag Параметр, отвечающий за выбор типа переименования,
     *
     *             если true - то файлы будут переименовываться
     *             с присвоением им имен в виде чисел (от 1 до кол-ва файлов)
     *
     *             если false - переименование будет происходить путем
     *             назначения вручную каждого выделенего элемента
     *             (имена файлов надо указывать с расщирением)
     * @return возвращает строку в которой описано кол-во удачных и неудачных переименований
     */
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
            else {
                oldName = JOptionPane.showInputDialog("Enter a new name for: \n " + files[i].getPath());
                if(oldName == null)
                    continue;
            }

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
        place = pos.getPosition();
        pos.setPosition(++place);
        renameList.add(map);
    return "Quantity of renamed files is " + positive + "\n"
            + "Quantity of unrenamed files is " + negative;

    }

    /**
     * Изменение регистра файлов
     * @param file массив типа File, содержащий пути к выделенным ранее
     *             элементам дерева
     * @param bln Параметр, отвечающий за выбор типа переименования
     *            true - верхний регистр
     *            false - нижний регистр
     * @return возвращает строку в которой описано кол-во удачных и неудачных переименований
     */
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
        place = pos.getPosition();
        pos.setPosition(++place);
        renameList.add(map);
        result = "Quantity of renamed files is " + positive + "\n"
                + "Quantity of unrenamed files is " + negative;
        return result;
    }

    /**
     * Установка значения setEnabled(boolean) для набора кнопок
     * @param bln Параметр, отвечающий за включение или выкючение кнопок
     *            true - кнопки включены
     *            false - кнопки выключены
     * @param buttons набор кнопок
     */
    void setButonsBool(boolean bln, JButton... buttons){
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setEnabled(bln);
        }
    }

    /**
     * Установка подсказок для набора кнопок
     * @param bln Параметр, отвечающий за тип подсказки
     *            true - подсказка для работающей кнопки
     *            false - подсказка для не работающей кнопки
     */
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

    /**
     * установка lookandFeel для программы
     */
    public static void setLookAndFeelForProgram() {
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

    /**
     * Добавление слушателя дерева для установления факта
     * выделения его элемента
     * @param tree прослушиваемое дерево
     */
    void addSelectListener(JTree tree){
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                //Получение пути выделенного файла, для того чтобы узнать время создания и посл изменения
                path = treePathtoPath(e.getPath());
                setButonsBool(true, rename, up, down, renameWithNames, CreationTime, LastFormatTime);
                setToolTips(true);

                //заполнение полей времения создания и последней модификации файла/директории
               FileTime timeModified = getPathLastModifiedTime(path);
               fieldChangeTime.setText(createDateFormat(timeModified));
               FileTime timeCreation = getPathCreationTime(path);
               fieldCreationTime.setText(createDateFormat(timeCreation));
            }
        });
    }

    /**
     * Получение времени создания файла по обьекту типа Path
     * @param path путь к файлу
     * @return время создания файла
     */
    private FileTime getPathCreationTime(Path path) {
        FileTime createTime = null;

        try {
            BasicFileAttributes att = Files.readAttributes(path, BasicFileAttributes.class);
            createTime = att.creationTime();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return createTime;
    }

    /**
     * Получение времени последенего редактирования файла по обьекту типа Path
     * @param path путь к файлу
     * @return время последнего редактирования файла
     */
    private FileTime getPathLastModifiedTime(Path path) {
        FileTime lastModified = null;
        try {
            lastModified = Files.getLastModifiedTime(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lastModified;
    }

    /**
     * Превращение обьекта типа FileTime к строке
     * @param time обьект для трансформации
     * @return строковое представление даты
     */
    private String createDateFormat(FileTime time) {
        Date date = new Date(time.toMillis());
        SimpleDateFormat formatDate = new SimpleDateFormat("d.MM.yyyy");
        return formatDate.format(date);
    }

    /**
     * Редактирование времени создания и последнего редактирования файла
     * @param attr аттрибут, который мы изменям
     *             creationTime - изменение времени создания
     *             lastModifiedTime - изменение времени последнего редактирования
     * @param field поле из которого берем новую дату
     *              fieldCreationTime - поле слздания файла
     *              fieldChangeTime - поле последенего редактирования файла
     * @param time старое время
     * @return возвращает строку с удачным или неудачым результатом и
     *          дополнительной информацией
     */
    private String changeTime(String attr, JTextField field, String time) {
        String result = field.getText();
        String oldDate = time;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Path returnedPath = null;

        try {
            Date date = dateFormat.parse(result);
            long mills = date.getTime();
            FileTime fileTime = FileTime.fromMillis(mills);
            returnedPath = Files.setAttribute(path, attr, fileTime);
        } catch (IOException e ) {
            e.printStackTrace();
        }catch (ParseException e){
            result = "wrong date format";
        }
        String temp = "File name: " + returnedPath.getFileName() + "\n"
                + "Path: " + path + "\n"
                + "Date change from " + oldDate + " to " + result;
        return temp;
    }

    /**
     * Реализацию слушателя для всех кнопок приложения
      * @param e
     */
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
                         result = toRedo();
                        break;
                    case "up":
                        result = toCase(treepathToFile(paths), true);
                        break;
                    case "down":
                        result = toCase(treepathToFile(paths), false);
                        break;
                    case "undo":
                        result = toUndo();
                        break;
                    case "Change":
                        result = changeTime("creationTime", fieldCreationTime, createDateFormat(getPathCreationTime(path)));
                        break;
                    case "lastModifiedTime":
                        result = changeTime("lastModifiedTime", fieldChangeTime, createDateFormat(getPathLastModifiedTime(path)));
                        break;
                }
                JOptionPane.showMessageDialog(window, result);

                //Удаление дерева и замена его на новое
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


    public Renamer(File file) {
        super("Renamer");
        window = this;
        this.file = file;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLookAndFeelForProgram();
        setLocationRelativeTo(null);

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
        CreationTime = new JButton("Change");
        LastFormatTime = new JButton("Change");
        LastFormatTime.setActionCommand("lastModifiedTime");
        setButonsBool(false, rename, up, down, renameWithNames, undo, redo, CreationTime, LastFormatTime);
        setToolTips(false);

        //Labels
        timeOFCreationName = new JLabel("Creation Time");
        lastChangingName = new JLabel("Last Changing");

        //TextFields
        fieldCreationTime = new JTextField("");
        fieldCreationTime.setHorizontalAlignment(JTextField.CENTER);
        fieldChangeTime = new JTextField("");
        fieldChangeTime.setHorizontalAlignment(JTextField.CENTER);

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

        //GroupLayout для нормального отображения поля с измененным временем
        GroupLayout groupLayout = new GroupLayout(rightUpPanel);
        rightUpPanel.setLayout(groupLayout);
        groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
                .addGroup(
                    groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(timeOFCreationName)
                        .addComponent(fieldCreationTime)
                        .addComponent(CreationTime)
                )
                .addGroup(
                        groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(lastChangingName)
                            .addComponent(fieldChangeTime)
                            .addComponent(LastFormatTime)
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
                                .addComponent(CreationTime)
                                .addComponent(LastFormatTime)
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
        CreationTime.addActionListener(this);
        LastFormatTime.addActionListener(this);

        //Custom listener for tree. Do it because it repeat in rename.actionListener
        addSelectListener(tree);
        pack();
        setVisible(true);
    }

    //inner class for undo and redo Enable/unEnable states

    /**
     * внутренний класс для уведомления кнопок undo и redo о том, можно ли
     * их использовать (типа Observer)
     */
    class Position{
        private int position = -1;

        public int getPosition(){
            return position;
        }

        public void setPosition(int position) {
            this.position = position;

            if(position == -1)
                undo.setEnabled(false);
            else
                undo.setEnabled(true);

            if(position >= renameList.size()-1)
                redo.setEnabled(false);
            else
                redo.setEnabled(true);
        }
    }

    public static void main(String[] args) {
        new Renamer(new File("C:\\Users\\au\\Desktop\\REC"));
    }
}
