package com.nume.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Food extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	setContentView(R.layout.food_activity);
		pullFood();
		Button backButton = (Button)this.findViewById(R.id.btnDone);
		backButton.setOnClickListener(new OnClickListener() {
		  @Override
		  public void onClick(View v) {
		    finish();
		  }
		});
	
	}
	
		private void pullFood() {
		String urlCall = "http://nume.detrickdeburr.com/Solutions/GetFood";
		new GetFood().execute(urlCall);
	}
	
	private void setFood(JSONObject Food){
		//Log.i("NuMe", "Settting Food"+ Food.toString());
		String CurrChakra="";
		String CurrFood="";
		String PrevChakra="";
		int myId=0;
		int rowmarker =100;
		int rownumber=1;
		int lastwrittenitem=0;
		JSONArray FoodArray;
		try {
			FoodArray = Food.getJSONArray("result");
			for (int i = 0; i < FoodArray.length();i++ ) {
			//for (int i = 0; i < 3; i++) {
			JSONObject jObj = FoodArray.getJSONObject(i);
			CurrChakra = jObj.getString("Chakra");
			char first = Character.toUpperCase(CurrChakra.charAt(0));
			CurrChakra = first + CurrChakra.substring(1);
			CurrFood=jObj.getString("Food");
			RelativeLayout layout = (RelativeLayout) findViewById(R.id.foodlinearlayoutInsideScrollView);
			if(!CurrChakra.equals(PrevChakra)){
			PrevChakra=CurrChakra;
			myId=SetHeader(CurrChakra,layout,rownumber,lastwrittenitem);
			lastwrittenitem=myId;
			rownumber++;
			}
			myId=SetFood(CurrFood,layout,rownumber,lastwrittenitem);
			lastwrittenitem=myId;
			rownumber++;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private int SetFood(String CurrFood,View layout,int itemId,int itemAbove){
		TextView foodTV = new TextView(Food.this);
		foodTV.setId(itemId);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
				RelativeLayout.TRUE);
		params.addRule(RelativeLayout.BELOW, itemAbove);
		params.setMargins(10, 10, 10, 10);
		foodTV.setLayoutParams(params);
		foodTV.setText(CurrFood);
		foodTV.setTextAppearance(this, R.style.apptext);
		((ViewGroup) layout).addView(foodTV);

		return foodTV.getId();
		
	}
	private int SetHeader(String CurrChakra,View layout,int headerId,int itemAbove){
		TextView Header = new TextView(Food.this);
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
	public class GetFood extends AsyncTask<String, Void, JSONObject> {
		// HealingPlan healingplan;
		@Override
		protected JSONObject doInBackground(String... urls) {
			JSONhelper helper = new JSONhelper();
			JSONObject myFood = JSONhelper.getJSONfromURL(urls[0].toString());
			return myFood;
		}

		@Override
		protected void onPostExecute(JSONObject myFood) {
			setFood(myFood);

		}
		
		
	}

}
