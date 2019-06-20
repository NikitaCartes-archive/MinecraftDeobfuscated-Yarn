package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2668 implements class_2596<class_2602> {
	public static final String[] field_12200 = new String[]{"block.minecraft.bed.not_valid"};
	private int field_12199;
	private float field_12198;

	public class_2668() {
	}

	public class_2668(int i, float f) {
		this.field_12199 = i;
		this.field_12198 = f;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12199 = arg.readUnsignedByte();
		this.field_12198 = arg.readFloat();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeByte(this.field_12199);
		arg.writeFloat(this.field_12198);
	}

	public void method_11490(class_2602 arg) {
		arg.method_11085(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11491() {
		return this.field_12199;
	}

	@Environment(EnvType.CLIENT)
	public float method_11492() {
		return this.field_12198;
	}
}
