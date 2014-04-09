package org.pescuma.buildhealth.analyser.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class SimpleTree<T> {
	
	private final Node root;
	private final Function<String[], T> factory;
	
	public SimpleTree(Function<String[], T> factory) {
		if (factory == null)
			throw new IllegalArgumentException();
		
		this.factory = factory;
		this.root = new Node();
	}
	
	public SimpleTree() {
		this(new Function<String[], T>() {
			@Override
			public T apply(String[] input) {
				return null;
			}
		});
	}
	
	public Node getRoot() {
		return root;
	}
	
	public void visit(Visitor<T> visitor) {
		root.visit(visitor);
	}
	
	public Node getNode(String... names) {
		return root.getChild(names);
	}
	
	public boolean hasNode(String... names) {
		return root.hasChild(names);
	}
	
	public class Node {
		
		private final String[] name;
		private T data;
		private final Map<String, Node> children = new TreeMap<String, Node>(String.CASE_INSENSITIVE_ORDER);
		private final List<Node> unnamedChildren = new ArrayList<Node>();
		
		/** Create the root node */
		private Node() {
			this.name = new String[0];
			this.data = factory.apply(name);
		}
		
		private Node(String[] path, String name) {
			this.name = Arrays.copyOf(path, path.length + 1);
			this.name[path.length] = name;
			this.data = factory.apply(this.name);
		}
		
		private Node(String[] name) {
			this.name = name;
			this.data = factory.apply(name);
		}
		
		public boolean isRoot() {
			return name.length == 0;
		}
		
		public String getName() {
			if (name.length == 0)
				return null;
			else
				return name[name.length - 1];
		}
		
		public T getData() {
			return data;
		}
		
		public void setData(T data) {
			this.data = data;
		}
		
		public Collection<Node> getChildren() {
			if (unnamedChildren == null)
				return children.values();
			else {
				List<Node> result = new ArrayList<Node>(children.values());
				result.addAll(unnamedChildren);
				return result;
			}
		}
		
		public boolean hasChild(String... names) {
			Node node = this;
			for (String name : names) {
				if (!node.hasChild(name))
					return false;
				
				node = node.getChild(name);
			}
			return true;
		}
		
		public boolean hasChild(String name) {
			return children.containsKey(name);
		}
		
		public Node getChild(String... names) {
			Node result = this;
			for (String name : names)
				result = result.getChild(name);
			return result;
		}
		
		public Node getChild(String name) {
			if (name == null)
				throw new IllegalArgumentException();
			
			Node result = children.get(name);
			if (result == null) {
				result = new Node(this.name, name);
				children.put(name, result);
			}
			return result;
		}
		
		public Node addUnnamedChild() {
			Node node = new Node(this.name);
			unnamedChildren.add(node);
			return node;
		}
		
		public void visit(Visitor<T> visitor) {
			visitor.preVisitNode(this);
			
			for (Node child : children.values())
				child.visit(visitor);
			
			for (Node child : unnamedChildren)
				child.visit(visitor);
			
			visitor.posVisitNode(this);
		}
		
		public void removeChildIf(Predicate<Node> predicate) {
			for (Iterator<Node> it = children.values().iterator(); it.hasNext();) {
				Node node = it.next();
				if (predicate.apply(node))
					it.remove();
			}
			for (Iterator<Node> it = unnamedChildren.iterator(); it.hasNext();) {
				Node node = it.next();
				if (predicate.apply(node))
					it.remove();
			}
		}
		
		public boolean hasChildren() {
			return !children.isEmpty() || !unnamedChildren.isEmpty();
		}
		
	}
	
	public static class Visitor<T> {
		public void preVisitNode(SimpleTree<T>.Node node) {
		}
		
		public void posVisitNode(SimpleTree<T>.Node node) {
		}
	}
	
	public void removeNodesIf(final Predicate<Node> predicate) {
		visit(new Visitor<T>() {
			@Override
			public void posVisitNode(SimpleTree<T>.Node node) {
				node.removeChildIf(predicate);
			}
		});
	}
	
}
