package com.ernovation.erlangforandroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class DetailsDialog extends DialogFragment {

	private LibraryInfo iInfo;
	
	public DetailsDialog(LibraryInfo aInfo) {
		super();
		iInfo = aInfo;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle aSavedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.details_dialog, null);
		builder.setView(view);
		Dialog dialog = builder.create();
		TextView abstractView = (TextView)view.findViewById(R.id.abstractTextView);
		abstractView.setText(iInfo.getAbstract());
		TextView summaryView = (TextView)view.findViewById(R.id.summaryTextView);
		summaryView.setText(iInfo.getSummary());
		TextView packagerView = (TextView)view.findViewById(R.id.packagerTextView);
		packagerView.setText(iInfo.getPackager());
		TextView authorView = (TextView)view.findViewById(R.id.authorTextView);
		authorView.setText(iInfo.getAuthor());

		return dialog;
	}
	
}
