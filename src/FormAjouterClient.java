import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class FormAjouterClient extends JFrame {

    private JTextField nomField, prenomField, numeroField, emailField;
    private JPasswordField passwordField;
    private JButton saveButton;

    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    private final Color ACCENT_COLOR = new Color(26, 188, 156);
    private final Color LIGHT_GRAY = new Color(236, 240, 241);

    public FormAjouterClient() {
        setTitle("Ajouter une Personne");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Créer un dégradé du haut vers le bas
                GradientPaint gp = new GradientPaint(
                        0, 0, Color.WHITE,
                        0, getHeight(), new Color(240, 248, 255)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(new EmptyBorder(25, 35, 25, 35));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 25, 0));

        JLabel headerLabel = new JLabel("Ajouter une Personne");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setHorizontalAlignment(JLabel.CENTER);

        headerPanel.add(headerLabel, BorderLayout.NORTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel de formulaire avec style moderne
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        nomField = createStyledTextField("Nom");
        prenomField = createStyledTextField("Prénom");
        numeroField = createStyledTextField("Numéro");
        emailField = createStyledTextField("Email");
        passwordField = createStyledPasswordField("Mot de Passe");

        String[] labels = {"Nom :", "Prénom :", "Numéro :", "Email :","Password :"};
        Component[] fields = {nomField, prenomField, numeroField, emailField,passwordField};

        for (int i = 0; i < labels.length; i++) {
            JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
            rowPanel.setOpaque(false);
            rowPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 12, 0));

            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            label.setForeground(TEXT_COLOR);
            label.setPreferredSize(new Dimension(150, 30));

            rowPanel.add(label, BorderLayout.WEST);
            rowPanel.add(fields[i], BorderLayout.CENTER);

            formPanel.add(rowPanel);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        JButton cancelButton = createStyledButton("Annuler", new Color(231, 76, 60));
        cancelButton.addActionListener(e -> dispose());

        saveButton = createStyledButton("Enregistrer", ACCENT_COLOR);
        saveButton.addActionListener(e -> ajouterPersonne());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setForeground(TEXT_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        textField.setToolTipText(placeholder); // Utiliser un tooltip comme placeholder
        return textField;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setForeground(TEXT_COLOR);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        passwordField.setToolTipText(placeholder); // Utiliser un tooltip comme placeholder
        return passwordField;
    }

    private void ajouterPersonne() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String numero = numeroField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim(); // Assurez-vous de récupérer la valeur du champ mot de passe

        // Validation des champs
        if (nom.isEmpty() || prenom.isEmpty() || numero.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs doivent être remplis, y compris le mot de passe.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/LogeFacile", "root", "Aymane2004@")) {
            String sql = "INSERT INTO users (nom, prenom, numero, email, password) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nom);
                stmt.setString(2, prenom);
                stmt.setString(3, numero);
                stmt.setString(4, email);
                stmt.setString(5, password);

                int rowsInserted = stmt.executeUpdate();

                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Personne ajoutée avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // Fermer la fenêtre après l'ajout
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de la personne.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}