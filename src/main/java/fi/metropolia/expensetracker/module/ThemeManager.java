package fi.metropolia.expensetracker.module;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class ThemeManager {
    private static final ThemeManager instance = new ThemeManager();

    private String currentColor = "#85bb65"; // Default color is dollar green
    private String currentLanguage = "English"; // Default language is english

    private ThemeManager() {}
    public static ThemeManager getInstance() {
        return instance;
    }

    public void setCurrentColor(String color) {
        currentColor = color;
    }
    public void setCurrentLanguage(String language) { currentLanguage = language; }
    public String getLanguage () { return currentLanguage; }

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
                }if (getIndex() >= 0 && getIndex() <= getListView().getItems().size()-1) {
                    setStyle(null);
                }
                else  {
                    setStyle("-fx-background-color:#FFFFFF;");
                }
            }
        });
    }

    public String getStyle() {
        return "-fx-background-color: " + currentColor.toLowerCase() + ";";
    }
}