import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.*;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Main class to run Food Query GUI. Contains all UI elements. 
 */
public class Main extends Application {
	@Override

	public void start(Stage primaryStage) {
		try {
			display(primaryStage);



		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param primaryStage
	 */
	public void display(Stage primaryStage) {
		try {
			String instr = "INSTRUCTIONS!\nIn this app you'll be able to load a variety of different foods and analyze "
					+ "their nutrition makeup. You can do this by first selecting the \"Load\" button.\n"
					+ "Once you Load the foods, you can create meals using the \"Add to Meal\" button. This will populate the list on the "
					+ "right of the screen with any food you select to add.\n"
					+ "If you make a mistake with the meal list, you can click on the \"Clear Meal\" button, which will "
					+ "clear the list on the right and give you a second chance at life.\n"
					+ "To update the list on the left with new food items, click on the \"Add Food Item\" button.\n"
					+ "Once you have a solid meal list, click the \"\" button at the bottom left of the window to find the total nutrition info for that meal.\n"
					+ "When you have all the info you need, make sure to save your food list by clicking the \"Save\" button. Enter your file name and press \"ENTER\".";

			primaryStage.setTitle("Food Query App");
			Label greetingLabel = new Label("Food Query App");
			Label foodLabel = new Label("Food List");
			Label mealLabel = new Label("Meal List");
			Label numItems = new Label("Total items: ");
			Label rulesDisplay = new Label("\nRules added to Query:\n");
			Label nameDisplay = new Label("\nKeywords added to Query:\n");

			//Make FoodData Instance for program
			FoodData foodD = new FoodData();			

			//File name entered by user during load
			final String fileName;
			final String saveName;

			//To display rules added to the query, we mist use a label or some 
			//sort of text box to print these, so the user can see them
			List<String> rules = new ArrayList<String>(); //This will contain rules in the format of <nutrient> <comparator> <value> 
			List<String> nameFilters = new ArrayList<String>();
			
			//Create Button
			Button foodButton = new Button("Add Food Item");
			Button analyzeButton = new Button("Analyze Meal");
			Button rulesButton = new Button("Filter By Rules");
			Button filterButton = new Button("Filter By Name");
			Button quitButton = new Button("Quit");
			Button saveButton = new Button("Save");
			Button loadButton = new Button("Load");
			Button mealButton = new Button("Add to Meal");
			Button clearMealButton = new Button("Clear Meal");
			Button clearFilterButton = new Button ("Clear Filter");
			Button helpButton = new Button("Help!");

			//Make list views
			ListView<FoodItem> foodList = new ListView<>();
			ListView<FoodItem> mealList = new ListView<>();
			List<FoodItem> mealArrList = new ArrayList<>();

			//Set size of food and meal lists in program
			foodList.setMaxWidth(600);
			mealList.setMaxWidth(600);

			//Create button actions for each Button

			// Set Analyze Meal action to get and display the
			// amount of each nutrient in the meal
			Label mealDisplay = new Label("\nMeal Nutrition:\n");
			analyzeButton.setOnAction(e ->{

				// initialize the nutrient values in the meal to zero
				if(mealArrList.size() > 0){
					double cals = 0;
					double fats = 0;
					double carbs = 0;
					double fiber = 0;
					double protein = 0;

					// loop through each food in the meal and add to each
					// nutrient category appropriately
					for (FoodItem food : mealArrList) {
						cals = cals + food.getNutrients().get("calories");
						fats = fats + food.getNutrients().get("fat");
						carbs = carbs + food.getNutrients().get("carbohydrate");
						fiber = fiber + food.getNutrients().get("fiber");
						protein = protein + food.getNutrients().get("protein");
					}

					// Display the computed meal nutrition amounts
					mealDisplay.setText("\nMeal Nutrition:\n" 
							+ "Calories: " + cals 
							+ "\nFat: " + fats 
							+ "\nCarbohydrates: " + carbs
							+ "\nFiber: " + fiber
							+ "\nProtein: " + protein);
				}
			});


			//Filter By Rules		
			
			try {
				rulesButton.setOnAction(e -> {
					//Create Choice Dialog Box for Filter By Nutrients
					List<String> nutrients = new ArrayList<String>(); //choices for nutrient
					nutrients.add("calories");
					nutrients.add("fat");
					nutrients.add("carbohydrate");
					nutrients.add("fiber");
					nutrients.add("protein");

					// Create choice dialog box for selecting a comparator
					List<String> comparator = new ArrayList<String>(); //choices for comparator
					comparator.add("<=");
					comparator.add("==");
					comparator.add(">=");

					// create a choice dialog box for selecting the nutrient value
					List<String> values = new ArrayList<String>(); //choices for value
					for (int i = 0; i <501; i++) { //values 0 through 500
						values.add(((Integer)i).toString());
					}

					// create a choice dialog box for filtering by rules
					ChoiceDialog<String> nOption = new ChoiceDialog<>("calories", nutrients);
					nOption.setHeaderText("Select the nutrient you want to filter");
					nOption.setContentText("Filter by  ");
					ChoiceDialog<String> cOption = new ChoiceDialog<>("<=", comparator);

					ChoiceDialog<String> vOption = new ChoiceDialog<>("0", values);

					Optional<String> n = nOption.showAndWait();
					// display the comparator selection and result
					if(n.isPresent()) {
						cOption.setHeaderText("Select the comparator you want to use");
						cOption.setContentText("You selected  " + n.get());
						Optional<String> c = cOption.showAndWait();

						// when the response is present from selecting a 
						// comparator, let the user select a nutrient.
						// value to filter on
						if (c.isPresent()) {
							vOption.setHeaderText("Select desired value");
							vOption.setContentText("All foods with " + n.get() + " " + c.get());
							Optional<String> v = vOption.showAndWait();

							// after a nutrient value is selected, display
							// the resulting matching food
							if(v.isPresent()) {
								String r = n.get() + " " + c.get() + " " + v.get();
								rules.add(r);


								rulesDisplay.setText(rulesDisplay.getText() + r + "\n");


								List<FoodItem> filter = foodD.filterByNutrients(rules);
								ObservableList<FoodItem> f = FXCollections.observableArrayList(filter);
								foodList.setItems(null);
								foodList.setItems(f);
							}
						}
					}

				});
			}catch (NoSuchElementException n) {

			}

			//Add Food Item
			foodButton.setOnAction(e -> {
				Optional<String> r; 
				while(true) {
					TextInputDialog name = new TextInputDialog("");
					name.setHeaderText("Type the food item you want to add");
					r = name.showAndWait();
					if (r.isPresent()){
						if (r.get().length() > 0) {
							break;
						} else {
							Alert d = new Alert(AlertType.ERROR);
							d.setTitle("Add Food Item");
							d.setHeaderText("Food name error");
							d.setContentText("You must enter a name before proceding");

							d.showAndWait();
						}
					}	
				}


				if(r.isPresent()) {
					FoodItem food = new FoodItem("", r.get());

					
					while(true) {//Valid value loop
						//Create CustomDialog Box for adding nutrient values
						ArrayList<Double> vals = new ArrayList<Double>();
						Dialog<ArrayList<Double>> nutr = new Dialog<>(); 

						nutr.setTitle("Add Food Item");
						nutr.setHeaderText("Type numeric values for each of the nutrients below"
								+ "\nNote: Leaving text fields blank will default all"
								+ "\nnutrient values to 0.0");

						// Set the button types.
						ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
						ButtonType cButtonType = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
			
						nutr.getDialogPane().getButtonTypes().addAll(okButtonType, cButtonType);

						// Create the username and password labels and fields.
						GridPane grid = new GridPane();
						grid.setHgap(10);
						grid.setVgap(10);
						grid.setPadding(new Insets(20, 150, 10, 10));

						TextField calories = new TextField();
						calories.setPromptText("0.0");
						TextField fat = new TextField();
						fat.setPromptText("0.0");
						TextField carbohydrate = new TextField();
						carbohydrate.setPromptText("0.0");
						TextField fiber = new TextField();
						fiber.setPromptText("0.0");
						TextField protein = new TextField();
						protein.setPromptText("0.0");

						grid.add(new Label("Calories:"), 0, 0);
						grid.add(calories, 1, 0);
						grid.add(new Label("Fat:"), 0, 1);
						grid.add(fat, 1, 1);
						grid.add(new Label("Carbohydrates:"), 0, 2);
						grid.add(carbohydrate, 1, 2);
						grid.add(new Label("Fiber:"), 0, 3);
						grid.add(fiber, 1, 3);
						grid.add(new Label("Protein:"), 0, 4);
						grid.add(protein, 1, 4);

						nutr.getDialogPane().setContent(grid);

						// Request focus on the calories field by default.
						Platform.runLater(() -> calories.requestFocus());
						Optional<ArrayList<Double>> n;

						
						
						// Convert the result to a list of values when the Ok button is clicked.
						nutr.setResultConverter(dialogButton -> {
							vals.clear(); //clear vals list
							if (dialogButton.equals(okButtonType)) { //Ok is pressed
								try {
									if (calories.getText().isEmpty())
										calories.setText("0.0");
									if (fat.getText().isEmpty())
										fat.setText("0.0");
									if (carbohydrate.getText().isEmpty())
										carbohydrate.setText("0.0");
									if (fiber.getText().isEmpty())
										fiber.setText("0.0");
									if (protein.getText().isEmpty())
										protein.setText("0.0");

									vals.add(Double.parseDouble(calories.getText()));
									vals.add(Double.parseDouble(fat.getText()));
									vals.add(Double.parseDouble(carbohydrate.getText()));
									vals.add(Double.parseDouble(fiber.getText()));
									vals.add(Double.parseDouble(protein.getText()));
									for (Double v : vals) {
										if (v < 0.0) {
											vals.clear();
											throw new NumberFormatException("negative number");
										}
									}
									return vals;
								} catch (NumberFormatException nF) { //Invalid values entered
									Alert d = new Alert(AlertType.ERROR);
									d.setTitle("Add Food Item");
									d.setHeaderText("Error adding food");
									d.setContentText("You may have entered an invalid nutrient value" 
											+ "\nDetails: " + nF.getMessage());

									d.showAndWait();
								}
							} else if (dialogButton.equals(cButtonType)) { //Cancel or Exit is clicked
								for (int x = 0; x < 10 ; x++){
									vals.add(0.0);
									}
								return vals;
							}
							Alert d = new Alert(AlertType.ERROR);
							d.setTitle("Add Food Item");
							d.setHeaderText(null);
							d.setContentText("Please enter a positive numeric value");

							d.showAndWait();
							return null; //Invalid values
						});
						
						//
						n = nutr.showAndWait(); //n can be either full, have only one val, or null
						if (n.isPresent()) { // if n exists
							if(n.get().size() == 10 ) { //if size is one, cancel has been pressed
								break;
							}else {
								food.addNutrient("calories", n.get().get(0));
								food.addNutrient("fat", n.get().get(1));
								food.addNutrient("carbohydrate", n.get().get(2));
								food.addNutrient("fiber", n.get().get(3));
								food.addNutrient("protein", n.get().get(4));

								foodD.addFoodItem(food);
								ObservableList<FoodItem> r1 = FXCollections.observableArrayList(foodD.getAllFoodItems());
								
								//Update total
								if(numItems.getText().length() > 13) {
									numItems.setText(numItems.getText().substring(0, 13) + r1.size());
								}else {
									numItems.setText(numItems.getText() + r1.size());
								}
								foodList.setItems(r1);
								break;
							}

						}
					}

				}
			});

			//Filter by name of food. Matches substrings - user input
			// and food names
			
			try {
				filterButton.setOnAction(e -> {
					//Create Text Input Dialog Box for Filter By Name
					TextInputDialog query = new TextInputDialog("");
					query.setHeaderText("Type the name of the food you want");		

					Optional<String> response = query.showAndWait();
					if(response.isPresent()) {
						String kw = response.get();
						nameFilters.add(kw); //adds keyword to name list
						List<FoodItem> filter = foodD.filterByName(response.get());
						if(rules.size() > 0) {
							foodD.filterByNutrients(rules);
						}
						nameDisplay.setText(nameDisplay.getText() + "\n"
								+ kw);		
						ObservableList<FoodItem> f = FXCollections.observableArrayList(filter);
						
						//Update total
						if(numItems.getText().length() > 13) {
							numItems.setText(numItems.getText().substring(0, 13) + f.size());
						}else {
							numItems.setText(numItems.getText() + f.size());
						}
						
						foodList.setItems(f);
					}
				});
		
			}catch (NoSuchElementException n) {

			}

			// Show help dialog when help button clicked
			helpButton.setOnAction(e -> {
				// Create help dialog box
				Alert help = new Alert(AlertType.INFORMATION);
				help.setTitle("Help!");
				help.setHeaderText("Instructions for Use");
				help.setContentText(instr);

				help.show();
			});

			// Loads in food from the food file
			loadButton.setOnAction(e -> {
				TextInputDialog load = new TextInputDialog("");
				Optional<String> l = load.showAndWait();
				if(l.isPresent()) {
					foodD.loadFoodItems(l.get());
					if (foodD.getAllFoodItems().size() == 0) {
						Alert d = new Alert(AlertType.ERROR);
						d.setTitle("Load File");
						d.setHeaderText("Error loading file");
						d.setContentText(l.get() + " was not found");

						d.showAndWait();
					}
				}

				
				ObservableList<FoodItem> foodItems =FXCollections.observableArrayList (foodD.getAllFoodItems());
				
				//Update total
				if(numItems.getText().length() > 13) {
					numItems.setText(numItems.getText().substring(0, 13) + foodItems.size());
				}else {
					numItems.setText(numItems.getText() + foodItems.size());
				}
				
				foodList.setItems(foodItems);

			});

			// Saves a collection of food items as a csv
			saveButton.setOnAction(e -> {
				foodD.saveFoodItems("savedList.csv");
			});

			// Adds the selected food item to the meal 
			mealButton.setOnAction(e -> {
				FoodItem toAdd =foodList.getSelectionModel().getSelectedItem();
				if(toAdd != null) {
					mealArrList.add(toAdd);
					ObservableList<FoodItem> mealItems =FXCollections.observableArrayList (mealArrList);
					mealList.setItems(mealItems);
				}

			});

			// clears all food added to the meal
			clearMealButton.setOnAction(e -> {
				mealArrList.clear();
				ObservableList<FoodItem> mealItems =FXCollections.observableArrayList (mealArrList);
				mealList.setItems(mealItems);
			});

			//Clear Filter
			clearFilterButton.setOnAction(e -> {

				List<String> cList = new ArrayList<String>();
				cList.add("Name");
				cList.add("Nutrients");
				ChoiceDialog<String> fChoice = new ChoiceDialog<String>("Name", cList);
				fChoice.setHeaderText("Select the filter you would like to clear");
				fChoice.setContentText("Clear ");

				//Handle the choice
				Optional<String> s = fChoice.showAndWait();
				if(s.isPresent()) {
					if (s.equals("Name")){
						ObservableList<FoodItem> foodItems =FXCollections.observableArrayList (foodD.getAllFoodItems());
						nameDisplay.setText("\nKeywords added to Query:\n");
						//Update total
						if(numItems.getText().length() > 13) {
							numItems.setText(numItems.getText().substring(0, 13) + foodItems.size());
						}else {
							numItems.setText(numItems.getText() + foodItems.size());
						}
						foodList.setItems(foodItems);
					}else {

						rules.clear();
						ObservableList<FoodItem> foodItems =FXCollections.observableArrayList (foodD.getAllFoodItems());
						
						//Update total
						if(numItems.getText().length() > 13) {
							numItems.setText(numItems.getText().substring(0, 13) + foodItems.size());
						}else {
							numItems.setText(numItems.getText() + foodItems.size());
						}
						
						foodList.setItems(foodItems);
						rulesDisplay.setText("\nRules added to Query:\n");
					}

				}
			});

			//Quits the program
			quitButton.setOnAction(e -> {
				System.exit(0);
			});



			//Make box panes
			VBox mealPane = new VBox();
			VBox foodPane = new VBox();
			VBox headerPane = new VBox();
			HBox footerPane = new HBox();
			VBox centerPane = new VBox();           
			HBox menuPane = new HBox();
			HBox instrPane = new HBox();

			//Fill each box pane with appropriate content
			headerPane.getChildren().add(greetingLabel);
			headerPane.setAlignment(Pos.CENTER);
			headerPane.getChildren().add(instrPane);
			headerPane.getChildren().add(menuPane);

			// fills the menu bar with the buttons
			menuPane.getChildren().add(rulesButton);
			menuPane.getChildren().add(filterButton);
			menuPane.getChildren().add(clearFilterButton);
			menuPane.getChildren().add(analyzeButton);
			menuPane.getChildren().add(saveButton);
			menuPane.getChildren().add(loadButton);
			menuPane.getChildren().add(helpButton);

			// places meal buttons in the center of the view
			centerPane.getChildren().add(mealButton);
			centerPane.getChildren().add(clearMealButton);
			centerPane.getChildren().add(foodButton);
			centerPane.setAlignment(Pos.CENTER);

			// sets information regarding the food pane
			foodPane.getChildren().add(foodLabel);
			foodPane.getChildren().add(foodList);
			foodPane.getChildren().add(numItems);
			foodPane.getChildren().add(rulesDisplay);
			foodPane.getChildren().add(nameDisplay);

			// sets information regarding the meal pane
			mealPane.getChildren().add(mealLabel);
			mealPane.getChildren().add(mealList);
			mealPane.getChildren().add(mealDisplay);

			// sets the quit button in the footer pane
			footerPane.getChildren().add(quitButton);
			footerPane.setAlignment(Pos.CENTER_RIGHT);

			//Create border pane
			BorderPane root = new BorderPane();
			root.setTop(headerPane);
			footerPane.setSpacing(root.getWidth());
			root.setBottom(footerPane);
			root.setLeft(foodPane);
			root.setRight(mealPane);
			root.setCenter(centerPane);

			//Create scene
			Scene scene = new Scene(root,800,800);

			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);


			primaryStage.show();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Main method to launch the program
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
	/**
	 * Implements event handler for the buttons so they can respond to events
	 *
	 */
	class ButtonHandler implements EventHandler<ActionEvent> {

		/**
		 * Event handler for creating new food data
		 */
		@Override
		public void handle(ActionEvent arg0) {
			FoodData f = new FoodData();

		}

	}


}