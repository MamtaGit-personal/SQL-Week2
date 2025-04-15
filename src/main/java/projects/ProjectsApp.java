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
			"3) Select a project",
			"4) Update project details",
			"5) Delete a project"
				
	);
	// @formatter:on
	
	/************************************************************************/
	public static void main(String[] args) {
		//DbConnection.getConnection(); // simple test
		
		new ProjectsApp().processUserSelections();
		
	}

	/**********************************************************************
	* processUserSelections() method, performs different actions based on 
	* user's selection. Example - If a user's choice of selection is 1, then
	* it creates a project. If a user's selection is 5, it deletes a project
	* and so on. If a user's hits the "Enter" button then the program
	* exits since the value os boolean variable done = true.
	************************************************************************/

	
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
				selectProjectByProjectID();
				break;
				
			case 4:
				updateProject();
				break;
				
			case 5:
				deleteProject();
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
	
	/**********************************************************************
	* createProject() method creates a project with all the values 
	* specified by the user. The user inputs all the project details that is
	* needed to create a new project. 
	************************************************************************/
	
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
	
	/**********************************************************************
	* listProjects() method lists all the projects with projectId and
	* project name for the given project schema.
	************************************************************************/
	
	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		
		System.out.println("\nProjects:");
		
		projects.forEach(project -> System.out
				.println("   " + project.getProjectId() + ": " + project.getProjectName()));
	}
	
	/**********************************************************************
	* selectProjectByProjectID() method selects a project for a given projectId
	* specified by the user.
	************************************************************************/
	
	private void selectProjectByProjectID() {
		listProjects();
		Integer projectId = getIntInput("Enter a project ID to select a project");
		
		/*     un-select the current Project     */
		curProject = null;
		
		/*     This will throw an exception if projectId is invalid    */
		curProject = projectService.fetchProjectById(projectId);
	
	}

	/**********************************************************************
	* updateProject() method updates a project for a given projectId
	* specified by the user. The user provides all the project details that
	* need to be updated. If a user hasn't selected a project then it informs
	* the user that it needs to select a project first to update it.
	************************************************************************/
	
	private void updateProject() {
		
		if(Objects.isNull(curProject)) {
			System.out.println("\nPlease select a project.");
		}
		else 
		{	
			Project project = new Project();
			
			String projectName = getStringInput("Enter the project name [" + curProject.getProjectName() + "]");
			project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
			
			BigDecimal estimatedHours = getDecimalInput("Enter estimated hours [" + curProject.getEstimatedHours() + "]");
			project.setEstimatedHours(Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : estimatedHours);
			
			BigDecimal actualHours = getDecimalInput("Enter actual hours [" + curProject.getActualHours() + "]");
			project.setActualHours(Objects.isNull(actualHours) ? curProject.getActualHours(): actualHours);
			
			Integer difficulty = getIntInput("Enter difficulty (1 - 5) " + "[" + curProject.getDifficulty() + "]");
			project.setDifficulty(Objects.isNull(difficulty) ? curProject.getDifficulty() : difficulty);
			
			String notes = getStringInput("Enter notes [" + curProject.getNotes() + "]");
			project.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);
			
			project.setProjectId(curProject.getProjectId());
			
			projectService.updateProjectDetails(project);
			
			curProject = projectService.fetchProjectById(curProject.getProjectId());
			
		}
		
	}

	
	/**********************************************************************
	* deleteProject() method deletes a project for a given projectId
	* specified by the user.
	************************************************************************/
	
	private void deleteProject() {
		
		listProjects();
		Integer projectId = getIntInput("Enter the ID of the project to delete");
		
		if(Objects.nonNull(projectId)) {
			projectService.deleteProject(projectId);
		}
		
		
		if(Objects.nonNull(curProject) && curProject.getProjectId().equals(projectId)) {
			curProject = null;
			System.out.println("\nIn DeleteProjec(), the curProject HAS BEEN set to NULL");
		}
		else {
			System.out.println("\nIn DeleteProjec(), the curProject HAS NOT BEEN set to NULL");
		}
			
	}
	
	
	/**********************************************************************
	* getUserSelection() method, gets user input which is an integer.
	************************************************************************/
	
	private int getUserSelection() {
		
		printOperations();
			
		Integer input = getIntInput("Enter a menu selection");
		return Objects.isNull(input) ? -1 : input;
	
	}
	
		
	/**********************************************************************
	 * printOperations() method, prints all the choices a user has, like
	 * 1. add a project
	 * 2. Show all the projects
	 * 3. Select a project
	 * and so on. The operations variable, which is a list has been defined 
	 * at the top .
	 ************************************************************************/
		
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
	
	/**********************************************************************
	 *  getIntInput("Enter a menu selection") method, gets input from the 
	 * user and then performs operation accordingly. Example - if a user
	 * selects 1. add a project, then it calls the createProject() method. 
	 * If the user enter wrong input then it warns the user of the wrong input 
	 * and asks for a valid input. If the user enter the "Enter" key, then the 
	 * program exits.
	 ************************************************************************/
		
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
	
	/**********************************************************************
	 *  exitMenu() method returns true if a user
	 *  clicks/enters on the "Enter" button.
	 ************************************************************************/
		
	private boolean exitMenu() {
		  System.out.println("Exiting the menu.");
          return true;
	}
	
	/************************************************************************/
			
}
