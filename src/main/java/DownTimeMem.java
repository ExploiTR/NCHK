import java.io.Serializable;

class DownTimeMem implements Serializable {
    private int fromHr, fromMin, fromSec, toHr, toMin, toSec;
    private long fromMills, toMills;

    static DownTimeMem getTime(int frh, int frm, int frs, int thr, int tmn, int tsc, long fromMills, long toMills) {
        return new DownTimeMem()
                .setFromHr(frh)
                .setFromMin(frm)
                .setFromSec(frs)
                .setToHr(thr)
                .setToMin(tmn)
                .setToSec(tsc)
                .setFromMills(fromMills)
                .setToMills(toMills);
    }

    int getFromHr() {
        return fromHr;
    }

    private DownTimeMem setFromHr(int fromHr) {
        this.fromHr = fromHr;
        return this;
    }

    int getFromMin() {
        return fromMin;
    }

    private DownTimeMem setFromMin(int fromMin) {
        this.fromMin = fromMin;
        return this;
    }

    int getFromSec() {
        return fromSec;
    }

    private DownTimeMem setFromSec(int fromSec) {
        this.fromSec = fromSec;
        return this;
    }

    int getToHr() {
        return toHr;
    }

    private DownTimeMem setToHr(int toHr) {
        this.toHr = toHr;
        return this;
    }

    int getToMin() {
        return toMin;
    }

    private DownTimeMem setToMin(int toMin) {
        this.toMin = toMin;
        return this;
    }

    int getToSec() {
        return toSec;
    }

    private DownTimeMem setToSec(int toSec) {
        this.toSec = toSec;
        return this;
    }

    long getFromMills() {
        return fromMills;
    }

    private DownTimeMem setFromMills(long fromMills) {
        this.fromMills = fromMills;
        return this;
    }

    long getToMills() {
        return toMills;
    }

    private DownTimeMem setToMills(long toMills) {
        this.toMills = toMills;
        return this;
    }
}
