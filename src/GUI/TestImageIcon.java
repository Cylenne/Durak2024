package GUI;

import Card.*;

import javax.swing.*;
import java.awt.*;

public class TestImageIcon {
    public static void main(String[] args) {

        JFrame frame = new JFrame();

        Card aceSpades = new Card(14, Card.Suit.SPADE);
        ImageIcon aceSpadesImageIcon = aceSpades.toImageIcon();

        if (aceSpadesImageIcon != null) {
            frame.add(new JLabel(aceSpadesImageIcon));
        } else {
            System.out.println("ImageIcon is null");
        }

//        frame.add(new JLabel(new ImageIcon(("Cards\\" + "8" + "_of_" + "spades" + ".png"))));
//        frame.add(new JLabel(new ImageIcon(("Cards\\8_of_spades.png"))));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new FlowLayout());
        frame.setVisible(true);

    }
}
