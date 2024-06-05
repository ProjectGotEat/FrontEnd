package com.example.prac7.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    public static Retrofit getRetrofitInstance() { //void가 아니다. Retrofit을 리턴한다. static은 객체를 만들지 않아도 함수만써도 되게한다.
        //리턴 타입 : Retrofit , 혹시 패키지가 다른곳에서도 쓸수있으니까 public
        //객체 생성 안하고 함수를 쓸 수 있게끔 static 설정한다.
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl("http://goteat-goteat-98eb531b.koyeb.app/");
//        builder.baseUrl("http://10.101.11.202:8080/");
        builder.addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        return retrofit;
    }
}