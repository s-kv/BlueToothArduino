package com.skv.ru.bluetootharduino;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by Константин on 29.05.2017.
 */

public class ClientThread extends Thread {
    private volatile Communicator communicator;

    private final BluetoothDevice device;
    private final CommunicatorService communicatorService;
    private BluetoothSocket socket;
    private BluetoothAdapter bluetoothAdapter;

    public ClientThread(BluetoothDevice device, CommunicatorService communicatorService) {

        this.communicatorService = communicatorService;
        this.device = device;

        BluetoothSocket tmp = null;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(MainFragment.UUID));
        } catch (IOException e) {
            Log.d("ClientThread", e.getLocalizedMessage());
        }
        socket = tmp;
    }

    public synchronized Communicator getCommunicator() {
        return communicator;
    }

    public void run() {
        bluetoothAdapter.cancelDiscovery();
        try {
            Log.d("ClientThread", "About to connect");
            socket.connect();
            Log.d("ClientThread", "Connected");
            synchronized (this) {
                communicator = communicatorService.createCommunicatorThread(socket);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("ClientThread", "Start");
                    communicator.startCommunication();
                }
            }).start();
        } catch (IOException connectException) {
            Log.d("ClientThread", connectException.getMessage());
            try {
                Log.d("ClientThread", "trying connect again...");
                socket.close();
                socket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
                socket.connect();

                /*Class<?> clazz = device.getClass();
                Class<?>[] paramTypes = new Class<?>[] {Integer.TYPE};
                Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                Object[] params = new Object[] {Integer.valueOf(1)};
                socket = (BluetoothSocket) m.invoke(device, params);
                socket.connect();*/

                Log.d("ClientThread","Connected");
                //socket.close();
            } catch (IOException exception) {
                Log.d("ClientThread", exception.getLocalizedMessage());
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public void cancel() {
        if (communicator != null) communicator.stopCommunication();
    }
}
