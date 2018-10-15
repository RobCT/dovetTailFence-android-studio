package com.example.robin.fenceController;
import android.bluetooth.*;
import android.content.Context;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class blue {
    private BluetoothAdapter mBluetoothAdapter;
    private static final UUID SUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private AcceptThread mSecureAcceptThread;
    private TextView resp;
    //private ConnectedThread mConnectedThread;
    private String MACA = "98:D3:31:20:A2:45";
    private static final String TAG = "Blue";
    private int mState;
    private Context context;
    private MainViewModel mModel;
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    public blue(MainViewModel mod) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mModel = mod;

    }
    public void getConnection(TextView response) {
        resp = response;
        System.out.println(TAG + " " + "BT not enabled");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(MACA);

        connect(device);


    }
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;


            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(SUID);

            } catch (IOException e) {
                e.printStackTrace();

                System.out.println(TAG + " " + "create() failed");
            }
            mmSocket = tmp;
            mState = STATE_CONNECTING;
        }
        public void run() {
            System.out.println(TAG + " " + "BEGIN mConnectThread SocketType:" + mSocketType);
            //setName("ConnectThread" + mSocketType);

            // Always cancel discovery because it will slow down a connection
            //mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    System.out.println(TAG + " " + "unable to close() " + mSocketType +
                            " socket during connection failure");
                }
                System.out.println(TAG + " " +  "connect socket failed");
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (blue.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
            int a = 2;
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                System.out.println(TAG + " " +"close() of connect " + mSocketType + " socket failed");
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            System.out.println(TAG + " " + "create ConnectedThread: ");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                System.out.println(TAG + "temp sockets not created");
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            mState = STATE_CONNECTED;
            mModel.getConnected().postValue(true);
        }

        public void run() {
            System.out.println(TAG + " " + "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;
            int term;
            String buf = "";
            // Keep listening to the InputStream while connected
            while (mState == STATE_CONNECTED) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    int a = 1;
                    //System.out.println("last char:" + new String(buffer, bytes , 1));
                    term = new String(buffer).indexOf("\n") ;
                    if (term > -1 && term < bytes -1) {
                        buf = buf + new String(buffer, 0, term);
                        System.out.println(buf);
                        mModel.getResponse().postValue(buf);
                        buf = new String(buffer, term + 1, bytes - term -1);

                    } else {
                        buf = buf + new String(buffer, 0, bytes);
                        buffer = new byte[1024];
                    }
                    if (buf.contains(";")) {

                        System.out.println(buf);
                        mModel.getResponse().postValue(buf);
                        buf = "";
                        buffer = new byte[1024];
                    }

                    // Send the obtained bytes to the UI Activity
                    //mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
                    //        .sendToTarget();
                } catch (IOException e) {
                    System.out.println(TAG + "disconnected");
                    connectionLost();
                    break;
                }
            }
        }
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                //mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer)
                //        .sendToTarget();
            } catch (IOException e) {
                System.out.println(TAG + " " + "Exception during write");
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                System.out.println(TAG + " " +  "close() of connect socket failed");
            }
        }
    }
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        String testout = new String(out);
        // Perform the write unsynchronized
        System.out.println(testout);
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        // Send a failure message back to the Activity
        //Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        //Bundle bundle = new Bundle();
        mModel.getSnack().postValue("Unable to connect device");
        //bundle.putString(SyncStateContract.Constants.TOAST, "Unable to connect device");
        //msg.setData(bundle);
        //mHandler.sendMessage(msg);

        mState = STATE_NONE;
        // Update UI title
        //updateUserInterfaceTitle();

        // Start the service over to restart listening mode
        //start();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
        //Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        mModel.getSnack().postValue("Device connection was lost");
        //bundle.putString(SyncStateContract.Constants.TOAST, "Device connection was lost");
        //msg.setData(bundle);
        //mHandler.sendMessage(msg);

        mState = STATE_NONE;
        mModel.getConnected().postValue(false);
        // Update UI title
        //updateUserInterfaceTitle();

        // Start the service over to restart listening mode
        //start();
    }
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;
        private String mSocketType;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;


            // Create a new listening server socket
            try {
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(MACA, SUID);


            } catch (IOException e) {
                System.out.println(TAG + " " + "Socket Type: listen() failed");
            }
            mmServerSocket = tmp;
            mState = STATE_LISTEN;
        }

        public void run() {
            System.out.println(TAG + " " + "Socket Type: BEGIN mAcceptThread");
            setName("AcceptThread");

            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    System.out.println(TAG + " " + "Socket Type: accept() failed");
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                   // synchronized (BluetoothChatService.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    System.out.println(TAG + " " + "Could not close unwanted socket");
                                }
                                break;
                        }
                    }

            }
            System.out.println(TAG + " " + "END mAcceptThread, socket Type: ");

        }
        public void cancel() {
            System.out.println(TAG + " " + "Socket  cancel " );
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                System.out.println(TAG + " " + "Socket Type" + mSocketType + "close() of server failed");
            }
        }
    }
    public synchronized void connect(BluetoothDevice device) {
        System.out.println(TAG + " " + "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        // Update UI title
        //updateUserInterfaceTitle();
    }
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice   device) {
        System.out.println(TAG + " " +  "connected, Socket Type:" );

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
           mSecureAcceptThread = null;
        }
        //if (mInsecureAcceptThread != null) {
         //   mInsecureAcceptThread.cancel();
        //    mInsecureAcceptThread = null;
        //}

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        //Message msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        //Bundle bundle = new Bundle();
        //bundle.putString(Constants.DEVICE_NAME, device.getName());
        //msg.setData(bundle);
        //mHandler.sendMessage(msg);
        // Update UI title
        //updateUserInterfaceTitle();
    }
    public synchronized void start() {
        System.out.println(TAG + " " + "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        if (mSecureAcceptThread == null) {
            mSecureAcceptThread = new AcceptThread();
            mSecureAcceptThread.start();
        }

        // Update UI title
        //updateUserInterfaceTitle();
    }
    public boolean isConnected() {
        if (mState == STATE_CONNECTED) {
            return true;
        } else { return false; }
    }

}
