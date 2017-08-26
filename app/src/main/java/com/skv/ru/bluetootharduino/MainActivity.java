package com.skv.ru.bluetootharduino;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends SingleFragmentActivity {

    private static final String EXTRA_DEVICE = "com.skv.ru.bluetootharduino.device";
    public static Intent newIntent(Context packageContext, BluetoothDevice bluetoothDevice) {
        Intent intent = new Intent(packageContext, MainActivity.class);
        intent.putExtra(EXTRA_DEVICE, bluetoothDevice);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        BluetoothDevice bluetoothDevice = (BluetoothDevice)getIntent().getParcelableExtra(EXTRA_DEVICE);
        return new MainFragment().newInstance(bluetoothDevice);
    }
}
