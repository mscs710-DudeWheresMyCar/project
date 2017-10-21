package wesdev.sample.db2;

import java.sql.*;

public class Db2 
{
	public Db2(){
	}
	/**
	 * TODO: Create functions and make class more generic
	 */
	/*public static void main(String[] args) 
	{
		Db2 db = new Db2();
		db.getAll();
	}  // End main*/
	public void getAll(){
		//String urlPrefix = "jdbc:db2:";
				String url;
				String user;
				String password;
				String carNo;
				String fileName;
				Connection con;
				Statement stmt;
				ResultSet res;
				
				//Auth credentials
				url = "jdbc:db2://awh-yp-small02.services.dal.bluemix.net:50001/BLUDB:sslConnection=true";
				user = "dash111495";
				password = "#T9Yts@ctXM1";
				try 
				{                                                                        
					// Load universal driver
					Class.forName("com.ibm.db2.jcc.DB2Driver"); 
					// Load specific driver (not used for now)
					//Class.forName ("COM.ibm.db2.jdbc.app.DB2Driver");
					System.out.println("**** JDBC driver Loaded");
				
					// Create the connection using the IBM Data Server Driver for JDBC
					//Drivers installed manually (Maven does not work for this)
					con = DriverManager.getConnection (url, user, password);                
					// Commit changes manually
					con.setAutoCommit(false);
					System.out.println("**** Created a JDBC connection to the data source");
				
					// Create the Statement
					stmt = con.createStatement();                                          
					System.out.println("**** Created JDBC Statement object");
					
					// Execute a query and generate a ResultSet instance
					//select * from CARS_TRAIN_ANNOS; //example query
					//SELECT * FROM CARS_TRAIN_ANNOS FETCH FIRST 10 ROWS ONLY; //example query
					res = stmt.executeQuery("SELECT * FROM CARS_TRAIN_ANNOS FETCH FIRST 20 ROWS ONLY");                  
					System.out.println("**** Created JDBC ResultSet object");
				  
					// Print all car info
					while (res.next()) 
					{
						carNo = res.getString(1);
						fileName = res.getString(6);
						System.out.println("Employee number = " + carNo + " " + fileName);
					}
					System.out.println("**** Fetched all rows from JDBC ResultSet");
					// Close the ResultSet
					res.close();
					System.out.println("**** Closed JDBC ResultSet");
					
					// Close the Statement
					stmt.close();
					System.out.println("**** Closed JDBC Statement");
				
					// Connection must be on a unit-of-work boundary to allow close
					con.commit();
					System.out.println ( "**** Transaction committed" );
				  
					// Close the connection
					con.close();                                                           
					System.out.println("**** Disconnected from data source");
					
					System.out.println("**** JDBC Exit from class EzJava - no errors");
					
				}
				catch (ClassNotFoundException e)
				{
					System.err.println("Could not load JDBC driver");
					System.out.println("Exception: " + e);
					e.printStackTrace();
				}
				catch(SQLException ex)                                                   
				{
					System.err.println("SQLException information");
					while(ex!=null) 
					{
						System.err.println ("Error msg: " + ex.getMessage());
						System.err.println ("SQLSTATE: " + ex.getSQLState());
						System.err.println ("Error code: " + ex.getErrorCode());
						ex.printStackTrace();
						ex = ex.getNextException(); // For drivers that support chained exceptions
				     }
				}
	}
}    // End DBData
