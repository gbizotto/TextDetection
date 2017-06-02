package com.gabriela.textdetection.textProcessor;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.gabriela.textdetection.camera.GraphicOverlay;
import com.gabriela.textdetection.utils.TextIdentifierUtils;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import java.util.Date;

public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private final GraphicOverlay<OcrGraphic> mGraphicOverlay;

    private String mCpf = null;
    private Integer mTopPointName = null;
    private Date mBirthDate = null;
    private String mFirstPossibleName;
    private final DataFoundCallback mCallback;
    private final Activity mActivity;

    public OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay, DataFoundCallback callback, Activity activity) {
        mGraphicOverlay = ocrGraphicOverlay;
        mCallback = callback;
        mActivity = activity;
    }

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        boolean drawGraphic;
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);

            drawGraphic = identifyData(item);

            if (drawGraphic) {
                OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
                mGraphicOverlay.add(graphic);
            }
        }

        Log.v(OcrDetectorProcessor.class.getSimpleName(), "mCpf = " + mCpf);
        Log.v(OcrDetectorProcessor.class.getSimpleName(), "mBirthDate = " + mBirthDate);
        Log.v(OcrDetectorProcessor.class.getSimpleName(), "mFirstPossibleName = " + mFirstPossibleName);
        Log.v(OcrDetectorProcessor.class.getSimpleName(), "mTopPointName = " + mTopPointName);
    }

    private boolean identifyData(TextBlock textBlock) {
        if (textBlock.getValue().length() < 10) {
            return false;
        }

        if (TextUtils.isEmpty(mCpf) && TextIdentifierUtils.isCpfValue(textBlock)) {
            mCpf = textBlock.getValue();
            mCallback.cpfFound(mCpf, mActivity);
            return true;
        }

        if (TextIdentifierUtils.isDate(textBlock)) {
            setBirthDate(textBlock);
            mCallback.birthDateFound(mBirthDate, mActivity);
            return true;
        }

        boolean foundData = false;

        setName(textBlock);
        if (!TextUtils.isEmpty(mFirstPossibleName)) {
            mCallback.nameFound(mFirstPossibleName, mActivity);
            foundData = true;
        }

        String possibleCpf = TextIdentifierUtils.getCpf(textBlock.getValue());
        if (!TextUtils.isEmpty(possibleCpf)) {
            mCpf = possibleCpf;
            mCallback.cpfFound(possibleCpf, mActivity);
            foundData = true;
        }

        Date possibleBirthDate = TextIdentifierUtils.getBirthDate(textBlock.getValue());
        if (possibleBirthDate != null && mBirthDate != null && mBirthDate.after(possibleBirthDate)) {
            mBirthDate = possibleBirthDate;
            mCallback.birthDateFound(mBirthDate, mActivity);
            foundData = true;
        }

        return foundData;
    }

    private void setName(TextBlock textBlock) {
        if (TextIdentifierUtils.mightBeName(textBlock.getValue())) {
            if (mTopPointName == null || mTopPointName > textBlock.getBoundingBox().top) {
                mTopPointName = textBlock.getBoundingBox().top;
                mFirstPossibleName = textBlock.getValue();
            }
        }
    }

    private void setBirthDate(TextBlock textBlock) {
        if (TextIdentifierUtils.isDate(textBlock)) {
            Date currentDate = TextIdentifierUtils.formatDate(textBlock.getValue());
            if (mBirthDate == null || mBirthDate.after(currentDate)) {
                mBirthDate = currentDate;
            }
        }
    }

    /**
     * Frees the resources associated with this detection processor.
     */
    @Override
    public void release() {
        mGraphicOverlay.clear();
    }
}
