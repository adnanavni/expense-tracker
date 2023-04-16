package fi.metropolia.expensetracker.module;

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

    public String getStyle() {
        return "-fx-background-color: " + currentColor.toLowerCase() + ";";
    }
}