package com.slicejobs.panacamera.cameralibrary.model.bean;

import android.graphics.Color;
import java.io.Serializable;

public class InOutPutData {
    private static final int OUTPUT_HINT_STATUS_SUCC = 0;
    private static final int OUTPUT_HINT_STATUS_WRONG_SHAPE = 1;
    private static InOutPutData m_instance = null;
    public InOutPutData.InputData m_inputData = new InOutPutData.InputData();
    public InOutPutData.OutputData m_outputData = new InOutPutData.OutputData();

    public InOutPutData() {
    }

    public static InOutPutData getInstance() {
        if (m_instance == null) {
            m_instance = new InOutPutData();
        }

        return m_instance;
    }

    public InOutPutData.InputData createInputData() {
        return new InOutPutData.InputData();
    }

    public InOutPutData.OutputData createOutputData() {
        return new InOutPutData.OutputData();
    }

    public static enum UNITIMAGESTATUS {
        UNIT_IMG_START(-1),
        UNIT_IMG_ARRIVETOP(0),
        UNIT_IMG_TOP2TOP(1),
        UNIT_IMG_TOP2BOTTOM(2),
        UNIT_IMG_ARRIVEBOTTOM(3),
        UNIT_IMG_BOTTOM2BOTTOM(4),
        UNIT_IMG_BOTTOM2TOP(5),
        UNIT_IMG_END(6),
        UNIT_IMG_MAXNUM(7);

        private int value;

        private UNITIMAGESTATUS(int value) {
            this.value = value;
        }

        private int getValue() {
            return this.value;
        }
    }

    public static enum PICQUALITY_STATUS {
        PIC_QULITY_OK(0),
        PIC_QULITY_LOW(1),
        PIC_QULITY_MAXNUM(2);

        private int value;

        private PICQUALITY_STATUS(int value) {
            this.value = value;
        }

        private int getValue() {
            return this.value;
        }
    }

    public static enum PICQUALITY_POSE {
        PIC_QUALITY_POSE_CORRECT(0),
        PIC_QUALITY_POSE_INCORRECT(1),
        PIC_QUALITY_POSE_MAXNUM(2);

        private int value;

        private PICQUALITY_POSE(int value) {
            this.value = value;
        }

        private int getValue() {
            return this.value;
        }
    }

    public static enum PICQUALITY_MOVE_SPEED {
        PIC_QUALITY_SPEED_STABLE(0),
        PIC_QUALITY_SPEED_TOOFAST(1),
        PIC_QUALITY_SPEED_NORMAL(2),
        PIC_QUALITY_SPEED_MAXNUM(3);

        private int value;

        private PICQUALITY_MOVE_SPEED(int value) {
            this.value = value;
        }

        private int getValue() {
            return this.value;
        }
    }

    public static enum PICOVERLAPSTATUS {
        PIC_OVERLAP_STATUS_MORE(0),
        PIC_OVERLAP_STATUS_OK(1),
        PIC_OVERLAP_STATUS_LESS(2),
        PIC_OVERLAP_STATUS_RISK(3),
        PIC_OVERLAP_STATUS_MAXNUM(4);

        private int value;

        private PICOVERLAPSTATUS(int value) {
            this.value = value;
        }

        private int getValue() {
            return this.value;
        }
    }

    public class OutputData implements Serializable {
        public int charRows = 0;
        public int currentBestPovFrameIndex;
        public int currentHintLinesHeight;
        public int currentHintLinesWidth;
        public int currentHintOverlap;
        public int currentHintP1x;
        public int currentHintP1y;
        public int currentHintP2x;
        public int currentHintP2y;
        public int currentHintP3x;
        public int currentHintP3y;
        public int currentHintP4x;
        public int currentHintP4y;
        public int currentHintStatus;
        public float[] currentHomography = new float[9];
        public int currentImageFrameIndex;
        public int currentMoveStatus;
        public int currentOuputImageBufferCurrentSize;
        public byte[] currentOutputImage;
        public int currentOutputImageBufferMaxSize;
        public int currentOutputImageHeight;
        public int currentOutputImageWidth;
        public byte[] currentPanoramaImage;
        public int currentPanoramaImageBufferCurrentSize;
        public int currentPanoramaImageBufferMaxSize;
        public int currentPanormaImageHeight;
        public int currentPanormaImageWidth;
        public int[] currentPixelCorrespondance;
        public int currentPixelCorrespondanceLen;
        public int[] framePositionCoordinate = new int[2];
        public float framePositionX;
        public float framePositionY;
        public float horizontalOffsetValue;
        public int isUnitImage;
        public int isValidHintLines;
        public int lastUnitImageFeaturesNum;
        int outLen = 10240;
        public float overlappingValue;
        public int picOverlapStatus;
        public int picQualityMoveSpeed;
        public int picQualityPose;
        public int picQualityStatus;
        public int shouldMoveStatus;
        public char[] version = new char[21];
        public float verticalOffSetValue;

        public OutputData() {
        }

        public void initPanoramaImageBuffer(int len) {
            this.currentPanoramaImage = new byte[len];
        }

        public void initOutputImageBuffer(int len) {
            this.currentOutputImage = new byte[len];
        }

        public void initPixelCorrespondance(int len) {
            this.currentPixelCorrespondance = new int[len];
        }

        public int getColor() {
            return this.currentHintOverlap >= 40 ? Color.argb(250 - this.currentHintOverlap * 2, 80, 183, 235) : Color.argb(250 - this.currentHintOverlap * 3, 178, 178, 178);
        }
    }

    public static enum MOVESTATUS {
        MOVE_STATUS_UNKNOWN(0),
        MOVE_STATUS_UP2DOWN(1),
        MOVE_STATUS_DOWN2UP(2),
        MOVE_STATUS_LEFT2RIGHT(3),
        MOVE_STATUS_RIGHT2LEFT(4),
        MOVE_STATUS_UP2DOWN_LEFT2RIGHT(5),
        MOVE_STATUS_DOWN2UP_LEFT2RIGHT(6),
        MOVE_STATUS_MAXNUM(7);

        private int value;

        private MOVESTATUS(int value) {
            this.value = value;
        }

        private int getValue() {
            return this.value;
        }
    }

    public class InputData {
        public int bufferFormat;
        public int bufferHeight;
        public int bufferWidth;
        public int curIndexFrame;
        public String curTime;
        public float[] gSensorValue = new float[3];
        public float[] gyroSensorValue = new float[3];
        public byte[] imgbuffer;
        public int isUnitImage;
        public float[] linearASensorValue = new float[3];
        public String logPath;
        public int orientation;

        public InputData() {
        }
    }

    public static enum IMAGEBUFFERFORMAT {
        IM_BUFFER_FORMAT_RGBA_4C(0),
        IM_BUFFER_FORMAT_RGB_3C(1),
        IM_BUFFER_FORMAT_YUV420_NV21(2),
        IM_BUFFER_FORMAT_YUV420_NV12(3),
        IM_BUFFER_FORMAT_MAXNUM(4);

        private int value;

        private IMAGEBUFFERFORMAT(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }
}
