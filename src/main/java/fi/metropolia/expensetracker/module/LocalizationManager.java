package fi.metropolia.expensetracker.module;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Singleton manager to keep the right bundle file for the localization on the app.
 * All the localization in the app goes through this class
 */
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

    /**
     * Sets the locale for the app for the right use of bundle file.
     *
     * @param locale
     */
    public void setLocale(Locale locale) {
        messages = ResourceBundle.getBundle("fi/metropolia/expensetracker/properties/messages", locale);
        Locale.setDefault(locale);
        this.locale = locale;
    }

    /**
     * Gets the string from the bundle file
     *
     * @param key
     * @return the key's value from the bundle file
     */
    public String getString(String key) {
        return messages.getString(key);
    }
}