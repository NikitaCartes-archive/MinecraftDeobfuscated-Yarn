package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.Set;

public class class_4111 extends class_4097<class_1646> {
	private long field_18368;

	public class_4111() {
		super(350, 350);
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18448, class_4141.field_18456), Pair.of(class_4140.field_18442, class_4141.field_18456));
	}

	protected boolean method_19571(class_3218 arg, class_1646 arg2) {
		return this.method_18972(arg2);
	}

	protected boolean method_18973(class_3218 arg, class_1646 arg2, long l) {
		return l <= this.field_18368 && this.method_18972(arg2);
	}

	protected void method_18974(class_3218 arg, class_1646 arg2, long l) {
		class_1646 lv = this.method_19570(arg2);
		class_4215.method_19548(arg2, lv);
		arg.method_8421(lv, (byte)18);
		arg.method_8421(arg2, (byte)18);
		int i = 275 + arg2.method_6051().nextInt(50);
		this.field_18368 = l + (long)i;
	}

	protected void method_18975(class_3218 arg, class_1646 arg2, long l) {
		class_1646 lv = this.method_19570(arg2);
		if (!(arg2.method_5858(lv) > 5.0)) {
			class_4215.method_19548(arg2, lv);
			if (l >= this.field_18368) {
				Optional<class_2338> optional = this.method_19573(arg, arg2);
				if (!optional.isPresent()) {
					arg.method_8421(lv, (byte)13);
					arg.method_8421(arg2, (byte)13);
					return;
				}

				arg2.method_19185();
				lv.method_19185();
				Optional<class_1646> optional2 = this.method_18970(arg2, lv);
				if (optional2.isPresent()) {
					arg2.method_19193(12);
					lv.method_19193(12);
					this.method_19572(arg, (class_1646)optional2.get(), (class_2338)optional.get());
				} else {
					arg.method_19494().method_19129((class_2338)optional.get());
				}
			}

			if (arg2.method_6051().nextInt(35) == 0) {
				arg.method_8421(lv, (byte)12);
				arg.method_8421(arg2, (byte)12);
			}
		}
	}

	protected void method_18976(class_3218 arg, class_1646 arg2, long l) {
		arg2.method_18868().method_18875(class_4140.field_18448);
	}

	private class_1646 method_19570(class_1646 arg) {
		return (class_1646)arg.method_18868().method_19543(class_4140.field_18448).get();
	}

	private boolean method_18972(class_1646 arg) {
		class_4095<class_1646> lv = arg.method_18868();
		if (!lv.method_19543(class_4140.field_18448).isPresent()) {
			return false;
		} else {
			class_1646 lv2 = this.method_19570(arg);
			return class_4215.method_19551(lv, class_4140.field_18448, class_1299.field_6077) && arg.method_19184() && lv2.method_19184();
		}
	}

	private Optional<class_2338> method_19573(class_3218 arg, class_1646 arg2) {
		return arg.method_19494().method_19126(class_4158.field_18517.method_19164(), argx -> true, new class_2338(arg2), 48);
	}

	private Optional<class_1646> method_18970(class_1646 arg, class_1646 arg2) {
		class_1646 lv = arg.method_7225(arg2);
		if (lv == null) {
			return Optional.empty();
		} else {
			arg.method_5614(6000);
			arg2.method_5614(6000);
			lv.method_5614(-24000);
			lv.method_5808(arg.field_5987, arg.field_6010, arg.field_6035, 0.0F, 0.0F);
			arg.field_6002.method_8649(lv);
			arg.field_6002.method_8421(lv, (byte)12);
			return Optional.of(lv);
		}
	}

	private void method_19572(class_3218 arg, class_1646 arg2, class_2338 arg3) {
		class_4208 lv = class_4208.method_19443(arg.method_8597().method_12460(), arg3);
		arg2.method_18868().method_18878(class_4140.field_18438, lv);
	}
}
