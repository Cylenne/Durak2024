import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimerTester {
    public static void main(String[] args) throws InterruptedException {
        TestGUI testGUI = new TestGUI(); // Create an instance of TestGUI

        ActionListener taskPerformer = new ActionListener() {
            int count = 1; // Initialize a counter

            @Override
            public void actionPerformed(ActionEvent e) {
                // Update the GUI label with the current count
                testGUI.updateGUI(count);

                // Increment the counter
                count++;

                // Stop the timer after 5 updates
                if (count > 5) {
                    ((Timer)e.getSource()).stop();
                }
            }
        };

        Timer timer = new Timer(1000, taskPerformer); // Use 1000 milliseconds (1 second) delay
        timer.setRepeats(true);
        timer.start();

        Thread.sleep(6000); // Sleep for 6 seconds to allow the timer to run
    }
}
