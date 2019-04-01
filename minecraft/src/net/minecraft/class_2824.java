package net.minecraft;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2824 implements class_2596<class_2792> {
	private int field_12870;
	private class_2824.class_2825 field_12871;
	private class_243 field_12872;
	private class_1268 field_12869;

	public class_2824() {
	}

	public class_2824(class_1297 arg) {
		this.field_12870 = arg.method_5628();
		this.field_12871 = class_2824.class_2825.field_12875;
	}

	@Environment(EnvType.CLIENT)
	public class_2824(class_1297 arg, class_1268 arg2) {
		this.field_12870 = arg.method_5628();
		this.field_12871 = class_2824.class_2825.field_12876;
		this.field_12869 = arg2;
	}

	@Environment(EnvType.CLIENT)
	public class_2824(class_1297 arg, class_1268 arg2, class_243 arg3) {
		this.field_12870 = arg.method_5628();
		this.field_12871 = class_2824.class_2825.field_12873;
		this.field_12869 = arg2;
		this.field_12872 = arg3;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12870 = arg.method_10816();
		this.field_12871 = arg.method_10818(class_2824.class_2825.class);
		if (this.field_12871 == class_2824.class_2825.field_12873) {
			this.field_12872 = new class_243((double)arg.readFloat(), (double)arg.readFloat(), (double)arg.readFloat());
		}

		if (this.field_12871 == class_2824.class_2825.field_12876 || this.field_12871 == class_2824.class_2825.field_12873) {
			this.field_12869 = arg.method_10818(class_1268.class);
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12870);
		arg.method_10817(this.field_12871);
		if (this.field_12871 == class_2824.class_2825.field_12873) {
			arg.writeFloat((float)this.field_12872.field_1352);
			arg.writeFloat((float)this.field_12872.field_1351);
			arg.writeFloat((float)this.field_12872.field_1350);
		}

		if (this.field_12871 == class_2824.class_2825.field_12876 || this.field_12871 == class_2824.class_2825.field_12873) {
			arg.method_10817(this.field_12869);
		}
	}

	public void method_12251(class_2792 arg) {
		arg.method_12062(this);
	}

	@Nullable
	public class_1297 method_12248(class_1937 arg) {
		return arg.method_8469(this.field_12870);
	}

	public class_2824.class_2825 method_12252() {
		return this.field_12871;
	}

	public class_1268 method_12249() {
		return this.field_12869;
	}

	public class_243 method_12250() {
		return this.field_12872;
	}

	public static enum class_2825 {
		field_12876,
		field_12875,
		field_12873;
	}
}
