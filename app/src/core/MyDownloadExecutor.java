package core;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyDownloadExecutor extends ScheduledThreadPoolExecutor {
    private int jobCount;

    public MyDownloadExecutor(int nThreads) {
        super(nThreads);
    }

    public synchronized void jobsCountInc() {
        jobCount++;
    }

    public synchronized void jobDone() {
        jobCount--;
        if (jobCount == 0) {
            schedule(() -> {
                        if (getJobCount() == 0) {
                            shutdown();
                        }
                    },
                    3,
                    TimeUnit.SECONDS);
        }
    }

    public synchronized int getJobCount() {
        return jobCount;
    }

    public void retryWithDelay(Runnable runnable) {
        schedule(runnable, 3, TimeUnit.SECONDS);
    }
}
