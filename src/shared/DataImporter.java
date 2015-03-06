/**
 * 
 */
package shared;

import java.io.File;

import server.database.DatabaseException;
import shared.model.*;

import java.io.*;
import java.nio.file.*;

import javax.xml.parsers.*;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.*;

/**
 * @author tchambs
 * 
 */
public class DataImporter {

	private static final String databasePath = "database";
	
	static DataImporter di = new DataImporter();
	
	private Model model = new Model();
	
	public static void main(String[] args) throws Exception {

		if (args.length != 1) {
			System.out.println("USAGE: DataImporter [filepath]");
		}

		

		di.doImport(args[0]);

	}

	/**
	 * @param string
	 * @throws ModelException
	 */
	private void doImport(String filepath) throws Exception {
		
		
		File newFile = new File(filepath);
		File dest = new File(databasePath);
		Model.initialize();

		System.out.println("Deleting Old Databases");
		if(!newFile.getParentFile().getCanonicalPath().equals(dest.getCanonicalPath()))
			FileUtils.deleteDirectory(dest);

		
		System.out.println("Copying Files from new filepath");
		FileUtils.copyDirectory(newFile.getParentFile(), dest);

		model.clear();
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(newFile);
		
		System.out.println("Parsing XML and addding to Database");
		di.parse(doc);
		
		System.out.println("Import Complete");
	}
	
	private void parse(Document doc) throws DatabaseException, ModelException {
		di.parseUsers(doc);
		di.parseProjects(doc);
		
	}

	private void parseUsers(Document doc) throws DatabaseException, ModelException {
		NodeList userList = doc.getElementsByTagName("user");
		for (int i = 0; i < userList.getLength(); i++) {
			
			Element userElem = (Element)userList.item(i);
			
			Element usernameElem = (Element)userElem.getElementsByTagName("username").item(0);
			Element passwordElem = (Element)userElem.getElementsByTagName("password").item(0);
			Element firstNameElem = (Element)userElem.getElementsByTagName("firstname").item(0);
			Element lastNameElem = (Element)userElem.getElementsByTagName("lastname").item(0);
			Element emailElem = (Element)userElem.getElementsByTagName("email").item(0);
			Element recordsIndexedElem = (Element)userElem.getElementsByTagName("indexedrecords").item(0);
			
			
			String username = usernameElem.getTextContent();
			String password = passwordElem.getTextContent();
			String firstName = firstNameElem.getTextContent();
			String lastName = lastNameElem.getTextContent();
			String email = emailElem.getTextContent();
			int recordsIndexed = Integer.parseInt(recordsIndexedElem.getTextContent());
			
			model.addUser(new User(0, username, password, firstName, lastName, email, recordsIndexed, -1));
		}
	}
	
	private void parseProjects(Document doc) throws DatabaseException, ModelException {
		NodeList projectList = doc.getElementsByTagName("project");
		for (int projectID = 0; projectID < projectList.getLength(); projectID++) {
			
			Element projectElem = (Element)projectList.item(projectID);
			
			Element titleElem = (Element) projectElem.getElementsByTagName("title").item(0);
			Element recordsPerImageElem = (Element) projectElem.getElementsByTagName("recordsperimage").item(0);
			Element firstYCoordElem = (Element) projectElem.getElementsByTagName("firstycoord").item(0);
			Element recordHeightElem = (Element) projectElem.getElementsByTagName("recordheight").item(0);
			
			String title = titleElem.getTextContent();
			int recordsPerImage = Integer.parseInt(recordsPerImageElem.getTextContent());
			int firstYCoord = Integer.parseInt(firstYCoordElem.getTextContent());
			int recordHeight = Integer.parseInt(recordHeightElem.getTextContent());
			
			Project project = new Project(0, title, recordsPerImage, firstYCoord, recordHeight);
			model.addProject(project);
			
			
			di.parseFields(doc, project.getId(), projectElem);
			di.parseImages(doc, project.getId(), projectElem);

		}
	}

	/**
	 * @param doc
	 * @param projectElem 
	 * @throws ModelException 
	 * @throws DatabaseException 
	 */
	private void parseFields(Document doc, int projectID, Element projectElem) throws DatabaseException, ModelException {
		NodeList fieldList = projectElem.getElementsByTagName("field");
		
		for (int fieldNumber = 0; fieldNumber < fieldList.getLength(); fieldNumber++) {
			
			Element fieldElem = (Element)fieldList.item(fieldNumber);
			
			Element titleElem = (Element) fieldElem.getElementsByTagName("title").item(0);
			Element xCoordElem = (Element) fieldElem.getElementsByTagName("xcoord").item(0);
			Element widthElem = (Element) fieldElem.getElementsByTagName("width").item(0);
			Element helpHTMLElem = (Element) fieldElem.getElementsByTagName("helphtml").item(0);
			Element knownDataElem = (Element) fieldElem.getElementsByTagName("knowndata").item(0);
			
			String title = titleElem.getTextContent();
			int xCoord = Integer.parseInt(xCoordElem.getTextContent());
			int width = Integer.parseInt(widthElem.getTextContent());
			String helpHTML = helpHTMLElem.getTextContent();
			String knownData = "";
			if (knownDataElem != null)
				knownData = knownDataElem.getTextContent();
			
			model.addField(new Field(fieldNumber+1, title, xCoord, width, helpHTML, knownData, projectID));
			
			
		}
	}
	
	/**
	 * @param doc
	 * @param projectElem 
	 * @param id
	 * @throws DatabaseException 
	 * @throws ModelException 
	 */
	private void parseImages(Document doc, int projectID, Element projectElem) throws ModelException, DatabaseException {
		NodeList imageList = projectElem.getElementsByTagName("image");
		for (int imageID = 0; imageID < imageList.getLength(); imageID++) {
			
			Element imageElem = (Element)imageList.item(imageID);
			
			Element filepathElem = (Element) imageElem.getElementsByTagName("file").item(0);
			
			String filepath = filepathElem.getTextContent();
			
			Image image = new Image(projectID, filepath, -1); //-1 = available
			model.addImage(image);
			
			di.parseRecords(doc, image.getId(), imageElem);
		}
	}

	/**
	 * @param doc
	 * @param imageElem 
	 * @param i
	 * @throws ModelException 
	 * @throws DatabaseException 
	 */
	private void parseRecords(Document doc, int imageID, Element imageElem) throws DatabaseException, ModelException {
		NodeList recordList = imageElem.getElementsByTagName("record");
		
		if (recordList == null)
			return;
		
		for (int recordID = 0; recordID < recordList.getLength(); recordID++) {
			
			Element recordElem = (Element)recordList.item(recordID);	
			
			Record record = new Record(imageID, recordID+1);
			model.addRecord(record); 
			
			di.parseValues(doc, record.getId(), recordElem);

		}
		
	}

	/**
	 * @param doc
	 * @param id
	 * @param recordElem
	 * @throws DatabaseException 
	 * @throws ModelException 
	 */
	private void parseValues(Document doc, int recordID, Element recordElem) throws ModelException, DatabaseException {
		NodeList valueList = recordElem.getElementsByTagName("record");
		
		if (valueList == null)
				return;
		
		for (int fieldID = 0; fieldID < valueList.getLength(); fieldID++) {
			Element valueElem = (Element)valueList.item(fieldID);
			
			Element textElem = (Element)valueElem.getElementsByTagName("value").item(0);
			String text = textElem.getTextContent();
			
			Value value = new Value(0, recordID, text, fieldID);
			model.addValue(value);
		}
	}
}
