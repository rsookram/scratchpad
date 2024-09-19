package io.github.rsookram.scratchpad;

import android.app.Activity;
import android.graphics.Insets;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.TextView;
import android.widget.Toolbar;

public class MainActivity extends Activity {

    private Storage storage;
    private boolean hasChanged = false;
    private TextView startEditor;
    private TextView endEditor;

    private int bottomIgnoreAreaHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        storage = new Storage(getApplicationContext());

        startEditor = findViewById(R.id.start_text);
        startEditor.setText(storage.load(true));

        endEditor = findViewById(R.id.end_text);
        if (endEditor != null) {
            endEditor.setText(storage.load(false));
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolbar);

        toolbar.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.undo) {
                // ctrl + z
                KeyEvent e = new KeyEvent(
                        0, 0, KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_Z, 0, KeyEvent.META_CTRL_ON
                );
                (endEditor != null && endEditor.hasFocus() ? endEditor : startEditor).onKeyShortcut(e.getKeyCode(), e);
                hasChanged = true;
                return true;
            }

            if (menuItem.getItemId() == R.id.redo) {
                // ctrl + shift + z
                KeyEvent e = new KeyEvent(
                        0, 0, KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_Z, 0, KeyEvent.META_CTRL_ON | KeyEvent.META_SHIFT_ON
                );
                (endEditor != null && endEditor.hasFocus() ? endEditor : startEditor).onKeyShortcut(e.getKeyCode(), e);
                hasChanged = true;
                return true;
            }

            return false;
        });

        startEditor.setFilters(new InputFilter[]{new RemoveFormattingFilter()});
        if (endEditor != null) {
            endEditor.setFilters(new InputFilter[]{new RemoveFormattingFilter()});
        }

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hasChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        startEditor.addTextChangedListener(textWatcher);
        if (endEditor != null) {
            endEditor.addTextChangedListener(textWatcher);
        }

        applySystemUiVisibility(toolbar, startEditor, endEditor);

        findViewById(android.R.id.content).setOnApplyWindowInsetsListener((v, insets) -> {
            bottomIgnoreAreaHeight = insets.getInsets(WindowInsets.Type.systemBars()).bottom;

            return insets;
        });
    }

    private void applySystemUiVisibility(View toolbar, View... contents) {
        getWindow().setDecorFitsSystemWindows(false);

        toolbar.setOnApplyWindowInsetsListener((v, insets) -> {
            Insets systemInsets = insets.getInsets(WindowInsets.Type.systemBars());
            v.setPadding(systemInsets.left, systemInsets.top, systemInsets.right, 0);

            return insets;
        });

        for (View content : contents) {
            if (content == null) {
                continue;
            }

            content.setOnApplyWindowInsetsListener((v, insets) -> {
                int padding = getResources().getDimensionPixelSize(R.dimen.content_padding);
                Insets systemInsets = insets.getInsets(WindowInsets.Type.systemBars() | WindowInsets.Type.ime());
                v.setPadding(
                        padding + systemInsets.left,
                        padding,
                        padding + systemInsets.right,
                        padding + systemInsets.bottom
                );

                return insets;
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (hasChanged) {
            storage.save(startEditor.getText().toString(), true);
            if (endEditor != null) {
                storage.save(endEditor.getText().toString(), false);
            }
            hasChanged = false;
        }

        startEditor.clearFocus();
        if (endEditor != null) {
            endEditor.clearFocus();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getY() > getWindow().getDecorView().getHeight() - bottomIgnoreAreaHeight) {
            return false;
        }

        return super.dispatchTouchEvent(ev);
    }
}
