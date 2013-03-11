package com.ernovation.erlangforandroid;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.android_scripting.interpreter.InterpreterConstants;
import com.googlecode.android_scripting.interpreter.InterpreterDescriptor;
import com.googlecode.android_scripting.interpreter.InterpreterUtils;

public class ErlangDescriptor implements InterpreterDescriptor {

	public static final String BASE_INSTALL_URL = "http://erlang4android.googlecode.com/files/";
	//public static final String BASE_INSTALL_URL = "http://erlang.ernovation.com/files/";
	public static final String DALVIKVM = "/system/bin/dalvikvm";
	
	@Override
	public String getName() {
		return "erlang";
	}

	@Override
	public String getNiceName() {
		return "Erlang";
	}

	public String getInterpreterVersion() {
		return "R16B";
	}

	public String getExtrasVersion() {
		return "R16B";
	}

	public String getScriptsVersion() {
		return "R16B";
	}

	public String getBaseInstallUrl() {
		return BASE_INSTALL_URL;
	}

	@Override
	public String getExtension() {
		return ".erl";
	}

	@Override
	public File getBinary(Context context) {
		getExtraArgs(context);
		return new File(getExtrasPath(context) + "/bin", "erl");
	}

	@Override
	public List<String> getArguments(Context context) {
		return new ArrayList<String>();
	}

	@Override
	public String getInteractiveCommand(Context context) {
		return "";
	}

	@Override
	public String getScriptCommand(Context context) {
		return "-s android -noshell -ascript %s ";
	}

	@Override
	public int getVersion() {
		return 3;
	}

	@Override
	public boolean hasInterpreterArchive() {
		return true;
	}

	@Override
	public String getInterpreterArchiveName() {
		return String.format("%s_%s.zip", getName(), getInterpreterVersion());
	}

	@Override
	public String getExtrasArchiveName() {
		return String
				.format("%s_extras_%s.zip", getName(), getExtrasVersion());
	}

	@Override
	public String getScriptsArchiveName() {
		return String.format("%s_scripts_%s.zip", getName(),
				getScriptsVersion());
	}

	@Override
	public String getInterpreterArchiveUrl() {
		return getBaseInstallUrl() + getInterpreterArchiveName();
	}

	@Override
	public String getExtrasArchiveUrl() {
		return getBaseInstallUrl() + getExtrasArchiveName();
	}

	@Override
	public String getScriptsArchiveUrl() {
		return getBaseInstallUrl() + getScriptsArchiveName();
	}

	@Override
	public boolean hasExtrasArchive() {
		return false;
	}

	@Override
	public boolean hasScriptsArchive() {
		return false;
	}

	@Override
	public Map<String, String> getEnvironmentVariables(Context arg0) {
		Map<String, String> vars = new HashMap<String, String>();
		vars.put("HOME", getExtrasPath(arg0).getAbsolutePath());
		return vars;
	}

	@Override
	public boolean hasInteractiveMode() {
		return true;
	}

	public File getExtrasPath(Context context) {
		if (!hasInterpreterArchive() && hasExtrasArchive()) {
			return new File(InterpreterConstants.SDCARD_ROOT
					+ this.getClass().getPackage().getName()
					+ InterpreterConstants.INTERPRETER_EXTRAS_ROOT, getName());
		}
		return InterpreterUtils.getInterpreterRoot(context, getName());
	} 

	public String getExtraArgs(Context aContext) {
		File interpreterRoot = InterpreterUtils.getInterpreterRoot(aContext,
				getName());
		try {
			File argsFile = new File(interpreterRoot.getCanonicalPath()
					+ "/args.txt");
			if (!argsFile.exists()) {
				argsFile.createNewFile();
				argsFile.setReadable(true, false);
				return "";
			}
			BufferedReader reader = new BufferedReader(new FileReader(argsFile));
			String args = reader.readLine();
			reader.close();
			return args;
		} catch (Exception e) {
			System.out.println("Could not read args file: " + e.getMessage());
			return "";
		}
	}

	public void setExtraArgs(Context aContext, String aArgs) {
		File interpreterRoot = InterpreterUtils.getInterpreterRoot(aContext,
				getName());
		try {
			File argsFile = new File(interpreterRoot.getCanonicalPath()
					+ "/args.txt");
			if (argsFile.exists()) {
				argsFile.delete();
			}
			argsFile.createNewFile();
			argsFile.setReadable(true, false);
			BufferedWriter writer = new BufferedWriter(new FileWriter(argsFile));
			writer.append(aArgs);
			writer.close();
		} catch (Exception e) {
			System.out.println("Could not read args file: " + e.getMessage());
		}

	}
}
