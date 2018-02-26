package JavaCode.db;

import org.bitcoinj.wallet.Wallet;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnector {
    private Connection connection;

    public DBConnector() {

        try {
            connection=getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  Connection getConnection()throws Exception{
        try{
            String driver="com.mysql.jdbc.Driver";
            String url="jdbc:mysql://172.16.222.13";
            String username="crypto_app";
            String password="qtcrypto321";
            Class.forName(driver);

            Connection conn= DriverManager.getConnection(url,username,password);
            System.out.println(("Connected"));
            return conn;

        }catch(Exception e){
            System.out.println(e);
            return  null;
        }

    }


    public static void addNewWallet(Wallet wallet){

    }

}
