package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;

public class class_2453 extends class_2248 {
	public static final class_2746 field_11413 = class_2459.field_11446;

	public class_2453(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.method_9564().method_11657(field_11413, Boolean.valueOf(false)));
	}

	@Override
	public int method_9593(class_2680 arg) {
		return arg.method_11654(field_11413) ? super.method_9593(arg) : 0;
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4) {
		super.method_9615(arg, arg2, arg3, arg4);
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		return this.method_9564().method_11657(field_11413, Boolean.valueOf(arg.method_8045().method_8479(arg.method_8037())));
	}

	@Override
	public void method_9612(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2248 arg4, class_2338 arg5) {
		if (!arg2.field_9236) {
			boolean bl = (Boolean)arg.method_11654(field_11413);
			if (bl != arg2.method_8479(arg3)) {
				if (bl) {
					arg2.method_8397().method_8676(arg3, this, 4);
				} else {
					arg2.method_8652(arg3, arg.method_11572(field_11413), 2);
				}
			}
		}
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (!arg2.field_9236) {
			if ((Boolean)arg.method_11654(field_11413) && !arg2.method_8479(arg3)) {
				arg2.method_8652(arg3, arg.method_11572(field_11413), 2);
			}
		}
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11413);
	}
}
