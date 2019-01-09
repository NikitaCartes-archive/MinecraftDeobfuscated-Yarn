package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2390 implements class_2394 {
	public static final class_2390 field_11188 = new class_2390(1.0F, 0.0F, 0.0F, 1.0F);
	public static final class_2394.class_2395<class_2390> field_11189 = new class_2394.class_2395<class_2390>() {
		public class_2390 method_10287(class_2396<class_2390> arg, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			float f = (float)stringReader.readDouble();
			stringReader.expect(' ');
			float g = (float)stringReader.readDouble();
			stringReader.expect(' ');
			float h = (float)stringReader.readDouble();
			stringReader.expect(' ');
			float i = (float)stringReader.readDouble();
			return new class_2390(f, g, h, i);
		}

		public class_2390 method_10288(class_2396<class_2390> arg, class_2540 arg2) {
			return new class_2390(arg2.readFloat(), arg2.readFloat(), arg2.readFloat(), arg2.readFloat());
		}
	};
	private final float field_11187;
	private final float field_11186;
	private final float field_11185;
	private final float field_11184;

	public class_2390(float f, float g, float h, float i) {
		this.field_11187 = f;
		this.field_11186 = g;
		this.field_11185 = h;
		this.field_11184 = class_3532.method_15363(i, 0.01F, 4.0F);
	}

	@Override
	public void method_10294(class_2540 arg) {
		arg.writeFloat(this.field_11187);
		arg.writeFloat(this.field_11186);
		arg.writeFloat(this.field_11185);
		arg.writeFloat(this.field_11184);
	}

	@Override
	public String method_10293() {
		return String.format(
			Locale.ROOT,
			"%s %.2f %.2f %.2f %.2f",
			class_2378.field_11141.method_10221(this.method_10295()),
			this.field_11187,
			this.field_11186,
			this.field_11185,
			this.field_11184
		);
	}

	@Override
	public class_2396<class_2390> method_10295() {
		return class_2398.field_11212;
	}

	@Environment(EnvType.CLIENT)
	public float method_10285() {
		return this.field_11187;
	}

	@Environment(EnvType.CLIENT)
	public float method_10286() {
		return this.field_11186;
	}

	@Environment(EnvType.CLIENT)
	public float method_10284() {
		return this.field_11185;
	}

	@Environment(EnvType.CLIENT)
	public float method_10283() {
		return this.field_11184;
	}
}
