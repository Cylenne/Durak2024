package GUI;

import javax.swing.*;
import java.awt.*;

public class DialogUtils {

    private JDialog dialog;
    private JPanel humanCardsPanel;
    private JButton selectButton;

    public DialogUtils(JDialog dialog, JPanel humanCardsPanel, JButton selectButton) {
        this.dialog = dialog;
        this.humanCardsPanel = humanCardsPanel;
        this.selectButton = selectButton;
    }

    public void createAndShowDialog() {
        // modal dialog automatically stops the game flow until user action takes place
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        ImageIcon frameIcon = new ImageIcon("Images/clubs.png");
        Image iconImage = frameIcon.getImage();
        dialog.setIconImage(iconImage);

//            dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); // prevent the player from not selecting any cards by closing the dialog box
        // UNCOMMENT THE ABOVE WHEN THE APP IS ALMOST READY

        JPanel centerPanel = new JPanel(new GridBagLayout());
        JLabel message = new JLabel("Your cards:");
        centerPanel.add(message, createConstraints(0, 0, 1, 1, GridBagConstraints.CENTER)); // center the text
        centerPanel.add(humanCardsPanel, createConstraints(0, 1, 1, 1, GridBagConstraints.CENTER));
        centerPanel.add(selectButton, createConstraints(0, 2, 1, 1, GridBagConstraints.CENTER));
        dialog.getContentPane().add(centerPanel, BorderLayout.CENTER);

        dialog.pack(); // sets the size of the dialog to be just large enough to accommodate all of its components
        dialog.setLocationRelativeTo(null); // center the dialog on the screen
        dialog.setVisible(true);
    }

    // used for centering
    public GridBagConstraints createConstraints(int gridx, int gridy, int gridwidth, int gridheight, int anchor) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.gridwidth = gridwidth;
        constraints.gridheight = gridheight;
        constraints.anchor = anchor;
        constraints.insets = new Insets(5, 5, 5, 5); // padding
        return constraints;
    }
}
