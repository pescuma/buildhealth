package org.pescuma.buildhealth.extractor.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MapReduceExecutor {
	
	public static <I, O> Collection<O> submit(List<I> elements, int step, final Func<I, O> processor)
			throws InterruptedException {
		SimpleExecutor exec = new SimpleExecutor();
		
		final Collection<O> result = new ConcurrentLinkedQueue<O>();
		
		int size = elements.size();
		for (int i = 0; i < size; i += step) {
			final List<I> in = new ArrayList<I>(elements.subList(i, Math.min(i + step, size)));
			
			exec.submit(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					O out = processor.process(in);
					
					if (out != null)
						result.add(out);
					
					return null;
				}
			});
		}
		
		exec.awaitTermination();
		
		return result;
	}
	
	public static interface Func<I, O> {
		O process(Collection<I> in) throws Exception;
	}
	
}
