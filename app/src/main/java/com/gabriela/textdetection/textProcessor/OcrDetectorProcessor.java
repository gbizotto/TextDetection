package com.gabriela.textdetection.textProcessor;

import android.app.Activity;
import android.content.Context;
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
    private final Context mContext;

    public OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay, DataFoundCallback callback, Activity activity, Context context) {
        mGraphicOverlay = ocrGraphicOverlay;
        mCallback = callback;
        mActivity = activity;
        mContext = context;
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

        boolean foundData = hasFoundCpf(textBlock.getValue());
        if (foundData) {
            return true;
        }

        foundData = hasFoundBirthDate(textBlock.getValue());
        if (foundData) {
            return true;
        }

        setName(textBlock);
        if (!TextUtils.isEmpty(mFirstPossibleName)) {
            mCallback.nameFound(mFirstPossibleName, mActivity);
            foundData = true;
        }

        String possibleCpf = TextIdentifierUtils.INSTANCE.getCpf(textBlock.getValue());
        if (!TextUtils.isEmpty(possibleCpf)) {
            setCpf(possibleCpf);
            foundData = true;
        }

        Date possibleBirthDate = TextIdentifierUtils.INSTANCE.getBirthDate(textBlock.getValue(), mContext);
        if (possibleBirthDate != null && (mBirthDate == null || mBirthDate.after(possibleBirthDate))) {
            mBirthDate = possibleBirthDate;
            mCallback.birthDateFound(mBirthDate, mActivity);
            foundData = true;
        }

        return foundData;
    }

    private boolean hasFoundCpf(String text) {
        if (TextUtils.isEmpty(mCpf) && TextIdentifierUtils.INSTANCE.isCpfValue(text)) {
           setCpf(text);
            return true;
        }
        return false;
    }

    private void setCpf(String cpf){
        mCpf = cpf;
        mCallback.cpfFound(mCpf, mActivity);
    }

    private boolean hasFoundBirthDate(String text) {
        if (TextIdentifierUtils.INSTANCE.isDate(text, mContext)) {
            setBirthDate(text);
            mCallback.birthDateFound(mBirthDate, mActivity);
            return true;
        }
        return false;
    }

    private void setName(TextBlock textBlock) {
        if (TextIdentifierUtils.INSTANCE.mightBeName(textBlock.getValue())) {
            if (mTopPointName == null || mTopPointName > textBlock.getBoundingBox().top) {
                mTopPointName = textBlock.getBoundingBox().top;
                mFirstPossibleName = textBlock.getValue();
            }
        }
    }

    private void setBirthDate(String text) {
        if (TextIdentifierUtils.INSTANCE.isDate(text, mContext)) {
            Date currentDate = TextIdentifierUtils.INSTANCE.formatDate(text, mContext);
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
