package com.intech.topfindprovider.Interface;


import com.intech.topfindprovider.Models.ResponseStk;
import com.intech.topfindprovider.Models.Result;
import com.intech.topfindprovider.Models.ResultStk;
import com.intech.topfindprovider.Models.StkQuery;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST("/stk")
    Call<ResponseStk> stk_push(@Body Map<String,Object> pushStk);

    @POST("/stk_callback")
    Call<ResultStk>  getResponse();

    @GET("/")
    Call<Result>  getResult();

    @POST("/stk/query")
    Call<StkQuery>   stk_Query  (@Body Map<String ,String> stkQuey);








}
