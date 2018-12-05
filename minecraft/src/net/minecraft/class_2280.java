package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

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
	public Vec3d method_9708(ServerCommandSource serverCommandSource) {
		Vec3d vec3d = serverCommandSource.getPosition();
		return new Vec3d(this.field_10765.method_9740(vec3d.x), this.field_10764.method_9740(vec3d.y), this.field_10766.method_9740(vec3d.z));
	}

	@Override
	public Vec2f method_9709(ServerCommandSource serverCommandSource) {
		Vec2f vec2f = serverCommandSource.getRotation();
		return new Vec2f((float)this.field_10765.method_9740((double)vec2f.x), (float)this.field_10764.method_9740((double)vec2f.y));
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
				throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(stringReader);
			}
		} else {
			stringReader.setCursor(i);
			throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(stringReader);
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
				throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(stringReader);
			}
		} else {
			stringReader.setCursor(i);
			throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(stringReader);
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
