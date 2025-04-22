import javax.swing.*;
import java.awt.*;

public class LoginUI extends JFrame {
    public LoginUI() {
        setTitle("Gestion de Location");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 2));

        // Partie gauche
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(224, 244, 255));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel bienvenueLabel = new JLabel("Bienvenue à", SwingConstants.CENTER);
        bienvenueLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        bienvenueLabel.setForeground(new Color(0, 102, 255));
        bienvenueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appNameLabel = new JLabel("LogeFacile", SwingConstants.CENTER);
        appNameLabel.setFont(new Font("SansSerif", Font.BOLD, 40));
        appNameLabel.setForeground(new Color(0, 0, 102));
        appNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Redimensionner l'image
        JLabel imageLabel = new JLabel();
        ImageIcon originalIcon = new ImageIcon("C:\\Users\\HP\\Desktop\\Cours Java\\exemple d'execution\\Projet java s2\\src\\images\\icon_aymen.jpg"); // ajoute ici le chemin de ton image
        Image image = originalIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH); // Redimensionner à 200x200
        ImageIcon resizedIcon = new ImageIcon(image);
        imageLabel.setIcon(resizedIcon);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(Box.createVerticalStrut(50));
        leftPanel.add(bienvenueLabel);
        leftPanel.add(appNameLabel);
        leftPanel.add(Box.createVerticalStrut(30));
        leftPanel.add(imageLabel);

        // Partie droite (modifiée pour un meilleur style)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(null);
        rightPanel.setBackground(new Color(240, 240, 255)); // Couleur de fond subtile

        JLabel iconLabel = new JLabel(new ImageIcon("C:\\Users\\HP\\Desktop\\Cours Java\\exemple d'execution\\Projet java s2\\src\\images\\img4.png"));
        iconLabel.setBounds(70, 30, 60, 60);
        rightPanel.add(iconLabel);

        JLabel adminWelcome = new JLabel("Bienvenue admin!");
        adminWelcome.setFont(new Font("SansSerif", Font.BOLD, 24));
        adminWelcome.setBounds(140, 30, 250, 30);
        adminWelcome.setForeground(new Color(0, 102, 255));
        rightPanel.add(adminWelcome);

        JLabel subtitle = new JLabel("Connectez-vous pour continuer");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitle.setForeground(new Color(0, 102, 255));
        subtitle.setBounds(140, 60, 300, 30);
        rightPanel.add(subtitle);

        // Utilisation de champs de texte avec bordures arrondies
        JTextField usernameField = new JTextField();
        usernameField.setBounds(100, 150, 300, 35);
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 255), 2));
        rightPanel.add(usernameField);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(100, 200, 300, 35);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.setEchoChar('*');
        passwordField.setToolTipText("Password");
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 255), 2));
        rightPanel.add(passwordField);

        JLabel forgotPass = new JLabel("Password oublier?");
        forgotPass.setFont(new Font("SansSerif", Font.PLAIN, 12));
        forgotPass.setForeground(new Color(0, 102, 255));
        forgotPass.setBounds(100, 240, 200, 20);
        rightPanel.add(forgotPass);

        // Bouton Login avec fond arrondi et ombre
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(100, 280, 300, 40);
        loginButton.setBackground(new Color(0, 153, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        loginButton.setOpaque(true); // Permet de gérer la couleur de fond

        loginButton.addActionListener(e->{
            String email = usernameField.getText();
            String passwd = new String (passwordField.getPassword());

            String adminEmail="LogeFacile@gmail.com";
            String adminpasswd="azer1234";

            if(email.equals(adminEmail) && passwd.equals(adminpasswd)){
                dispose();
                new AdminDashboardSwing().setVisible(true);
            }
            else{
                JOptionPane.showMessageDialog(this, "Email ou mot de passe incorrect", "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        });



        rightPanel.add(loginButton);

        add(leftPanel);
        add(rightPanel);
        setVisible(true);
    }
}
