package com.skv.ru.bluetootharduino;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by Константин on 21.05.2017.
 */

public class MainFragment extends Fragment {
    public final static String UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private static final String ARG_DEVICE = "device";

    private BluetoothDevice bluetoothDevice;
    private ClientThread clientThread;

    private final CommunicatorService communicatorService = new CommunicatorService() {
        @Override
        public Communicator createCommunicatorThread(BluetoothSocket socket) {
            return new CommunicatorImpl(socket);
        }
    };

    private class WriteTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... args) {
            try {
                clientThread.getCommunicator().write(args[0]);
            } catch (Exception e) {
                Log.d("MainActivity", e.getClass().getSimpleName() + " " + e.getLocalizedMessage());
            }
            return null;
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (clientThread == null) {
                Toast.makeText(getActivity(), "Сначала выберите клиента", Toast.LENGTH_SHORT).show();
                return;
            }

            switch (v.getId()) {
                case R.id.imageButtonLeftUp: {
                    new WriteTask().execute("G");
                    break;
                }
                case R.id.imageButtonUp: {
                    new WriteTask().execute("F");
                    break;
                }
                case R.id.imageButtonRightUp: {
                    new WriteTask().execute("I");
                    break;
                }
                case R.id.imageButtonLeft: {
                    new WriteTask().execute("L");
                    break;
                }
                case R.id.imageButtonStop: {
                    new WriteTask().execute("S");
                    break;
                }
                case R.id.imageButtonRight: {
                    new WriteTask().execute("R");
                    break;
                }
                case R.id.imageButtonLeftDown: {
                    new WriteTask().execute("H");
                    break;
                }
                case R.id.imageButtonDown: {
                    new WriteTask().execute("B");
                    break;
                }
                case R.id.imageButtonRightDown: {
                    new WriteTask().execute("J");
                    break;
                }
            }
        }
    };

    public static MainFragment newInstance(BluetoothDevice bluetoothDevice) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_DEVICE, bluetoothDevice);
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothDevice = (BluetoothDevice) getArguments().getParcelable(ARG_DEVICE);

        if(clientThread == null) {
            clientThread = new ClientThread(bluetoothDevice, communicatorService);
            clientThread.start();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        ((ImageButton)v.findViewById(R.id.imageButtonLeftUp)).setOnClickListener(clickListener);
        ((ImageButton)v.findViewById(R.id.imageButtonUp)).setOnClickListener(clickListener);
        ((ImageButton)v.findViewById(R.id.imageButtonRightUp)).setOnClickListener(clickListener);

        ((ImageButton)v.findViewById(R.id.imageButtonLeft)).setOnClickListener(clickListener);
        ((ImageButton)v.findViewById(R.id.imageButtonStop)).setOnClickListener(clickListener);
        ((ImageButton)v.findViewById(R.id.imageButtonRight)).setOnClickListener(clickListener);

        ((ImageButton)v.findViewById(R.id.imageButtonLeftDown)).setOnClickListener(clickListener);
        ((ImageButton)v.findViewById(R.id.imageButtonDown)).setOnClickListener(clickListener);
        ((ImageButton)v.findViewById(R.id.imageButtonRightDown)).setOnClickListener(clickListener);

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (clientThread != null) {
            clientThread.cancel();
            clientThread = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (clientThread != null) {
            clientThread.cancel();
            clientThread = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(clientThread == null) {
            clientThread = new ClientThread(bluetoothDevice, communicatorService);
            clientThread.start();
        }
    }
}
