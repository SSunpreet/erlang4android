package com.ernovation.erlangforandroid;

import com.googlecode.android_scripting.interpreter.InterpreterDescriptor;
import com.googlecode.android_scripting.interpreter.InterpreterProvider;

public class ErlangProvider extends InterpreterProvider {
  @Override
  protected InterpreterDescriptor getDescriptor() {
    return new ErlangDescriptor();
  }
}
