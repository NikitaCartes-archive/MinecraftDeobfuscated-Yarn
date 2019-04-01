package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class class_2278 {
	public static final SimpleCommandExceptionType field_10759 = new SimpleCommandExceptionType(new class_2588("argument.pos.missing.double"));
	public static final SimpleCommandExceptionType field_10761 = new SimpleCommandExceptionType(new class_2588("argument.pos.missing.int"));
	private final boolean field_10760;
	private final double field_10758;

	public class_2278(boolean bl, double d) {
		this.field_10760 = bl;
		this.field_10758 = d;
	}

	public double method_9740(double d) {
		return this.field_10760 ? this.field_10758 + d : this.field_10758;
	}

	public static class_2278 method_9743(StringReader stringReader, boolean bl) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '^') {
			throw class_2277.field_10757.createWithContext(stringReader);
		} else if (!stringReader.canRead()) {
			throw field_10759.createWithContext(stringReader);
		} else {
			boolean bl2 = method_9742(stringReader);
			int i = stringReader.getCursor();
			double d = stringReader.canRead() && stringReader.peek() != ' ' ? stringReader.readDouble() : 0.0;
			String string = stringReader.getString().substring(i, stringReader.getCursor());
			if (bl2 && string.isEmpty()) {
				return new class_2278(true, 0.0);
			} else {
				if (!string.contains(".") && !bl2 && bl) {
					d += 0.5;
				}

				return new class_2278(bl2, d);
			}
		}
	}

	public static class_2278 method_9739(StringReader stringReader) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '^') {
			throw class_2277.field_10757.createWithContext(stringReader);
		} else if (!stringReader.canRead()) {
			throw field_10761.createWithContext(stringReader);
		} else {
			boolean bl = method_9742(stringReader);
			double d;
			if (stringReader.canRead() && stringReader.peek() != ' ') {
				d = bl ? stringReader.readDouble() : (double)stringReader.readInt();
			} else {
				d = 0.0;
			}

			return new class_2278(bl, d);
		}
	}

	private static boolean method_9742(StringReader stringReader) {
		boolean bl;
		if (stringReader.peek() == '~') {
			bl = true;
			stringReader.skip();
		} else {
			bl = false;
		}

		return bl;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_2278)) {
			return false;
		} else {
			class_2278 lv = (class_2278)object;
			return this.field_10760 != lv.field_10760 ? false : Double.compare(lv.field_10758, this.field_10758) == 0;
		}
	}

	public int hashCode() {
		int i = this.field_10760 ? 1 : 0;
		long l = Double.doubleToLongBits(this.field_10758);
		return 31 * i + (int)(l ^ l >>> 32);
	}

	public boolean method_9741() {
		return this.field_10760;
	}
}
