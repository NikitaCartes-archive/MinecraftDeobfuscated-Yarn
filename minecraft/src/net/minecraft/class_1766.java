package net.minecraft;

import com.google.common.collect.Multimap;
import java.util.Set;

public class class_1766 extends class_1831 {
	private final Set<class_2248> field_7941;
	protected final float field_7940;
	protected final float field_7939;
	protected final float field_7938;

	protected class_1766(float f, float g, class_1832 arg, Set<class_2248> set, class_1792.class_1793 arg2) {
		super(arg, arg2);
		this.field_7941 = set;
		this.field_7940 = arg.method_8027();
		this.field_7939 = f + arg.method_8028();
		this.field_7938 = g;
	}

	@Override
	public float method_7865(class_1799 arg, class_2680 arg2) {
		return this.field_7941.contains(arg2.method_11614()) ? this.field_7940 : 1.0F;
	}

	@Override
	public boolean method_7873(class_1799 arg, class_1309 arg2, class_1309 arg3) {
		arg.method_7956(2, arg3);
		return true;
	}

	@Override
	public boolean method_7879(class_1799 arg, class_1937 arg2, class_2680 arg3, class_2338 arg4, class_1309 arg5) {
		if (!arg2.field_9236 && arg3.method_11579(arg2, arg4) != 0.0F) {
			arg.method_7956(1, arg5);
		}

		return true;
	}

	@Override
	public Multimap<String, class_1322> method_7844(class_1304 arg) {
		Multimap<String, class_1322> multimap = super.method_7844(arg);
		if (arg == class_1304.field_6173) {
			multimap.put(class_1612.field_7363.method_6167(), new class_1322(field_8006, "Tool modifier", (double)this.field_7939, class_1322.class_1323.field_6328));
			multimap.put(class_1612.field_7356.method_6167(), new class_1322(field_8001, "Tool modifier", (double)this.field_7938, class_1322.class_1323.field_6328));
		}

		return multimap;
	}
}
