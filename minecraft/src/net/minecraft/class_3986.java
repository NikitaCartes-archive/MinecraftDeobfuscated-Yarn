package net.minecraft;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3986 extends class_1501 {
	private int field_17716 = 47999;

	public class_3986(class_1299<? extends class_3986> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_6807() {
		return true;
	}

	@Override
	protected class_1501 method_18004() {
		return class_1299.field_17714.method_5883(this.field_6002);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("DespawnDelay", this.field_17716);
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		if (arg.method_10573("DespawnDelay", 99)) {
			this.field_17716 = arg.method_10550("DespawnDelay");
		}
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.field_6201.method_6277(1, new class_1374(this, 2.0));
		this.field_6185.method_6277(1, new class_3986.class_3987(this));
	}

	@Override
	protected void method_6726(class_1657 arg) {
		class_1297 lv = this.method_5933();
		if (!(lv instanceof class_3989)) {
			super.method_6726(arg);
		}
	}

	@Override
	public void method_6007() {
		super.method_6007();
		if (!this.field_6002.field_9236) {
			this.method_20501();
		}
	}

	private void method_20501() {
		if (this.method_20502()) {
			this.field_17716 = this.method_20503() ? ((class_3989)this.method_5933()).method_18014() - 1 : this.field_17716 - 1;
			if (this.field_17716 <= 0) {
				this.method_5932(true, false);
				this.method_5650();
			}
		}
	}

	private boolean method_20502() {
		return !this.method_6727() && !this.method_20504() && !this.method_5817();
	}

	private boolean method_20503() {
		return this.method_5933() instanceof class_3989;
	}

	private boolean method_20504() {
		return this.method_5934() && !this.method_20503();
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		class_1315 lv = super.method_5943(arg, arg2, arg3, arg4, arg5);
		if (arg3 == class_3730.field_16467) {
			this.method_5614(0);
		}

		return lv;
	}

	public class class_3987 extends class_1405 {
		private final class_1501 field_17718;
		private class_1309 field_17719;
		private int field_17720;

		public class_3987(class_1501 arg2) {
			super(arg2, false);
			this.field_17718 = arg2;
			this.method_6265(EnumSet.of(class_1352.class_4134.field_18408));
		}

		@Override
		public boolean method_6264() {
			if (!this.field_17718.method_5934()) {
				return false;
			} else {
				class_1297 lv = this.field_17718.method_5933();
				if (!(lv instanceof class_3989)) {
					return false;
				} else {
					class_3989 lv2 = (class_3989)lv;
					this.field_17719 = lv2.method_6065();
					int i = lv2.method_6117();
					return i != this.field_17720 && this.method_6328(this.field_17719, class_4051.field_18092);
				}
			}
		}

		@Override
		public void method_6269() {
			this.field_6660.method_5980(this.field_17719);
			class_1297 lv = this.field_17718.method_5933();
			if (lv instanceof class_3989) {
				this.field_17720 = ((class_3989)lv).method_6117();
			}

			super.method_6269();
		}
	}
}
