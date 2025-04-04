package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

//import projects.dao.DbConnection;

public class ProjectsApp {
	
	private ProjectService projectService = new ProjectService();
	private Scanner scanner = new Scanner(System.in);
	private Project curProject;
	
	// @formatter:off
	private List<String> operations = List.of(
			"1) Add a project",
			"2) List projects",
			"3) Select a project"
				
	);
	// @formatter:on
	
	
	/************************************************************************/
	public static void main(String[] args) {
		//DbConnection.getConnection(); // simple test
		
		new ProjectsApp().processUserSelections();
		
	}

	/************************************************************************/
	private void processUserSelections() {
		boolean done = false;
		
		while(!done) {
			try {
			int selection = getUserSelection();
			
			switch(selection) {
			case -1:
				done = exitMenu();
				break;
				
			case 1:
				createProject();
				break;
			
			case 2:
				listProjects();
				break;
				
			case 3:
				selectProject();
				break;
				
			default:
				System.out.println("\n" + selection + " is not valid a valid selection. Try again.");
				break;
				
			} // switch
			
			} catch(Exception e) {
				System.out.println("\nError: " + e + " Try again.");
			}
			
		}// while loop
		
	}// processUserSelections() 
	
	/************************************************************************/
	private void selectProject() {
		listProjects();
		Integer projectId = getIntInput("Enter a project ID to select a project");
		
		/** un-select the current Project ***/
		curProject = projectService.fetchProjectById(projectId);
		
		System.out.println("\nProject Details are:");
		System.out.println("\n Project Id: " + curProject.getProjectId() + ", project name: " + curProject.getProjectName()
		+ ", project difficulty: " + curProject.getDifficulty());
	}

	/************************************************************************/
	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		
		System.out.println("\nProjects:");
		
		projects.forEach(project -> System.out
				.println("   " + project.getProjectId() + ": " + project.getProjectName()));
	}

	/************************************************************************/

	private int getUserSelection() {
		
		/**********************************************************************
		 * printOperations() method, prints all the choices a user has, like
		 * 1. add a project
		 * 2. Show all the projects
		 * 3. Select a project
		 * and so on. The operations variable, which is a list has been defined 
		 * at the top .
		 ************************************************************************/
			
		printOperations();
			
		/**********************************************************************
		 *  getIntInput("Enter a menu selection") method, takes input from the 
		 * user and then performs operation accordingly. Example - if a user
		 * selects 1. add a project, then it calls the createProject() method. 
		 * If the user enter wrong input then it warns the user of the wrong input 
		 * and asks for a valid input. If the user enter the "Enter" key, then the 
		 * program exits.
		 ************************************************************************/
		Integer input = getIntInput("Enter a menu selection");
		return Objects.isNull(input) ? -1 : input;
	
	}
	
	/************************************************************************/
	
	private void printOperations() {
		
		System.out.println("\nThese are the avaiable selections. Press the Enter key to quit:");
		operations.forEach(line -> System.out.println("   " + line));
		
		if(Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project.");
		}
		else {
			System.out.println("\nYou are working with a project: " + curProject);
		}
		
	}
	
	/************************************************************************/
	
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);

		if(Objects.isNull(input)) {
			return null;
		}
		try {
			return Integer.valueOf(input);
		
		}catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
			
		}
		
	}
	/************************************************************************/
	
	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();
		
		return input.isBlank() ? null : input.trim();
	}
	
	/************************************************************************/
	private boolean exitMenu() {
		  System.out.println("Exiting the menu.");
          return true;
	}
	
	/************************************************************************/
	private void createProject() {
		String projectName = getStringInput("Enter your project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal acctualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter your project notes");
		
		Project project  = new Project();
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(acctualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);
	}
	
	/************************************************************************/
	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);

		if(Objects.isNull(input)) {
			return null;
		}
		try {
			return new BigDecimal(input).setScale(2);
		
		}catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number.");
			
		}
		
	}
			
}
