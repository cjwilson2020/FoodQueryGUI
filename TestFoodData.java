/**
 * Filename:   TestFoodData.java
 * Project:    Food Query
 * Authors:    Debra Deppeler, Josiah Fee, Aaron Kelly, Chris Willson, Chloe 
 * Chan
 *
 * Semester:   Fall 2018
 * Course:     CS400
 * Lecture:    001
 * 
 * Due Date:   Before 10pm on December 13, 2018
 */

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
/*
 * If you have an error that org.junit cannot be resolved
 * right click on your project folder >>go to build path >>
 * Add Library>> click JUnit>> select JUnit 4 >> Finish
 */
/**
 * Test class for the food data. Methods to test expected behavior
 *
 */
public class TestFoodData {
	FoodData food;
	String filePath = "ideal-pancake/foodItems.csv";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}

	@Before
	public void setUp() throws Exception {
		food = new FoodData();
	}

	@After
	public void tearDown() throws Exception {
		food= null;
	}

	@Test
	public void testLoadFoodItemsList() {
		food.loadFoodItems(filePath);
		List<FoodItem> list = food.getAllFoodItems();
		for (String s: food.toStringList()) {
		    System.out.println(s);
		}
		assertFalse(list.isEmpty());
	}

	@Test
	public void testSaveFoodItems() {
		food.loadFoodItems(filePath);
	//	Scanner scnr = new Scanner(System.in);
		//System.out.println("Enter the ename you would like to save this file as: ");
	//	String input = scnr.next();
		String saveFileName = "ideal-pancake/test.csv";
	//	String saveFileName = "ideal-pancake/" + input + ".csv";
		food.saveFoodItems(saveFileName);
		File testF = new File(saveFileName);
		assertTrue(testF.exists());
		assertTrue(testF.isFile());
		assertTrue(testF.canWrite());
		assertTrue(testF.canRead());
	}
	
	@Test
	public void testAddFoodItem() {
		int startSize = food.getAllFoodItems().size();
		FoodItem foodItem = new FoodItem("1", "milk");
		food.addFoodItem(foodItem);
		int endSize = food.getAllFoodItems().size();
		assertTrue(endSize > startSize);
	}
	
	@Test
	public void testFilterByName() {
		food.loadFoodItems("ideal-pancake/foodItems.csv");
		List<FoodItem> filter = food.filterByName("yoplait");
		assertTrue(filter.size() <= food.getAllFoodItems().size());
	}
	@Test
	public void testFilterByNutrients() {
		food.loadFoodItems("ideal-pancake/foodItems.csv");
		List<String> rules = new ArrayList<String>();
		rules.add("protein == 2");
		rules.add("calories <= 210");
		rules.add("fiber >= 2");
		
		List<FoodItem> filter = food.filterByNutrients(rules);
		assertTrue(filter.size() <= food.getAllFoodItems().size());
		
		
	}
}