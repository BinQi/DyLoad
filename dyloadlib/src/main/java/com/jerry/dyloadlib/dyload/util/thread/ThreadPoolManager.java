package com.jerry.dyloadlib.dyload.util.thread;

import android.os.Build;
import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {

	private static HashMap<String, ThreadPoolManager> sThreadPoolManagerhHashMap = new HashMap<String, ThreadPoolManager>();

	private final static int DEFAULT_COREPOOL_SIZE = 4;
	private final static int DEFAULT_MAXIMUMPOOL_SIZE = 4;
	private final static long DEFAULT_KEEPALIVE_TIME = 0;
	private final static TimeUnit DEFAULT_TIMEUNIT = TimeUnit.SECONDS;
	private ThreadPoolExecutor mWorkThreadPool = null; // 线程池
	private Queue<Runnable> mWaitTasksQueue = null; // 等待任务队列
	private RejectedExecutionHandler mRejectedExecutionHandler = null; // 任务被拒绝执行的处理器
	private Object mLock = new Object();
	private String mName;
	
	private ThreadPoolManager() {
		this(DEFAULT_COREPOOL_SIZE, DEFAULT_MAXIMUMPOOL_SIZE, DEFAULT_KEEPALIVE_TIME,
				DEFAULT_TIMEUNIT, false, null);
	}

	private ThreadPoolManager(int corePoolSize, int maximumPoolSize, long keepAliveTime,
			TimeUnit unit, boolean isPriority, final ITaskExecuteListener listener) {
		mWaitTasksQueue = new ConcurrentLinkedQueue<Runnable>();
		initRejectedExecutionHandler();
		BlockingQueue<Runnable> queue = isPriority
				? new PriorityBlockingQueue<Runnable>(16)
				: new LinkedBlockingQueue<Runnable>(16);
		if (listener == null) {
			mWorkThreadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
					unit, queue, mRejectedExecutionHandler);
		} else {
			mWorkThreadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
					unit, queue, mRejectedExecutionHandler) {
				@Override
				protected void beforeExecute(Thread t, Runnable r) {
					listener.beforeExecute(t, r);
				}

				@Override
				protected void afterExecute(Runnable r, Throwable t) {
					listener.afterExecute(r, t);
				}
			};
		}
	}

	public static ThreadPoolManager getInstance(String threadPoolManagerName) {
		ThreadPoolManager threadPoolManager = null;
		if (threadPoolManagerName != null && !"".equals(threadPoolManagerName.trim())) {
			synchronized (sThreadPoolManagerhHashMap) {
				threadPoolManager = sThreadPoolManagerhHashMap.get(threadPoolManagerName);
				if (null == threadPoolManager) {
					threadPoolManager = new ThreadPoolManager();
					threadPoolManager.mName = threadPoolManagerName;
					sThreadPoolManagerhHashMap.put(threadPoolManagerName, threadPoolManager);
				}
			}
		}
		return threadPoolManager;
	}

	public static ThreadPoolManager buildInstance(String threadPoolManagerName,
			int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
		return buildInstance(threadPoolManagerName, corePoolSize, maximumPoolSize, keepAliveTime,
				unit, false);
	}

	public static ThreadPoolManager buildInstance(String threadPoolManagerName,
			int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			boolean isPriority) {
		return buildInstance(threadPoolManagerName, corePoolSize, maximumPoolSize, keepAliveTime,
				unit, isPriority, null);

	}

	public static ThreadPoolManager buildInstance(String threadPoolManagerName,
			int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			boolean isPriority, ITaskExecuteListener listener) {
		if (threadPoolManagerName == null || "".equals(threadPoolManagerName.trim())
				|| corePoolSize < 0 || maximumPoolSize <= 0 || maximumPoolSize < corePoolSize
				|| keepAliveTime < 0) {
			return null;
		} else {
			ThreadPoolManager threadPoolManager = new ThreadPoolManager(corePoolSize,
					maximumPoolSize, keepAliveTime, unit, isPriority, listener);
			threadPoolManager.mName = threadPoolManagerName;
			synchronized (sThreadPoolManagerhHashMap) {
				sThreadPoolManagerhHashMap.put(threadPoolManagerName, threadPoolManager);
			}
			return threadPoolManager;
		}
	}

	/**
	 * 初始化调度Runable
	 */
	private static class ScheduledRunnable implements Runnable {
		@Override
		public void run() {
			synchronized (sThreadPoolManagerhHashMap) {
				android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
				Collection<ThreadPoolManager> values = sThreadPoolManagerhHashMap.values();
				for (ThreadPoolManager manager : values) {
					manager.executeWaitTask();
				}
			}
		}
	}
	
	private void executeWaitTask() {
		synchronized (mLock) {
			if (hasMoreWaitTask()) {
				Runnable runnable = mWaitTasksQueue.poll();
				if (runnable != null) {
					execute(runnable);
				}
			}
		}
	}
	
	/**
	 * 初始化任务被拒绝执行的处理器的方法
	 */
	private void initRejectedExecutionHandler() {
		mRejectedExecutionHandler = new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				// 把被拒绝的任务重新放入到等待队列中
				synchronized (mLock) {
					mWaitTasksQueue.offer(r);
				}
			}
		};
	}

	/**
	 * 是否还有等待任务的方法
	 * 
	 * @return
	 */
	public boolean hasMoreWaitTask() {
		boolean result = false;
		if (!mWaitTasksQueue.isEmpty()) {
			result = true;
		}
		return result;
	}

	/**
	 * 执行任务的方法
	 * 
	 * @param task
	 */
	public void execute(Runnable task) {
		if (task != null) {
			mWorkThreadPool.execute(task);
		}
	}

	/**
	 * 取消任务
	 * 
	 * @param task
	 */
	public void cancel(Runnable task) {
		if (task != null) {
			synchronized (mLock) {
				if (mWaitTasksQueue.contains(task)) {
					mWaitTasksQueue.remove(task);
				}
			}
			mWorkThreadPool.remove(task);
		}
	}

	public void removeAllTask() {
		// 如果取task过程中task队列数量改变了会抛异常
		try {
			if (!mWorkThreadPool.isShutdown()) {
				BlockingQueue<Runnable> tasks = mWorkThreadPool.getQueue();
				for (Runnable task : tasks) {
					mWorkThreadPool.remove(task);
				}
			}
		} catch (Throwable e) {
			Log.e("ThreadPoolManager", "removeAllTask " + e.getMessage());
		}
	}

	public boolean isShutdown() {
		return mWorkThreadPool.isShutdown();
	}

	/**
	 * 清理方法
	 */
	private void cleanUp() {
		if (!mWorkThreadPool.isShutdown()) {
			try {
				mWorkThreadPool.shutdownNow();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		mRejectedExecutionHandler = null;
//		if (sScheduledExecutorService != null) {
//			if (!sScheduledExecutorService.isShutdown()) {
//				try {
//					sScheduledExecutorService.shutdownNow();
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//			}
//			sScheduledExecutorService = null;
//		}
//		sScheduledRunnable = null;
		synchronized (mLock) {
			mWaitTasksQueue.clear();
		}
	}

	public void setThreadFactory(ThreadFactory factory) {
		mWorkThreadPool.setThreadFactory(factory);
	}

	public void allowCoreThreadTimeOut(boolean allow) {
		if (Build.VERSION.SDK_INT > 8) {
			mWorkThreadPool.allowCoreThreadTimeOut(allow);
		}
	}

	public String getManagerName() {
		return mName;
	}
	
	/**
	 * 销毁全部ThreadPoolManager
	 */
	public static void destroyAll() {
		synchronized (sThreadPoolManagerhHashMap) {
			Set<String> keySet = sThreadPoolManagerhHashMap.keySet();
			if (keySet != null && keySet.size() > 0) {
				ThreadPoolManager threadPoolManager = null;
				for (String key : keySet) {
					threadPoolManager = sThreadPoolManagerhHashMap.get(key);
					if (threadPoolManager != null) {
						threadPoolManager.cleanUp();
					}
				}
			}
			sThreadPoolManagerhHashMap.clear();
		}
	}

	/**
	 * 销毁某个ThreadPoolManager
	 * @param threadPoolManagerName
	 */
	public static void destroy(String threadPoolManagerName) {
		synchronized (sThreadPoolManagerhHashMap) {
			ThreadPoolManager threadPoolManager = sThreadPoolManagerhHashMap
					.get(threadPoolManagerName);
			if (threadPoolManager != null) {
				threadPoolManager.cleanUp();
			}
		}
	}
	
	/**
	 * 
	 *
	 * @author matt
	 * @date: 2015年5月15日
	 *
	 */
	public static interface ITaskExecuteListener {
		public void beforeExecute(Thread thread, Runnable task);
		public void afterExecute(Runnable task, Throwable throwable);
	}
}
