package ua.khai.hubarev.course.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.khai.hubarev.course.Graph;

public class Connector {

	private static final String URL = "jdbc:mysql://localhost:3306/sys?autoReconnect=true&useSSL=false";

	private static final String USER = "root";

	private static final String PASSWORD = "1";

	private static final String GET_STATEMENT = "select * from graph";

	{
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<Graph.Arc> getArcs() {
		List<Graph.Arc> res = new ArrayList<>();

		try (Connection connection = getConnection();) {
			Statement stmt = connection.createStatement();
			ResultSet set = stmt.executeQuery(GET_STATEMENT);
			while (set.next()) {
				Graph.Arc arc = new Graph.Arc(set.getInt(2), set.getInt(3), set.getInt(4), set.getInt(5));
				res.add(arc);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return res;
	}

	public Map<Integer, String> getONUs() {
		Map<Integer, String> res = new HashMap<>();

		try (Connection connection = getConnection();) {
			Statement stmt = connection.createStatement();
			ResultSet set = stmt.executeQuery("select * from ONUs;");
			while (set.next()) {
				res.put(set.getInt(1), set.getString(2));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return res;
	}

	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}
}
