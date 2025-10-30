package ApacheDerby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ContadorSqlTransaccional {

	public static void main(String[] args) {
		// Prueba de concepto de transacción con bloqueo de fila para lectura
		// Sería más fácil en el propio sql poner un set cuenta=cuenta+1 pero ilustramos
		// aquí el problema de concurrencia entre varios procesos.
		// con el for update + transacción conseguimos el bloque de fila y atomicidad
		String sqlConsulta = "select nombre,cuenta from contadores where nombre='contador1' for update;";
		String sqlActualizacion = "update contadores set cuenta=? where nombre='contador1';";

		String url = "jdbc:derby:contadoresDB;create=true";

		try (Connection connection = DriverManager.getConnection(url)) {
			connection.setAutoCommit(false);

			PreparedStatement consulta = connection.prepareStatement(sqlConsulta);
			PreparedStatement actualizacion = connection.prepareStatement(sqlActualizacion);
			int cuenta = 0;

			for (int i = 0; i < 1000; i++) {
				ResultSet res = consulta.executeQuery();
				if (res.next()) {
					cuenta = res.getInt(2) + 1;
					actualizacion.setInt(1, cuenta);
					actualizacion.executeUpdate();
				} else
					break;
				connection.commit();
			}

			System.out.println("Valor final: " + cuenta);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
