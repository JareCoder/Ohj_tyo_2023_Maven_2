package fi.utu.tko2116;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        //muodostetaan tietokantayhteys
        Connection conn = null;
        try {
            conn = getConnection();
        } catch (SQLException e) {
            System.out.println("Ei voida jatkaa, tietokantayhteys ei toiminut:");
            throw new RuntimeException(e);
        }



        Scanner lukija = new Scanner(System.in);
        System.out.println("Syötä haettavien henkilöiden minimi-ikä:");
        int min = Integer.parseInt(lukija.nextLine());
        System.out.println("Syötä haettavien henkilöiden maksimi-ikä:");
        int max = Integer.parseInt(lukija.nextLine());

        ArrayList<Henkilo> henkilot = new ArrayList<>();

        System.out.println("Haetaan henkilöt tietokannasta...");

        // kysely
        String s = "SELECT * FROM henkilo WHERE ika >= ? AND ika <= ?";

        try (PreparedStatement c = conn.prepareStatement(s)) {
            c.setInt(1,min);
            c.setInt(2,max);
            ResultSet results =  c.executeQuery();

            // luetaan henkilö kerrallaan
            while(results.next()){
                String nimi = results.getString(1);
                int ika = results.getInt(2);
                Henkilo h = new Henkilo(nimi,ika);
                henkilot.add(h);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Löydettiin seuraavat henkilöt:");
        for(Henkilo h : henkilot){
            System.out.println(h);
        }
        if(henkilot.size()==0){
            System.out.println("Ei henkilöitä. Kokeile suurempaa ikähaarukkaa!");
        }

        File f = new File("tulokset.txt");
        FileWriter fw = new FileWriter(f);
        for(Henkilo h : henkilot){
          fw.write(h.toString());
          fw.write("\n");
        }
        fw.close();
        System.out.println("Tiedosto on sijainnissa " +f.getAbsolutePath());
        System.out.println("Varmista että se on projetkin hakemistossa!");
    }

    public static Connection getConnection() throws SQLException {


        // db parameters
        String url = "jdbc:sqlite:viikko3tietokanta.db";
        // create a connection to the database
        Connection conn = DriverManager.getConnection(url);

        System.out.println("Tietokantayhteys ok");
        return conn;

    }
}


class Henkilo{
    private String nimi;
    private int ika;

    public Henkilo(String nimi,int ika) {
        this.nimi = nimi;
        this.ika = ika;
    }

    @Override
    public String toString() {
        return "Henkilo{" +
                "nimi='" + nimi + '\'' +
                "ikä="+ika+"}'";
    }
}