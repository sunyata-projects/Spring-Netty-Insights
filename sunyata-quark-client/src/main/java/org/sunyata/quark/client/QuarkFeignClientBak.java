///*
// *
// *
// *  * Copyright (c) 2017 Leo Lee(lichl.1980@163.com).
// *  *
// *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
// *  * use this file except in compliance with the License. You may obtain a copy
// *  * of the License at
// *  *
// *  *   http://www.apache.org/licenses/LICENSE-2.0
// *  *
// *  * Unless required by applicable law or agreed to in writing, software
// *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
// *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
// *  * License for the specific language governing permissions and limitations
// *  * under the License.
// *  *
// *
// */
//
//package org.sunyata.quark.client;
//
//import org.springframework.cloud.netflix.feign.FeignClient;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.sunyata.quark.client.dto.BusinessComponentDescriptor;
//import org.sunyata.quark.client.dto.BusinessComponentInstance;
//
//import java.util.List;
//
///**
// * Created by leo on 17/4/1.
// */
//@FeignClient(value = "quark-service", path = "business", fallback = QuarkFeignClientBak.QuarkFeignClientHystrix.class)
////@FeignClient(name = "quark-service",path = "business")
//public interface QuarkFeignClientBak {
//    @Component
//    public class QuarkFeignClientHystrix implements QuarkFeignClientBak {
//
//        public JsonResponseResult create(@RequestParam(value = "serialNo") String serialNo, @RequestParam(value =
//                "businName") String businName, @RequestParam(value = "parameterString") String parameterString,
//                                         @RequestParam(value = "autoRun") boolean autoRun) {
//            return JsonResponseResult.Error(99, "服务访问异常");
//        }
//
//        public JsonResponseResult<List<BusinessComponentDescriptor>> components() {
//            return JsonResponseResult.Error(99, "服务访问异常");
//        }
//
//        public JsonResponseResult run(@RequestParam(value = "serialNo") String serialNo) {
//            return JsonResponseResult.Error(99, "服务访问异常");
//        }
//
//        public JsonResponseResult runByManual(@RequestParam(value = "serialNo") String serialNo, @RequestParam(value =
//                "quarkIndex") Integer quarkIndex, @RequestParam(value = "parameterString") String parameterString) {
//            return JsonResponseResult.Error(99, "服务访问异常");
//        }
//
//        public JsonResponseResult<BusinessComponentInstance> instance(@RequestParam(value = "serialNo") String
//                                                                              serialNo) {
//            return JsonResponseResult.Error(99, "服务访问异常");
//        }
//    }
//
//    @RequestMapping(method = RequestMethod.POST, value = "/create")
//    JsonResponseResult create(@RequestParam(value = "serialNo") String serialNo, @RequestParam(value = "businName")
//    String businName, @RequestParam(value = "parameterString") String parameterString, @RequestParam(value =
//            "autoRun") boolean autoRun);
//
//    @RequestMapping(method = RequestMethod.GET, value = "/components")
//    JsonResponseResult<List<BusinessComponentDescriptor>> components();
//
//    @RequestMapping(method = RequestMethod.POST, value = "/run")
//    JsonResponseResult run(@RequestParam(value = "serialNo") String serialNo);
//
//    @RequestMapping(method = RequestMethod.POST, value = "/runByManual")
//    JsonResponseResult runByManual(@RequestParam(value = "serialNo") String serialNo, @RequestParam(value =
//            "quarkIndex") Integer quarkIndex, @RequestParam(value = "parameterString") String
//                                           parameters);
//
//    @RequestMapping(method = RequestMethod.GET, value = "/instance")
//    JsonResponseResult<BusinessComponentInstance> instance(@RequestParam(value = "serialNo") String serialNo);
//}
