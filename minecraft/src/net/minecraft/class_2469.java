package net.minecraft;

import java.nio.file.Path;

public class class_2469 extends class_2474<class_3611> {
	public class_2469(class_2403 arg) {
		super(arg, class_2378.field_11154);
	}

	@Override
	protected void method_10514() {
		this.method_10512(class_3486.field_15517).method_15150(class_3612.field_15910, class_3612.field_15909);
		this.method_10512(class_3486.field_15518).method_15150(class_3612.field_15908, class_3612.field_15907);
	}

	@Override
	protected Path method_10510(class_2960 arg) {
		return this.field_11483.method_10313().resolve("data/" + arg.method_12836() + "/tags/fluids/" + arg.method_12832() + ".json");
	}

	@Override
	public String method_10321() {
		return "Fluid Tags";
	}

	@Override
	protected void method_10511(class_3503<class_3611> arg) {
		class_3486.method_15096(arg);
	}
}
