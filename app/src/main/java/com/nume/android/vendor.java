package com.nume.android;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class vendor  extends Activity {
	OnClickListener doneBtnListener;
	protected void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor);
		View bg = findViewById(R.id.vendorlayout);
		Bundle extras = getIntent().getExtras();
		int PrimaryVendor = extras.getInt("PrimaryVendorId");
		getPrimaryVendor(PrimaryVendor);
		
		Button doneBtn = (Button)findViewById(R.id.savedone);
						
		doneBtnListener = new OnClickListener(){

			@Override
			public void onClick(View v) {
				WelcomeScreenActivity Welcome = new WelcomeScreenActivity();
				Welcome.QuitApp();
			}
			
		};
		doneBtn.setOnClickListener(doneBtnListener);
	}
	
	private void getPrimaryVendor(int pvid) {
		String urlCall = "";
		urlCall = "http://nume.detrickdeburr.com/Vendor/GetVendor?id=" + pvid;
		Log.d("NuMe","urlCall: "+urlCall);
			new getPrimaryVendorJSON().execute(urlCall);
	}
	
	public class getPrimaryVendorJSON extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... urls) {
			JSONhelper helper = new JSONhelper();
			JSONObject jsonDetail = JSONhelper.getJSONfromURL(urls[0].toString());
			return jsonDetail;
		}

		@Override
		protected void onPostExecute(JSONObject jsonDetail) {
			setVendor(jsonDetail);

		}

		private void setVendor(JSONObject jsonDetail) {
			Log.d("NuMe","jsonDetail: "+jsonDetail);
			try {
				JSONArray VendorArray = jsonDetail.getJSONArray("vendor");
				if(VendorArray.toString()!=""){
				JSONObject jObj = VendorArray.getJSONObject(0);
				String logo=jObj.getString("logo").trim();
				String zip=jObj.getString("zip").trim();
				String phone=jObj.getString("phone").trim();
				String address=jObj.getString("address").trim();
				String description=jObj.getString("description").trim();
				String name=jObj.getString("name");
				String state=jObj.getString("state").trim();
				String city=jObj.getString("city").trim();
				ImageView ivLogo = (ImageView)findViewById(R.id.vendorlogo);
				TextView tvVendorName = (TextView)findViewById(R.id.vendorname);
				TextView tvDetail = (TextView)findViewById(R.id.vendordetail);
				TextView tvPhoneNumber = (TextView)findViewById(R.id.phonenumber);
				TextView tvAddress = (TextView)findViewById(R.id.address);
				ivLogo.setBackgroundResource(R.drawable.sundrop);
				tvDetail.setText(description);
				tvPhoneNumber.setText(phone);
				tvAddress.setText(address +" "+ city+"," +state +" "+ zip);
			//	tvVendorName.setText(name);
				}else{
					
				}

			} catch (JSONException e) {
				noVendorFound();
				e.printStackTrace();
			}
		
			
		}

	}

	public void noVendorFound() {
		Button phoneButton = (Button)findViewById(R.id.phonebutton);
		Button addressButton = (Button)findViewById(R.id.mapbutton);
		phoneButton.setVisibility(View.GONE);
		addressButton.setVisibility(View.GONE);
		String detailText="No Vendor Found!";
		TextView header = (TextView) findViewById(R.id.vendordetail);
		header.setText(detailText);
		
		
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
		
		switch(item.getItemId()){
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
