package org.pescuma.buildhealth.analyser.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.pescuma.buildhealth.analyser.utils.buildstatus.BuildStatusAndExplanation;
import org.pescuma.buildhealth.core.BuildStatus;

import com.google.common.base.Predicate;

public class BuildHealthAnalyserUtils {
	
	public static <T extends TreeStats> Collection<SimpleTree<T>.Node> sort(Collection<SimpleTree<T>.Node> nodes,
			boolean highlighProblems) {
		
		if (!highlighProblems)
			return nodes;
		else
			return sort(nodes);
	}
	
	public static <T extends TreeStats> Collection<SimpleTree<T>.Node> sort(Collection<SimpleTree<T>.Node> nodes) {
		
		List<SimpleTree<T>.Node> result = new ArrayList<SimpleTree<T>.Node>(nodes);
		
		Collections.sort(result, new Comparator<SimpleTree<T>.Node>() {
			@Override
			public int compare(SimpleTree<T>.Node o1, SimpleTree<T>.Node o2) {
				int cmp = precedence(o1.getData().getStatusWithChildren())
						- precedence(o2.getData().getStatusWithChildren());
				if (cmp != 0)
					return cmp;
				
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
			
			private int precedence(BuildStatus status) {
				switch (status) {
					case Good:
						return 2;
					case SoSo:
						return 1;
					case Problematic:
						return 0;
					default:
						throw new IllegalStateException();
				}
			}
		});
		
		return result;
	}
	
	public static <T extends TreeStats> void removeNonSummaryNodes(SimpleTree<T> tree, boolean highlighProblems) {
		if (!highlighProblems)
			tree.getRoot().removeChildIf(new Predicate<SimpleTree<T>.Node>() {
				@Override
				public boolean apply(SimpleTree<T>.Node input) {
					return true;
				}
			});
		
		else
			tree.removeNodesIf(new Predicate<SimpleTree<T>.Node>() {
				@Override
				public boolean apply(SimpleTree<T>.Node node) {
					T data = node.getData();
					return data.getStatusWithChildren() == BuildStatus.Good
							|| (!node.isRoot() && !data.hasOwnStatus() && node.hasChildren());
				}
			});
	}
	
	public static class TreeStats {
		
		private final String[] names;
		private BuildStatusAndExplanation ownStatus = null;
		/** Worst status including only first children with own status */
		private BuildStatus status = BuildStatus.Good;
		/** Worst status including all children */
		private BuildStatus statusWithChildren = BuildStatus.Good;
		
		protected TreeStats(String... names) {
			this.names = names;
		}
		
		public String[] getNames() {
			return names;
		}
		
		public BuildStatus getStatus() {
			return status;
		}
		
		public BuildStatus getStatusWithChildren() {
			return statusWithChildren;
		}
		
		public boolean hasOwnStatus() {
			return ownStatus != null;
		}
		
		public BuildStatusAndExplanation getOwnStatus() {
			return ownStatus;
		}
		
		public void setOwnStatus(BuildStatusAndExplanation status) {
			if (ownStatus != null)
				ownStatus = ownStatus.mergeWith(status);
			else
				ownStatus = status;
			
			this.status = ownStatus.status;
			statusWithChildren = statusWithChildren.mergeWith(ownStatus.status);
		}
		
		public String getProblemDescription() {
			if (ownStatus == null)
				return null;
			else
				return ownStatus.explanation;
		}
		
		public void mergeChildStatus(TreeStats other) {
			if (!hasOwnStatus())
				status = status.mergeWith(other.status);
			
			statusWithChildren = statusWithChildren.mergeWith(other.statusWithChildren);
		}
		
		public void mergeChildStatus(BuildStatusAndExplanation other) {
			if (!hasOwnStatus())
				status = status.mergeWith(other.status);
			
			statusWithChildren = statusWithChildren.mergeWith(other.status);
		}
	}
}
