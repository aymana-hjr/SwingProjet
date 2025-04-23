import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Home implements ActionListener {
    JFrame frame = new JFrame();
    JButton userButton, adminButton;

    Home() {
        // Frame properties
        frame.setVisible(true);
        frame.setTitle("LogeFacile");
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Background panel with custom background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon icon = new ImageIcon("C:\\Users\\HP\\Desktop\\Cours Java\\exemple d'execution\\Projet java s2\\src\\images\\appartement.jpg");
                Image image = icon.getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                g.setColor(new Color(0, 0, 0, 150)); // Semi-transparent overlay
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));

        // Title labels
        JLabel titlePart1 = new JLabel("Bienvenue sur ");
        titlePart1.setFont(new Font("Arial", Font.BOLD, 30));
        titlePart1.setForeground(Color.WHITE);
        titlePart1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titlePart2 = new JLabel("LogeFacile");
        titlePart2.setFont(new Font("Arial", Font.BOLD, 30));
        titlePart2.setForeground(new Color(231, 76, 60)); // Custom red color
        titlePart2.setAlignmentX(Component.CENTER_ALIGNMENT);

        // User Button
        userButton = createStyledButton("Utilisateur", new Color(52, 152, 219)); // Blue color
        userButton.addActionListener(this);

        // Admin Button
        adminButton = createStyledButton("Administrateur", new Color(46, 204, 113)); // Green color
        adminButton.addActionListener(e -> {
            frame.dispose();
           new LoginUI();
        });

        // Add components to the background panel
        backgroundPanel.add(Box.createVerticalGlue());  // Center vertically
        backgroundPanel.add(titlePart1);
        backgroundPanel.add(titlePart2);
        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        backgroundPanel.add(userButton);
        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        backgroundPanel.add(adminButton);
        backgroundPanel.add(Box.createVerticalGlue());  // Center vertically

        frame.add(backgroundPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == userButton) {
            frame.dispose();
            new LoginPage();
        }
    }

    /**
     * Creates a styled button with a modern look.
     *
     * @param text      The text to display on the button.
     * @param bgColor   The background color of the button.
     * @return A styled JButton instance.
     */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 50));
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }
}