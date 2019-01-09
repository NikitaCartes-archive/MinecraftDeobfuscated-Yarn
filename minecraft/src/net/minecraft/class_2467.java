package net.minecraft;

import java.nio.file.Path;

public class class_2467 extends class_2474<class_1299<?>> {
	public class_2467(class_2403 arg) {
		super(arg, class_2378.field_11145);
	}

	@Override
	protected void method_10514() {
		this.method_10512(class_3483.field_15507).method_15150(class_1299.field_6137, class_1299.field_6098, class_1299.field_6076);
	}

	@Override
	protected Path method_10510(class_2960 arg) {
		return this.field_11483.method_10313().resolve("data/" + arg.method_12836() + "/tags/entity_types/" + arg.method_12832() + ".json");
	}

	@Override
	public String method_10321() {
		return "Entity Type Tags";
	}

	@Override
	protected void method_10511(class_3503<class_1299<?>> arg) {
		class_3483.method_15078(arg);
	}
}
