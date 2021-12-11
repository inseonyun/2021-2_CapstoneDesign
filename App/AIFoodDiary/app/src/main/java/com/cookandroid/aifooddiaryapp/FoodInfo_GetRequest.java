package com.cookandroid.aifooddiaryapp;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class FoodInfo_GetRequest extends StringRequest{
    // 서버 URL 설정 - php 파일 연동
    final static private String URL = "http://15.164.88.236/GetFoodInfo.php";
    private Map<String, String> map;

    public FoodInfo_GetRequest(String foodK_Name, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();

        map.put("foodK_Name", foodK_Name);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
