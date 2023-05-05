package fi.metropolia.expensetracker.module;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

/**
 * The ThemeManager class manages the application's theme.
 **/
public class ThemeManager {
    /**
     * The instance variable stores the only instance of the ThemeManager class that exists throughout the application.
     */
    private static final ThemeManager instance = new ThemeManager();

    /**
     * The currentColor variable stores the current color of the theme.
     */
    private String currentColor = "#85bb65"; // Default color is dollar green

    /**
     * The constructor is private to prevent direct instantiation from outside the class.
     * It is only called once to create the instance variable.
     */
    private ThemeManager() {
    }

    /**
     * The getInstance() method returns the single instance of the ThemeManager class.
     *
     * @return The ThemeManager instance.
     */
    public static ThemeManager getInstance() {
        return instance;
    }

    /**
     * The setCurrentColor() method sets the current color of the theme to the given color.
     *
     * @param color The new color of the theme.
     */
    public void setCurrentColor(String color) {
        currentColor = color;
    }

    /**
     * The styleListView() method styles a ListView object with the current theme.
     *
     * @param listView The ListView object to be styled.
     */
    public void styleListView(ListView listView) {
        listView.setCellFactory(list -> new ListCell<Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(null);
                } else {
                    setText(item.toString());
                }
                if (getIndex() >= 0 && getIndex() <= getListView().getItems().size() - 1) {
                    setStyle(null);
                } else {
                    setStyle("-fx-background-color:#FFFFFF;");
                }
            }
        });
    }

    /**
     * The getStyle() method returns the current style of the theme in the format of a CSS style string.
     *
     * @return The current style of the theme.
     */
    public String getStyle() {
        return "-fx-background-color: " + currentColor.toLowerCase() + ";";
    }
}