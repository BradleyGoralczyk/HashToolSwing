import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.lang.reflect.InvocationTargetException;

public class HashTool
{
    JFrame mMainFrame;
    JLabel mLabelFileName;
    JTextField mTextFieldCRC32;
    JTextField mTextFieldMD5;
    JTextField mTextFieldSHA1;
    JTextField mTextFieldPiki;

    public static void main(String[] args) throws InvocationTargetException, InterruptedException
    {
        // Swing calls must be run by the event dispatching thread.
        SwingUtilities.invokeAndWait(HashTool::new);
    }

    public HashTool()
    {
        mMainFrame = new JFrame("Hash Tool");
        mMainFrame.setLayout(new FlowLayout());
        // I can't get the vertical layout to look good without a
        // FlowLayout, but that falls apart when the window is resized.
        mMainFrame.setBounds(100, 100, 480, 240);
        mMainFrame.setResizable(false);

        mMainFrame.setJMenuBar(makeMenuBar());
        mLabelFileName  = makeFileNameLabel();
        mTextFieldCRC32 = makeLabeledFieldHBox("CRC32:");
        mTextFieldMD5   = makeLabeledFieldHBox("MD5:");
        mTextFieldSHA1  = makeLabeledFieldHBox("SHA1:");
        mTextFieldPiki  = makeLabeledFieldHBox("Pikmin:");

        mMainFrame.setVisible(true);
        mMainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private JMenuBar makeMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(makeFileMenu());
        menuBar.add(makeHelpMenu());
        return menuBar;
    }

    private JMenu makeFileMenu()
    {
        JMenu menu = new JMenu("File");
        menu.add(makeOpenMenuItem());
        return menu;
    }

    private JMenuItem makeOpenMenuItem()
    {
        final JFileChooser fileChooser = new JFileChooser();

        final JMenuItem menuItem = new JMenuItem("Open");
        menuItem.addActionListener((ActionEvent event) -> {
            if (fileChooser.showOpenDialog(mMainFrame) != JFileChooser.APPROVE_OPTION)
            {
                System.out.println("Open command cancelled by user.");
                return;
            }
            final File file = fileChooser.getSelectedFile();
            System.out.println("Opening: " + file.getName() + ".");
            mLabelFileName.setText(file.getPath());

            // This is probably faster for large files, right?  Not even going to benchmark this.
            (new Thread(new RunnableHashJob(file, new HashCRC32(), mTextFieldCRC32))).start();
            (new Thread(new RunnableHashJob(file, new HashMD5()  , mTextFieldMD5  ))).start();
            (new Thread(new RunnableHashJob(file, new HashSHA1() , mTextFieldSHA1 ))).start();
            (new Thread(new RunnableHashJob(file, new HashPiki() , mTextFieldPiki ))).start();
        });
        return menuItem;
    }

    private JMenu makeHelpMenu()
    {
        JMenu menu = new JMenu("Help");
        menu.add(makeAboutMenuItem());
        return menu;
    }

    private JMenuItem makeAboutMenuItem()
    {
        final JMenuItem menuItem = new JMenuItem("About");
        menuItem.addActionListener((ActionEvent event) -> {
            JOptionPane.showMessageDialog(mMainFrame, "  This program was created by Bradley Goralczyk\nand Jakub Niedzielski for ECE 251 (Object Oriented\n    Programming) at Purdue University Northwest.");
        });
        return menuItem;
    }

    private JLabel makeFileNameLabel()
    {
        JLabel label = new JLabel("Select File > Open to hash a file.");
        label.setPreferredSize(new Dimension(400, label.getPreferredSize().height));
        mMainFrame.add(label);
        return label;
    }

    private JTextField makeLabeledFieldHBox(String labelText)
    {
        JTextField textField = new JTextField("", 32);
        textField.setEditable(false);
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(100, label.getPreferredSize().height));

        Box box = Box.createHorizontalBox();
        box.add(label);
        box.add(textField);
        mMainFrame.add(box);

        return textField;
    }

    private JButton makeButton()
    {
        final JButton b = new JButton();
        b.setText("Click me!");
        b.setBounds(40, 40, 100, 30);
        return b;
    }
}

class RunnableHashJob implements Runnable
{
    private final File mFile;
    private final HashInterface mHashInterface;
    private final JTextField mTextField;

    public RunnableHashJob(File file, HashInterface hashInterface, JTextField textField)
    {
        mFile = file;
        mHashInterface = hashInterface;
        mTextField = textField;
    }

    @Override
    public void run()
    {
        mTextField.setText("Now hashing...");
        try (FileInputStream fis = new FileInputStream(mFile))
        {
            mHashInterface.calcHash(fis);
            mTextField.setText(mHashInterface.toString());
        }
        catch (IOException exception)
        {
            // Who cares
        }
    }
}