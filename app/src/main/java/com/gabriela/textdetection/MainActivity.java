package com.gabriela.textdetection;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.auto_focus)
    CompoundButton autoFocus;
    @BindView(R.id.use_flash)
    CompoundButton useFlash;
    @BindView(R.id.status_message)
    TextView statusMessage;
    @BindView(R.id.txt_name)
    TextView mTxtName;
    @BindView(R.id.txt_cpf)
    TextView mTxtCpf;
    @BindView(R.id.txt_birth_date)
    TextView mTxtBirthDate;

    private static final int RC_OCR_CAPTURE = 9003;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.read_text)
    public void onReadTextClick() {
        MainActivityPermissionsDispatcher.forwardToCameraWithCheck(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    void showDeniedForCamera() {
        Toast.makeText(this, R.string.permission_camera_rationale, Toast.LENGTH_SHORT).show();
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    public void forwardToCamera() {
        Intent intent = new Intent(this, OcrCaptureActivity.class);
        intent.putExtra(OcrCaptureActivity.AUTOFOCUS, autoFocus.isChecked());
        intent.putExtra(OcrCaptureActivity.USEFLASH, useFlash.isChecked());
        startActivityForResult(intent, RC_OCR_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String cpf = data.getStringExtra(OcrCaptureActivity.CPF);
                    Date birthDate = (Date) data.getSerializableExtra(OcrCaptureActivity.BIRTH_DATE);
                    String name = data.getStringExtra(OcrCaptureActivity.NAME);
                    statusMessage.setText(R.string.ocr_success);
                    mTxtName.setText(name);
                    mTxtCpf.setText(FormatUtils.formatCpf(cpf));
                    mTxtBirthDate.setText(FormatUtils.formatDate(birthDate));

                } else {
                    statusMessage.setText(R.string.ocr_failure);
                    Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.ocr_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
