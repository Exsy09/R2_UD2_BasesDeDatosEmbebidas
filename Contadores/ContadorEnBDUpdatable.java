import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/*
public class ContadorEnBDUpdatable {

    public static void main(String[] args) {
        final String claveContador = "contador1";
        final String sqlActualizacion = "UPDATE contadores SET cuenta = cuenta + 1 WHERE nombre = ?";

        String url = "jdbc:sqlite:contadores.db";

        try (Connection connection = DriverManager.getConnection(url)) {
            connection.setAutoCommit(false);

            PreparedStatement actualizacion = connection.prepareStatement(sqlActualizacion);
            actualizacion.setString(1, claveContador);

            int cuenta = 0;

            for (int i = 0; i < 1000; i++) {
                if (actualizacion.executeUpdate() != 1) {
                    System.out.println("Error: contador no encontrado");
                    break;
                }
                connection.commit();
            }

            System.out.println("Actualización completa (SQLite).");

        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
*/


public class ContadorEnBDUpdatable {

    public static void main(String[] args) {
        final String claveContador = "contador1";
        final String sqlConsulta = "SELECT nombre, cuenta FROM contadores WHERE nombre=?";

        String url = "jdbc:derby:contadoresDB;create=true";

        try (Connection connection = DriverManager.getConnection(url)) {
            connection.setAutoCommit(false);

            PreparedStatement consulta = connection.prepareStatement(
                    sqlConsulta,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE
            );
            consulta.setString(1, claveContador);

            int cuenta = 0;

            for (int i = 0; i < 1000; i++) {
                if (consulta.execute()) {
                    ResultSet res = consulta.getResultSet();
                    if (res.next()) {
                        cuenta = res.getInt(2) + 1;
                        res.updateInt(2, cuenta);
                        res.updateRow(); // actualiza la fila
                        connection.commit(); // commit después de cada actualización
                    } else {
                        System.out.println("Error: contador no encontrado");
                    }
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
