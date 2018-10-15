package com.example.robin.fenceController;

public class Commands {
    public static String SET_HOME = "8;\n";
    public static String SEND_PINS_FIRST = "12,1,";
    public static String SEND_PINS_CONTINUE = "12,2,";
    public static String SEND_TAILS_FIRST = "13,1,";
    public static String SEND_TAILS_CONTINUE = "13,2,";
    public static String SEND_REVERSE_TAILS_FIRST = "20,1,";
    public static String SEND_REVERSE_TAILS_CONTINUE = "20,2,";
    public static String GET_PINS = "18;\n";
    public static String GET_TAILS = "19;\n";
    public static String NEXT_PIN = "16;\n";
    public static String NEXT_TAIL = "14;\n";
    public static String PREV_PIN = "17;\n";
    public static String PREV_TAIL = "15;\n";
    public static String GET_STATUS = "1;\n";
    public static String GET_POSITION = "2,";
    public static String GET_INFO = "3;\n";
    public static String TERMINATOR = "-99;\n";
    public static String REVERSE_TERMINATOR = "99;\n";

}
