package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2822 implements class_2596<class_2792> {
	private int field_12868;
	private int field_12867;

	public class_2822() {
	}

	@Environment(EnvType.CLIENT)
	public class_2822(int i, int j) {
		this.field_12868 = i;
		this.field_12867 = j;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12868 = arg.method_10816();
		this.field_12867 = arg.method_10816();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12868);
		arg.method_10804(this.field_12867);
	}

	public void method_12243(class_2792 arg) {
		arg.method_12074(this);
	}

	public int method_12245() {
		return this.field_12868;
	}

	public int method_12244() {
		return this.field_12867;
	}
}
