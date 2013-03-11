package com.ernovation.erlangforandroid;

import java.io.File;
import java.net.MalformedURLException;

import android.content.Context;
import android.os.AsyncTask;

import com.ernovation.erlangforandroid.database.LibsDb;
import com.googlecode.android_scripting.AsyncTaskListener;
import com.googlecode.android_scripting.FileUtils;
import com.googlecode.android_scripting.exception.Sl4aException;
import com.googlecode.android_scripting.interpreter.InterpreterUtils;

public class LibraryUninstaller extends AsyncTask<Void, Void, Boolean> {

	protected final AsyncTaskListener<Boolean> mTaskListener;
	protected final Context mContext;

	protected volatile AsyncTask<Void, Integer, Long> mTaskHolder;

	protected final ErlangDescriptor mDescriptor;

	private final Iterable<LibraryInfo> iLibInfo;
	private final LibsDb iDb;
	
	public LibraryUninstaller(LibsDb aDb, Iterable<LibraryInfo> aLibInfo, Context context,
			AsyncTaskListener<Boolean> taskListener) throws Sl4aException,
			MalformedURLException {
		super();
		iDb = aDb;
		mDescriptor = new ErlangDescriptor();

		iLibInfo = aLibInfo;
		mContext = context;
		mTaskListener = taskListener;

	}

	@Override
	protected Boolean doInBackground(Void... params) {
		String baseDir = InterpreterUtils.getInterpreterRoot(mContext)
				.getAbsolutePath() + "/erlang/lib/";
		for (LibraryInfo libInfo : iLibInfo) {
			String name = libInfo.getName();
			String version = libInfo.getVersion();
			String dirName = name + "-" + version;

			String fullDirName = baseDir + dirName;
			File dir = new File(fullDirName);
			if (dir.exists()) {
				FileUtils.delete(dir);
			}
			libInfo.clearState();
			iDb.updateLib(libInfo);
		}
		return true;
	}

	@Override
	public void onPostExecute(Boolean aResult) {
		if (aResult) {
			mTaskListener.onTaskFinished(true, "Uninstallation successful.");
		} else {
			mTaskListener.onTaskFinished(false, "Uninstallation failed.");
		}
	}

}
