package io.github.rsookram.scratchpad;

import android.content.Context;
import android.content.SharedPreferences;

public class Storage {

    private final SharedPreferences sharedPreferences;

    public Storage(Context context) {
        this.sharedPreferences = context.getSharedPreferences("note", Context.MODE_PRIVATE);
    }

    public String load() {
        return sharedPreferences.getString("n", "");
    }

    public void save(String value) {
        sharedPreferences.edit().putString("n", value).apply();
    }
}
