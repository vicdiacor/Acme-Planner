/*
 * AcmeTest.java
 *
 * Copyright (C) 2012-2021 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.testing;

import java.util.List;

import org.hibernate.internal.util.StringHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import acme.framework.testing.AbstractTest;

public abstract class AcmeTest extends AbstractTest {

	// Check methods ----------------------------------------------------------

	protected void checkLinkExists(final String label) {
		assert !StringHelper.isBlank(label);

		By locator;

		locator = By.xpath(String.format("//a[normalize-space()='%s']", label));
		assert super.exists(locator) : String.format("Cannot find link '%s'", label);
	}

	protected void checkButtonExists(final String label) {
		assert !StringHelper.isBlank(label);

		By locator;

		locator = By.xpath(String.format("//button[@type='submit' and normalize-space()='%s']", label));
		assert super.exists(locator) : String.format("Cannot find button '%s'", label);
	}

	protected void checkAlertExists(final boolean success) {
		By locator;
		String className;

		className = success ? "alert-success" : "alert-danger";
		locator = By.xpath(String.format("//div[contains(@class, '%s')]", className));
		assert super.exists(locator) : String.format("Cannot find alert '%s'", className);
	}

	protected void checkPanicExists() {
		By locator;

		locator = By.xpath("//h1[normalize-space() = 'Unexpected error']");
		assert super.exists(locator) : "Action didn't result in panic";		
	}

	protected void checkNotPanicExists() {
		By locator;

		locator = By.xpath("h1[normalize-space() = 'Unexpected error'");
		assert !super.exists(locator) : "Action resulted in panic";
	}

	protected void checkErrorsExist() {
		By locator;
		List<WebElement> errors;

		locator = By.className("text-danger");
		errors = super.locateMany(locator);
		assert !errors.isEmpty() : "Errors were expected in current form";
	}

	protected void checkErrorsExist(final String name) {
		assert !StringHelper.isBlank(name);

		String xpath;
		By locator;

		xpath = String.format("//div[@class='form-group'][.//*[@id='%s'] and .//div[@class='text-danger']]", name);
		locator = By.xpath(xpath);
		assert super.exists(locator) : String.format("No errors found in input box '%s'", name);
	}

	protected void checkNotErrorsExist() {
		By locator;
		List<WebElement> errors;

		locator = By.className("text-danger");
		errors = super.locateMany(locator);
		assert errors.isEmpty() : "No errors were expected in current form";
	}

	protected void checkNotErrorsExist(final String name) {
		assert !StringHelper.isBlank(name);

		String xpath;
		By inputGroupLocator;

		xpath = String.format("//div[@class='form-group'][.//*[@id='%s'] and .//div[@class='text-danger']]", name);
		inputGroupLocator = By.xpath(xpath);
		assert !super.exists(inputGroupLocator) : String.format("Unexpected errors in input box '%s'", name);
	}

	protected void checkInputBoxHasValue(final String name, final String expectedValue) {
		assert !StringHelper.isBlank(name);
		// expectedValue is nullable

		By inputLocator, optionLocator;
		String inputTag, inputType;
		WebElement inputBox;
		WebElement option;
		String contents, value;

		inputLocator = By.name(name);
		inputBox = super.locateOne(inputLocator);
		inputTag = inputBox.getTagName();
		switch (inputTag) {
		case "textarea":
			contents = inputBox.getAttribute("value");
			break;
		case "input":
			inputType = inputBox.getAttribute("type");
			switch (inputType) {
			case "text":
			case "password":
			case "hidden":
				contents = inputBox.getAttribute("value");
				break;
			default:
				contents = null;
				assert false : String.format("Cannot check an input box of type '%s/%s'", inputTag, inputType);
			}
			break;
		case "select":
			optionLocator = By.xpath(String.format("//select[@name='%s']/option[@selected]", name));
			assert super.exists(optionLocator) : String.format("Cannot find selected option in input box '%s'", name);
			option = super.locateOne(optionLocator);
			contents = option.getText();
			break;
		default:
			contents = null;
			assert false : String.format("Cannot check an input box of type '%s'", inputTag);
		}
		contents = (contents == null ? "" : contents.trim());
		value = (expectedValue != null ? expectedValue.trim() : "");

		assert contents.equals(value) : String.format("Expected value '%s' in input box '%s', but '%s' was found", value, name, contents);
	}

	protected void checkColumnHasValue(final int recordIndex, final int attributeIndex, final String expectedValue) {
		assert recordIndex >= 0;
		assert attributeIndex >= 0;
		// expectedValue is nullable

		List<WebElement> row;
		WebElement attribute, toggle;
		String contents, value;

		row = this.getListingRecord(recordIndex);
		assert attributeIndex + 1 < row.size() : String.format("Attribute %d in record %d is out of range", attributeIndex, recordIndex);
		attribute = row.get(attributeIndex + 1);
		if (attribute.isDisplayed())
			contents = attribute.getText();
		else {
			toggle = row.get(0);
			toggle.click();
			contents = (String) super.executor.executeScript("return arguments[0].innerText;", attribute);
			toggle.click();
		}

		contents = (contents == null ? "" : contents.trim());
		value = (expectedValue != null ? expectedValue.trim() : "");

		assert contents.equals(value) : String.format("Expected value '%s' in attribute %d of record %d, but found '%s'", value, attributeIndex, recordIndex, contents);
	}
	
	//Este método permite evitar la repetición del xpath cuando buscamos en tablas de las que conocemos el id
    protected void checkTableHasValue(final String tableId, final int tableCell, final String expectedValue) {
        
        final String xpath = "//table[@id='" + tableId + "']//tr[" + tableCell + "]/td";
        final String cellContent = super.locateOne(By.xpath(xpath)).getText();
        
        assert expectedValue.equals(cellContent) 
                            : String.format("Expected value '%s' in table with id =  %s and cell = %s, but found '%s'", expectedValue, tableId, tableCell, cellContent);
    }
    
    //Este método permite tener un test mucho más simple y evita tener que poner valores triviales como el número de celda 
    //concreta cuando se va a testear una tabla del dashbaord
    protected void checkTableWithId(final String tableId, final String firstExpectedValue, final String secondExpectedValue, 
                                                final String thirdExpectedValue, final String fourthExpectedValue) {
        
        
        final String xpath1 = "//table[@id='" + tableId + "']//tr[" + 1 + "]/td";
        final String cellContent1 = super.locateOne(By.xpath(xpath1)).getText();
        final String xpath2 = "//table[@id='" + tableId + "']//tr[" + 2 + "]/td";
        final String cellContent2 = super.locateOne(By.xpath(xpath2)).getText();
        final String xpath3 = "//table[@id='" + tableId + "']//tr[" + 3 + "]/td";
        final String cellContent3 = super.locateOne(By.xpath(xpath3)).getText();
        final String xpath4 = "//table[@id='" + tableId + "']//tr[" + 4 + "]/td";
        final String cellContent4 = super.locateOne(By.xpath(xpath4)).getText();
        
        assert firstExpectedValue.equals(cellContent1) 
        : String.format("Expected value '%s' in table with id =  %s and cell = %d, but found '%s'", firstExpectedValue, tableId, 1, cellContent1);
        assert secondExpectedValue.equals(cellContent2) 
        : String.format("Expected value '%s' in table with id =  %s and cell = %d, but found '%s'", secondExpectedValue, tableId, 2, cellContent2);
        assert thirdExpectedValue.equals(cellContent3) 
        : String.format("Expected value '%s' in table with id =  %s and cell = %d, but found '%s'", thirdExpectedValue, tableId, 3, cellContent3);
        assert fourthExpectedValue.equals(cellContent4) 
        : String.format("Expected value '%s' in table with id =  %s and cell = %d, but found '%s'", fourthExpectedValue, tableId, 4, cellContent4);
        
    }

	// Form-filling methods ---------------------------------------------------

	protected void fillInputBoxIn(final String name, final String value) {
		assert !StringHelper.isBlank(name);
		// value is nullable

		By inputLocator, proxyLocator, optionLocator;
		String inputTag, inputType, proxyXpath;
		WebElement inputBox, inputProxy, option;

		inputLocator = By.name(name);
		inputBox = super.locateOne(inputLocator);
		inputTag = inputBox.getTagName();
		switch (inputTag) {
		case "textarea":
			super.fill(inputLocator, value);
			break;
		case "input":
			inputType = inputBox.getAttribute("type");
			switch (inputType) {
			case "text":
			case "password":
				super.fill(inputLocator, value);
				break;
			case "hidden":
				proxyXpath = String.format("//input[@name='%s$proxy' and @type='checkbox']", name);
				proxyLocator = By.xpath(proxyXpath);
				assert value == null || value.equals("true") || value.equals("false") : String.format("Input box '%s' cannot be set to '%s'", name, value);
				assert super.exists(proxyLocator) : String.format("Cannot find proxy for input box '%s'", name);
				inputProxy = super.locateOne(proxyLocator);
				if (inputProxy.getAttribute("checked") != null && (value == null || value.equals("false")))
					inputProxy.click();
				else if (inputProxy.getAttribute("checked") == null && value.equals("true"))
					inputProxy.click();
				break;
			default:
				assert false : String.format("Cannot fill input box '%s/%s' in", name, inputType);
			}
			break;
		case "select":
			optionLocator = By.xpath(String.format("//select[@name='%s']/option[@value='%s']", name, value == null ? "" : value));
			assert super.exists(optionLocator) : "Cannot find option with requested value in select";
			option = super.locateOne(optionLocator);
			option.click();
			break;
		default:
			assert false : String.format("Cannot fill input box '%s' in", name);
		}
	}

	// Click-related methods --------------------------------------------------

	protected void clickOnMenu(final String header, final String option) {
		assert !StringHelper.isBlank(header);
		assert option == null || !StringHelper.isBlank(option);

		By toggleLocator, headerLocator, optionLocator;
		WebElement toggle;
		String ariaExpanded;

		try {
			toggleLocator = By.xpath("//button[@class='navbar-toggler']");
			toggle = super.locateOne(toggleLocator);
			if (toggle.isDisplayed()) {
				ariaExpanded = toggle.getAttribute("aria-expanded");
				if (ariaExpanded == null)
					super.clickAndGo(toggle);
			}
		} catch (final Throwable oops) {
			// INFO: Can silently ignore the exception here.
			// INFO+ Sometimes, the toggle gets stale unexpectedly.
		}

		headerLocator = By.xpath(String.format("//div[@id='mainMenu']/ul/li/a[normalize-space()='%s']", header));
		if (option == null)
			super.clickAndWait(headerLocator);
		else {
			try {
				super.clickAndGo(headerLocator);
			} catch (final Throwable oops) {
				// INFO: Can silently ignore the exception here.
				// INFO+ Sometimes, the toggle gets stale unexpectedly
				// INFO+ and that has an impact on the main menu.
			} 
			optionLocator = By.xpath(String.format("//div[@id='mainMenu']/ul/li[a[normalize-space()='%s']]/div[contains(@class, 'dropdown-menu')]/a[normalize-space()='%s']", header, option));
			super.clickAndWait(optionLocator);
		}
	}

	protected void clickOnLink(final String label) {
		assert !StringHelper.isBlank(label);

		By locator;

		locator = By.xpath(String.format("//a[normalize-space()='%s']", label));
		super.clickAndWait(locator);
	}

	protected void clickOnListingRecord(final int recordIndex) {
		assert recordIndex >= 0;

		List<WebElement> record;
		WebElement column;

		record = this.getListingRecord(recordIndex);
		column = record.get(1);
		super.clickAndWait(column);
	}

	protected void clickOnSubmitButton(final String label) {
		assert !StringHelper.isBlank(label);

		By locator;

		locator = By.xpath(String.format("//button[@type='submit' and normalize-space()='%s']", label));
		super.clickAndWait(locator);
	}

	protected void clickOnReturnButton(final String label) {
		assert !StringHelper.isBlank(label);

		By locator;

		locator = By.xpath(String.format("//button[normalize-space()='%s']", label));
		super.clickAndWait(locator);
	}

	// Ancillary methods ------------------------------------------------------

	protected List<WebElement> getListingRecord(final int recordIndex) {
		assert recordIndex >= 0;

		List<WebElement> result;
		int pageIndex, rowIndex;
		By listLocator, lengthLocator, paginatorLocator, pageLinkLocator, rowLocator, columnLocator;
		WebElement list, lengthOption, paginator, pageLink, row;
		List<WebElement> pageLinks, rows;

		pageIndex = recordIndex / 5;
		rowIndex = 1 + recordIndex % 5;

		listLocator = By.id("list");
		list = super.locateOne(listLocator);
		lengthLocator = By.xpath("//select[@name='list_length']/option[@value='5']");
		lengthOption = super.locateOne(lengthLocator);
		super.clickAndGo(lengthOption);

		paginatorLocator = By.className("pagination");
		paginator = super.locateOne(paginatorLocator);
		pageLinkLocator = By.className("page-link");
		pageLinks = paginator.findElements(pageLinkLocator);
		assert pageIndex < pageLinks.size() : String.format("Record index %d is out of range", recordIndex);
		pageLink = pageLinks.get(pageIndex);
		super.clickAndGo(pageLink);

		rowLocator = By.tagName("tr");
		rows = list.findElements(rowLocator);
		assert rowIndex < rows.size() : String.format("Record index %d is out of range", recordIndex);
		row = rows.get(rowIndex);
		columnLocator = By.tagName("td");
		result = row.findElements(columnLocator);

		return result;
	}
	
	
protected WebElement getRowAsWebElement(final int recordIndex) {
		
		assert recordIndex >= 0;
		int pageIndex, rowIndex;
		By listLocator, lengthLocator, paginatorLocator, pageLinkLocator, rowLocator;
		
		WebElement list, lengthOption, paginator, pageLink, row;
		List<WebElement> pageLinks, rows;

		pageIndex = recordIndex / 5;
		rowIndex = 1 + recordIndex % 5;

		listLocator = By.id("list");
		list = super.locateOne(listLocator);
		lengthLocator = By.xpath("//select[@name='list_length']/option[@value='5']");
		lengthOption = super.locateOne(lengthLocator);
		super.clickAndGo(lengthOption);

		paginatorLocator = By.className("pagination");
		paginator = super.locateOne(paginatorLocator);
		pageLinkLocator = By.className("page-link");
		pageLinks = paginator.findElements(pageLinkLocator);
		assert pageIndex < pageLinks.size() : String.format("Record index %d is out of range", recordIndex);
		pageLink = pageLinks.get(pageIndex);
		super.clickAndGo(pageLink);

		rowLocator = By.tagName("tr");
		rows = list.findElements(rowLocator);
		assert rowIndex < rows.size() : String.format("Record index %d is out of range", recordIndex);
		row = rows.get(rowIndex);
		return row;
	}

}
