package org.sunyata.quark.embed.springcloud;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by leo on 17/4/10.
 */
@FeignClient(value = "quark-service",path = "business",fallback = QuarkFeignClient.QuarkFeignClientHystrix.class)
//@FeignClient(name = "quark-service",path = "business")
public interface QuarkFeignClient {
    @Component
    public class QuarkFeignClientHystrix implements QuarkFeignClient {

        public JsonResponseResult create(@RequestParam(value = "serialNo") String serialNo, @RequestParam(value =
                "businName") String businName, @RequestParam(value = "parameterString") String parameterString) {
            return JsonResponseResult.Error(99, "服务访问异常");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    JsonResponseResult create(@RequestParam(value = "serialNo") String serialNo, @RequestParam(value = "businName")
    String businName, @RequestParam(value = "parameterString") String parameterString);
}