package net.minecraft;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_528 extends class_350<class_529> {
	private static final Logger field_3238 = LogManager.getLogger();
	private final class_526 field_3237;
	private int field_3236 = -1;
	@Nullable
	private List<class_34> field_3239;

	public class_528(class_526 arg, class_310 arg2, int i, int j, int k, int l, int m, Supplier<String> supplier, @Nullable class_528 arg3) {
		super(arg2, i, j, k, l, m);
		this.field_3237 = arg;
		if (arg3 != null) {
			this.field_3239 = arg3.field_3239;
		}

		this.method_2750(supplier, false);
	}

	public void method_2750(Supplier<String> supplier, boolean bl) {
		this.method_1902();
		class_32 lv = this.field_2164.method_1586();
		if (this.field_3239 == null || bl) {
			try {
				this.field_3239 = lv.method_235();
			} catch (class_33 var7) {
				field_3238.error("Couldn't load level list", (Throwable)var7);
				this.field_2164.method_1507(new class_421(class_1074.method_4662("selectWorld.unable_to_load"), var7.getMessage()));
				return;
			}

			Collections.sort(this.field_3239);
		}

		String string = ((String)supplier.get()).toLowerCase(Locale.ROOT);

		for (class_34 lv3 : this.field_3239) {
			if (lv3.method_252().toLowerCase(Locale.ROOT).contains(string) || lv3.method_248().toLowerCase(Locale.ROOT).contains(string)) {
				this.method_1901(new class_529(this, lv3, this.field_2164.method_1586()));
			}
		}
	}

	@Override
	protected int method_1948() {
		return super.method_1948() + 20;
	}

	@Override
	public int method_1932() {
		return super.method_1932() + 50;
	}

	public void method_2751(int i) {
		this.field_3236 = i;
		this.field_3237.method_2746(this.method_2753());
	}

	@Override
	protected boolean method_1955(int i) {
		return i == this.field_3236;
	}

	@Nullable
	public class_529 method_2753() {
		return this.field_3236 >= 0 && this.field_3236 < this.method_1947() ? (class_529)this.method_1968().get(this.field_3236) : null;
	}

	public class_526 method_2752() {
		return this.field_3237;
	}
}
