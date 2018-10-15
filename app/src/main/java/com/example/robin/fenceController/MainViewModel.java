package com.example.robin.fenceController;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    private MutableLiveData<String> mResponse;
    private MutableLiveData<String> mSnack;
    private MutableLiveData<String> mNum1;
    private MutableLiveData<String> mNum2;
    private MutableLiveData<String> mTarget;
    private MutableLiveData<Boolean> mConnected;
    public MutableLiveData<String> getResponse() {
        if (mResponse == null) {
            mResponse = new MutableLiveData<String>();
        }
        return mResponse;
    }
    public MutableLiveData<String> getSnack() {
        if (mSnack == null) {
            mSnack = new MutableLiveData<String>();
        }
        return mSnack;
    }
    public MutableLiveData<String> getNum1() {
        if (mNum1 == null) {
            mNum1 = new MutableLiveData<String>();
        }
        return mNum1;
    }
    public MutableLiveData<String> getNum2() {
        if (mNum2 == null) {
            mNum2 = new MutableLiveData<String>();
        }
        return mNum2;
    }
    public MutableLiveData<String> getTarget() {
        if (mTarget == null) {
            mTarget = new MutableLiveData<String>();
        }
        return mTarget;
    }
    public MutableLiveData<Boolean> getConnected() {
        if (mConnected == null) {
            mConnected = new MutableLiveData<Boolean>();
        }
        return mConnected;
    }

}
