package com.example.goalapp.activities;

import static java.lang.String.valueOf;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goalapp.R;
import com.example.goalapp.database.DatenBankManager;

public class SetErzeugenActivity extends AppCompatActivity implements View.OnClickListener {

    private Button abbrechen;
    private Button speichern;
    private EditText setNameEdit;
    private EditText setBeschreibungEdit;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private String radioSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_erzeugen);

        abbrechen = findViewById(R.id.abbrechen);
        speichern = findViewById(R.id.speichern);
        abbrechen.setOnClickListener(this);
        speichern.setOnClickListener(this);

        setNameEdit = findViewById(R.id.setErzeugenNameEdit);
        setBeschreibungEdit = findViewById(R.id.setErzeugenBeschrEdit);

        radioGroup = findViewById(R.id.setErzeugenRadioGr);


    }

    @Override
    public void onClick(View v) {
        if(v == speichern){
            if(!(setNameEdit.getText() == null || setNameEdit.getText().toString().trim().equals(""))){
                if(!(setBeschreibungEdit.getText() == null || setBeschreibungEdit.getText().toString().trim().equals(""))){
                    if(!(radioGroup.getCheckedRadioButtonId() == -1)){

                        DatenBankManager db = new DatenBankManager(this);
                        String setName = setNameEdit.getText().toString();
                        String setBeschreibung = setBeschreibungEdit.getText().toString();
                        int setStatus = 0;

                        //Hier wird die ausgewählte Farbe zu einem String umgewandelt...
                        radioButton = (RadioButton)findViewById(radioGroup.getCheckedRadioButtonId());
                        String selectedColor = radioButton.getText().toString();
                        switch (selectedColor) {
                            case "ROT": selectedColor = "red"; break;
                            case "GRÜN": selectedColor = "green"; break;
                            case "GELB": selectedColor = "yellow"; break;
                            case "LILA": selectedColor = "purple"; break;
                            case "ORANGE": selectedColor = "orange"; break;
                            case "BLAU": selectedColor = "blue"; break;
                        }
                        //Auswahl einfügen in die Datenbank...
                        db.insertSet(setName, setBeschreibung,selectedColor,setStatus);
                        //Anschließend wird die Übersicht des Set mittels ID geöffnet...
                        //Dazu benötigen wir nun die ID:
//                        int maxID = db.getMaxSetID();
//                        i.putExtra("SET_ID",maxID);
//                        startActivity(i);
                        finish();
                    }
                }
            }
        }
        if(v == abbrechen) {
            finish();
        }
    }
}