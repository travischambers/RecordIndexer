package shared.model;

import java.util.*;

import server.database.*;
import shared.communication.*;


public class Model {

	public static void initialize() throws ModelException {		
		try {
			ServerFacade.initialize();		
		}
		catch (ServerFacadeException e) {
			throw new ModelException(e.getMessage(), e);
		}		
	}
	
	public ValidateUser_Result validateUser(ValidateUser_Params params) throws ModelException, ServerFacadeException {
		ServerFacade db = new ServerFacade();
		User user = params.getParams();

		User returnUser = null;
		try {
			db.startTransaction();
			returnUser = db.getUserDAO().validateUser(user);
			db.endTransaction(true);
		}
		catch (ServerFacadeException e) {
			db.endTransaction(false);
			throw new ModelException(e.getMessage(), e);
		}
		
		ValidateUser_Result result = new ValidateUser_Result(returnUser);
		return result;
	}
	
	public GetProjects_Result getProjects(GetProjects_Params params) throws ModelException, ServerFacadeException {
		ServerFacade db = new ServerFacade();
		User user = params.getParams();
		
		//TODO: check if user is valid??
		
		List<Project> returnProjects = null;
		try {
			db.startTransaction();
			returnProjects = db.getProjectDAO().getAll();
			db.endTransaction(true);
		}
		catch (ServerFacadeException e) {
			db.endTransaction(false);
			throw new ModelException(e.getMessage(), e);
		}
				
		GetProjects_Result result = new GetProjects_Result(returnProjects);
		return result;
	}
	
	public static List<Field> getAllFields() throws ModelException, ServerFacadeException {	

		ServerFacade db = new ServerFacade();
		
		try {
			db.startTransaction();
			List<Field> fields = db.getFieldDAO().getAll();
			db.endTransaction(true);
			return fields;
		}
		catch (ServerFacadeException e) {
			db.endTransaction(false);
			throw new ModelException(e.getMessage(), e);
		}
	}
	
	public static void addField(Field field) throws ModelException, ServerFacadeException {

		ServerFacade db = new ServerFacade();
		
		try {
			db.startTransaction();
			db.getFieldDAO().add(field);
			db.endTransaction(true);
		}
		catch (ServerFacadeException e) {
			db.endTransaction(false);
			throw new ModelException(e.getMessage(), e);
		}
	}
	
	public static void updateField(Field field) throws ModelException, ServerFacadeException {

		ServerFacade db = new ServerFacade();
		
		try {
			db.startTransaction();
			db.getFieldDAO().update(field);
			db.endTransaction(true);
		}
		catch (ServerFacadeException e) {
			db.endTransaction(false);
			throw new ModelException(e.getMessage(), e);
		}
	}
	
	public static void deleteField(Field field) throws ModelException, ServerFacadeException {

		ServerFacade db = new ServerFacade();
		
		try {
			db.startTransaction();
			db.getFieldDAO().delete(field);
			db.endTransaction(true);
		}
		catch (ServerFacadeException e) {
			db.endTransaction(false);
			throw new ModelException(e.getMessage(), e);
		}
	}

	public static void clear() {
		
		ServerFacade db = new ServerFacade();
		try {
			db.startTransaction();
			db.clear();
			db.endTransaction(true);
		} catch (ServerFacadeException e) {
			e.printStackTrace();
		}
		
	}

}