package com.example.robin.fenceController;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private MainViewModel mModel;
    private static final int READ_REQUEST_CODE = 42;
    private static final int ENCSTEPS = 8300;
    private static final  int MAX_LENGTH = 350;
    private static Uri uri;
    final private static ArrayList<Float> pins = new ArrayList<Float>();
    final private static ArrayList<Float> tails = new ArrayList<Float>();
    DecimalFormat posvals = new DecimalFormat("0.00");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final StringBuffer mOutStringBuffer;
        mOutStringBuffer = new StringBuffer("");
        setSupportActionBar(toolbar);
        Button snd = (Button) findViewById(R.id.btnSend);
        final Button cnct = (Button) findViewById(R.id.btnConnect);
        cnct.setBackgroundColor(Color.RED);
        Button get_data = (Button) findViewById(R.id.btnFile);
        Button back3 = (Button) findViewById(R.id.btnBackBig);
        Button back2 = (Button) findViewById(R.id.btnBackMid);
        Button back1 = (Button) findViewById(R.id.btnBackSmall);
        Button forward1 = (Button) findViewById(R.id.btnForwardSmall);
        Button forward2 = (Button) findViewById(R.id.btnForwardMid);
        Button forward3 = (Button) findViewById(R.id.btnForwardBig);
        Button home = (Button) findViewById(R.id.btnHome);
        Button setHome = (Button) findViewById(R.id.btnSetHome);
        Button next = (Button) findViewById(R.id.btnNext);
        Button prev = (Button) findViewById(R.id.btnPrev);
        Button sendPins = (Button) findViewById(R.id.btnPins);
        Button sendTails = (Button) findViewById(R.id.btnTails);
        Button sendReverseTails = (Button) findViewById(R.id.btnReverseTails);
        final TextView msg = (TextView) findViewById(R.id.txtSend);
        final TextView resp = (TextView) findViewById(R.id.txtResp);
        final TextView num1 = (TextView) findViewById(R.id.txtNum1);
        final TextView num2 = (TextView) findViewById(R.id.txtNum2);
        msg.setText(mOutStringBuffer);
        mModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mModel.getTarget().setValue("0.00");
        final blue bl = new blue(mModel);
        final Utils ut = new Utils(mModel, bl);


        //bl.start();

        cnct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               bl.getConnection(resp);
            }
        });
        get_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performFileSearch();
            }
            });
        snd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = msg.getText().toString();

                if (message.length() > 0) {
                    mModel.getTarget().setValue(message);
                    // Get the message bytes and tell the BluetoothChatService to write
                    //byte[] send = message.getBytes();
                    //bl.write(send);

                    // Reset out string buffer to zero and clear the edit text field
                    mOutStringBuffer.setLength(0);
                    msg.setText(mOutStringBuffer);
                }
            }

        });
        back3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ut.back3();
            }
        });
        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ut.back2();
            }
        });
        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ut.back1();
            }
        });
        forward1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ut.forward1();
            }
        });
        forward2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ut.forward2();
            }
        });
        forward3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ut.forward3();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ut.home();
            }
        });
        setHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ut.setHome();
            }
        });
        sendPins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pins.size() <= 0) { performFileSearch();}

                ut.sendPins(pins);
            }
        });
        sendTails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tails.size() <= 0) { performFileSearch();}
                ut.sendTails(tails);
            }
        });
        sendReverseTails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tails.size() <= 0) { performFileSearch();}
                ut.sendReverseTails(tails);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ut.next();
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ut.prev();
            }
        });





        // Create the observer which updates the UI.
        final Observer<String> respObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newResp) {
                // Update the UI, in this case, a TextView.
                resp.setText(newResp);
                ut.decodeInput(newResp);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mModel.getResponse().observe(this, respObserver);

        final Observer<String> num1Observer = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newNum1) {
                // Update the UI, in this case, a TextView.
                num1.setText(newNum1);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mModel.getNum1().observe(this, num1Observer);
        final Observer<String> num2Observer = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newNum2) {
                // Update the UI, in this case, a TextView.
                num2.setText(newNum2);

            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mModel.getNum2().observe(this, num2Observer);
        final Observer<String> targetObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newTarget) {
                // Update the UI, in this case, a TextView.
                String message = mModel.getTarget().getValue();
                if (message.length() > 0) {
                    // Get the message bytes and tell the BluetoothChatService to write
                    ut.position(message);


                }

            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mModel.getTarget().observe(this, targetObserver);

        final Observer<Boolean> connectedObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean newConnected) {
                // Update the UI, in this case, a TextView.
                if (newConnected) {
                    cnct.setBackgroundColor(Color.GREEN);


                } else { cnct.setBackgroundColor(Color.RED); }

            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mModel.getConnected().observe(this, connectedObserver);
        final Observer<String> snackObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newSnack) {

                Snackbar.make(findViewById(R.id.layContent), newSnack, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mModel.getSnack().observe(this, snackObserver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("text/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                System.out.println( "Uri: " + uri.toString());
                String txt;
                try {
                    txt = readTextFromUri(uri);
                    if (txt.contains("Pins:") && txt.contains("Tails:")) {
                        mModel.getSnack().setValue("Valid file loaded ");
                        int st = txt.indexOf("Pins:") + 5;
                        int ta = txt.indexOf("Tails:");
                        int en = txt.indexOf("END");
                        String pinsect = txt.substring(st + 1, ta - 1);
                        String tailsect = txt.substring(ta + 7, en - 1);
                        String[] ps = pinsect.split(";");
                        String[] ts = tailsect.split(";");
                        for (String p : ps) {
                            pins.add(Float.valueOf(p) * ENCSTEPS / MAX_LENGTH);
                        }
                        for (String t : ts) {
                            tails.add(Float.valueOf(t) * ENCSTEPS / MAX_LENGTH);
                        }
                        System.out.println(pinsect);
                        System.out.println(tailsect);

                    } else {
                        mModel.getSnack().setValue("Bad file - try again ");
                    }
                    System.out.println(txt);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //showImage(uri);
            }
        }
    }
    private String readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(";");
        }
        inputStream.close();
        //parcelFileDescriptor.close();
        return stringBuilder.toString();
    }



}
