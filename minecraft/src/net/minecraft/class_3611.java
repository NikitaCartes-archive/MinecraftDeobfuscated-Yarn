package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_3611 {
	public static final class_2361<class_3610> field_15904 = new class_2361<>();
	protected final class_2689<class_3611, class_3610> field_15905;
	private class_3610 field_15903;

	protected class_3611() {
		class_2689.class_2690<class_3611, class_3610> lv = new class_2689.class_2690<>(this);
		this.method_15775(lv);
		this.field_15905 = lv.method_11668(class_3613::new);
		this.method_15781(this.field_15905.method_11664());
	}

	protected void method_15775(class_2689.class_2690<class_3611, class_3610> arg) {
	}

	public class_2689<class_3611, class_3610> method_15783() {
		return this.field_15905;
	}

	protected final void method_15781(class_3610 arg) {
		this.field_15903 = arg;
	}

	public final class_3610 method_15785() {
		return this.field_15903;
	}

	@Environment(EnvType.CLIENT)
	protected abstract class_1921 method_15786();

	public abstract class_1792 method_15774();

	@Environment(EnvType.CLIENT)
	protected void method_15776(class_1937 arg, class_2338 arg2, class_3610 arg3, Random random) {
	}

	protected void method_15778(class_1937 arg, class_2338 arg2, class_3610 arg3) {
	}

	protected void method_15792(class_1937 arg, class_2338 arg2, class_3610 arg3, Random random) {
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	protected class_2394 method_15787() {
		return null;
	}

	protected abstract boolean method_15777(class_3610 arg, class_1922 arg2, class_2338 arg3, class_3611 arg4, class_2350 arg5);

	protected abstract class_243 method_15782(class_1922 arg, class_2338 arg2, class_3610 arg3);

	public abstract int method_15789(class_1941 arg);

	protected boolean method_15795() {
		return false;
	}

	protected boolean method_15794() {
		return false;
	}

	protected abstract float method_15784();

	public abstract float method_15788(class_3610 arg, class_1922 arg2, class_2338 arg3);

	public abstract float method_20784(class_3610 arg);

	protected abstract class_2680 method_15790(class_3610 arg);

	public abstract boolean method_15793(class_3610 arg);

	public abstract int method_15779(class_3610 arg);

	public boolean method_15780(class_3611 arg) {
		return arg == this;
	}

	public boolean method_15791(class_3494<class_3611> arg) {
		return arg.method_15141(this);
	}

	public abstract class_265 method_17775(class_3610 arg, class_1922 arg2, class_2338 arg3);
}
