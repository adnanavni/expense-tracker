package fi.metropolia.expensetracker.module;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationManager {
    private static LocalizationManager instance;
    private ResourceBundle messages;

    private LocalizationManager() {
        messages = ResourceBundle.getBundle("fi/metropolia/expensetracker/properties/messages", Locale.getDefault());
    }

    public static synchronized LocalizationManager getInstance() {
        if (instance == null) {
            instance = new LocalizationManager();
        }
        return instance;
    }

    public void setLocale(Locale locale) {
        messages = ResourceBundle.getBundle("fi/metropolia/expensetracker/properties/messages", locale);
    }

    public String getString(String key) {
        return messages.getString(key);
    }
}