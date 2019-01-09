package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2820 implements class_2596<class_2792> {
	private class_1799 field_12863;
	private boolean field_12864;
	private class_1268 field_12865;

	public class_2820() {
	}

	@Environment(EnvType.CLIENT)
	public class_2820(class_1799 arg, boolean bl, class_1268 arg2) {
		this.field_12863 = arg.method_7972();
		this.field_12864 = bl;
		this.field_12865 = arg2;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12863 = arg.method_10819();
		this.field_12864 = arg.readBoolean();
		this.field_12865 = arg.method_10818(class_1268.class);
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10793(this.field_12863);
		arg.writeBoolean(this.field_12864);
		arg.method_10817(this.field_12865);
	}

	public void method_12236(class_2792 arg) {
		arg.method_12053(this);
	}

	public class_1799 method_12237() {
		return this.field_12863;
	}

	public boolean method_12238() {
		return this.field_12864;
	}

	public class_1268 method_12235() {
		return this.field_12865;
	}
}
