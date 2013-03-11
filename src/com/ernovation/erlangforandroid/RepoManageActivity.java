package com.ernovation.erlangforandroid;

import com.ernovation.erlangforandroid.database.LibsDb;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class RepoManageActivity extends ListActivity {

	private LibsDb iDb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		iDb = new LibsDb(this);
		registerForContextMenu(getListView());
		refreshCursor();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_repo_manage, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_add_repo:
			promptNewRepo();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		System.out.println("Selected an item");
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		Repository repo;
		switch (item.getItemId()) {
		case R.id.delete_repo_item:
			System.out.println("Should install " + info.position + "/"
					+ info.id);
			repo = iDb.getRepositoryById(info.id);
			if (repo == null) {
				return true;
			}
			promptDeleteRepo(repo);
			return true;
		case R.id.edit_repo_item:
			repo = iDb.getRepositoryById(info.id);
			if (repo == null) {
				return true;
			}
			promptEditRepo(repo);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.repo_popup_menu, menu);
	}

	private void promptDeleteRepo(Repository aRepo) {
		System.out.println("Should remove repo "+aRepo.getName());
		
	}
	
	private void promptNewRepo() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		LayoutInflater factory = LayoutInflater.from(this);
		final View alertView = factory.inflate(R.layout.repo_details, null);
		alert.setView(alertView);
		alert.setTitle(R.string.add_repo);
		alert.setMessage(R.string.enter_repo_details);
		final EditText nameText = (EditText)alertView.findViewById(R.id.nameEditText);
		final EditText urlText = (EditText)alertView.findViewById(R.id.urlEditText);
		alert.setPositiveButton("Add", 
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						System.out.println("Should add "+nameText.getText().toString()+" @ "+urlText.getText().toString());
						Repository repo = new Repository();
						repo.setName(nameText.getText().toString());
						repo.setURL(urlText.getText().toString());
						iDb.insertRepository(repo);
						refreshCursor();
					}
			
		});
		
		alert.setNegativeButton("Cancel", 
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						System.out.println("Cancelled");
						dialog.cancel();
					}
			
		});
		
		alert.show();
	}
	
	private void promptEditRepo(final Repository aRepo) {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		LayoutInflater factory = LayoutInflater.from(this);
		final View alertView = factory.inflate(R.layout.repo_details, null);
		alert.setView(alertView);
		alert.setTitle(R.string.add_repo);
		alert.setMessage(R.string.enter_repo_details);
		final EditText nameText = (EditText)alertView.findViewById(R.id.nameEditText);
		final EditText urlText = (EditText)alertView.findViewById(R.id.urlEditText);
		nameText.setText(aRepo.getName());
		urlText.setText(aRepo.getURL());
		alert.setPositiveButton("Edit", 
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						System.out.println("Should edit "+nameText.getText().toString()+" @ "+urlText.getText().toString());
						aRepo.setName(nameText.getText().toString());
						aRepo.setURL(urlText.getText().toString());
						iDb.updateRepository(aRepo);
						refreshCursor();
					}
			
		});
		
		alert.setNegativeButton("Cancel", 
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						System.out.println("Cancelled");
						dialog.cancel();
					}
			
		});
		
		alert.show();
	}
	private void refreshCursor() {
		ListView listView = getListView();
		Cursor rowCursor = iDb.repositoriesCursor();
		SimpleCursorAdapter rowAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, rowCursor,
				new String[] {"name"}, new int[] {android.R.id.text1}, 0);
		listView.setAdapter(rowAdapter);

	}

}
