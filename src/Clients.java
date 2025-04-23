import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Clients extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public Clients(){
        setTitle("Liste des Clients");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Liste des Clients");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(titleLabel,BorderLayout.WEST);

        JButton ajouterButton = new JButton("Ajouter un Client");
        ajouterButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ajouterButton.setBackground(new Color(26, 188, 156));
        ajouterButton.setForeground(Color.WHITE);
        ajouterButton.setFocusPainted(false);
        ajouterButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ajouterButton.addActionListener(e -> new FormAjouterClient().setVisible(true));
        headerPanel.add(ajouterButton, BorderLayout.EAST);

        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Nom", "Prénom", "Numéro", "Email"};
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
        loadClients();

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
                        String prenom = table.getValueAt(row, 2).toString();
                        String numero = table.getValueAt(row, 3).toString();
                        String email = table.getValueAt(row, 4).toString();


                        // Ouvrir le formulaire de modification
                        new FormModifierClient(id,nom,prenom,numero,email);


                    }
                }
            }
        });
    }

    private void loadClients() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/LogeFacile", "root", "Aymane2004@")) {

            // Requête SQL pour récupérer les utilisateurs
            String query = "SELECT id, nom, prenom, numero, email FROM users";

            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                // Parcourir les résultats et les ajouter au modèle
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nom = rs.getString("nom");
                    String prenom = rs.getString("prenom");
                    String numero = rs.getString("numero");
                    String email = rs.getString("email");

                    // Ajouter une ligne au modèle de la table
                    model.addRow(new Object[]{id, nom, prenom, numero, email});
                }
            }
        } catch (Exception e) {
            // Gestion des erreurs
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des clients : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }




}
