package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.TranslatableText;

public class CoordinateArgument {
	public static final SimpleCommandExceptionType MISSING_COORDINATE = new SimpleCommandExceptionType(new TranslatableText("argument.pos.missing.double"));
	public static final SimpleCommandExceptionType MISSING_BLOCK_POSITION = new SimpleCommandExceptionType(new TranslatableText("argument.pos.missing.int"));
	private final boolean relative;
	private final double value;

	public CoordinateArgument(boolean bl, double d) {
		this.relative = bl;
		this.value = d;
	}

	public double toAbsoluteCoordinate(double d) {
		return this.relative ? this.value + d : this.value;
	}

	public static CoordinateArgument parse(StringReader stringReader, boolean bl) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '^') {
			throw Vec3ArgumentType.MIXED_COORDINATE_EXCEPTION.createWithContext(stringReader);
		} else if (!stringReader.canRead()) {
			throw MISSING_COORDINATE.createWithContext(stringReader);
		} else {
			boolean bl2 = isRelative(stringReader);
			int i = stringReader.getCursor();
			double d = stringReader.canRead() && stringReader.peek() != ' ' ? stringReader.readDouble() : 0.0;
			String string = stringReader.getString().substring(i, stringReader.getCursor());
			if (bl2 && string.isEmpty()) {
				return new CoordinateArgument(true, 0.0);
			} else {
				if (!string.contains(".") && !bl2 && bl) {
					d += 0.5;
				}

				return new CoordinateArgument(bl2, d);
			}
		}
	}

	public static CoordinateArgument parse(StringReader stringReader) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '^') {
			throw Vec3ArgumentType.MIXED_COORDINATE_EXCEPTION.createWithContext(stringReader);
		} else if (!stringReader.canRead()) {
			throw MISSING_BLOCK_POSITION.createWithContext(stringReader);
		} else {
			boolean bl = isRelative(stringReader);
			double d;
			if (stringReader.canRead() && stringReader.peek() != ' ') {
				d = bl ? stringReader.readDouble() : (double)stringReader.readInt();
			} else {
				d = 0.0;
			}

			return new CoordinateArgument(bl, d);
		}
	}

	private static boolean isRelative(StringReader stringReader) {
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
		} else if (!(object instanceof CoordinateArgument)) {
			return false;
		} else {
			CoordinateArgument coordinateArgument = (CoordinateArgument)object;
			return this.relative != coordinateArgument.relative ? false : Double.compare(coordinateArgument.value, this.value) == 0;
		}
	}

	public int hashCode() {
		int i = this.relative ? 1 : 0;
		long l = Double.doubleToLongBits(this.value);
		return 31 * i + (int)(l ^ l >>> 32);
	}

	public boolean isRelative() {
		return this.relative;
	}
}
