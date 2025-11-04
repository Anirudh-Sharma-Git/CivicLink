package com.book.civiclink2o;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class LanguageSelectionActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        CardView cardEnglish = findViewById(R.id.cardEnglish);
        CardView cardHindi = findViewById(R.id.cardHindi);
        CardView cardBengali = findViewById(R.id.cardBengali);
        CardView cardSantali = findViewById(R.id.cardSantali);
        CardView cardUrdu = findViewById(R.id.cardUrdu);
        CardView cardOdia = findViewById(R.id.cardOdia);
        CardView cardTelugu = findViewById(R.id.cardTelugu);
        CardView cardMarathi = findViewById(R.id.cardMarathi);

        cardEnglish.setOnClickListener(this);
        cardHindi.setOnClickListener(this);
        cardBengali.setOnClickListener(this);
        cardSantali.setOnClickListener(this);
        cardUrdu.setOnClickListener(this);
        cardOdia.setOnClickListener(this);
        cardTelugu.setOnClickListener(this);
        cardMarathi.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String language = "";
        int id = v.getId();
        if (id == R.id.cardEnglish) {
            language = "English";
        } else if (id == R.id.cardHindi) {
            language = "Hindi";
        } else if (id == R.id.cardBengali) {
            language = "Bengali";
        } else if (id == R.id.cardSantali) {
            language = "Santali";
        } else if (id == R.id.cardUrdu) {
            language = "Urdu";
        } else if (id == R.id.cardOdia) {
            language = "Odia";
        } else if (id == R.id.cardTelugu) {
            language = "Telugu";
        } else if (id == R.id.cardMarathi) {
            language = "Marathi";
        }

        if (!language.isEmpty()) {
            Toast.makeText(this, language + " selected", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LanguageSelectionActivity.this, LoginActivity.class);

            intent.putExtra("SELECTED_LANGUAGE", language);

            startActivity(intent);

            finish();
        }
    }
}
