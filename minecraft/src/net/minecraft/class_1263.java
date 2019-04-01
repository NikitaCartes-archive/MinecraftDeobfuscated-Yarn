package net.minecraft;

import java.util.Set;

public interface class_1263 extends class_3829 {
	int method_5439();

	boolean method_5442();

	class_1799 method_5438(int i);

	class_1799 method_5434(int i, int j);

	class_1799 method_5441(int i);

	void method_5447(int i, class_1799 arg);

	default int method_5444() {
		return 64;
	}

	void method_5431();

	boolean method_5443(class_1657 arg);

	default void method_5435(class_1657 arg) {
	}

	default void method_5432(class_1657 arg) {
	}

	default boolean method_5437(int i, class_1799 arg) {
		return true;
	}

	default int method_18861(class_1792 arg) {
		int i = 0;

		for (int j = 0; j < this.method_5439(); j++) {
			class_1799 lv = this.method_5438(j);
			if (lv.method_7909().equals(arg)) {
				i += lv.method_7947();
			}
		}

		return i;
	}

	default boolean method_18862(Set<class_1792> set) {
		for (int i = 0; i < this.method_5439(); i++) {
			class_1799 lv = this.method_5438(i);
			if (set.contains(lv.method_7909()) && lv.method_7947() > 0) {
				return true;
			}
		}

		return false;
	}
}
