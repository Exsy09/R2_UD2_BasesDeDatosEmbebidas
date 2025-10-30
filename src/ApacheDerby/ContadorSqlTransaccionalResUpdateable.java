package ApacheDerby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ContadorSqlTransaccionalResUpdateable {

	public static void main(String[] args) {
		// Prueba de concepto de transacción con bloqueo de fila para lectura
		// Sería más fácil en el propio sql poner un set cuenta=cuenta+1 pero ilustramos
		// aquí el problema de concurrencia entre varios procesos.
		// con el for update + transacción conseguimos el bloque de fila y atomicidad

		String sqlConsulta = "SELECT nombre, cuenta FROM contadores WHERE nombre='contador1' FOR UPDATE";
		String url = "jdbc:derby:contadoresDB;create=true"; // Base de datos Derby embebida

		try (Connection connection = DriverManager.getConnection(url)) {
			connection.setAutoCommit(false); // Control manual de transacciones

			// Se crea un ResultSet updatable
			PreparedStatement consulta = connection.prepareStatement(
					sqlConsulta,
					ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_UPDATABLE
			);

			int cuenta = 0;

			for (int i = 0; i < 1000; i++) {
				ResultSet res = consulta.executeQuery(); // Ejecuta la consulta con bloqueo de fila
				if (res.next()) {
					cuenta = res.getInt(2) + 1;  // Incrementa el contador
					res.updateInt(2, cuenta);    // Actualiza el valor en el ResultSet
					res.updateRow();             // Refleja el cambio en la base de datos
				} else {
					System.out.println("Error: contador no encontrado");
					break;
				}
				connection.commit(); // Confirmar cada incremento
			}

			System.out.println("Valor final: " + cuenta);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}