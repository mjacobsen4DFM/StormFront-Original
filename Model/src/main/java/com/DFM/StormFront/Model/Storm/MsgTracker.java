package com.DFM.StormFront.Model.Storm;

import com.DFM.StormFront.Util.DateTimeUtil;
import com.DFM.StormFront.Util.LogUtil;

/**
 * Created by Mick on 9/19/2016.
 */
public class MsgTracker {
    public String key = "";
    public long msStart = 0;
    public long msDelay = 0;
    protected long msCompletion = 0;
    protected long msDuration = 0;
    public String delim = "~";

    public MsgTracker(String key) {
        if (key.contains(this.delim)) {
            this.fromString(key);
        } else {
            this.key = key;
            this.msStart = System.currentTimeMillis();
            this.msDelay = 0;
            this.msCompletion = 0;
            this.msDuration = 0;
        }
    }

    private void fromString(String key) {
        String[] msgArray = key.split(this.delim);
        this.key = msgArray[0];
        this.msStart = Long.parseLong(msgArray[1]);
    }

    public void NewStart(){
         this.NewStart(System.currentTimeMillis());
    }

    public void NewStart(long ms){
        this.msStart = ms;
    }

    public boolean Ready(){
        return msStart - 1000 < System.currentTimeMillis();
    }

    public boolean TooFast(Integer msDelay){
        boolean bTooFast = this.getMsDuration() < msDelay;
        if( bTooFast ){
            this.msDelay = msDelay - this.getMsDuration();
            LogUtil.log(String.format("Prev START: %s", DateTimeUtil.MilliSecondsToDateISO8601(this.msStart)));
            LogUtil.log(String.format("Prev ACK: %s", DateTimeUtil.MilliSecondsToDateISO8601(this.getMsCompletion())));
            this.NewStart(this.msStart + this.msDelay);
            LogUtil.log(String.format("Next START: %s", DateTimeUtil.MilliSecondsToDateISO8601(this.msStart)));
        }
        return bTooFast;
    }

    public long getMsCompletion(){
        if ( this.msCompletion == 0 ){
            this.msCompletion = System.currentTimeMillis();
        }
        return this.msCompletion;
    }

    public long getMsDuration(){
        if ( this.msDuration == 0 ){
            this.msDuration = this.getMsCompletion() - this.msStart;
        }
        return this.msDuration;
    }

    @Override
    public String toString() {
        return String.format("%s%s%s", this.key, this.delim, this.msStart);
    }
}
