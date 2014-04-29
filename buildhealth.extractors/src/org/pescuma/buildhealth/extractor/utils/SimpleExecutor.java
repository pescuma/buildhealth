package org.pescuma.buildhealth.extractor.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SimpleExecutor {
	
	private final ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	private final List<Future<?>> futures = new LinkedList<Future<?>>();
	
	public void submit(Runnable task) {
		futures.add(exec.submit(task, null));
	}
	
	public <T> void submit(Callable<T> task) {
		futures.add(exec.submit(task));
	}
	
	public void awaitTermination() throws InterruptedException {
		List<Exception> exceptions = new ArrayList<Exception>();
		
		try {
			
			for (Future<?> f : futures) {
				if (!f.isDone()) {
					try {
						f.get();
					} catch (CancellationException ignore) {
					} catch (ExecutionException ignore) {
						exceptions.add(ignore);
					}
				}
			}
			
			if (exceptions.size() > 0)
				throw new RuntimeException(exceptions.get(0));
			
		} finally {
			exec.shutdown();
		}
	}
	
}
