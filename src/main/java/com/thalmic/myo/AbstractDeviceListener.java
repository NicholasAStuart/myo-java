package com.thalmic.myo;

import com.thalmic.myo.enums.Arm;
import com.thalmic.myo.enums.WarmupResult;
import com.thalmic.myo.enums.WarmupState;
import com.thalmic.myo.enums.XDirection;

public class AbstractDeviceListener implements DeviceListener {
	@Override
	public void onPair(Myo myo, long timestamp, FirmwareVersion firmwareVersion) {
	}

	@Override
	public void onUnpair(Myo myo, long timestamp) {
	}

	@Override
	public void onConnect(Myo myo, long timestamp, FirmwareVersion firmwareVersion) {
	}

	@Override
	public void onDisconnect(Myo myo, long timestamp) {
	}

	@Override
	public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection, float rotation, WarmupState warmupState) {
	}

	@Override
	public void onArmUnsync(Myo myo, long timestamp) {
	}

	@Override
	public void onUnlock(Myo myo, long timestamp) {
	}

	@Override
	public void onLock(Myo myo, long timestamp) {
	}

	@Override
	public void onPose(Myo myo, long timestamp, Pose pose) {
	}

	@Override
	public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
	}

	@Override
	public void onAccelerometerData(Myo myo, long timestamp, Vector3 accel) {
	}

	@Override
	public void onGyroscopeData(Myo myo, long timestamp, Vector3 gyro) {
	}

	@Override
	public void onRssi(Myo myo, long timestamp, int rssi) {
	}

	@Override
	public void onEmgData(Myo myo, long timestamp, byte[] emg) {
	}

	@Override
	public void onBatteryLevelReceived(Myo myo, long timestamp, int level) {
	}

	@Override
	public void onWarmupCompleted(Myo myo, long timestamp, WarmupResult warmupResult) {
	}
}
