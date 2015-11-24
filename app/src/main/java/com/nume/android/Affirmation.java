package com.nume.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Affirmation extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	setContentView(R.layout.affirmation_activity);
		PullAffirmations();
		Button backButton = (Button)this.findViewById(R.id.btnDone);
		backButton.setOnClickListener(new OnClickListener() {
		  @Override
		  public void onClick(View v) {
		    finish();
		  }
		});
	
	}
	
		private void PullAffirmations() {
		String urlCall = "http://nume.detrickdeburr.com/Solutions/GetAffirmations";
		new getAffirmations().execute(urlCall);
	}
	
	private void SetAffirmations(JSONObject Affirmations){
		Log.i("NuMe", "Settting Affirmations"+ Affirmations.toString());
		String CurrChakra="";
		String CurrAffirmation="";
		String PrevChakra="";
		int myId=0;
		int rowmarker =100;
		int rownumber=1;
		int lastwrittenitem=0;
		JSONArray AffirmationsArray;
		try {
			AffirmationsArray = Affirmations.getJSONArray("result");
			for (int i = 0; i < AffirmationsArray.length();i++ ) {
			//for (int i = 0; i < 3; i++) {
			JSONObject jObj = AffirmationsArray.getJSONObject(i);
			CurrChakra = jObj.getString("Chakra");
			char first = Character.toUpperCase(CurrChakra.charAt(0));
			CurrChakra = first + CurrChakra.substring(1);
			CurrAffirmation=jObj.getString("Affirmation");
			RelativeLayout layout = (RelativeLayout) findViewById(R.id.affirmationslinearlayoutInsideScrollView);
			if(!CurrChakra.equals(PrevChakra)){
			PrevChakra=CurrChakra;
			myId=SetHeader(CurrChakra,layout,rownumber,lastwrittenitem);
			lastwrittenitem=myId;
			rownumber++;
			}
			myId=SetAffirmation(CurrAffirmation,layout,rownumber,lastwrittenitem);
			lastwrittenitem=myId;
			rownumber++;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private int SetAffirmation(String CurrAffirmation,View layout,int itemId,int itemAbove){
		TextView affirmationTv = new TextView(Affirmation.this);
		affirmationTv.setId(itemId);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
				RelativeLayout.TRUE);
		params.addRule(RelativeLayout.BELOW, itemAbove);
		params.setMargins(10, 10, 10, 10);
		affirmationTv.setLayoutParams(params);
		affirmationTv.setText(CurrAffirmation);
		affirmationTv.setTextAppearance(this, R.style.apptext);
		((ViewGroup) layout).addView(affirmationTv);

		return affirmationTv.getId();
		
	}
	private int SetHeader(String CurrChakra,View layout,int headerId,int itemAbove){
		TextView Header = new TextView(Affirmation.this);
		Header.setId(headerId);
		RelativeLayout.LayoutParams headerParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		headerParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
				RelativeLayout.TRUE);
		headerParams.addRule(RelativeLayout.BELOW, itemAbove);
		Header.setLayoutParams(headerParams);
		Header.setText(CurrChakra);
		Header.setTextAppearance(this, R.style.appheading);
		((ViewGroup) layout).addView(Header);
		return Header.getId();
	}
	public class getAffirmations extends AsyncTask<String, Void, JSONObject> {
		// HealingPlan healingplan;
		@Override
		protected JSONObject doInBackground(String... urls) {
			JSONhelper helper = new JSONhelper();
			JSONObject myAffirmations = JSONhelper.getJSONfromURL(urls[0].toString());
			return myAffirmations;
		}

		@Override
		protected void onPostExecute(JSONObject myAffirmations) {
			SetAffirmations(myAffirmations);

		}
		
		
	}

}
