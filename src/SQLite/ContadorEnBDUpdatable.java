package SQLite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContadorEnBDUpdatable {

	public static void main(String[] args) {
		final String claveContador = "contador1";
		final String sqlActualizacion = "UPDATE contadores SET cuenta = cuenta + 1 WHERE nombre = ?";

		String url = "jdbc:sqlite:contadores.db";

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (Exception e) {
			e.printStackTrace();
		}

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
