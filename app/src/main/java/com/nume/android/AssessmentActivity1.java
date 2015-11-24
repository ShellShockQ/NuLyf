package com.nume.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AssessmentActivity1 extends Activity {
	public final String TAG = "AssessmentActivity1";
	OnClickListener nextBtnListener;
	int CompletionPercent;
	ListView list;
	Button but;
	ItemsAdapter adapter;
	public int getCompletionPercent() {
		return CompletionPercent;
	}

	public void setCompletionPercent(int completionPercent) {
		CompletionPercent = completionPercent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.assessment_activity1);
		//Button nextBtn = (Button)findViewById(R.id.nextBtn);
		Button nextBtn = (Button)findViewById(R.id.nextBtn);
		setTextViewTextOnAssessmentAct1();
		pullListOfAilmentsDatabase();
		nextBtnListener = new OnClickListener(){

		   @Override
			public void onClick(View arg0) {
			   goToAssessmentActivity2(arg0);			 
		   }
		};
		nextBtn.setOnClickListener(nextBtnListener);	
		
	}

	public void goToAssessmentActivity2(View v){
		  ArrayList<String> checkedItems = getCheckedItems(); 
	
			
		  Intent intent = new Intent(getBaseContext(), AssessmentActivity2.class);
		   setCompletionPercent(100);
		   intent.putExtra("AssessmentCompletionPct",getCompletionPercent());
		   intent.putExtra("SelectedItems", checkedItems);
		   startActivity(intent);
	}
	
	
	private ArrayList<String> getCheckedItems() {
		int ItemCount=list.getChildCount();
		ArrayList<String> checkedItemArray = new ArrayList<String>() ;
		for (int i = 0; i < ItemCount; i++) {
		View v = list.getChildAt(i);
		CheckedTextView cv = (CheckedTextView)v.findViewById(R.id.checkList);
		if (cv.isChecked()) {
			
			checkedItemArray.add(cv.getText().toString());
		}
		}
		return checkedItemArray;
		
	}

	private void setTextViewTextOnAssessmentAct1() {
		 Bundle extras = getIntent().getExtras();
		   CompletionPercent = extras.getInt("AssessmentCompletionPct");
		   TextView txtView = (TextView)findViewById(R.id.assessmentProgressInAssessmentActivity1);
		   txtView.setText("Assessment "+ CompletionPercent +"% Complete");
	}
	
		
	 private void pullListOfAilmentsDatabase() {
		String urlCall="http://nume.detrickdeburr.com/Solutions/GetAilments";
		new getAilments().execute(urlCall);
	}

	 private ArrayList<String> JSONToStringArray(JSONArray jsonArray){
	 ArrayList<String> stringArray = new ArrayList<String>();
	 //JSONArray jsonArray = new JSONArray();
	 for(int i = 0, count = jsonArray.length(); i< count; i++)
	 {
	     try {
	         JSONObject jsonObject = jsonArray.getJSONObject(i);
	         stringArray.add(jsonObject.toString());
	     }
	     catch (JSONException e) {
	         e.printStackTrace();
	     }
	 }
	 return stringArray;
	 }
	public class ItemsAdapter extends BaseAdapter {
		 ArrayList<String> items;

		 public ItemsAdapter(Context context, ArrayList<String> item) {
		 this.items = item;
		 }

		 // @Override
		 public View getView(int position, View convertView, ViewGroup parent) {
		 View v = convertView;
		 if (v == null) {
		 LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 v = vi.inflate(R.layout.row, null);
		 } 
		final CheckedTextView ctv = (CheckedTextView) v.findViewById(R.id.checkList);
			 ctv.setText(items.get(position));
			 ctv.setOnClickListener(new OnClickListener() {
				 @Override
				 public void onClick(View view) {
					 if (ctv.isChecked())
						 ctv.setChecked(false);
					 else
						 ctv.setChecked(true);


				 }
			 });
			 return v;
		 }

		 public int getCount() {
		 return items.size();
		 }

		 public Object getItem(int position) {
		 return position;
		 }

		 public long getItemId(int position) {
		 return position;
		 }


	}

		public void setAilments(JSONObject ailments) {
			ArrayList<String> Ailments = new ArrayList<String>();
				try {
	            JSONArray excessArray = ailments.getJSONArray("excess");
	            JSONArray deficiencyArray = ailments.getJSONArray("deficiency");
	            for(int i = 0; i < excessArray.length(); i++) {
	                try {
	                	JSONObject jObj2 =excessArray.getJSONObject(i);
	                //	JSONArray excessArray2 = ailments.getJSONArray("Excess");
	                	Ailments.add(jObj2.getString("Excess"));
	                	
	    				
	                } catch (JSONException e) {
	                    e.printStackTrace();
	                }
	            }
	            for(int i = 0; i < deficiencyArray.length(); i++) {
	                try {
	                	JSONObject jObj3 =deficiencyArray.getJSONObject(i);
	                //	JSONArray excessArray2 = ailments.getJSONArray("Excess");
	                	Ailments.add(jObj3.getString("Deficiency"));
	                	
	    				
	                } catch (JSONException e) {
	                    e.printStackTrace();
	                }
	            }
	            
	            
	            
	        } catch (JSONException e) {
	            e.printStackTrace();
	        }
				adapter = new ItemsAdapter(this,Ailments);
				 list =(ListView)findViewById(R.id.ailmentlist);
			     list.setAdapter(adapter);

//					setOnItemClickListener(new OnItemClickListener() {
//			        	@Override
//			        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
//			        	// TODO Auto-generated method stub
//			        	CheckedTextView tv = (CheckedTextView)arg1;
//			        	toggle(tv);
//			        	}
//			        }) ;
		}
	public void toggle(CheckBox v)
	{
		if (v.isChecked())
		{
			v.setChecked(false);
		}
		else
		{
			v.setChecked(true);
		}
	}
		public class getAilments extends AsyncTask<String, Void, JSONObject> {
			@Override
			protected JSONObject doInBackground(String... urls) {
			JSONhelper helper = new JSONhelper();
			return helper.getJSONfromURL(urls[0].toString());
			}

			@Override
			protected void onPostExecute(JSONObject ailments) {
			//Log.d(TAG, "Ailments pulled from server: " + ailments.toString());
				setAilments(ailments);
					    
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
			return false; //super.onOptionsItemSelected(item);
		}
}
