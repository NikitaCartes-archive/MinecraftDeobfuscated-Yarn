package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;

public abstract class class_2621 extends class_2624 {
	@Nullable
	protected class_2960 field_12037;
	protected long field_12036;

	protected class_2621(class_2591<?> arg) {
		super(arg);
	}

	public static void method_11287(class_1922 arg, Random random, class_2338 arg2, class_2960 arg3) {
		class_2586 lv = arg.method_8321(arg2);
		if (lv instanceof class_2621) {
			((class_2621)lv).method_11285(arg3, random.nextLong());
		}
	}

	protected boolean method_11283(class_2487 arg) {
		if (arg.method_10573("LootTable", 8)) {
			this.field_12037 = new class_2960(arg.method_10558("LootTable"));
			this.field_12036 = arg.method_10537("LootTableSeed");
			return true;
		} else {
			return false;
		}
	}

	protected boolean method_11286(class_2487 arg) {
		if (this.field_12037 == null) {
			return false;
		} else {
			arg.method_10582("LootTable", this.field_12037.toString());
			if (this.field_12036 != 0L) {
				arg.method_10544("LootTableSeed", this.field_12036);
			}

			return true;
		}
	}

	public void method_11289(@Nullable class_1657 arg) {
		if (this.field_12037 != null && this.field_11863.method_8503() != null) {
			class_52 lv = this.field_11863.method_8503().method_3857().method_367(this.field_12037);
			this.field_12037 = null;
			class_47.class_48 lv2 = new class_47.class_48((class_3218)this.field_11863)
				.method_312(class_181.field_1232, new class_2338(this.field_11867))
				.method_304(this.field_12036);
			if (arg != null) {
				lv2.method_303(arg.method_7292()).method_312(class_181.field_1226, arg);
			}

			lv.method_329(this, lv2.method_309(class_173.field_1179));
		}
	}

	public void method_11285(class_2960 arg, long l) {
		this.field_12037 = arg;
		this.field_12036 = l;
	}

	@Override
	public class_1799 method_5438(int i) {
		this.method_11289(null);
		return this.method_11282().get(i);
	}

	@Override
	public class_1799 method_5434(int i, int j) {
		this.method_11289(null);
		class_1799 lv = class_1262.method_5430(this.method_11282(), i, j);
		if (!lv.method_7960()) {
			this.method_5431();
		}

		return lv;
	}

	@Override
	public class_1799 method_5441(int i) {
		this.method_11289(null);
		return class_1262.method_5428(this.method_11282(), i);
	}

	@Override
	public void method_5447(int i, class_1799 arg) {
		this.method_11289(null);
		this.method_11282().set(i, arg);
		if (arg.method_7947() > this.method_5444()) {
			arg.method_7939(this.method_5444());
		}

		this.method_5431();
	}

	@Override
	public boolean method_5443(class_1657 arg) {
		return this.field_11863.method_8321(this.field_11867) != this
			? false
			: !(
				arg.method_5649((double)this.field_11867.method_10263() + 0.5, (double)this.field_11867.method_10264() + 0.5, (double)this.field_11867.method_10260() + 0.5)
					> 64.0
			);
	}

	@Override
	public void method_5448() {
		this.method_11282().clear();
	}

	protected abstract class_2371<class_1799> method_11282();

	protected abstract void method_11281(class_2371<class_1799> arg);

	@Override
	public boolean method_17489(class_1657 arg) {
		return super.method_17489(arg) && (this.field_12037 == null || !arg.method_7325());
	}

	@Nullable
	@Override
	public class_1703 createMenu(int i, class_1661 arg, class_1657 arg2) {
		if (this.method_17489(arg2)) {
			this.method_11289(arg.field_7546);
			return this.method_5465(i, arg);
		} else {
			return null;
		}
	}
}
