package io.github.rsookram.scratchpad;

import android.content.Context;
import android.content.SharedPreferences;

public class Storage {

    private final SharedPreferences sharedPreferences;

    public Storage(Context context) {
        this.sharedPreferences = context.getSharedPreferences("note", Context.MODE_PRIVATE);
    }

    public String load(boolean main) {
        return sharedPreferences.getString(main ? "n" : "n2", "");
    }

    public void save(String value, boolean main) {
        sharedPreferences.edit().putString(main ? "n" : "n2", value).apply();
    }
}
