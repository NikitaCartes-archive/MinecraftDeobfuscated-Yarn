package net.minecraft;

import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1511 extends class_1297 {
	private static final class_2940<Optional<class_2338>> field_7033 = class_2945.method_12791(class_1511.class, class_2943.field_13315);
	private static final class_2940<Boolean> field_7035 = class_2945.method_12791(class_1511.class, class_2943.field_13323);
	public int field_7034;

	public class_1511(class_1299<? extends class_1511> arg, class_1937 arg2) {
		super(arg, arg2);
		this.field_6033 = true;
		this.field_7034 = this.field_5974.nextInt(100000);
	}

	public class_1511(class_1937 arg, double d, double e, double f) {
		this(class_1299.field_6110, arg);
		this.method_5814(d, e, f);
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	protected void method_5693() {
		this.method_5841().method_12784(field_7033, Optional.empty());
		this.method_5841().method_12784(field_7035, true);
	}

	@Override
	public void method_5773() {
		this.field_6014 = this.field_5987;
		this.field_6036 = this.field_6010;
		this.field_5969 = this.field_6035;
		this.field_7034++;
		if (!this.field_6002.field_9236) {
			class_2338 lv = new class_2338(this);
			if (this.field_6002.field_9247 instanceof class_2880 && this.field_6002.method_8320(lv).method_11588()) {
				this.field_6002.method_8501(lv, class_2246.field_10036.method_9564());
			}
		}
	}

	@Override
	protected void method_5652(class_2487 arg) {
		if (this.method_6838() != null) {
			arg.method_10566("BeamTarget", class_2512.method_10692(this.method_6838()));
		}

		arg.method_10556("ShowBottom", this.method_6836());
	}

	@Override
	protected void method_5749(class_2487 arg) {
		if (arg.method_10573("BeamTarget", 10)) {
			this.method_6837(class_2512.method_10691(arg.method_10562("BeamTarget")));
		}

		if (arg.method_10573("ShowBottom", 1)) {
			this.method_6839(arg.method_10577("ShowBottom"));
		}
	}

	@Override
	public boolean method_5863() {
		return true;
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (this.method_5679(arg)) {
			return false;
		} else if (arg.method_5529() instanceof class_1510) {
			return false;
		} else {
			if (!this.field_5988 && !this.field_6002.field_9236) {
				this.method_5650();
				if (!arg.method_5535()) {
					this.field_6002.method_8437(null, this.field_5987, this.field_6010, this.field_6035, 6.0F, class_1927.class_4179.field_18687);
				}

				this.method_6835(arg);
			}

			return true;
		}
	}

	@Override
	public void method_5768() {
		this.method_6835(class_1282.field_5869);
		super.method_5768();
	}

	private void method_6835(class_1282 arg) {
		if (this.field_6002.field_9247 instanceof class_2880) {
			class_2880 lv = (class_2880)this.field_6002.field_9247;
			class_2881 lv2 = lv.method_12513();
			if (lv2 != null) {
				lv2.method_12526(this, arg);
			}
		}
	}

	public void method_6837(@Nullable class_2338 arg) {
		this.method_5841().method_12778(field_7033, Optional.ofNullable(arg));
	}

	@Nullable
	public class_2338 method_6838() {
		return (class_2338)this.method_5841().method_12789(field_7033).orElse(null);
	}

	public void method_6839(boolean bl) {
		this.method_5841().method_12778(field_7035, bl);
	}

	public boolean method_6836() {
		return this.method_5841().method_12789(field_7035);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_5640(double d) {
		return super.method_5640(d) || this.method_6838() != null;
	}

	@Override
	public class_2596<?> method_18002() {
		return new class_2604(this);
	}
}
