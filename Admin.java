// Will establish the administrative privileges.

package ai;

public class Admin {
	
	// These variables should only be accessed through this file.
	private static String url = "jdbc:mysql://localhost:3306/ai_db?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	private static String user = "admin";
	private static String pwd = "test123";
	
	/**
	 * Returns the identity of the administrator.
	 *
	 * @param none
	 * @return administrator's user.
	 * @exception none
	 */
	
	public static String returnAdminIdentity() {
		return user;
	}
	
	/**
	 * Returns the password of the administrator.
	 *
	 * @param none
	 * @return administrator's password.
	 * @exception none
	 */
	
	public static String returnAdminPwd() {
		return pwd;
	}
	
	/**
	 * Returns the URL of the database that the driver will utilize.
	 *
	 * @param none
	 * @return URL
	 * @exception none
	 */
	
	public static String returnAdminUrl() {
		return url;
	}

}
