package SQLite;

import java.sql.*;

public class ContadorSqlTransaccionalResUpdateable {

	public static void main(String[] args) {
		// Prueba de concepto de transacción con bloqueo de fila para lectura
		// Aquí se usa UPDATE atómico porque SQLite no soporta FOR UPDATE ni ResultSet updatable
		// Sería más fácil en el propio sql poner un set cuenta=cuenta+1 pero ilustramos
		// aquí el problema de concurrencia entre varios procesos.
		String sqlActualizacion = "UPDATE contadores SET cuenta = cuenta + 1 WHERE nombre = 'contador1'";
		String url = "jdbc:sqlite:contadores.db"; // Base de datos SQLite embebida

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (Connection connection = DriverManager.getConnection(url)) {
			connection.setAutoCommit(false); // Control manual de transacciones

			PreparedStatement actualizacion = connection.prepareStatement(sqlActualizacion);

			for (int i = 0; i < 1000; i++) {
				if (actualizacion.executeUpdate() != 1) { // Verifica que se haya actualizado una fila
					System.out.println("Error: contador no encontrado");
					break;
				}
				connection.commit(); // Confirmar cada incremento
			}

			System.out.println("Actualización completa (SQLite).");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
