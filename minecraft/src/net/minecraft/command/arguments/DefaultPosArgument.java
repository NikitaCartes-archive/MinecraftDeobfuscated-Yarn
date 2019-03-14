package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class DefaultPosArgument implements PosArgument {
	private final CoordinateArgument x;
	private final CoordinateArgument y;
	private final CoordinateArgument z;

	public DefaultPosArgument(CoordinateArgument coordinateArgument, CoordinateArgument coordinateArgument2, CoordinateArgument coordinateArgument3) {
		this.x = coordinateArgument;
		this.y = coordinateArgument2;
		this.z = coordinateArgument3;
	}

	@Override
	public Vec3d toAbsolutePos(ServerCommandSource serverCommandSource) {
		Vec3d vec3d = serverCommandSource.getPosition();
		return new Vec3d(this.x.toAbsoluteCoordinate(vec3d.x), this.y.toAbsoluteCoordinate(vec3d.y), this.z.toAbsoluteCoordinate(vec3d.z));
	}

	@Override
	public Vec2f toAbsoluteRotation(ServerCommandSource serverCommandSource) {
		Vec2f vec2f = serverCommandSource.getRotation();
		return new Vec2f((float)this.x.toAbsoluteCoordinate((double)vec2f.x), (float)this.y.toAbsoluteCoordinate((double)vec2f.y));
	}

	@Override
	public boolean isXRelative() {
		return this.x.isRelative();
	}

	@Override
	public boolean isYRelative() {
		return this.y.isRelative();
	}

	@Override
	public boolean isZRelative() {
		return this.z.isRelative();
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof DefaultPosArgument)) {
			return false;
		} else {
			DefaultPosArgument defaultPosArgument = (DefaultPosArgument)object;
			if (!this.x.equals(defaultPosArgument.x)) {
				return false;
			} else {
				return !this.y.equals(defaultPosArgument.y) ? false : this.z.equals(defaultPosArgument.z);
			}
		}
	}

	public static DefaultPosArgument parse(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();
		CoordinateArgument coordinateArgument = CoordinateArgument.parse(stringReader);
		if (stringReader.canRead() && stringReader.peek() == ' ') {
			stringReader.skip();
			CoordinateArgument coordinateArgument2 = CoordinateArgument.parse(stringReader);
			if (stringReader.canRead() && stringReader.peek() == ' ') {
				stringReader.skip();
				CoordinateArgument coordinateArgument3 = CoordinateArgument.parse(stringReader);
				return new DefaultPosArgument(coordinateArgument, coordinateArgument2, coordinateArgument3);
			} else {
				stringReader.setCursor(i);
				throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(stringReader);
			}
		} else {
			stringReader.setCursor(i);
			throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(stringReader);
		}
	}

	public static DefaultPosArgument parse(StringReader stringReader, boolean bl) throws CommandSyntaxException {
		int i = stringReader.getCursor();
		CoordinateArgument coordinateArgument = CoordinateArgument.parse(stringReader, bl);
		if (stringReader.canRead() && stringReader.peek() == ' ') {
			stringReader.skip();
			CoordinateArgument coordinateArgument2 = CoordinateArgument.parse(stringReader, false);
			if (stringReader.canRead() && stringReader.peek() == ' ') {
				stringReader.skip();
				CoordinateArgument coordinateArgument3 = CoordinateArgument.parse(stringReader, bl);
				return new DefaultPosArgument(coordinateArgument, coordinateArgument2, coordinateArgument3);
			} else {
				stringReader.setCursor(i);
				throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(stringReader);
			}
		} else {
			stringReader.setCursor(i);
			throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(stringReader);
		}
	}

	public static DefaultPosArgument zero() {
		return new DefaultPosArgument(new CoordinateArgument(true, 0.0), new CoordinateArgument(true, 0.0), new CoordinateArgument(true, 0.0));
	}

	public int hashCode() {
		int i = this.x.hashCode();
		i = 31 * i + this.y.hashCode();
		return 31 * i + this.z.hashCode();
	}
}
