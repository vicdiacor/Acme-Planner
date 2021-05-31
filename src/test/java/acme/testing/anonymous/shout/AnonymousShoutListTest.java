
package acme.testing.anonymous.shout;

import java.time.LocalDate;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.By;

import acme.components.UtilComponent;
import acme.testing.AcmePlannerTest;

public class AnonymousShoutListTest extends AcmePlannerTest {

	

	

	/**
	 * La feature que prueba este test es la de listar shouts como anonymous, pero el caso negativo, que seria intentando acceder al listado con un usuario logueado con
	 * el rol de Administrador
	 * <p>
	 * Una vez loguado como administrator, lo primero es comprobar que no existe la seccion de anonymous. Despues intentamos acceder mediante url
	 * al listado de shouts", y comprobamos que el resultado es una pagina de error, ya que no está autorizado.
	 */

	@Test
	public void anonymousShoutListNegative() {

		super.signIn("administrator", "administrator");
 		super.driver.get("http://localhost:8090/Acme-Planner/anonymous/shout/list");
 		this.checkPanicExists();
		this.signOut();

	}
	
	//Dado que el listado de shouts varía en función de la fecha (se muestran los del último mes), se han adaptado para los siguientes 2
	//Meses: Junio y Julio.
	
	/**
	 * La feature que prueba este test es la de listar tareas siendo un usuario anonymous, es decir, sin autenticar
	 * <p>
	 * Este metodo accede al menu desplegable de anonymous y entra al listado
	 * shouts. Debe comrobar que el listado se muestra igual que nuestro archivo csv de entrada, en el que introducimos
	 *  los registros del sample data que se deben mostrar, es decir, los que tienen un maximo de
	 *  30 dias desde que se publicaron. También se comprueba que al cambiar de idioma a español, el formato de las fechas se muestre 
	 *  de la siguiente forma "día/mes/año horas:minutos"
	 */
	@ParameterizedTest
	@CsvFileSource(resources = "/anonymous/shout/list-recent-shouts-june.csv", encoding = "utf-8", numLinesToSkip = 1)
	@Order(20)
	public void anonymousShoutListPositiveJune(final int recordIndex,final String moment,final String author,final String text) {		
		if (LocalDate.now().isBefore(LocalDate.of(2021, 6, 30))&& LocalDate.now().isAfter(LocalDate.of(2021, 4, 30))) {
		
		super.clickOnMenu("Anonymous", "List of shouts");
		
		super.checkColumnHasValue(recordIndex, 0, moment);
		super.checkColumnHasValue(recordIndex, 1, author);
		super.checkColumnHasValue(recordIndex, 2, text);
		
		super.navigateHome();
		super.clickAndGo(By.xpath("/html/body/footer/div/div[3]/ul/li[2]/a")); //Spanish button
		
		super.clickOnMenu("Anónimo", "Lista de shouts");
		super.checkColumnHasValue(recordIndex, 0, UtilComponent.formatDateStringToSpanish(moment));
		super.checkColumnHasValue(recordIndex, 1, author);
		super.checkColumnHasValue(recordIndex, 2, text);
		
		
		}
	}
	
	/**
	 * La feature que prueba este test es la de listar tareas siendo un usuario anonymous, es decir, sin autenticar
	 * <p>
	 * Este metodo accede al menu desplegable de anonymous y entra al listado
	 * shouts. Debe comrobar que el listado se muestra igual que nuestro archivo csv de entrada, en el que introducimos
	 *  los registros del sample data que se deben mostrar, es decir, los que tienen un maximo de
	 *  30 dias desde que se publicaron.También se comprueba que al cambiar de idioma a español, el formato de las fechas se muestre 
	 *  de la siguiente forma "día/mes/año horas:minutos"
	 */
	@ParameterizedTest
	@CsvFileSource(resources = "/anonymous/shout/list-recent-shouts-july.csv", encoding = "utf-8", numLinesToSkip = 1)
	@Order(30)
	public void anonymousShoutListPositiveJuly(final int recordIndex,final String moment,final String author,final String text) {		
	 
		if (LocalDate.now().isBefore(LocalDate.of(2021, 7, 31)) && LocalDate.now().isAfter(LocalDate.of(2021, 6, 30))) {
		super.clickOnMenu("Anonymous", "List of shouts");
		
		super.checkColumnHasValue(recordIndex, 0, moment);
		super.checkColumnHasValue(recordIndex, 1, author);
		super.checkColumnHasValue(recordIndex, 2, text);
		
		super.navigateHome();
		super.clickAndGo(By.xpath("/html/body/footer/div/div[3]/ul/li[2]/a")); //Spanish button
		
		super.clickOnMenu("Anónimo", "Lista de shouts");
		super.checkColumnHasValue(recordIndex, 0, UtilComponent.formatDateStringToSpanish(moment));
		super.checkColumnHasValue(recordIndex, 1, author);
		super.checkColumnHasValue(recordIndex, 2, text);
		
		}
	}

	
	

}
