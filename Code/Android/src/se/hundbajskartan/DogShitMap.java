package se.hundbajskartan;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import se.hundbajskartan.classlibrary.DogShit;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.gson.Gson;

public class DogShitMap extends MapActivity {
	private final String TAG = "DOGSHITMAP";
	private final String URL = "http://hundbajsar.appspot.com/dogshitmapserver";
	// private final String URL = "http://74.125.77.141/dogshitmapserver";

	private MapView mMapView;
	private TextView mTextViewResult;
	private TextView mTextViewGPSStatus;
	private LocationManager mLocationManager;
	private LocationListener mLocationListener;
	private MapController mMapController;
	private List<Overlay> mapOverlays;
	private HelloItemizedOverlay itemizedoverlay;
	private Button mButtonGetLocation;
	private ProgressDialog mDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mButtonGetLocation = (Button) findViewById(R.id.btn_get_location);
		mTextViewResult = (TextView) findViewById(R.id.tv_result_value);
		mTextViewGPSStatus = (TextView) findViewById(R.id.tv_gps_status);
		mMapView = (MapView) findViewById(R.id.map_view);
		mMapController = mMapView.getController();
		mMapController.setZoom(11);
		setMapCenter(18.04, 59.35);

		mDialog = new ProgressDialog(this);
		mDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				mButtonGetLocation.setEnabled(true);
				mLocationManager.removeUpdates(mLocationListener);
			}
		});
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		mLocationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				// Called when a new location is found.
				new PostDogShitTask().execute(location);
				mLocationManager.removeUpdates(mLocationListener);
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				switch (status) {
				case LocationProvider.AVAILABLE:
					mTextViewGPSStatus.setText(mTextViewGPSStatus.getText().toString()
					    + "GPS available again\n");
					break;
				case LocationProvider.OUT_OF_SERVICE:
					mTextViewGPSStatus.setText(mTextViewGPSStatus.getText().toString()
					    + "GPS out of service\n");
					break;
				case LocationProvider.TEMPORARILY_UNAVAILABLE:
					mTextViewGPSStatus.setText(mTextViewGPSStatus.getText().toString()
					    + "GPS temporarily unavailable\n");
					break;
				}
			}

			@Override
			public void onProviderEnabled(String provider) {
				Log.i(TAG, "Provider enabled");
			}

			@Override
			public void onProviderDisabled(String provider) {
				Log.i(TAG, "Provider disabled");
			}
		};

		mapOverlays = mMapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.poop);
		itemizedoverlay = new HelloItemizedOverlay(drawable);
	}

	private void setMapCenter(Double longitude, Double latitude) {
		mMapController.setCenter(new GeoPoint((int) (latitude * 1000000),
		    (int) (longitude * 1000000)));
	}

	protected void postLocation(Location location) {
		DogShit dogshit = new DogShit(location.getLongitude(), location
		    .getLatitude(), new Date());
		sendJson(dogshit);
	}

	public void getLocationClick(View view) {
		try {
			// MOCK GPS
			// ProgressDialog dialog = ProgressDialog.show(DogShitMap.this, "",
			// "Getting position. Please wait...", true);
			// DogShit dogshit = createFakeDogShit();
			// if (dogshit != null)
			// {
			// mTextViewResult.setText(dogshit.getLongitude() + " "
			// + dogshit.getLatitude());
			// sendJson(dogshit);
			// Show position on map
			// GeoPoint point = new GeoPoint(dogshit.getLatitude()*1000000,
			// Integer.parseInt((dogshit.getLongitude()*1000000.toString()
			// }

			// ORIGINAL CODE, will be used when real GPS data is required
			// Register the listener with the Location Manager to receive location
			// updates
			mDialog.setMessage(getString(R.string.waiting_for_location));
			mDialog.show();
			mButtonGetLocation.setEnabled(false);
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
			    0, mLocationListener);
		} catch (Exception ex) {
			Logger.LogError(ex.toString());
		}
	}

	private class PostDogShitTask extends AsyncTask<Location, Void, Void> {
		private Location location;

		@Override
		protected void onPreExecute() {
			mDialog.setMessage(getString(R.string.uploading_shit));
			mDialog.setCancelable(false);
		}

		@Override
		protected Void doInBackground(Location... params) {
			location = params[0];
			if (location != null) {
				postLocation(location);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {
			if (location != null) {
				double longitude = location.getLongitude();
				double latitude = location.getLatitude();
				setMapCenter(longitude, latitude);
				mMapController.setZoom(19);
				GeoPoint point = new GeoPoint((int) (latitude * 1E6),
				    (int) (longitude * 1E6));
				OverlayItem overlayitem = new OverlayItem(point, "Bajs!",
				    "H�r vare bajs!");

				itemizedoverlay.addOverlay(overlayitem);
				mapOverlays.add(itemizedoverlay);
				mMapView.invalidate();
				mTextViewResult.setText(longitude + " " + latitude);
			} else {
				mTextViewResult.setText("No location available.");
			}

			mButtonGetLocation.setEnabled(true);
			mDialog.dismiss();
			mDialog.setCancelable(true);
		}

	}

	private DogShit createFakeDogShit() {
		DogShit dogshit = null;
		Random random = new Random();
		final int latDecimals = random.nextInt(99);
		final int lngDecimals = random.nextInt(99);

		dogshit = new DogShit(18 + (lngDecimals / 100d), 59 + (latDecimals / 100d),
		    new Date());

		return dogshit;
	}

	private void sendEmail(Location location) {
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {
		    "m4gnus@gmail.com", "robert.b.johansson@gmail.com" });
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
		    "Hundbajskartan - bajs hittat och geotaggat");
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
		    "http://maps.google.com/?q=" + location.getLatitude() + ","
		        + location.getLongitude());
		startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}

	protected void sendJson(final DogShit dogshit) {
		Thread t = new Thread() {
			@Override
			public void run() {
				Looper.prepare(); // For Preparing Message Pool for the child Thread
				HttpClient client = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
				// Limit
				HttpResponse response;
				JSONObject json = new JSONObject();
				try {
					HttpPost post = new HttpPost(URL);
					json.put("longitude", dogshit.getLongitude());
					json.put("latitude", dogshit.getLatitude());
					json.put("date", dogshit.getDate());
					Log.e(TAG, dogshit.getDate().toString());
					StringEntity se = new StringEntity(json.toString());
					// se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					// "application/json"));
					post.setEntity(se);
					response = client.execute(post);
					/* Checking response */
					if (response != null) {
						Log.d(TAG, response.getStatusLine().getStatusCode() + "  "
						    + response.getStatusLine().getReasonPhrase() + "");
						// Get the data in the entity
						InputStream in = response.getEntity().getContent();
					}
				} catch (Exception e) {
					Log.d(TAG, e.getStackTrace().toString());
					// Toast.makeText(getContext(),
					// e.getStackTrace().toString(), 20000);
					// createDialog("Error", "Cannot Establish Connection");
				}
				Looper.loop(); // Loop in the message queue
			}
		};
		t.start();
	}

	protected void sendJson(final String position) {
		Thread t = new Thread() {
			@Override
			public void run() {
				Looper.prepare(); // For Preparing Message Pool for the child
				// Thread
				HttpClient client = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
				// Limit
				HttpResponse response;

				try {
					HttpPost post = new HttpPost(URL);
					StringEntity se = new StringEntity("JSON: " + position);
					se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					    "application/json"));
					post.setEntity(se);
					response = client.execute(post);
					/* Checking response */
					if (response != null) {
						// Get the data in the entity
						InputStream in = response.getEntity().getContent();
					}
				} catch (Exception e) {
					Log.d(TAG, e.getStackTrace().toString());
					// createDialog("Error", "Cannot Establish Connection");
				}
				Looper.loop(); // Loop in the message queue
			}
		};
		t.start();
	}

	protected void sendJsonWithGson(DogShit dogshit) {
		// DogShit dogshit = new DogShit(location.getLongitude(),
		// location.getLongitude(), new Date());
		Gson gson = new Gson();

		// Convert java object to JSON format
		String json = gson.toJson(dogshit);

		sendJson(json);
	}

	protected void sendJsonWithGson(Location location) {
		DogShit dogshit = new DogShit(location.getLongitude(), location
		    .getLongitude(), new Date());
		Gson gson = new Gson();

		// Convert java object to JSON format
		String json = gson.toJson(dogshit);

		sendJson(json);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}