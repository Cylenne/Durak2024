import javax.swing.*;

public class TestGUI extends JFrame {
    JLabel label;

    public TestGUI() {
        super("Test GUI");
        label = new JLabel("TEST");
        getContentPane().add(label);
        setSize(200, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void updateGUI(int i) {
        // Update the label text with the current count
        label.setText("TEST " + i);
    }
}
