package se.hundbajskartan;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DogShitMap extends Activity
{
	// private static final int CAMERA_PIC_REQUEST=1337;
	//
	//
	// /** Called when the activity is first created. */
	// @Override
	// public void onCreate(Bundle savedInstanceState)
	// {
	// super.onCreate(savedInstanceState);
	// setContentView(R.layout.main);
	//
	// takePicture();
	//
	// Button takePicture=(Button)findViewById(R.id.btn_take_picture);
	// takePicture.setOnClickListener(new View.OnClickListener()
	// {
	// @Override
	// public void onClick(View v)
	// {
	// takePicture();
	// }
	// });
	// }
	//
	//
	// private void takePicture()
	// {
	// Intent cameraIntent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	// startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
	// }
	//
	//
	// protected void onActivityResult(int requestCode, int resultCode, Intent data)
	// {
	// if (requestCode==CAMERA_PIC_REQUEST)
	// {
	// // do something
	// Bitmap thumbnail=(Bitmap)data.getExtras().get("data");
	// ImageView image=(ImageView)findViewById(R.id.iv_photo_result);
	// image.setImageBitmap(thumbnail);
	// }
	private static final int CAMERA_PIC_REQUEST=1337;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button takePicture=(Button)findViewById(R.id.btn_take_picture);
		takePicture.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				try
				{
					takePicture();
					Toast.makeText(getApplicationContext(), "Camera's OK button not working yet!", Toast.LENGTH_LONG).show();
				}
				catch (Exception ex)
				{
					Logger.LogError(ex.toString());
				}
			}
		});

		Button getLocation=(Button)findViewById(R.id.btn_get_location);
		getLocation.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				try
				{
					Location location=getLocation();
					if (location!=null)
						sendEmail(location);
				}
				catch (Exception ex)
				{
					Logger.LogError(ex.toString());
				}
			}
		});
	}


	private void takePicture()
	{
		// Intent cameraIntent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		// startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
		Intent i=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		if (hasImageCaptureBug())
		{
			i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File("/sdcard/tmp")));
		}
		else
		{
			i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(i, CAMERA_PIC_REQUEST);
	}


	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		switch (requestCode)
		{
			// case GlobalConstants.IMAGE_CAPTURE:
			case CAMERA_PIC_REQUEST:
				Uri u;
				if (hasImageCaptureBug())
				{
					File fi=new File("/sdcard/tmp");
					try
					{
						u=Uri
								.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(), fi.getAbsolutePath(), null,
																							null));
						if (!fi.delete())
							Log.i(Globals.DSM_TAG, "Failed to delete "+fi);
					}
					catch (FileNotFoundException e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					u=intent.getData();
				}

				// Bitmap thumbnail=(Bitmap)intent.getExtras().get("data");
				// ImageView image=(ImageView)findViewById(R.id.iv_photo_result);
				// image.setImageBitmap(thumbnail);
		}
	}


	private void SavePicture()
	{
		// // Write the file to storage
		// java.util.UUID uid=java.util.UUID.randomUUID();
		// android.content.Context c=getContext();
		//
		// try
		// {
		// boolean mExternalStorageAvailable=false;
		// boolean mExternalStorageWriteable=false;
		// String state=Environment.getExternalStorageState();
		//
		// if (Environment.MEDIA_MOUNTED.equals(state))
		// {
		// // We can read and write the media
		// mExternalStorageAvailable=mExternalStorageWriteable=true;
		//
		// Log.i("P_PC_WRITE.FILE", "Begin write file");
		// File ff=new File(Environment.getExternalStorageDirectory(),
		// "test"+uid.toString()+".jpg");
		//
		// OutputStream os=new FileOutputStream(ff);
		//
		// os.write(_data);
		// os.close();
		// }
		// else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
		// {
		// // We can only read the media
		// mExternalStorageAvailable=true;
		// mExternalStorageWriteable=false;
		// }
		// else
		// {
		// // Something else is wrong. It may be one of many other states, but all we need
		// // to know is we can neither read nor write
		// mExternalStorageAvailable=mExternalStorageWriteable=false;
		// }
		// }
		// catch (Exception ex)
		// {
		// // TODO: Error handling
		// }
	}


	private Location getLocation()
	{
		Location location;

		LocationManager locManager=(LocationManager)getSystemService(LOCATION_SERVICE);
		Criteria criteria=new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider=locManager.getBestProvider(criteria, true);
		location=locManager.getLastKnownLocation(provider);

		return location;
	}


	private void sendEmail(Location location)
	{
		final Intent emailIntent=new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"m4gnus@gmail.com"});
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hundbajskartan - bajs hittat och geotaggat");
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "http://maps.google.com/?q="+location.getLatitude()+","+location.getLongitude());
		startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}


	// protected void onActivityResult(int requestCode, int resultCode, Intent data)
	// {
	// if (requestCode==CAMERA_PIC_REQUEST)
	// {
	// // do something
	// Bitmap thumbnail=(Bitmap)data.getExtras().get("data");
	// ImageView image=(ImageView)findViewById(R.id.iv_photo_result);
	// image.setImageBitmap(thumbnail);
	// }
	// }

	// }

	public boolean hasImageCaptureBug()
	{
		// list of known devices that have the bug
		ArrayList<String> devices=new ArrayList<String>();
		devices.add("android-devphone1/dream_devphone/dream");
		devices.add("generic/sdk/generic");
		devices.add("vodafone/vfpioneer/sapphire");
		devices.add("tmobile/kila/dream");
		devices.add("verizon/voles/sholes");
		devices.add("google_ion/google_ion/sapphire");

		return devices.contains(android.os.Build.BRAND+"/"+android.os.Build.PRODUCT+"/"+android.os.Build.DEVICE);
	}
}
