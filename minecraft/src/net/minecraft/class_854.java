package net.minecraft;

import java.util.BitSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_854 {
	private static final int field_4491 = class_2350.values().length;
	private final BitSet field_4492 = new BitSet(field_4491 * field_4491);

	public void method_3693(Set<class_2350> set) {
		for (class_2350 lv : set) {
			for (class_2350 lv2 : set) {
				this.method_3692(lv, lv2, true);
			}
		}
	}

	public void method_3692(class_2350 arg, class_2350 arg2, boolean bl) {
		this.field_4492.set(arg.ordinal() + arg2.ordinal() * field_4491, bl);
		this.field_4492.set(arg2.ordinal() + arg.ordinal() * field_4491, bl);
	}

	public void method_3694(boolean bl) {
		this.field_4492.set(0, this.field_4492.size(), bl);
	}

	public boolean method_3695(class_2350 arg, class_2350 arg2) {
		return this.field_4492.get(arg.ordinal() + arg2.ordinal() * field_4491);
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(' ');

		for (class_2350 lv : class_2350.values()) {
			stringBuilder.append(' ').append(lv.toString().toUpperCase().charAt(0));
		}

		stringBuilder.append('\n');

		for (class_2350 lv : class_2350.values()) {
			stringBuilder.append(lv.toString().toUpperCase().charAt(0));

			for (class_2350 lv2 : class_2350.values()) {
				if (lv == lv2) {
					stringBuilder.append("  ");
				} else {
					boolean bl = this.method_3695(lv, lv2);
					stringBuilder.append(' ').append((char)(bl ? 'Y' : 'n'));
				}
			}

			stringBuilder.append('\n');
		}

		return stringBuilder.toString();
	}
}
