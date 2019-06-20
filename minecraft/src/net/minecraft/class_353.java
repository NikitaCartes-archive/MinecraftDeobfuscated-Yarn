package net.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_353 extends class_4265<class_353.class_354> {
	public class_353(class_310 arg, int i, int j, int k, int l, int m) {
		super(arg, i, j, k, l, m);
		this.centerListVertically = false;
	}

	public int method_20406(class_316 arg) {
		return this.addEntry(class_353.class_354.method_20409(this.minecraft.field_1690, this.width, arg));
	}

	public void method_20407(class_316 arg, @Nullable class_316 arg2) {
		this.addEntry(class_353.class_354.method_20410(this.minecraft.field_1690, this.width, arg, arg2));
	}

	public void method_20408(class_316[] args) {
		for (int i = 0; i < args.length; i += 2) {
			this.method_20407(args[i], i < args.length - 1 ? args[i + 1] : null);
		}
	}

	@Override
	public int getRowWidth() {
		return 400;
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 32;
	}

	@Environment(EnvType.CLIENT)
	public static class class_354 extends class_4265.class_4266<class_353.class_354> {
		private final List<class_339> field_18214;

		private class_354(List<class_339> list) {
			this.field_18214 = list;
		}

		public static class_353.class_354 method_20409(class_315 arg, int i, class_316 arg2) {
			return new class_353.class_354(ImmutableList.of(arg2.method_18520(arg, i / 2 - 155, 0, 310)));
		}

		public static class_353.class_354 method_20410(class_315 arg, int i, class_316 arg2, @Nullable class_316 arg3) {
			class_339 lv = arg2.method_18520(arg, i / 2 - 155, 0, 150);
			return arg3 == null
				? new class_353.class_354(ImmutableList.of(lv))
				: new class_353.class_354(ImmutableList.of(lv, arg3.method_18520(arg, i / 2 - 155 + 160, 0, 150)));
		}

		@Override
		public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			this.field_18214.forEach(arg -> {
				arg.field_23659 = j;
				arg.render(n, o, f);
			});
		}

		@Override
		public List<? extends class_364> children() {
			return this.field_18214;
		}
	}
}
