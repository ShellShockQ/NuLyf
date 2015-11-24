package com.nume.android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class order extends Activity {

	OnClickListener ConfirmBtnListener;
    private static final String TAG = "paymentExample";
    /**
     * - Set to PaymentActivity.ENVIRONMENT_PRODUCTION to move real money.
     *
     * - Set to PaymentActivity.ENVIRONMENT_SANDBOX to use your test credentials
     * from https://developer.paypal.com
     *
     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
     * without communicating to PayPal's servers.
     */
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;

    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "credential from developer.paypal.com";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
                    // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("Nu Dred")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));


	protected void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
		View bg = findViewById(R.id.orderlayout);
        double shippingcost=1.95;
        Bundle extras = getIntent().getExtras();
	    final int PrimaryVendor = extras.getInt("PrimaryVendorId");
        final String Item =extras.getString("Item");

        String ItemPriceAmt = extras.getString("ItemPrice").replaceAll("(?<=\\d),(?=\\d)|\\$", "");
        TextView Price = (TextView)findViewById(R.id.txtviewprice);
        TextView Tax = (TextView)findViewById(R.id.txtviewtax);
        TextView Shipping = (TextView)findViewById(R.id.txtviewshipping);
        TextView Total = (TextView)findViewById(R.id.txtviewtotal);
        Price.setText("Price: $"+ItemPriceAmt);
        Shipping.setText("Shipping: $"+ shippingcost);
        double taxamt = CalculateTax(ItemPriceAmt);
        final double totalamt=(Double.parseDouble(ItemPriceAmt)+shippingcost+taxamt);
        Tax.setText("Tax: $"+String.valueOf(taxamt));
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        Total.setText("Total: "+formatter.format(totalamt));
        // TODO: Calculate the total

        //	getPrimaryVendor(PrimaryVendor);
		
		Button ConfirmBtn = (Button)findViewById(R.id.btnconfirm);
        final CheckBox ShippingCheckBox = (CheckBox)findViewById(R.id.chckBoxShipping);
        final TextView txtBillingAddress = (TextView)findViewById(R.id.txtBillingAddress);
        final TextView txtShippingAddress = (TextView)findViewById(R.id.txtShippingAddress);
        final TextView txtBillingCity = (TextView)findViewById(R.id.txtBillingCity);
        final TextView txtShippingCity = (TextView)findViewById(R.id.txtShippingCity);
        final TextView txtBillingState = (TextView)findViewById(R.id.txtBillingState);
        final TextView txtShippingState = (TextView)findViewById(R.id.txtShippingState);
        final TextView txtBillingZip = (TextView)findViewById(R.id.txtBillingZip);
        final TextView txtShippingZip = (TextView)findViewById(R.id.txtShippingZip);

        ConfirmBtnListener = new OnClickListener(){
			@Override
			public void onClick(View v) {
			    processOrderViaPal(v);
			}

            private void processOrderViaPal(View v) {
                PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
                  Intent intent = new Intent(order.this, PaymentActivity.class);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
                startActivityForResult(intent, REQUEST_CODE_PAYMENT);

            }
            private PayPalPayment getThingToBuy(String paymentIntent) {
                return new PayPalPayment(new BigDecimal(totalamt), "USD", Item,
                        paymentIntent);
            }
        };
        ConfirmBtn.setOnClickListener(ConfirmBtnListener);

        ShippingCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String strBillingAddress = txtBillingAddress.getText().toString();
            String strBillingCity = txtBillingCity.getText().toString();
            String strBillingState = txtBillingState.getText().toString();
            String strBillingZip = txtBillingZip.getText().toString();
            if (strBillingAddress.equals("Billing Address")){strBillingAddress="";}
            if (strBillingCity.equals("Billing City")){strBillingCity="";}
            if (strBillingState.equals("State")){strBillingState="";}
            if (strBillingZip.equals("Billing Zip")){strBillingZip="";}

            if ((strBillingAddress!="") &&
            (strBillingCity!="") &&
                    (strBillingState!="") &&
                    (strBillingZip!="")) {
                Toast.makeText(order.this, "Setting Shipping Address to Billing Address", Toast.LENGTH_LONG).show();
                txtShippingAddress.setText(txtBillingAddress.getText());
                txtShippingCity.setText(txtBillingCity.getText());
                txtShippingState.setText(txtBillingState.getText());
                txtShippingZip.setText(txtBillingZip.getText());
            } else{
                Toast.makeText(order.this, "Please complete your Billing Address", Toast.LENGTH_LONG).show();
                ShippingCheckBox.setChecked(false);

            }


            }
        }
        );

    }

    private double CalculateTax(String itemPrice) {
        double  taxrate=.07;
        return Math.round(taxrate * Double.parseDouble(itemPrice)* 100.0) / 100.0;
    }

    public String formatDecimal(float number) {
        float epsilon = 0.004f; // 4 tenths of a cent
        if (Math.abs(Math.round(number) - number) < epsilon) {
            return String.format("%10.0f", number); // sdb
        } else {
            return String.format("%10.2f", number); // dj_segfault
        }
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
			return JSONhelper.getJSONfromURL(urls[0].toString());
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));
                        /**
                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         * or consent completion.
                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                         * for more details.
                         *
                         * For sample mobile backend interactions, see
                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                         */
                        Toast.makeText(
                                getApplicationContext(),
                                "Order Placed Sucessfully!!!", Toast.LENGTH_LONG)
                                .show();
                        Intent intent = new Intent(order.this, HealingPlan.class);
                        startActivity(intent);

                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        TAG,
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("FuturePaymentExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("FuturePaymentExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        Toast.makeText(
                                getApplicationContext(),
                                "Future Payment code received from PayPal", Toast.LENGTH_LONG)
                                .show();

                    } catch (JSONException e) {
                        Log.e("FuturePaymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("FuturePaymentExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("ProfileSharingExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("ProfileSharingExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        Toast.makeText(
                                getApplicationContext(),
                                "Profile Sharing code received from PayPal", Toast.LENGTH_LONG)
                                .show();

                    } catch (JSONException e) {
                        Log.e("ProfileSharingExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("ProfileSharingExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "ProfileSharingExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }
    private void sendAuthorizationToServer(PayPalAuthorization authorization) {

        /**
         * TODO: Send the authorization response to your server, where it can
         * exchange the authorization code for OAuth access and refresh tokens.
         *
         * Your server must then store these tokens, so that your server code
         * can execute payments for this user in the future.
         *
         * A more complete example that includes the required app-server to
         * PayPal-server integration is available from
         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
         */

    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
}
