package com.jerry.dyloadlib.dyload.util.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池
 *
 * @author chenchongji
 * 2016年3月15日
 */
public class ThreadPool {
    public static final int THREADPOOL_CAPACITY_DEF = 2; //默认线程池容量
    private ExecutorService mThreadPool; //线程池
    private int mThreadCapacity;
    
    public ThreadPool() {
    	this(THREADPOOL_CAPACITY_DEF);
    }
    
    /**
     * 线程池数量
     * @param threadCapacity
     */
    public ThreadPool(int threadCapacity) {
    	mThreadCapacity = threadCapacity;
    	mThreadPool = Executors.newFixedThreadPool(mThreadCapacity);
    }
    
    public void submit(Runnable r) {
        open();
        mThreadPool.submit(r);
    }
    
    /**
     * 关闭线程池,清除已submit但未执行的任务
     */
    public void shutDown() {
//      mThreadPool.shutdown();
        mThreadPool.shutdownNow();
    }
    
    /**
     * 清除已submit，但未执行的任务
     */
    public void clear() {
    	if (mThreadPool instanceof ThreadPoolExecutor) {
    		((ThreadPoolExecutor) mThreadPool).getQueue().clear();
    	}
    }
    
    /**
     * 检查并开启线程池
     */
    private void open() {
        if (null == mThreadPool || mThreadPool.isShutdown()) {
            mThreadPool = Executors.newFixedThreadPool(mThreadCapacity);
        }
    }
}
