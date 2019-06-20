package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2880 extends class_2869 {
	public static final class_2338 field_13103 = new class_2338(100, 50, 0);
	private final class_2881 field_13104;

	public class_2880(class_1937 arg, class_2874 arg2) {
		super(arg, arg2);
		class_2487 lv = arg.method_8401().method_170(class_2874.field_13078);
		this.field_13104 = arg instanceof class_3218 ? new class_2881((class_3218)arg, lv.method_10562("DragonFight")) : null;
	}

	@Override
	public class_2794<?> method_12443() {
		class_2916 lv = class_2798.field_12770.method_12117();
		lv.method_12571(class_2246.field_10471.method_9564());
		lv.method_12572(class_2246.field_10124.method_9564());
		lv.method_12651(this.method_12466());
		return class_2798.field_12770
			.create(this.field_13058, class_1969.field_9399.method_8772(class_1969.field_9399.method_8774().method_9205(this.field_13058.method_8412())), lv);
	}

	@Override
	public float method_12464(long l, float f) {
		return 0.0F;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	@Override
	public float[] method_12446(float f, float g) {
		return null;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_243 method_12445(float f, float g) {
		int i = 10518688;
		float h = class_3532.method_15362(f * (float) (Math.PI * 2)) * 2.0F + 0.5F;
		h = class_3532.method_15363(h, 0.0F, 1.0F);
		float j = 0.627451F;
		float k = 0.5019608F;
		float l = 0.627451F;
		j *= h * 0.0F + 0.15F;
		k *= h * 0.0F + 0.15F;
		l *= h * 0.0F + 0.15F;
		return new class_243((double)j, (double)k, (double)l);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_12449() {
		return false;
	}

	@Override
	public boolean method_12448() {
		return false;
	}

	@Override
	public boolean method_12462() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public float method_12455() {
		return 8.0F;
	}

	@Nullable
	@Override
	public class_2338 method_12452(class_1923 arg, boolean bl) {
		Random random = new Random(this.field_13058.method_8412());
		class_2338 lv = new class_2338(arg.method_8326() + random.nextInt(15), 0, arg.method_8329() + random.nextInt(15));
		return this.field_13058.method_8495(lv).method_11620().method_15801() ? lv : null;
	}

	@Override
	public class_2338 method_12466() {
		return field_13103;
	}

	@Nullable
	@Override
	public class_2338 method_12444(int i, int j, boolean bl) {
		return this.method_12452(new class_1923(i >> 4, j >> 4), bl);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_12453(int i, int j) {
		return false;
	}

	@Override
	public class_2874 method_12460() {
		return class_2874.field_13078;
	}

	@Override
	public void method_12450() {
		class_2487 lv = new class_2487();
		if (this.field_13104 != null) {
			lv.method_10566("DragonFight", this.field_13104.method_12530());
		}

		this.field_13058.method_8401().method_160(class_2874.field_13078, lv);
	}

	@Override
	public void method_12461() {
		if (this.field_13104 != null) {
			this.field_13104.method_12538();
		}
	}

	@Nullable
	public class_2881 method_12513() {
		return this.field_13104;
	}
}
