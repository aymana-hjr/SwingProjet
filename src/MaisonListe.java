import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MaisonListe extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public MaisonListe() {
        setTitle("Liste des Maisons");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // En-tête
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Liste des Maisons");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(titleLabel);
        headerPanel.setBackground(Color.WHITE);
        add(headerPanel, BorderLayout.NORTH);

        // Table et colonnes
        String[] columns = {"ID", "Nom", "Ville", "Adresse", "Prix/Nuit (€)", "Statut"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        styleTable();
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // Charger les données des maisons
        loadMaisons();
    }

    private void loadMaisons() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/LogeFacile", "root", "Aymane2004@")) {
            // Préparer la requête SQL
            String query = "SELECT maison_id, titre, adresse, prix_par_jour, statut FROM maisons";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                // Parcourir les résultats de la requête
                while (rs.next()) {
                    String id = String.format("M%03d", rs.getInt("maison_id")); // Formatage de l'ID
                    String nom = rs.getString("titre");
                    String adresse = rs.getString("adresse");
                    String ville = extraireVilleDepuisAdresse(adresse); // Extraction de la ville
                    String prix = String.format("%.2f", rs.getDouble("prix_par_jour")); // Formatage du prix
                    String statut = rs.getString("statut");

                    // Ajouter une ligne au modèle de la table
                    model.addRow(new Object[]{id, nom, ville, adresse, prix, statut});
                }
            }
        } catch (Exception e) {
            // Afficher un message d'erreur si la connexion ou la requête échoue
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des données :\n" + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Optionnel : pour voir les détails de l'erreur dans la console
        }
    }

    private String extraireVilleDepuisAdresse(String adresse) {
        if (adresse != null && adresse.contains(",")) {
            String[] parts = adresse.split(",");
            return parts[parts.length - 1].trim();
        } else if (adresse != null) {
            return adresse;
        }
        return "Inconnue";
    }

    private void styleTable() {
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(52, 152, 219));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setGridColor(new Color(189, 195, 199));
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // double-clic
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String id = table.getValueAt(row, 0).toString();
                        String nom = table.getValueAt(row, 1).toString();
                        String ville = table.getValueAt(row, 2).toString();
                        String adresse = table.getValueAt(row, 3).toString();
                        String prix = table.getValueAt(row, 4).toString();
                        String statut = table.getValueAt(row, 5).toString();

                        // Ouvrir le formulaire de modification
                        new FormModifierMaison(id, nom, ville, adresse, prix, statut).setVisible(true);
                    }
                }
            }
        });

    }

}