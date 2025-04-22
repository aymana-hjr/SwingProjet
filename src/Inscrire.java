import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Inscrire implements ActionListener {
    JFrame frame = new JFrame("LogeFacile - Inscription");
    JButton retourButton;
    JButton registerButton;
    JTextField nomField;
    JTextField prenomField;
    JTextField numeroField;
    JTextField emailField;
    JPasswordField passwordField;
    JPasswordField passwordField2;

    // Enregistre les placeholders pour vérifier si un champ est vraiment rempli
    private final String NOM_PLACEHOLDER = "Entrez votre nom";
    private final String PRENOM_PLACEHOLDER = "Entrez votre prénom";
    private final String NUMERO_PLACEHOLDER = "Entrez votre numéro";
    private final String EMAIL_PLACEHOLDER = "Entrez votre email";
    private final String PASSWORD_PLACEHOLDER = "Entrez un mot de passe";
    private final String CONFIRM_PASSWORD_PLACEHOLDER = "Confirmez votre mot de passe";

    Inscrire() {
        frame.setVisible(true);
        frame.setTitle("LogeFacile - Inscription");
        frame.setSize(700, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        // Panel principal avec image de fond
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon icon = new ImageIcon("C:\\Users\\HP\\Desktop\\Cours Java\\exemple d'execution\\Projet java s2\\src\\images\\appartement2.jpg");
                Image image = icon.getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);

                // Amélioration du dégradé pour plus de lisibilité
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(255, 255, 255, 180),
                        0, getHeight(), new Color(255, 255, 255, 220));
                ((Graphics2D) g).setPaint(gradient);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));
        backgroundPanel.setBorder(new EmptyBorder(25, 50, 25, 50));

        // Logo et Titre avec effet d'ombre
        JLabel title = new JLabel("Créer un nouveau compte");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(41, 128, 185));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Effet d'ombre pour le titre
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Champs de formulaire
        nomField = createStyledTextField(NOM_PLACEHOLDER);
        prenomField = createStyledTextField(PRENOM_PLACEHOLDER);
        numeroField = createStyledTextField(NUMERO_PLACEHOLDER);
        emailField = createStyledTextField(EMAIL_PLACEHOLDER);
        passwordField = createStyledPasswordField(PASSWORD_PLACEHOLDER);
        passwordField2 = createStyledPasswordField(CONFIRM_PASSWORD_PLACEHOLDER);

        // Boutons avec effets de survol
        retourButton = createStyledButton("Retour", new Color(231, 76, 60), new Color(192, 57, 43));
        registerButton = createStyledButton("S'inscrire", new Color(46, 204, 113), new Color(39, 174, 96));

        // Désactiver le bouton d'inscription au démarrage
        registerButton.setEnabled(false);

        retourButton.addActionListener(this);
        registerButton.addActionListener(this);

        // Ajouter les éléments au panneau principal
        backgroundPanel.add(title);
        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Création d'un panel pour le formulaire avec animation
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        formPanel.add(createFormRow("Nom :", nomField));
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        formPanel.add(createFormRow("Prénom :", prenomField));
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        formPanel.add(createFormRow("Téléphone :", numeroField));
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        formPanel.add(createFormRow("Email :", emailField));
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        formPanel.add(createFormRow("Mot de passe :", passwordField));
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        formPanel.add(createFormRow("Confirmation :", passwordField2));

        backgroundPanel.add(formPanel);

        // Panel de boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(retourButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(30, 0)));
        buttonPanel.add(registerButton);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));

        backgroundPanel.add(buttonPanel);

        // Ajouter des écouteurs pour vérifier si tous les champs sont remplis
        addFieldListeners();

        frame.add(backgroundPanel);
    }

    private JPanel createFormRow(String labelText, JComponent field) {
        JPanel rowPanel = new JPanel(new BorderLayout(15, 0));
        rowPanel.setOpaque(false);
        rowPanel.setMaximumSize(new Dimension(600, 50));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(new Color(44, 62, 80));
        label.setPreferredSize(new Dimension(120, 30));

        rowPanel.add(label, BorderLayout.WEST);
        rowPanel.add(field, BorderLayout.CENTER);

        return rowPanel;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setForeground(new Color(189, 195, 199)); // Couleur du placeholder
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(52, 152, 219)), // Bordure inférieure uniquement
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        textField.setMaximumSize(new Dimension(400, 40));
        textField.setText(placeholder);
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(new Color(44, 62, 80)); // Couleur du texte réel
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(new Color(189, 195, 199)); // Couleur du placeholder
                }
            }
        });

        return textField;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setForeground(new Color(189, 195, 199)); // Couleur du placeholder
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(52, 152, 219)), // Bordure inférieure uniquement
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        passwordField.setMaximumSize(new Dimension(400, 40));
        passwordField.setEchoChar((char) 0); // Affiche le texte du placeholder

        passwordField.setText(placeholder);
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (new String(passwordField.getPassword()).equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setEchoChar('\u2022'); // Change à un caractère caché
                    passwordField.setForeground(new Color(44, 62, 80)); // Couleur du texte réel
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (new String(passwordField.getPassword()).isEmpty()) {
                    passwordField.setText(placeholder);
                    passwordField.setEchoChar((char) 0); // Placeholder visible
                    passwordField.setForeground(new Color(189, 195, 199)); // Couleur du placeholder
                }
            }
        });
        return passwordField;
    }

    private JButton createStyledButton(String text, Color backgroundColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Effet de dégradé sur le bouton
                GradientPaint gradient = new GradientPaint(
                        0, 0, backgroundColor,
                        0, c.getHeight(), backgroundColor.darker());
                g2.setPaint(gradient);

                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);

                super.paint(g, c);
            }
        });

        // Effet de survol
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor.darker());
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
        });

        return button;
    }

    // Ajoute des écouteurs à tous les champs pour vérifier si le formulaire est complet
    private void addFieldListeners() {
        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkFormCompletion();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkFormCompletion();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkFormCompletion();
            }
        };

        nomField.getDocument().addDocumentListener(listener);
        prenomField.getDocument().addDocumentListener(listener);
        numeroField.getDocument().addDocumentListener(listener);
        emailField.getDocument().addDocumentListener(listener);
        passwordField.getDocument().addDocumentListener(listener);
        passwordField2.getDocument().addDocumentListener(listener);
    }

    // Vérifie si tous les champs sont remplis et active/désactive le bouton d'inscription
    private void checkFormCompletion() {
        boolean allFieldsFilled =
                !nomField.getText().equals(NOM_PLACEHOLDER) && !nomField.getText().isEmpty() &&
                        !prenomField.getText().equals(PRENOM_PLACEHOLDER) && !prenomField.getText().isEmpty() &&
                        !numeroField.getText().equals(NUMERO_PLACEHOLDER) && !numeroField.getText().isEmpty() &&
                        !emailField.getText().equals(EMAIL_PLACEHOLDER) && !emailField.getText().isEmpty() &&
                        !new String(passwordField.getPassword()).equals(PASSWORD_PLACEHOLDER) && passwordField.getPassword().length > 0 &&
                        !new String(passwordField2.getPassword()).equals(CONFIRM_PASSWORD_PLACEHOLDER) && passwordField2.getPassword().length > 0;

        registerButton.setEnabled(allFieldsFilled);

        // Change l'apparence du bouton selon son état
        if (allFieldsFilled) {
            registerButton.setBackground(new Color(46, 204, 113));
            registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else {
            registerButton.setBackground(new Color(149, 165, 166)); // Gris pour bouton désactivé
            registerButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == retourButton) {
            frame.dispose();
            new LoginPage();
        } else if (e.getSource() == registerButton) {
            registerUser();
        }
    }

    private void registerUser() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String numero = numeroField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(passwordField2.getPassword());

        // Validation des champs
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(frame, "Veuillez entrer une adresse email valide.", "Erreur de validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!isValidPhoneNumber(numero)) {
            JOptionPane.showMessageDialog(frame, "Veuillez entrer un numéro de téléphone valide.", "Erreur de validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(frame, "Le mot de passe doit contenir au moins 6 caractères.", "Erreur de validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.equals(confirmPassword)) {
            try {
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/LogeFacile", "root", "Aymane2004@");
                String query = "INSERT INTO users (nom, prenom, numero, email, password) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, nom);
                preparedStatement.setString(2, prenom);
                preparedStatement.setString(3, numero);
                preparedStatement.setString(4, email);
                preparedStatement.setString(5, password);

                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    // Utiliser un JOptionPane personnalisé avec une icône de succès
                    JOptionPane optionPane = new JOptionPane(
                            "Inscription réussie ! Vous allez être redirigé vers la page de connexion.",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    JDialog dialog = optionPane.createDialog(frame, "Succès");

                    // Configurer un timer pour fermer automatiquement après 2 secondes
                    Timer timer = new Timer(2000, (e) -> {
                        dialog.dispose();
                        frame.dispose();
                        new LoginPage();
                    });
                    timer.setRepeats(false);
                    timer.start();

                    dialog.setVisible(true);
                }

                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame,
                        "Erreur lors de l'inscription : " + ex.getMessage(),
                        "Erreur de base de données",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Les mots de passe ne correspondent pas.",
                    "Erreur de validation",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // Méthodes de validation
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhoneNumber(String phone) {
        // Accepte les formats internationaux ou locaux
        return phone.matches("^[+]?[0-9]{8,15}$");
    }



}