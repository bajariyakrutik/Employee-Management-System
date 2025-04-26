package view;

import controller.EmployeeController;
import model.UserManager;
import model.DatabaseManager;
import util.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * LoginGUI provides a graphical user interface for user authentication.
 * Updated to show information about employee accounts.
 */
public class LoginGUI {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private EmployeeController controller;
    private final Logger logger = Logger.getInstance();
    
    /**
     * Constructs a LoginGUI.
     * 
     * @param controller the EmployeeController
     */
    public LoginGUI(EmployeeController controller) {
        this.controller = controller;
        
        // Create the main frame
        frame = new JFrame("Employee Management System - Login");
        frame.setSize(700, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);  // Center on screen
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create header panel
        JPanel headerPanel = new JPanel();
        JLabel titleLabel = new JLabel("Employee Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create login panel
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username label and field
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        usernameField = new JTextField(15);
        loginPanel.add(usernameField, gbc);
        
        // Password label and field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        loginPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        passwordField = new JPasswordField(15);
        loginPanel.add(passwordField, gbc);
        
        // Login button
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        loginPanel.add(loginButton, gbc);
        
        // Exit button
        gbc.gridx = 2;
        gbc.gridy = 2;
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        loginPanel.add(exitButton, gbc);
        
        // Add instructions
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        JLabel instructionsLabel = new JLabel("<html><br>Default admin/manager logins:<br>" +
                                           "- Username: admin, Password: admin123<br>" +
                                           "- Username: manager, Password: manager123<br><br>" +
                                           "For employee logins:<br>" +
                                           "Username is the employee's name (lowercase with underscores instead of spaces)<br>" +
                                           "Default password is the employee ID followed by the first 3 letters of their name<br><br>" +
                                           "Example: For employee 'John Smith' with ID 101:<br>" +
                                           "- Username: john_smith<br>" +
                                           "- Password: 101joh</html>");
        loginPanel.add(instructionsLabel, gbc);
        
        // Find all accounts button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        JButton showAccountsButton = new JButton("Show All Account Info");
        showAccountsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAllAccounts();
            }
        });
        loginPanel.add(showAccountsButton, gbc);
        
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        
        // Create footer panel
        JPanel footerPanel = new JPanel();
        JLabel copyrightLabel = new JLabel("© 2023 Krutik Bajariya");
        footerPanel.add(copyrightLabel);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        frame.add(mainPanel);
        frame.setVisible(true);
        
        // Set focus to username field
        usernameField.requestFocusInWindow();
        
        // Add enter key listener to password field
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
    }
    
    /**
     * Attempts to log in with the provided credentials.
     */
    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter both username and password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        UserManager userManager = UserManager.getInstance();
        if (userManager.authenticate(username, password)) {
            logger.info("User logged in: " + username);
            frame.dispose();
            new EmployeeGUI(controller);
        } else {
            logger.warning("Login failed for username: " + username);
            JOptionPane.showMessageDialog(frame, "Invalid username or password", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
    
    /**
     * Shows information about all user accounts in the system.
     * This is a convenience method for testing/demonstration purposes.
     * In a real system, this would not be available.
     */
    private void showAllAccounts() {
        // Get all users from database
        DatabaseManager dbManager = DatabaseManager.getInstance();
        StringBuilder accountInfo = new StringBuilder("<html><h3>User Accounts</h3><table border='1'>");
        
        // Table header
        accountInfo.append("<tr><th>Username</th><th>Password</th><th>Role</th><th>Employee ID</th></tr>");
        
        // Get all users
        dbManager.getAllUsers().forEach(user -> {
            accountInfo.append("<tr>");
            accountInfo.append("<td>").append(user.getUsername()).append("</td>");
            accountInfo.append("<td>").append(user.getPassword()).append("</td>");
            accountInfo.append("<td>").append(user.getRole()).append("</td>");
            accountInfo.append("<td>").append(user.getEmployeeId() != null ? user.getEmployeeId() : "N/A").append("</td>");
            accountInfo.append("</tr>");
        });
        
        accountInfo.append("</table></html>");
        
        // Create and display the dialog
        JDialog dialog = new JDialog(frame, "User Accounts", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(frame);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel label = new JLabel(accountInfo.toString());
        JScrollPane scrollPane = new JScrollPane(label);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}