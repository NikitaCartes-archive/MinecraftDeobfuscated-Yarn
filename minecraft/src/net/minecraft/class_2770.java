package net.minecraft;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2770 implements class_2596<class_2602> {
	private class_2960 field_12676;
	private class_3419 field_12677;

	public class_2770() {
	}

	public class_2770(@Nullable class_2960 arg, @Nullable class_3419 arg2) {
		this.field_12676 = arg;
		this.field_12677 = arg2;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		int i = arg.readByte();
		if ((i & 1) > 0) {
			this.field_12677 = arg.method_10818(class_3419.class);
		}

		if ((i & 2) > 0) {
			this.field_12676 = arg.method_10810();
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		if (this.field_12677 != null) {
			if (this.field_12676 != null) {
				arg.writeByte(3);
				arg.method_10817(this.field_12677);
				arg.method_10812(this.field_12676);
			} else {
				arg.writeByte(1);
				arg.method_10817(this.field_12677);
			}
		} else if (this.field_12676 != null) {
			arg.writeByte(2);
			arg.method_10812(this.field_12676);
		} else {
			arg.writeByte(0);
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public class_2960 method_11904() {
		return this.field_12676;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public class_3419 method_11903() {
		return this.field_12677;
	}

	public void method_11905(class_2602 arg) {
		arg.method_11082(this);
	}
}
