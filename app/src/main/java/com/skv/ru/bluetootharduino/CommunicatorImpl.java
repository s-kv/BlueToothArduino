package com.skv.ru.bluetootharduino;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Константин on 29.05.2017.
 */

public class CommunicatorImpl implements Communicator {
    private final BluetoothSocket socket;
    private final OutputStream outputStream;

    public CommunicatorImpl(BluetoothSocket socket) {
        this.socket = socket;
        OutputStream tmpOut = null;
        try {
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.d("CommunicatorImpl", e.getLocalizedMessage());
        }
        outputStream = tmpOut;
    }

    @Override
    public void startCommunication() {

    }

    public void write(String message) {
        try {
            Log.d("CommunicatorImpl", "Write " + message);
            outputStream.write(message.getBytes());
        } catch (IOException e) {
            Log.d("CommunicatorImpl", e.getLocalizedMessage());
        }
    }

    @Override
    public void stopCommunication() {
        try {
            socket.close();
        } catch (IOException e) {
            Log.d("CommunicatorImpl", e.getLocalizedMessage());
        }
    }
}
