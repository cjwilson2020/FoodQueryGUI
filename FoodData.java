/**
 * Filename:   FoodData.java
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
import java.util.Iterator;
import java.io.*;

/**
 * This class represents the backend for managing all 
 * the operations associated with FoodItems
 * 
 * @author sapan (sapan@cs.wisc.edu)
 */
public class FoodData implements FoodDataADT<FoodItem> {

	// List of all the food items.
	private List<FoodItem> foodItemList;
	private List<FoodItem> fList;


	// Map of nutrients and their corresponding index
	private HashMap<String, BPTree<Double, FoodItem>> indexes;

	/**
	 * Public constructor
	 */
	public FoodData() {
		foodItemList = new ArrayList<FoodItem>();
		fList = new ArrayList<FoodItem>();
		indexes = new HashMap<String, BPTree<Double, FoodItem>>();
		//		indexes.put("Fiber", value)
	}


	/**
	 * (non-Javadoc)
	 * @see skeleton.FoodDataADT#loadFoodItems(java.lang.String)
	 * 
	 * This method takes the input csv file or txt file and creates a FoodItem List and HashMap
	 *  
	 */
	@Override
	public void loadFoodItems(String filePath) {
		// read in food data from a csv or txt (given correct format) file
		try {
			File file = new File(filePath);
			if(file.exists()) {
				FileReader fRead = new FileReader(file);
				BufferedReader bR = new BufferedReader(fRead);
				// parse input
				while(bR.ready()) {
					String s = bR.readLine(); //line of the file
					String[] info = s.split(","); //divides line by "," delimiter

					/* WITH THE IF STATEMENT BELOW
					 * In the foodItems.txt file there are lines the just have ,,,,,,,,
					 * So, this would mean that info is loaded with nothing or is null when
					 * the bufferedReader reaches those lines it skips them. 
					 * 
					 * There is potential that there ae lines later in the file with info,
					 * so instead of stopping the while loop, I just have it load the List
					 * with lines and skip empty lines
					 */

					if ((info != null) && (info.length > 1)) { 
						FoodItem item = new FoodItem(info[0], info[1]); //Creates food item with id of info[0] and name of info[1]
						for (int i = 2; i < info.length;i = i + 2) { //iterates through each nutrition name
							item.addNutrient(info[i], (double)Double.parseDouble(info[i+1])); //name is info[i], nutrition value is info[i + 1]
						}
						addFoodItem(item); //adds "item" to the foodItemList
						fList.add(item);
					}
					// Method tested by putting the data in different areas 
					// of the file, SOOO this is a ROBUST Method
				}
				bR.close();
				fRead.close();
			}else {
				foodItemList.clear();
			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException i) {
			i.printStackTrace();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	/**
	 * (non-Javadoc)
	 * @see skeleton.FoodDataADT#filterByName(java.lang.String)
	 */
	@Override
	public List<FoodItem> filterByName(String substring) {
		List<FoodItem> filteredList = new ArrayList<FoodItem>();

		for (FoodItem curr: fList) { //iterates through each FoodItem in foodItemList while the iterator hasNext

			// if curr name contains the substring, add it to a new food list

			if (curr.getName().toLowerCase().contains(substring.toLowerCase())){
				FoodItem newItem = new FoodItem(curr.getID(),curr.getName()); //make a food item with the same iD and name
				//make sure the nutrients of this food item are the same as the curr

				//TODO : Copy the nutrients hashMap from curr item to new item
				newItem.getNutrients().putAll(curr.getNutrients());


				//				newItem.setNutrients(curr.getNutrients());


				//Add to filtered list
				if (!filteredList.contains(newItem)) {
					filteredList.add(newItem);
				}
			}

		}
		fList = filteredList;
		return fList;
	}

	/**
	 * (non-Javadoc)
	 * @see skeleton.FoodDataADT#filterByNutrients(java.util.List)
	 */
	@Override
	public List<FoodItem> filterByNutrients(List<String> rules) {
		List<FoodItem> filteredList = new ArrayList<FoodItem>();

		//Split a rule into its components  
		//nutrient -> comparator -> value
		ArrayList<String[]> listOfRules = new ArrayList<String[]>();
		for (String line : rules) {
			listOfRules.add(line.split(" "));
		}

		// iterate over the food item list to select all data correctly
		for (FoodItem food: fList) {

			// test against all rules
			for (String[] operands: listOfRules) { 
				// all foods matching less than or equals to
				if (operands[1].equals("<=")) {
					if (food.getNutrients().get(operands[0]) <= Double.parseDouble(operands[2])){
					} else {
						break;
					}

				} 
				// all foods matching greater than or equals to
				else if (operands[1].equals(">=")) {
					if (food.getNutrients().get(operands[0]) >= Double.parseDouble(operands[2])){
					}else {
						break;
					}
				}

				// all foods matching exactly equals to 
				else if (operands[1].equals("==")) {
					Double val = food.getNutrients().get(operands[0]);
					if (val.equals(Double.parseDouble(operands[2]))){
					}else {
						break;
					}
				}
				// add selected food back in to the filtered list
				if (!filteredList.contains(food)) {

					filteredList.add(food);
				}
			}

		}
		// return the final filtered list
		fList = filteredList;
		return fList;
	}

	/**
	 * add a food item to the food data structure
	 * @see skeleton.FoodDataADT#addFoodItem(skeleton.FoodItem)
	 */
	@Override
	public void addFoodItem(FoodItem foodItem) {
		foodItemList.add(foodItem);
	}

	/**
	 * return a list of all food items
	 * @see skeleton.FoodDataADT#getAllFoodItems()
	 */
	@Override
	public List<FoodItem> getAllFoodItems() {
		return foodItemList;
	}

	/**
	 *  return a list of the food item's names as strings 
	 * @return
	 */
	public List<String> toStringList() {
		List<String> foodItemStringList = new ArrayList<>();
		for (FoodItem item:foodItemList) {
			foodItemStringList.add(item.getName());
		}
		return foodItemStringList;
	}

	/**
	 * save the selected food items as a csv
	 */
	@Override
	public void saveFoodItems(String filename) {
		File save = new File(filename);
		//Write to this file each food item's info on one line
		// ID,name,nutrient1,value1,nutrient2,value2,nutrient3,value3,nutrient4,value4 \n

		// java.io validation
		try{
			FileWriter fW = new FileWriter(save);
			BufferedWriter writer = new BufferedWriter(fW);
			for (FoodItem item: foodItemList) {
				String iD = item.getID();
				String name = item.getName();
				//we want to store nutrient names and their values here
				String[] nutrients = new String[item.getNutrients().size()*2]; 
				//nutrients.size is the number of names of nutrients. Each nutrient has a value, so we multiply by 2

				String[] typesN = {"calories", "fat", "carbohydrate","fiber", "protein"};
				for(int i = 0; i < nutrients.length; i=i+2) { // We will load this array with each nutrient and value
					nutrients[i] = typesN[i/2];//stores nutrinet name first
					nutrients[i+1] = item.getNutrients().get(typesN[i/2]).toString(); //stores nutrient value directly after
				}

				String line = iD + "," + name; //adds id and name
				for(int i = 0; i < nutrients.length; i++) { //adds all this item's nutrients
					line = line + "," + nutrients[i];
				}

				writer.write(line+"\n");
			}
			writer.close();
			fW.close();
		}catch (IOException i){
			i.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}



	}
}