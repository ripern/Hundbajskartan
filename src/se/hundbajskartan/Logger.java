package se.hundbajskartan;

import android.util.Log;

public class Logger
{
	private static boolean _loggingEnabled=true;


	public static void setLoggingEnabled(boolean loggingEnabled)
	{
		_loggingEnabled=loggingEnabled;
	}


	public static boolean isLoggingEnabled()
	{
		return _loggingEnabled;
	}


	public static void LogInfo(String message)
	{
		Logger.LogInfo(Globals.DSM_TAG, message);
	}


	public static void LogInfo(String tag, String message)
	{
		if (isLoggingEnabled())
			Log.i(tag, message);
	}


	public static void LogDebug(String message)
	{
		Logger.LogDebug(Globals.DSM_TAG, message);
	}


	public static void LogDebug(String tag, String message)
	{
		if (isLoggingEnabled())
			Log.d(tag, message);
	}


	public static void LogError(String message)
	{
		Logger.LogError(Globals.DSM_TAG, message);
	}


	public static void LogError(String tag, String message)
	{
		if (isLoggingEnabled())
			Log.e(tag, message);
	}


	public static void LogWarning(String message)
	{
		Logger.LogWarning(Globals.DSM_TAG, message);
	}


	public static void LogWarning(String tag, String message)
	{
		if (isLoggingEnabled())
			Log.w(tag, message);
	}
}
