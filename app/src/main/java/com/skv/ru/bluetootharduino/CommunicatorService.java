package com.skv.ru.bluetootharduino;

import android.bluetooth.BluetoothSocket;

/**
 * Created by Константин on 29.05.2017.
 */

interface CommunicatorService {
    Communicator createCommunicatorThread(BluetoothSocket socket);
}
