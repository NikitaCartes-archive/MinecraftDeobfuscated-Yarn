package net.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_353 extends class_4265<class_353.class_354> {
	public class_353(class_310 arg, int i, int j, int k, int l, int m, class_316... args) {
		super(arg, i, j, k, l, m);
		this.field_19089 = false;
		this.method_1901(new class_353.class_354(arg.field_1690, i, class_316.field_1931));

		for (int n = 0; n < args.length; n += 2) {
			class_316 lv = args[n];
			if (n < args.length - 1) {
				this.method_1901(new class_353.class_354(arg.field_1690, i, lv, args[n + 1]));
			} else {
				this.method_1901(new class_353.class_354(arg.field_1690, i, lv));
			}
		}
	}

	@Override
	public int method_20053() {
		return 400;
	}

	@Override
	protected int method_20078() {
		return super.method_20078() + 32;
	}

	@Environment(EnvType.CLIENT)
	public static class class_354 extends class_4265.class_4266<class_353.class_354> {
		private final List<class_339> field_18214;

		private class_354(List<class_339> list) {
			this.field_18214 = list;
		}

		public class_354(class_315 arg, int i, class_316 arg2) {
			this(ImmutableList.of(arg2.method_18520(arg, i / 2 - 155, 0, 310)));
		}

		public class_354(class_315 arg, int i, class_316 arg2, class_316 arg3) {
			this(ImmutableList.of(arg2.method_18520(arg, i / 2 - 155, 0, 150), arg3.method_18520(arg, i / 2 - 155 + 160, 0, 150)));
		}

		@Override
		public void method_1903(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			this.field_18214.forEach(arg -> {
				arg.y = j;
				arg.render(n, o, f);
			});
		}

		@Override
		public List<? extends class_364> children() {
			return this.field_18214;
		}
	}
}
