package backend;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBConnect {

    private Connection con;
    private Statement st;
    private ResultSet re;
    private ResultSet credRS;

    public DBConnect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/weka", "root", "");
            st = con.createStatement();
        } catch (Exception ex) {
            System.out.println("err1: " + ex);
        }
    }

    public int checkTable(String tableName) {
        try {
            // String query = "select * from '" + tableName + "' where exists";
            // // where table_name = '" + tableName + "'";
            // rsTable = st.executeQuery(query);
            DatabaseMetaData meta = con.getMetaData();
            ResultSet set = meta.getTables(null, null, tableName, null);
            if (set.next()) {
                System.out.println("exist");
                return 1;
            } else {
                return 0;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 2;
        }
    }

    public int checkNumofAtt(String tableName) {
        String query = "select * from " + tableName + "";
        int NumofAtt = 0;
        PreparedStatement stmt;
        ResultSet rls;
        ResultSetMetaData MData;
        try {
            stmt = con.prepareStatement(query);
            rls = stmt.executeQuery(query);
            MData = rls.getMetaData();
            NumofAtt = MData.getColumnCount();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return NumofAtt;
    }

    public String getCurrentAttName(String tableName, int NumIndex) {
        String query = "select * from " + tableName + "";
        String NameofAtt = "";
        PreparedStatement stmt;
        ResultSet rls;
        ResultSetMetaData MData;
        try {
            stmt = con.prepareStatement(query);
            rls = stmt.executeQuery(query);
            MData = rls.getMetaData();
            NameofAtt = MData.getColumnName(NumIndex);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return NameofAtt;

    }

    public int getIndexValue(String table, String RMSE, String RMSEValue) {
        try {
            String query = "select ID from " + table + " where " + RMSE + " = " + RMSEValue;
            int Nid = -1;
            re = st.executeQuery(query);
            //System.out.println(re);
            //System.out.println("Records: ");
            while (re.next()) {
                // String name = re.getString("name");
                // re.getRow()
                String requestData = re.getString("ID");
                Nid = Integer.parseInt(requestData);
                //System.out.println("Name: ID " + "Age: " + requestData);
            }
            return Nid;
        } catch (Exception e) {
            System.out.println("err2: " + e);
            return -1;
        }
        // return requestData;
    }

    public String getData(String table, String ID, String IDValue, String Target) {
        String requestData = null;
        try {
            String query = "select " + Target + " from " + table + " where " + ID + " = " + IDValue;

            re = st.executeQuery(query);
            //System.out.println(re);
            //System.out.println("Records: ");
            while (re.next()) {
                // String name = re.getString("name");
                // re.getRow()

                requestData = re.getString("" + Target + "");
                //TargetValue = Double.parseDouble(requestData);
                //System.out.println("Name: " + Target + " " + "Age: " + requestData);
            }
            return requestData;
        } catch (Exception e) {
            System.out.println("err2: " + e);
            return requestData;
        }
        // return requestData;
    }

    public int getNumberofData(String table) {
        try {
            String query = "select COUNT(*) AS number from " + table + "";
            int NumberofData = 0;
            re = st.executeQuery(query);
            //System.out.println(re);
            //System.out.println("Records: ");
            while (re.next()) {
                // String name = re.getString("name");
                // re.getRow()

                NumberofData = re.getInt("number");
            }
            return NumberofData;
        } catch (Exception e) {
            System.out.println("err2: " + e);
            return 0;
        }

    }

    public void copyTables(String table, String newTableName) {
        String query = "create table " + newTableName + " SELECT * FROM " + table + " WHERE 1 = 2";
        String query1 = "INSERT INTO " + newTableName + " SELECT * FROM " + table;
        try {
            st.executeUpdate(query);
            st.executeUpdate(query1);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createTables(String table, ArrayList<String> TableNameList, ArrayList<String> TableTypeList) {
        String query = "create table " + table + " ( " + "ID" + " int primary key,";
        for (int index = 0; index < TableNameList.size(); index++) {
            if (index != TableNameList.size() - 1) {
                query = query + " " + TableNameList.get(index) + " " + TableTypeList.get(index) + ",";
            } else {
                query = query + " " + TableNameList.get(index) + " " + TableTypeList.get(index) + ")";
            }
        }
        System.out.println(query);
        try {
            st.executeUpdate(query);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void insertToTables(String table, ArrayList<String> TableNameList, ArrayList<String> TableValueList) {
        String query = "insert into " + table + " (`ID`,";

        for (int index = 0; index < TableNameList.size(); index++) {
            if (index != TableNameList.size() - 1) {
                query = query + "`" + TableNameList.get(index) + "`,";
            } else {
                query = query + "`" + TableNameList.get(index) + "`)";
            }
        }
        query = query + " values (";
        for (int index = 0; index < TableValueList.size(); index++) {
            if (index != TableValueList.size() - 1) {
                query = query + "" + TableValueList.get(index) + ",";
            } else {
                query = query + "" + TableValueList.get(index) + ")";
            }
        }
        //System.out.println(query);
        try {
            st.executeUpdate(query);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createARFFDB(String table, String id, String date, String air_temperature, String wind_speed,
            String relative_humidity, String pressure_numeric, String surface_temperature, String net_radiation,
            String soil_heat_flux, String ndvi, String evi, String et) {
        String query = "create table " + table + " ( " + id + " int primary key, " + date + " date, " + air_temperature
                + " double(14,8), " + wind_speed + " double(14,8), " + relative_humidity + " double(14,8), "
                + pressure_numeric + " double(14,8), " + surface_temperature + " double(14,8), " + net_radiation
                + " double(14,8), " + soil_heat_flux + " double(14,8), " + ndvi + " double(14,8), " + evi
                + " double(14,8), " + et + " double(14,8))";
        //System.out.println(query);
        try {
            st.executeUpdate(query);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createUserDB(String table, String id, String username, String password) {
        String query = "create table " + table + " ( " + id + " int auto_increment primary key, " + username
                + " varchar(30), " + password + " varchar(30))";
        //System.out.println(query);
        try {
            st.executeUpdate(query);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createResultDB(String table, String ID, String Model, String ModelSetting, String RMSE, String R,
            String NSE, String NameofArff) {
        String query = "create table " + table + " ( " + ID + " int primary key, " + Model + " varchar(255), "
                + ModelSetting + " varchar(255), " + RMSE + " double(14,8), " + R + " double(14,8), " + NSE
                + " double(14,8), " + NameofArff + " varchar(255))";
        //System.out.println(query);
        try {
            st.executeUpdate(query);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void dropTable(String table) {
        String query = "drop table " + table;
        //System.out.println(query);
        try {
            st.executeUpdate(query);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void insertARFFDB(String table, String id_v, String date_v,
            String air_temperature_v, String wind_speed_v, String relative_humidity_v, String pressure_numeric_v,
            String surface_temperature_v, String net_radiation_v, String soil_heat_flux_v, String ndvi_v, String evi_v,
            String et_v) {
        String query = "insert into " + table + " (`" + "ID" + "`,`" + "date" + "`,`" + "AirTemperature" + "`,`" + "WindSpeed"
                + "`,`" + "RelativeHumidity" + "`,`" + "Pressure" + "`,`" + "SurfaceTemperature" + "`,`"
                + "NetRadiation" + "`,`" + "SoilHeatFlux" + "`,`" + "NDVI" + "`,`" + "EVI" + "`,`" + "ET" + "`) values ("
                + id_v + "," +"'"+ date_v + "'"+"," + air_temperature_v + "," + wind_speed_v + "," + relative_humidity_v + ","
                + pressure_numeric_v + "," + surface_temperature_v + "," + net_radiation_v + "," + soil_heat_flux_v
                + "," + ndvi_v + "," + evi_v + "," + et_v + ")";
        //System.out.println(query);
        try {
            st.executeUpdate(query);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void insertUserDB(String table, String id, String username, String password, String username_v,
            String password_v) {
        String query = "insert into " + table + " (`" + id + "`,`" + username + "`,`" + password + "`) values ("
                + username_v + "," + password_v + ")";
        //System.out.println(query);
        try {
            st.executeUpdate(query);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void insertResultDB(String table, String ID, String Model, String ModelSetting, String RMSE, String R,
            String NSE, String NameofArff, String ID_v, String Model_v, String ModelSetting_v, String RMSE_v,
            String R_v, String NSE_v, String NameofArff_v) {
        String query = "insert into " + table + " (`" + ID + "`,`" + Model + "`,`" + ModelSetting + "`,`" + RMSE + "`,`"
                + R + "`,`" + NSE + "`,`" + NameofArff + "`) values (" + ID_v + "," + Model_v + "," + ModelSetting_v
                + "," + RMSE_v + "," + R_v + "," + NSE_v + "," + NameofArff_v + ")";
        //System.out.println(query);
        try {
            st.executeUpdate(query);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public double getMaxResult(String table, String Col) {
        String query = "select MAX(" + Col + ") from " + table + ";";
        //System.out.println(query);
        try {
            ResultSet rs = st.executeQuery(query);
            rs.next();
            //System.out.println(rs.getDouble(1));
            return rs.getDouble(1);
        } catch (Exception e) {
            System.out.println(e);
            return 0;
        }

    }

    public double getMinResult(String table, String Col) {
        String query = "select MIN(" + Col + ") from " + table + ";";
        //System.out.println(query);
        try {
            ResultSet rs = st.executeQuery(query);
            rs.next();
            //System.out.println(rs.getDouble(1));
            return rs.getDouble(1);
        } catch (Exception e) {
            System.out.println(e);
            return 0;
        }

    }

    public boolean checkCred(String un, String pw) {
        try {
            String query = "select * from users";
            credRS = st.executeQuery(query);

            while (credRS.next()) {
                String username = credRS.getString("username");
                String password = credRS.getString("password");
                // System.out.println("username: " + username + " " + "password:
                // " + password);
                if (username.equals(un) & password.equals(pw)) {
                    return true;
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }
}
