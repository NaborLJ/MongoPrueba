package examenpruebamongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import static examenpruebamongo.Examenpruebamongo.Conexion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bson.Document;
import org.bson.types.ObjectId;

public class Examenpruebamongo {
    static int prezo1,prezo2;
    static double total;
    static Connection conn = null;

    public static void main(String[] args) throws SQLException {
        lerReservas();
    }

    public static void Conexion() throws SQLException {
        String driver = "jdbc:oracle:thin:";
        String host = "localhost.localdomain";
        String porto = "1521";
        String sid = "orcl";
        String usuario = "hr";
        String password = "hr";
        String url = driver + usuario + "/" + password + "@" + host + ":" + porto + ":" + sid;
        conn = DriverManager.getConnection(url);
    }
    public static MongoCollection <Document> coleccion;
    
    static MongoClient cliente = new MongoClient("localhost", 27017);

    static MongoDatabase base = cliente.getDatabase("internacional");

    static MongoCollection<Document> coll = base.getCollection("reserva");

    public static void lerReservas() throws SQLException {

        FindIterable<Document> cursor = coll.find();
        MongoCursor<Document> iterator = cursor.iterator();
        while (iterator.hasNext()) {
            Document d = iterator.next();
            Double codr = d.getDouble("codr");
            String dni = d.getString("dni");
            Double ida = d.getDouble("idvooida");
            Double volta = d.getDouble("idvoovolta");
            Double prezoreserva = d.getDouble("prezoreserva");
            Integer confirmado = d.getInteger("confirmado");
            System.out.println("codr :" + codr + " DNI :" + dni + " idvooida :" + ida + " idvoovolta " + volta + " prezoreserva " + prezoreserva + " confirmado " + confirmado);
            //confirmado(codr);
            //nreservas(dni);
            updateprezo(codr,ida,volta);
        }
        iterator.close();
        conn.close();
    }

    public static void confirmado(Double codr) {
        coll.updateOne(new Document("codr", codr), new Document("$set", new Document("confirmado", 1)));
    }

    public static void nreservas(String dni) throws SQLException {
        Conexion();
        PreparedStatement update = conn.prepareStatement("update  pasaxeiros set NRESERVAS=NRESERVAS+1 where DNI='" + dni + "'");

        update.executeUpdate();
        update.close();

       
    }

    public static void updateprezo(Double codr, Double vida, Double vvolt) throws SQLException {
        Conexion();
        Statement consulta = conn.createStatement();
        ResultSet rs = consulta.executeQuery("select * from voos where VOO=" + vida);
        while (rs.next()) {
            prezo1 = rs.getInt("PREZO");
            System.out.println(vida + " " + prezo1);
        }
       
        Statement consulta2 = conn.createStatement();
        ResultSet rs2 = consulta2.executeQuery("select * from voos where VOO=" + vvolt);
        while (rs2.next()) {
            prezo2 = rs2.getInt("PREZO");
            System.out.println(vvolt + " " + prezo2);
        }
       
        total =(prezo1 + prezo2);
        coleccion.updateOne(new Document("codr",codr),new Document("$set",new Document("prezoreserva",total)));
}
    public static void getprezoIda(Double vida) throws SQLException{
            Statement state = conn.createStatement();
            ResultSet rs =state.executeQuery("select prezo from voos where voo='"+vvolt+"'");
            while (rs.next()){
                System.out.println("Prezo ida "+rs.getInt(1));
                prezoida = rs.getInt(1);
                
            }
                
        }
        public static void getprezoVolta() throws SQLException{
            Statement state = conn.createStatement();
            ResultSet rs =state.executeQuery("select prezo from voos where voo='"+idvoovolta+"'");
            while (rs.next()){
                System.out.println("Prezo volta "+rs.getInt(1));
                prezovolta = rs.getInt(1);
            }
        }
}
