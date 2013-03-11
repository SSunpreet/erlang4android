package com.ernovation.erlangforandroid;


import com.ernovation.erlangforandroid.database.LibsDb;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LibraryRowAdapter extends CursorAdapter {

	private LayoutInflater iInflater;
	private LibsDb iDb;

	public LibraryRowAdapter(Context context, Cursor c, LibsDb aDb, int flags) {
		super(context, c, flags);
		iDb = aDb;
		iInflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		View v = iInflater.inflate(R.layout.library_row_layout, parent, false);

		return v;
	}

	@Override
	public void bindView(View v, Context context, Cursor c) {
		LibraryInfo libInfo = iDb.getLibraryInfoById(c.getLong(0));

		String name = libInfo.getName();
		/**
		 * Next set the name of the entry.
		 */
		TextView name_text = (TextView) v.findViewById(R.id.nameView);
		if (name_text != null) {
			name_text.setText(name);
		}
		String version = libInfo.getVersion();
		TextView versionView = (TextView) v.findViewById(R.id.versionView);
		if (versionView != null) {
			versionView.setText(version);
		}
	
		ImageView stateView = (ImageView) v.findViewById(R.id.stateImageView);
		
		if ((libInfo.getState() & LibraryInfo.TO_INSTALL)!=0) {
			stateView.setVisibility(ImageView.VISIBLE);
			stateView.setImageResource(R.drawable.green_circle);
		} else if ((libInfo.getState() & LibraryInfo.TO_REMOVE)!=0) {
			stateView.setVisibility(ImageView.VISIBLE);
			stateView.setImageResource(R.drawable.red_circle);
		} else {
			stateView.setVisibility(ImageView.INVISIBLE);
		}
		libInfo.setName(name);
		libInfo.setVersion(version);
		if (libInfo.isInstalled()) {
			versionView.setTextColor(Color.GREEN);
		} else {
			versionView.setTextColor(Color.BLACK);
		}
	}

}
