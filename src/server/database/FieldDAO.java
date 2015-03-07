/**
 * 
 */
package server.database;

import java.sql.*;
import java.util.*;

import shared.communication.Search_Result;
import shared.model.Field;

/**
 * @author tchambs
 * 
 */
public class FieldDAO {

	private Database db;

	FieldDAO(Database database) {
		this.setDb(database);
	}

	public void add(Field field) throws DatabaseException {
		PreparedStatement stmt = null;
		Statement keyStmt = null;
		ResultSet keyRS = null;
		try {
			String query = "INSERT INTO fields"
				+ "(title, xCoord, width, helpHTML, knownData, projectID) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
			stmt = db.getConnection().prepareStatement(query);
			
			stmt.setString(1, field.getTitle());
			stmt.setInt(2, field.getxCoord());
			stmt.setInt(3, field.getWidth());
			stmt.setString(4, field.getHelpHTML());
			stmt.setString(5, field.getKnownData());
			stmt.setInt(6, field.getProjectID());
			
			if (stmt.executeUpdate() == 1) {
				keyStmt = db.getConnection().createStatement();
				keyRS = keyStmt.executeQuery("SELECT last_insert_rowid()");
				keyRS.next();
				int id = keyRS.getInt(1);
				field.setId(id);
			} else {
				throw new DatabaseException("Could not insert field");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			Database.safeClose(stmt);
			Database.safeClose(keyStmt);
			Database.safeClose(keyRS);
		}
	}

	public void update(Field field) throws DatabaseException {
		PreparedStatement stmt = null;
		try {
			String query = "UPDATE fields "
					+ "SET title=?, xCoord=?, width=?, helpHTML=?, knownData=?, projectID=? "
					+ "WHERE id=?";
			stmt = db.getConnection().prepareStatement(query);

			stmt.setString(1, field.getTitle());
			stmt.setInt(2, field.getxCoord());
			stmt.setInt(3, field.getWidth());
			stmt.setString(4, field.getHelpHTML());
			stmt.setString(5, field.getKnownData());
			stmt.setInt(6, field.getProjectID());
			stmt.setInt(7,  field.getId());
			
			if (stmt.executeUpdate() != 1) {
				throw new DatabaseException("Could not update field");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			Database.safeClose(stmt);
		}
	}

	public void delete(Field field) throws DatabaseException {
		PreparedStatement stmt = null;
		try {
			String query = "DELETE FROM fields WHERE id = ?";
			stmt = db.getConnection().prepareStatement(query);
			stmt.setInt(1, field.getId());

			if (stmt.executeUpdate() != 1) {
				throw new DatabaseException("Could not delete field");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			Database.safeClose(stmt);
		}
	}

	public ArrayList<Search_Result> search(ArrayList<Integer> fields,
			ArrayList<String> search_values) throws DatabaseException {

		ArrayList<Search_Result> result = new ArrayList<Search_Result>();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			
			for (int id : fields) {
				int test = search_values.size();
				for (int i = 0; i < test; i++) {
					String value = search_values.get(i);
					String query = "SELECT DISTINCT records.imageID, images.filepath, records.rowNumber, fields.id "
							+ "FROM images, records, fields, value "
							+ "WHERE images.id = records.imageID "
							+ "AND images.projectID = fields.projectID "
							+ "AND fields.id =  value.fieldID "
							+ "AND fields.id = ? " + "AND value.text = ? ";

					stmt = db.getConnection().prepareStatement(query);
					stmt.setInt(1, id);
					stmt.setString(2, value);
					rs = stmt.executeQuery();
					while (rs.next()) {
						int imageID = rs.getInt(1);
						String imageURL = rs.getString(2);
						int rowNumber = rs.getInt(3);
						int fieldID = rs.getInt(4);

						// add new search result to list
						result.add(new Search_Result(imageID, imageURL,
								rowNumber, fieldID));
					}
				}
			}
		} catch (Exception e) {
			throw new DatabaseException(e.getMessage(), e);
		}

		// return a list of type Search_Result
		return result;
	}

	public ArrayList<Field> getFields(int projectID) throws DatabaseException {

		ArrayList<Field> result = new ArrayList<Field>();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			String query = "select * from fields where fields.projectID = ?";
			stmt = db.getConnection().prepareStatement(query);

			stmt.setInt(1, projectID);

			rs = stmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(1);
				String title = rs.getString(2);
				int xCoord = rs.getInt(3);
				int width = rs.getInt(4);
				String helpHTML = rs.getString(5);
				String knownData = rs.getString(6);
				int projID = rs.getInt(7);

				result.add(new Field(id, title, xCoord, width,
						helpHTML, knownData, projID));
			}

		} catch (Exception e) {
			throw new DatabaseException(e.getMessage(), e);
		}

		return result;
	}

	public Database getDb() {
		return db;
	}

	public void setDb(Database db) {
		this.db = db;
	}

	/**
	 * @return
	 * @throws DatabaseException
	 */
	public List<Field> getAll() throws DatabaseException {

		ArrayList<Field> result = new ArrayList<Field>();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			String query = "select id, title, xCoord, width, helpHTML, knownData, projectID from fields";
			stmt = db.getConnection().prepareStatement(query);

			rs = stmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(1);
				String title = rs.getString(2);
				int xCoord = rs.getInt(3);
				int width = rs.getInt(4);
				String helpHTML = rs.getString(5);
				String knownData = rs.getString(6);
				int projectID = rs.getInt(7);

				result.add(new Field(id, title, xCoord, width,
						helpHTML, knownData, projectID));
			}
		} catch (SQLException e) {
			DatabaseException serverEx = new DatabaseException(e.getMessage(),
					e);
			throw serverEx;
		} finally {
			Database.safeClose(rs);
			Database.safeClose(stmt);
		}
		return result;
	}
}
