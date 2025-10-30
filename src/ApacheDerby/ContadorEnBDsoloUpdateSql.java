package ApacheDerby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ContadorEnBDsoloUpdateSql {

	public static void main(String[] args) {
		final String claveContador = "contador1";
		final String sqlActualizacion = "UPDATE contador SET cuenta = cuenta + 1 WHERE nombre = '" + claveContador + "';";

		String url = "jdbc:derby:contadoresDB;create=true";

		try (Connection connection = DriverManager.getConnection(url)) {
			connection.setAutoCommit(false);
			PreparedStatement actualizacion = connection.prepareStatement(sqlActualizacion);

			for (int i = 0; i < 1000; i++) {
				if (actualizacion.executeUpdate() != 1) break;
				connection.commit();
			}

			System.out.println("Actualización completa.");

		} catch (SQLException e) {
			System.out.println("Error SQL: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
