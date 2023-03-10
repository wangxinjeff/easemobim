package com.hyphenate.easemob.imlibs.thirdpart;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import java.lang.ref.WeakReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Memory safer implementation of android.os.Handler
 * <p/>
 * Original implementation of Handlers always keeps hard reference to handler in queue of execution.
 * If you create anonymous handler and post delayed message into it, it will keep all parent class
 * for that time in memory even if it could be cleaned.
 * <p/>
 * This implementation is trickier, it will keep WeakReferences to runnables and messages,
 * and GC could collect them once WeakHandler instance is not referenced any more
 * <p/>
 *
 * @see Handler
 * <p/>
 * Created by Dmytro Voronkevych on 17/06/2014.
 *
 *
 * git:
 * https://github.com/badoo/android-weak-handler/blob/master/src/main/java/com/badoo/mobile/util/WeakHandler.java
 */
public class WeakHandler {
	private final Handler.Callback mCallback; // hard reference to Callback. We need to keep callback in memory
	private final ExecHandler mExec;
	private Lock mLock = new ReentrantLock();

	final ChainedRef mRunnable = new ChainedRef(mLock, null);

	/**
	 * Default constructor associates this handler with the {@link Looper} for the current Thread
	 * <p/>
	 * If this thread does not have a looper, this handler won't be able to receive messages
	 * so an exception is thrown.
	 */
	public WeakHandler(){
		mCallback = null;
		mExec = new ExecHandler();
	}

	/**
	 * Constructor associates this handler with the {@link Looper} for the
	 * current thread and takes a callback interface in which you can handle messages.
	 * <p/>
	 * If this thread does not have a looper, this handler won't be able to receive messages
	 * so an exception is thrown.
	 * @param callback The callback interface in which to handle messages, or null.
	 */
	public WeakHandler(Handler.Callback callback){
		mCallback = callback;
		mExec = new ExecHandler(new WeakReference<>(callback)); // Weak referencing inside ExecHandler
	}

	/**
	 * Use the provider {@link Looper} instead of the default one.
	 * @param looper The looper, must not be null.
	 */
	public WeakHandler(Looper looper){
		mCallback = null;
		mExec = new ExecHandler(looper);
	}

	/**
	 * Use the provider {@link Looper} instead of the default one and take a callback
	 * interface in which to handle messages.
	 *
	 * @param looper The looper, must not be null.
	 * @param callback The callback interface in which to handle messages, or null.
	 */
	public WeakHandler(Looper looper, Handler.Callback callback){
		mCallback = callback;
		mExec = new ExecHandler(looper, new WeakReference<>(callback));
	}

	/**
	 * Causes the Runnable r to be added to the message queue.
	 * The runnable will be run on the thread to which this handler is attached.
	 *
	 * @param r The Runnable that will be executed.
	 * @return Returns true if the Runnable was successfully placed in to
	 * message queue. Returns false on failure, usually because the
	 * looper processing the message queue is exiting.
	 */
	public final boolean post(Runnable r) { return mExec.post(wrapRunnable(r));}

	/**
	 * Causes the Runnable r to be added to the message queue, to be run
	 * at a specific time given by <var>uptimeMills</var>.
	 * <b>The time-base is @{link android.os.SystemClock#uptimeMillis.}</b>
	 * The runnable will be run on the thread to which this handler is attached.
	 *
	 * @param r The runnable that will be executed.
	 * @param uptimeMillis The absolute time at which the callback should run,
	 *                     using the @{link android.os.SystemClock#uptimeMillis} time-base.
	 * @return Returns true if the Runnable was successfully placed in to the message queue.
	 * Returns false on failure, usually because the looper processing the message queue is exiting.
	 * Note that a result of true does not mean the Runnable will be processed -- if the looper is
	 * quit before the delivery time of the message occurs then the message will be dropped.
	 */
	public final boolean postAtTime(Runnable r, long uptimeMillis){
		return mExec.postAtTime(wrapRunnable(r), uptimeMillis);
	}

	/**
	 * Cause the Runnable r to be added to the message queue, to be run at a specific the
	 * given by <var>uptimeMillis</var>.
	 * <b>The time-base is @{@link SystemClock#uptimeMillis} </b>
	 * The runnable will be run on the thread to which this handler is attached.
	 *
	 * @param r The Runnable that will be executed.
	 * @param uptimeMillis The absolute time at which the callback should run,
	 *      using the {@link SystemClock#uptimeMillis} time-base.
	 * @return Return true if the Runnable was successfully placed in to the
	 * message queue. Return false on failure, usually because the looper processing the message
	 * queue is exiting. Note that a result of true does not mean the Runnable will be processed -- if
	 * the looper is quit before the delivery time of the message occurs then the message will be dropped.
	 * @see SystemClock#uptimeMillis
	 */
	public final boolean postAtTime(Runnable r, Object token, long uptimeMillis){
		return mExec.postAtTime(wrapRunnable(r), token, uptimeMillis);
	}

	/**
	 * Causes the Runnable r to be added to the message queue, to be run after the specified amount of time elapses.
	 * The runnable will be run on the thread to which this handler is attached.
	 * @param r The Runnable that will be executed.
	 * @param delayMillis The delay (in milliseconds) until the Runnable will be executed.
	 * @return Returns true if the Runnable was successfully placed in to the message queue.
	 * Returns false on failure, usually because the looper processing the message queue is exiting. Note
	 * that a result of true does not mean the Runnable will be processed -- if the looper is quit
	 * before the delivery time of the message occurs then the message will be dropped.
	 */
	public final boolean postDelay(Runnable r, long delayMillis){
		return mExec.postDelayed(wrapRunnable(r), delayMillis);
	}

	/**
	 * Posts a message to an object that implements Runnable.
	 * Cause the Runnable r to executed on the next iteration through the
	 * message queue. The runnable will be run on the thread to which this handler is attached.
	 * <b>This method is only for use in very special circumstances</b>
	 * @param r
	 * @return
	 */
	public final boolean postAtFrontOfQueue(Runnable r){
		return mExec.postAtFrontOfQueue(wrapRunnable(r));
	}

	/**
	 * Remove any pending posts of Runnable r that are in the message queue.
	 * @param r
	 */
	public final void removeCallbacks(Runnable r){
		final WeakRunnable runnable = mRunnable.remove(r);
		if (runnable != null) {
			mExec.removeCallbacks(runnable);
		}
	}

	/**
	 * Remove any pending posts of Runnable <var>r</var> with Object
	 * <var>token</var> that are in the message queue. If <var>token</var> is null,
	 * all callbacks will be removed.
	 */
	public final void removeCallbacks(Runnable r, Object token){
		final WeakRunnable runnable = mRunnable.remove(r);
		if(runnable != null) {
			mExec.removeCallbacks(runnable, token);
		}
	}

	/**
	 * Pushes a message onto the end of the message queue after all pending messages
	 * before the current time. It will be received in callback,
	 * in the thread attached to this handler.
	 * @return Returns true if the message was successfully placed in to the
	 * message queue. Returns false on failure, usually because the
	 * looper processing the message queue is exiting.
	 */
	public final boolean sendMessage(Message msg) {
		return mExec.sendMessage(msg);
	}

	/**
	 * Sends a Message containing only the what value.
	 *
	 * @return Returns true if the message was successfully placed in to the
	 * message queue. Returns false on failure, usually because the
	 * looper processing the message queue is exiting.
	 */
	public final boolean sendEmptyMessage(int what) {
		return mExec.sendEmptyMessage(what);
	}

	/**
	 * Send a Message containing only the what value, to be delivered
	 * after the specified amount of time elapses.
	 *
	 * @return Returns true if the message was successfully placed in to the
	 * message queue. Returns false on failure, usually because the
	 * looper processing the message queue is exiting.
	 * @see #sendMessageDelayed(Message, long)
	 */
	public final boolean sendEmptyMessageDelayed(int what, long delayMillis){
		return mExec.sendEmptyMessageDelayed(what, delayMillis);
	}


	/**
	 * Sends a Message containing only the what value, to be delivered at a specific time.
	 *
	 * @return Returns true if the message was successfully placed in to the
	 * message queue. Returns false on failure, usually because the
	 * looper processing the message queue is exiting.
	 * @see #sendMessageAtTime(Message, long)
	 */
	public final boolean sendEmptyMessageAtTime(int what, long uptimeMillis){
		return mExec.sendEmptyMessageAtTime(what, uptimeMillis);
	}

	/**
	 * Enqueue a message into the message queue after all pending messages
	 * before (current time + delayMillis). You will receive it in
	 * callback, in the thread attached to this handler.
	 * @return Returns true if the message was successfully placed in to the
	 * message queue. Returns false on failure, usually because the looper
	 * processing the message queue is exiting. Note that a
	 * result of true does not mean the message will be processed -- if
	 * occurs then the message will be dropped.
	 */
	public final boolean sendMessageDelayed(Message msg, long delayMillis){
		return mExec.sendMessageDelayed(msg, delayMillis);
	}

	/**
	 * Enqueue a message into the message queue after all pending messages
	 * before the absolute time (in milliseconds)<var>uptimeMillis</var>.
	 * <b>The time-base is {@link SystemClock#uptimeMillis()}.</b>
	 * You will receive it in callback, in the thread attached to this handler.
	 * @param uptimeMillis The absolute time at which the message should be delivered,
	 *        using the {@link SystemClock#uptimeMillis()} time-base.
	 * @return Returns true if the message was successfully placed in to the
	 * message queue. Returns false on failure, usually because the
	 * looper processing the message queue is exiting. Note that a
	 * result of true does not mean the message will be processed -- if
	 * the looper is quit before the delivery time of the message
	 * occurs then the message will be dropped.
	 */
	public boolean sendMessageAtTime(Message msg, long uptimeMillis){
		return mExec.sendMessageAtTime(msg, uptimeMillis);
	}

	/**
	 * Enqueue a message at the front of the message queue, to be processed on
	 * the next iteration of the message loop. You will receive it in callback,
	 * in the thread attached to this handler.
	 * <b>This method is only for use in very special circumstances -- it can easily starve the message queue,
	 * cause ordering problems, or have other unexpected side-effects.</b>
	 * @return Returns true if the message was successfully placed in to the
	 * message queue. Returns false on failure, usually because the looper
	 * processing the message queue is exiting.
	 */
	public final boolean sendMessageAtFrontOfQueue(Message msg){
		return mExec.sendMessageAtFrontOfQueue(msg);
	}

	/**
	 * Remove any pending posts of messages with code 'what' that are in the message queue.
	 * @param what
	 */
	public final void removeMessages(int what) { mExec.removeMessages(what); }

	/**
	 * Remove any pending posts of messages with code 'what' and whose obj is 'object'
	 * that are in the message queue. If <var>object</var> is null,
	 * all messages will be removed.
	 */
	public final void removeMessages(int what, Object object) {
		mExec.removeMessages(what, object);
	}

	/**
	 * Remove any pending posts of callbacks and sent messages whose
	 * <var>obj</var> is <var>token</var>. If <var>token</var> is null,
	 * all callbacks and messages will be removed.
	 * @param token
	 */
	public final void removeCallbacksAndMessages(Object token) {
		mExec.removeCallbacksAndMessages(token);
	}

	/**
	 * Check if there are any pending posts of messages with code 'what' in
	 * the message queue.
	 */
	public final boolean hasMessages(int what) {
		return mExec.hasMessages(what);
	}

	/**
	 * Check if there are any pending posts of messages with code 'what'
	 * and whose obj is 'object' in the message queue.
	 */
	public final boolean hasMessages(int what, Object object){
		return mExec.hasMessages(what, object);
	}

	public final Looper getLooper(){
		return mExec.getLooper();
	}

	private WeakRunnable wrapRunnable(Runnable r) {
		if (r == null) {
			throw new NullPointerException("Runnable can't be null");
		}
		final ChainedRef hardRef = new ChainedRef(mLock, r);
		mRunnable.insertAfter(hardRef);
		return hardRef.wrapper;
	}

	private static class ExecHandler extends Handler {
		private final WeakReference<Callback> mCallback;

		ExecHandler(){
			mCallback = null;
		}
		ExecHandler(WeakReference<Callback> callback){
			mCallback = callback;
		}

		ExecHandler(Looper looper){
			super(looper);
			mCallback = null;
		}

		ExecHandler(Looper looper, WeakReference<Callback> callback) {
			super(looper);
			mCallback = callback;
		}

		@Override
		public void handleMessage(Message msg) {
			if (mCallback == null){
				return;
			}
			final Callback callback = mCallback.get();
			if (callback == null) {
				return;
			}
			callback.handleMessage(msg);
		}
	}


	static class WeakRunnable implements Runnable {
		private final WeakReference<Runnable> mDelegate;
		private final WeakReference<ChainedRef> mReference;

		WeakRunnable(WeakReference<Runnable> delegate, WeakReference<ChainedRef> reference) {
			mDelegate = delegate;
			mReference = reference;
		}

		@Override
		public void run() {
			final Runnable delegate = mDelegate.get();
			final ChainedRef reference = mReference.get();
			if (reference != null){
				reference.remove();
			}
			if (delegate != null){
				delegate.run();
			}
		}
	}

	static class ChainedRef{
		ChainedRef next;
		ChainedRef prev;
		final Runnable runnable;
		final WeakRunnable wrapper;

		Lock lock;

		public ChainedRef(Lock lock, Runnable r){
			this.runnable = r;
			this.lock = lock;
			this.wrapper = new WeakRunnable(new WeakReference<Runnable>(r), new WeakReference<ChainedRef>(this));
		}

		public WeakRunnable remove(){
			lock.lock();
			try {
				if (prev != null){
					prev.next = next;
				}
				if (next != null){
					next.prev = prev;
				}
				prev = null;
				next = null;
			}finally {
				lock.unlock();
			}
			return wrapper;
		}

		public void insertAfter(ChainedRef candidate){
			lock.lock();
			try {
				if (this.next != null){
					this.next.prev = candidate;
				}
				candidate.next = this.next;
				this.next = candidate;
				candidate.prev = this;
			}finally {
				lock.unlock();
			}
		}

		public WeakRunnable remove(Runnable obj){
			lock.lock();
			try {
				ChainedRef curr = this.next;
				while (curr != null){
					if (curr.runnable == obj){
						return curr.remove();
					}
					curr = curr.next;
				}
			}finally {
				lock.unlock();
			}
			return null;
		}




	}


}
