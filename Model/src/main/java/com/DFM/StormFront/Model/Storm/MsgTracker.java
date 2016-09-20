package com.DFM.StormFront.Model.Storm;

/**
 * Created by Mick on 9/19/2016.
 */
public class MsgTracker {
    public String key = "";
    public long msStart = 0;
    public String delim = "~";

    public MsgTracker(String key) {
        if (!key.contains(this.delim)) {
            this.key = key;
            this.msStart = System.currentTimeMillis();
        } else {
            this.fromSting(key);
        }
    }

    private void fromSting(String key) {
        String[] msgArray = key.split(this.delim);
        this.key = msgArray[0];
        this.msStart = Long.parseLong(msgArray[1]);
    }

    public boolean Ready(){
        return msStart < System.currentTimeMillis();
    }

    public boolean TooFast(Integer msDelay){
        return System.currentTimeMillis() - this.msStart > msDelay;
    }

    @Override
    public String toString() {
        return String.format("%s%s%s", this.key, this.delim, this.msStart);
    }
}
