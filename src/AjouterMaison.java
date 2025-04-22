import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class AjouterMaison extends JFrame {
    private JTextField titreField, adresseField, prixField, chambresField, superficieField;
    private JTextArea descriptionArea;
    private JComboBox<String> statutComboBox;
    private JButton ajouterButton;

    // Couleurs thématiques
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);  // Bleu principal
    private final Color ACCENT_COLOR = new Color(26, 188, 156);   // Vert accent
    private final Color TEXT_COLOR = new Color(44, 62, 80);       // Texte foncé
    private final Color LIGHT_GRAY = new Color(236, 240, 241);    // Fond gris clair
    private final Color DISABLED_COLOR = new Color(189, 195, 199); // Gris pour les éléments désactivés

    public AjouterMaison() {
        setTitle("LogeFacile - Ajouter une Maison");
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

        // En-tête avec style amélioré
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 25, 0));

        JLabel headerLabel = new JLabel("Ajouter une nouvelle propriété");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel subHeaderLabel = new JLabel("Remplissez les informations ci-dessous pour ajouter une maison");
        subHeaderLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        subHeaderLabel.setForeground(new Color(127, 140, 141));
        subHeaderLabel.setHorizontalAlignment(JLabel.CENTER);

        headerPanel.add(headerLabel, BorderLayout.NORTH);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.CENTER);
        headerPanel.add(subHeaderLabel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel de formulaire avec style moderne
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 20, 5));

        // Créer les champs avec style amélioré
        titreField = createStyledTextField("Titre de l'annonce");
        adresseField = createStyledTextField("Adresse complète");
        prixField = createStyledTextField("Prix par jour (€)");
        chambresField = createStyledTextField("Nombre de chambres");
        superficieField = createStyledTextField("Superficie en m²");

        // Text area avec style moderne
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setForeground(TEXT_COLOR);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_GRAY, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        // Placeholder pour le text area
        descriptionArea.setText("Description détaillée de la propriété...");
        descriptionArea.setForeground(DISABLED_COLOR);
        descriptionArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (descriptionArea.getText().equals("Description détaillée de la propriété...")) {
                    descriptionArea.setText("");
                    descriptionArea.setForeground(TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (descriptionArea.getText().isEmpty()) {
                    descriptionArea.setText("Description détaillée de la propriété...");
                    descriptionArea.setForeground(DISABLED_COLOR);
                }
            }
        });

        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setBorder(BorderFactory.createLineBorder(new Color(218, 223, 225), 1, true));

        // ComboBox avec style moderne
        String[] statuts = {"DISPONIBLE", "INDISPONIBLE"};
        statutComboBox = new JComboBox<>(statuts);
        statutComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statutComboBox.setForeground(TEXT_COLOR);

        // Structure des labels et champs dans le formulaire
        String[] labels = {
                "Titre :", "Adresse :", "Prix par jour :",
                "Nombre de chambres :", "Superficie :", "Description :", "Statut :"
        };

        Component[] fields = {
                titreField, adresseField, prixField,
                chambresField, superficieField, descriptionScroll, statutComboBox
        };

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

        // Panel pour les boutons avec du padding
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        // Bouton Annuler
        JButton annulerButton = createStyledButton("Annuler", new Color(231, 76, 60));
        annulerButton.addActionListener(e -> dispose());

        // Bouton Ajouter
        ajouterButton = createStyledButton("Enregistrer la maison", ACCENT_COLOR);
        ajouterButton.setEnabled(false); // Désactivé par défaut
        ajouterButton.addActionListener(e -> enregistrerMaison());

        buttonPanel.add(annulerButton);
        buttonPanel.add(ajouterButton);

        // Ajouter les panels au panel principal
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Ajouter des écouteurs pour activer/désactiver le bouton
        ajouterFormValidationListeners();

        // Ajouter le panel principal au frame
        setContentPane(mainPanel);
        setVisible(true);
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setForeground(DISABLED_COLOR);
        textField.setText(placeholder);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(DISABLED_COLOR);
                }
            }
        });

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

    private void ajouterFormValidationListeners() {
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

        titreField.getDocument().addDocumentListener(listener);
        adresseField.getDocument().addDocumentListener(listener);
        prixField.getDocument().addDocumentListener(listener);
        chambresField.getDocument().addDocumentListener(listener);
        superficieField.getDocument().addDocumentListener(listener);
        descriptionArea.getDocument().addDocumentListener(listener);
    }

    private void checkFormCompletion() {
        boolean isValid =
                !isPlaceholderOrEmpty(titreField, "Titre de l'annonce") &&
                        !isPlaceholderOrEmpty(adresseField, "Adresse complète") &&
                        !isPlaceholderOrEmpty(prixField, "Prix par jour (€)") && isNumeric(prixField.getText()) &&
                        !isPlaceholderOrEmpty(chambresField, "Nombre de chambres") && isInteger(chambresField.getText()) &&
                        !isPlaceholderOrEmpty(superficieField, "Superficie en m²") && isNumeric(superficieField.getText()) &&
                        !descriptionArea.getText().equals("Description détaillée de la propriété...") && !descriptionArea.getText().isEmpty();

        ajouterButton.setEnabled(isValid);
    }

    private boolean isPlaceholderOrEmpty(JTextField field, String placeholder) {
        return field.getText().equals(placeholder) || field.getText().trim().isEmpty();
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void enregistrerMaison() {
        String titre = titreField.getText();
        String adresse = adresseField.getText();
        double prix = Double.parseDouble(prixField.getText());
        int nbChambres = Integer.parseInt(chambresField.getText());
        double superficie = Double.parseDouble(superficieField.getText());
        String description = descriptionArea.getText();
        String statut = (String) statutComboBox.getSelectedItem();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/LogeFacile", "root", "Aymane2004@")) {
            String query = "INSERT INTO maisons (titre, adresse, prix_par_jour, nombre_chambres, superficie, description, statut) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, titre);
            preparedStatement.setString(2, adresse);
            preparedStatement.setDouble(3, prix);
            preparedStatement.setInt(4, nbChambres);
            preparedStatement.setDouble(5, superficie);
            preparedStatement.setString(6, description);
            preparedStatement.setString(7, statut);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "La propriété a été enregistrée avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement :\n" + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}