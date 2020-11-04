package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;

@Environment(EnvType.CLIENT)
public class class_5607 {
	private final class_5609 field_27723;
	private final class_5608 field_27724;

	private class_5607(class_5609 arg, class_5608 arg2) {
		this.field_27723 = arg;
		this.field_27724 = arg2;
	}

	public ModelPart method_32109() {
		return this.field_27723.method_32111().method_32112(this.field_27724.field_27725, this.field_27724.field_27726);
	}

	public static class_5607 method_32110(class_5609 arg, int i, int j) {
		return new class_5607(arg, new class_5608(i, j));
	}
}
