/*
 *
 *
 *  * Copyright (c) 2017 Leo Lee(lichl.1980@163.com).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy
 *  * of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations
 *  * under the License.
 *  *
 *
 */

package org.sunyata.quark.client;/**
 * Created by leo on 17/4/1.
 */

/**
 * Created by leo on 17/3/30.
 */
public class JsonResponseResult<T> {

    private Integer code;

    private String msg;

    private T response;

    public Integer getCode() {
        return code;
    }

    public JsonResponseResult<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public JsonResponseResult<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getResponse() {
        return response;
    }

    public JsonResponseResult<T> setResponse(T response) {
        this.response = response;
        return this;
    }

    public JsonResponseResult<T> createSuccess() {
        return this.setCode(0).setMsg("success");
    }

    public static JsonResponseResult Success() {
        return new JsonResponseResult().setCode(0).setMsg("success");
    }

    public static JsonResponseResult Success(Object response) {
        return JsonResponseResult.Success().setResponse(response);
    }

    public static JsonResponseResult Error(Integer errorCode, String message) {
        return new JsonResponseResult().setCode(errorCode).setMsg(message);
    }
}