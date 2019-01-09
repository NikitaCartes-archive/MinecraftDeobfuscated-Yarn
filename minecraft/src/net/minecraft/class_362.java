package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_362 extends class_332 implements class_363 {
	@Nullable
	private class_364 field_2196;
	private boolean field_2195;

	protected abstract List<? extends class_364> method_1968();

	private final boolean method_1970() {
		return this.field_2195;
	}

	protected final void method_1966(boolean bl) {
		this.field_2195 = bl;
	}

	@Nullable
	@Override
	public class_364 getFocused() {
		return this.field_2196;
	}

	protected void method_1967(@Nullable class_364 arg) {
		this.field_2196 = arg;
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		for (class_364 lv : this.method_1968()) {
			boolean bl = lv.method_16807(d, e, i);
			if (bl) {
				this.method_1973(lv);
				if (i == 0) {
					this.method_1966(true);
				}

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean method_16805(int i, int j, int k) {
		return class_363.super.method_16805(i, j, k);
	}

	@Override
	public boolean method_16801(double d, double e, int i, double f, double g) {
		return this.getFocused() != null && this.method_1970() && i == 0 ? this.getFocused().method_16801(d, e, i, f, g) : false;
	}

	@Override
	public boolean method_16804(double d, double e, int i) {
		this.method_1966(false);
		return class_363.super.method_16804(d, e, i);
	}

	public void method_1973(@Nullable class_364 arg) {
		this.method_1969(arg, this.method_1968().indexOf(this.getFocused()));
	}

	public void method_1971() {
		int i = this.method_1968().indexOf(this.getFocused());
		int j = i == -1 ? 0 : (i + 1) % this.method_1968().size();
		this.method_1969(this.method_1972(j), i);
	}

	@Nullable
	private class_364 method_1972(int i) {
		List<? extends class_364> list = this.method_1968();
		int j = list.size();

		for (int k = 0; k < j; k++) {
			class_364 lv = (class_364)list.get((i + k) % j);
			if (lv.method_16015()) {
				return lv;
			}
		}

		return null;
	}

	private void method_1969(@Nullable class_364 arg, int i) {
		class_364 lv = i == -1 ? null : (class_364)this.method_1968().get(i);
		if (lv != arg) {
			if (lv != null) {
				lv.method_1974(false);
			}

			if (arg != null) {
				arg.method_1974(true);
			}

			this.method_1967(arg);
		}
	}
}
