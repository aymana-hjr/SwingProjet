import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.text.SimpleDateFormat;

/**
 * MyReservations - Interface for managing user reservations
 * Enhanced version with improved styling and user experience
 */
public class MyReservations {
    // Database connection constants
    private static final String DB_URL = "jdbc:mysql://localhost:3306/LogeFacile";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Aymane2004@";

    // UI components
    private JFrame frame;
    private JTable reservationsTable;
    private DefaultTableModel tableModel;
    private final int userId;

    // Colors
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color LIGHT_BG_COLOR = new Color(240, 240, 240);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);

    /**
     * Constructor
     * @param userId The ID of the current user
     */
    public MyReservations(int userId) {
        this.userId = userId;
        initialize();
    }

    /**
     * Initialize the UI components
     */
    private void initialize() {
        // Configure frame
        frame = new JFrame("Mes Réservations");
        frame.setSize(900, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        // Header panel with title and retour button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        // Button Retour
        JButton retourButton = new JButton("← Retour");
        retourButton.setFocusPainted(false);
        retourButton.setBackground(new Color(220, 220, 220));
        retourButton.setFont(new Font("SansSerif", Font.PLAIN, 14));

        retourButton.addActionListener(e -> {
            frame.dispose(); // Ferme la fenêtre actuelle
            new UserDashboard(userId); // Retourne à la page UserDashboard
        });

        // Title label
        JLabel titleLabel = new JLabel("Mes Réservations", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);

        // Add retourButton and titleLabel to headerPanel
        headerPanel.add(retourButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create table and scroll pane
        createReservationsTable();
        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Status panel at the bottom
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JLabel statusLabel = new JLabel("Total de vos réservations : " + reservationsTable.getRowCount());
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statusPanel.add(statusLabel);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    /**
     * Creates the header panel with title and refresh button
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Mes Réservations", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);

        // Refresh button
        JButton refreshButton = new JButton("Actualiser");
        refreshButton.setFocusPainted(false);
        refreshButton.setBackground(PRIMARY_COLOR);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> refreshReservations());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(refreshButton, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Creates the status panel at the bottom
     */
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(Color.WHITE);

        JLabel statusLabel = new JLabel("Statut des réservations: ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel pendingLabel = new JLabel("En attente");
        pendingLabel.setForeground(WARNING_COLOR);
        pendingLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel confirmedLabel = new JLabel("Confirmée");
        confirmedLabel.setForeground(SUCCESS_COLOR);
        confirmedLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel cancelledLabel = new JLabel("Annulée");
        cancelledLabel.setForeground(DANGER_COLOR);
        cancelledLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        statusPanel.add(statusLabel);
        statusPanel.add(new JLabel(" • "));
        statusPanel.add(pendingLabel);
        statusPanel.add(new JLabel(" • "));
        statusPanel.add(confirmedLabel);
        statusPanel.add(new JLabel(" • "));
        statusPanel.add(cancelledLabel);

        return statusPanel;
    }

    /**
     * Creates and configures the reservations table
     */
    private void createReservationsTable() {
        String[] columnNames = {"ID", "Hébergement", "Date Début", "Date Fin", "Montant", "Statut", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only actions column is editable
            }
        };

        reservationsTable = new JTable(tableModel);
        reservationsTable.setRowHeight(45);
        reservationsTable.setShowGrid(true);
        reservationsTable.setGridColor(LIGHT_BG_COLOR);
        reservationsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reservationsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        reservationsTable.getTableHeader().setBackground(PRIMARY_COLOR);
        reservationsTable.getTableHeader().setForeground(Color.WHITE);
        reservationsTable.setSelectionBackground(new Color(41, 128, 185, 50));

        // Configure column widths
        reservationsTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        reservationsTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Hébergement
        reservationsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Date Début
        reservationsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Date Fin
        reservationsTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Montant
        reservationsTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Statut
        reservationsTable.getColumnModel().getColumn(6).setPreferredWidth(200); // Actions

        // Custom renderers
        reservationsTable.getColumn("Statut").setCellRenderer(new StatusRenderer());
        reservationsTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        reservationsTable.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox()));

        // Center align for some columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        reservationsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        reservationsTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Date Début
        reservationsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Date Fin
        reservationsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Montant

        // Load data
        loadReservations();
    }

    /**
     * Load reservations from database
     */
    private void loadReservations() {
        // Clear existing data
        tableModel.setRowCount(0);

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT r.reservation_id, m.titre, r.date_debut, r.date_fin, r.montant_total, r.statut " +
                    "FROM reservations r " +
                    "JOIN maisons m ON r.maison_id = m.maison_id " +
                    "WHERE r.user_id = ? " +
                    "ORDER BY r.date_debut DESC";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, userId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

                    while (rs.next()) {
                        int reservationId = rs.getInt("reservation_id");
                        String titreMaison = rs.getString("titre");
                        Date dateDebut = rs.getDate("date_debut");
                        Date dateFin = rs.getDate("date_fin");
                        double montantTotal = rs.getDouble("montant_total");
                        String statut = rs.getString("statut");

                        // Format the data
                        String formattedDateDebut = dateFormat.format(dateDebut);
                        String formattedDateFin = dateFormat.format(dateFin);
                        String formattedMontant = String.format("%.2f €", montantTotal);

                        tableModel.addRow(new Object[]{
                                reservationId,
                                titreMaison,
                                formattedDateDebut,
                                formattedDateFin,
                                formattedMontant,
                                statut,
                                reservationId
                        });
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Erreur lors du chargement des réservations", e.getMessage());
        }
    }

    /**
     * Refresh reservations data
     */
    private void refreshReservations() {
        loadReservations();
        showInfoMessage("Les données ont été actualisées");
    }

    /**
     * Updates the status of a reservation
     */
    private void updateReservationStatus(int reservationId, String newStatus) {
        String statusText = newStatus.equals("CONFIRMEE") ? "confirmer" : "annuler";
        String title = newStatus.equals("CONFIRMEE") ? "Confirmation" : "Annulation";

        int confirm = JOptionPane.showConfirmDialog(
                frame,
                "Voulez-vous vraiment " + statusText + " cette réservation ?",
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "UPDATE reservations SET statut = ? WHERE reservation_id = ? AND user_id = ?";

                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    pstmt.setString(1, newStatus);
                    pstmt.setInt(2, reservationId);
                    pstmt.setInt(3, userId); // Ensure the user owns this reservation

                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        showSuccessMessage("Statut mis à jour avec succès");
                        refreshReservations(); // Reload the data
                    } else {
                        showErrorDialog("Mise à jour échouée", "Aucune réservation n'a été modifiée");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorDialog("Erreur de base de données", e.getMessage());
            }
        }
    }

    /**
     * Shows details for a specific reservation
     */
    private void showReservationDetails(int reservationId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT r.*, m.titre, m.adresse, u.nom, u.prenom, u.email " +
                    "FROM reservations r " +
                    "JOIN maisons m ON r.maison_id = m.maison_id " +
                    "JOIN users u ON r.user_id = u.id " +
                    "WHERE r.reservation_id = ? AND r.user_id = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, reservationId);
                pstmt.setInt(2, userId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");

                        StringBuilder details = new StringBuilder();
                        details.append("<html><body style='width: 400px; font-family: Segoe UI'>");
                        details.append("<h2 style='color: #2980b9'>Détails de la Réservation</h2>");
                        details.append("<hr>");
                        details.append("<p><b>Réservation #:</b> ").append(rs.getInt("reservation_id")).append("</p>");
                        details.append("<p><b>Hébergement:</b> ").append(rs.getString("titre")).append("</p>");
                        details.append("<p><b>Adresse:</b> ").append(rs.getString("adresse")).append("</p>");
                        details.append("<p><b>Période:</b> ").append(dateFormat.format(rs.getDate("date_debut"))).append(" au ");
                        details.append(dateFormat.format(rs.getDate("date_fin"))).append("</p>");
                        details.append("<p><b>Montant total:</b> ").append(String.format("%.2f €", rs.getDouble("montant_total"))).append("</p>");
                        details.append("<p><b>Statut:</b> <span style='color: ");

                        String statut = rs.getString("statut");
                        if ("CONFIRMEE".equals(statut)) {
                            details.append("#27ae60'>Confirmée");
                        } else if ("ANNULEE".equals(statut)) {
                            details.append("#c0392b'>Annulée");
                        } else {
                            details.append("#f39c12'>En attente");
                        }

                        details.append("</span></p>");
                        details.append("<hr>");

                        details.append("</body></html>");

                        JOptionPane.showMessageDialog(
                                frame,
                                new JLabel(details.toString()),
                                "Détails de la Réservation",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Erreur", "Impossible de récupérer les détails de la réservation");
        }
    }

    /**
     * Custom renderer for the status column
     */
    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            label.setHorizontalAlignment(JLabel.CENTER);
            label.setBorder(new EmptyBorder(5, 5, 5, 5));

            String status = value.toString();

            if ("CONFIRMEE".equals(status)) {
                label.setText("Confirmée ✓");
                label.setForeground(SUCCESS_COLOR);
            } else if ("ANNULEE".equals(status)) {
                label.setText("Annulée ✗");
                label.setForeground(DANGER_COLOR);
            } else {
                label.setText("En attente ⌛");
                label.setForeground(WARNING_COLOR);
            }

            return label;
        }
    }

    /**
     * Custom renderer for action buttons
     */
    private class ButtonRenderer implements TableCellRenderer {
        private final JPanel panel;
        private final JButton confirmButton;
        private final JButton cancelButton;
        private final JButton detailsButton;

        public ButtonRenderer() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setOpaque(false);

            confirmButton = createStyledButton("Confirmer", SUCCESS_COLOR);
            cancelButton = createStyledButton("Annuler", DANGER_COLOR);
            detailsButton = createStyledButton("Détails", PRIMARY_COLOR);

            panel.add(confirmButton);
            panel.add(cancelButton);
            panel.add(detailsButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String status = table.getValueAt(row, 5).toString();

            // Disable buttons based on status
            confirmButton.setEnabled(!"CONFIRMEE".equals(status) && !"ANNULEE".equals(status));
            cancelButton.setEnabled(!"ANNULEE".equals(status));

            return panel;
        }
    }

    /**
     * Custom editor for action buttons
     */
    private class ButtonEditor extends DefaultCellEditor {
        private final JPanel panel;
        private final JButton confirmButton;
        private final JButton cancelButton;
        private final JButton detailsButton;
        private int currentReservationId;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setOpaque(false);

            confirmButton = createStyledButton("Confirmer", SUCCESS_COLOR);
            cancelButton = createStyledButton("Annuler", DANGER_COLOR);
            detailsButton = createStyledButton("Détails", PRIMARY_COLOR);

            panel.add(confirmButton);
            panel.add(cancelButton);
            panel.add(detailsButton);

            // Action listeners
            confirmButton.addActionListener(e -> {
                updateReservationStatus(currentReservationId, "CONFIRMEE");
                fireEditingStopped();
            });

            cancelButton.addActionListener(e -> {
                updateReservationStatus(currentReservationId, "ANNULEE");
                fireEditingStopped();
            });

            detailsButton.addActionListener(e -> {
                showReservationDetails(currentReservationId);
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentReservationId = (int) table.getValueAt(row, 0);
            String status = table.getValueAt(row, 5).toString();

            // Disable buttons based on status
            confirmButton.setEnabled(!"CONFIRMEE".equals(status) && !"ANNULEE".equals(status));
            cancelButton.setEnabled(!"ANNULEE".equals(status));

            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return currentReservationId;
        }
    }

    /**
     * Creates a styled button
     */
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return button;
    }

    /**
     * Shows an error dialog
     */
    private void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(
                frame,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Shows a success message
     */
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(
                frame,
                message,
                "Succès",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Shows an info message
     */
    private void showInfoMessage(String message) {
        JLabel label = new JLabel(message);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JOptionPane.showMessageDialog(
                frame,
                label,
                "Information",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}