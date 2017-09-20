package com.parasme.swopinfo.webservice;

/**
 * Created by :- Mukesh Kumawat on 18-Jan-17.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */

public interface ProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
