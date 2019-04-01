package net.minecraft;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1092 extends class_4080<class_1088> {
	private Map<class_2960, class_1087> field_5408;
	private final class_1059 field_5409;
	private final class_773 field_5410;
	private class_1087 field_5407;

	public class_1092(class_1059 arg) {
		this.field_5409 = arg;
		this.field_5410 = new class_773(this);
	}

	public class_1087 method_4742(class_1091 arg) {
		return (class_1087)this.field_5408.getOrDefault(arg, this.field_5407);
	}

	public class_1087 method_4744() {
		return this.field_5407;
	}

	public class_773 method_4743() {
		return this.field_5410;
	}

	protected class_1088 method_18178(class_3300 arg, class_3695 arg2) {
		arg2.method_16065();
		class_1088 lv = new class_1088(arg, this.field_5409, arg2);
		arg2.method_16066();
		return lv;
	}

	protected void method_18179(class_1088 arg, class_3300 arg2, class_3695 arg3) {
		arg3.method_16065();
		arg3.method_15396("upload");
		arg.method_18177(arg3);
		this.field_5408 = arg.method_4734();
		this.field_5407 = (class_1087)this.field_5408.get(class_1088.field_5374);
		arg3.method_15405("cache");
		this.field_5410.method_3341();
		arg3.method_15407();
		arg3.method_16066();
	}
}
