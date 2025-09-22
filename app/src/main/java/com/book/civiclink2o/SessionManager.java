package com.book.civiclink2o;

import android.content.Context;
import android.content.SharedPreferences;

// A helper class to manage the user's login session
public class SessionManager {

    private static final String PREF_NAME = "CivicLinkSession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Creates a login session for the user.
     * @param id The user's unique ID from the database.
     * @param name The user's name.
     */
    public void createLoginSession(int id, String name) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, id);
        editor.putString(KEY_USER_NAME, name);
        editor.commit(); // Save the changes
    }

    /**
     * Gets the logged-in user's ID.
     * @return The user ID, or -1 if no one is logged in.
     */
    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    // --- THIS IS THE FIX: The missing method ---
    /**
     * Gets the logged-in user's name.
     * @return The user's name, or null if no one is logged in.
     */
    public String getUserName() {
        return pref.getString(KEY_USER_NAME, null);
    }
    // --- END OF FIX ---


    /**
     * Checks if a user is currently logged in.
     * @return true if logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Clears session details.
     */
    public void logoutUser() {
        // Clearing all data from SharedPreferences
        editor.clear();
        editor.commit();
    }
}