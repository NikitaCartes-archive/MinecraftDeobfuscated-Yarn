package net.minecraft;

import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3952 implements class_3949 {
	private final class_3949 field_17472;
	private final class_3846<Runnable> field_17473;

	public class_3952(class_3949 arg, Executor executor) {
		this.field_17472 = arg;
		this.field_17473 = class_3846.method_16902(executor, "progressListener");
	}

	@Override
	public void method_17669(class_1923 arg) {
		this.field_17473.method_16901(() -> this.field_17472.method_17669(arg));
	}

	@Override
	public void method_17670(class_1923 arg, @Nullable class_2806 arg2) {
		this.field_17473.method_16901(() -> this.field_17472.method_17670(arg, arg2));
	}

	@Override
	public void method_17671() {
		this.field_17473.method_16901(this.field_17472::method_17671);
	}
}
