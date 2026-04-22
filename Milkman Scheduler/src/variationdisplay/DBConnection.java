
package variationdisplay;

import java.sql.DriverManager;

import java.sql.Connection;

public class DBConnection {

	static Connection doConnect()
	{
		
		Connection con=null; 	//lpv
		try {
			//Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost/milkmandb","root","password");
			
		} 
		catch (Exception e) {
			
			e.printStackTrace();
			
		}
		return con;
		
	}
	public static void main(String[] args) 
	{
		doConnect();
	}

}
