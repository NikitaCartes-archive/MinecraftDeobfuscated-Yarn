package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2748 implements class_2596<class_2602> {
	private float field_12580;
	private int field_12582;
	private int field_12581;

	public class_2748() {
	}

	public class_2748(float f, int i, int j) {
		this.field_12580 = f;
		this.field_12582 = i;
		this.field_12581 = j;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12580 = arg.readFloat();
		this.field_12581 = arg.method_10816();
		this.field_12582 = arg.method_10816();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeFloat(this.field_12580);
		arg.method_10804(this.field_12581);
		arg.method_10804(this.field_12582);
	}

	public void method_11829(class_2602 arg) {
		arg.method_11101(this);
	}

	@Environment(EnvType.CLIENT)
	public float method_11830() {
		return this.field_12580;
	}

	@Environment(EnvType.CLIENT)
	public int method_11827() {
		return this.field_12582;
	}

	@Environment(EnvType.CLIENT)
	public int method_11828() {
		return this.field_12581;
	}
}
