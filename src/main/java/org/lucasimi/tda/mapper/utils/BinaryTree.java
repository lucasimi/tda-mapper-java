package org.lucasimi.tda.mapper.utils;

public class BinaryTree<T> {

	private T data;

	private final BinaryTree<T> left;

	private final BinaryTree<T> right;
	
	public BinaryTree(T data) {
		this.data = data;
		this.left = null;
		this.right = null;
	}
	
	public BinaryTree(T data, BinaryTree<T> left, BinaryTree<T> right) {
		this.data = data;
		this.left = left;
		this.right = right;
	}
	
	public T getData() {
		return this.data;
	}
	
	public BinaryTree<T> getLeft() {
		return this.left;
	}
	
	public BinaryTree<T> getRight() {
		return this.right;
	}
	
	public boolean isTerminal() {
		return (this.left == null) && (this.right == null);
	}
	
}
