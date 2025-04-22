import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ListeReservation extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public ListeReservation() {
        setTitle("Liste des Réservations");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // En-tête
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Liste des Réservations");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(titleLabel);
        headerPanel.setBackground(Color.WHITE);
        add(headerPanel, BorderLayout.NORTH);

        // Colonnes du tableau
        String[] columns = {"ID", "Maison", "Locataire", "Date Début", "Date Fin", "Montant (€)", "Statut"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rendre les cellules non éditables
            }
        };

        table = new JTable(model);
        styleTable();
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // Charger les données des réservations
        loadReservations();
    }

    private void loadReservations() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/LogeFacile", "root", "Aymane2004@")) {

            // Requête SQL pour récupérer les réservations
            String query = "SELECT r.reservation_id, r.date_debut, r.date_fin, r.montant_total, r.statut, "
                    + "m.titre AS maison_titre, u.nom AS locataire_nom "
                    + "FROM reservations r "
                    + "JOIN maisons m ON r.maison_id = m.maison_id "
                    + "JOIN users u ON r.user_id = u.id";

            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                // Parcourir les résultats et les ajouter au modèle
                while (rs.next()) {
                    int reservationId = rs.getInt("reservation_id");
                    String maisonTitre = rs.getString("maison_titre");
                    String locataireNom = rs.getString("locataire_nom");
                    Date dateDebut = rs.getDate("date_debut");
                    Date dateFin = rs.getDate("date_fin");
                    double montantTotal = rs.getDouble("montant_total");
                    String statut = rs.getString("statut");

                    // Ajouter une ligne au modèle de la table
                    model.addRow(new Object[]{
                            reservationId, maisonTitre, locataireNom, dateDebut, dateFin, montantTotal, statut
                    });
                }
            }
        } catch (Exception e) {
            // Gestion des erreurs
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des réservations : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleTable() {
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(52, 152, 219));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setGridColor(new Color(189, 195, 199));
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // double-clic
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        // Récupérer les données de la ligne sélectionnée
                        String id = table.getValueAt(row, 0).toString();
                        String maison = table.getValueAt(row, 1).toString();
                        String locataire = table.getValueAt(row, 2).toString();
                        String dateDebut = table.getValueAt(row, 3).toString();
                        String dateFin = table.getValueAt(row, 4).toString();
                        String montant = table.getValueAt(row, 5).toString();
                        String statut = table.getValueAt(row, 6).toString();

                        // Afficher ou traiter les données récupérées (par exemple, ouvrir un formulaire)
                        JOptionPane.showMessageDialog(null, "Réservation sélectionnée :\n"
                                + "ID: " + id + "\nMaison: " + maison + "\nLocataire: " + locataire
                                + "\nDate Début: " + dateDebut + "\nDate Fin: " + dateFin
                                + "\nMontant: " + montant + " €\nStatut: " + statut);
                    }
                }
            }
        });
    }
}