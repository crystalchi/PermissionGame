package com.crystal.permissiongame;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.crystal.permission.PermissionDenied;
import com.crystal.permission.PermissionGranted;
import com.crystal.permission.PermissionRequest;

public class MainActivity extends AppCompatActivity {

    private Button mBtnSdcard, mBtnCallPhone;
    private static final int REQUECT_CODE_CALL_PHONE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnCallPhone = (Button) findViewById(R.id.id_btn_callphone);

        mBtnCallPhone.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                PermissionRequest.requestPermission(MainActivity.this, REQUECT_CODE_CALL_PHONE, Manifest.permission.CALL_PHONE);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        PermissionRequest.requestPermissionResult(MainActivity.this, requestCode, permissions, grantResults);
    }

    @PermissionGranted(REQUECT_CODE_CALL_PHONE)
    public void requestCallPhoneSuccess(){
        callPhone();
    }

    @PermissionDenied(REQUECT_CODE_CALL_PHONE)
    public void requestCallPhoneFailed(){
        Toast.makeText(this, "DENY ACCESS SDCARD!", Toast.LENGTH_SHORT).show();
    }

    public void callPhone() throws SecurityException{
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + "10086");
        intent.setData(data);
        startActivity(intent);
    }

}
