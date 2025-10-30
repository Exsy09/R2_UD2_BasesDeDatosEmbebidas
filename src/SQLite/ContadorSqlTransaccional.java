package SQLite;

import java.sql.*;

public class ContadorSqlTransaccional {

	public static void main(String[] args) {
		// Prueba de concepto de transacción con bloqueo de fila para lectura
		// Sería más fácil en el propio sql poner un set cuenta=cuenta+1 pero ilustramos
		// aquí el problema de concurrencia entre varios procesos.
		// con el for update + transacción conseguimos el bloque de fila y atomicidad
		String sqlActualizacion = "UPDATE contadores SET cuenta = cuenta + 1 WHERE nombre = 'contador1'";
		String url = "jdbc:sqlite:contadores.db";

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (Connection connection = DriverManager.getConnection(url)) {
			connection.setAutoCommit(false);

			PreparedStatement actualizacion = connection.prepareStatement(sqlActualizacion);

			for (int i = 0; i < 1000; i++) {
				if (actualizacion.executeUpdate() != 1) {
					System.out.println("Error: contador no encontrado");
					break;
				}
				connection.commit(); // commit después de cada iteración
			}

			System.out.println("Actualización completa (SQLite).");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}