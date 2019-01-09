package net.minecraft;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1092 implements class_3302 {
	private Map<class_2960, class_1087> field_5408;
	private final class_1059 field_5409;
	private final class_773 field_5410;
	private class_1087 field_5407;

	public class_1092(class_1059 arg) {
		this.field_5409 = arg;
		this.field_5410 = new class_773(this);
	}

	@Override
	public void method_14491(class_3300 arg) {
		this.field_5408 = new class_1088(arg, this.field_5409).method_4734();
		this.field_5407 = (class_1087)this.field_5408.get(class_1088.field_5374);
		this.field_5410.method_3341();
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
}
