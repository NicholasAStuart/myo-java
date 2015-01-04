package com.thalmic.myo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import com.thalmic.myo.enums.LockingPolicy;

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

    public void setLockingPolicy(LockingPolicy lockingPolicy) {
	setLockingPolicy(lockingPolicy.ordinal());
    }

    private native void setLockingPolicy(int lockingPolicy);

    private final void loadJniResources() {
	String osName = System.getProperty("os.name")
		.toLowerCase();
	if (osName.contains("mac")) {
	    boolean wasLoadSuccessful = loadOSXResourceFromSysPath();
	    if (!wasLoadSuccessful) {
		wasLoadSuccessful = copyAndLoadOSXFromTemp();
		if (!wasLoadSuccessful) {
		    throw new UnsatisfiedLinkError("Could Not Load myo and myo-java libs");
		}
	    }

	} else if (osName.contains("win")) {
	    boolean wasLoadSuccessful = loadX64ResourcesFromSysPath();
	    if (!wasLoadSuccessful) {
		wasLoadSuccessful = loadWin32ResourcesFromSysPath();
		if (!wasLoadSuccessful) {
		    setLibDirectory();

		    wasLoadSuccessful = copyAndLoadX64FromTemp();
		    if (!wasLoadSuccessful) {
			wasLoadSuccessful = copyAndLoadWin32FromTemp();
			if (!wasLoadSuccessful) {
			    throw new UnsatisfiedLinkError("Could Not Load myo and myo-java libs");
			}
		    }
		}
	    }
	} else {
	    System.err.println("Your Operating System is not supported at this time.");
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

    private boolean copyAndLoadOSXFromTemp() {
	try {

	    File macMyoTempFile = new File(TEMP_DIRECTORY_LOCATION, "libmyo.jnilib");
	    InputStream macMyoInputStream = this.getClass()
		    .getResourceAsStream("/osx/libmyo.jnilib");
	    if (!macMyoTempFile.exists()) {
		Files.copy(macMyoInputStream, macMyoTempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		macMyoTempFile.deleteOnExit();
	    }

	    File myoFrameworkCompleteDirectory = new File(TEMP_DIRECTORY_LOCATION + "/myo.framework/Versions/A/myo");
	    myoFrameworkCompleteDirectory.mkdirs();
	    InputStream macMyoLibInputStream = this.getClass()
		    .getResourceAsStream("/osx/myo.framework/Versions/A/myo");
	    if (!myoFrameworkCompleteDirectory.exists()) {
		Files.copy(macMyoLibInputStream, myoFrameworkCompleteDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING);
		myoFrameworkCompleteDirectory.deleteOnExit();
	    }

	    setLibDirectory();
	    System.loadLibrary("myo");
	    return true;
	} catch (UnsatisfiedLinkError e) {
	    String errorMessage = String.format("Unable to load %s from directory %s", "libmyo.jnilib", TEMP_DIRECTORY_LOCATION);
	    System.err.println(errorMessage);
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return false;
    }

    private boolean loadOSXResourceFromSysPath() throws UnsatisfiedLinkError {
	try {
	    System.loadLibrary("myo");
	    return true;
	} catch (UnsatisfiedLinkError e) {
	    String errorMessage = String.format("Unable to load myo from system directories.");
	    System.err.println(errorMessage);
	}
	return false;
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
	    if (!myo64DllTempFile.exists()) {
		Files.copy(myo64DllInputStream, myo64DllTempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		myo64DllTempFile.deleteOnExit();
	    }

	    File jniJavaMyoLibDllTempFile = new File(TEMP_DIRECTORY_LOCATION, "JNIJavaMyoLib64.dll");
	    InputStream jniJavaMyoLibDllInputStream = this.getClass()
		    .getResourceAsStream("/x64/JNIJavaMyoLib.dll");
	    if (!jniJavaMyoLibDllTempFile.exists()) {
		Files.copy(jniJavaMyoLibDllInputStream, jniJavaMyoLibDllTempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		jniJavaMyoLibDllTempFile.deleteOnExit();
	    }

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
	    if (!myo32DllTempFile.exists()) {
		Files.copy(myo32DllInputStream, myo32DllTempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		myo32DllTempFile.deleteOnExit();
	    }

	    File jniJavaMyoLibDllTempFile = new File(TEMP_DIRECTORY_LOCATION, "JNIJavaMyoLib32.dll");
	    InputStream jniJavaMyoLibDllInputStream = this.getClass()
		    .getResourceAsStream("/Win32/JNIJavaMyoLib.dll");
	    if (!jniJavaMyoLibDllTempFile.exists()) {
		Files.copy(jniJavaMyoLibDllInputStream, jniJavaMyoLibDllTempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		jniJavaMyoLibDllTempFile.deleteOnExit();
	    }

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