package com.ernovation.erlangforandroid;

import android.content.Context;

import com.googlecode.android_scripting.AsyncTaskListener;
import com.googlecode.android_scripting.InterpreterInstaller;
import com.googlecode.android_scripting.exception.Sl4aException;
import com.googlecode.android_scripting.interpreter.InterpreterDescriptor;

public class ErlangInstaller extends InterpreterInstaller {

  public ErlangInstaller(InterpreterDescriptor descriptor, Context context,
      AsyncTaskListener<Boolean> listener) throws Sl4aException {
    super(descriptor, context, listener);
  }

  @Override
  protected boolean setup() {
    // TODO Auto-generated method stub
    return true;
  }

}
