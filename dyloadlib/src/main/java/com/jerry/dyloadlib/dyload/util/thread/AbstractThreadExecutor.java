package com.jerry.dyloadlib.dyload.util.thread;


/**
 * 抽象的线程执行器
 *
 * @author chenchongji
 * 2016年3月15日
 */
public abstract class AbstractThreadExecutor {

	protected ThreadPoolManager mManager;

	private byte[] mLock = new byte[0];

	protected abstract ThreadPoolManager initThreadPoolManager();

	protected ThreadPoolManager.ITaskExecuteListener getTaskExecuteListener() {
		return new ThreadPoolManager.ITaskExecuteListener() {

			@Override
			public void beforeExecute(Thread thread, Runnable task) {
				if (task instanceof GoTask) {
					GoTask goTask = (GoTask) task;
					if (goTask.mThreadName != null) {
						thread.setName(goTask.mThreadName);
					}
					thread.setPriority(goTask.mPriority);
				}
			}

			@Override
			public void afterExecute(Runnable task, Throwable throwable) {

			}

		};
	}
	/**
	 * 执行一个异步任务
	 * @param task 需要执行的任务
	 */
	public void execute(Runnable task) {
		if (mManager == null) {
			synchronized (mLock) {
				if (mManager == null) {
					mManager = initThreadPoolManager();
				}
			}
		}
		mManager.execute(task);
	}

	/**
	 * 执行一个异步任务
	 * @param task 需要执行的任务
	 * @param threadName 线程名称
	 */
	public void execute(Runnable task, String threadName) {
		execute(task, threadName, Thread.currentThread().getPriority());
	}

	/**
	 * 执行一个异步任务
	 * @param task 需要执行的任务
	 * @param priority 线程优先级，该值来自于Thread，不是来自于Process；
	 * 如需要设置OS层级的优先级，可以在task.run方法开头调用如Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
	 * Android建议使用OS层级设置优先级，效果更显著
	 */
	public void execute(Runnable task, int priority) {
		execute(task, null, priority);
	}

	/**
	 * 执行一个异步任务
	 * @param task 需要执行的任务
	 * @param threadName 线程名称
	 * @param priority 线程优先级，该值来自于Thread，不是来自于Process；
	 * 如需要设置OS层级的优先级，可以在task.run方法开头调用如Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
	 * Android建议使用OS层级设置优先级，效果更显著
	 */
	public void execute(Runnable task, String threadName, int priority) {
		GoTask goTask = new GoTask(task);
		goTask.mThreadName = threadName;
		goTask.mPriority = priority;
		execute(goTask);
	}

	/**
	 * 取消指定的任务
	 * @param task
	 */
	public void cancel(final Runnable task) {
		if (mManager != null) {
			mManager.cancel(task);
		}
	}

	/**
	 * 销毁对象
	 */
	public void destroy() {
		if (mManager != null) {
			ThreadPoolManager.destroy(mManager.getManagerName());
			mManager = null;
		}
	}

	/**
	 * 
	 *
	 */
	public static class GoTask implements Runnable {

		public Runnable mTask;
		public int mPriority = Thread.NORM_PRIORITY;
		public String mThreadName;

		public GoTask(Runnable task) {
			mTask = task;
		}

		@Override
		public void run() {
			mTask.run();
		}
	}
}
