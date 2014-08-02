package com.thalmic.myo.example;

import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.FirmwareVersion;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;
import com.thalmic.myo.enums.Arm;
import com.thalmic.myo.enums.PoseType;
import com.thalmic.myo.enums.VibrationType;
import com.thalmic.myo.enums.XDirection;

public class DataCollector implements DeviceListener {
    private double rollW;
    private double pitchW;
    private double yawW;
    private Pose currentPose;
    private Arm whichArm;

    public DataCollector() {
	rollW = 0;
	pitchW = 0;
	yawW = 0;
	currentPose = new Pose();
    }

    @Override
    public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
	Quaternion normalized = rotation.normalized();

	double roll = Math.atan2(2.0f * (normalized.getW() * normalized.getX() + normalized.getY() * normalized.getZ()), 1.0f - 2.0f * (normalized.getX() * normalized.getX() + normalized.getY() * normalized.getY()));
	double pitch = Math.asin(2.0f * (normalized.getW() * normalized.getY() - normalized.getZ() * normalized.getX()));
	double yaw = Math.atan2(2.0f * (normalized.getW() * normalized.getZ() + normalized.getX() * normalized.getY()), 1.0f - 2.0f * (normalized.getY() * normalized.getY() + normalized.getZ() * normalized.getZ()));

	rollW = ((roll + Math.PI) / (Math.PI * 2.0) * 18);
	pitchW = ((pitch + Math.PI / 2.0) / Math.PI * 18);
	yawW = ((yaw + Math.PI) / (Math.PI * 2.0) * 18);
    }

    @Override
    public void onPose(Myo myo, long timestamp, Pose pose) {
	currentPose = pose;
	if (currentPose.getType() == PoseType.FIST) {
	    myo.vibrate(VibrationType.VIBRATION_MEDIUM);
	}
    }

    @Override
    public void onArmRecognized(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
	whichArm = arm;
    }

    @Override
    public void onArmLost(Myo myo, long timestamp) {
	whichArm = null;
    }

    @Override
    public void onAccelerometerData(Myo myo, long timestamp, Vector3 accel) {
    }

    @Override
    public void onConnect(Myo myo, long timestamp, FirmwareVersion firmwareVersion) {
    }

    @Override
    public void onDisconnect(Myo myo, long timestamp) {
    }

    @Override
    public void onPair(Myo myo, long timestamp, FirmwareVersion firmwareVersion) {
    }

    @Override
    public void onGyroscopeData(Myo myo, long timestamp, Vector3 gyro) {
    }

    @Override
    public void onRssi(Myo myo, long timestamp, int rssi) {
    }

    @Override
    public String toString() {
	return "DataCollector [rollW=" + rollW + ", pitchW=" + pitchW + ", yawW=" + yawW + ", currentPose=" + currentPose + ", whichArm=" + whichArm + "]";
    }
}