package SQLite;

import java.sql.*;

public class ContadorBuggy {
	static final String SQL_CONSULTA = "SELECT cuenta FROM contadores WHERE nombre='contador1'";
	static final String SQL_ACTUALIZA = "UPDATE contadores SET cuenta=? WHERE nombre='contador1'";

	public static void main(String[] args) {
		String url = "jdbc:sqlite:contadores.db";

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (Connection con = DriverManager.getConnection(url)) {
			con.setAutoCommit(false); // Control manual de transacciones

			int cuenta = 0;
			for (int i = 1; i <= 1000; i++) {
				Statement consulta = con.createStatement();
				ResultSet res = consulta.executeQuery(SQL_CONSULTA);

				if (res.next()) {
					cuenta = res.getInt(1) + 1;
				}

				PreparedStatement actualiza = con.prepareStatement(SQL_ACTUALIZA);
				actualiza.setInt(1, cuenta);
				actualiza.executeUpdate();

				con.commit(); // Solo commit, no rollback
			}
			System.out.println("Valor final: " + cuenta);
		} catch (SQLException e) {
			e.printStackTrace(); // Error mostrado, pero no se hace rollback
		}
	}
}

