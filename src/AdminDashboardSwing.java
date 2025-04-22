import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class AdminDashboardSwing extends JFrame {
    private JLabel maisonsDispoLabel, clientsInscritsLabel, locationsActivesLabel;
    private JTable maisonTable;
    private DefaultTableModel model;

    public AdminDashboardSwing() {
        setTitle("Dashboard - Location de Maisons");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Color primaryBlue = new Color(42, 116, 239);
        Color sidebarBg = new Color(33, 47, 79);
        Color mainBg = new Color(245, 247, 250);
        Font menuFont = new Font("Segoe UI", Font.BOLD, 14);
        Font titleFont = new Font("Segoe UI", Font.BOLD, 22);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(sidebarBg);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 15, 30, 15));

        JLabel welcome = new JLabel("BIENVENU ADMIN!");
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(welcome);
        sidebar.add(Box.createVerticalStrut(30));

        String[] btnTexts = {"Dashboard", "Ajouter Maison", "Liste des Maisons", "Réservations", "Clients", "Statistiques", "Déconnexion"};
        for (String text : btnTexts) {
            JButton btn = createSidebarButton(text, menuFont, sidebarBg, primaryBlue);

            if(text.equals("Ajouter Maison")){
                btn.addActionListener(e->{
                    new AjouterMaison();
                });
            }
            if(text.equals("Liste des Maisons")){
                btn.addActionListener(e->{
                    new MaisonListe().setVisible(true);
                });
            }
            if(text.equals("Dashboard")){
                btn.addActionListener(e->{
                    dispose();
                    new AdminDashboardSwing().setVisible(true);
                });
            }


            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(12));
        }

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        JLabel headerTitle = new JLabel(" Tableau de bord - LogeFacile");
        headerTitle.setFont(titleFont);
        headerTitle.setForeground(new Color(44, 62, 80));
        header.add(headerTitle, BorderLayout.WEST);

        // Main content
        JPanel content = new JPanel();
        content.setBackground(mainBg);
        content.setLayout(new BorderLayout());
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Stats cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        maisonsDispoLabel = new JLabel("0");
        clientsInscritsLabel = new JLabel("0");
        locationsActivesLabel= new JLabel("0");
        statsPanel.add(createStatCard("Maisons disponibles", maisonsDispoLabel, primaryBlue));
        statsPanel.add(createStatCard("Clients inscrits", clientsInscritsLabel, new Color(39, 174, 96)));
        statsPanel.add(createStatCard("Locations actives", locationsActivesLabel, new Color(241, 196, 15)));

        // Table
        String[] columns = {"ID", "Nom", "Ville", "Prix/Nuit (€)", "Statut"};
        model = new DefaultTableModel(columns, 0);
        maisonTable = new JTable(model);
        maisonTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        maisonTable.setRowHeight(28);
        maisonTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        JScrollPane scrollPane = new JScrollPane(maisonTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Liste des Maisons"));
        scrollPane.setPreferredSize(new Dimension(700, 400));
        scrollPane.setBackground(Color.WHITE);

        content.add(statsPanel, BorderLayout.NORTH);
        content.add(Box.createVerticalStrut(25), BorderLayout.CENTER);
        content.add(scrollPane, BorderLayout.SOUTH);

        add(sidebar, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
        loadDataFromDatabase();
    }

    private JButton createSidebarButton(String text, Font font, Color bg, Color hoverColor) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(font);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Rounded corners and shadow
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(33, 47, 79), 1), // Dark border
                BorderFactory.createEmptyBorder(8, 16, 8, 16) // Padding
        ));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hoverColor);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(hoverColor, 2), // Highlight border on hover
                        BorderFactory.createEmptyBorder(8, 16, 8, 16)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(33, 47, 79), 1), // Reset border
                        BorderFactory.createEmptyBorder(8, 16, 8, 16)
                ));
            }
        });

        return btn;
    }

    private JPanel createStatCard(String labelText, JLabel valueLabel, Color valueColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(15, 20, 15, 20)
        ));
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(valueColor);

        card.add(label, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }
    public void loadDataFromDatabase(){
        String url="jdbc:mysql://localhost:3306/LogeFacile";
        String user = "root";
        String password = "Aymane2004@";
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Maisons disponibles
            String maisonsQuery = "SELECT COUNT(*) AS count FROM maisons WHERE statut = 'DISPONIBLE'";
            try (PreparedStatement stmt = conn.prepareStatement(maisonsQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    maisonsDispoLabel.setText(rs.getString("count"));
                }
            }

            // Clients inscrits
            String clientsQuery = "SELECT COUNT(*) AS count FROM users";
            try (PreparedStatement stmt = conn.prepareStatement(clientsQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    clientsInscritsLabel.setText(rs.getString("count"));
                }
            }

            // Locations actives
            String locationsQuery = "SELECT COUNT(*) AS count FROM reservations WHERE statut = 'CONFIRMEE'";
            try (PreparedStatement stmt = conn.prepareStatement(locationsQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    locationsActivesLabel.setText(rs.getString("count"));
                }
            }

            //tableau
            String query="Select maison_id , titre, adresse, prix_par_jour,statut FROM maisons";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                // Parcourir les résultats et remplir le modèle de tableau
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add("M" + String.format("%03d", rs.getInt("maison_id"))); // ID formaté
                    row.add(rs.getString("titre")); // Nom
                    row.add(rs.getString("adresse")); // Ville
                    row.add(rs.getString("prix_par_jour")); // Prix/Nuit
                    row.add(rs.getString("statut")); // Statut
                    model.addRow(row); // Ajouter la ligne au modèle
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la connexion à la base de données : " + e.getMessage(),
                    "Erreur Base de Données", JOptionPane.ERROR_MESSAGE);
        }


    }


}

