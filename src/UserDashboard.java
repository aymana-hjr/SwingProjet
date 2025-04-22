import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UserDashboard {
    JFrame frame = new JFrame();
    private JLabel timeLabel;
    private Timer timer;
    private int userId;

    public UserDashboard(int userId) {
        this.userId = userId;
        String[] userDetails = getUserDetails(userId);
        String nom = userDetails[0] != null ? userDetails[0] : "Nom inconnu";
        String prenom = userDetails[1] != null ? userDetails[1] : "Prénom inconnu";
        String numero = userDetails[2] != null ? userDetails[2] : "Numéro inconnu";
        String email = userDetails[3] != null ? userDetails[3] : "Email inconnu";

        frame.setTitle("User Dashboard");
        frame.setSize(800, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Panel d'arrière-plan avec image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon icon = new ImageIcon("C:\\Users\\HP\\Desktop\\Cours Java\\exemple d'execution\\Projet java s2\\src\\images\\appartement2.jpg");
                Image image = icon.getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                g.setColor(new Color(0, 0, 0, 100));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());

        // Panel principal
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Date et heure
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        startTimer();

        // Informations utilisateur
        JLabel userLabel = new JLabel("Bienvenue, " + prenom + " " + nom);
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userLabel.setForeground(Color.WHITE);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel numeroLabel = new JLabel("Numéro : " + numero);
        numeroLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        numeroLabel.setForeground(Color.WHITE);
        numeroLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emailLabel = new JLabel("Email : " + email);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Titre
        JLabel welcomeLabel = new JLabel("User Dashboard");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 30));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Boutons
        JButton viewHousesButton = createStyledButton("Browse Available Houses");
        JButton myReservationsButton = createStyledButton("My Reservations");
        JButton myProfileButton = createStyledButton("My Profile");
        JButton myFavoritesButton = createStyledButton("House Feedback");
        JButton contactButton = createStyledButton("Contact Support");
        JButton logoutButton = createStyledButton("Logout");

        // Actions des boutons
        viewHousesButton.addActionListener(e -> {
            frame.dispose();
            new browseHouses(userId); // Passer l'ID utilisateur à la classe browseHouses
        });
        myReservationsButton.addActionListener(e -> {
            frame.dispose();
            new MyReservations(userId);
        });

        myProfileButton.addActionListener(e -> openProfileEditor());
        myFavoritesButton.addActionListener(e ->  openDonnerAvisDialog());
        contactButton.addActionListener(e -> contactSupport());
        logoutButton.addActionListener(e -> logout());

        // Ajout des éléments au panel
        contentPanel.add(timeLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(userLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(numeroLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(emailLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(welcomeLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        contentPanel.add(viewHousesButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(myReservationsButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(myProfileButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(myFavoritesButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(contactButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(logoutButton);

        backgroundPanel.add(contentPanel);
        frame.add(backgroundPanel);
        frame.setVisible(true);
    }

    private String[] getUserDetails(int userId) {
        String[] userDetails = new String[4]; // [nom, prenom, numero, email]
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/LogeFacile", "root", "Aymane2004@")) {
            String query = "SELECT nom, prenom, numero, email FROM users WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        userDetails[0] = rs.getString("nom");
                        userDetails[1] = rs.getString("prenom");
                        userDetails[2] = rs.getString("numero");
                        userDetails[3] = rs.getString("email");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Erreur lors du chargement des informations utilisateur : " + e.getMessage(), "Erreur Base de données", JOptionPane.ERROR_MESSAGE);
        }
        return userDetails;
    }


    private void openProfileEditor() {
        JFrame profileFrame = new JFrame("Edit Profile");
        profileFrame.setSize(450, 450);
        profileFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        profileFrame.setLocationRelativeTo(null);

        JPanel profilePanel = new JPanel(new BorderLayout());
        profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        profilePanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Edit Your Profile");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Form fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel[] labels = {
                new JLabel("First Name:"),
                new JLabel("Last Name:"),
                new JLabel("Phone Number:"),
                new JLabel("Email:"),
                new JLabel("Password:")
        };

        for (JLabel label : labels) {
            label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        }

        JTextField nameField = new JTextField(20);
        JTextField surnameField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);

        JTextField[] fields = { nameField, surnameField, phoneField, emailField };
        for (JTextField field : fields) {
            field.setFont(new Font("SansSerif", Font.PLAIN, 14));
            field.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        }

        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        String[] fieldNames = { "First Name", "Last Name", "Phone", "Email", "Password" };
        Component[] inputFields = { nameField, surnameField, phoneField, emailField, passwordField };

        for (int i = 0; i < fieldNames.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0;
            formPanel.add(labels[i], gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            formPanel.add(inputFields[i], gbc);
        }

        // Save button
        JButton saveButton = new JButton("Save");
        saveButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        saveButton.setBackground(new Color(41, 128, 185));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(saveButton);

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String surname = surnameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (name.isEmpty() || surname.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(profileFrame, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                updateUserProfile(name, surname, phone, email, password);
                profileFrame.dispose();
            }
        });

        // Add components to the main panel
        profilePanel.add(titleLabel, BorderLayout.NORTH);
        profilePanel.add(formPanel, BorderLayout.CENTER);
        profilePanel.add(buttonPanel, BorderLayout.SOUTH);

        profileFrame.add(profilePanel);
        profileFrame.setVisible(true);

        // Load user data
        loadUserProfile(userId, nameField, surnameField, phoneField, emailField, passwordField);
    }


    private void loadUserProfile(int userId, JTextField nameField, JTextField surnameField, JTextField phoneField,
                                 JTextField emailField, JPasswordField passwordField) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/LogeFacile", "root", "Aymane2004@")) {
            String query = "SELECT nom, prenom, numero, email, password FROM users WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        nameField.setText(rs.getString("nom"));
                        surnameField.setText(rs.getString("prenom"));
                        phoneField.setText(rs.getString("numero"));
                        emailField.setText(rs.getString("email"));
                        passwordField.setText(rs.getString("password"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading profile: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateUserProfile(String name, String surname, String phone, String email, String password) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/LogeFacile", "root", "Aymane2004@")) {
            String query = "UPDATE users SET nom = ?, prenom = ?, numero = ?, email = ?, password = ? WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, name);
                pstmt.setString(2, surname);
                pstmt.setString(3, phone);
                pstmt.setString(4, email);
                pstmt.setString(5, password);
                pstmt.setInt(6, userId);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(frame, "Profile updated successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to update profile.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error updating profile: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDonnerAvisDialog() {
        JFrame avisFrame = new JFrame("Donner un Avis");
        avisFrame.setSize(500, 450);
        avisFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        avisFrame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Donner un Avis");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel maisonLabel = new JLabel("Sélectionnez une maison :");
        JLabel noteLabel = new JLabel("Note (1-5) :");
        JLabel commentaireLabel = new JLabel("Commentaire :");

        JComboBox<String> maisonComboBox = new JComboBox<>();
        loadUserHouses(maisonComboBox);

        SpinnerNumberModel noteModel = new SpinnerNumberModel(1, 1, 5, 1);
        JSpinner noteSpinner = new JSpinner(noteModel);

        JTextArea commentaireArea = new JTextArea(5, 20);
        commentaireArea.setLineWrap(true);
        commentaireArea.setWrapStyleWord(true);
        commentaireArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        commentaireArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JScrollPane commentaireScroll = new JScrollPane(commentaireArea);
        commentaireScroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        maisonComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        noteSpinner.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Layout
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(maisonLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(maisonComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(noteLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(noteSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(commentaireLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(commentaireScroll, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Submit Button
        JButton submitButton = new JButton("Soumettre");
        submitButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        submitButton.setBackground(new Color(39, 174, 96));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submitButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(submitButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Submit Action
        submitButton.addActionListener(e -> {
            String selectedMaison = (String) maisonComboBox.getSelectedItem();
            if (selectedMaison == null) {
                JOptionPane.showMessageDialog(avisFrame, "Aucune maison sélectionnée.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int maisonId = Integer.parseInt(selectedMaison.split(" - ")[0]);
            int note = (int) noteSpinner.getValue();
            String commentaire = commentaireArea.getText().trim();

            if (commentaire.isEmpty()) {
                JOptionPane.showMessageDialog(avisFrame, "Le commentaire ne peut pas être vide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            } else {
                submitAvis(maisonId, note, commentaire);
                avisFrame.dispose();
            }
        });

        avisFrame.add(mainPanel);
        avisFrame.setVisible(true);
    }


    private void loadUserHouses(JComboBox<String> maisonComboBox) {
        // Charger les maisons réservées par l'utilisateur
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/LogeFacile", "root", "Aymane2004@")) {
            String query = "SELECT DISTINCT m.maison_id, m.titre " +
                    "FROM maisons m " +
                    "INNER JOIN reservations r ON m.maison_id = r.maison_id " +
                    "WHERE r.user_id = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        int maisonId = rs.getInt("maison_id");
                        String titre = rs.getString("titre");
                        maisonComboBox.addItem(maisonId + " - " + titre);
                    }


                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Erreur lors du chargement des maisons : " + e.getMessage(), "Erreur Base de données", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void submitAvis(int maisonId, int note, String commentaire) {
        // Enregistrer l'avis dans la base de données
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/LogeFacile", "root", "Aymane2004@")) {
            String query = "INSERT INTO avis (maison_id, user_id, note, commentaire) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, maisonId);
                pstmt.setInt(2, userId);
                pstmt.setInt(3, note);
                pstmt.setString(4, commentaire);

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Avis soumis avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Erreur lors de la soumission de l'avis : " + e.getMessage(), "Erreur Base de données", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void contactSupport() {
        JOptionPane.showMessageDialog(null, "<html><div style='text-align: center;'><h2 style='color: #4682B4;'>Contactez-nous</h2>" +
                "<p>Email: logefacile4@gmail.com<br>Téléphone: 05 00 92 21 81</p></div></html>", "Contact", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(frame, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            frame.dispose();
            new LoginPage();
        }
    }

    private void startTimer() {
        timer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            timeLabel.setText("Current Date and Time (UTC): " + sdf.format(new Date()));
        });
        timer.start();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(200, 50));
        button.setMaximumSize(new Dimension(200, 50));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
            }
        });
        return button;
    }
}