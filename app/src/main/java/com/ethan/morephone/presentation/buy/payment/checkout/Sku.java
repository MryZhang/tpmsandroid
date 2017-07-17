
package com.ethan.morephone.presentation.buy.payment.checkout;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * SKU object as described <a href="http://developer.android.com/google/play/billing/billing_reference.html#product-details-table">here</a>
 */
@Immutable
public final class Sku {

    @Nonnull
    public final Id id;

    // formatted price of the item, including its currency sign. The price does not include tax.
    // See #detailedPrice for parsed values
    @Nonnull
    public final String price;

    @Nonnull
    public final Price detailedPrice;

    // title of the product
    @Nonnull
    public final String title;

    // description of the product
    @Nonnull
    public final String description;

    @Nullable
    private String mDisplayTitle;

    public Sku(@Nonnull String product, @Nonnull String code, @Nonnull String price, @Nonnull Price detailedPrice, @Nonnull String title, @Nonnull String description) {
        this.id = new Id(product, code);
        this.price = price;
        this.detailedPrice = detailedPrice;
        this.title = title;
        this.description = description;
    }

    Sku(@Nonnull String json, @Nonnull String product) throws JSONException {
        final JSONObject object = new JSONObject(json);
        id = new Id(product, object.getString("productId"));
        price = object.getString("price");
        detailedPrice = Price.fromJson(object);
        title = object.getString("title");
        description = object.optString("description");
    }

    @Nonnull
    static Sku fromJson(@Nonnull String json, @Nonnull String product) throws JSONException {
        return new Sku(json, product);
    }

    @Nonnull
    public String toJson() throws JSONException {
        return toJsonObject().toString();
    }

    @Nonnull
    private JSONObject toJsonObject() throws JSONException {
        final JSONObject json = new JSONObject();
        json.put("productId", id.code);
        json.put("price", price);
        json.put("price_amount_micros", detailedPrice.amount);
        json.put("price_currency_code", detailedPrice.currency);
        json.put("title", title);
        json.put("description", description);
        return json;
    }

    @Override
    public String toString() {
        return id + "{" + getDisplayTitle() + ", " + price + "}";
    }

    /**
     * @return {@link #title} without application name in it (the last group of characters
     * surrounded by brackets is removed)
     */
    @Nonnull
    public String getDisplayTitle() {
        if (mDisplayTitle == null) {
            mDisplayTitle = makeDisplayTitle(title);
        }
        return mDisplayTitle;
    }

    @Nonnull
    private static String makeDisplayTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return "";
        }
        final char lastChar = title.charAt(title.length() - 1);
        if (lastChar != ')') {
            return title;
        }
        final int i = indexOfAppName(title);
        if (i > 0) {
            return title.substring(0, i).trim();
        }
        return title;
    }

    /**
     * This method assumes that SKU's title has the following format: "$title$ ($app_name$)", where
     * $title$ is the SKU's name and $title$ is the application name.
     * @param title SKU's title
     * @return position in the title where application name begins
     */
    private static int indexOfAppName(String title) {
        int depth = 0;
        for (int i = title.length() - 1; i >= 0; i--) {
            final char c = title.charAt(i);
            if (c == ')') {
                depth++;
            } else if (c == '(') {
                depth--;
            }
            if (depth == 0) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Sku that = (Sku) o;
        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public boolean isInApp() {
        return id.isInApp();
    }

    public boolean isSubscription() {
        return id.isSubscription();
    }

    public static final class Id {
        // either “inapp” for in-apps or "subs" for subscriptions.
        public final String product;
        // SKU code
        public final String code;

        public Id(String product, String code) {
            this.product = product;
            this.code = code;
        }

        public boolean isInApp() {
            return product.equals(ProductTypes.IN_APP);
        }

        public boolean isSubscription() {
            return product.equals(ProductTypes.SUBSCRIPTION);
        }

        @Override
        public String toString() {
            return product + "/" + code;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final Id that = (Id) o;

            if (!product.equals(that.product)) return false;
            return code.equals(that.code);

        }

        @Override
        public int hashCode() {
            int result = product.hashCode();
            result = 31 * result + code.hashCode();
            return result;
        }
    }

        /**
     * Contains detailed information about SKU's price as described <a
     * href="http://developer.android.com/google/play/billing/billing_reference.html#getSkuDetails">here</a>
     */
    public static final class Price {

        @Nonnull
        public static final Price EMPTY = new Price(0, "");

        // price in micro-units, where 1,000,000 micro-units equal one unit of the currency.
        // For example, if price is "€7.99", price_amount_micros is "7990000"
        public final long amount;

        // ISO 4217 currency code for price. For example, if price is specified in British pounds
        // sterling, price_currency_code is "GBP"
        @Nonnull
        public final String currency;

        public Price(long amount, @Nonnull String currency) {
            this.amount = amount;
            this.currency = currency;
        }

        @Nonnull
        private static Price fromJson(@Nonnull JSONObject json) throws JSONException {
            final long amount = json.optLong("price_amount_micros");
            final String currency = json.optString("price_currency_code");
            if (amount == 0 || TextUtils.isEmpty(currency)) {
                return EMPTY;
            } else {
                return new Price(amount, currency);
            }
        }

        /**
         * @return true if both {@link #amount} and {@link #currency} are valid (non empty)
         */
        public boolean isValid() {
            return amount > 0 && !TextUtils.isEmpty(currency);
        }

        @Override
        public String toString() {
            return currency + amount;
        }
    }
}
