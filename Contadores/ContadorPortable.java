import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class ContadorPortable {

    public static void main(String[] args) {
        Properties props = new Properties();
        String dbPath = "";

     // Leemos la configuración de la base de datos desde config.properties
        // Esto permite que el proyecto sea portable y la ruta del fichero
        // se pueda modificar sin recompilar.
		// lo que se veria dentro del config.properties seria:
		// db.path=contadores.db nada mas .
		// El fichero config.properties debe estar en el mismo directorio
		// desde el que se ejecuta el programa y introduce la ruta del fichero contadores.db
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
            dbPath = props.getProperty("db.path"); // ruta del fichero .db
        } catch (IOException e) {
            System.out.println("No se pudo leer el archivo de configuración: " + e.getMessage());
            return;
        }

        String url = "jdbc:sqlite:" + dbPath; // JDBC URL para SQLite

        // SQL directo: incremento atómico
        // Razones por las que se ha elegido esta versión:
        // 1. Eficiencia: cada iteración solo ejecuta un UPDATE,
        //    evitando SELECT + ResultSet + updateRow().
        // 2. Seguridad en concurrencia: la base de datos garantiza
        //    que el incremento es atómico si varios procesos acceden
        //    al contador simultáneamente.
        // 3. Portabilidad: funciona igual en SQLite y Derby, no depende
        //    de FOR UPDATE ni ResultSet updatable.
        // 4. Simplicidad: código compacto y fácil de empaquetar como JAR.
        
        String sqlIncremento = "UPDATE contadores SET cuenta = cuenta + 1 WHERE nombre = 'contador1'";

        try (Connection connection = DriverManager.getConnection(url)) {
            connection.setAutoCommit(false); // control manual de transacciones
           
            // Inicialización de la base de datos
            // Si la tabla no existe, se crea automáticamente.
            // Si la fila del contador no existe, se inserta con valor inicial 0.
            // Esto permite ejecutar el proyecto en cualquier máquina
            // sin requerir preparación previa de la DB.
        
            connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS contadores (" +
                "nombre TEXT PRIMARY KEY," +
                "cuenta INTEGER)"
            );

            connection.createStatement().execute(
                "INSERT OR IGNORE INTO contadores (nombre, cuenta) VALUES ('contador1', 0)"
            );

            PreparedStatement incremento = connection.prepareStatement(sqlIncremento);

            // Bucle principal de incremento
            // Cada iteración:
            // - ejecuta el UPDATE atómico
            // - confirma la transacción (commit)
            // Esto asegura consistencia y rendimiento, evitando rollback
        
            for (int i = 0; i < 1000; i++) {
                incremento.executeUpdate();
                connection.commit();
            }

            System.out.println("Incremento completado con éxito.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

