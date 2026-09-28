package com.anylocale.demo.exampleandroid;

import android.content.Context;
import android.os.Bundle;

import android.widget.Button;
import android.widget.TextView;
import androidx.activity.ComponentActivity;
import com.anylocale.AnylocaleAndroid;
import com.anylocale.Anylocale;
import com.anylocale.AnylocaleContextWrapper;

import java.util.Locale;

public class MainJavaActivity extends ComponentActivity implements Anylocale.ChangeListener {

    private final AnylocaleAndroid anylocale = Anylocale.getInstance();

    @Override
    protected void attachBaseContext(Context newBase) {
        // Wrapping base context will make sure getString calls will use anylocale
        // even for instances which cannot be replaced automatically by the compiler
        super.attachBaseContext(AnylocaleContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        anylocale.addChangeListener(this);

        setContentView(R.layout.activity_main);

        // Make sure the app title stays updated
        setTitle(R.string.app_name);

        // Simple strings (app_name, description) are automatically translated by AnylocaleLayoutInflaterFactory!
        // No need to manually set them - they're handled during layout inflation

        // Only parameterized strings and plurals need manual handling
        updateParameterizedStrings();

        Button buttonEn = findViewById(R.id.button_en);
        Button buttonDe = findViewById(R.id.button_de);
        Button buttonFr = findViewById(R.id.button_fr);

        buttonEn.setOnClickListener(v -> {
            anylocale.setLocale(Locale.ENGLISH);
            anylocale.preload(this);
        });
        buttonDe.setOnClickListener(v -> {
            anylocale.setLocale(Locale.GERMAN);
            anylocale.preload(this);
        });
        buttonFr.setOnClickListener(v -> {
            anylocale.setLocale(Locale.FRENCH);
            anylocale.preload(this);
        });
    }

    private void updateParameterizedStrings() {
        // These require manual handling because they have format arguments or are plurals
        TextView parameter = findViewById(R.id.parameterized_text);
        TextView plural = findViewById(R.id.plural_text);
        TextView array = findViewById(R.id.array_text);

        parameter.setText(getString(R.string.percentage_placeholder, "87"));
        plural.setText(getResources().getQuantityString(R.plurals.plr_test_placeholder_2, 2, 3, "Plurals"));
        array.setText(String.join(", ", getResources().getStringArray(R.array.array_test)));
        updateProbeStrings();
    }

    // Keys published by the local anylocale server (see README, "Local server").
    private void updateProbeStrings() {
        TextView getStringText = findViewById(R.id.probe_get_string_text);
        TextView noConnection = findViewById(R.id.probe_no_connection_text);
        TextView pluralOne = findViewById(R.id.probe_plural_one_text);
        TextView pluralOther = findViewById(R.id.probe_plural_other_text);

        getStringText.setText(getString(R.string.home_hourly_forecast));
        noConnection.setText(getString(R.string.error_no_connection));
        pluralOne.setText(getResources().getQuantityString(R.plurals.home_rain_alerts_count, 1, 1));
        pluralOther.setText(getResources().getQuantityString(R.plurals.home_rain_alerts_count, 3, 3));
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Make sure the translations are loaded
        // This function will initiate translations fetching in the background and
        // will trigger changeFlow whenever updated translations are available
        anylocale.preload(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        anylocale.removeChangeListener(this);
    }

    @Override
    public void onTranslationsChanged() {
        // Re-translate views without recreating the Activity for smoother UX
        anylocale.retranslate(this); // or recreate() for more complex activities

        // Make sure the app title is updated
        setTitle(R.string.app_name);

        // Still need to manually update parameterized strings and plurals
        updateParameterizedStrings();
    }
}