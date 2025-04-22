import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Charger le driver JDBC MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // URL de connexion, nom d'utilisateur et mot de passe
            String url = "jdbc:mysql://localhost:3306/?user=root";
            String user = "root";
            String password = "Aymane2004@";

            // Etablir la connexion
            Connection cnx = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion établie avec succès.");
            return cnx;

        } catch (ClassNotFoundException e) {
            System.out.println("Driver JDBC non trouvé.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la connexion à la base de données.");
            e.printStackTrace();
        }
        return connection;
    }

    public static void main(String[] args) {
        // Tester la connexion
        Connection connection = MySQLConnection.getConnection();
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connexion fermée.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}