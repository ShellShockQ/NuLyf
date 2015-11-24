package com.nume.android;


import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class NuMeDummyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nu_me_dummy);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nu_me_dummy, menu);
		return true;
	}

}
