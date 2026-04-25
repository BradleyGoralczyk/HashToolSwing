import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.lang.reflect.InvocationTargetException;

public class HashTool
{
    JFrame mMainFrame;

    JTextField mTextFieldCRC32;
    JTextField mTextFieldMD5;
    JTextField mTextFieldSHA1;
    JTextField mTextFieldPiki;

    public HashTool()
    {
        mMainFrame = new JFrame("Hash Tool");
        mMainFrame.setLayout(new FlowLayout());
        mMainFrame.setBounds(100, 100, 640, 480);

        mMainFrame.setJMenuBar(makeMenuBar());

        mTextFieldCRC32 = new JTextField("", 32);
        mTextFieldCRC32.setEditable(false);
        JLabel labelCRC32 = new JLabel("CRC32: ");
        labelCRC32.setPreferredSize(new Dimension(100, labelCRC32.getPreferredSize().height));
        mMainFrame.add(labelCRC32);
        mMainFrame.add(mTextFieldCRC32);

        mTextFieldMD5 = new JTextField("", 32);
        mTextFieldMD5.setEditable(false);
        JLabel labelMD5 = new JLabel("MD5: ");
        labelMD5.setPreferredSize(new Dimension(100, labelMD5.getPreferredSize().height));
        mMainFrame.add(labelMD5);
        mMainFrame.add(mTextFieldMD5);

        mTextFieldSHA1 = new JTextField("", 32);
        mTextFieldSHA1.setEditable(false);
        JLabel labelSHA1 = new JLabel("SHA1: ");
        labelSHA1.setPreferredSize(new Dimension(100, labelSHA1.getPreferredSize().height));
        mMainFrame.add(labelSHA1);
        mMainFrame.add(mTextFieldSHA1);

        mTextFieldPiki = new JTextField("", 32);
        mTextFieldPiki.setEditable(false);
        JLabel labelPiki = new JLabel("Piki: ");
        labelPiki.setPreferredSize(new Dimension(100, labelPiki.getPreferredSize().height));
        mMainFrame.add(labelPiki);
        mMainFrame.add(mTextFieldPiki);
//
//        Container contentPane = mMainFrame.getContentPane();
//        contentPane.setLayout(new FlowLayout());
//        contentPane.add();
//        contentPane.add();

//        mMainFrame.pack();
        mMainFrame.setVisible(true);
        mMainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private JMenuBar makeMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(makeFileMenu());
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
                System.out.println("Open command cancelled by user." + '\n');
                return;
            }
            final File file = fileChooser.getSelectedFile();
            System.out.println("Opening: " + file.getName() + ".");

            try
            {
                new Thread()
                new Thread(() throws IOException -> {
                    FileInputStream fis = new FileInputStream(file)
                });
//                final HashCRC32 hashCRC32 = new HashCRC32();
//                mTextFieldCRC32.setText(hashCRC32.calcHash(fis));
//                final HashMD5 hashMD5 = new HashMD5();
//                mTextFieldMD5.setText(hashMD5.calcHash(fis));
                final HashSHA1 hashSHA1 = new HashSHA1();
                mTextFieldSHA1.setText(hashSHA1.calcHash(fis));
//                final HashPiki pikiHash = new HashPiki();
//                mTextFieldPiki.setText(pikiHash.calcHash(fis));
            } catch (IOException e)
            {
                System.out.println("Failed to open " + file.getName() + ".");
            }
        });
        return menuItem;
    }

    private JButton makeButton()
    {
        final JButton b = new JButton();
        b.setText("Click me!");
        b.setBounds(40, 40, 100, 30);
        return b;
    }

    public static void main(String[] args) throws InvocationTargetException, InterruptedException
    {
        // Swing calls must be run by the event dispatching thread.
        SwingUtilities.invokeAndWait(HashTool::new);
    }
}