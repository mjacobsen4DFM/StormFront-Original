package com.DFM.StormFront.Model.Storm;

/**
 * Created by Mick on 9/19/2016.
 */
public class MsgTracker {
    public String key = "";
    public long msStart = 0;
    public long msDelay = 0;
    protected long completion = 0;
    protected long duration = 0;
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

    public void NewStart(){
         this.NewStart(System.currentTimeMillis());
    }

    public void NewStart(long ms){
        this.msStart = ms;
    }

    public boolean Ready(){
        return msStart < this.getCompletion();
    }

    public boolean TooFast(Integer msDelay){
        boolean bTooFast = this.getDuration() < msDelay;
        if( bTooFast ){
            this.msDelay = msDelay - this.getDuration();
            this.NewStart(this.getCompletion() + this.msDelay);
        }
        return bTooFast;
    }

    public long getCompletion(){
        if ( this.completion == 0 ){
            this.completion = System.currentTimeMillis();
        }
        return this.duration;
    }

    public long getDuration(){
        if ( this.duration == 0 ){
            this.duration = this.getCompletion() - this.msStart;
        }
        return this.duration;
    }

    @Override
    public String toString() {
        return String.format("%s%s%s", this.key, this.delim, this.msStart);
    }
}
