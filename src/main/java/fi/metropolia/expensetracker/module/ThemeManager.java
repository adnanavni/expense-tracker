package fi.metropolia.expensetracker.module;

public class ThemeManager {
    private static final ThemeManager instance = new ThemeManager();

    private String currentColor = "#85bb65"; // Default color is dollar green

    private ThemeManager() {

    }

    public static ThemeManager getInstance() {
        return instance;
    }

    public void setCurrentColor(String color) {
        currentColor = color;
    }

    public String getCurrentColor() {
        return currentColor;
    }

    public String getStyle() {
        return "-fx-background-color: " + currentColor.toLowerCase() + ";";
    }
}