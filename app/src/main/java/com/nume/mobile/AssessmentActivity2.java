package com.nume.mobile;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class AssessmentActivity2 extends Activity {
	OnClickListener saveBtnListener;
	ListView Selectedlist;
	ItemsAdapter   adapter;
	ArrayList<CharSequence> SelectedItems;	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
		   setContentView(R.layout.assessment_activity2);
		   //Button saveBtn = (Button)findViewById(R.id.saveBtn);
		   Button saveBtn = (Button)findViewById(R.id.saveBtn);
		  Bundle extras = getIntent().getExtras();
		   int assessmentCompletePct = extras.getInt("AssessmentCompletionPct");
		   SelectedItems = extras.getCharSequenceArrayList("SelectedItems");
		   
	//	Log.d("NuMe", SelectedItems.toString()+" is returned to new activity. Good Job!");
		   TextView txtView = (TextView)findViewById(R.id.assessmentProgressInAssessmentActivity2);
		   txtView.setText("Assessment "+assessmentCompletePct+"% Complete");
		   buildPriorityList(SelectedItems);
		   
		   
		saveBtnListener = new OnClickListener(){

		   @Override
			public void onClick(View arg0) {
			  //TODO: Write the ailments and there priority
			//   SaveAilments();
			   goHealingPlanActivity(arg0);			
			 
		   }

		private void SaveAilments() {
		// TODO Get the Ailments from the Activity
		SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("healingplansaved", true);
		editor.putString("primaryenergycenter", "root");
		editor.commit();	
		int ItemCount = Selectedlist.getCount();
		View v;
	//	Spinner s;
	//	for(int i=0; i<ItemCount;i++){
	//        v = Selectedlist.getAdapter().getView(i, null, null);
	//   }
		}

		
		
		private void goHealingPlanActivity(View arg0) {
			   Intent intent = new Intent(getBaseContext(), HealingPlan.class);
			   intent.putExtra("SelectedItems", SelectedItems);
			   if(SelectedItems.isEmpty()){
					Toast.makeText(AssessmentActivity2.this, "Please Select at least One Item you are experiencing... Go Back <<", Toast.LENGTH_LONG).show();
					}else{
			   startActivity(intent);
			   }
		}
		};
		saveBtn.setOnClickListener(saveBtnListener);
	}

	private void buildPriorityList(ArrayList<CharSequence> SelectedAilments) {
		 adapter = new ItemsAdapter(this,SelectedAilments);
		 Selectedlist =(ListView)findViewById(R.id.selectedailmentlist);
		 Selectedlist.setAdapter(adapter);
		 
		 Selectedlist.setOnItemSelectedListener(new OnItemSelectedListener() {
	        	@Override
	        	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
	        	}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
		    		
					
				}
	        }) ;	
	}
	 private class ItemsAdapter extends BaseAdapter {
		 ArrayList<CharSequence> items;

		 public ItemsAdapter(Context context, ArrayList<CharSequence> selectedAilments) {
		 this.items = selectedAilments;
		 }

		 // @Override
		 public View getView(int position, View convertView, ViewGroup parent) {
		 View v = convertView;
		 if (v == null) {
		 LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 v = vi.inflate(R.layout.selectedailments, null);
		 if(adapter.getCount() >1){
				Spinner spinner = (Spinner)v.findViewById(R.id.spinner1);
				 spinner.setVisibility(View.VISIBLE);
				 		 }
		 } 
		 
		 ListView post = (ListView) v.findViewById(R.id.selectedailmentlist);
		 TextView tv = (TextView)v.findViewById(R.id.selectedailmentText);
		 tv.setText(items.get(position));
	
		
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
