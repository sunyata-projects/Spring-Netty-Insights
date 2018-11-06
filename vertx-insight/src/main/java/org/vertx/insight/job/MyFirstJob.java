package org.vertx.insight.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by leo on 17/3/27.
 */
public class MyFirstJob implements Job {
    public void init() {

    }

    public MyFirstJob() {

    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("hahahah");
    }
}