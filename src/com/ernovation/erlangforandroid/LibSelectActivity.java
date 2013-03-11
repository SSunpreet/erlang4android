package com.ernovation.erlangforandroid;

import java.util.List;

import com.ernovation.erlangforandroid.database.LibsDb;
import com.ernovation.erlangforandroid.tasks.RefreshTask;
import com.googlecode.android_scripting.AsyncTaskListener;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class LibSelectActivity extends FragmentActivity implements
		AsyncTaskListener<Boolean> {

	private LibsDb iDb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lib_select);
		ListView listView = (ListView) findViewById(R.id.libsListView);
		iDb = new LibsDb(this);
		registerForContextMenu(listView);

		refreshCursor();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_lib_select, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.refreshMenuItem:
			refresh();
			return true;
		case R.id.goMenuItem:
			go();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// super.onCreateContextMenu(menu, v, menuInfo);
		System.out.println("Creating context menu for "
				+ v.getClass().getName());
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.library_popup_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		System.out.println("Selected an item");
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		LibraryInfo libInfo;
		switch (item.getItemId()) {
		case R.id.install_library_item:
			System.out.println("Should install " + info.position + "/"
					+ info.id);
			libInfo = iDb.getLibraryInfoById(info.id);
			if (libInfo == null) {
				return true;
			}
			if (libInfo.hasState(LibraryInfo.TO_REMOVE)) {
				libInfo.resetState(LibraryInfo.TO_REMOVE);
				iDb.updateLib(libInfo);
				refreshCursor();

			} else if (libInfo.isInstalled()) {
				Toast toast = Toast.makeText(getApplicationContext(),
						R.string.already_installed, Toast.LENGTH_LONG);
				toast.show();
			} else if (libInfo.hasState(LibraryInfo.TO_INSTALL)) {
				Toast toast = Toast.makeText(getApplicationContext(),
						R.string.already_installing, Toast.LENGTH_LONG);
				toast.show();
			} else {
				installLibrary(libInfo);
			}
			return true;
		case R.id.uninstall_library_item:
			System.out.println("Should uninstall " + info.position + "/"
					+ info.id);
			libInfo = iDb.getLibraryInfoById(info.id);
			if (libInfo == null) {
				return true;
			}
			if (libInfo.hasState(LibraryInfo.TO_INSTALL)) {
				libInfo.resetState(LibraryInfo.TO_INSTALL);
				iDb.updateLib(libInfo);
				refreshCursor();

			} else if (!libInfo.isInstalled()) {
				Toast toast = Toast.makeText(this, R.string.not_yet_installed,
						Toast.LENGTH_LONG);
				toast.show();
			} else if (libInfo.hasState(LibraryInfo.TO_REMOVE)) {
				Toast toast = Toast.makeText(getApplicationContext(),
						R.string.already_uninstalling, Toast.LENGTH_LONG);
				toast.show();
			} else {
				uninstallLibrary(libInfo);
			}
			return true;
		case R.id.details_library_item:
			libInfo = iDb.getLibraryInfoById(info.id);
			if (libInfo == null) {
				return true;
			}
			DialogFragment detailsDialog = new DetailsDialog(libInfo);
			detailsDialog.show(getFragmentManager(), "details");
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void refreshCursor() {
		ListView listView = (ListView) findViewById(R.id.libsListView);
		Cursor rowCursor = iDb.rowsCursor();
		LibraryRowAdapter rowAdapter = new LibraryRowAdapter(this, rowCursor,
				iDb, 0);
		listView.setAdapter(rowAdapter);

	}

	private void refresh() {
		RefreshTask task = new RefreshTask(this, iDb, this);
		task.execute();
	}

	private void installLibrary(LibraryInfo aLibInfo) {
		System.out.println("Should install " + aLibInfo.getName());
		aLibInfo.setState(LibraryInfo.TO_INSTALL);
		iDb.updateLib(aLibInfo);
		refreshCursor();
	}

	private void installSingleLibrary(LibraryInfo aLibInfo) throws Exception {
		/*
		 * LibraryInstaller installer = new LibraryInstaller(new LibraryInfo[]
		 * {aLibInfo}, this, this); installer.execute();
		 */
		refreshCursor();

	}

	private void uninstallLibrary(LibraryInfo aLibInfo) {
		System.out.println("Should uninstall " + aLibInfo.getName());
		aLibInfo.setState(LibraryInfo.TO_REMOVE);
		iDb.updateLib(aLibInfo);
		try {
			uninstallSingleLibrary(aLibInfo);
		} catch (Exception e) {
			System.out.println("Error uninstalling " + aLibInfo.getName()
					+ ": " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void uninstallSingleLibrary(LibraryInfo aLibInfo) throws Exception {
		/*
		 * LibraryUninstaller uninstaller = new LibraryUninstaller(new
		 * LibraryInfo[] {aLibInfo}, this, this); uninstaller.execute();
		 */
		refreshCursor();
	}

	@Override
	public void onTaskFinished(Boolean arg0, String arg1) {
		System.out.println("Task done");
		Toast toast = Toast.makeText(this, arg1, Toast.LENGTH_LONG);
		toast.show();
		refreshCursor();

	}

	private void go() {
		System.out.println("Should now install/uninstall");
		List<LibraryInfo> toInstall = iDb
				.getLibraryInfoByState(LibraryInfo.TO_INSTALL);
		List<LibraryInfo> toUninstall = iDb
				.getLibraryInfoByState(LibraryInfo.TO_REMOVE);
		try {
			if (!toInstall.isEmpty()) {
				LibraryInstaller installer = new LibraryInstaller(iDb,
						toInstall, this, this);
				installer.execute();
			}
			if (!toUninstall.isEmpty()) {
				LibraryUninstaller uninstaller = new LibraryUninstaller(iDb,
						toUninstall, this, this);
				uninstaller.execute();
			}
		} catch (Exception e) {
			System.out.println("Error (un)installing: " + e.getMessage());
			e.printStackTrace();
		}

	}
}
