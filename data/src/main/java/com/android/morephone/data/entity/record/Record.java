package com.android.morephone.data.entity.record;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ethan on 4/5/17.
 */

public class Record {

    public String sid;

    @SerializedName("account_sid")
    public String accountSid;

    @SerializedName("call_sid")
    public String callSid;

    @SerializedName("phone_number")
    public String phoneNumber;

    public String duration;

    @SerializedName("date_created")
    public String dateCreated;

    @SerializedName("api_version")
    public String apiVersion;

    @SerializedName("date_updated")
    public String dateUpdated;

    public String status;

    public String source;

    public int channels;

    public String price;

    @SerializedName("price_unit")
    public String priceUnit;

    public String uri;
}
