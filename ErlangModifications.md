# Introduction #

The source code for Erlang/OTP as provided on http://www.erlang.org does not work out of the box on Android. This document describes the modifications made to the source of OTP R15B03 to get it working on Android and ready to be used with SL4A.


# Details #

## autoconf files ##

The files config.guess and config.sub have been replaced by more recent versions. The provided versions did not recognize the arm-linux-androideabi as a build target, but the new versions do.

## librt ##

Used functions of librt are provided elsewhere on Android. Therefore references to librt had to be removed.

## Keep fd to system properties open ##

Android provides access to system properties through a memory mapped file. The relevant file descriptor should be passed to child processes. The Android system (bionic) makes use of this file descriptor to access system properties, specifically to find dns servers.
Erlang closes all file descriptors for a child. This has been modified to not close the file descriptor for the system properties. Without this modification, name resolution does not work natively.

## Location of sh ##

On Android sh is located at /system/bin/sh, not at /bin/sh. In all relevant scripts and code, the /bin/sh has been replaced by /system/bin/sh.

## Removal of applications ##

Some applications have been removed, because they require a working supported gui library (wx or gs), which is not supported. Applications appmon, wx, gs, pman, toolbar and tv have been removed. Also odbc has been removed.

## Access to SL4A ##

This port of Erlang/OTP is intended to be used with SL4A. A small library to access the SL4A API has been added.

## Automatically compile .erl files ##

SL4A is intended for scripting languages. Editable scripts should be made runnable. This has been achieved by automatically compiling .erl files to .beam files if the .beam file cannot be found, but the .erl file can be found. A scriptable .erl file should contain a main/0 function, which will be called when the .erl file is run.
This has been implemented by modifying error\_handler.erl

## Removed unsupported Unix functions ##

Calls to gethostid() (in ei\_connect.c) and getting extended sysinfo (in memsup.c) have been removed. Since pthread\_sigmask() is at least unreliable on Android, this call has been replaced by sigprocmask().

## Extra BIFs ##

Some extra bifs were added for some lower level access to files etcetera.


Add your content here.  Format your content with:
  * Text in **bold** or _italic_
  * Headings, paragraphs, and lists
  * Automatic links to other wiki pages