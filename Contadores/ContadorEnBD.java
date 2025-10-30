/* 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContadorEnBD {

    public static void main(String[] args) {
        final String sqlConsulta = "SELECT cuenta FROM contadores WHERE nombre=?;";
        final String sqlActualizacion = "UPDATE contadores SET cuenta=? WHERE nombre=?;";
        final String claveContador = "contador1";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:contadores.db")) {
            connection.setAutoCommit(false); // control manual de transacciones

            PreparedStatement consulta = connection.prepareStatement(sqlConsulta);
            PreparedStatement actualizacion = connection.prepareStatement(sqlActualizacion);
            int cuenta = 0;

            consulta.setString(1, claveContador);
            actualizacion.setString(2, claveContador);

            for (int i = 0; i < 1000; i++) {
                ResultSet res = consulta.executeQuery();
                if (res.next()) {
                    cuenta = res.getInt(1) + 1;
                    actualizacion.setInt(1, cuenta);
                    actualizacion.executeUpdate(); // corregido: executeUpdate() para updates
                    connection.commit(); // commit después de cada actualización
                } else {
                    System.out.println("Error: contador no encontrado");
                }
            }
            System.out.println("Valor final: " + cuenta);

        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
*/
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContadorEnBD {

    public static void main(String[] args) {
        final String sqlConsulta = "SELECT cuenta FROM contadores WHERE nombre=?;";
        final String sqlActualizacion = "UPDATE contadores SET cuenta=? WHERE nombre=?;";
        final String claveContador = "contador1";

        // URL de Derby en modo embebido. 'create=true' crea la DB si no existe
        String url = "jdbc:derby:contadoresDB;create=true";

        try (Connection connection = DriverManager.getConnection(url)) {
            connection.setAutoCommit(false); // control manual de transacciones

            PreparedStatement consulta = connection.prepareStatement(sqlConsulta);
            PreparedStatement actualizacion = connection.prepareStatement(sqlActualizacion);
            int cuenta = 0;

            consulta.setString(1, claveContador);
            actualizacion.setString(2, claveContador);

            for (int i = 0; i < 1000; i++) {
                ResultSet res = consulta.executeQuery();
                if (res.next()) {
                    cuenta = res.getInt(1) + 1;
                    actualizacion.setInt(1, cuenta);
                    actualizacion.executeUpdate(); // actualizar correctamente
                    connection.commit(); // commit después de cada actualización
                } else {
                    System.out.println("Error: contador no encontrado");
                }
            }
            System.out.println("Valor final: " + cuenta);

        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
