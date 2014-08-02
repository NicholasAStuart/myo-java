package com.thalmic.myo;

import com.thalmic.myo.enums.VibrationType;

public final class Myo {
    private long nativeHandle;

    private Myo() {
    }

    public void vibrate(VibrationType type) {
	vibrate(type.ordinal());
    }

    public native void requestRssi();

    private native void vibrate(int type);
}