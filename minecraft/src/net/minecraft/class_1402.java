package net.minecraft;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1402 extends class_1352 {
	private static final Logger field_6651 = LogManager.getLogger();
	private final class_1308 field_6652;
	private final Predicate<class_1297> field_6650;
	private final class_1400.class_1401 field_6648;
	private class_1309 field_6649;

	public class_1402(class_1308 arg) {
		this.field_6652 = arg;
		if (arg instanceof class_1314) {
			field_6651.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");
		}

		this.field_6650 = argx -> {
			if (!(argx instanceof class_1657)) {
				return false;
			} else if (((class_1657)argx).field_7503.field_7480) {
				return false;
			} else {
				double d = this.method_6324();
				if (argx.method_5715()) {
					d *= 0.8F;
				}

				if (argx.method_5767()) {
					float f = ((class_1657)argx).method_7309();
					if (f < 0.1F) {
						f = 0.1F;
					}

					d *= (double)(0.7F * f);
				}

				return (double)argx.method_5739(this.field_6652) > d ? false : class_1405.method_6327(this.field_6652, (class_1309)argx, false, true);
			}
		};
		this.field_6648 = new class_1400.class_1401(arg);
	}

	@Override
	public boolean method_6264() {
		double d = this.method_6324();
		List<class_1657> list = this.field_6652.field_6002.method_8390(class_1657.class, this.field_6652.method_5829().method_1009(d, 4.0, d), this.field_6650);
		Collections.sort(list, this.field_6648);
		if (list.isEmpty()) {
			return false;
		} else {
			this.field_6649 = (class_1309)list.get(0);
			return true;
		}
	}

	@Override
	public boolean method_6266() {
		class_1309 lv = this.field_6652.method_5968();
		if (lv == null) {
			return false;
		} else if (!lv.method_5805()) {
			return false;
		} else if (lv instanceof class_1657 && ((class_1657)lv).field_7503.field_7480) {
			return false;
		} else {
			class_270 lv2 = this.field_6652.method_5781();
			class_270 lv3 = lv.method_5781();
			if (lv2 != null && lv3 == lv2) {
				return false;
			} else {
				double d = this.method_6324();
				return this.field_6652.method_5858(lv) > d * d ? false : !(lv instanceof class_3222) || !((class_3222)lv).field_13974.method_14268();
			}
		}
	}

	@Override
	public void method_6269() {
		this.field_6652.method_5980(this.field_6649);
		super.method_6269();
	}

	@Override
	public void method_6270() {
		this.field_6652.method_5980(null);
		super.method_6269();
	}

	protected double method_6324() {
		class_1324 lv = this.field_6652.method_5996(class_1612.field_7365);
		return lv == null ? 16.0 : lv.method_6194();
	}
}
