package com.nume.mobile;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;



public class Therapy extends Activity {
	OnClickListener doneBtnListener;
	ArrayList<CharSequence> SelectedItems;
	String PrimaryEnergyCenter;
	public static final String CROWN="crown";
	public static final String BROW="brow";
	public static final String THROAT="throat";
	public static final String HEART="heart";
	public static final String SOLARPLEXUS="solarplexus";
	public static final String NAVEL="navel";
	public static final String ROOT="root";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.therapies_activity);
		GetTherapy();
		}
	
    private void GetTherapy() {
		//http://nume.detrickdeburr.com/Therapy/GetAll	
    	SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
    	PrimaryEnergyCenter= settings.getString("primaryenergycenter", "root");
    	String urlCall="http://nume.detrickdeburr.com/Therapy/GetTherapy?energycenter=";
		String ActualCall = urlCall+PrimaryEnergyCenter;
		Log.i("NuMe", "ActualCall: "+ ActualCall);
		new getTherapyAsync().execute(ActualCall);
		
	}
	
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case android.R.id.home:
            goHome();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    private void goHome(){
		   Intent intent = new Intent(getBaseContext(), WelcomeScreenActivity.class);
		   startActivity(intent);
		   
	 }
    public void setOtherTherapy(JSONObject OtherTherapy) throws JSONException {
    	ArrayList<CharSequence>  Root = new ArrayList<CharSequence>();
		ArrayList<CharSequence>  SolarPlexus = new ArrayList<CharSequence>();
		ArrayList<CharSequence>  Navel = new ArrayList<CharSequence>();
		ArrayList<CharSequence>  Heart = new ArrayList<CharSequence>();
		ArrayList<CharSequence>  Throat = new ArrayList<CharSequence>();
		ArrayList<CharSequence>  Brow = new ArrayList<CharSequence>();
		ArrayList<CharSequence>  Crown = new ArrayList<CharSequence>();
		
		
		
		if (PrimaryEnergyCenter==ROOT){
				setTherapy(OtherTherapy,Root);
		}
		if (PrimaryEnergyCenter==SOLARPLEXUS){
			setTherapy(OtherTherapy,SolarPlexus);
		}
		if (PrimaryEnergyCenter==NAVEL){
			setTherapy(OtherTherapy,Navel);
		}
		if (PrimaryEnergyCenter==HEART){
			setTherapy(OtherTherapy,Heart);
		}
		if (PrimaryEnergyCenter==THROAT){
			setTherapy(OtherTherapy,Throat);
		}
		if (PrimaryEnergyCenter==BROW){
			setTherapy(OtherTherapy,Brow);
		}
		if (PrimaryEnergyCenter==CROWN){
			setTherapy(OtherTherapy,Crown);
		}
	
	}
   
    private void setTherapy(JSONObject OtherTherapy,
			ArrayList<CharSequence> therapy) throws JSONException {
    	TextView tv = null;
    	TextView headertv = null;
		JSONArray Array = OtherTherapy.getJSONArray("result");
            for(int i = 0; i < Array.length(); i++) {
                	JSONObject jObj = Array.getJSONObject(i);
                	therapy.add("<a href=\"http://"+jObj.getString("resource").trim()+"\">"+jObj.getString("therapy").trim()+"</a>");
                	if (PrimaryEnergyCenter==ROOT){
                		headertv = (TextView)findViewById(R.id.RootEnergyCenter);
                        tv = (TextView)findViewById(R.id.RootEnergyCenterTherapies);
                    	}
                	if (PrimaryEnergyCenter==SOLARPLEXUS){
                	headertv = (TextView)findViewById(R.id.SolarPlexEnergyCenter);
                    tv = (TextView)findViewById(R.id.SolarPlexEnergyTherapies);
                	}
                	if (PrimaryEnergyCenter==NAVEL){
                		headertv = (TextView)findViewById(R.id.NavelEnergyCenter);
                        tv = (TextView)findViewById(R.id.NavelEnergyCenterTherapies);
                    }
                	if (PrimaryEnergyCenter==HEART){
                		headertv = (TextView)findViewById(R.id.HeartEnergyCenter);
                        tv = (TextView)findViewById(R.id.HeartEnergyCenterTherapies);
                    }
                	if (PrimaryEnergyCenter==THROAT){
                		headertv = (TextView)findViewById(R.id.ThroatEnergyCenter);
                        tv = (TextView)findViewById(R.id.ThroatEnergyCenterTherapies);
                    }
                	if (PrimaryEnergyCenter==BROW){
                		headertv = (TextView)findViewById(R.id.BrowEnergyCenter);
                        tv = (TextView)findViewById(R.id.BrowEnergyCenterTherapies);
                    }
                	if (PrimaryEnergyCenter==CROWN){
                		headertv = (TextView)findViewById(R.id.CrownEnergyCenter);
                        tv = (TextView)findViewById(R.id.CrownEnergyCenterTherapies);
                    }
				tv.setText(Html.fromHtml(ConvertToString(therapy)));
				headertv.setVisibility(View.VISIBLE);
				tv.setVisibility(View.VISIBLE);
				tv.setMovementMethod(LinkMovementMethod.getInstance());
            }
	}
    
    private String ConvertToString(ArrayList<CharSequence> ailments) {
		String convertedString="";
		
		for(int i=0;i<ailments.size();i++){
			
			convertedString+=ailments.get(i)+",";	
			
		}
		convertedString = convertedString.substring(0, convertedString.length() - 1);
		return convertedString;
	}
    
	public class getTherapyAsync extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... urls) {
		JSONhelper helper = new JSONhelper();
		JSONObject OtherTherapy = helper.getJSONfromURL(urls[0].toString());
		return OtherTherapy;
		}

		@Override
		protected void onPostExecute(JSONObject OtherTherapy) {

			try {
				setOtherTherapy(OtherTherapy);
			} catch (JSONException e) {
				e.printStackTrace();
			}
				    
		}
	
	
	
	}
	
	
	

		}




