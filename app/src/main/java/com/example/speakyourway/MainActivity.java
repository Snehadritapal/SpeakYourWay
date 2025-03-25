package com.example.speakyourway;
import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.Locale;
import java.util.ArrayList;
import android.content.Intent;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//import com.google.firebase.FirebaseApp;
//import com.google.firebase.tracing.FirebaseTrace;


public class MainActivity extends AppCompatActivity {

    private EditText inputText;
    private TextView translatedText;
    private Button translateButton;
    private ImageButton micButton,speakerButton;
    private Spinner fromLanguage,toLanguage;
    private TextToSpeech textToSpeech;
    private Translator translator;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FirebaseApp.initializeApp(this);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputText = findViewById(R.id.inputText);
        translatedText = findViewById(R.id.translatedText);
        translateButton = findViewById(R.id.translateButton);
        micButton = findViewById(R.id.micButton);
        speakerButton = findViewById(R.id.speakerButton);
        fromLanguage = findViewById(R.id.fromLanguage);
        toLanguage = findViewById(R.id.toLanguage);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.languages_array,
                R.layout.spinner_item // Use your custom spinner_item.xml
        );

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        fromLanguage.setAdapter(adapter);
        toLanguage.setAdapter(adapter);

        //for text to speech-output
        textToSpeech=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i==TextToSpeech.SUCCESS){
                    setTTSLanguage();
                }
            }
        });

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translateText();
            }
        });

        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSpeechToText();
            }
        });

        speakerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTTSLanguage();
                textToSpeech.speak(translatedText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }

    private void setTTSLanguage() {
        String selectedLanguage = toLanguage.getSelectedItem().toString();
        Locale locale = Locale.ENGLISH;

        switch (selectedLanguage) {
            case "Tamil":
                locale = new Locale("ta");
                break;
            case "Telugu":
                locale = new Locale("te");
                break;
            case "Hindi":
                locale = new Locale("hi");
                break;
            case "Bengali":
                locale = new Locale("bn");
                break;
            default:
                locale = Locale.forLanguageTag(selectedLanguage);
                break;
        }
        textToSpeech.setLanguage(locale);
    }

    private void translateText() {
    String sourceText=inputText.getText().toString();
    if(sourceText.isEmpty()){
        Toast.makeText(this,"Please enter text to translate",Toast.LENGTH_SHORT).show();
        return;
    }

    String sourceLang=fromLanguage.getSelectedItem().toString();
    String targetLang=toLanguage.getSelectedItem().toString();

    TranslatorOptions options= new TranslatorOptions.Builder()
            .setSourceLanguage(getLanguageCode(sourceLang))
            .setTargetLanguage(getLanguageCode(targetLang))
            .build();
    translator=Translation.getClient(options);
    translator.downloadModelIfNeeded().addOnSuccessListener(aVoid -> {
        translator.translate(sourceText).addOnSuccessListener(translated -> {
            translatedText.setText(translated);
        }).addOnFailureListener(e -> Toast.makeText(this, "Translation failed", Toast.LENGTH_SHORT).show());
        }).addOnFailureListener(e -> Toast.makeText(this, "Model download failed", Toast.LENGTH_SHORT).show());
    }

    private String getLanguageCode(String language) {
        switch (language) {
            case "Tamil": return TranslateLanguage.TAMIL;
            case "Telugu": return TranslateLanguage.TELUGU;
            case "English": return TranslateLanguage.ENGLISH;
            case "Hindi": return TranslateLanguage.HINDI;
            case "Bengali": return TranslateLanguage.BENGALI;
            case "Arabic": return TranslateLanguage.ARABIC;
            case "Spanish": return TranslateLanguage.SPANISH;
            case "French": return TranslateLanguage.FRENCH;
            case "German": return TranslateLanguage.GERMAN;
            case "Italian": return TranslateLanguage.ITALIAN;
            case "Gujarati": return TranslateLanguage.GUJARATI;
            case "Kannada": return TranslateLanguage.KANNADA;
            case "Marathi": return TranslateLanguage.MARATHI;

            default: Toast.makeText(this, "Unsupported language", Toast.LENGTH_SHORT).show(); return null;
        }
    }

    private void startSpeechToText(){
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault());
        try{
            startActivityForResult(intent,1);
        }catch (Exception e){
            Toast.makeText(this,"Speech recognition not available",Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null){
            ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            //if(result!=null && !result.isEmpty()){
                inputText.setText(result.get(0));
            //}
        }
    }
}
