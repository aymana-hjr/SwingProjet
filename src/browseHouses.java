import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class browseHouses {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/LogeFacile";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Aymane2004@";

    private JFrame frame;
    private int userId;
    private JScrollPane scrollPane;
    private JTextField minPriceField;
    private JTextField maxPriceField;
    private JComboBox<String> bedroomComboBox;

    public browseHouses(int userId) {
        this.userId = userId;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Browse Houses");
        frame.setSize(900, 700);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon icon = new ImageIcon("C:\\Users\\HP\\Desktop\\Cours Java\\exemple d'execution\\Projet java s2\\src\\images\\appartement.jpg");
                Image img = icon.getImage();
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Available Houses");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel filterPanel = createFilterPanel();
        mainPanel.add(filterPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel housesPanel = new JPanel();
        housesPanel.setLayout(new BoxLayout(housesPanel, BoxLayout.Y_AXIS));
        housesPanel.setOpaque(false);

        JButton backButton = new JButton("Retour");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setBackground(new Color(52, 152, 219));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        backButton.setFont(new Font("Arial", Font.BOLD, 14));

        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backButton.setBackground(new Color(41, 128, 185));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                backButton.setBackground(new Color(52, 152, 219));
            }
        });

        backButton.addActionListener(e -> {
            frame.dispose(); // Ferme la fenêtre actuelle
            new UserDashboard(userId); // Retourne à la page UserDashboard
        });

        scrollPane = new JScrollPane(housesPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(52, 152, 219);
                this.trackColor = new Color(0, 0, 0, 80);
            }
        });

        mainPanel.add(scrollPane);
        backgroundPanel.add(mainPanel);
        mainPanel.add(backButton);
        frame.add(backgroundPanel);
        loadHouses(housesPanel);
        frame.setVisible(true);
    }

    private ImageIcon loadAndScaleImage(String path, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'image: " + path);
            return null;
        }
    }

    private void loadHouses(JPanel housesPanel) {
        housesPanel.removeAll();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM maisons WHERE statut = 'DISPONIBLE'";
            try (PreparedStatement pstmt = connection.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    JPanel houseCard = createHouseCard(
                            rs.getInt("maison_id"),
                            rs.getString("titre"),
                            rs.getString("description"),
                            rs.getDouble("prix_par_jour"),
                            rs.getString("adresse"),
                            rs.getInt("nombre_chambres"),
                            rs.getDouble("superficie")
                    );
                    housesPanel.add(houseCard);
                    housesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                    "Error loading houses: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        housesPanel.revalidate();
        housesPanel.repaint();
    }

    private String getImageUrlForTitle(String titre) {
        String imageUrl = null;
        String url = "jdbc:mysql://localhost:3306/LogeFacile";
        String user = "root";
        String password = "Aymane2004@"; // Remplacez par votre mot de passe

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT image_url FROM maisons WHERE titre = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, titre);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        imageUrl = rs.getString("image_url");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Si aucune URL n'est trouvée, retourner une URL par défaut
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = "http://example.com/images/default_house.jpg"; // Image par défaut
        }

        return imageUrl;
    }

    private JPanel createHouseCard(int maison_id, String titre, String description, double prix_par_jour,
                                   String adresse, int nombre_chambres, double superficie) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setMaximumSize(new Dimension(850, 220));
        card.setBackground(new Color(255, 255, 255, 220));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(new Color(220, 220, 220), 2, true)
        ));

        // Image
        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(220, 180));
        String imagePath = getImageUrlForTitle(titre); // méthode utilitaire
        ImageIcon icon = new ImageIcon(imagePath);
        imageLabel.setIcon(new ImageIcon(icon.getImage().getScaledInstance(220, 180, Image.SCALE_SMOOTH)));
        card.add(imageLabel, BorderLayout.WEST);

        // Infos
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JLabel titleLabel = new JLabel(titre);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(30, 30, 30));

        JLabel addressLabel = new JLabel(adresse);
        JLabel detailsLabel = new JLabel("🛏 " + nombre_chambres + " chambres | 📐 " + superficie + " m²");

        JTextArea descArea = new JTextArea(description);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setBackground(null);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setForeground(new Color(60, 60, 60));

        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(addressLabel);
        infoPanel.add(detailsLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(descArea);
        card.add(infoPanel, BorderLayout.CENTER);

        // Prix + boutons
        JPanel sidePanel = new JPanel();
        sidePanel.setOpaque(false);
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        JLabel priceLabel = new JLabel(String.format("%.2f DH/jour", prix_par_jour));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceLabel.setForeground(new Color(52, 152, 219));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton viewDetailsBtn = new JButton("Détails");
        JButton reserveBtn = new JButton("Réserver");

        stylizeModernButton(viewDetailsBtn);
        stylizeModernButton(reserveBtn);

        viewDetailsBtn.addActionListener(e -> viewHouseDetails(maison_id));
        reserveBtn.addActionListener(e -> {
            new ConfirmReservation(maison_id, userId, prix_par_jour);
        });

        sidePanel.add(priceLabel);
        sidePanel.add(Box.createVerticalStrut(12));
        sidePanel.add(viewDetailsBtn);
        sidePanel.add(Box.createVerticalStrut(8));
        sidePanel.add(reserveBtn);

        card.add(sidePanel, BorderLayout.EAST);

        return card;
    }

    private void viewHouseDetails(int maison_id) {
        JOptionPane.showMessageDialog(frame,
                "Viewing details for house ID: " + maison_id,
                "House Details",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void stylizeButton(JButton button) {
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFont(new Font("Arial", Font.BOLD, 12));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
            }
        });
    }

    private void stylizeModernButton(JButton button) {
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 50)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        filterPanel.setBackground(new Color(255, 255, 255, 150));

        // Prix minimum et maximum
        filterPanel.add(new JLabel("Price Range:"));
        minPriceField = new JTextField(8); // Utiliser le champ de classe
        maxPriceField = new JTextField(8); // Utiliser le champ de classe
        filterPanel.add(minPriceField);
        filterPanel.add(new JLabel(" - "));
        filterPanel.add(maxPriceField);

        // Nombre de chambres
        filterPanel.add(new JLabel("   Bedrooms:"));
        String[] bedrooms = {"Any", "1", "2", "3", "4+"};
        bedroomComboBox = new JComboBox<>(bedrooms); // Utiliser le champ de classe
        filterPanel.add(bedroomComboBox);

        // Bouton d'application des filtres avec action
        JButton applyFilters = new JButton("Apply Filters");
        stylizeButton(applyFilters);
        applyFilters.addActionListener(e -> {
            String minPrice = minPriceField.getText().trim();
            String maxPrice = maxPriceField.getText().trim();
            String selectedBedrooms = (String) bedroomComboBox.getSelectedItem();
            applyFilters(minPrice, maxPrice, selectedBedrooms);
        });

        // Bouton de réinitialisation
        JButton resetButton = new JButton("Reset");
        stylizeButton(resetButton);
        resetButton.addActionListener(e -> {
            minPriceField.setText("");
            maxPriceField.setText("");
            bedroomComboBox.setSelectedItem("Any");
            applyFilters("", "", "Any"); // Recharger toutes les maisons
        });

        filterPanel.add(applyFilters);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(resetButton);

        return filterPanel;
    }

    private void applyFilters(String minPriceText, String maxPriceText, String bedrooms) {
        try {
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM maisons WHERE statut = 'DISPONIBLE'");

            // Traitement du prix minimum
            if (!minPriceText.isEmpty()) {
                double minPrice = Double.parseDouble(minPriceText);
                queryBuilder.append(" AND prix_par_jour >= ").append(minPrice);
            }

            // Traitement du prix maximum
            if (!maxPriceText.isEmpty()) {
                double maxPrice = Double.parseDouble(maxPriceText);
                queryBuilder.append(" AND prix_par_jour <= ").append(maxPrice);
            }

            // Traitement du nombre de chambres
            if (!bedrooms.equals("Any")) {
                int numBedrooms = Integer.parseInt(bedrooms.replace("+", ""));
                if (bedrooms.endsWith("+")) {
                    queryBuilder.append(" AND nombre_chambres >= ").append(numBedrooms);
                } else {
                    queryBuilder.append(" AND nombre_chambres = ").append(numBedrooms);
                }
            }

            // Ajouter un tri par prix
            queryBuilder.append(" ORDER BY prix_par_jour ASC");

            // Exécuter la requête filtrée
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString())) {

                ResultSet rs = pstmt.executeQuery();
                JPanel housesPanel = (JPanel) scrollPane.getViewport().getView();
                housesPanel.removeAll();

                boolean hasResults = false;
                while (rs.next()) {
                    hasResults = true;
                    JPanel houseCard = createHouseCard(
                            rs.getInt("maison_id"),
                            rs.getString("titre"),
                            rs.getString("description"),
                            rs.getDouble("prix_par_jour"),
                            rs.getString("adresse"),
                            rs.getInt("nombre_chambres"),
                            rs.getDouble("superficie")
                    );
                    housesPanel.add(houseCard);
                    housesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }

                // Afficher un message si aucun résultat
                if (!hasResults) {
                    JLabel noResultsLabel = new JLabel("Aucun logement ne correspond à vos critères");
                    noResultsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    noResultsLabel.setForeground(Color.WHITE);
                    noResultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    housesPanel.add(noResultsLabel);
                }

                housesPanel.revalidate();
                housesPanel.repaint();

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame,
                        "Erreur lors de l'application des filtres: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame,
                    "Veuillez entrer des valeurs numériques valides pour les prix",
                    "Erreur de saisie",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}