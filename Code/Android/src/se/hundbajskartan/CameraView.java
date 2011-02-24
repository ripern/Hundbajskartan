package se.hundbajskartan;

import java.io.IOException;

import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;

public class CameraView extends SurfaceView implements Callback, AutoFocusCallback
{
	private int mAutoFocusMessage;

	private Camera mCamera;
	private SurfaceHolder mHolder;
	private Handler mHandler;

	private boolean mPreviewRunning;

	private final Context mContext;


	public CameraView(final Context context, AttributeSet attr)
	{
		super(context, attr);
		mContext=context;
		// Logger.info("CameraView.CameraView()");
		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder=getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}


	public void surfaceCreated(final SurfaceHolder holder)
	{
		// Logger.LogInfo("CameraView.surfaceCreated()");
		// The Surface has been created, acquire the camera and tell it where
		// to draw.
		mCamera=Camera.open();
		try
		{
			mCamera.setPreviewDisplay(holder);
		}
		catch (final IOException e)
		{
			// Logger.error("CameraView.surfaceCreated()", e);
		}
	}


	public void surfaceDestroyed(final SurfaceHolder holder)
	{
		// Logger.info("CameraView.surfaceDestroyed()");
		// Surface will be destroyed when we return, so stop the preview.
		// Because the CameraDevice object is not a shared resource, it's very
		// important to release it when the activity is paused.
		mCamera.stopPreview();
		mPreviewRunning=false;
		mCamera.release();
		mCamera=null;
	}


	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
	{
		// Logger.info("CameraView.surfaceChanged() w: "+w+" h: "+h);
		// Stop old preview to prevent possible crashes.
		if (mPreviewRunning)
		{
			mCamera.stopPreview();
		}

		final Camera.Parameters p=mCamera.getParameters();

		// w and h can be inverted after the screen has been off even when the
		// activity is locked in landscape mode. Same fix in
		// OverLiew.onSizeChanged()
		WindowManager manager=(WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
		Display display=manager.getDefaultDisplay();
		int dispHeight=display.getHeight();
		int dispWidth=display.getWidth();
		// Logger.info("CameraView.surfaceChanged() display width: "+dispWidth+" height: "+dispHeight);
		int targetHeight=dispHeight;
		int targetWidth=dispWidth;
		if (dispHeight>dispWidth)
		{
			targetHeight=dispWidth;
			targetWidth=dispHeight;
		}

		p.setPreviewSize(targetWidth, targetHeight);
		try
		{
			mCamera.setParameters(p);
		}
		catch (RuntimeException e)
		{
			// Logger.error("CameraView.surfaceChanged() setParameters failed.", e);
		}
		mCamera.startPreview();
		mPreviewRunning=true;

		// if (DemoPrefs.useAutoFocus(getContext()))
		// {
		// autoFocus();
		// }
	}


	@Override
	public void onDraw(final Canvas canvas)
	{
		// Logger.info("CameraView.onDraw()");
	}


	public void setHandler(Handler handler, int autoFocusMessage)
	{
		mHandler=handler;
		mAutoFocusMessage=autoFocusMessage;
	}


	@Override
	public void onAutoFocus(boolean success, Camera camera)
	{
		if (mHandler!=null)
		{
			// Logger.debug("CameraView.AutoFocusCallback.onAutoFocus()");
			mHandler.sendEmptyMessageDelayed(mAutoFocusMessage, 1500);
		}
	}


	public void autoFocus()
	{
		if (mCamera!=null)
		{
			mCamera.autoFocus(this);
		}
	}
}
