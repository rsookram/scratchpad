package io.github.rsookram.scratchpad;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Storage {

    private final Context context;

    public Storage(Context context) {
        this.context = context;
    }

    public String load(boolean main) {
        String fileName = main ? "n" : "n2";
        try (InputStream is = context.openFileInput(fileName)) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            return "";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(String value, boolean main) {
        String fileName = main ? "n" : "n2";
        try (OutputStream os = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            os.write(value.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
