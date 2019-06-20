package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class class_3682 implements AutoCloseable {
	private final class_310 field_16256;
	private final class_323 field_16255;

	public class_3682(class_310 arg) {
		this.field_16256 = arg;
		this.field_16255 = new class_323(this::method_16039);
	}

	public class_313 method_16039(long l) {
		class_313 lv = new class_313(this.field_16255, l);
		class_316.field_1931.method_18612((float)lv.method_1621());
		return lv;
	}

	public class_1041 method_16038(class_543 arg, String string, String string2) {
		return new class_1041(this.field_16256, this.field_16255, arg, string, string2);
	}

	public void close() {
		this.field_16255.method_15992();
	}
}
