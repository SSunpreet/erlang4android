package com.ernovation.erlangforandroid;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;

import com.ernovation.erlangforandroid.database.LibsDb;
import com.googlecode.android_scripting.AsyncTaskListener;
import com.googlecode.android_scripting.FileUtils;
import com.googlecode.android_scripting.Log;
import com.googlecode.android_scripting.UrlDownloaderTask;
import com.googlecode.android_scripting.ZipExtractorTask;
import com.googlecode.android_scripting.exception.Sl4aException;
import com.googlecode.android_scripting.interpreter.InterpreterConstants;
import com.googlecode.android_scripting.interpreter.InterpreterUtils;

public class LibraryInstaller extends AsyncTask<Void, Void, Boolean> {
	protected final AsyncTaskListener<Boolean> mTaskListener;
	protected final Queue<InstallTask> mTaskQueue;
	protected final Context mContext;

	protected final Handler mainThreadHandler;
	protected Handler mBackgroundHandler;

	protected volatile AsyncTask<Void, Integer, Long> mTaskHolder;

	protected final ErlangDescriptor mDescriptor;

	protected final String mInterpreterRoot;

	protected static enum RequestCode {
		DOWNLOAD_LIBRARY, EXTRACT_LIBRARY
	}

	private class InstallTask {
		private RequestCode iCode;
		private LibraryInfo iInfo;

		public InstallTask(RequestCode aCode, LibraryInfo aInfo) {
			iCode = aCode;
			iInfo = aInfo;
		}

		public RequestCode getRequestCode() {
			return iCode;
		}

		public LibraryInfo getLibraryInfo() {
			return iInfo;
		}
	}

	private final LibsDb iDb;
	
	// Executed in the UI thread.
	private final Runnable mTaskStarter = new Runnable() {
		@Override
		public void run() {
			InstallTask task = mTaskQueue.peek();
			try {
				AsyncTask<Void, Integer, Long> newTask = null;
				switch (task.getRequestCode()) {
				case DOWNLOAD_LIBRARY:
					newTask = downloadLibrary(task.getLibraryInfo());
					break;
				case EXTRACT_LIBRARY:
					newTask = extractLibrary(task.getLibraryInfo());
					break;
				}
				mTaskHolder = newTask.execute();
			} catch (Exception e) {
				Log.v(e.getMessage(), e);
			}

			if (mBackgroundHandler != null) {
				mBackgroundHandler.post(mTaskWorker);
			}
		}
	};

	// Executed in the background.
	private final Runnable mTaskWorker = new Runnable() {
		@Override
		public void run() {
			InstallTask request = mTaskQueue.peek();
			try {
				if (mTaskHolder != null && mTaskHolder.get() != null) {
					mTaskQueue.remove();
					mTaskHolder = null;
					// Post processing.
					if (request.getRequestCode() == RequestCode.EXTRACT_LIBRARY) {
						LibraryInfo info = request.getLibraryInfo();
						info.setState(LibraryInfo.INSTALLED);
						info.resetState(LibraryInfo.TO_INSTALL);
						iDb.updateLib(info);
					}
					if (request.getRequestCode() == RequestCode.EXTRACT_LIBRARY
							&& !chmodLibrary(request.getLibraryInfo())) {
						// Chmod returned false.
						Looper.myLooper().quit();
					} else if (mTaskQueue.size() == 0) {
						// We're done here.
						Looper.myLooper().quit();
						return;
					} else if (mainThreadHandler != null) {
						// There's still some work to do.
						mainThreadHandler.post(mTaskStarter);
						return;
					}
				}
			} catch (Exception e) {
				Log.e(e);
			}
			// Something went wrong...
			switch (request.getRequestCode()) {
			case DOWNLOAD_LIBRARY:
				Log.e("Downloading interpreter failed.");
				break;
			case EXTRACT_LIBRARY:
				Log.e("Extracting interpreter failed.");
				break;
			}
			Looper.myLooper().quit();
		}
	};

	// TODO(Alexey): Add Javadoc.
	public LibraryInstaller(LibsDb aDb, Iterable<LibraryInfo> aLibInfo, Context context,
			AsyncTaskListener<Boolean> taskListener) throws Sl4aException,
			MalformedURLException {
		super();
		iDb = aDb;
		mDescriptor = new ErlangDescriptor();

		mContext = context;
		mTaskListener = taskListener;
		mainThreadHandler = new Handler();
		mTaskQueue = new LinkedList<InstallTask>();

		String packageName = getClass().getPackage().getName();

		if (packageName.length() == 0) {
			throw new Sl4aException("Interpreter package name is empty.");
		}

		mInterpreterRoot = InterpreterConstants.SDCARD_ROOT + packageName;

		File f = new File(mInterpreterRoot);
		if (!f.exists()) {
			f.mkdirs();
		}
		for (LibraryInfo libInfo : aLibInfo) {
			mTaskQueue.offer(new InstallTask(RequestCode.DOWNLOAD_LIBRARY,
					libInfo));
		}
		for (LibraryInfo libInfo : aLibInfo) {
			mTaskQueue.offer(new InstallTask(RequestCode.EXTRACT_LIBRARY,
					libInfo));
		}

	}

	@Override
	protected Boolean doInBackground(Void... params) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				executeInBackground();
				final boolean result = (mTaskQueue.size() == 0);
				mainThreadHandler.post(new Runnable() {
					@Override
					public void run() {
						finish(result);
					}
				});
			}
		}).start();
		return true;
	}

	private boolean executeInBackground() {

		if (Looper.myLooper() == null) {
			Looper.prepare();
		}
		mBackgroundHandler = new Handler(Looper.myLooper());
		mainThreadHandler.post(mTaskStarter);
		Looper.loop();
		// Have we executed all the tasks?
		return (mTaskQueue.size() == 0);
	}

	protected void finish(boolean result) {
		if (result) {
			mTaskListener.onTaskFinished(true, "Installation successful.");
		} else {
			if (mTaskHolder != null) {
				mTaskHolder.cancel(true);
			}
			cleanup();
			mTaskListener.onTaskFinished(false, "Installation failed.");
		}
	}

	protected AsyncTask<Void, Integer, Long> download(String in)
			throws MalformedURLException {
		String out = mInterpreterRoot;
		System.out.println("Downloading to " + out);
		return new UrlDownloaderTask(in, out, mContext);
	}

	protected AsyncTask<Void, Integer, Long> downloadLibrary(LibraryInfo aInfo)
			throws MalformedURLException {
		String url = aInfo.getRepository().getURL();
		if (!url.endsWith("/")) {
			url += "/";
		}
		url+=mDescriptor.getInterpreterVersion()+"/";
		String archive = aInfo.getName() + "-" + aInfo.getVersion() + ".zip";

		return download(url + archive);
	}

	protected AsyncTask<Void, Integer, Long> extract(LibraryInfo aInfo, String in, String out,
			boolean replaceAll) throws Sl4aException {
		return new ZipExtractorTask(in, out, mContext, replaceAll);
	}

	protected AsyncTask<Void, Integer, Long> extractLibrary(LibraryInfo aInfo)
			throws Sl4aException {
		String archive = aInfo.getName() + "-" + aInfo.getVersion() + ".zip";
		String in = new File(mInterpreterRoot, archive).getAbsolutePath();
		String out = InterpreterUtils.getInterpreterRoot(mContext)
				.getAbsolutePath() + "/erlang/lib";
		return extract(aInfo, in, out, true);
	}

	protected boolean chmodLibrary(LibraryInfo aInfo) {
		boolean interpreterChmodSuccess;
		String out = InterpreterUtils.getInterpreterRoot(mContext)
				.getAbsolutePath() + "/erlang/lib";
		try {
			interpreterChmodSuccess = FileUtils.recursiveChmod(new File(out),
					0755);
		} catch (Exception e) {
			Log.e(e);
			return false;
		}
		return interpreterChmodSuccess;
	}

	protected boolean isInstalled() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return preferences.getBoolean(
				InterpreterConstants.INSTALLED_PREFERENCE_KEY, false);
	}

	private void cleanup() {
		List<File> directories = new ArrayList<File>();

		directories.add(new File(mInterpreterRoot));

		for (File directory : directories) {
			FileUtils.delete(directory);
		}
	}

}
