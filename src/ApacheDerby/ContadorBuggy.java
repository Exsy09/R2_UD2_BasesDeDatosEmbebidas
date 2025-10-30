package ApacheDerby;

import java.sql.*;

public class ContadorBuggy {
	static final String SQL_CONSULTA = "SELECT cuenta FROM contadores WHERE nombre = 'contador1'";
	static final String SQL_ACTUALIZA = "UPDATE contadores SET cuenta=? WHERE nombre = 'contador1'";

	public static void main(String[] args) {
		String url = "jdbc:derby:contadoresDB;create=true";

		try (Connection con = DriverManager.getConnection(url)) {
			con.setAutoCommit(false);

			int cuenta = 0;
			for (int i = 1; i <= 1000; i++) {
				try (Statement consulta = con.createStatement();
				ResultSet res = consulta.executeQuery(SQL_CONSULTA)) {

					if (res.next()) {
						cuenta = res.getInt(1) + 1;
					}
				} catch (Exception e){
					e.printStackTrace();
				}

				try(PreparedStatement actualiza = con.prepareStatement(SQL_ACTUALIZA)) {
					actualiza.setInt(1, cuenta);
					actualiza.executeUpdate();
				} catch (Exception e){
					e.printStackTrace();
				}

			}
			con.commit();
			System.out.println("Valor final: " + cuenta);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
