package net.minecraft;

import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2739 implements class_2596<class_2602> {
	private int field_12476;
	private List<class_2945.class_2946<?>> field_12477;

	public class_2739() {
	}

	public class_2739(int i, class_2945 arg, boolean bl) {
		this.field_12476 = i;
		if (bl) {
			this.field_12477 = arg.method_12793();
			arg.method_12792();
		} else {
			this.field_12477 = arg.method_12781();
		}
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12476 = arg.method_10816();
		this.field_12477 = class_2945.method_12788(arg);
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12476);
		class_2945.method_12787(this.field_12477, arg);
	}

	public void method_11808(class_2602 arg) {
		arg.method_11093(this);
	}

	@Environment(EnvType.CLIENT)
	public List<class_2945.class_2946<?>> method_11809() {
		return this.field_12477;
	}

	@Environment(EnvType.CLIENT)
	public int method_11807() {
		return this.field_12476;
	}
}
