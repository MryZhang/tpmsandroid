package com.android.morephone.data.network;


import com.android.morephone.data.entity.BaseResponse;
import com.android.morephone.data.entity.MessageItem;
import com.android.morephone.data.entity.Response;
import com.android.morephone.data.entity.phonenumbers.PhoneNumber;
import com.android.morephone.data.entity.purchase.MorePhonePurchase;
import com.android.morephone.data.entity.register.BindingRequest;
import com.android.morephone.data.entity.usage.UsageItem;
import com.android.morephone.data.entity.user.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by AnPEthan on 8/11/2016.
 */
interface ApiMorePhonePath {

    /*-----------------------------------------USER-----------------------------------------*/
    @POST("user")
    Call<BaseResponse<User>> createUser(@Body User user);

    @PUT("user/{id}/token")
    Call<BaseResponse<User>> updateFcmToken(@Path("id") String id, @Query("token") String token);

    @PUT("user/{id}/forward")
    Call<BaseResponse<User>> updateForward(@Path("id") String id, @Query("forward_phone_number") String forwardPhoneNumber, @Query("forward_email") String forwardEmail);

    @PUT("user/{id}/forward/enable")
    Call<BaseResponse<User>> enableForward(@Path("id") String id, @Query("is_forward") boolean isEnable);

    /*-----------------------------------------PHONE NUMBER-----------------------------------------*/

    @POST("phone-number")
    Call<BaseResponse<PhoneNumber>> createPhoneNumber(@Body PhoneNumber user);

    @DELETE("phone-number/{id}")
    Call<BaseResponse<String>> deletePhoneNumber(@Path("id") String id,
                                                      @Query("account_sid") String accountSid,
                                                      @Query("auth_token") String authToken);

    /*-----------------------------------------MESSAGE-----------------------------------------*/

    @FormUrlEncoded
    @POST("message/send-message")
    Call<BaseResponse<MessageItem>> createMessage(@Field("account_sid") String accountSid,
                                                  @Field("auth_token") String authToken,
                                                  @Field("userId") String userId,
                                                  @Field("from") String from,
                                                  @Field("to") String to,
                                                  @Field("body") String body);


    /*-----------------------------------------CALL-----------------------------------------*/

    @FormUrlEncoded
    @POST("call/token")
    Call<BaseResponse<String>> createToken(@Field("client") String client,
                                           @Field("account_sid") String accountSid,
                                           @Field("auth_token") String authToken,
                                           @Field("application_sid") String application_sid);

    /*-----------------------------------------USAGE-----------------------------------------*/

    @GET("usage/{id}")
    Call<BaseResponse<UsageItem>> usage(@Path("id") String userId);

    /*-----------------------------------------PURCHASE-----------------------------------------*/

    @POST("purchase")
    Call<MorePhonePurchase> purchase(@Body MorePhonePurchase morePhonePurchase);


    /*-----------------------------------------PHONE REGISTER-----------------------------------------*/
    @POST("phone/register-application")
    Call<Response> registerApplication(@Query("incoming_phone_number_sid") String incomingPhoneNumberSid);

    @POST("phone/binding")
    Call<Response> binding(@Body BindingRequest bindingRequest);


}
