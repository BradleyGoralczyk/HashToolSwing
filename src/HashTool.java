import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.InvocationTargetException;

public class HashTool extends JFrame
{
    public HashTool()
    {
        setTitle("Hash Tool");
        getContentPane().setLayout(null);
        setBounds(100, 100, 180, 140);

        JButton b = makeButton();
        b.addActionListener(new FileChooserActionListener(this));

        add(b);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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

class FileChooserActionListener implements ActionListener
{
    private final Component mParent;
    private final JFileChooser mFileChooser = new JFileChooser();
    private InputStream mInputStream;

    public FileChooserActionListener(Component parent)
    {
        this.mParent = parent;
    }

    public void actionPerformed(ActionEvent event)
    {
        if (mFileChooser.showOpenDialog(mParent) != JFileChooser.APPROVE_OPTION)
        {
            System.out.println("Open command cancelled by user." + '\n');
            return;
        }
        final File file = mFileChooser.getSelectedFile();
        System.out.println("Opening: " + file.getName() + ".");
        try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr))
        {
            System.out.println(br.readLine());
        }
        catch (IOException e)
        {
            System.out.println("Failed to open " + file.getName() + ".");
        }
    }
}