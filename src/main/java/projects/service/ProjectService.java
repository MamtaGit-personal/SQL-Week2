package projects.service;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;

public class ProjectService {
	
	private static final String SCHEMA_FILE = "project_schema.sql";
	private static final String DATA_FILE = "project_data.sql";
	
	ProjectDao projectDao = new ProjectDao();

	/********************************************************/
	/* 1. First check to see if the project table exists. 
	*  2. It the table exists then add a new project to the table specified by the user.
	*  3. If the project table DOESN'T exist then create the table, and then add a new project to the table specified by user.
	*/
	
	public Project addProject(Project project) {
		
		boolean tableExists = projectDao.checkToSeeIfProjectTableExist();
		System.out.println("Table exists is " + tableExists);
		
		if(tableExists) {
			return projectDao.insertProject(project);
		}
		else {
			/* If the project table DOESN'T exist then create all the tables 
			 * and then add the project specified by the user. */
			
			createTables();
			return projectDao.insertProject(project);
		}
	}
	
	/********************************************************/
	
	/* 1. First check to see if the project table exists. 
	*  2. It the table exists then check if the row count in the table is > 0
	*  3. If the table exists and the row count > 0 then list all the projects.
	*  4. If the table exists but the row count is NOT > 0 then populate the table and then list all the projects.
	*  5. If the project table DOESN'T exist then create the table, populate it and then list all the projects.
	*/
		
	public List<Project> fetchAllProjects() {
		
		List<Project> projects = new ArrayList<Project>();
		
		boolean tableExists = projectDao.checkToSeeIfProjectTableExist();
		int rowCount = projectDao.checkToSeeIfRowCountInProjectTableIsGreaterThanZero();
		
		if(tableExists) {
			if(rowCount > 0) {
				//If the table exists and the row count > 0 then list all the projects.
				projects = projectDao.fetchAllProjects(); 
					
			}
			else {
				// If the table exists but the row count is NOT > 0 then populate the tables and then list all the projects.
				populateTables();
				projects = projectDao.fetchAllProjects();
			}
		}
			
		else {
			// If the project table DOESN'T exist then create tables, populate them and then list all the projects.
			createTables();
			populateTables();
			projects = projectDao.fetchAllProjects();
		}
			
		return projects;
	}
	
	/********************************************************/
	public Project fetchProjectById(Integer projectId) {
		
		return projectDao.fetchProjectById(projectId).orElseThrow(() -> new NoSuchElementException(
				"Project with project ID= "	+ projectId + " does not exist."));
	}

	/********************************************************/
	public void updateProjectDetails(Project project) {
		if(!projectDao.modifyProjectDetails(project)) 
		{
			throw new DbException("Project with ID= " + project.getProjectId() + " does not exist.");
		}
		else System.out.println("\nThe project was updated.");
		
	}
	
	/********************************************************/
	public void deleteProject(Integer projectId) {

		if(!projectDao.deleteProject(projectId)) 
		{
			throw new DbException("Project with ID= " + projectId + " does not exist.");
		}
		else System.out.println("\nThe project was deleted.");
		
	}
	
	/****************   Added the lines below to load Schema and Data files   ******************/
	
	/************************************************************************/
	public void createTables() {
		loadFromFile(SCHEMA_FILE);
	}
	
	/************************************************************************/
	public void populateTables() {
		loadFromFile(DATA_FILE);
	}
	
	/************************************************************************/
	private void loadFromFile(String fileName) {
		
		String content = readFileContent(fileName);
		List<String> sqlStatements = convertContentToSqlStatements(content);
		
		//sqlStatements.forEach(line -> System.out.println(line));
		
		projectDao.executeBatch(sqlStatements);
		
	}
	
	/************************************************************************/
	private List<String> convertContentToSqlStatements(String content) {
		content = removeComments(content);
		content = removeWhiteSpaceSequencesWithSingleSpace(content);
		
		return extractLinesFromContent(content);
	}

	/************************************************************************/
	private List<String> extractLinesFromContent(String content) {
		List<String> lines = new LinkedList<>();
		while(!content.isEmpty()) {
			int semiColon = content.indexOf(";");
			
			if(semiColon == -1) {
				if(!content.isBlank()) {
					lines.add(content);
				}
				content = "";
			}
			else {
				lines.add(content.substring(0, semiColon).trim());
				content = content.substring(semiColon + 1 );
			}
		}
		
		return lines;
	}
	
	/************************************************************************/
	private String removeWhiteSpaceSequencesWithSingleSpace(String content) {
		
		return content.replaceAll("\\s+", " ");
	}
	
	/************************************************************************/
	private String removeComments(String content) {
		StringBuilder builder = new StringBuilder(content);
		int commentPos = 0;  //comment position
		
		while((commentPos = builder.indexOf("-- " , commentPos)) != -1) {
			int eolPos = builder.indexOf("\n", commentPos +1);  // end of line position
			if(eolPos == -1) {
				builder.replace(commentPos, builder.length(), "");
			}
			else {
				builder.replace(commentPos, eolPos +1, "");
			}
		}
		
		return builder.toString();
	}

	/************************************************************************/
	private String readFileContent(String fileName) {
		try {
			Path path = Paths.get(getClass().getClassLoader().getResource(fileName).toURI());
			return Files.readString(path);
		} catch (Exception e) {
			throw new DbException(e);
		}
		
	}
	/************************************************************************/

}
