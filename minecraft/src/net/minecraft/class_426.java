package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_426 extends class_437 {
	protected final class_437 field_2490;
	private class_426.class_427 field_2486;
	private final class_315 field_2489;
	private final class_1076 field_2488;
	private class_349 field_2487;
	private class_349 field_2491;

	public class_426(class_437 arg, class_315 arg2, class_1076 arg3) {
		this.field_2490 = arg;
		this.field_2489 = arg2;
		this.field_2488 = arg3;
	}

	@Override
	public class_364 getFocused() {
		return this.field_2486;
	}

	@Override
	protected void method_2224() {
		this.field_2486 = new class_426.class_427(this.field_2563);
		this.field_2557.add(this.field_2486);
		this.field_2487 = this.method_2219(
			new class_349(
				100, this.field_2561 / 2 - 155, this.field_2559 - 38, class_315.class_316.field_1961, this.field_2489.method_1642(class_315.class_316.field_1961)
			) {
				@Override
				public void method_1826(double d, double e) {
					class_426.this.field_2489.method_1629(this.method_1899(), 1);
					this.field_2074 = class_426.this.field_2489.method_1642(class_315.class_316.field_1961);
					class_426.this.method_2181();
				}
			}
		);
		this.field_2491 = this.method_2219(new class_349(6, this.field_2561 / 2 - 155 + 160, this.field_2559 - 38, class_1074.method_4662("gui.done")) {
			@Override
			public void method_1826(double d, double e) {
				class_426.this.field_2563.method_1507(class_426.this.field_2490);
			}
		});
		super.method_2224();
	}

	private void method_2181() {
		this.field_2563.method_15993();
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.field_2486.method_1930(i, j, f);
		this.method_1789(this.field_2554, class_1074.method_4662("options.language"), this.field_2561 / 2, 16, 16777215);
		this.method_1789(this.field_2554, "(" + class_1074.method_4662("options.languageWarning") + ")", this.field_2561 / 2, this.field_2559 - 56, 8421504);
		super.method_2214(i, j, f);
	}

	@Environment(EnvType.CLIENT)
	class class_427 extends class_358 {
		private final List<String> field_2492 = Lists.<String>newArrayList();
		private final Map<String, class_1077> field_2493 = Maps.<String, class_1077>newHashMap();

		public class_427(class_310 arg2) {
			super(arg2, class_426.this.field_2561, class_426.this.field_2559, 32, class_426.this.field_2559 - 65 + 4, 18);

			for (class_1077 lv : class_426.this.field_2488.method_4665()) {
				this.field_2493.put(lv.getCode(), lv);
				this.field_2492.add(lv.getCode());
			}
		}

		@Override
		protected int method_1947() {
			return this.field_2492.size();
		}

		@Override
		protected boolean method_1937(int i, int j, double d, double e) {
			class_1077 lv = (class_1077)this.field_2493.get(this.field_2492.get(i));
			class_426.this.field_2488.method_4667(lv);
			class_426.this.field_2489.field_1883 = lv.getCode();
			this.field_2164.method_1521();
			class_426.this.field_2554.method_1719(class_426.this.field_2488.method_4666());
			class_426.this.field_2491.field_2074 = class_1074.method_4662("gui.done");
			class_426.this.field_2487.field_2074 = class_426.this.field_2489.method_1642(class_315.class_316.field_1961);
			class_426.this.field_2489.method_1640();
			class_426.this.method_2181();
			return true;
		}

		@Override
		protected boolean method_1955(int i) {
			return ((String)this.field_2492.get(i)).equals(class_426.this.field_2488.method_4669().getCode());
		}

		@Override
		protected int method_1928() {
			return this.method_1947() * 18;
		}

		@Override
		protected void method_1936() {
			class_426.this.method_2240();
		}

		@Override
		protected void method_1935(int i, int j, int k, int l, int m, int n, float f) {
			class_426.this.field_2554.method_1719(true);
			this.method_1789(class_426.this.field_2554, ((class_1077)this.field_2493.get(this.field_2492.get(i))).toString(), this.field_2168 / 2, k + 1, 16777215);
			class_426.this.field_2554.method_1719(class_426.this.field_2488.method_4669().method_4672());
		}
	}
}
