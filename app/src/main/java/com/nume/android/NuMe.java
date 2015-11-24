package com.nume.android;

import android.app.Activity;
import android.content.Intent;

public class NuMe extends Activity {
	 public void goHome(){
		   Intent intent = new Intent(getBaseContext(), WelcomeScreenActivity.class);
		   startActivity(intent);
		   
	 }
}
