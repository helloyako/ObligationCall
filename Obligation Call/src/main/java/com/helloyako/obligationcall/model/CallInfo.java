package com.helloyako.obligationcall.model;

/**
 * Created by helloyako on 2014. 5. 18..
 *
 */
public class CallInfo {
    private int index;
    private long callTime;
    private boolean isDone;

    public CallInfo(int index, long callTime, boolean isDone) {
        this.index = index;
        this.isDone = isDone;
        this.callTime = callTime;
    }

    public int getIndex() {
        return index;
    }


    public long getCallTime() {
        return callTime;
    }


    public boolean isDone() {
        return isDone;
    }

}
