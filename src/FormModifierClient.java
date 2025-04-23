import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class FormModifierClient extends JFrame {

    private JTextField nomField, prenomField, numeroField, emailField;
    private String userId;
    private JButton saveButton;


    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    private final Color ACCENT_COLOR = new Color(26, 188, 156);
    private final Color LIGHT_GRAY = new Color(236, 240, 241);

    public FormModifierClient(String id, String nom, String prenom, String numero, String email) {
        this.userId = id;
        setTitle("Modifier Client - " + id);
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

        JLabel headerLabel = new JLabel("Modifier Client");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setHorizontalAlignment(JLabel.CENTER);

        headerPanel.add(headerLabel, BorderLayout.NORTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel de formulaire avec style moderne
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        nomField = createStyledTextField(nom);
        prenomField = createStyledTextField(prenom);
        numeroField = createStyledTextField(numero);
        emailField = createStyledTextField(email);

        String[] labels = {"Nom :", "Prenom :", "Numero :", "Email :"};
        Component[] fields = {nomField, prenomField, numeroField, emailField};

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
        saveButton.addActionListener(e -> updateClient());

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
    private JTextField createStyledTextField(String text) {
        JTextField textField = new JTextField(text);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setForeground(TEXT_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
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

    private void updateClient() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/LogeFacile", "root", "Aymane2004@")) {
            // Requête SQL pour mettre à jour les informations du client
            String sql = "UPDATE users SET nom=?, prenom=?, numero=?, email=? WHERE id=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Récupérer les valeurs entrées par l'utilisateur dans les champs
                stmt.setString(1, nomField.getText());
                stmt.setString(2, prenomField.getText());
                stmt.setString(3, numeroField.getText());
                stmt.setString(4, emailField.getText());
                stmt.setInt(5, Integer.parseInt(userId.replace("M", ""))); // Utilisation de l'id pour identifier le client à mettre à jour

                // Exécuter la requête de mise à jour
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    // Afficher un message de succès si la mise à jour est réussie
                    JOptionPane.showMessageDialog(this, "Client mis à jour avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // Fermer la fenêtre après la mise à jour
                } else {
                    // Si aucune ligne n'a été mise à jour, afficher un message d'erreur
                    JOptionPane.showMessageDialog(this, "Erreur : Aucun client trouvé avec cet ID.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            // Gestion des exceptions et affichage d'un message d'erreur
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour du client : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
