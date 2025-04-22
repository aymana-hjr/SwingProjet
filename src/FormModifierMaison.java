import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.NumberFormat;

public class FormModifierMaison extends JFrame {
    private JTextField nomField, villeField, adresseField, prixField;
    private JComboBox<String> statutBox;
    private JButton saveButton;

    // Couleurs thématiques
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);  // Bleu principal
    private final Color ACCENT_COLOR = new Color(26, 188, 156);   // Vert accent
    private final Color TEXT_COLOR = new Color(44, 62, 80);       // Texte foncé
    private final Color LIGHT_GRAY = new Color(236, 240, 241);    // Fond gris clair

    private String maisonId;

    public FormModifierMaison(String id, String nom, String ville, String adresse, String prix, String statut) {
        this.maisonId = id;
        setTitle("Modifier Maison - " + id);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal avec marge et fond dégradé
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

        // En-tête avec style moderne
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 25, 0));

        JLabel headerLabel = new JLabel("Modifier Maison");
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
        villeField = createStyledTextField(ville);
        adresseField = createStyledTextField(adresse);
        prixField = createStyledTextField(prix);

        statutBox = new JComboBox<>(new String[]{"DISPONIBLE", "INDISPONIBLE"});
        statutBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statutBox.setForeground(TEXT_COLOR);
        statutBox.setSelectedItem(statut);

        String[] labels = {"Nom :", "Ville :", "Adresse :", "Prix/Nuit (€) :", "Statut :"};
        Component[] fields = {nomField, villeField, adresseField, prixField, statutBox};

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

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        JButton cancelButton = createStyledButton("Annuler", new Color(231, 76, 60));
        cancelButton.addActionListener(e -> dispose());

        saveButton = createStyledButton("Enregistrer", ACCENT_COLOR);
        saveButton.addActionListener(e -> updateMaison());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        // Ajouter les panels au panel principal
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



    private void updateMaison() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/LogeFacile", "root", "Aymane2004@")) {
            String sql = "UPDATE maisons SET titre=?, adresse=?, prix_par_jour=?, statut=? WHERE maison_id=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nomField.getText());
                stmt.setString(2, adresseField.getText());

                // Utilisation de NumberFormat pour analyser un nombre localisé
                NumberFormat format = NumberFormat.getInstance();
                Number number = format.parse(prixField.getText());
                double prix = number.doubleValue();

                stmt.setDouble(3, prix);
                stmt.setString(4, statutBox.getSelectedItem().toString());
                stmt.setInt(5, Integer.parseInt(maisonId.replace("M", "")));

                int updated = stmt.executeUpdate();
                if (updated > 0) {
                    JOptionPane.showMessageDialog(this, "Maison mise à jour avec succès !");
                    dispose(); // Fermer la fenêtre après la mise à jour
                } else {
                    JOptionPane.showMessageDialog(this, "Échec de la mise à jour.");
                }
            }
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Erreur : Le champ 'Prix' doit contenir un nombre valide.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }
}