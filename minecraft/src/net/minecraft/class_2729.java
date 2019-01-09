package net.minecraft;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2729 implements class_2596<class_2602> {
	@Nullable
	private class_2960 field_12440;

	public class_2729() {
	}

	public class_2729(@Nullable class_2960 arg) {
		this.field_12440 = arg;
	}

	public void method_11794(class_2602 arg) {
		arg.method_11161(this);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		if (arg.readBoolean()) {
			this.field_12440 = arg.method_10810();
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeBoolean(this.field_12440 != null);
		if (this.field_12440 != null) {
			arg.method_10812(this.field_12440);
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public class_2960 method_11793() {
		return this.field_12440;
	}
}
