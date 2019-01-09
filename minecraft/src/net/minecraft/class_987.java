package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_987<T extends class_1309, M extends class_583<T>, A extends class_572<T>> extends class_970<T, M, A> {
	public class_987(class_3883<T, M> arg, A arg2, A arg3) {
		super(arg, arg2, arg3);
	}

	protected void method_4189(A arg, class_1304 arg2) {
		this.method_4190(arg);
		switch (arg2) {
			case field_6169:
				arg.field_3398.field_3665 = true;
				arg.field_3394.field_3665 = true;
				break;
			case field_6174:
				arg.field_3391.field_3665 = true;
				arg.field_3401.field_3665 = true;
				arg.field_3390.field_3665 = true;
				break;
			case field_6172:
				arg.field_3391.field_3665 = true;
				arg.field_3392.field_3665 = true;
				arg.field_3397.field_3665 = true;
				break;
			case field_6166:
				arg.field_3392.field_3665 = true;
				arg.field_3397.field_3665 = true;
		}
	}

	protected void method_4190(A arg) {
		arg.method_2805(false);
	}
}
