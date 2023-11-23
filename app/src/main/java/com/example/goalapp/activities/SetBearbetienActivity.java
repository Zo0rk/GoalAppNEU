package com.example.goalapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.goalapp.R;
import com.example.goalapp.database.DatenBankManager;

public class SetBearbetienActivity extends AppCompatActivity {

    private Button abbrechen;
    private Button speichern;
    private EditText setNameEdit;
    private EditText setBeschreibungEdit;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private String selectedRadioColor;
    private int setID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_bearbetien);

        abbrechen = findViewById(R.id.setBearbeitenAbbrechenButton);
        speichern = findViewById(R.id.setBearbeitenSpeichernButton);
        abbrechen.setOnClickListener(this::onClick);
        speichern.setOnClickListener(this::onClick);

        setNameEdit = findViewById(R.id.setBearbeitenNameEdit);
        setBeschreibungEdit = findViewById(R.id.setBearbeitenBeschrEdit);

        radioGroup = findViewById(R.id.setBearbeitenRadioGr);

        setID = getIntent().getIntExtra("SET_ID",9999);

        // Hier wird der OnCheckedChangeListener für die RadioGroup gesetzt
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                // Ein Radiobutton wurde ausgewählt
                radioButton = findViewById(checkedId);
                String selectedColor = radioButton.getText().toString();
                selectColor(selectedColor);
            }
        });

    }
    private void selectColor(String selectedColor) {
        switch (selectedColor) {
            case "ROT": selectedRadioColor = "red"; break;
            case "GRÜN": selectedRadioColor = "green"; break;
            case "GELB": selectedRadioColor = "yellow"; break;
            case "LILA": selectedRadioColor = "purple"; break;
            case "ORANGE": selectedRadioColor = "orange"; break;
            case "BLAU": selectedRadioColor = "blue"; break;
        }
    }

    public void onClick(View v) {
        if (v == speichern) {
            if (!(setNameEdit.getText() == null || setNameEdit.getText().toString().trim().equals(""))) {
                if (!(setBeschreibungEdit.getText() == null || setBeschreibungEdit.getText().toString().trim().equals(""))) {
                    // Hier wird überprüft, ob ein RadioButton ausgewählt wurde
                    if (radioGroup.getCheckedRadioButtonId() != -1) {
                        String setName = setNameEdit.getText().toString();
                        String setBeschreibung = setBeschreibungEdit.getText().toString();

                        // Der ausgewählte RadioButton wird über den OnCheckedChangeListener verarbeitet
                        // Du kannst die Farbe hier verwenden oder sie zu einem String umwandeln, wie zuvor gezeigt

                        DatenBankManager db = new DatenBankManager(this);
                        db.updateSet(setID, setName, setBeschreibung, selectedRadioColor);
                        finish();
                    } else {
                        // Hier kannst du eine Meldung ausgeben oder andere Aktionen durchführen,
                        // wenn kein RadioButton ausgewählt wurde.
                        Toast.makeText(this, "Bitte eine Farbe auswählen", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else if (v == abbrechen) {
            finish();
        }
    }
}
