/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author David
 */
public class DBConnect {
    private Connection con;
    private Statement st;
    private ResultSet re;
    private ResultSet credRS;
    
    public DBConnect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/weka_test", "root", "");
            st = con.createStatement();
        } catch (Exception ex) {
            System.out.println("err: " + ex);
        }
    }
     public void getData(String table) {
        try {
            String query = "select * from "+table;
           
            re = st.executeQuery(query);

            System.out.println("Records: ");
            while (re.next()) {
                String name = re.getString("name");
                String age = re.getString("age");
                System.out.println("Name: " + name + " " + "Age: " + age);
            }
        } catch (Exception e) {
            System.out.println("err: " + e);
        }
    }

    public void updateDB() {
        String query = "INSERT INTO persons (`id`, `name`, `age`) VALUES (NULL, 'me', '40')";
        try {
            st.executeUpdate(query);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public boolean checkCred(String un, String pw){
       try {
            String query = "select * from users";          
            credRS = st.executeQuery(query);
            
            while (credRS.next()) {
                String username = credRS.getString("username");
                String password = credRS.getString("password");
                //System.out.println("username: " + username + " " + "password: " + password);
                if (username.equals(un) & password.equals(pw)){
                    return true;
                }
            }
           
       }catch(Exception e){
       System.out.println(e);
       } return false;
    }
}
