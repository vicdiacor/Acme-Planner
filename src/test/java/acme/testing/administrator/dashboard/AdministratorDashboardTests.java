
package acme.testing.administrator.dashboard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.By;

import acme.testing.AcmeWorkPlansTest;

public class AdministratorDashboardTests extends AcmeWorkPlansTest {

	//Test cases--------------------------------------

	/**
	 * La feature que prueba este test es el dashboard de administrator con todas las 
	 * métricas de la página. Aquí comprobamos las métricas de la página según el 
	 * sample data. Crearemos una nueva task en cada iteración con diferentes workloads para ver cómo van cambiando las métricas (cambiará el workload minimo, máximo, la media etc)
	 * <p>
	 * Una vez iniciada sesion como administrador, se accede a su menu desplegable y se entra
	 * al dashboard, donde se pueden ver todas las métricas organizadas.
	 */
	
	@ParameterizedTest
	@CsvFileSource(resources = "/administrator/dashboard/dashboard-positive.csv", encoding = "utf-8", numLinesToSkip = 1)
	@Order(10)
	public void administratorDashboardPositive(final int recordIndex, final String numberPublicTasks, final String numberPrivateTasks, final String numberFinishedTasks, 
															final String numberNonFinishedTasks, final String averageTaskExecPeriod, final String deviationTaskExecPeriod, 
															final String maxTaskExecPeriod, final String minTaskExecPeriod, final String averageTaskWorkload, 
															final String deviationTaskWorkload, final String maxTaskWorkload, final String minTaskWorkload,
															final String description,final String end_date,final String optional_link,final String start_date,final String title,final String visibility,final String workload) {

		//Creación nueva task
		super.signIn("manager1", "manager1");

		super.clickOnMenu("Manager", "Create task");
		
		super.fillInputBoxIn("description", description);
		super.fillInputBoxIn("endDate", end_date );
		super.fillInputBoxIn("optionalLink", optional_link);
		super.fillInputBoxIn("startDate", start_date);
		super.fillInputBoxIn("title", title );
		super.fillInputBoxIn("visibility", visibility);		
		super.fillInputBoxIn("workload", workload );
		super.clickOnSubmitButton("Create");
		Assertions.assertFalse(super.exists(By.className("text-danger")));
		super.signOut();
		
		//Comprobar las métricas del dashboard
		super.signIn("administrator", "administrator");

		super.clickOnMenu("Administrator", "Dashboard");

		super.checkTableWithId("tasks", numberPublicTasks, numberPrivateTasks, numberFinishedTasks, numberNonFinishedTasks);
		super.checkTableWithId("executionPeriods", averageTaskExecPeriod, deviationTaskExecPeriod, maxTaskExecPeriod, minTaskExecPeriod);
		super.checkTableWithId("workloads", averageTaskWorkload, deviationTaskWorkload, maxTaskWorkload, minTaskWorkload);

		super.signOut();
		
		
	}

	/**
	 * La feature que prueba este test es el dashboard de administrator con todas las 
	 * métricas de la página pero en este caso no se podrá entrar ya que manager 
	 * no dispone de los permisos.
	 * <p>
	 * Una vez iniciada sesion como manaager, se intenta acceder al dashboard desde la url
	 * pero al no tener permisos nos devolverá un panic.
	 */
	
	@Test
	@Order(20)
	public void managerDashboardNegative() {

		super.navigateHome();
		super.signIn("manager1", "manager1");

		super.driver.get("http://localhost:8090/Acme-Work-Plans/administrator/dashboard/show");
		super.checkPanicExists();
		
		super.signOut();
	}
	
	/**
	 * La feature que prueba este test es el dashboard de administrator con todas las 
	 * métricas de la página pero en este caso no se podrá entrar ya que anonymous 
	 * no dispone de los permisos.
	 * <p>
	 * Desde anonymous, se intenta acceder al dashboard desde la url pero al no tener 
	 * permisos nos devolverá un panic.
	 */
	
	@Test
	@Order(30)
	public void anonymousDashboardNegative() {

		super.navigateHome();

		super.driver.get("http://localhost:8090/Acme-Work-Plans/administrator/dashboard/show");
		super.checkPanicExists();
		
	}
	
	

}
