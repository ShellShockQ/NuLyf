package com.nume.android;


//import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;


public class WelcomeScreenActivity extends Activity {
	public final String TAG = "WelcomeScreenActivity";
	OnClickListener getassessmentnowImgBtnListener;
	OnClickListener assessmentImgBtnListener;
	OnClickListener healingPlanImgBtnListener;
	OnClickListener therapiesImgBtnListener;
	OnClickListener storeImgBtnListener;
	OnClickListener foodImgBtnListener;
	OnClickListener hygieneImgBtnListener;
	OnClickListener selfimageImgBtnListener;
	OnClickListener affirmationImgBtnListener;
	public Boolean HEALINGPLANFOUND=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		setContentView(R.layout.activity_welcome);
		SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
		HEALINGPLANFOUND= settings.getBoolean("healingplansaved", false);
		Button affirmationImgBtn = (Button)findViewById(R.id.imgbtnAffirmation);
		Button foodImgBtn = (Button)findViewById(R.id.imgbtnFood);
		
		if (HEALINGPLANFOUND){
			showAllButtons();
		}else{
			
			showGetAssessmentOnly();
		}
		affirmationBtn(affirmationImgBtn);
		foodBtn(foodImgBtn);
		
		
		assessmentImgBtnListener = new OnClickListener(){

		   @Override
			public void onClick(View arg0) {
				
			   Intent intent = new Intent(getBaseContext(), AssessmentActivity1.class);
			   intent.putExtra("AssessmentCompletionPct", 0);
			   startActivity(intent);
			   
			   
			}
		};

	
	
	}

	private void affirmationBtn(Button affirmationImgBtn) {
		affirmationImgBtnListener = new OnClickListener(){
			   @Override
				public void onClick(View arg0) {
				   Intent intent = new Intent(getBaseContext(), Affirmation.class);
				   startActivity(intent);
				   
				   
				}
			};

			affirmationImgBtn.setOnClickListener(affirmationImgBtnListener);
	}
	
	private void foodBtn(Button foodImgBtn) {
		foodImgBtnListener = new OnClickListener(){
			   @Override
				public void onClick(View arg0) {
				   Intent intent = new Intent(getBaseContext(), Food.class);
				   startActivity(intent);
				   
				   
				}
			};

			foodImgBtn.setOnClickListener(foodImgBtnListener);
	}
	
	@Override
	  public void onStart() {
	    super.onStart();
	   // EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	  }
	@Override
	  public void onStop() {
	    super.onStop();

	   // EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	  }
	

	private void showGetAssessmentOnly() {
		Button assessmentImgBtn = (Button)findViewById(R.id.imgBtnAssessment);
		Button healingPlanImgBtn = (Button)findViewById(R.id.imgBtnHealingPlan);
		Button therapiesImgBtn = (Button)findViewById(R.id.imgBtnTherapy);
		Button storeImgBtn = (Button)findViewById(R.id.imgbtnStore);
		Button dietImgBtn = (Button)findViewById(R.id.imgbtnFood);
	//	ImageButton hygieneImgBtn = (ImageButton)findViewById(R.id.imgbtnHygiene);
		Button selfimageImgBtn = (Button)findViewById(R.id.imgbtnSelfImage);
		Button getassessmentnowImgBtn = (Button)findViewById(R.id.imgBtnGetAssessmentNow);
		assessmentImgBtn.setVisibility(Button.GONE);
		healingPlanImgBtn.setVisibility(Button.GONE);
		therapiesImgBtn.setVisibility(Button.GONE);
		getassessmentnowImgBtn.setVisibility(Button.VISIBLE);
		getassessmentnowImgBtnListener = new OnClickListener(){

			   @Override
				public void onClick(View arg0) {
					
				   Intent intent = new Intent(getBaseContext(), AssessmentActivity1.class);
				   intent.putExtra("AssessmentCompletionPct", 0);
				   startActivity(intent);
				   
				   
				}
			};
			getassessmentnowImgBtn.setOnClickListener(getassessmentnowImgBtnListener);
		
			storeImgBtnListener = new OnClickListener(){

				   @Override
					public void onClick(View arg0) {
						
					   Intent intent = new Intent(getBaseContext(), NuMeDummyActivity.class);
					   startActivity(intent);
					   
					   
					}
				};
	
				storeImgBtn.setOnClickListener(storeImgBtnListener);
				foodImgBtnListener = new OnClickListener(){

					   @Override
						public void onClick(View arg0) {
							
						   Intent intent = new Intent(getBaseContext(),Food.class);
						   startActivity(intent);
						   
						   
						}
					};
		
					dietImgBtn.setOnClickListener(foodImgBtnListener);
					hygieneImgBtnListener = new OnClickListener(){

						   @Override
							public void onClick(View arg0) {
								
							   Intent intent = new Intent(getBaseContext(), NuMeDummyActivity.class);
							   startActivity(intent);
							   
							   
							}
						};
			
					/*	hygieneImgBtn.setOnClickListener(hygieneImgBtnListener);
						
						selfimageImgBtnListener = new OnClickListener(){

							   @Override
								public void onClick(View arg0) {
									
								   Intent intent = new Intent(getBaseContext(), NuMeDummyActivity.class);
								   startActivity(intent);
								   
								   
								}
							};
				*/
							selfimageImgBtn.setOnClickListener(selfimageImgBtnListener);
							
				
									
	}

	private void showAllButtons() {
		Button assessmentImgBtn = (Button)findViewById(R.id.imgBtnAssessment);
		Button healingPlanImgBtn = (Button)findViewById(R.id.imgBtnHealingPlan);
		Button therapiesImgBtn = (Button)findViewById(R.id.imgBtnTherapy);
		Button getassessmentnowImgBtn = (Button)findViewById(R.id.imgBtnGetAssessmentNow);
		assessmentImgBtn.setVisibility(ImageButton.VISIBLE);
		healingPlanImgBtn.setVisibility(ImageButton.VISIBLE);
		therapiesImgBtn.setVisibility(ImageButton.VISIBLE);
		getassessmentnowImgBtn.setVisibility(ImageButton.GONE);
		
		
		assessmentImgBtnListener = new OnClickListener(){

			   @Override
				public void onClick(View arg0) {
					
				   Intent intent = new Intent(getBaseContext(), AssessmentActivity1.class);
				   intent.putExtra("AssessmentCompletionPct", 0);
				   startActivity(intent);
				   
				   
				}
			};
		assessmentImgBtn.setOnClickListener(assessmentImgBtnListener);
		
		healingPlanImgBtnListener = new OnClickListener(){

			   @Override
				public void onClick(View arg0) {
				   Log.i("NuMe","Your Healing Plan Button Clicked");
				   Intent intent = new Intent(getBaseContext(), HealingPlan.class);
				   startActivity(intent);
				   
				   
				}
			};
			healingPlanImgBtn.setOnClickListener(healingPlanImgBtnListener);
		
			therapiesImgBtnListener = new OnClickListener(){

				   @Override
					public void onClick(View arg0) {
						
					   Intent intent = new Intent(getBaseContext(), Therapy.class);
					   startActivity(intent);
					   
					   
					}
				};
				therapiesImgBtn.setOnClickListener(therapiesImgBtnListener);

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuinflater = getMenuInflater();
		menuinflater.inflate(R.menu.usermenu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()){
		
		case R.id.resetoption:
		resetHealingPlan(this);
		break;
		case R.id.quitoption:
		QuitApp();
		break;	
		case R.id.helpoption:
		ShowVersion(this);
		break;
		default:
		
		}
		return super.onOptionsItemSelected(item);
	}

	public void QuitApp() {
		//this.finish();
		System.exit(0);  
	}

	public void ShowVersion(Context ctx){
		Toast.makeText(ctx, "Version: "+getVersionName()+getVersion(),Toast.LENGTH_LONG).show();
		
		
	}
	public String getVersion() {
	    return Integer.toString(BuildConfig.VERSION_CODE);

	}
	
	public String getVersionName() {
		return BuildConfig.VERSION_NAME;
	}
	public void resetHealingPlan(Context ctx) {
		// TODO Auto-generated method stub
		SharedPreferences settings = ctx.getSharedPreferences("settings", MODE_PRIVATE);
		if(settings!=null){
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
		Toast.makeText(ctx, "Healing Plan Reset", Toast.LENGTH_LONG).show();
		}
		
	}
	

}
