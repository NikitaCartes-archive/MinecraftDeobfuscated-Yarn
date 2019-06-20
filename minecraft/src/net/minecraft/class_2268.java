package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;

public class class_2268 implements class_2267 {
	private final double field_10714;
	private final double field_10713;
	private final double field_10712;

	public class_2268(double d, double e, double f) {
		this.field_10714 = d;
		this.field_10713 = e;
		this.field_10712 = f;
	}

	@Override
	public class_243 method_9708(class_2168 arg) {
		class_241 lv = arg.method_9210();
		class_243 lv2 = arg.method_9219().method_9299(arg);
		float f = class_3532.method_15362((lv.field_1342 + 90.0F) * (float) (Math.PI / 180.0));
		float g = class_3532.method_15374((lv.field_1342 + 90.0F) * (float) (Math.PI / 180.0));
		float h = class_3532.method_15362(-lv.field_1343 * (float) (Math.PI / 180.0));
		float i = class_3532.method_15374(-lv.field_1343 * (float) (Math.PI / 180.0));
		float j = class_3532.method_15362((-lv.field_1343 + 90.0F) * (float) (Math.PI / 180.0));
		float k = class_3532.method_15374((-lv.field_1343 + 90.0F) * (float) (Math.PI / 180.0));
		class_243 lv3 = new class_243((double)(f * h), (double)i, (double)(g * h));
		class_243 lv4 = new class_243((double)(f * j), (double)k, (double)(g * j));
		class_243 lv5 = lv3.method_1036(lv4).method_1021(-1.0);
		double d = lv3.field_1352 * this.field_10712 + lv4.field_1352 * this.field_10713 + lv5.field_1352 * this.field_10714;
		double e = lv3.field_1351 * this.field_10712 + lv4.field_1351 * this.field_10713 + lv5.field_1351 * this.field_10714;
		double l = lv3.field_1350 * this.field_10712 + lv4.field_1350 * this.field_10713 + lv5.field_1350 * this.field_10714;
		return new class_243(lv2.field_1352 + d, lv2.field_1351 + e, lv2.field_1350 + l);
	}

	@Override
	public class_241 method_9709(class_2168 arg) {
		return class_241.field_1340;
	}

	@Override
	public boolean method_9705() {
		return true;
	}

	@Override
	public boolean method_9706() {
		return true;
	}

	@Override
	public boolean method_9707() {
		return true;
	}

	public static class_2268 method_9711(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();
		double d = method_9710(stringReader, i);
		if (stringReader.canRead() && stringReader.peek() == ' ') {
			stringReader.skip();
			double e = method_9710(stringReader, i);
			if (stringReader.canRead() && stringReader.peek() == ' ') {
				stringReader.skip();
				double f = method_9710(stringReader, i);
				return new class_2268(d, e, f);
			} else {
				stringReader.setCursor(i);
				throw class_2277.field_10755.createWithContext(stringReader);
			}
		} else {
			stringReader.setCursor(i);
			throw class_2277.field_10755.createWithContext(stringReader);
		}
	}

	private static double method_9710(StringReader stringReader, int i) throws CommandSyntaxException {
		if (!stringReader.canRead()) {
			throw class_2278.field_10759.createWithContext(stringReader);
		} else if (stringReader.peek() != '^') {
			stringReader.setCursor(i);
			throw class_2277.field_10757.createWithContext(stringReader);
		} else {
			stringReader.skip();
			return stringReader.canRead() && stringReader.peek() != ' ' ? stringReader.readDouble() : 0.0;
		}
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_2268)) {
			return false;
		} else {
			class_2268 lv = (class_2268)object;
			return this.field_10714 == lv.field_10714 && this.field_10713 == lv.field_10713 && this.field_10712 == lv.field_10712;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.field_10714, this.field_10713, this.field_10712});
	}
}
