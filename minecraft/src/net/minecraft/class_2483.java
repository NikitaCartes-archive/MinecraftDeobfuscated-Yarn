package net.minecraft;

import java.util.AbstractList;

public abstract class class_2483<T extends class_2520> extends AbstractList<T> implements class_2520 {
	public abstract int size();

	public T method_10534(int i) {
		return this.method_10536(i);
	}

	public T method_10531(int i, T arg) {
		T lv = this.method_10534(i);
		this.method_10535(i, arg);
		return lv;
	}

	public abstract T method_10536(int i);

	public abstract void method_10535(int i, class_2520 arg);

	public abstract void method_10533(int i, class_2520 arg);

	public abstract void method_10532(int i);
}
