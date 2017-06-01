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

    /**
     * Called by the detector to deliver detection results.
     * If your application called for it, this could be a place to check for
     * equivalent detections by tracking TextBlocks that are similar in location and content from
     * previous frames, or reduce noise by eliminating TextBlocks that have not persisted through
     * multiple detections.
     */
    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        boolean drawGraphic;
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);

            drawGraphic = false;

            Log.v(OcrDetectorProcessor.class.getSimpleName(), "tentativa de identificação = " + item.getValue());

            if (item.getValue().length() >= 10) {
                if (TextUtils.isEmpty(mCpf) && TextIdentifierUtils.isCpfValue(item)) {
                    mCpf = item.getValue();
                    mCallback.cpfFound(mCpf, mActivity);
                    drawGraphic = true;
                } else if (TextIdentifierUtils.isDate(item)) {
                    setBirthDate(item);
                    mCallback.birthDateFound(mBirthDate, mActivity);
                    drawGraphic = true;
                } else {
                    setName(item);
                    if (!TextUtils.isEmpty(mFirstPossibleName)) {
                        mCallback.nameFound(mFirstPossibleName, mActivity);
                        drawGraphic = true;
                    }
                    String possibleCpf = TextIdentifierUtils.getCpf(item.getValue());
                    if (!TextUtils.isEmpty(possibleCpf)) {
                        mCpf = possibleCpf;
                        mCallback.cpfFound(possibleCpf, mActivity);
                        drawGraphic = true;
                    }
                }

                if (drawGraphic) {
                    OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
                    mGraphicOverlay.add(graphic);
                }
            }
        }

        Log.v(OcrDetectorProcessor.class.getSimpleName(), "mCpf = " + mCpf);
        Log.v(OcrDetectorProcessor.class.getSimpleName(), "mBirthDate = " + mBirthDate);
        Log.v(OcrDetectorProcessor.class.getSimpleName(), "mFirstPossibleName = " + mFirstPossibleName);
        Log.v(OcrDetectorProcessor.class.getSimpleName(), "mTopPointName = " + mTopPointName);
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
        Log.v(OcrDetectorProcessor.class.getSimpleName(), "vai testar se é data = " + textBlock.getValue());
        if (TextIdentifierUtils.isDate(textBlock)) {

            Date currentDate = TextIdentifierUtils.formatDate(textBlock.getValue());
            Log.v(OcrDetectorProcessor.class.getSimpleName(), "data testada = " + currentDate);
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
