package org.lucasimi.tda.mapper.utils;

public class BinaryTree<T> {

	private T data;

	private BinaryTree<T> left;

	private BinaryTree<T> right;
	
	public BinaryTree() {}
	
	public BinaryTree(T data) {
		this.data = data;
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
	
	public void setData(T x) {
		this.data = x;
	}
	
}
