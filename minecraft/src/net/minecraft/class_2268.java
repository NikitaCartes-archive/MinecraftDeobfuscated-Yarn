package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

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
	public Vec3d method_9708(ServerCommandSource serverCommandSource) {
		Vec2f vec2f = serverCommandSource.getRotation();
		Vec3d vec3d = serverCommandSource.getEntityAnchor().positionAt(serverCommandSource);
		float f = MathHelper.cos((vec2f.y + 90.0F) * (float) (Math.PI / 180.0));
		float g = MathHelper.sin((vec2f.y + 90.0F) * (float) (Math.PI / 180.0));
		float h = MathHelper.cos(-vec2f.x * (float) (Math.PI / 180.0));
		float i = MathHelper.sin(-vec2f.x * (float) (Math.PI / 180.0));
		float j = MathHelper.cos((-vec2f.x + 90.0F) * (float) (Math.PI / 180.0));
		float k = MathHelper.sin((-vec2f.x + 90.0F) * (float) (Math.PI / 180.0));
		Vec3d vec3d2 = new Vec3d((double)(f * h), (double)i, (double)(g * h));
		Vec3d vec3d3 = new Vec3d((double)(f * j), (double)k, (double)(g * j));
		Vec3d vec3d4 = vec3d2.crossProduct(vec3d3).multiply(-1.0);
		double d = vec3d2.x * this.field_10712 + vec3d3.x * this.field_10713 + vec3d4.x * this.field_10714;
		double e = vec3d2.y * this.field_10712 + vec3d3.y * this.field_10713 + vec3d4.y * this.field_10714;
		double l = vec3d2.z * this.field_10712 + vec3d3.z * this.field_10713 + vec3d4.z * this.field_10714;
		return new Vec3d(vec3d.x + d, vec3d.y + e, vec3d.z + l);
	}

	@Override
	public Vec2f method_9709(ServerCommandSource serverCommandSource) {
		return Vec2f.ZERO;
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
				throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(stringReader);
			}
		} else {
			stringReader.setCursor(i);
			throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(stringReader);
		}
	}

	private static double method_9710(StringReader stringReader, int i) throws CommandSyntaxException {
		if (!stringReader.canRead()) {
			throw class_2278.field_10759.createWithContext(stringReader);
		} else if (stringReader.peek() != '^') {
			stringReader.setCursor(i);
			throw Vec3ArgumentType.MIXED_COORDINATE_EXCEPTION.createWithContext(stringReader);
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
