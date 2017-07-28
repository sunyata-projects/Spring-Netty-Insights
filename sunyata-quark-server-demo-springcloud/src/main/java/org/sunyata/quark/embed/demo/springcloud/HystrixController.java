//package org.sunyata.quark.embed.demo.springcloud;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Random;
//
///**
// * Created by leo on 17/7/27.
// */
//@RestController
//@RequestMapping("/main")
//public class HystrixController {
//
//    Logger logger = LoggerFactory.getLogger(HystrixController.class);
//
//    private Random random = new Random();
//
//    @RequestMapping("index")
//    public String hello() {
//        // 用随机数来模拟错误, 这里让正确率高一些
//        return new TestCommandWrapper(random.nextInt(100) < 95).execute();
//
//    }
//}package org.sunyata.quark.embed.demo.springcloud;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Random;
//
///**
// * Created by leo on 17/7/27.
// */
//@RestController
//@RequestMapping("/main")
//public class HystrixController {
//
//    Logger logger = LoggerFactory.getLogger(HystrixController.class);
//
//    private Random random = new Random();
//
//    @RequestMapping("index")
//    public String hello() {
//        // 用随机数来模拟错误, 这里让正确率高一些
//        return new TestCommandWrapper(random.nextInt(100) < 95).execute();
//
//    }
//}