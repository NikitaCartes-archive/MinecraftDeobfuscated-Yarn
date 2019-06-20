package net.minecraft;

import com.google.common.collect.Multimap;
import java.util.Set;

public abstract class class_1766 extends class_1831 {
	private final Set<class_2248> field_7941;
	protected final float field_7940;

	protected class_1766(class_1832 arg, Set<class_2248> set, class_1792.class_1793 arg2) {
		super(arg, arg2);
		this.field_7941 = set;
		this.field_7940 = arg.method_8027();
	}

	@Override
	public float method_7865(class_1799 arg, class_2680 arg2) {
		return this.field_7941.contains(arg2.method_11614()) ? this.field_7940 : 1.0F;
	}

	@Override
	public boolean method_7873(class_1799 arg, class_1309 arg2, class_1309 arg3) {
		arg.method_7956(2, arg3, argx -> argx.method_20235(class_1304.field_6173));
		return true;
	}

	@Override
	public boolean method_7879(class_1799 arg, class_1937 arg2, class_2680 arg3, class_2338 arg4, class_1309 arg5) {
		if (!arg2.field_9236 && arg3.method_11579(arg2, arg4) != 0.0F) {
			arg.method_7956(1, arg5, argx -> argx.method_20235(class_1304.field_6173));
		}

		return true;
	}

	protected abstract class_5117 method_26739();

	@Override
	public Multimap<String, class_1322> method_7844(class_1304 arg) {
		Multimap<String, class_1322> multimap = super.method_7844(arg);
		if (arg == class_1304.field_6173) {
			this.method_26739().method_26741(this.method_8022(), multimap);
		}

		return multimap;
	}
}
