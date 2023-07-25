import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class SpamFilterClientGUI {
    private JFrame frame;
    private JTextField emailTextField;
    private JTextArea resultTextArea;
    private JButton submitButton;
    private JList<String> emailList;
    private DefaultListModel<String> emailListModel;

    public void createAndShowGUI() {
        frame = new JFrame("Email Spam Filter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new BorderLayout());
        frame.add(inputPanel, BorderLayout.NORTH);

        emailTextField = new JTextField(30);
        inputPanel.add(emailTextField, BorderLayout.CENTER);

        submitButton = new JButton("Submit");
        inputPanel.add(submitButton, BorderLayout.EAST);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitEmail();
            }
        });

        emailListModel = new DefaultListModel<>();
        emailList = new JList<>(emailListModel);
        JScrollPane scrollPane = new JScrollPane(emailList);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void submitEmail() {
        String emailContent = emailTextField.getText();
        if (emailContent.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter email content.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Socket socket = new Socket("localhost", 5555)) {
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            output.println(emailContent);

            String spamStatus = input.readLine();
            String spamStatusText = "unknown";

            if ("0".equals(spamStatus)) {
                spamStatusText = "not_spam";
            } else if ("1".equals(spamStatus)) {
                spamStatusText = "spam";
            }

            emailListModel.addElement("Email: " + emailContent + " | Status: " + spamStatusText);

            emailTextField.setText("");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error connecting to the server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}