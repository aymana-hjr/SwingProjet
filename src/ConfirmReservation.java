import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ConfirmReservation {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/LogeFacile";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Aymane2004@";

    private JFrame frame;
    private int maisonId;
    private int userId;
    private double prixParJour;

    public ConfirmReservation(int maisonId, int userId, double prixParJour) {
        this.maisonId = maisonId;
        this.userId = userId;
        this.prixParJour = prixParJour;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Confirmer la Réservation");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = new JLabel("Confirmer la Réservation");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Sélection des dates
        JLabel startDateLabel = new JLabel("Date de début :");
        startDateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(startDateLabel);

        JTextField startDateField = new JTextField("YYYY-MM-DD");
        startDateField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(startDateField);

        JLabel endDateLabel = new JLabel("Date de fin :");
        endDateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(endDateLabel);

        JTextField endDateField = new JTextField("YYYY-MM-DD");
        endDateField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(endDateField);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Montant total
        JLabel totalAmountLabel = new JLabel("Montant total : 0.00 DH");
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalAmountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(totalAmountLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Boutons
        JButton checkAvailabilityButton = new JButton("Vérifier la disponibilité");
        JButton calculateButton = new JButton("Calculer le montant");
        JButton confirmButton = new JButton("Confirmer");
        JButton cancelButton = new JButton("Annuler");

        checkAvailabilityButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        calculateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(checkAvailabilityButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(calculateButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(confirmButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(cancelButton);

        frame.add(panel);
        frame.setVisible(true);

        // Actions des boutons
        checkAvailabilityButton.addActionListener(e -> {
            try {
                LocalDate startDate = LocalDate.parse(startDateField.getText().trim());
                LocalDate endDate = LocalDate.parse(endDateField.getText().trim());
                LocalDate today = LocalDate.now();

                // Vérification des dates
                if (startDate.isBefore(today)) {
                    JOptionPane.showMessageDialog(frame, "La date de début ne peut pas être dans le passé.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (endDate.isBefore(startDate)) {
                    JOptionPane.showMessageDialog(frame, "La date de fin doit être après ou égale à la date de début.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Vérifier la disponibilité
                if (isMaisonAvailable(maisonId, startDate, endDate)) {
                    JOptionPane.showMessageDialog(frame, "La maison est disponible pour ces dates.", "Disponibilité", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "La maison n'est pas disponible pour ces dates.", "Indisponible", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Veuillez entrer des dates valides au format YYYY-MM-DD.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        calculateButton.addActionListener(e -> {
            try {
                LocalDate startDate = LocalDate.parse(startDateField.getText().trim());
                LocalDate endDate = LocalDate.parse(endDateField.getText().trim());

                if (endDate.isBefore(startDate)) {
                    JOptionPane.showMessageDialog(frame, "La date de fin doit être après la date de début.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                long days = ChronoUnit.DAYS.between(startDate, endDate) + 1; // Inclure le dernier jour
                double totalAmount = days * prixParJour;

                totalAmountLabel.setText(String.format("Montant total : %.2f DH", totalAmount));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Veuillez entrer des dates valides au format YYYY-MM-DD.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        confirmButton.addActionListener(e -> {
            try {
                LocalDate startDate = LocalDate.parse(startDateField.getText().trim());
                LocalDate endDate = LocalDate.parse(endDateField.getText().trim());

                if (isMaisonAvailable(maisonId, startDate, endDate)) {
                    long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
                    double totalAmount = days * prixParJour;

                    // Insérer dans la table 'reservations'
                    try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                        String query = "INSERT INTO reservations (maison_id, user_id, date_debut, date_fin, montant_total, statut) " +
                                "VALUES (?, ?, ?, ?, ?, 'EN_ATTENTE')";
                        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                            pstmt.setInt(1, maisonId);
                            pstmt.setInt(2, userId);
                            pstmt.setDate(3, Date.valueOf(startDate));
                            pstmt.setDate(4, Date.valueOf(endDate));
                            pstmt.setDouble(5, totalAmount);

                            pstmt.executeUpdate();
                            JOptionPane.showMessageDialog(frame, "Réservation confirmée avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                            frame.dispose();
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "La maison n'est pas disponible pour ces dates.", "Indisponible", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Erreur lors de la confirmation de la réservation : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> frame.dispose());
    }

    private boolean isMaisonAvailable(int maisonId, LocalDate startDate, LocalDate endDate) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Vérifier les réservations existantes
            String reservationQuery = "SELECT 1 FROM reservations WHERE maison_id = ? AND statut = 'CONFIRMEE' " +
                    "AND (date_debut <= ? AND date_fin >= ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(reservationQuery)) {
                pstmt.setInt(1, maisonId);
                pstmt.setDate(2, Date.valueOf(endDate));
                pstmt.setDate(3, Date.valueOf(startDate));
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return false; // Chevauchement trouvé
                    }
                }
            }

            // Vérifier les disponibilités spécifiques
            String disponibiliteQuery = "SELECT 1 FROM disponibilites WHERE maison_id = ? AND statut = 'INDISPONIBLE' " +
                    "AND (date_debut <= ? AND date_fin >= ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(disponibiliteQuery)) {
                pstmt.setInt(1, maisonId);
                pstmt.setDate(2, Date.valueOf(endDate));
                pstmt.setDate(3, Date.valueOf(startDate));
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return false; // Chevauchement trouvé
                    }
                }
            }

            return true; // La maison est disponible
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}