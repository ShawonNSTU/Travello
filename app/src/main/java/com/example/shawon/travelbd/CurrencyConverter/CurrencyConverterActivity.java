package com.example.shawon.travelbd.CurrencyConverter;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.shawon.travelbd.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by SHAWON on 6/19/2019.
 */

public class CurrencyConverterActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    Spinner spinner_firstChose, spinner_secondChose;
    HashMap<String, String> hm;
    EditText edt_firstCountry, edt_secondCountry;
    TextView txtview_result, TextView_date;
    String date;
    private TextToSpeech tts;
    ImageButton tts_button;
    private ImageView mbackArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_converter);
        hm = new HashMap<>();
        initialize();
        getOnlineMoneyData();
        addTextWatcher();
        spinnerListener();
        buttonListener();
    }

    private void initialize() {
        spinner_firstChose = (Spinner) findViewById(R.id.spinner_firstChose);
        spinner_secondChose = (Spinner) findViewById(R.id.spinner_secondChose);
        edt_firstCountry = (EditText) findViewById(R.id.edt_firstCountry);
        edt_secondCountry = (EditText) findViewById(R.id.edt_secondCountry);
        txtview_result = (TextView) findViewById(R.id.txtview_result);
        TextView_date = (TextView) findViewById(R.id.TextView_date);
        tts_button = (ImageButton) findViewById(R.id.tts_button);
        mbackArrow = (ImageView) findViewById(R.id.backArrow);
        tts = new TextToSpeech(this, this);

        mbackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edt_secondCountry.setEnabled(false);
        edt_firstCountry.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});

        spinner_secondChose.setSelection(0);
        spinner_firstChose.setSelection(1);
    }

    private void addTextWatcher() {

        edt_firstCountry.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                calculateAndSetResult();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

    }

    private void spinnerListener() {

        spinner_firstChose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateAndSetResult();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_secondChose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateAndSetResult();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void buttonListener() {

        tts_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!txtview_result.getText().toString().equals("")) {
                    tts.speak(txtview_result.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                    RunAnimation(R.anim.clockwise,txtview_result);
                }

            }
        });
    }

    private void calculateAndSetResult() {

        if (edt_firstCountry.getText().toString().equals("")) {
            edt_firstCountry.setText("0");
        }

        if (!edt_firstCountry.getText().toString().equals("") || !edt_secondCountry.getText().toString().equals("")) {

            String inititCurrency = spinner_firstChose.getSelectedItem().toString();
            String targetCurrency = spinner_secondChose.getSelectedItem().toString();

            try {
                double baseRate = Double.valueOf(hm.get("USD"));
                double initRate = Double.valueOf(hm.get(inititCurrency));
                double targetRate = Double.valueOf(hm.get(targetCurrency));
                double first_input = Double.valueOf(edt_firstCountry.getText().toString());
                String resultFinal = String.valueOf(String.format("%.2f", ((targetRate * first_input) / initRate)));
                edt_secondCountry.setText(resultFinal);
                txtview_result.setText(edt_firstCountry.getText().toString() + " "
                        + inititCurrency + " = "+ resultFinal + " " + targetCurrency);
                RunAnimation(R.anim.blink,txtview_result);

            } catch (Exception e) {
                e.getMessage();
            }

        }
    }

    private void RunAnimation(int rID,TextView textView) {
        Animation a = AnimationUtils.loadAnimation(this,rID);
        a.reset();
        textView.clearAnimation();
        textView.startAnimation(a);
    }

    private void getOnlineMoneyData() {

        hm.clear();
        hm.put("USD", "1");
        hm.put("BDT", "85.45");

        String url = "http://data.fixer.io/api/latest?access_key=20e939f50700f45c087541d53de105d2&?base=USD";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("Tag", response.toString());

                try {

                    // Parsing json object response
                    // response will be a json object

                    date = response.getString("date");
                    JSONObject phone = response.getJSONObject("rates");
                    String BGN = phone.getString("BGN"); String BRL = phone.getString("BRL");
                    String CAD = phone.getString("CAD"); String CHF = phone.getString("CHF");
                    String INR = phone.getString("INR"); String AED = phone.getString("AED");
                    String ANG = phone.getString("ANG"); String ARS = phone.getString("ARS");
                    String BAM = phone.getString("BAM"); String BIF = phone.getString("BIF");
                    String COP = phone.getString("COP"); String DZD = phone.getString("DZD");
                    String EGP = phone.getString("EGP"); String GEL = phone.getString("GEL");
                    String GMD = phone.getString("GMD"); String HTG = phone.getString("HTG");
                    String JPY = phone.getString("JPY"); String KHR = phone.getString("KHR");
                    String LBP = phone.getString("LBP"); String MNT = phone.getString("MNT");
                    String MVR = phone.getString("MVR"); String NPR = phone.getString("NPR");
                    String OMR = phone.getString("OMR"); String PLN = phone.getString("PLN");
                    String RSD = phone.getString("RSD"); String SDG = phone.getString("SDG");
                    String SHP = phone.getString("SHP"); String TJS = phone.getString("TJS");
                    String TWD = phone.getString("TWD"); String UAH = phone.getString("UAH");
                    String UZS = phone.getString("UZS"); String YER = phone.getString("YER");
                    String ZMW = phone.getString("ZMW");

                    hm.put("BGN", BGN); hm.put("BRL", BRL); hm.put("CAD", CAD); hm.put("CHF", CHF);
                    hm.put("INR", INR); hm.put("AED", AED); hm.put("ANG", ANG); hm.put("ARS", ARS);
                    hm.put("BAM", BAM); hm.put("BIF", BIF); hm.put("COP", COP); hm.put("DZD", DZD);
                    hm.put("EGP", EGP); hm.put("GEL", GEL); hm.put("GMD", GMD); hm.put("HTG", HTG);
                    hm.put("JPY", JPY); hm.put("KHR", KHR); hm.put("LBP", LBP); hm.put("MNT", MNT);
                    hm.put("MVR", MVR); hm.put("NPR", NPR); hm.put("OMR", OMR); hm.put("PLN", PLN);
                    hm.put("RSD", RSD); hm.put("SDG", SDG); hm.put("SHP", SHP); hm.put("TJS", TJS);
                    hm.put("TWD", TWD); hm.put("UAH", UAH); hm.put("UZS", UZS); hm.put("YER", YER);
                    hm.put("ZMW", ZMW);

                    TextView_date.setText("Last updated on " + date);
                    RunAnimation(R.anim.fade,TextView_date);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        // Adding request to request queue
        queue.add(jsonObjReq);

    }

        @Override
    public void onInit(int status) {
            //check for successful instantiation
            if (status == TextToSpeech.SUCCESS) {
                if (tts.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE)
                    tts.setLanguage(Locale.US);
            } else if (status == TextToSpeech.ERROR) {
                Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
            }
    }
}