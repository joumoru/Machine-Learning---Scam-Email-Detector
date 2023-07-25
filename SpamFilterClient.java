import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SpamFilterClient {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SpamFilterClientGUI gui = new SpamFilterClientGUI();
            gui.createAndShowGUI();
        });
    }
}
