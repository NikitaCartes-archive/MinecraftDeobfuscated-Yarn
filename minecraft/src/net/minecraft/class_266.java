package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_266 {
	private final class_269 field_1404;
	private final String field_1405;
	private final class_274 field_1406;
	private class_2561 field_1402;
	private class_274.class_275 field_1403;

	public class_266(class_269 arg, String string, class_274 arg2, class_2561 arg3, class_274.class_275 arg4) {
		this.field_1404 = arg;
		this.field_1405 = string;
		this.field_1406 = arg2;
		this.field_1402 = arg3;
		this.field_1403 = arg4;
	}

	@Environment(EnvType.CLIENT)
	public class_269 method_1117() {
		return this.field_1404;
	}

	public String method_1113() {
		return this.field_1405;
	}

	public class_274 method_1116() {
		return this.field_1406;
	}

	public class_2561 method_1114() {
		return this.field_1402;
	}

	public class_2561 method_1120() {
		return class_2564.method_10885(
			this.field_1402.method_10853().method_10859(arg -> arg.method_10949(new class_2568(class_2568.class_2569.field_11762, new class_2585(this.method_1113()))))
		);
	}

	public void method_1121(class_2561 arg) {
		this.field_1402 = arg;
		this.field_1404.method_1175(this);
	}

	public class_274.class_275 method_1118() {
		return this.field_1403;
	}

	public void method_1115(class_274.class_275 arg) {
		this.field_1403 = arg;
		this.field_1404.method_1175(this);
	}
}
