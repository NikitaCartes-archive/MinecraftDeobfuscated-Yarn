package net.minecraft;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1398 extends class_1352 {
	private static final Logger field_6635 = LogManager.getLogger();
	private final class_1308 field_6636;
	private final Predicate<class_1309> field_6634;
	private final class_1400.class_1401 field_6631;
	private class_1309 field_6632;
	private final Class<? extends class_1309> field_6633;

	public class_1398(class_1308 arg, Class<? extends class_1309> class_) {
		this.field_6636 = arg;
		this.field_6633 = class_;
		if (arg instanceof class_1314) {
			field_6635.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");
		}

		this.field_6634 = argx -> {
			double d = this.method_6315();
			if (argx.method_5715()) {
				d *= 0.8F;
			}

			if (argx.method_5767()) {
				return false;
			} else {
				return (double)argx.method_5739(this.field_6636) > d ? false : class_1405.method_6327(this.field_6636, argx, false, true);
			}
		};
		this.field_6631 = new class_1400.class_1401(arg);
	}

	@Override
	public boolean method_6264() {
		double d = this.method_6315();
		List<class_1309> list = this.field_6636.field_6002.method_8390(this.field_6633, this.field_6636.method_5829().method_1009(d, 4.0, d), this.field_6634);
		Collections.sort(list, this.field_6631);
		if (list.isEmpty()) {
			return false;
		} else {
			this.field_6632 = (class_1309)list.get(0);
			return true;
		}
	}

	@Override
	public boolean method_6266() {
		class_1309 lv = this.field_6636.method_5968();
		if (lv == null) {
			return false;
		} else if (!lv.method_5805()) {
			return false;
		} else {
			double d = this.method_6315();
			return this.field_6636.method_5858(lv) > d * d ? false : !(lv instanceof class_3222) || !((class_3222)lv).field_13974.method_14268();
		}
	}

	@Override
	public void method_6269() {
		this.field_6636.method_5980(this.field_6632);
		super.method_6269();
	}

	@Override
	public void method_6270() {
		this.field_6636.method_5980(null);
		super.method_6269();
	}

	protected double method_6315() {
		class_1324 lv = this.field_6636.method_5996(class_1612.field_7365);
		return lv == null ? 16.0 : lv.method_6194();
	}
}
