package projects.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;
import projects.dao.DbConnection;

public class ProjectDao extends DaoBase {
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";
	
		
	/************************************************************************/
	
	public boolean checkToSeeIfProjectTableExist() {
		
		boolean tableAlreadyExist = false;
		
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "project", null);
            
            if (tables.next()) {
            	tableAlreadyExist = true;
            	System.out.println("Table '" + "project" + "' exists.");
            } else {
                System.out.println("Table '" + "project" + "' does not exist.");
            }
			
		}// outer most try()
		catch(SQLException e) {
		throw new DbException(e);
		}
		return tableAlreadyExist;
	}
	
	/************************************************************************/
	
	public int checkToSeeIfRowCountInProjectTableIsGreaterThanZero() {
		
		int rowCount = 0;
		
		String sql = "SELECT COUNT(*) AS number_of_rows FROM " + PROJECT_TABLE;
		
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            
            if (rs.next()) {
               rowCount = rs.getInt("number_of_rows");
            }
			
		}// outer most try()
		catch(SQLException e) {
		throw new DbException(e);
		}
		return rowCount;
	}
	
	/************************************************************************/
	
	public Project insertProject(Project project) {
		String sql = ""
			+ "INSERT INTO " + PROJECT_TABLE + " " 
			+ "(project_name, estimated_hours, actual_hours, difficulty, notes) "
			+ "VALUES "
			+ "(?, ?, ?, ?, ?)";
		
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				
				stmt.executeUpdate();
				
				Integer projectID = getLastInsertId(conn, PROJECT_TABLE);
				commitTransaction(conn);
				
				project.setProjectId(projectID);
				return project;
		
		} catch(Exception e){
			rollbackTransaction(conn);
			throw new DbException(e);
			
			}
		
		} 
		catch (SQLException e) {
		throw new DbException(e);
		}
		
	}
		 
	/************************************************************************/
	
	public List<Project> fetchAllProjects() {
		List<Project> projects = new LinkedList<Project>();
		
		//String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";
		String sql = "SELECT * FROM " + PROJECT_TABLE;
		
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				
				try(ResultSet rs = stmt.executeQuery()){
					
					while(rs.next()) {
						
						projects.add(extract(rs, Project.class));
						
					}// while ()
					
					return projects;
					
				} // inner most try()
				
				
			}// inner try()
			
			catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}// outer most try()
		catch(SQLException e) {
		throw new DbException(e);
		}
		
	}
	
	/************************************************************************/
	
	public Optional<Project> fetchProjectById(Integer projectId) {
		
		String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			try {	
				Project project = null;
				
				try(PreparedStatement stmt = conn.prepareStatement(sql)){
					setParameter(stmt, 1, projectId, Integer.class);
					
					try(ResultSet rs = stmt.executeQuery()){
						if(rs.next()) {
							project = extract(rs, Project.class);
						}
					} //stmt.executeQuery()
				} //conn.prepareStatement(sql)
				
				if(Objects.nonNull(project)) {
					project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
					project.getSteps().addAll(fetchStepsForProject(conn, projectId));
					project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
				}
				
				commitTransaction(conn);
				return Optional.ofNullable(project);
			}//project = null;
			
			catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}// outer most try()
		catch(SQLException e) {
		throw new DbException(e);
		}
	}
	

	/**********************************************************************/
	private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException {
		
		String sql = "SELECT c.* FROM " + CATEGORY_TABLE + " c JOIN " 
		+ PROJECT_CATEGORY_TABLE + " pc ON c.category_id = pc.category_id "
		+ "WHERE pc.project_id = ?";
		

		try(PreparedStatement stmt = conn.prepareStatement(sql)){
			setParameter(stmt, 1, projectId, Integer.class);
			
			try(ResultSet rs = stmt.executeQuery()){
				List<Category> categories = new LinkedList<Category>();
					
				while(rs.next()) {
					categories.add(extract(rs, Category.class));
				}// while ()
					
				return categories;
			}
		}
	}

	/**********************************************************************/
	private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {
		
		String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";
		
		try(PreparedStatement stmt = conn.prepareStatement(sql)){
			setParameter(stmt, 1, projectId, Integer.class);
			
			try(ResultSet rs = stmt.executeQuery()){
				List<Step> steps = new LinkedList<Step>();
					
				while(rs.next()) {
					steps.add(extract(rs, Step.class));
				}// while ()
					
				return steps;
			}
		}
	}

	/**********************************************************************/
	private List< Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException {
		
		String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";
		
		try(PreparedStatement stmt = conn.prepareStatement(sql)){
			setParameter(stmt, 1, projectId, Integer.class);
			
			try(ResultSet rs = stmt.executeQuery()){
				List<Material> materials = new LinkedList<Material>();
					
				while(rs.next()) {
					materials.add(extract(rs, Material.class));
				}// while ()
					
				return materials;
			}
		}
	}
	
	/************************************************************************/
	// /*
	
	public boolean modifyProjectDetails(Project project) {
			
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			
			try(CallableStatement stmt = conn.prepareCall("{call update_project(?, ?, ?, ?, ?, ?)}")){
				stmt.setString(1, project.getProjectName());
				stmt.setBigDecimal(2, project.getEstimatedHours());
				stmt.setBigDecimal(3, project.getActualHours());
				stmt.setInt(4, project.getDifficulty());
				stmt.setString(5, project.getNotes());
				stmt.setInt(6, project.getProjectId());
				
				boolean updatedProject = stmt.executeUpdate() == 1;
		        commitTransaction(conn);
		        
		        System.out.println("\nThe project was updated and the value is: " + updatedProject);
		        
		        return updatedProject;
			
			}catch(Exception e){
				rollbackTransaction(conn);
				throw new DbException(e);
			}
			
		} // outer try()
		catch (SQLException e) {
			throw new DbException(e);
		}
	}
	
	/************************************************************************/
		
	public boolean deleteProject(Integer projectId) {
		
		String sql = "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				setParameter(stmt, 1, projectId, Integer.class);
				
				System.out.println("\nBEFORE deletion, Projects:");  // added for testing
				listAllProjects(conn);  // added for testing
				
				boolean updated = stmt.executeUpdate() == 1;
				commitTransaction(conn);
				        
				System.out.println("\nThe project was deleted : " + updated);
				
				System.out.println("\nAFTER deletion, Projects:");  // added for testing
				listAllProjects(conn);  // added for testing        
				
				return updated;
					
				}catch(Exception e){
					rollbackTransaction(conn);
					throw new DbException(e);
				}
					
			} // outer try()
			catch (SQLException e) {
				throw new DbException(e);
			}	
	}
	
	 
	/************************************************************************/
	private void listAllProjects(Connection conn) throws SQLException {
		List<Project> projects = new LinkedList<Project>();
		
		try(CallableStatement stmt = conn.prepareCall("{call list_projects()}")){
			
			try(ResultSet rs = stmt.executeQuery()){
			
				while(rs.next()) {
					projects.add(extract(rs, Project.class));
				
			}// while ()
						
			projects.forEach(project -> System.out.println("   " + project.getProjectId() + ": " + project.getProjectName()));
			} // inner try
		}// outer try
	}
	
	/************************************************************************/
	public void executeBatch(List<String> sqlBatch) {
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			
			try(Statement stmt = conn.createStatement()){
				for(String sql : sqlBatch) {
					stmt.addBatch(sql);
				}
				
				stmt.executeBatch();
				commitTransaction(conn);
				
			}// inner try()
			catch(Exception e){
				rollbackTransaction(conn);
				throw new DbException(e);
				
			}
			
		} // outer try()
		catch (SQLException e) {
			throw new DbException(e);
		}
		
	}

	/***********************************************************************************/
}

	
	
	
