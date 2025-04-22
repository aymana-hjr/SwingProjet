import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginPage implements ActionListener {
    JFrame frame = new JFrame();
    JButton loginButton;
    JButton registerButton;
    JTextField emailField;
    JPasswordField passwordField;

    LoginPage() {
        // Frame properties
        frame.setVisible(true);
        frame.setTitle("LogeFacile");
        frame.setSize(700, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Background panel with custom background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon icon = new ImageIcon("C:\\Users\\HP\\Desktop\\Cours Java\\exemple d'execution\\Projet java s2\\src\\images\\appartement2.jpg");
                Image image = icon.getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                g.setColor(new Color(0, 0, 0, 100)); // Semi-transparent overlay
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new GridBagLayout()); // Use GridBagLayout to center components

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Margin between components
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titlePart1 = new JLabel("Bienvenue à LogeFacile");
        titlePart1.setFont(new Font("Arial", Font.BOLD, 30));
        titlePart1.setForeground(Color.WHITE);
        titlePart1.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundPanel.add(titlePart1, gbc);

        // Email label and field
        gbc.gridy++;
        gbc.gridwidth = 1;
        JLabel emailLabel = new JLabel("Email :");
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        emailLabel.setForeground(Color.WHITE);
        backgroundPanel.add(emailLabel, gbc);

        gbc.gridx++;
        emailField = new JTextField();
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        emailField.setMaximumSize(new Dimension(400, 30));
        emailField.setPreferredSize(new Dimension(200, 30));
        backgroundPanel.add(emailField, gbc);

        // Password label and field
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel passwordLabel = new JLabel("Mot de passe :");
        passwordLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        passwordLabel.setForeground(Color.WHITE);
        backgroundPanel.add(passwordLabel, gbc);

        gbc.gridx++;
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(400, 30));
        passwordField.setPreferredSize(new Dimension(200, 30));
        backgroundPanel.add(passwordField, gbc);

        // Login button
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        loginButton = new JButton("Connexion");
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginButton.setBackground(new Color(52, 152, 219));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        loginButton.addActionListener(this);
        backgroundPanel.add(loginButton, gbc);

        // Register button
        gbc.gridy++;
        registerButton = new JButton("S'inscrire");
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        registerButton.setBackground(new Color(46, 204, 113));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        registerButton.addActionListener(this);
        backgroundPanel.add(registerButton, gbc);

        frame.add(backgroundPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            frame.dispose();
            new Inscrire();
        } else if (e.getSource() == loginButton) {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            // Vérification des identifiants et récupération de l'ID utilisateur
            int userId = verifyCredentials(email, password);
            if (userId > 0) { // Si la connexion est réussie
                JOptionPane.showMessageDialog(frame, "Connexion réussie !");
                frame.dispose();
                new UserDashboard(userId); // Passer l'ID utilisateur au tableau de bord
            } else { // Si la connexion échoue
                JOptionPane.showMessageDialog(frame, "Email ou mot de passe invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int verifyCredentials(String email, String password) {
        String dbUrl = "jdbc:mysql://localhost:3306/LogeFacile";
        String dbUsername = "root";
        String dbPassword = "Aymane2004@";
        int userId = -1;

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String query = "SELECT id FROM users WHERE email = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userId = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Erreur de la base de données : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return userId;
    }
}