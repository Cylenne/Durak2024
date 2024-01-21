import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.SwingConstants;

public class StartingScreen {

    private GameSetup gameSetup;

    private JFrame frame;
    private JComboBox<String> playersComboBox;
    private JTable playerTable;
    private JButton startButton;

    // using callback instead of a return statement to save the created players
    private OnPlayersReadyListener callback;
    private Deck standardDeck;
    public interface OnPlayersReadyListener {
        void onPlayersReady(List<Player> players, Deck standardDeck);
    }
    public StartingScreen(OnPlayersReadyListener callback) {
        this.callback = callback;
    }
    public void setStandardDeck(Deck standardDeck) {
        this.standardDeck = standardDeck;
    }

    public void setupStartingScreen() {

        List<Player> allPlayers = new ArrayList<>();

        gameSetup = new GameSetup();

        frame = new JFrame("Durak");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());

        // Number of players
        JPanel numPlayersPanel = new JPanel();
        numPlayersPanel.setLayout(new FlowLayout());
        JLabel playersLabel = new JLabel("Number of Players:");
        numPlayersPanel.add(playersLabel);

        String[] playerOptions = {"2", "3", "4"};
        playersComboBox = new JComboBox<>(playerOptions);
        numPlayersPanel.add(playersComboBox);
        frame.add(numPlayersPanel, BorderLayout.NORTH);

        playersComboBox.addActionListener(e -> {
            updatePlayerTable();
            startButton.setEnabled(true); // Enable the "Start Game" button when the number of players is selected
        });

        // table for the type of players
        playerTable = new JTable();
        playerTable.setDefaultRenderer(Object.class, new CenteredCellRenderer());
        JScrollPane scrollPane = new JScrollPane(playerTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        startButton = new JButton("Start Game");
        startButton.setEnabled(false); // otherwise user could start the game (crash) without selecting the number of players
        startButton.addActionListener(e -> {
            // retrieves the number of players selected and parses it into String then int
            int numPlayers = Integer.parseInt((String) playersComboBox.getSelectedItem());

            // retrieves player types selected for each player and adds it to an array
            String[] playerTypes = new String[numPlayers];
            for (int i = 0; i < numPlayers; i++) {
                playerTypes[i] = (String) playerTable.getValueAt(i, 1);
            }
//            System.out.println(Arrays.toString(playerTypes));

            List<HumanPlayer> humanPlayers = new ArrayList<>();
            List<ComputerPlayer> computerPlayers = new ArrayList<>();
            // creates HumanPlayer and ComputerPlayer instances based on the array of player types
            for (int i = 0; i < numPlayers; i++) {
                if ("Human".equals(playerTypes[i])) {
                    humanPlayers.add(gameSetup.createHumanPlayer(i));
                } else if ("Computer".equals(playerTypes[i])) {
                    computerPlayers.add(gameSetup.createComputerPlayer(i));
                }
            }
//            System.out.println(humanPlayers);
//            System.out.println(computerPlayers);
            allPlayers.addAll(humanPlayers);
            allPlayers.addAll(computerPlayers);
//            System.out.println(allPlayers);

            callback.onPlayersReady(allPlayers,standardDeck);
            // Handle the game initialization based on selected players
            frame.dispose(); // close starting screen window
        });
        frame.add(startButton, BorderLayout.SOUTH);

        frame.setVisible(true);
        centerFrame();

    }

    private void updatePlayerTable() {
        int numPlayers = Integer.parseInt((String) playersComboBox.getSelectedItem());
        String[] columnNames = {"Player", "Type"};
        Object[][] data = new Object[numPlayers][2];

        for (int i = 0; i < numPlayers; i++) {
            data[i][0] = "Player " + (i + 1);
            data[i][1] = "Computer"; // Default value
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 1 ? String.class : Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Make only the "Type" column editable
            }
        };

        playerTable.setModel(model);
        playerTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JComboBox<>(new String[]{"Human", "Computer"})));
    }

    private void centerFrame() {
        // puts window in the middle of your screen
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
