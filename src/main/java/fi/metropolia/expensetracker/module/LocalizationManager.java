package fi.metropolia.expensetracker.module;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationManager {
    private static LocalizationManager instance;
    private ResourceBundle messages;
    private Locale locale = Locale.getDefault();

    private LocalizationManager() {
        messages = ResourceBundle.getBundle("fi/metropolia/expensetracker/properties/messages", Locale.getDefault());
    }

    public static synchronized LocalizationManager getInstance() {
        if (instance == null) {
            instance = new LocalizationManager();
        }
        return instance;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        messages = ResourceBundle.getBundle("fi/metropolia/expensetracker/properties/messages", locale);
        Locale.setDefault(locale);
        this.locale = locale;
    }

    public String getString(String key) {
        return messages.getString(key);
    }
}