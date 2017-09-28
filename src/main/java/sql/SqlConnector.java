package sql;

import javafx.geometry.Rectangle2D;
import org.jetbrains.annotations.NotNull;
import textonclasses.*;
import util.FXCustomDialogs;
import util.Util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Mikaël on 2016-11-11.
 */

public class SqlConnector implements TextonReaderWriter {

    public SqlConnector() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Error initializing SQL driver.");
            e.printStackTrace();
        }

    }

   /* public static void closeSqlOnCloseRequest(Stage stage, SqlCapable sqlCapable) {
        stage.setOnCloseRequest(event -> {
            try {
                sqlCapable.getSqlConnector().closeConnection();
            } catch (SQLException e) {
                System.out.println("Could not close SQL Connection.");
                e.printStackTrace();
            }
        });
    }*/

    public Texton readTexton(int numTexton) throws SQLException, IOException {
        Texton texton = null;
        String name = null;
        TextonType type = null;
        String source = null;
        String desc = null;
        int timer = 0;
        BufferedImage bImage = null;
        Connection conn;
        PreparedStatement pstmt;

        conn = openConnection();
        String readTextonString = "SELECT texton_name, texton_type, texton_source, texton_desc, image, timer " +
                "FROM texton " +
                "WHERE texton_num = ?";
        pstmt = conn.prepareStatement(readTextonString);
        pstmt.setInt(1, numTexton);
        ResultSet rs = pstmt.executeQuery();
        InputStream is;
        Blob blob;

        while (rs.next()) {
            name = rs.getString("texton_name");
            type = TextonType.valueOf(rs.getString("texton_type"));
            System.out.println("Type = " + type);
            source = rs.getString("texton_source");
            desc = rs.getString("texton_desc");
            blob = rs.getBlob("image");
            timer = rs.getInt("timer");
            if (blob != null) {
                is = blob.getBinaryStream();
                bImage = ImageIO.read(is);
                System.out.println("Blob is : " + blob.toString());
            } else System.out.println("There is no Blob in this row.");
        }

        pstmt.close();

        //Build texton with core information.
        try {
            switch (type != null ? type : TextonType.TT) {
                case TT:
                    texton = TextonFactory.createTextonT(numTexton, source, name, bImage);
                    texton.setDescription(desc);
                    break;
                case TM:
                    texton = TextonFactory.createTextonM(numTexton, source, name);
                    texton.setDescription(desc);
                    break;
                case TV:
                    texton = TextonFactory.createTextonV(numTexton, source, name, timer, bImage);
                    texton.setDescription(desc);
                    break;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }

        //Read links
        int dest;
        double minX;
        double minY;
        double maxX;
        double maxY;

        String readLinkString = "SELECT num_dest, pos_minX, pos_minY, pos_maxX, pos_maxY " +
                "FROM lien WHERE num_source = ?";
        pstmt = conn.prepareStatement(readLinkString);
        pstmt.setInt(1, numTexton);
        rs = pstmt.executeQuery();
        conn.commit();

        for (int i = 0; i < 4 && rs.next(); i++) {
            dest = rs.getInt("num_dest");
            minX = rs.getDouble("pos_minX");
            minY = rs.getDouble("pos_minY");
            maxX = rs.getDouble("pos_maxX");
            maxY = rs.getDouble("pos_maxY");
            texton.getLienTexton()
                    .add(new LienTexton(dest, "", null, new Rectangle2D(minX, minY, maxX - minX, maxY - minY)));
        }
        pstmt.close();
        rs.close();

        //Get link names, if they exist.
        String linkName;
        String readLinkNameString = "SELECT texton_name FROM texton, lien " +
                "WHERE lien.num_dest = texton.texton_num AND lien.num_source = ?";
        pstmt = conn.prepareStatement(readLinkNameString);
        pstmt.setInt(1, numTexton);
        rs = pstmt.executeQuery();
        for (int i = 0; i < texton.getLienTexton().size() && rs.next(); i++) {
            linkName = rs.getString("texton_name");
            texton.getLienTexton().get(i).setName(linkName);
        }
        pstmt.close();
        rs.close();
        closeConnection(conn);
        return texton;
    }

    @Override
    public void writeTexton(Texton texton, boolean setNextAvailNumTexton) throws SQLException, IOException {
        boolean overwriting = false;
        int highestIndex = -1;
        if (setNextAvailNumTexton) {
            //Déterminer le numéro du prochain texton.
            Connection conn;
            Statement stmt;
            conn = openConnection();
            String highestIndexString = "SELECT * FROM texton ORDER BY texton_num DESC LIMIT 1";
            ResultSet rs;
            stmt = conn.createStatement();
            rs = stmt.executeQuery(highestIndexString);
            conn.commit();
            if (rs.next()) {
                highestIndex = rs.getInt("texton_num");
            }
            if (highestIndex == -1) {
                //Il y a eu une erreur.
                FXCustomDialogs.showError("Erreur dans la récupération de l'index le plus élevé. Texton non sauvegardé.");
                return;
            } else {
                //Modifier le texton avec le plus grand index + 1
                System.out.println("Le numéro de texton le plus élevé dans la base de données était : " + highestIndex);
                texton.setNumTexton(highestIndex + 1);
            }
        }
        if (!setNextAvailNumTexton) {
            //Vérifier s'il y a déjà un texton avec le numéro courant.
            Connection conn;
            Statement stmt;
            conn = openConnection();
            String testExistsString = "SELECT * FROM texton WHERE texton_num = " + texton.getNumTexton();
            System.out.println(testExistsString);
            ResultSet rs;

            stmt = conn.createStatement();
            rs = stmt.executeQuery(testExistsString);
            conn.commit();

            if (!rs.isBeforeFirst()) {
                //Il n'y a pas de texton avec ce numéro.
            } else {
                if (FXCustomDialogs.showConfirmationAction("Il y a déjà un texton avec le numéro " + texton.getNumTexton() + ". Souhaitez-vous l'écraser?")) {
                    //Écraser le texton.
                    overwriting = true;
                } else {
                    //Ne pas écraser le texton.
                    return;
                }
                System.out.println("Il y a un texton avec ce numéro.");
            }
            while (rs.next()) {
                System.out.println(rs.getInt(1));
            }

            stmt.close();
            closeConnection(conn);
        }


        /* Ordre des colonnes de la table textons :
        texton_num (auto-increment, donc pas besoin de le spécifier)
        1 - texton_name (String)
        2 - texton_type (String, doit être TT, TM ou TV)
        3 - texton_source (String)
        4 - texton_desc (String)
        5 - image (BLOB, doit être chargé avec la fonction LOAD_IMAGE.
        6 - timer (int)
         */
        Connection conn;
        PreparedStatement pstmt;
        conn = openConnection();
        String writeTextonString;

        writeTextonString = "REPLACE INTO " +
                "texton(texton_name, texton_type, texton_source, texton_desc, image, timer, texton_num) VALUES(?, ?, ?, ?, ?, ?, ?)";

        pstmt = conn.prepareStatement(writeTextonString);
        pstmt.setString(1, texton.getName());
        pstmt.setString(2, texton.getType().toString());
        pstmt.setString(3, texton.getSource());
        pstmt.setString(4, texton.getDescription());
        if (texton instanceof TextonWithImage) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(texton.getBimage(), "png", baos);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            pstmt.setBlob(5, is);
            is.close();
        } else {
            pstmt.setNull(5, Types.BLOB);
        }
        if (texton instanceof TextonV) {
            pstmt.setInt(6, ((TextonV) texton).getTimer());
        } else {
            pstmt.setNull(6, Types.INTEGER);
        }
        pstmt.setInt(7, texton.getNumTexton());
        pstmt.executeUpdate();
        conn.commit();
        pstmt.close();

        /*  Upload links
        Ordre des colonnes dans la table lien:
        (id, mais auto-increment donc pas besoin de le spécifier)
        1 - num_source (int)
        2 - num_dest (int)
        3 - pos_minX (double)
        4 - pos_minY (double)
        5 - pos_maxX (double)
        6 - pos_maxY (double)
         */
        if (overwriting) {
            //Delete all the old links.
            String deleteString = "DELETE FROM lien WHERE num_source = " + texton.getNumTexton();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(deleteString);
        }
        if (texton.getLienTexton().size() > 0) {
            String saveLinkString = "REPLACE INTO " +
                    "lien(num_source, num_dest, pos_minX, pos_minY, pos_maxX, pos_maxY) VALUES(?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(saveLinkString);

            for (int i = 0; i < texton.getLienTexton().size(); i++) {
                pstmt.setInt(1, texton.getNumTexton());
                pstmt.setInt(2, texton.getLienTexton().get(i).getNumSerie());
                pstmt.setDouble(3, texton.getLienTexton().get(i).getPos().getMinX());
                pstmt.setDouble(4, texton.getLienTexton().get(i).getPos().getMinY());
                pstmt.setDouble(5, texton.getLienTexton().get(i).getPos().getMaxX());
                pstmt.setDouble(6, texton.getLienTexton().get(i).getPos().getMaxY());
                pstmt.executeUpdate();
                conn.commit();
            }
            pstmt.close();
        }
        if (highestIndex != -1) {
            //Si setNextAvail == true;
            FXCustomDialogs.showInfoSimple("Le texton a été sauvegardé avec le numéro : " + (highestIndex + 1));
        }
        closeConnection(conn);
    }

    @Override
    public List<List<Integer>> getHangingLinks() throws SQLException {
        //1 - Créer une liste avec tous les numéros de texton
        List<Integer> textonNums = new ArrayList<>();
        Connection conn = openConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT texton_num FROM texton");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            textonNums.add(rs.getInt("texton_num"));
        }
        pstmt.close();
        rs.close();

        //2 - Créer une liste avec tous les numéros de liens et leurs numéros de textons correspondants
        List<List<Integer>> linkSourcesAndDests = new ArrayList<>();
        pstmt = conn.prepareStatement("SELECT num_source, num_dest FROM lien");
        rs = pstmt.executeQuery();
        while (rs.next()) {
            List<Integer> list = new ArrayList<>();
            list.add(rs.getInt("num_source"));
            list.add(rs.getInt("num_dest"));
            linkSourcesAndDests.add(list);
        }
        pstmt.close();
        rs.close();
        closeConnection(conn);
        //Returned list wraps a source list and a dest list.
        return IntStream.range(0, 2).mapToObj(i -> linkSourcesAndDests.stream().filter(integers -> !textonNums
                .contains(integers.get(1))).collect(Collectors.toList()).stream().map(integers -> integers.get(i))
                .collect(Collectors.toList())).collect(Collectors.toList());
    }

    @Override
    public String getTextonName(int numTexton) throws SQLException {
        Connection conn = openConnection();
        String name = getTextonNameCore(conn, numTexton);
        closeConnection(conn);
        return name;
    }

    @Override
    public Map<Integer, String> getTextonNumsToNamesMap(List<Integer> numTextons) throws SQLException {
        Connection conn = openConnection();
        List<String> nameList = new ArrayList<>();
        for (int i = 0; i < numTextons.size(); i++) {
            nameList.add(getTextonNameCore(conn, numTextons.get(i)));
        }
        Map<Integer, String> map = new HashMap<>();
        IntStream.range(0, numTextons.size()).forEach(i -> map.put(numTextons.get(i), nameList.get(i)));
        return map;
    }

    private @NotNull String getTextonNameCore(Connection conn, int numTexton) throws SQLException {
        String getNameString = "SELECT texton_name FROM texton WHERE texton_num = ?";
        PreparedStatement pstmt = conn.prepareStatement(getNameString);
        pstmt.setInt(1, numTexton);
        ResultSet rs = pstmt.executeQuery();
        String name = null;
        while (rs.next()) {
            name = rs.getString("texton_name");
        }
        return name == null ? "Sans nom" : name;
    }

    private Connection openConnection() throws SQLException {
        String URL = "jdbc:mysql://212.1.211.4:3306/";
        String DBNAME = "mikaelfr_textons";
        String USER = "mikaelfr_mikael";
        String PASSWORD = "Récital2016";
        Connection conn = DriverManager.getConnection(URL + DBNAME, USER, PASSWORD);
        System.out.println("Connection SQL établie.");
        conn.setAutoCommit(false);
        return conn;
    }

    private void closeConnection(Connection conn) throws SQLException {
        if (conn == null) return;
        conn.close();
        System.out.println("Connection closed.");
    }

    public List<Util.Triple<Integer, String, TextonType>> getGraphNodes() throws SQLException {
        Connection conn;
        Statement stmt;
        ResultSet rs;
        String getGraphString = "SELECT texton_num, texton_name, texton_type FROM texton";

        conn = openConnection();
        stmt = conn.createStatement();
        rs = stmt.executeQuery(getGraphString);
        conn.commit();

        List<Util.Triple<Integer, String, TextonType>> list = new ArrayList<>();
        while (rs.next()) {
            int num = rs.getInt("texton_num");
            String name = rs.getString("texton_name");
            TextonType type = TextonType.valueOf(rs.getString("texton_type"));
            Util.Triple<Integer, String, TextonType> triple = new Util.Triple<>(num, name, type);
            list.add(triple);
        }

        stmt.close();
        rs.close();
        closeConnection(conn);

        return list;
    }

    public List<List<Integer>> getGraphEdges() throws SQLException {
        Connection conn;
        Statement stmt;
        ResultSet rs;
        String getEdgesString = "SELECT num_dest, num_source FROM lien";
        conn = openConnection();
        stmt = conn.createStatement();
        rs = stmt.executeQuery(getEdgesString);
        conn.commit();

        List<List<Integer>> list = new ArrayList<>();

        while (rs.next()) {
            int source = rs.getInt("num_source");
            int dest = rs.getInt("num_dest");
            List<Integer> link = new ArrayList<>();
            link.addAll(Stream.of(source, dest).collect(Collectors.toList()));
            list.add(link);
        }

        stmt.close();
        rs.close();
        closeConnection(conn);

        return list;
    }

}
