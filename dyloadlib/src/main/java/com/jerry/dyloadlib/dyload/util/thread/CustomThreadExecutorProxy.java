package com.jerry.dyloadlib.dyload.util.thread;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.MessageQueue;
import android.os.MessageQueue.IdleHandler;

import com.jerry.dyloadlib.dyload.util.log.Logger;
import com.jerry.dyloadlib.dyload.util.reflect.FieldUtils;
import java.util.concurrent.TimeUnit;

/**
 * 线程执行代理类，用于统一管理线程
 * @author wubinqi
 */
public class CustomThreadExecutorProxy {
	private final static String POOL_NAME = "custom_thread_pool";
	private final static int DEFAULT_CORE_POOL_SIZE = 1;
	private final static int DEFAULT_MAX_POOL_SIZE = 6;
	private final static int KEEP_ALIVE_TIME = 60;
	private final static String ASYNC_THREAD_NAME = "custom-single-async-thread";

	private static CustomThreadExecutorProxy sInstance;

	private CustomThreadExecutor mExecutor;
	private int mCorePoolSize = DEFAULT_CORE_POOL_SIZE;

	private HandlerThread mSingleAsyncThread;
	private Handler mSingleAsyncHandler;
	private Handler mMainHandler;
	private MessageQueue mMsgQueue;

	private CustomThreadExecutorProxy() {
		mCorePoolSize = 2/*CpuManager.getCpuCoreNums() - 1*/;
		if (mCorePoolSize < DEFAULT_CORE_POOL_SIZE) {
			mCorePoolSize = DEFAULT_CORE_POOL_SIZE;
		}
		if (mCorePoolSize > DEFAULT_MAX_POOL_SIZE) {
			mCorePoolSize = DEFAULT_MAX_POOL_SIZE;
		}
		mExecutor = new CustomThreadExecutor();

		mSingleAsyncThread = new HandlerThread(ASYNC_THREAD_NAME);
		mSingleAsyncThread.start();
		mSingleAsyncHandler = new Handler(mSingleAsyncThread.getLooper());

		mMainHandler = new Handler(Looper.getMainLooper());

		//兼容处理非主线程构建的情况
		if (Looper.getMainLooper() == Looper.myLooper()) {
			mMsgQueue = Looper.myQueue();
		} else {
			Object queue = null;
			try {
				queue = FieldUtils.readField(Looper.getMainLooper(), "mQueue");
			} catch (Throwable e) {
				Logger.e("matt", "error->", e);
			}
			if (queue instanceof MessageQueue) {
				mMsgQueue = (MessageQueue) queue;
			} else {
				runOnMainThread(new Runnable() {

					@Override
					public void run() {
						mMsgQueue = Looper.myQueue();
					}
				});
			}
		}
	}

	public static CustomThreadExecutorProxy getInstance() {
		if (sInstance == null) {
			sInstance = new CustomThreadExecutorProxy();
		}
		return sInstance;
	}

	/**
	 * 提交异步任务到线程池中执行
	 * @param task 需要执行的任务
	 */
	public void execute(Runnable task) {
		mExecutor.execute(task);
	}

	/**
	 * 提交异步任务到线程池中执行
	 * @param task 需要执行的任务
	 * @param threadName 线程名称
	 */
	public void execute(Runnable task, String threadName) {
		mExecutor.execute(task, threadName);
	}

	/**
	 * 提交异步任务到线程池中执行
	 * @param task 需要执行的任务
	 * @param priority 线程优先级，该值来自于Thread，不是来自于Process；
	 * 如需要设置OS层级的优先级，可以在task.run方法开头调用如Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
	 * Android建议使用OS层级设置优先级，效果更显著
	 */
	public void execute(Runnable task, int priority) {
		mExecutor.execute(task, priority);
	}

	/**
	 * 提交异步任务到线程池中执行
	 * @param task 需要执行的任务
	 * @param threadName 线程名称
	 * @param priority 线程优先级，该值来自于Thread，不是来自于Process；
	 * 如需要设置OS层级的优先级，可以在task.run方法开头调用如Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
	 * Android建议使用OS层级设置优先级，效果更显著
	 */
	public void execute(Runnable task, String threadName, int priority) {
		mExecutor.execute(task, threadName, priority);
	}

	/**
	 * 取消指定的任务
	 * @param task
	 */
	public void cancel(final Runnable task) {
		mExecutor.cancel(task);
		mSingleAsyncHandler.removeCallbacks(task);
		mMainHandler.removeCallbacks(task);
	}

	/**
	 * 销毁对象
	 */
	public void destroy() {
		mExecutor.destroy();
		mSingleAsyncHandler.removeCallbacksAndMessages(null);
		mMainHandler.removeCallbacksAndMessages(null);
	}

	/**
	 * 提交一个Runable到异步线程队列，该异步线程为单队列
	 *
	 * @param r
	 */
	public void runOnAsyncThread(Runnable r) {
		mSingleAsyncHandler.post(r);
	}

	/**
	 * 提交一个Runable到异步线程队列，该异步线程为单队列
	 * @param r
	 * @param delay
	 */
	public void runOnAsyncThread(Runnable r, long delay) {
		mSingleAsyncHandler.postDelayed(r, delay);
	}

	/**
	 * 提交一个Runable到主线程队列
	 *
	 * @param r
	 */
	public void runOnMainThread(Runnable r) {
		mMainHandler.post(r);
	}

	/**
	 * 提交一个Runable到主线程队列
	 * @param r
	 * @param delay
	 */
	public void runOnMainThread(Runnable r, long delay) {
		mMainHandler.postDelayed(r, delay);
	}

	/**
	 * 提交一个Runnable到主线程空闲时执行
	 * @param r
	 */
	public void runOnIdleTime(final Runnable r) {
		IdleHandler handler = new IdleHandler() {

			@Override
			public boolean queueIdle() {
				r.run();
				return false;
			}
		};
		mMsgQueue.addIdleHandler(handler);
	}

	/**
	 *
	 *
	 */
	private class CustomThreadExecutor extends AbstractThreadExecutor {

		private CustomThreadExecutor() {

		}

		@Override
		protected ThreadPoolManager initThreadPoolManager() {
			ThreadPoolManager manager = ThreadPoolManager.buildInstance(POOL_NAME, mCorePoolSize,
					DEFAULT_MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, false,
					getTaskExecuteListener());
			manager.allowCoreThreadTimeOut(true);
			return manager;
		}
	}
}