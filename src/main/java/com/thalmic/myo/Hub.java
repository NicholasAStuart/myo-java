package com.thalmic.myo;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Hub {
    private long nativeHandle;
    private final String applicationIdentifier;

    public Hub() {
	this("");
    }

    public Hub(String applicationIdentifier) {
	this.applicationIdentifier = applicationIdentifier;
	initialize(applicationIdentifier);
    }

    private native void initialize(String applicationIdentifier);

    public native Myo waitForMyo(int timeout);

    public native void addListener(DeviceListener listener);

    public native void removeListener(DeviceListener listener);

    public native void run(int duration);

    public native void runOnce(int duration);

    static {
	loadJniResources();
    }

    private static void loadJniResources() {
	Map<String, String> archMap = new LinkedHashMap<>();
	archMap.put("src/main/resources/x64/", "myo64");
	archMap.put("src/main/resources/Win32/", "myo32");

	for (String directory : archMap.keySet()) {
	    String myoLibName = archMap.get(directory);
	    try {
		System.setProperty("java.library.path", directory);

		Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
		fieldSysPath.setAccessible(true);
		fieldSysPath.set(null, null);

		System.loadLibrary(myoLibName);
		System.loadLibrary("JNIJavaMyoLib");
		break;
	    } catch (UnsatisfiedLinkError e) {
		String errorMessage = String.format("Unable to load %s from directory %s.", myoLibName, directory);
		System.err.println(errorMessage);
	    } catch (NoSuchFieldException e) {
		e.printStackTrace();
	    } catch (SecurityException e) {
		e.printStackTrace();
	    } catch (IllegalArgumentException e) {
		e.printStackTrace();
	    } catch (IllegalAccessException e) {
		e.printStackTrace();
	    }
	}
    }
}