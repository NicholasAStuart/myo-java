package com.thalmic.myo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class Hub {
    private static final File TEMP_DIRECTORY_LOCATION = new File(System.getProperty("java.io.tmpdir"));
    private long nativeHandle;
    private final String applicationIdentifier;

    public Hub() {
	this("");
    }

    public Hub(String applicationIdentifier) {
	this.applicationIdentifier = applicationIdentifier;
	loadJniResources();
	initialize(applicationIdentifier);
    }

    private native void initialize(String applicationIdentifier);

    public native Myo waitForMyo(int timeout);

    public native void addListener(DeviceListener listener);

    public native void removeListener(DeviceListener listener);

    public native void run(int duration);

    public native void runOnce(int duration);

    private void loadJniResources() {
	boolean wasLoadSuccessful = loadX64ResourcesFromSysPath();
	if (!wasLoadSuccessful) {
	    wasLoadSuccessful = loadWin32ResourcesFromSysPath();
	    if (!wasLoadSuccessful) {
		setLibDirectory();

		wasLoadSuccessful = copyAndLoadX64FromTemp();
		if (!wasLoadSuccessful) {
		    wasLoadSuccessful = copyAndLoadWin32FromTemp();
		    if (wasLoadSuccessful) {
			throw new UnsatisfiedLinkError("Could Not Load myo and myo-java libs");
		    }
		}
	    }
	}
    }

    private void setLibDirectory() {
	try {
	    System.setProperty("java.library.path", TEMP_DIRECTORY_LOCATION.getAbsolutePath());
	    Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
	    fieldSysPath.setAccessible(true);
	    fieldSysPath.set(null, null);
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    private boolean loadX64ResourcesFromSysPath() throws UnsatisfiedLinkError {
	try {
	    System.loadLibrary("myo64");
	    System.loadLibrary("JNIJavaMyoLib");
	    return true;
	} catch (UnsatisfiedLinkError e) {
	    String errorMessage = String.format("Unable to load myo64 from system directories.");
	    System.err.println(errorMessage);
	}
	return false;
    }

    private boolean loadWin32ResourcesFromSysPath() throws UnsatisfiedLinkError {
	try {
	    System.loadLibrary("myo32");
	    System.loadLibrary("JNIJavaMyoLib");
	    return true;
	} catch (UnsatisfiedLinkError e) {
	    String errorMessage = String.format("Unable to load myo32 from system directories.");
	    System.err.println(errorMessage);
	}
	return false;
    }

    private boolean copyAndLoadX64FromTemp() {
	try {
	    File myo64DllTempFile = new File(TEMP_DIRECTORY_LOCATION, "myo64.dll");
	    InputStream myo64DllInputStream = this.getClass()
		    .getResourceAsStream("/x64/myo64.dll");
	    Files.copy(myo64DllInputStream, myo64DllTempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

	    File jniJavaMyoLibDllTempFile = new File(TEMP_DIRECTORY_LOCATION, "JNIJavaMyoLib64.dll");
	    InputStream jniJavaMyoLibDllInputStream = this.getClass()
		    .getResourceAsStream("/x64/JNIJavaMyoLib.dll");
	    Files.copy(jniJavaMyoLibDllInputStream, jniJavaMyoLibDllTempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

	    myo64DllTempFile.deleteOnExit();
	    jniJavaMyoLibDllTempFile.deleteOnExit();

	    System.loadLibrary("myo64");
	    System.loadLibrary("JNIJavaMyoLib64");
	    return true;
	} catch (UnsatisfiedLinkError e) {
	    String errorMessage = String.format("Unable to load %s from directory %s.", "myo64.dll", TEMP_DIRECTORY_LOCATION);
	    System.err.println(errorMessage);
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return false;
    }

    private boolean copyAndLoadWin32FromTemp() {
	try {
	    File myo32DllTempFile = new File(TEMP_DIRECTORY_LOCATION, "myo32.dll");
	    myo32DllTempFile.deleteOnExit();
	    InputStream myo32DllInputStream = this.getClass()
		    .getResourceAsStream("/Win32/myo32.dll");
	    Files.copy(myo32DllInputStream, myo32DllTempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

	    File jniJavaMyoLibDllTempFile = new File(TEMP_DIRECTORY_LOCATION, "JNIJavaMyoLib32.dll");
	    InputStream jniJavaMyoLibDllInputStream = this.getClass()
		    .getResourceAsStream("/Win32/JNIJavaMyoLib.dll");
	    Files.copy(jniJavaMyoLibDllInputStream, jniJavaMyoLibDllTempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

	    myo32DllTempFile.deleteOnExit();
	    jniJavaMyoLibDllTempFile.deleteOnExit();

	    System.loadLibrary("myo32");
	    System.loadLibrary("JNIJavaMyoLib32");
	    return true;
	} catch (UnsatisfiedLinkError e) {
	    String errorMessage = String.format("Unable to load %s from directory %s.", "myo32.dll", TEMP_DIRECTORY_LOCATION);
	    System.err.println(errorMessage);
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return false;
    }

}