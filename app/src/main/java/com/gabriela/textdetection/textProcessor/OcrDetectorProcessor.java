package com.gabriela.textdetection.textProcessor;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.gabriela.textdetection.camera.GraphicOverlay;
import com.gabriela.textdetection.utils.TextIdentifierUtils;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private final GraphicOverlay<OcrGraphic> mGraphicOverlay;

    private String mCpf = null;
    private Integer mTopPointDate = null;
    private Integer mTopPointName = null;
    private Date mBirthDate = null;
    private String mName;
    private final DataFoundCallback mCallback;

    public OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay, DataFoundCallback callback) {
        mGraphicOverlay = ocrGraphicOverlay;
        mCallback = callback;
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
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);

            if (item.getValue().length() >= 10) {
                if (TextUtils.isEmpty(mCpf) && TextIdentifierUtils.isCpfValue(item)) {
                    mCpf = item.getValue();
                }

                if (TextIdentifierUtils.isDate(item)) {
                    setBirthDate(item);
                }

                setName(item);

                OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
                mGraphicOverlay.add(graphic);
            }
        }

        if (!TextUtils.isEmpty(mCpf) && mBirthDate != null && !TextUtils.isEmpty(mName)) {
            mCallback.dataFound(mCpf, mBirthDate, mName);
        }
    }

    private void setName(TextBlock textBlock) {
        if (TextIdentifierUtils.isLettersOnly(textBlock.getValue())) {
            if (mTopPointName == null || mTopPointName < textBlock.getBoundingBox().top) {
                mTopPointName = textBlock.getBoundingBox().top;
                mName = textBlock.getValue();
            }
        }
    }

    private void setBirthDate(TextBlock textBlock) {
        if (TextIdentifierUtils.isDate(textBlock)) {
            if (mTopPointDate == null || mTopPointDate < textBlock.getBoundingBox().top) {
                mTopPointDate = textBlock.getBoundingBox().top;
                mBirthDate = TextIdentifierUtils.formatDate(textBlock.getValue());
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

    private String toString(TextBlock textBlock) {
        StringBuilder builder = new StringBuilder();
        builder.append(textBlock.getValue())
                .append(", ")
                .append(textBlock.getLanguage())
                .append(", bounding box = ")
                .append(textBlock.getBoundingBox().top)
                .append(",")
                .append(textBlock.getBoundingBox().right)
                .append(",")
                .append(textBlock.getBoundingBox().bottom)
                .append(",")
                .append(textBlock.getBoundingBox().left)
                .append(", corner points = ")
                .append(textBlock.getCornerPoints()[0])
                .append(",")
                .append(textBlock.getCornerPoints()[1])
                .append(",")
                .append(textBlock.getCornerPoints()[2])
                .append(",")
                .append(textBlock.getCornerPoints()[3]);

        return builder.toString();
    }
}
