package semsimKB.webservices;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class VPRKBSQL {
	Connection conn = null;
	
	public VPRKBSQL(String user, String pw) {
		
		try {
			// The newInstance() call is a work around for some
			// broken Java implementations
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			} 
		catch (Exception ex) {
			// handle the error
			}
		
		try {
			conn =DriverManager.getConnection("jdbc:mysql://localhost/vpr_knowledge_base?" +
			"user=" + user + "&password=" + pw);
			// Do something with the Connection	
	
		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}
}
