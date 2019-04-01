package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;

public class class_4112 extends class_4097<class_1308> {
	@Nullable
	private class_11 field_18369;
	@Nullable
	private class_2338 field_18370;
	private float field_18371;
	private int field_18964;

	public class_4112(int i) {
		super(i);
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18449, class_4141.field_18457), Pair.of(class_4140.field_18445, class_4141.field_18456));
	}

	protected boolean method_18978(class_3218 arg, class_1308 arg2) {
		class_4095<?> lv = arg2.method_18868();
		class_4142 lv2 = (class_4142)lv.method_19543(class_4140.field_18445).get();
		if (!this.method_18980(arg2, lv2) && this.method_18977(arg2, lv2)) {
			this.field_18370 = lv2.method_19094().method_18989();
			return true;
		} else {
			lv.method_18875(class_4140.field_18445);
			return false;
		}
	}

	protected boolean method_18979(class_3218 arg, class_1308 arg2, long l) {
		if (this.field_18369 != null && this.field_18370 != null) {
			Optional<class_4142> optional = arg2.method_18868().method_19543(class_4140.field_18445);
			class_1408 lv = arg2.method_5942();
			return !lv.method_6357() && optional.isPresent() && !this.method_18980(arg2, (class_4142)optional.get());
		} else {
			return false;
		}
	}

	protected void method_18981(class_3218 arg, class_1308 arg2, long l) {
		arg2.method_5942().method_6340();
		arg2.method_18868().method_18875(class_4140.field_18445);
		arg2.method_18868().method_18875(class_4140.field_18449);
		this.field_18369 = null;
	}

	protected void method_18982(class_3218 arg, class_1308 arg2, long l) {
		arg2.method_18868().method_18878(class_4140.field_18449, this.field_18369);
		arg2.method_5942().method_6334(this.field_18369, (double)this.field_18371);
		this.field_18964 = arg.method_8409().nextInt(10);
	}

	protected void method_18983(class_3218 arg, class_1308 arg2, long l) {
		this.field_18964--;
		if (this.field_18964 <= 0) {
			class_11 lv = arg2.method_5942().method_6345();
			if (this.field_18369 != lv) {
				this.field_18369 = lv;
				arg2.method_18868().method_18878(class_4140.field_18449, lv);
			}

			if (lv != null && this.field_18370 != null) {
				class_4142 lv2 = (class_4142)arg2.method_18868().method_19543(class_4140.field_18445).get();
				if (lv2.method_19094().method_18989().method_10262(this.field_18370) > 4.0 && this.method_18977(arg2, lv2)) {
					this.field_18370 = lv2.method_19094().method_18989();
					this.method_18982(arg, arg2, l);
				}
			}
		}
	}

	private boolean method_18977(class_1308 arg, class_4142 arg2) {
		class_2338 lv = arg2.method_19094().method_18989();
		this.field_18369 = arg.method_5942().method_6348(lv);
		this.field_18371 = arg2.method_19095();
		if (!this.method_18980(arg, arg2)) {
			if (this.field_18369 != null) {
				return true;
			}

			class_243 lv2 = class_1414.method_6373((class_1314)arg, 10, 7, new class_243(lv));
			if (lv2 != null) {
				this.field_18369 = arg.method_5942().method_6352(lv2.field_1352, lv2.field_1351, lv2.field_1350);
				return this.field_18369 != null;
			}
		}

		return false;
	}

	private boolean method_18980(class_1308 arg, class_4142 arg2) {
		return arg2.method_19094().method_18989().method_19455(new class_2338(arg)) <= arg2.method_19096();
	}
}
