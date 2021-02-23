package com.vs.planplat.middlecourt.util;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 连接sqlite数据库，并执行对应的sql语句 or sql文件
 */
public class OpSqliteDBUtil {

	private static final String Class_Name = "org.sqlite.JDBC";
	public static final String DB_PREFIX = "jdbc:sqlite:";
	public static String SQL_FILE;
	//sqlite数据库地址
	/*private static final String DB_URL = "jdbc:sqlite:C:\\Users\\刘咸鱼\\Desktop\\sqlite\\1.db";*/

	public static void execSqlFile(String dbUrl) {
		Connection connection = null;
		try {
			connection = createConnection(DB_PREFIX + dbUrl + ".db");
			func2(connection);
			System.out.println("Success!");
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				// connection close failed.
				System.err.println(e);
			}
		}
	}

	// 创建Sqlite数据库连接
	public static Connection createConnection(String dbUrl) throws SQLException, ClassNotFoundException {
		Class.forName(Class_Name);
		return DriverManager.getConnection(dbUrl);
	}

	public static void func2(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.

		List<String> sqlList = readSqlFile(SQL_FILE);

		// 执行建表语句语句
		for (String s : sqlList) {
			System.out.println(s);
			statement.execute(s);
		}
	}

	/**
	 * 读取sql文件
	 * @param sqlFilePath
	 * @return
	 */
	private static List<String> readSqlFile(String sqlFilePath) {
		File cwFile = new File(sqlFilePath);
		if (!cwFile.exists()) {
			return null;
		}
		List<String> list = new ArrayList<>();	// 存放所有sql语句
		try {
			InputStream is = new FileInputStream(cwFile);
			String line; // 用来保存每行读取的内容
			List<String> tmpList = new ArrayList<>(); // 存放一条SQL语句
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			line = reader.readLine(); // 读取第一行

			while (line != null) { // 如果 line 为空说明读完了
				String sql = "";
				tmpList.add(line);
				if(line.contains(";")){
					for (String s : tmpList) {
						sql = sql + s;
					}
					list.add(sql);
					tmpList.clear();
				}

				line = reader.readLine(); // 读取下一行
			}
			reader.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}