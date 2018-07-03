package com.shopify.sample.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.shopify.sample.SampleApplication;

/**
 * Provides convenience methods and abstraction for storing data in the
 * {@link android.content.SharedPreferences}
 * @author Jay
 **/
public class SharedPreferenceManager {

    private SharedPreferences mSettings;

    private static final String PREF_FILE_NAME =  "Shopify_prefs";
    private Editor mEditor;

    private static SharedPreferenceManager sInstance;


    public static SharedPreferenceManager getInstance() {
        if(sInstance == null) {
            sInstance = new SharedPreferenceManager();
        }
        return sInstance;
    }

    @SuppressLint("CommitPrefEdits")
    private SharedPreferenceManager() {
        mSettings = SampleApplication.instance().getApplicationContext().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        mEditor = mSettings.edit();
    }

    /***
     * Set a value for the key
     ****/
    public void setValue(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    /***
     * Set a value for the key
     ****/
    public void setValue(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    /***
     * Set a value for the key
     ****/
    public void setValue(String key, double value) {
        setValue(key, Double.toString(value));
    }

    /***
     * Set a value for the key
     ****/
    public void setValue(String key, long value) {
        mEditor.putLong(key, value);
        mEditor.commit();
    }

    /****
     * Gets the value from the settings stored natively on the device.
     *
     * @param defaultValue Default value for the key, if one is not found.
     **/
    public String getValue(String key, String defaultValue) {
        return mSettings.getString(key, defaultValue);
    }

    public int getIntValue(String key, int defaultValue) {
        return mSettings.getInt(key, defaultValue);
    }

    public long getLongValue(String key, long defaultValue) {
        return mSettings.getLong(key, defaultValue);
    }

    /****
     * Gets the value from the preferences stored natively on the device.
     *
     * @param defValue Default value for the key, if one is not found.
     **/
    public boolean getValue(String key, boolean defValue) {
        return mSettings.getBoolean(key, defValue);
    }

    public void setValue(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    /**
     * Clear all the preferences store in this {@link android.content.SharedPreferences.Editor}
     */
    public boolean clear() {
        try {
            mEditor.clear().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Removes preference entry for the given key.
     *
     * @param key This is the key
     */
    public void removeValue(String key) {
        if (mEditor != null) {
            mEditor.remove(key).commit();
        }
    }
}