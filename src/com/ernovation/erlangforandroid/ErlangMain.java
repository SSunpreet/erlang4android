package com.ernovation.erlangforandroid;

import com.googlecode.android_scripting.AsyncTaskListener;
import com.googlecode.android_scripting.InterpreterInstaller;
import com.googlecode.android_scripting.InterpreterUninstaller;
import com.googlecode.android_scripting.activity.Main;
import com.googlecode.android_scripting.exception.Sl4aException;
import com.googlecode.android_scripting.interpreter.InterpreterDescriptor;

import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ErlangMain extends Main {

	private Button iSelectLibsButton;
	protected final String iId = getClass().getPackage().getName();
	public static Context CONTEXT;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CONTEXT = this.getApplicationContext();
	}

	@Override
	protected void initializeViews() {
		setContentView(R.layout.activity_main);
		mButton = (Button) findViewById(R.id.installButton);
		iSelectLibsButton = (Button) findViewById(R.id.selectLibsButton);
		iSelectLibsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ErlangMain.this,
						LibSelectActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem aItem) {
		switch (aItem.getItemId()) {
		case R.id.menu_repositories:
			Intent intent = new Intent(ErlangMain.this,
					RepoManageActivity.class);
			startActivity(intent);
			return true;
		case R.id.menu_settings:
			getSettings();
			return true;
		default:
			return super.onOptionsItemSelected(aItem);
		}
	}

	@Override
	protected void prepareInstallButton() {
		super.prepareInstallButton();
		iSelectLibsButton.setEnabled(false);
	}

	@Override
	protected void prepareUninstallButton() {
		super.prepareUninstallButton();
		iSelectLibsButton.setEnabled(true);
	}

	public void selectLibraries(View aView) {
		System.out.println("Select libraries");
	}

	@Override
	protected InterpreterDescriptor getDescriptor() {
		return new ErlangDescriptor();
	}

	@Override
	protected InterpreterInstaller getInterpreterInstaller(
			InterpreterDescriptor descriptor, Context context,
			AsyncTaskListener<Boolean> listener) throws Sl4aException {
		return new ErlangInstaller(descriptor, context, listener);
	}

	@Override
	protected InterpreterUninstaller getInterpreterUninstaller(
			InterpreterDescriptor descriptor, Context context,
			AsyncTaskListener<Boolean> listener) throws Sl4aException {
		return new ErlangUninstaller(descriptor, context, listener);
	}

	private void getSettings() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		final ErlangDescriptor desc = (ErlangDescriptor)getDescriptor();
		
		alert.setTitle("Settings");
		alert.setMessage("Command line arguments:");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		input.setText(desc.getExtraArgs(getApplicationContext()));
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  String value = input.getText().toString();
		  desc.setExtraArgs(getApplicationContext(), value);
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
	}
}
