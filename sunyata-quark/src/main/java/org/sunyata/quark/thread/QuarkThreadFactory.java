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

package org.sunyata.quark.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;



public class QuarkThreadFactory implements ThreadFactory {
	private static final AtomicInteger poolNumber = new AtomicInteger(1);
	private AtomicInteger threadNumber = new AtomicInteger(1);
	private boolean isMultiple = true;
	private String threadName = "thread";
	

	public QuarkThreadFactory() {
		this.threadName = "Quark-thread" + "-" + poolNumber.getAndIncrement() + "-thread-";
		
	}
	public QuarkThreadFactory(String threadName, boolean isMultiple) {
		this.isMultiple = isMultiple;
		this.threadName = threadName;
	}

	@Override
	public Thread newThread(Runnable r) {
		String name = isMultiple?threadName + threadNumber.getAndIncrement():threadName;
		Thread t = new Thread(r, name);
		if (t.isDaemon()) {
			t.setDaemon(false);
		}
		if (t.getPriority() != Thread.NORM_PRIORITY) {
			t.setPriority(Thread.NORM_PRIORITY);
		}
		return t;
	}
}
