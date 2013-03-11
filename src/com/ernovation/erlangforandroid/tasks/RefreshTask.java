package com.ernovation.erlangforandroid.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ernovation.erlangforandroid.ErlangDescriptor;
import com.ernovation.erlangforandroid.LibraryInfo;
import com.ernovation.erlangforandroid.Repository;
import com.ernovation.erlangforandroid.database.LibsDb;
import com.ernovation.erlangforandroid.network.JSONLoader;
import com.googlecode.android_scripting.AsyncTaskListener;
import com.googlecode.android_scripting.Log;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;

public class RefreshTask extends AsyncTask<Void, Integer, Boolean> {

	Cursor iCursor;
	int iCount;
	AsyncTaskListener<Boolean> iListener;
	LibsDb iDb;
	ProgressDialog iDialog;
	private Exception iException;
	private final ErlangDescriptor iDescriptor;
	
	public RefreshTask(Context aContext, LibsDb aDb,
			AsyncTaskListener<Boolean> aListener) {
		super();
		iListener = aListener;
		iDescriptor = new ErlangDescriptor();
		iCursor = aDb.repositoriesCursor();
		iCount = iCursor.getCount();
		iDb = aDb;
		if (aContext != null) {
			iDialog = new ProgressDialog(aContext);			
		} else {
			iDialog = null;
		}
	}

	@Override
	protected void onPreExecute() {
		Log.v("Refreshing libraries");
		if (iDialog != null) {
			iDialog.setTitle("Refreshing");
			iDialog.setMessage("Refreshing libraries");
			iDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			iDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					cancel(true);
				}
			});
			iDialog.show();
		}
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {

		if (!iCursor.moveToFirst()) {
			return false;
		}
		int i = 0;
		String url;
		String name;
		int id;

		do {
			id = iCursor.getInt(0);
			name = iCursor.getString(1);
			url = iCursor.getString(2);
			Repository repo = new Repository();
			repo.setId(id);
			repo.setName(name);
			repo.setURL(url);
			if (!url.endsWith("/")) {
				url += "/";
			}
			url+=iDescriptor.getInterpreterVersion()+"/";

			iDb.deleteAllFromRepository(id);
			try {
				JSONLoader loader = new JSONLoader();
				JSONObject jsonObject = loader.getJSONFromUrl(url
						+ "index.json");
				JSONArray libArray = jsonObject.getJSONArray("libs");
				int arrayLength = libArray.length();
				System.out.println("Got " + arrayLength + " libs");
				publishProgress(0, arrayLength);
				for (int j = 0; j < arrayLength; j++) {
					JSONObject libInfo = libArray.getJSONObject(j);
					String libName = libInfo.getString("name");
					String libVersion = libInfo.getString("version");
					publishProgress(j);
					System.out.println("Getting " + libName);
					JSONObject libDetails = loader.getJSONFromUrl(url
							+ libName + "-" + libVersion + ".json");
					LibraryInfo lib = LibraryInfo.fromJSON(libDetails);
					if (lib.getName() != null) {
						lib.setVersion(libVersion);
						lib.setRepository(repo);
						updateLib(lib);
					}
				}
			} catch (Exception e) {
				System.out.println("Could not download " + name + ":"
						+ e.getMessage());
			}
		} while (iCursor.moveToNext());
		return true;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		if (iDialog == null) {
			return;
		}
		if (progress.length > 1) {
			int contentLength = progress[1];
			if (contentLength == -1) {
				iDialog.setIndeterminate(true);
			} else {
				iDialog.setIndeterminate(false);
				iDialog.setMax(contentLength);
			}
		} else {
			System.out.println("Progress " + progress[0].intValue());
			iDialog.setProgress(progress[0].intValue());
		}
	}

	@Override
	public void onPostExecute(Boolean aResult) {
		if (iDialog != null && iDialog.isShowing()) {
			iDialog.dismiss();
		}
		if (isCancelled()) {
			return;
		}
		if (iException != null) {
			Log.e("Download failed.", iException);
		}
		iListener.onTaskFinished(aResult, "Refresh done.");
	}

	@Override
	protected void onCancelled() {
		if (iDialog != null) {
			iDialog.setTitle("Download cancelled.");
		}
	}

	private void updateLib(LibraryInfo aLib) {
		System.out.println("Should store lib " + aLib.getName() + " for repo "
				+ aLib.getRepository().getName());
		iDb.updateLib(aLib);
	}

}
