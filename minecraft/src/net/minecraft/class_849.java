package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_849 {
	public static final class_849 field_4451 = new class_849() {
		@Override
		protected void method_3643(class_1921 arg) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void method_3647(class_1921 arg) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean method_3650(class_2350 arg, class_2350 arg2) {
			return false;
		}
	};
	private final boolean[] field_4450 = new boolean[class_1921.values().length];
	private final boolean[] field_4452 = new boolean[class_1921.values().length];
	private boolean field_4454 = true;
	private final List<class_2586> field_4456 = Lists.<class_2586>newArrayList();
	private class_854 field_4455 = new class_854();
	private class_287.class_288 field_4453;

	public boolean method_3645() {
		return this.field_4454;
	}

	protected void method_3643(class_1921 arg) {
		this.field_4454 = false;
		this.field_4450[arg.ordinal()] = true;
	}

	public boolean method_3641(class_1921 arg) {
		return !this.field_4450[arg.ordinal()];
	}

	public void method_3647(class_1921 arg) {
		this.field_4452[arg.ordinal()] = true;
	}

	public boolean method_3649(class_1921 arg) {
		return this.field_4452[arg.ordinal()];
	}

	public List<class_2586> method_3642() {
		return this.field_4456;
	}

	public void method_3646(class_2586 arg) {
		this.field_4456.add(arg);
	}

	public boolean method_3650(class_2350 arg, class_2350 arg2) {
		return this.field_4455.method_3695(arg, arg2);
	}

	public void method_3640(class_854 arg) {
		this.field_4455 = arg;
	}

	public class_287.class_288 method_3644() {
		return this.field_4453;
	}

	public void method_3648(class_287.class_288 arg) {
		this.field_4453 = arg;
	}
}
