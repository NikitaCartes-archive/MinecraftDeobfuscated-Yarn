package net.minecraft;

import javax.annotation.Nullable;

public abstract class class_1693 extends class_1688 implements class_1263, class_3908 {
	private class_2371<class_1799> field_7735 = class_2371.method_10213(36, class_1799.field_8037);
	private boolean field_7733 = true;
	@Nullable
	private class_2960 field_7734;
	private long field_7732;

	protected class_1693(class_1299<?> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	protected class_1693(class_1299<?> arg, double d, double e, double f, class_1937 arg2) {
		super(arg, arg2, d, e, f);
	}

	@Override
	public void method_7516(class_1282 arg) {
		super.method_7516(arg);
		if (this.field_6002.method_8450().method_8355(class_1928.field_19393)) {
			class_1264.method_5452(this.field_6002, this, this);
		}
	}

	@Override
	public boolean method_5442() {
		for (class_1799 lv : this.field_7735) {
			if (!lv.method_7960()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public class_1799 method_5438(int i) {
		this.method_7563(null);
		return this.field_7735.get(i);
	}

	@Override
	public class_1799 method_5434(int i, int j) {
		this.method_7563(null);
		return class_1262.method_5430(this.field_7735, i, j);
	}

	@Override
	public class_1799 method_5441(int i) {
		this.method_7563(null);
		class_1799 lv = this.field_7735.get(i);
		if (lv.method_7960()) {
			return class_1799.field_8037;
		} else {
			this.field_7735.set(i, class_1799.field_8037);
			return lv;
		}
	}

	@Override
	public void method_5447(int i, class_1799 arg) {
		this.method_7563(null);
		this.field_7735.set(i, arg);
		if (!arg.method_7960() && arg.method_7947() > this.method_5444()) {
			arg.method_7939(this.method_5444());
		}
	}

	@Override
	public boolean method_5758(int i, class_1799 arg) {
		if (i >= 0 && i < this.method_5439()) {
			this.method_5447(i, arg);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void method_5431() {
	}

	@Override
	public boolean method_5443(class_1657 arg) {
		return this.field_5988 ? false : !(arg.method_5858(this) > 64.0);
	}

	@Nullable
	@Override
	public class_1297 method_5731(class_2874 arg) {
		this.field_7733 = false;
		return super.method_5731(arg);
	}

	@Override
	public void method_5650() {
		if (!this.field_6002.field_9236 && this.field_7733) {
			class_1264.method_5452(this.field_6002, this, this);
		}

		super.method_5650();
	}

	@Override
	protected void method_5652(class_2487 arg) {
		super.method_5652(arg);
		if (this.field_7734 != null) {
			arg.method_10582("LootTable", this.field_7734.toString());
			if (this.field_7732 != 0L) {
				arg.method_10544("LootTableSeed", this.field_7732);
			}
		} else {
			class_1262.method_5426(arg, this.field_7735);
		}
	}

	@Override
	protected void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.field_7735 = class_2371.method_10213(this.method_5439(), class_1799.field_8037);
		if (arg.method_10573("LootTable", 8)) {
			this.field_7734 = new class_2960(arg.method_10558("LootTable"));
			this.field_7732 = arg.method_10537("LootTableSeed");
		} else {
			class_1262.method_5429(arg, this.field_7735);
		}
	}

	@Override
	public boolean method_5688(class_1657 arg, class_1268 arg2) {
		arg.method_17355(this);
		return true;
	}

	@Override
	protected void method_7525() {
		float f = 0.98F;
		if (this.field_7734 == null) {
			int i = 15 - class_1703.method_7618(this);
			f += (float)i * 0.001F;
		}

		this.method_18799(this.method_18798().method_18805((double)f, 0.0, (double)f));
	}

	public void method_7563(@Nullable class_1657 arg) {
		if (this.field_7734 != null && this.field_6002.method_8503() != null) {
			class_52 lv = this.field_6002.method_8503().method_3857().method_367(this.field_7734);
			this.field_7734 = null;
			class_47.class_48 lv2 = new class_47.class_48((class_3218)this.field_6002)
				.method_312(class_181.field_1232, new class_2338(this))
				.method_304(this.field_7732);
			if (arg != null) {
				lv2.method_303(arg.method_7292()).method_312(class_181.field_1226, arg);
			}

			lv.method_329(this, lv2.method_309(class_173.field_1179));
		}
	}

	@Override
	public void method_5448() {
		this.method_7563(null);
		this.field_7735.clear();
	}

	public void method_7562(class_2960 arg, long l) {
		this.field_7734 = arg;
		this.field_7732 = l;
	}

	@Nullable
	@Override
	public class_1703 createMenu(int i, class_1661 arg, class_1657 arg2) {
		if (this.field_7734 != null && arg2.method_7325()) {
			return null;
		} else {
			this.method_7563(arg.field_7546);
			return this.method_17357(i, arg);
		}
	}

	protected abstract class_1703 method_17357(int i, class_1661 arg);
}
