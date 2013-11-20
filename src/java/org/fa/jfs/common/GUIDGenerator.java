package org.fa.jfs.common;

import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class GUIDGenerator {

    public static final long TIME_MASK = 0x3FFFFFFFFFFL;
    public static final int NODE_ID_MASK = 0x3F;
    public static final int INCREMENT_MASK = 0x7FFF;
    private final static int nodeIndex;
    private final static Object lock = new Object();
    private static final int NODE_ID_SHIFT = 15; // shift address part
    private static final int TIME_SHIFT = 21; // shift time part
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");

    static {
        lLastCallTime = new AtomicLong(System.currentTimeMillis());
        iIncrement = new AtomicInteger(0);
        int nIndex = 0; //todo
        nodeIndex = (((byte) nIndex) & NODE_ID_MASK) << NODE_ID_SHIFT;
    }

    private static AtomicLong lLastCallTime;
    private static AtomicInteger iIncrement;

    public static long getGUID() {
        return getGUID(nodeIndex);
    }

    private static long getGUID(int nodeIndex) {
        int increment = 0;
        long lCurTime;

        lCurTime = System.currentTimeMillis();
        synchronized (lock) {
            if (lCurTime <= lLastCallTime.get()) {
                if (lCurTime < lLastCallTime.get())  // warning! time was moved to the past
                    lCurTime = lLastCallTime.get(); // correct current time
                increment = iIncrement.incrementAndGet();
            } else {
                iIncrement.set(0);
                increment = 0;
                lLastCallTime.set(lCurTime);
            }
        }
        long lCurTimeCut = lCurTime & TIME_MASK;
        return (lCurTimeCut << (TIME_SHIFT)) | nodeIndex | (increment & INCREMENT_MASK);
    }

    public static String decode(long guid) {
        int iIncrementCut = (int) guid & INCREMENT_MASK;
        int iNodeIdCut = (int) (guid >>> NODE_ID_SHIFT) & NODE_ID_MASK;
        long timeCut = (guid >>> TIME_SHIFT) & TIME_MASK;
        return "Time: " + sdf.format(new Date(timeCut)) + "; Node identifier:" + iNodeIdCut + "; Inc:" + iIncrementCut;
    }
}
