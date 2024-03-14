package GUI;

import Player.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.SwingConstants;

import static Card.Card.resizeImageIcon;

public class StartingScreen {

    private PlayerCreation playerCreation;

    private JFrame frame;
    private JComboBox<String> numberOfPlayersSelectionBox;
    private JTable playerTypeTable;
    private JButton startButton;
    JPanel centerPanel;

    private OnPlayersReadyListener callback;

    public interface OnPlayersReadyListener {
        void onPlayersReady(List<Player> players);
    }

    public StartingScreen(OnPlayersReadyListener callback) {
        this.callback = callback;
    }

    public void setupStartingScreen() {

        List<Player> players = new ArrayList<>();
        playerCreation = new PlayerCreation();

        createFrame();
        createWelcomeMessageAndNumberOfPlayersSelectionBox();
        createPlayerTypesTable();
        createSpadesArt();
        createButtons(players);
        frame.setVisible(true);
        centerFrame();

    }

    private void createFrame() {
        frame = new JFrame("Durak");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 400);
        frame.setLayout(new BorderLayout());

        ImageIcon frameIcon = new ImageIcon("Images/clubs.png");
        Image iconImage = frameIcon.getImage();
        frame.setIconImage(iconImage);
    }

    private void createWelcomeMessageAndNumberOfPlayersSelectionBox() {
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));

        JPanel welcomeMessagePanel = new JPanel();
        welcomeMessagePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        JLabel welcomeMessageLabel = new JLabel("Welcome to the Russian card game, Durak! Please select the total number of players:");
        welcomeMessagePanel.add(welcomeMessageLabel);
        welcomePanel.add(welcomeMessagePanel);

        JPanel numPlayersPanel = new JPanel();
        numPlayersPanel.setLayout(new FlowLayout());
        JLabel numPlayersLabel = new JLabel("Number of Players:");
        numPlayersPanel.add(numPlayersLabel);

        String[] numPlayerOptions = {"2", "3", "4"};
        numberOfPlayersSelectionBox = new JComboBox<>(numPlayerOptions);
        numPlayersPanel.add(numberOfPlayersSelectionBox);
        welcomePanel.add(numPlayersPanel);

        frame.add(welcomePanel, BorderLayout.NORTH);

        numberOfPlayersSelectionBox.addActionListener(e -> {
            updatePlayerTable();
            startButton.setEnabled(true); // Enable the "Start Game" button when the number of players is selected
        });
    }

    private void createPlayerTypesTable() {
        // center panel for player types table and spades image
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        frame.add(centerPanel, BorderLayout.CENTER);

        playerTypeTable = new JTable();
        playerTypeTable.setDefaultRenderer(Object.class, new CenteredCellRenderer());
        JScrollPane scrollPane = new JScrollPane(playerTypeTable);
        centerPanel.add(scrollPane);
    }

    private void createSpadesArt() {
        ImageIcon spadesImage = new ImageIcon("Images/coolClubs.png");
        spadesImage = resizeImageIcon(spadesImage, 100, 100);
        JLabel spadesImageLabel = new JLabel(spadesImage);
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(spadesImageLabel, BorderLayout.CENTER);
        centerPanel.add(imagePanel, BorderLayout.CENTER);
    }

    private void createButtons(List<Player> players) {
        // bottom panel for start game and game rules buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        frame.add(bottomPanel, BorderLayout.SOUTH);

        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton gameRulesButton = new JButton("Game Rules");
        gameRulesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameRulesButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, gameRulesButton.getPreferredSize().height));
        gameRulesButton.addActionListener(e -> GameRulesScreen.showGameRules(frame));
        bottomPanel.add(gameRulesButton);
        bottomPanel.add(Box.createVerticalStrut(10)); // add some spacing between buttons

        startButton = new JButton("Start Game");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT); // center align
        startButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, startButton.getPreferredSize().height)); // set button width to match frame width
        startButton.setEnabled(false); // otherwise user could start the game (crash) without selecting the number of players
        startButton.addActionListener(e -> {
            // retrieves the number of players selected and parses it into String then int
            int numPlayers = Integer.parseInt((String) numberOfPlayersSelectionBox.getSelectedItem());

            // retrieves player types selected for each player and adds it to an array
            String[] playerTypes = new String[numPlayers];
            for (int i = 0; i < numPlayers; i++) {
                playerTypes[i] = (String) playerTypeTable.getValueAt(i, 1);
            }

            List<HumanPlayer> humanPlayers = new ArrayList<>();
            List<ComputerPlayer> computerPlayers = new ArrayList<>();
            // creates HumanPlayer and ComputerPlayer instances based on the array of player types
            for (int i = 0; i < numPlayers; i++) {
                if ("Human".equals(playerTypes[i])) {
                    humanPlayers.add(playerCreation.createHumanPlayer(i));
                } else if ("Computer".equals(playerTypes[i])) {
                    computerPlayers.add(playerCreation.createComputerPlayer(i));
                }
            }

            players.addAll(humanPlayers);
            players.addAll(computerPlayers);

            callback.onPlayersReady(players);
            // Handle the game initialization based on selected players
            frame.dispose(); // close starting screen window
        });
        bottomPanel.add(startButton);
    }

    private void updatePlayerTable() {
        int numPlayers = Integer.parseInt((String) numberOfPlayersSelectionBox.getSelectedItem());
        String[] columnNames = {"Player", "Type"};
        Object[][] data = new Object[numPlayers][2];

        // this code was used for the possibility of multiple human players
//        for (int i = 0; i < numPlayers; i++) {
//            data[i][0] = "Player " + (i + 1);
//            data[i][1] = "Computer"; // Default value
//        }

        // first player is human, the rest are bots
        data[0][0] = "Player 1";
        data[0][1] = "Human"; // CHANGE THIS TO HUMAN AS SOON AS THE CODE FOR HUMAN PLAYER IS WRITTEN

        for (int i = 1; i < numPlayers; i++) {
            data[i][0] = "Player " + (i + 1);
            data[i][1] = "Computer";
        }


        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 1 ? String.class : Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0 && column != 1; // Make only the "Type" column editable
                // REMOVE "&& column !=1" if you want to make the second column editable again!!!
            }
        };

        playerTypeTable.setModel(model);
        playerTypeTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JComboBox<>(new String[]{"Human", "Computer"})));
    }

    // puts window in the middle of your screen
    private void centerFrame() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize(); // grabs your display's dimensions, both width and height
        int x = (dim.width - frame.getSize().width) / 2;
        int y = (dim.height - frame.getSize().height) / 2;
        frame.setLocation(x, y);
    }

    private static class CenteredCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            return this;
        }
    }


}
