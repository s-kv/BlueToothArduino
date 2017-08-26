package com.skv.ru.bluetootharduino;

import android.support.v4.app.Fragment;

/**
 * Created by Константин on 29.05.2017.
 */

public class BluetoothDeviceListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new BluetoothDeviceListFragment();
    }
}
