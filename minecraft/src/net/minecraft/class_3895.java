package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3895 implements class_2596<class_2602> {
	private class_1268 field_17199;

	public class_3895() {
	}

	public class_3895(class_1268 arg) {
		this.field_17199 = arg;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_17199 = arg.method_10818(class_1268.class);
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10817(this.field_17199);
	}

	public void method_17187(class_2602 arg) {
		arg.method_17186(this);
	}

	@Environment(EnvType.CLIENT)
	public class_1268 method_17188() {
		return this.field_17199;
	}
}
