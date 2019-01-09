package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2775 implements class_2596<class_2602> {
	private int field_12694;
	private int field_12693;
	private int field_12692;

	public class_2775() {
	}

	public class_2775(int i, int j, int k) {
		this.field_12694 = i;
		this.field_12693 = j;
		this.field_12692 = k;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12694 = arg.method_10816();
		this.field_12693 = arg.method_10816();
		this.field_12692 = arg.method_10816();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12694);
		arg.method_10804(this.field_12693);
		arg.method_10804(this.field_12692);
	}

	public void method_11914(class_2602 arg) {
		arg.method_11150(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11915() {
		return this.field_12694;
	}

	@Environment(EnvType.CLIENT)
	public int method_11912() {
		return this.field_12693;
	}

	@Environment(EnvType.CLIENT)
	public int method_11913() {
		return this.field_12692;
	}
}
