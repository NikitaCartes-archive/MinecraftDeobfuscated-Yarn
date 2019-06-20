package net.minecraft;

import java.util.BitSet;
import java.util.Random;

public class class_2922<WC extends class_2920> {
	public final class_2939<WC> field_13279;
	public final WC field_13278;

	public class_2922(class_2939<WC> arg, WC arg2) {
		this.field_13279 = arg;
		this.field_13278 = arg2;
	}

	public boolean method_12669(Random random, int i, int j) {
		return this.field_13279.method_12705(random, i, j, this.field_13278);
	}

	public boolean method_12668(class_2791 arg, Random random, int i, int j, int k, int l, int m, BitSet bitSet) {
		return this.field_13279.method_12702(arg, random, i, j, k, l, m, bitSet, this.field_13278);
	}
}
