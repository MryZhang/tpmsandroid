

package com.ethan.morephone.presentation.buy.payment.checkout;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.Service;
import android.content.Context;
import android.os.Build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

/**
 * Billing API helper class. Can be be used in the context of {@link Activity},
 * {@link Fragment}, {@link Service} or any other UI. In such case its lifespan should
 * be bound to the lifecycle of the bound activity/fragment/service.
 * For example, {@link #start()} and {@link #stop()} methods of this class
 * should be called from the appropriate methods of activity:<br/>
 * <pre>{@code
 * public class MainActivity extends Activity {
 *
 *    private final UiCheckout mCheckout = Checkout.forActivity(this, getCheckout());
 *
 *    private final RequestListener<Purchase> mPurchaseListener = new BillingListener<Purchase>() {
 *        public void onSuccess(Purchase purchase) {
 *            // item was purchased
 *            // ...
 *        }
 *    };
 *
 *    protected void onCreate(Bundle savedInstanceState) {
 *         super.onCreate(savedInstanceState);
 *         // ...
 *         mCheckout.start(new Checkout.EmptyListener() {
 *              public void onReady(BillingRequests requests, String product, boolean supported) {
 *                      if (supported) {
 *                          // billing for a product is supported
 *                          // ...
 *                      }
 *                  }
 *              });
 *
 *         mCheckout.createPurchaseFlow(mPurchaseListener);
 *     }
 *
 *     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 *         super.onActivityResult(requestCode, resultCode, data);
 *         mCheckout.onActivityResult(requestCode, resultCode, data);
 *     }
 *
 *     protected void onDestroy() {
 *         mCheckout.stop();
 *         super.onDestroy();
 *     }
 * }
 * }</pre>
 * <br/>
 * If no more billing information is needed {@link Checkout} can be stopped via {@link #stop()}
 * method call (internally the Billing service might be unbound from the application).
 * If needed {@link #start()} can be used to start {@link Checkout} over. Be aware, though, that
 * {@link #stop()} will cancel all pending requests and remove all previously set listeners.
 * </p>
 * <p>
 * As soon as Billing API is ready for product
 * {@link Listener#onReady(BillingRequests, String, boolean)} is called. If all the products are
 * ready {@link Listener#onReady(BillingRequests)} is called. In case of any error while executing
 * the initial requests {@link Listener#onReady(BillingRequests, String, boolean)} is called with
 * <code>billingSupported=false</code>
 * </p>
 * <p>
 * <b>Note</b>: currently this class can only be used on the main application thread
 * </p>
 */
public class Checkout {

    @Nullable
    private final Object mTag;
    @Nonnull
    protected final Billing mBilling;
    @Nonnull
    final Object mLock = new Object();
    @GuardedBy("mLock")
    @Nonnull
    private final Map<String, Boolean> mSupportedProducts = new HashMap<>();
    @GuardedBy("mLock")
    @Nonnull
    private final Listeners mListeners = new Listeners();
    @Nonnull
    private final OnLoadExecutor mOnLoadExecutor = new OnLoadExecutor();
    @GuardedBy("mLock")
    private Billing.Requests mRequests;
    @GuardedBy("mLock")
    @Nonnull
    private State mState = State.INITIAL;

    Checkout(@Nullable Object tag, @Nonnull Billing billing) {
        mTag = tag;
        mBilling = billing;
    }

    /**
     * @param intentStarter {@link android.content.IntentSender} starter
     * @param tag requests marker
     * @param billing billing instance
     */
    @Nonnull
    public static UiCheckout forUi(@Nonnull IntentStarter intentStarter, @Nonnull Object tag, @Nonnull Billing billing) {
        return new CustomUiCheckout(intentStarter, tag, billing);
    }

    /**
     * <p>{@link Build.VERSION_CODES#N} API version is required by {@link
     * Fragment#startIntentSenderForResult}.</p>
     * <p>There are two possibilities to make it work on older versions:
     * <ul>
     * <li>Either to use {@link #forActivity} and delegate {@link Activity#onActivityResult}
     * to an appropriate fragment</li>
     * <li>Or to use {@link #forUi} implementing {@link IntentStarter} in the following manner:
     * <pre> {@code
     *  private class MyIntentStarter implements IntentStarter {
     *      private final Fragment mFragment;
     *
     *      public MyIntentStarter(Fragment fragment) {
     *          mFragment = fragment;
     *      }
     *
     *      public void startForResult(IntentSender intentSender, int requestCode, Intent intent) throws IntentSender.SendIntentException {
     *          mFragment.startIntentSenderForResult(intentSender, requestCode, intent, 0, 0, 0, null);
     *      }
     *   }
     * }
     * </pre>
     * The reason why {@link IntentStarter} is not implemented in the lib is to avoid support-lib
     * dependency.</li>
     * </ul>
     * </p>
     */
    @TargetApi(Build.VERSION_CODES.N)
    @Nonnull
    public static UiCheckout forFragment(@Nonnull Fragment fragment, @Nonnull Billing billing) {
        return new FragmentCheckout(fragment, billing);
    }

    @Nonnull
    public static ActivityCheckout forActivity(@Nonnull Activity activity, @Nonnull Billing billing) {
        return new ActivityCheckout(activity, billing);
    }

    @Nonnull
    public static Checkout forService(@Nonnull Service service, @Nonnull Billing billing) {
        return new Checkout(service, billing);
    }

    @Nonnull
    public static Checkout forApplication(@Nonnull Billing billing) {
        return new Checkout(null, billing);
    }

    @Nonnull
    Context getContext() {
        return mBilling.getContext();
    }

    /**
     * Same as {@link #start(Listener)} but with no initial request listener.
     */
    public void start() {
        start(null);
    }

    /**
     * Starts this {@link Checkout} and sends an initial request that checks whether billing is
     * supported for each product available in the Billing API.
     *
     * @param listener initial request listener
     */
    public void start(@Nullable final Listener listener) {
        Check.isMainThread();

        synchronized (mLock) {
            Check.isFalse(mState == State.STARTED, "Already started");
            Check.isNull(mRequests, "Already started");
            mState = State.STARTED;
            mBilling.onCheckoutStarted();
            mRequests = mBilling.getRequests(mTag);
            if (listener != null) {
                mListeners.add(listener);
            }
            for (final String product : ProductTypes.ALL) {
                mRequests.isBillingSupported(product, new RequestListener<Object>() {
                    @Override
                    public void onSuccess(@Nonnull Object result) {
                        onBillingSupported(product, true);
                    }

                    @Override
                    public void onError(int response, @Nonnull Exception e) {
                        onBillingSupported(product, false);
                    }
                });
            }
        }
    }

    /**
     * Adds an initial request listener to this {@link Checkout} if the initial request hasn't
     * finished yet or calls appropriate methods of the passed listener if some/all data has
     * already been loaded.
     * Depending on the current state of {@link Checkout} some methods of the passed listener might
     * be called synchronously while other - asynchronously.
     *
     * @param listener listener which is notified about the initial request's results
     */
    public void whenReady(@Nonnull Listener listener) {
        Check.isMainThread();

        synchronized (mLock) {
            for (Map.Entry<String, Boolean> entry : mSupportedProducts.entrySet()) {
                listener.onReady(mRequests, entry.getKey(), entry.getValue());
            }

            if (isReady()) {
                checkIsNotStopped();
                Check.isNotNull(mRequests);
                listener.onReady(mRequests);
            } else {
                // still waiting
                mListeners.add(listener);
            }
        }
    }

    private void checkIsNotStopped() {
        Check.isFalse(mState == State.STOPPED, "Checkout is stopped");
    }

    private boolean isReady() {
        Check.isTrue(Thread.holdsLock(mLock), "Should be called from synchronized block");
        return mSupportedProducts.size() == ProductTypes.ALL.size();
    }

    private void onBillingSupported(@Nonnull String product, boolean supported) {
        synchronized (mLock) {
            mSupportedProducts.put(product, supported);
            mListeners.onReady(mRequests, product, supported);
            if (isReady()) {
                mListeners.onReady(mRequests);
                mListeners.clear();
            }
        }
    }

    /**
     * Creates an {@link Inventory} object related to this {@link Checkout} instance. This
     * method also starts loading data defined by the passed inventory <var>request</var>.
     *
     * @param request  request that defines what data should be loaded
     * @param callback inventory listener
     * @return inventory
     */
    @Nonnull
    public Inventory loadInventory(@Nonnull Inventory.Request request, @Nonnull Inventory.Callback callback) {
        final Inventory inventory = makeInventory();
        inventory.load(request, callback);
        return inventory;
    }

    /**
     * Creates an {@link Inventory} object related to this {@link Checkout} instance. The created
     * {@link Inventory} will use a fall-back {@link Inventory} if it is returned from
     * {@link Billing.Configuration#getFallbackInventory(Checkout, Executor)} method.
     *
     * @return inventory
     */
    @Nonnull
    public Inventory makeInventory() {
        Check.isMainThread();

        synchronized (mLock) {
            checkIsNotStopped();
        }

        final Inventory inventory;
        final Inventory fallbackInventory = mBilling.getConfiguration().getFallbackInventory(this, mOnLoadExecutor);
        if (fallbackInventory == null) {
            inventory = new CheckoutInventory(this);
        } else {
            inventory = new FallingBackInventory(this, fallbackInventory);
        }
        return inventory;
    }

    /**
     * Method clears all listeners and cancels all pending requests. After this method is called no
     * more work can be done with this class unless {@link Checkout#start()} method is called
     * again.
     */
    public void stop() {
        Check.isMainThread();

        synchronized (mLock) {
            mSupportedProducts.clear();
            mListeners.clear();
            if (mState != State.INITIAL) {
                mState = State.STOPPED;
            }
            if (mRequests != null) {
                mRequests.cancelAll();
                mRequests = null;
            }
            if (mState == State.STOPPED) {
                mBilling.onCheckoutStopped();
            }
        }
    }

    /**
     * @param product product
     * @return the last loaded value for the given product
     */
    public boolean isBillingSupported(@Nonnull String product) {
        Check.isTrue(ProductTypes.ALL.contains(product), "Product should be added to the products list");
        Check.isTrue(mSupportedProducts.containsKey(product), "Billing information is not ready yet");
        return mSupportedProducts.get(product);
    }

    private enum State {
        INITIAL,
        STARTED,
        STOPPED
    }

    /**
     * Initial request listener, all methods are called on the main application thread
     */
    public interface Listener {
        /**
         * Called when {@link BillingRequests#isBillingSupported(String, RequestListener)} finishes
         * for all the products
         *
         * @param requests requests ready to use
         */
        void onReady(@Nonnull BillingRequests requests);

        /**
         * Called when {@link BillingRequests#isBillingSupported(String, RequestListener)} finishes
         * for a <var>product</var> with <var>billingSupported</var> result
         *
         * @param requests         requests ready to use
         * @param product          product for which check was done
         * @param billingSupported true if billing is supported for <var>product</var>
         */
        void onReady(@Nonnull BillingRequests requests, @Nonnull String product, boolean billingSupported);
    }

    /**
     * Empty implementation of {@link Checkout.Listener}. Any custom listener that cares only
     * about a subset of the methods of {@link Checkout.Listener} can subclass this class and
     * implement only the methods it is interested in.
     */
    public static abstract class EmptyListener implements Listener {
        @Override
        public void onReady(@Nonnull BillingRequests requests) {
        }

        @Override
        public void onReady(@Nonnull BillingRequests requests, @Nonnull String product, boolean billingSupported) {
        }
    }

    private static final class Listeners implements Listener {
        @Nonnull
        private final List<Listener> mList = new ArrayList<>();

        public void add(@Nonnull Listener l) {
            if (!mList.contains(l)) {
                mList.add(l);
            }
        }

        @Override
        public void onReady(@Nonnull BillingRequests requests) {
            final List<Listener> localList = new ArrayList<>(mList);
            mList.clear();
            for (Listener listener : localList) {
                listener.onReady(requests);
            }
        }

        @Override
        public void onReady(@Nonnull BillingRequests requests, @Nonnull String product, boolean billingSupported) {
            for (Listener listener : mList) {
                listener.onReady(requests, product, billingSupported);
            }
        }

        public void clear() {
            mList.clear();
        }
    }

    private final class OnLoadExecutor implements Executor {
        @Override
        public void execute(Runnable command) {
            final Executor executor;
            synchronized (mLock) {
                executor = mRequests != null ? mRequests.getDeliveryExecutor() : null;
            }

            if (executor != null) {
                executor.execute(command);
            } else {
                Billing.error("Trying to deliver result on a stopped checkout.");
            }
        }
    }
}
