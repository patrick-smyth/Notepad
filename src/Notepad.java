
import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.security.Key;

public class Notepad  {

    JFrame frame;
    JMenuBar menuBar;
    JMenu file, edit, format;
    JMenuItem open, newFile,save,saveAs,exit;
    JMenuItem undo, cut, copy, paste, selectAll;
    JMenuItem font;
    UndoManager undoManager = new UndoManager();
    InputMap inputMap;
    ActionMap actionMap;
    JPanel panel;

    JFileChooser fileChooser;
    JTextArea textArea;
    Clipboard clip;

    File fileSave;
    AbstractAction saveAction, openAction;

    Notepad() {
        file = new JMenu("File");
        edit = new JMenu("Edit");
        format = new JMenu("Format");

        // File menu items
        open = new JMenuItem("Open");
        newFile = new JMenuItem("New");
        save = new JMenuItem("Save");
        saveAs = new JMenuItem("Save As");
        exit = new JMenuItem("Exit");

        file.add(open);
        file.add(newFile);
        file.add(save);
        file.add(saveAs);
        file.add(exit);

        // Edit menu items
        undo = new JMenuItem("Undo");
        cut = new JMenuItem("Cut");
        copy = new JMenuItem("Copy");
        paste = new JMenuItem("Paste");
        selectAll = new JMenuItem("Select All");

        edit.add(undo);
        edit.add(cut);
        edit.add(copy);
        edit.add(paste);
        edit.add(selectAll);

        // Format menu items
        font = new JMenuItem("Font");
        format.add(font);

        menuBar = new JMenuBar();
        menuBar.add(file);
        menuBar.add(edit);
        menuBar.add(format);

        frame = new JFrame("Notepad");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(700,500));
        frame.setJMenuBar(menuBar);
        frame.setLocationRelativeTo(null);


        panel = new JPanel();
        frame.add(panel);

        panel.setLayout(new BorderLayout());
        textArea = new JTextArea(BorderLayout.CENTER);
        Font font = new Font("Times New Roman", Font.PLAIN,18);
        textArea.setFont(font);
        textArea.setText("");
        textArea.setLineWrap(true);
        textArea.setEditable(true);
        textArea.getDocument().addUndoableEditListener(undoManager);
        panel.add(textArea);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(800,110));
        panel.add(scrollPane,BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);

        // Initialise save file action
        saveAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(fileSave == null) {
                    displaySaveFileChooser();
                }
                try {
                    save(fileSave);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };

        // Initialises open file action
        openAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser = new JFileChooser();
                fileChooser.showOpenDialog(frame);
                try {
                    File file = fileChooser.getSelectedFile();
                    FileReader fr = new FileReader(file);
                    textArea.read(fr,"file");
                    fileSave = file;
                    frame.setTitle(fileSave.getName());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };

        //Initialise input and action maps for key bindings
       inputMap = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
       actionMap = panel.getActionMap();

        fileSetUp(); // sets up file menu
        fileMenuBindingSetUp();
        editSetup(); // sets up edit menu

    }

    private void fileSetUp() {
        // Creates new Notepad window on button click
        newFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Notepad newNotepad = new Notepad();
            }
        });

        // Calls openAction on "Open" button click, which allows user to choose which file to open
        open.addActionListener(openAction);
        // Calls saveAction on "Save" click, which saves current file
        save.addActionListener(saveAction);
        // Save file to new location on button click
        saveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displaySaveFileChooser();
                try {
                    save(fileSave);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });
        // Exits window on button click
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

    }

    private void editSetup() {
         // Initialises undoAction
        AbstractAction undoAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undoManager.undo();
            }
        };

        // Undoes last keystroke when you click undo button
        undo.addActionListener(undoAction);


        // Edit menu key bindings

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK),"undo");
        actionMap.put("undo",undoAction);

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK),"redo");
        actionMap.put("redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undoManager.redo();
            }
        });

    }



    private void fileMenuBindingSetUp() {

        // File menu key bindings
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "save");
        actionMap.put("save", saveAction);

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK),"open");
        actionMap.put("open",openAction);

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK),"new");
        actionMap.put("new", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Notepad notepad = new Notepad();
            }
        });


    }


    private void save(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("File does not exist");
        }

        BufferedWriter wr = new BufferedWriter(new FileWriter(file,false));
        textArea.write(wr);
        wr.close();
        frame.setTitle(fileSave.getName());

    }

    private void displaySaveFileChooser() {
        fileChooser = new JFileChooser();
        fileChooser.showSaveDialog(frame);
        fileChooser.setDialogTitle("Save your txt file");
        fileSave = fileChooser.getSelectedFile();
    }


    public static void main(String[] args) {
        Notepad notepad = new Notepad();
    }
}
