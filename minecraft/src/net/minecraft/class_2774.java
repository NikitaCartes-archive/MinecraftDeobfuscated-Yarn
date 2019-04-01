package net.minecraft;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2774 implements class_2596<class_2602> {
	private int field_12691;
	@Nullable
	private class_2487 field_12690;

	public class_2774() {
	}

	public class_2774(int i, @Nullable class_2487 arg) {
		this.field_12691 = i;
		this.field_12690 = arg;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12691 = arg.method_10816();
		this.field_12690 = arg.method_10798();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12691);
		arg.method_10794(this.field_12690);
	}

	public void method_11909(class_2602 arg) {
		arg.method_11127(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11910() {
		return this.field_12691;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public class_2487 method_11911() {
		return this.field_12690;
	}

	@Override
	public boolean method_11051() {
		return true;
	}
}
