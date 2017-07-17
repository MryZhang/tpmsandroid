
package com.ethan.morephone.presentation.buy.payment.checkout;

import android.os.RemoteException;

import com.android.vending.billing.IInAppBillingService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class ConsumePurchaseRequest extends Request<Object> {

    @Nonnull
    private final String mToken;

    ConsumePurchaseRequest(@Nonnull String token) {
        super(RequestType.CONSUME_PURCHASE);
        mToken = token;
    }

    @Override
    void start(@Nonnull IInAppBillingService service, @Nonnull String packageName) throws RemoteException, RequestException {
        final int response = service.consumePurchase(mApiVersion, packageName, mToken);
        if (!handleError(response)) {
            Billing.waitGooglePlay();
            onSuccess(new Object());
        }
    }

    @Nullable
    @Override
    protected String getCacheKey() {
        return null;
    }
}
