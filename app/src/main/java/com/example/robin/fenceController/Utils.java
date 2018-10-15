package com.example.robin.fenceController;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Utils {
    private static MainViewModel mModel = null;
    private blue blu = null;
    private Float home = 0f;
    private Float actual = 0f;
    private Float target = 0f;
    private Float prevPos = 0f;
    private static final int ENCSTEPS = 8300;
    private static final  int MAX_LENGTH = 350;
    private boolean isPins = false;
    private boolean isTails = false;
    DecimalFormat posvals = new DecimalFormat("0.00");

    public Utils(MainViewModel mod, blue bl) {
        mModel = mod;
        blu = bl;


    }
    public void sendPins(ArrayList<Float> pins) {
        isPins = true;
        isTails = false;
        String send_msg = Commands.SEND_PINS_FIRST;
        int chunk = (int) pins.size()/5;
        for (int i = 0; i < chunk; i++) {
            for (int j = 0; j < 5; j++) {
                send_msg = send_msg + posvals.format(Float.valueOf(pins.get(i * 5 + j) ) ) + ",";

            }
            send_msg = send_msg + Commands.TERMINATOR;
            System.out.println(send_msg);
            blu.write(send_msg.getBytes());
            send_msg = Commands.SEND_PINS_CONTINUE;

        }
        int k = pins.size()%5;
        send_msg = Commands.SEND_PINS_CONTINUE;
        if (k > 0 ) {
            for (int l = chunk * 5; l < pins.size(); l++) {
                send_msg = send_msg + posvals.format(Float.valueOf(pins.get(l) ) ) + ",";

            }
            send_msg = send_msg + Commands.TERMINATOR;
            System.out.println(send_msg);
            blu.write(send_msg.getBytes());
        }
        send_msg = Commands.GET_PINS;
        blu.write(send_msg.getBytes());

    }
    public void sendTails(ArrayList<Float> tails) {
        isPins = false;
        isTails = true;
        String send_msg = Commands.SEND_TAILS_FIRST;
        int chunk = (int) tails.size()/5;
        for (int i = 0; i < chunk; i++) {
            for (int j = 0; j < 5; j++) {
                send_msg = send_msg + posvals.format(Float.valueOf(tails.get(i * 5 + j) )  ) + ",";
            }
            send_msg = send_msg + Commands.TERMINATOR;
            System.out.println(send_msg);
            blu.write(send_msg.getBytes());
            send_msg = Commands.SEND_TAILS_CONTINUE;
        }
        int k = tails.size()%5;
        send_msg = Commands.SEND_TAILS_CONTINUE;
        if (k > 0 ) {
            for (int l = chunk * 5; l < tails.size(); l++) {
                send_msg = send_msg + posvals.format(Float.valueOf(tails.get(l) )  ) + ",";
            }
            send_msg = send_msg + Commands.TERMINATOR;
            System.out.println(send_msg);
            blu.write(send_msg.getBytes());

        }
        send_msg = Commands.GET_TAILS;
        blu.write(send_msg.getBytes());
    }
    public void sendReverseTails(ArrayList<Float> tails) {
        isPins = false;
        isTails = true;
        String send_msg = Commands.SEND_REVERSE_TAILS_FIRST;
        int chunk = (int) tails.size()/5;
        for (int i = 0; i < chunk; i++) {
            for (int j = 0; j < 5; j++) {
                send_msg = send_msg + posvals.format(-Float.valueOf(tails.get(i * 5 + j) )  ) + ",";
            }
            send_msg = send_msg + Commands.REVERSE_TERMINATOR;
            System.out.println(send_msg);
            blu.write(send_msg.getBytes());
            send_msg = Commands.SEND_REVERSE_TAILS_CONTINUE;
        }
        int k = tails.size()%5;
        send_msg = Commands.SEND_REVERSE_TAILS_CONTINUE;
        if (k > 0 ) {
            for (int l = chunk * 5; l < tails.size(); l++) {
                send_msg = send_msg + posvals.format(-Float.valueOf(tails.get(l) ) ) + ",";
            }
            send_msg = send_msg + Commands.REVERSE_TERMINATOR;
            System.out.println(send_msg);
            blu.write(send_msg.getBytes());

        }
        send_msg = Commands.GET_TAILS;
        blu.write(send_msg.getBytes());
    }
    public void decodeInput(String msg) {
        String[] dat = null;
        int cmd = msg.indexOf(",");
        int end = msg.indexOf(";");
        int sw = Integer.valueOf(msg.substring(0,cmd).trim());
        String data = msg.substring(cmd + 1, end);
        switch (sw) {
            case 3:
                System.out.println("case 3 "  + msg);
                dat = data.split(",");
                if (dat.length == 8) {
                    mModel.getSnack().postValue("Message Received");
                    target = Float.valueOf(dat[3] );
                    actual = Float.valueOf(dat[0] );
                    mModel.getNum1().postValue(posvals.format(Float.valueOf(dat[3] ) * MAX_LENGTH / ENCSTEPS ));
                    mModel.getNum2().postValue(posvals.format(Float.valueOf(dat[0] ) * MAX_LENGTH / ENCSTEPS ));
                }
                break;
            case 9:
                prevPos = actual = 0f;
                System.out.println("case 9  "  + msg );
                Float testtarg = Float.valueOf(mModel.getTarget().getValue());
                if (testtarg != 0f) {
                    mModel.getTarget().postValue("0.00");
                }
                break;
            default: System.out.println("default  "  + msg );



        }
    }

    public void setHome() {
        blu.write(Commands.SET_HOME.getBytes());
    }
    public void getPins() {
        blu.write(Commands.GET_PINS.getBytes());
    }
    public void getTails() {
        blu.write(Commands.GET_TAILS.getBytes());
    }
    public void status() {
        blu.write(Commands.GET_STATUS.getBytes());
    }
    public void info() {
        blu.write(Commands.GET_INFO.getBytes());
    }
    public void position(String pos) {
        if (blu.isConnected()) {
            String op = Commands.GET_POSITION + posvals.format(Float.valueOf(pos)   ) + ";\n";
            blu.write(op.getBytes());
        }
    }
    public void back3() {
        mModel.getTarget().postValue(posvals.format(Float.valueOf(mModel.getTarget().getValue()) - 10  ));
    }
    public void back2() {
            mModel.getTarget().postValue(posvals.format(Float.valueOf(mModel.getTarget().getValue()) - 1));
    }
    public void back1() {
            mModel.getTarget().postValue(posvals.format(Float.valueOf(mModel.getTarget().getValue()) - 0.1  ));
    }
    public void forward1() {
        mModel.getTarget().postValue(posvals.format(Float.valueOf(mModel.getTarget().getValue()) + 0.1  ));
    }
    public void forward2() {
            mModel.getTarget().postValue(posvals.format(Float.valueOf(mModel.getTarget().getValue()) + 1 ));
    }
    public void forward3() {
        mModel.getTarget().postValue(posvals.format(Float.valueOf(mModel.getTarget().getValue()) + 10  ));
    }
    public void home() { mModel.getTarget().postValue("0.00");}
    public void next() {
        if (isPins) { blu.write(Commands.NEXT_PIN.getBytes());
        } else { blu.write(Commands.NEXT_TAIL.getBytes()); }
    }
    public void prev() {
        if (isPins) { blu.write(Commands.PREV_PIN.getBytes());
        } else { blu.write(Commands.PREV_TAIL.getBytes()); }
    }
}
