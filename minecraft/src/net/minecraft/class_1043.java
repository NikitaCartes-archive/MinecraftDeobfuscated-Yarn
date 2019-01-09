package net.minecraft;

import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1043 extends class_1044 implements AutoCloseable {
	@Nullable
	private class_1011 field_5200;

	public class_1043(class_1011 arg) {
		this.field_5200 = arg;
		TextureUtil.prepareImage(this.method_4624(), this.field_5200.method_4307(), this.field_5200.method_4323());
		this.method_4524();
	}

	public class_1043(int i, int j, boolean bl) {
		this.field_5200 = new class_1011(i, j, bl);
		TextureUtil.prepareImage(this.method_4624(), this.field_5200.method_4307(), this.field_5200.method_4323());
	}

	@Override
	public void method_4625(class_3300 arg) throws IOException {
	}

	public void method_4524() {
		this.method_4623();
		this.field_5200.method_4301(0, 0, 0, false);
	}

	@Nullable
	public class_1011 method_4525() {
		return this.field_5200;
	}

	public void method_4526(class_1011 arg) throws Exception {
		this.field_5200.close();
		this.field_5200 = arg;
	}

	public void close() {
		this.field_5200.close();
		this.method_4528();
		this.field_5200 = null;
	}
}
