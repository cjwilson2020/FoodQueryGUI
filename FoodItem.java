/**
 * Filename:   FoodItem.java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents a food item with all its properties.
 * 
 * @author Christopher Wilson
 */
public class FoodItem {
	// The name of the food item.
	private String name;

	// The id of the food item.
	private String id;

	// Map of nutrients and value.
	private HashMap<String, Double> nutrients;

	/**
	 * Constructor
	 * @param name name of the food item
	 * @param id unique id of the food item 
	 */
	public FoodItem(String id, String name) {
		this.id = id;
		this.name = name;
		nutrients = new HashMap<String, Double>();
	}

	/**
	 * Gets the name of the food item
	 * 
	 * @return name of the food item
	 */
	public String getName() {
		return name;
	}
	
	/**
     * Gets the name of the food item
     * 
     * @return name of the food item
     */
	@Override
    public String toString() {
        return name;
    }

	/**
	 * Gets the unique id of the food item
	 * 
	 * @return id of the food item
	 */
	public String getID() {
		return id;
	}

	/**
	 * Gets the nutrients of the food item
	 * 
	 * @return nutrients of the food item
	 */
	public HashMap<String, Double> getNutrients() {
		return nutrients;
	}

	/**
	 * Adds a nutrient and its value to this food. 
	 * If nutrient already exists, updates its value.
	 */
	public void addNutrient(String name, double value) {
		if (nutrients.containsKey(name)) {
			nutrients.replace(name, nutrients.get(name), value);
		}else {
			nutrients.put(name, value);
		}
	}

	/**
	 * Returns the value of the given nutrient for this food item. 
	 * If not present, then returns 0.
	 */
	public double getNutrientValue(String name) {
		if (nutrients.containsValue(name)) {
			return nutrients.get(name);
		}
		return 0;
	}
	 /**
	  * Sets the nutrients to a HashMap n
	  * @param n
	  */
	public void setNutrients(HashMap<String, Double> n){
		nutrients = n;		
	}
}