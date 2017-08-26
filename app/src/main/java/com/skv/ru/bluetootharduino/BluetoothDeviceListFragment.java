package com.skv.ru.bluetootharduino;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Константин on 29.05.2017.
 */

public class BluetoothDeviceListFragment extends Fragment {
    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver discoverDevicesReceiver;
    private BroadcastReceiver discoveryFinishedReceiver;

    private ArrayAdapter<BluetoothDevice> listAdapter;
    private final List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();

    private ProgressDialog progressDialog;
    private ListView mDevicesList;
    private Button mButtonFind;

    private static final int REQUEST_ENABLE_BT = 1;

    private final CommunicatorService communicatorService = new CommunicatorService() {
        @Override
        public Communicator createCommunicatorThread(BluetoothSocket socket) {
            return new CommunicatorImpl(socket);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_list, container, false);

        mDevicesList = (ListView)v.findViewById(R.id.deviceslist);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!bluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


        listAdapter = new ArrayAdapter<BluetoothDevice>(getActivity().getBaseContext(), android.R.layout.simple_list_item_1, discoveredDevices) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final BluetoothDevice device = getItem(position);
                ((TextView) view.findViewById(android.R.id.text1)).setText(device.getName());
                ((TextView) v.findViewById(R.id.textFind)).setText(getString(R.string.title_tap_to_connect));
                return view;
            }
        };

        mDevicesList.setAdapter(listAdapter);

        mDevicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                BluetoothDevice deviceSelected = discoveredDevices.get(position);

                Toast.makeText(getActivity().getBaseContext(), "Вы подключились к устройству \"" + discoveredDevices.get(position).getName() + "\"", Toast.LENGTH_SHORT).show();

                Intent intent = MainActivity.newIntent(getActivity(), deviceSelected);
                startActivity(intent);
            }
        });

        mButtonFind = (Button)v.findViewById(R.id.buttonFind);

        mButtonFind.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                discoverDevices(v);
            }
        });

        ((TextView) v.findViewById(R.id.textFind)).setText(getString(R.string.title_no_devices));

        return v;
    }

    public void discoverDevices(View view) {

        discoveredDevices.clear();
        listAdapter.notifyDataSetChanged();

        if (discoverDevicesReceiver == null) {
            discoverDevicesReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();

                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                        if (!discoveredDevices.contains(device)) {
                            discoveredDevices.add(device);
                            listAdapter.notifyDataSetChanged();
                        }
                    }
                }
            };
        }

        if (discoveryFinishedReceiver == null) {
            discoveryFinishedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    mDevicesList.setEnabled(true);
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    Toast.makeText(getActivity().getBaseContext(), "Поиск закончен. Выберите устройство для отправки ообщения.", Toast.LENGTH_LONG).show();
                    getActivity().unregisterReceiver(discoveryFinishedReceiver);
                }
            };
        }

        getActivity().registerReceiver(discoverDevicesReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        getActivity().registerReceiver(discoveryFinishedReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        mDevicesList.setEnabled(false);

        progressDialog = ProgressDialog.show(getActivity(), "Поиск устройств", "Подождите...");

        bluetoothAdapter.startDiscovery();
    }

    @Override
    public void onPause() {
        super.onPause();
        bluetoothAdapter.cancelDiscovery();

        if (discoverDevicesReceiver != null) {
            try {
                getActivity().unregisterReceiver(discoverDevicesReceiver);
            } catch (Exception e) {
                Log.d("MainActivity", "Не удалось отключить ресивер " + discoverDevicesReceiver);
            }
        }

        /*if (clientThread != null) {
            clientThread.cancel();
        }*/
        //if (serverThread != null) serverThread.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        //serverThread = new ServerThread(communicatorService);
        //serverThread.start();

        discoveredDevices.clear();
        listAdapter.notifyDataSetChanged();
    }
}
