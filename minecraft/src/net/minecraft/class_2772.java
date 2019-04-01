package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2772 implements class_2596<class_2602> {
	private class_2561 field_12683;
	private class_2561 field_12684;

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12683 = arg.method_10808();
		this.field_12684 = arg.method_10808();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10805(this.field_12683);
		arg.method_10805(this.field_12684);
	}

	public void method_11907(class_2602 arg) {
		arg.method_11105(this);
	}

	@Environment(EnvType.CLIENT)
	public class_2561 method_11908() {
		return this.field_12683;
	}

	@Environment(EnvType.CLIENT)
	public class_2561 method_11906() {
		return this.field_12684;
	}
}
