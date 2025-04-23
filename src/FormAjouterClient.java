import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class FormAjouterClient extends JFrame {
    private JTextField nomField, prenomField, numeroField, emailField, passwordField;

    public FormAjouterClient() {
        setTitle("Ajouter un Client");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // En-tête
        JLabel headerLabel = new JLabel("Ajouter un Nouveau Client");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(headerLabel, BorderLayout.NORTH);

        // Formulaire
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        nomField = createStyledTextField("Nom");
        prenomField = createStyledTextField("Prénom");
        numeroField = createStyledTextField("Numéro de Téléphone");
        emailField = createStyledTextField("Email");
        passwordField = createStyledTextField("Mot de Passe");

        formPanel.add(nomField);
        formPanel.add(prenomField);
        formPanel.add(numeroField);
        formPanel.add(emailField);
        formPanel.add(passwordField);

        // Bouton Ajouter
        JButton ajouterButton = new JButton("Ajouter");
        ajouterButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ajouterButton.setBackground(new Color(26, 188, 156));
        ajouterButton.setForeground(Color.WHITE);
        ajouterButton.setFocusPainted(false);
        ajouterButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ajouterButton.addActionListener(e -> ajouterClient());

        formPanel.add(Box.createVerticalStrut(20)); // Espacement
        formPanel.add(ajouterButton);

        add(formPanel, BorderLayout.CENTER);
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        textField.setToolTipText(placeholder); // Ajouter un placeholder
        return textField;
    }

    private void ajouterClient() {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String numero = numeroField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/LogeFacile", "root", "Aymane2004@")) {

            // Vérifier que les champs ne sont pas vides
            if (nom.isEmpty() || prenom.isEmpty() || numero.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tous les champs doivent être remplis.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Requête SQL pour insérer un nouveau client
            String query = "INSERT INTO users (nom, prenom, numero, email, password) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, nom);
                stmt.setString(2, prenom);
                stmt.setString(3, numero);
                stmt.setString(4, email);
                stmt.setString(5, password);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Client ajouté avec succès !");
                    dispose(); // Fermer la fenêtre après l'ajout
                }
            }
        } catch (Exception e) {
            // Gestion des erreurs
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du client : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}