package com.nume.mobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class HealingPlan extends Activity {
    OnClickListener doneBtnListener;
    OnClickListener purchaseBtnListener;
    OnClickListener modalityClickListener;
    ArrayList<CharSequence> SelectedItems;
    String lineSep = System.getProperty("line.separator");
    VideoView mVideoView = null;
    ProgressDialog pDialog;
    Handler mHandler;
    private Uri uriYouTube;
    public static int stopPosition;
    final String videotoplay="https://www.youtube.com/watch?v=SHK_ZCB7DR4";

    RelativeLayout layout;

    enum Modality {
        Aroma, Diet, Movement, Affirmation, Sound, Meditation, Herb, Visualization
    }

    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.healingplan_activity);
        Button doneBtn = (Button) findViewById(R.id.btnDone);
        //ImageButton doneBtn = (ImageButton) findViewById(R.id.btnDone);
        SharedPreferences settings = getSharedPreferences("settings",
                MODE_PRIVATE);
        Boolean HEALINGPLANFOUND = settings.getBoolean("healingplansaved",
                false);

        if (HEALINGPLANFOUND) {
            GetSavedHealingPlan();
        } else {
            Bundle extras = getIntent().getExtras();
            SelectedItems = extras.getCharSequenceArrayList("SelectedItems");
            if (extras != null) {
                PullHealingPlan(SelectedItems);
            }
        }
        doneBtnListener = new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getBaseContext(),
                        WelcomeScreenActivity.class);
                startActivity(intent);
            }
        };
        doneBtn.setOnClickListener(doneBtnListener);

    }

    private void GetSavedHealingPlan() {

        // TODO Auto-generated method stub

        SharedPreferences settings = getSharedPreferences("settings",
                MODE_PRIVATE);
        JSONObject myHealingPlan;
        try {
            myHealingPlan = new JSONObject(settings.getString("myhealingplan",
                    ""));
            setHealingPlan(myHealingPlan);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void PullHealingPlan(ArrayList<CharSequence> ailments) {
        String urlCall = "http://nume.detrickdeburr.com/Solutions/GetHealingPlan?ailments=";
        String Parameters = ConvertToString(ailments);
        String ActualCall = urlCall + Parameters;
        Log.i("NuMe", "The Call is  " + ActualCall);
        new getHealingPlan().execute(ActualCall);
    }

    private void PullDetail(int id, String modality) {
        String urlCall = "";
        urlCall = "http://nume.detrickdeburr.com/Solutions/GetDetail?id=" + id
                + "&type=" + modality;
        new getDetail().execute(urlCall);
    }

    private String ConvertToString(ArrayList<CharSequence> ailments) {
        String convertedString = "";

        for (int i = 0; i < ailments.size(); i++) {

            convertedString += ailments.get(i) + ",";

        }
        convertedString = convertedString.substring(0,
                convertedString.length() - 1);
        String EncodedString = "";
        try {
            EncodedString = URLEncoder.encode(convertedString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            // Log.d("NuMe",e.toString());
        }
        return EncodedString;
    }

    public void setHealingPlan(JSONObject myHealingPlan) {
        ArrayList<CharSequence> Aromas = new ArrayList<CharSequence>();
        ArrayList<CharSequence> Diet = new ArrayList<CharSequence>();
        ArrayList<CharSequence> Movements = new ArrayList<CharSequence>();
        ArrayList<CharSequence> Affirmations = new ArrayList<CharSequence>();
        ArrayList<CharSequence> Sounds = new ArrayList<CharSequence>();
        ArrayList<CharSequence> Meditations = new ArrayList<CharSequence>();
        ArrayList<CharSequence> Herbs = new ArrayList<CharSequence>();
        ArrayList<CharSequence> Visualization = new ArrayList<CharSequence>();
        Log.d("NuMe", "myHealingPlan: " + myHealingPlan.toString());
        try {

            int startDietHere = setAromasHealingPlan(myHealingPlan, Aromas,
                    1000);
            int startMovementHere = setDietHealingPlan(myHealingPlan, Diet,
                    startDietHere);
            int startAffirmationsHere = setMovementHealingPlan(myHealingPlan,
                    Movements, startMovementHere);
            int startSoundsHere = setAffirmationHealingPlan(myHealingPlan,
                    Affirmations, startAffirmationsHere);
            int startMeditationHere = setSoundHealingPlan(myHealingPlan,
                    Sounds, startSoundsHere);
            int StartVisualizationHere = setMeditationHealingPlan(myHealingPlan,
                    Meditations, startMeditationHere);
            int startHerbsHere = setVisualizationHealingPlan(myHealingPlan, Visualization,
                    StartVisualizationHere);
            int EndHere = setHerbHealingPlan(myHealingPlan, Herbs,
                    startHerbsHere);

        } catch (JSONException e) {
            Log.e("NuMe", e.toString());
        }

    }

    private int setNextHeader(String headerName, int rowmarker, int series) {
        TextView Header = new TextView(HealingPlan.this);
        layout = (RelativeLayout) findViewById(R.id.healingplanlinearlayoutInsideScrollView);

        RelativeLayout.LayoutParams headerParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        headerParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
                RelativeLayout.TRUE);
        headerParams.addRule(RelativeLayout.BELOW, rowmarker);
        Header.setLayoutParams(headerParams);
        Header.setText(headerName);
        Header.setTextAppearance(this, R.style.appheading);
        Header.setId(series);
        layout.addView(Header);
        int rownum = Header.getId();
        return rownum;
    }

    private int setVisualizationHealingPlan(JSONObject myHealingPlan,
                                            ArrayList<CharSequence> Visualization, int idOfLastItemFromPreviousGroup)
            throws JSONException {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.healingplanlinearlayoutInsideScrollView);
        int rownum = setNextHeader("Visualizations", idOfLastItemFromPreviousGroup, 7000);
        JSONArray VisualizationArray = myHealingPlan.getJSONArray("visualization");
        for (int i = 0; i < VisualizationArray.length(); i++) {
            try {
                JSONObject jObj = VisualizationArray.getJSONObject(i);
                TextView Visualizationtv = new TextView(HealingPlan.this);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
                        RelativeLayout.TRUE);
                params.addRule(RelativeLayout.BELOW, rownum);
                params.setMargins(10, 10, 10, 10);
                Visualizationtv.setLayoutParams(params);
                Visualizationtv.setText(jObj.getString("Visualization").trim());
                Visualizationtv.setTag(jObj.getString("VisualizationId").trim());
                Visualizationtv.setId(++rownum);
                Visualizationtv.setClickable(true);
                Visualizationtv.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Detail(v, "Visualization");
                    }
                });
                Visualizationtv.setBackgroundResource(R.drawable.pressbuttons);
                Visualizationtv.setPadding(10, 10, 10, 10);
                Visualizationtv.setTextAppearance(this, R.style.healingplanitem);
                Visualizationtv.setWidth(600);
                layout.addView(Visualizationtv);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return rownum;
    }

    private int setHerbHealingPlan(JSONObject myHealingPlan,
                                   ArrayList<CharSequence> Herbs, int idOfLastItemFromPreviousGroup)
            throws JSONException {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.healingplanlinearlayoutInsideScrollView);
        int rownum = setNextHeader("Herbs", idOfLastItemFromPreviousGroup, 8000);
        JSONArray herbArray = myHealingPlan.getJSONArray("herb");
        for (int i = 0; i < herbArray.length(); i++) {
            try {
                JSONObject jObj = herbArray.getJSONObject(i);
                TextView herbtv = new TextView(HealingPlan.this);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
                        RelativeLayout.TRUE);
                params.addRule(RelativeLayout.BELOW, rownum);
                params.setMargins(10, 10, 10, 10);
                herbtv.setLayoutParams(params);
                herbtv.setText(jObj.getString("Herb").trim());
                herbtv.setTag(jObj.getString("HerbId").trim());
                herbtv.setId(++rownum);
                herbtv.setClickable(true);
                herbtv.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Detail(v, "Herb");
                    }
                });
                herbtv.setBackgroundResource(R.drawable.pressbuttons);
                herbtv.setPadding(10, 10, 10, 10);
                herbtv.setTextAppearance(this, R.style.healingplanitem);
                herbtv.setWidth(600);
                layout.addView(herbtv);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return rownum;
    }

    private int setMeditationHealingPlan(JSONObject myHealingPlan,
                                         ArrayList<CharSequence> Meditations,
                                         int idOfLastItemFromPreviousGroup) throws JSONException {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.healingplanlinearlayoutInsideScrollView);
        int rownum = setNextHeader("Meditation", idOfLastItemFromPreviousGroup,
                6000);
        JSONArray meditationArray = myHealingPlan.getJSONArray("meditation");
        for (int i = 0; i < meditationArray.length(); i++) {
            try {
                JSONObject jObj = meditationArray.getJSONObject(i);
                TextView meditationtv = new TextView(HealingPlan.this);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
                        RelativeLayout.TRUE);
                params.addRule(RelativeLayout.BELOW, rownum);
                params.setMargins(10, 10, 10, 10);
                meditationtv.setLayoutParams(params);
                meditationtv.setText(jObj.getString("Meditation").trim());
                meditationtv.setTag(jObj.getString("MeditationId").trim());
                meditationtv.setId(++rownum);
                meditationtv.setClickable(true);
                meditationtv.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Detail(v, "Meditation");
                    }
                });
                meditationtv.setBackgroundResource(R.drawable.pressbuttons);
                meditationtv.setPadding(10, 10, 10, 10);
                meditationtv.setTextAppearance(this, R.style.healingplanitem);
                meditationtv.setWidth(600);
                layout.addView(meditationtv);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return rownum;
    }

    private int setSoundHealingPlan(JSONObject myHealingPlan,
                                    ArrayList<CharSequence> Sounds, int idOfLastItemFromPreviousGroup)
            throws JSONException {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.healingplanlinearlayoutInsideScrollView);
        int rownum = setNextHeader("Sounds", idOfLastItemFromPreviousGroup,
                5000);
        JSONArray soundArray = myHealingPlan.getJSONArray("sound");
        for (int i = 0; i < soundArray.length(); i++) {
            try {
                JSONObject jObj = soundArray.getJSONObject(i);
                TextView soundtv = new TextView(HealingPlan.this);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
                        RelativeLayout.TRUE);
                params.addRule(RelativeLayout.BELOW, rownum);
                params.setMargins(10, 10, 10, 10);
                soundtv.setLayoutParams(params);
                soundtv.setText(jObj.getString("Sound").trim());
                soundtv.setTag(jObj.getString("SoundId").trim());
                soundtv.setId(++rownum);
                soundtv.setClickable(true);
                soundtv.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Detail(v, "Sound");
                    }
                });
                soundtv.setBackgroundResource(R.drawable.pressbuttons);
                soundtv.setPadding(10, 10, 10, 10);
                soundtv.setTextAppearance(this, R.style.healingplanitem);
                soundtv.setWidth(600);
                layout.addView(soundtv);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return rownum;
    }

    private int setAffirmationHealingPlan(JSONObject myHealingPlan,
                                          ArrayList<CharSequence> Movement, int idOfLastItemFromPreviousGroup)
            throws JSONException {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.healingplanlinearlayoutInsideScrollView);
        int rownum = setNextHeader("Affirmation",
                idOfLastItemFromPreviousGroup, 4000);
        JSONArray affirmationArray = myHealingPlan.getJSONArray("affirmation");
        for (int i = 0; i < affirmationArray.length(); i++) {
            try {
                JSONObject jObj = affirmationArray.getJSONObject(i);
                TextView affirmationtv = new TextView(HealingPlan.this);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
                        RelativeLayout.TRUE);
                params.addRule(RelativeLayout.BELOW, rownum);
                params.setMargins(10, 10, 10, 10);
                affirmationtv.setLayoutParams(params);
                affirmationtv.setText(jObj.getString("Affirmation").trim());
                affirmationtv.setTag(jObj.getString("AffirmationId").trim());
                affirmationtv.setId(++rownum);
                affirmationtv.setClickable(true);
                affirmationtv.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Detail(v, "Affirmation");
                    }
                });
                affirmationtv.setBackgroundResource(R.drawable.pressbuttons);
                affirmationtv.setPadding(10, 10, 10, 10);
                affirmationtv.setTextAppearance(this, R.style.healingplanitem);
                affirmationtv.setWidth(600);
                layout.addView(affirmationtv);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return rownum;
    }

    private int setMovementHealingPlan(JSONObject myHealingPlan,
                                       ArrayList<CharSequence> Movement, int idOfLastItemFromPreviousGroup)
            throws JSONException {
        Log.i("NuMe", "Movement is " + Movement.toString());

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.healingplanlinearlayoutInsideScrollView);
        int rownum = setNextHeader("Movement", idOfLastItemFromPreviousGroup,
                3000);
        JSONArray movementArray = myHealingPlan.getJSONArray("movement");
        for (int i = 0; i < movementArray.length(); i++) {
            try {
                JSONObject jObj = movementArray.getJSONObject(i);
                TextView movementtv = new TextView(HealingPlan.this);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
                        RelativeLayout.TRUE);
                params.addRule(RelativeLayout.BELOW, rownum);
                params.setMargins(10, 10, 10, 10);
                movementtv.setLayoutParams(params);
                movementtv.setText(jObj.getString("Movement").trim());
                movementtv.setTag(jObj.getString("MovementId").trim());
                movementtv.setId(++rownum);
                movementtv.setClickable(true);
                movementtv.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Detail(v, "Movement");
                    }
                });
                movementtv.setBackgroundResource(R.drawable.pressbuttons);
                movementtv.setPadding(10, 10, 10, 10);
                movementtv.setTextAppearance(this, R.style.healingplanitem);
                movementtv.setWidth(600);
                layout.addView(movementtv);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return rownum;
    }

    private int setDietHealingPlan(JSONObject myHealingPlan,
                                   ArrayList<CharSequence> Diet, int idOfLastItemFromPreviousGroup)
            throws JSONException {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.healingplanlinearlayoutInsideScrollView);
        int rownum = setNextHeader("Diet", idOfLastItemFromPreviousGroup, 2000);
        JSONArray dietArray = myHealingPlan.getJSONArray("food");
        for (int i = 0; i < dietArray.length(); i++) {
            try {
                JSONObject jObj = dietArray.getJSONObject(i);
                TextView diettv = new TextView(HealingPlan.this);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
                        RelativeLayout.TRUE);
                params.addRule(RelativeLayout.BELOW, rownum);
                params.setMargins(10, 10, 10, 10);
                diettv.setLayoutParams(params);
                diettv.setText(jObj.getString("Food").trim());
                diettv.setTag(jObj.getString("FoodId").trim());
                diettv.setId(++rownum);
                diettv.setClickable(true);
                diettv.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Detail(v, "Diet");
                    }
                });
                diettv.setBackgroundResource(R.drawable.pressbuttons);
                diettv.setPadding(10, 10, 10, 10);
                diettv.setTextAppearance(this, R.style.healingplanitem);
                diettv.setWidth(600);
                layout.addView(diettv);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return rownum;
    }

    private int setAromasHealingPlan(JSONObject myHealingPlan,
                                     ArrayList<CharSequence> Aromas, int idOfLastItemFromPreviousGroup)
            throws JSONException {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.healingplanlinearlayoutInsideScrollView);
        int rownum = setNextHeader("Aromas", idOfLastItemFromPreviousGroup,
                1000);
        JSONArray aromaArray = myHealingPlan.getJSONArray("aromas");
        for (int i = 0; i < aromaArray.length(); i++) {
            try {
                JSONObject jObj = aromaArray.getJSONObject(i);
                TextView aromatv = new TextView(HealingPlan.this);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
                        RelativeLayout.TRUE);
                params.addRule(RelativeLayout.BELOW, rownum);
                params.setMargins(10, 10, 10, 10);
                aromatv.setLayoutParams(params);
                aromatv.setText(jObj.getString("Aroma").trim());
                aromatv.setTag(jObj.getString("AromaId").trim());
                aromatv.setId(++rownum);
                aromatv.setClickable(true);
                aromatv.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Detail(v, "Aroma");
                    }
                });
                aromatv.setBackgroundResource(R.drawable.pressbuttons);
                aromatv.setPadding(10, 10, 10, 10);
                aromatv.setTextAppearance(this, R.style.healingplanitem);
                aromatv.setWidth(600);
                layout.addView(aromatv);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return rownum;
    }

    public void Detail(View vw, String modality) {
        setContentView(R.layout.modalitydetail);
        // Get the Data
        PullDetail(Integer.parseInt(vw.getTag().toString()), modality);
        Button preferredsourceBtn = (Button) findViewById(R.id.preferredsourceBtn);

        purchaseBtnListener = new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                TextView clickedHeader = (TextView) findViewById(R.id.modalityDetailHeader);
                int primaryvendor = (Integer) clickedHeader.getTag();
                int itemid = 17;
                //TODO:Get the name of the item and pass it in the intent
                TextView itemprice = (TextView) findViewById(R.id.modalityprice);
                Intent intent = new Intent(getBaseContext(), order.class);
                intent.putExtra("PrimaryVendorId", primaryvendor);
                intent.putExtra("Item", clickedHeader.getText());
                intent.putExtra("ItemPrice", itemprice.getText());
                ;

                startActivity(intent);
            }
        };
        preferredsourceBtn.setOnClickListener(purchaseBtnListener);
    }

    public void ModalityDetailSundrop(View vw) {
        setContentView(R.layout.modalitydetail);
        Button preferredsourceBtn = (Button) findViewById(R.id.preferredsourceBtn);
        TextView tv = (TextView) findViewById(R.id.modalityDetailHeader);
        tv.setText("Water");
        purchaseBtnListener = new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //	Intent intent = new Intent(getBaseContext(), vendor.class);
                Intent intent = new Intent(getBaseContext(), order.class);

                startActivity(intent);
            }
        };
        preferredsourceBtn.setOnClickListener(purchaseBtnListener);
    }

    public void Vendor(View vw) {
        Log.i("NuMe", vw.getId() + " is the id of the clicked item");
        setContentView(R.layout.vendor);
        // TextView tv = (TextView)findViewById(R.id.vendorHeader);
        // tv.setText("Ann's Healthfood Store");

    }

    private String FixString(ArrayList item) {
        return item.toString().replace("[", "").replace("]", "")
                .replace(",", "");

    }

    private SpannableString Underline(String string) {
        String udata = string;
        SpannableString content = new SpannableString(udata);
        content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
        return content;

    }

    private Spanned ConvertToLink(ArrayList item) {
        return Html.fromHtml(item.toString().replace("[", "").replace("]", ""));

    }

    public class getHealingPlan extends AsyncTask<String, Void, JSONObject> {
        // HealingPlan healingplan;
        @Override
        protected JSONObject doInBackground(String... urls) {
            JSONhelper helper = new JSONhelper();
            JSONObject myHealingPlan = JSONhelper.getJSONfromURL(urls[0]
                    .toString());
            return myHealingPlan;
        }

        @Override
        protected void onPostExecute(JSONObject myHealingPlan) {
            Log.i("NuMe", "myHealingPlan = " + myHealingPlan.toString());
            saveHealingPlan(myHealingPlan);
            setHealingPlan(myHealingPlan);

        }

        private void saveHealingPlan(JSONObject myHealingPlan) {
            SharedPreferences settings = getSharedPreferences("settings",
                    MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("healingplansaved", true);
            editor.putString("primaryenergycenter", "root");
            editor.putString("myhealingplan", myHealingPlan.toString());
            editor.commit();

        }
    }

    private void setModality(JSONObject theModality) {
        String detailText = "";
        String headerText = "";
        int primaryVendor = 0;
        String priceText = "0.00";
        try {
            JSONArray aromaDetailArray = theModality.getJSONArray("Detail");
            JSONObject jObj = aromaDetailArray.getJSONObject(0);
            detailText = jObj.getString("Description");
            primaryVendor = jObj.getInt("PrimaryVendor");
            String type = jObj.getString("Type");
            priceText = jObj.getString("Price");

            switch (Modality.valueOf(type)) {
                case Aroma:
                    headerText = jObj.getString("Aroma");
                    break;
                case Diet:
                    headerText = jObj.getString("Food");
                    break;
                case Movement:
                    headerText = jObj.getString("Movement");
                    break;
                case Affirmation:
                    headerText = jObj.getString("Affirmation");
                    break;
                case Sound:
                    headerText = jObj.getString("Sound");
                    break;
                case Meditation:
                    headerText = jObj.getString("Meditation");
                    break;
                case Herb:
                    headerText = jObj.getString("herb");
                    break;
                case Visualization:
                    headerText = jObj.getString("Visualization");
                    break;
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        TextView header = (TextView) findViewById(R.id.modalityDetailHeader);
        TextView detail = (TextView) findViewById(R.id.modalityDetail);
        TextView price = (TextView) findViewById(R.id.modalityprice);
        header.setText(headerText);

        //detail.setText(detailText);
        boolean isurl = isURL(detailText, detail);
        if (primaryVendor == 8 && isurl) {
            Button btn = (Button) findViewById(R.id.preferredsourceBtn);
            btn.setVisibility(View.VISIBLE);
            price.setText("$" + priceText);

        }
        //detail.setText(isURL(detailText,detail));
        //detail.setMovementMethod(LinkMovementMethod.getInstance());
        header.setTag(primaryVendor);

    }

    public boolean isURL(String detailText, TextView detail) {
        String urlTemp = detailText;
        if(detailText.startsWith("http://")) {
            final MediaController mMediaController = new MediaController(HealingPlan.this, true);
            mVideoView = (VideoView) findViewById(R.id.videoViewer);
            mMediaController.setEnabled(true);
            mVideoView.setMediaController(mMediaController);
            mVideoView.setVisibility(View.VISIBLE);
            pDialog = new ProgressDialog(HealingPlan.this);
            pDialog.setTitle("NUME Video");
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();
   RTSPUrlTask truitonTask = new RTSPUrlTask();
            truitonTask.execute(detailText);
            return false;
        }else{
            detail.setText(detailText);
            return true;
        }

    }

//		//if URL ends in gp3
//		if(sURL.substring(sURL.lastIndexOf('.') + 1).equals("3gp")){
//		Intent theIntent = new Intent(this,
//				VideoViewActivity.class);
//		theIntent.putExtra("URL", detailText);
//		startActivity(theIntent);
//		}else{
//		detail.setText(Html.fromHtml("<a href=\"" + url + "\">"+ url + "</a> "));
//		detail.setMovementMethod(LinkMovementMethod.getInstance());
//		}
//
//		}catch(MalformedURLException e){
//		//result = detailText;
//		detail.setText(detailText);
//		//Call Video
//		}
//	}

    //	public class URLInString {
//	    public static void main(String[] args) {
//	        String s = args[0];
//	        // separete input by spaces ( URLs don't have spaces )
//	        String [] parts = s.split("\\s");
//
//	        // Attempt to convert each item into an URL.   
//	        for( String item : parts ) try {
//	            URL url = new URL(item);
//	            // If possible then replace with anchor...
//	            System.out.print("<a href=\"" + url + "\">"+ url + "</a> " );    
//	        } catch (MalformedURLException e) {
//	            // If there was an URL that was not it!...
//	            System.out.print( item + " " );
//	        }
//
//	        System.out.println();
//	    }
//	}
//	
    public class getDetail extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... urls) {
            JSONhelper helper = new JSONhelper();
            JSONObject jsonDetail = JSONhelper.getJSONfromURL(urls[0]
                    .toString());
            return jsonDetail;
        }

        @Override
        protected void onPostExecute(JSONObject jsonDetail) {
            setModality(jsonDetail);

        }

    }
    void startPlaying(String url) {
       uriYouTube = Uri.parse(url);
        mVideoView.setVideoURI(uriYouTube);
        mVideoView.start();
        pDialog.dismiss();

    }
    private class RTSPUrlTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = getRTSPVideoUrl(urls[0]);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            startPlaying(result);
        }
        public String getRTSPVideoUrl(String urlYoutube) {
            try {
                String gdy = "http://gdata.youtube.com/feeds/api/videos/";
                DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder();
                String id = extractYoutubeId(urlYoutube);
                URL url = new URL(gdy + id);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                Document doc = dBuilder.parse(connection.getInputStream());
                Element el = doc.getDocumentElement();
                NodeList list = el.getElementsByTagName("media:content");
                String cursor = urlYoutube;
                for (int i = 0; i < list.getLength(); i++) {
                    Node node = list.item(i);
                    if (node != null) {
                        NamedNodeMap nodeMap = node.getAttributes();
                        HashMap<String, String> maps = new HashMap<String, String>();
                        for (int j = 0; j < nodeMap.getLength(); j++) {
                            Attr att = (Attr) nodeMap.item(j);
                            maps.put(att.getName(), att.getValue());
                        }
                        if (maps.containsKey("yt:format")) {
                            String f = maps.get("yt:format");
                            if (maps.containsKey("url"))
                                cursor = maps.get("url");
                            if (f.equals("1"))
                                return cursor;
                        }
                    }
                }
                return cursor;
            } catch (Exception ex) {
                return urlYoutube;
            }
        }

        public String extractYoutubeId(String url) throws MalformedURLException {
            String query = new URL(url).getQuery();
            String[] param = query.split("&");
            String id = null;
            for (String row : param) {
                String[] param1 = row.split("=");
                if (param1[0].equals("v")) {
                    id = param1[1];
                }
            }
            return id;
        }
    }



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuinflater = getMenuInflater();
		menuinflater.inflate(R.menu.usermenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		WelcomeScreenActivity Welcome = new WelcomeScreenActivity();

		switch (item.getItemId()) {
		case R.id.resetoption:
			Welcome.resetHealingPlan(this);
			break;
		case R.id.quitoption:
			Welcome.QuitApp();
			break;
		case R.id.helpoption:
			Welcome.ShowVersion(this);
			break;
		default:
		}
		return super.onOptionsItemSelected(item);
	}

}

