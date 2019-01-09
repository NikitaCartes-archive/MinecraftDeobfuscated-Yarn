package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class class_3534 implements Comparable<class_3534> {
	public final double field_15739;
	public final double field_15737;
	public final String field_15738;

	public class_3534(String string, double d, double e) {
		this.field_15738 = string;
		this.field_15739 = d;
		this.field_15737 = e;
	}

	public int method_15408(class_3534 arg) {
		if (arg.field_15739 < this.field_15739) {
			return -1;
		} else {
			return arg.field_15739 > this.field_15739 ? 1 : arg.field_15738.compareTo(this.field_15738);
		}
	}

	@Environment(EnvType.CLIENT)
	public int method_15409() {
		return (this.field_15738.hashCode() & 11184810) + 4473924;
	}
}
