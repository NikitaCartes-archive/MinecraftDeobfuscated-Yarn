package net.minecraft;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1075 extends class_3288 {
	@Nullable
	private class_1011 field_5321;
	@Nullable
	private class_2960 field_5320;

	public class_1075(String string, boolean bl, Supplier<class_3262> supplier, class_3262 arg, class_3272 arg2, class_3288.class_3289 arg3) {
		super(string, bl, supplier, arg, arg2, arg3);
		class_1011 lv = null;

		try {
			InputStream inputStream = arg.method_14410("pack.png");
			Throwable var9 = null;

			try {
				lv = class_1011.method_4309(inputStream);
			} catch (Throwable var19) {
				var9 = var19;
				throw var19;
			} finally {
				if (inputStream != null) {
					if (var9 != null) {
						try {
							inputStream.close();
						} catch (Throwable var18) {
							var9.addSuppressed(var18);
						}
					} else {
						inputStream.close();
					}
				}
			}
		} catch (IllegalArgumentException | IOException var21) {
		}

		this.field_5321 = lv;
	}

	public class_1075(
		String string,
		boolean bl,
		Supplier<class_3262> supplier,
		class_2561 arg,
		class_2561 arg2,
		class_3281 arg3,
		class_3288.class_3289 arg4,
		boolean bl2,
		@Nullable class_1011 arg5
	) {
		super(string, bl, supplier, arg, arg2, arg3, arg4, bl2);
		this.field_5321 = arg5;
	}

	public void method_4664(class_1060 arg) {
		if (this.field_5320 == null) {
			if (this.field_5321 == null) {
				this.field_5320 = new class_2960("textures/misc/unknown_pack.png");
			} else {
				this.field_5320 = arg.method_4617("texturepackicon", new class_1043(this.field_5321));
			}
		}

		arg.method_4618(this.field_5320);
	}

	@Override
	public void close() {
		super.close();
		if (this.field_5321 != null) {
			this.field_5321.close();
			this.field_5321 = null;
		}
	}
}
