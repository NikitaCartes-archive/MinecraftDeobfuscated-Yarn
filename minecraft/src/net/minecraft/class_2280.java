package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class class_2280 implements class_2267 {
	private final class_2278 field_10765;
	private final class_2278 field_10764;
	private final class_2278 field_10766;

	public class_2280(class_2278 arg, class_2278 arg2, class_2278 arg3) {
		this.field_10765 = arg;
		this.field_10764 = arg2;
		this.field_10766 = arg3;
	}

	@Override
	public class_243 method_9708(class_2168 arg) {
		class_243 lv = arg.method_9222();
		return new class_243(this.field_10765.method_9740(lv.field_1352), this.field_10764.method_9740(lv.field_1351), this.field_10766.method_9740(lv.field_1350));
	}

	@Override
	public class_241 method_9709(class_2168 arg) {
		class_241 lv = arg.method_9210();
		return new class_241((float)this.field_10765.method_9740((double)lv.field_1343), (float)this.field_10764.method_9740((double)lv.field_1342));
	}

	@Override
	public boolean method_9705() {
		return this.field_10765.method_9741();
	}

	@Override
	public boolean method_9706() {
		return this.field_10764.method_9741();
	}

	@Override
	public boolean method_9707() {
		return this.field_10766.method_9741();
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_2280)) {
			return false;
		} else {
			class_2280 lv = (class_2280)object;
			if (!this.field_10765.equals(lv.field_10765)) {
				return false;
			} else {
				return !this.field_10764.equals(lv.field_10764) ? false : this.field_10766.equals(lv.field_10766);
			}
		}
	}

	public static class_2280 method_9749(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();
		class_2278 lv = class_2278.method_9739(stringReader);
		if (stringReader.canRead() && stringReader.peek() == ' ') {
			stringReader.skip();
			class_2278 lv2 = class_2278.method_9739(stringReader);
			if (stringReader.canRead() && stringReader.peek() == ' ') {
				stringReader.skip();
				class_2278 lv3 = class_2278.method_9739(stringReader);
				return new class_2280(lv, lv2, lv3);
			} else {
				stringReader.setCursor(i);
				throw class_2277.field_10755.createWithContext(stringReader);
			}
		} else {
			stringReader.setCursor(i);
			throw class_2277.field_10755.createWithContext(stringReader);
		}
	}

	public static class_2280 method_9750(StringReader stringReader, boolean bl) throws CommandSyntaxException {
		int i = stringReader.getCursor();
		class_2278 lv = class_2278.method_9743(stringReader, bl);
		if (stringReader.canRead() && stringReader.peek() == ' ') {
			stringReader.skip();
			class_2278 lv2 = class_2278.method_9743(stringReader, false);
			if (stringReader.canRead() && stringReader.peek() == ' ') {
				stringReader.skip();
				class_2278 lv3 = class_2278.method_9743(stringReader, bl);
				return new class_2280(lv, lv2, lv3);
			} else {
				stringReader.setCursor(i);
				throw class_2277.field_10755.createWithContext(stringReader);
			}
		} else {
			stringReader.setCursor(i);
			throw class_2277.field_10755.createWithContext(stringReader);
		}
	}

	public static class_2280 method_9751() {
		return new class_2280(new class_2278(true, 0.0), new class_2278(true, 0.0), new class_2278(true, 0.0));
	}

	public int hashCode() {
		int i = this.field_10765.hashCode();
		i = 31 * i + this.field_10764.hashCode();
		return 31 * i + this.field_10766.hashCode();
	}
}
