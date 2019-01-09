package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_405 extends class_437 {
	private final class_437 field_2360;
	protected final class_405.class_406 field_2366;
	protected final String field_2363;
	private final String field_2364;
	private final List<String> field_2365 = Lists.<String>newArrayList();
	protected final String field_2361;
	protected final String field_2359;
	protected final String field_2362;

	public class_405(class_437 arg, class_405.class_406 arg2, String string, String string2) {
		this.field_2360 = arg;
		this.field_2366 = arg2;
		this.field_2363 = string;
		this.field_2364 = string2;
		this.field_2361 = class_1074.method_4662("selectWorld.backupJoinConfirmButton");
		this.field_2359 = class_1074.method_4662("selectWorld.backupJoinSkipButton");
		this.field_2362 = class_1074.method_4662("gui.cancel");
	}

	@Override
	protected void method_2224() {
		super.method_2224();
		this.field_2365.clear();
		this.field_2365.addAll(this.field_2554.method_1728(this.field_2364, this.field_2561 - 50));
		this.method_2219(new class_349(0, this.field_2561 / 2 - 155, 100 + (this.field_2365.size() + 1) * 9, this.field_2361) {
			@Override
			public void method_1826(double d, double e) {
				class_405.this.field_2366.proceed(true);
			}
		});
		this.method_2219(new class_349(1, this.field_2561 / 2 - 155 + 160, 100 + (this.field_2365.size() + 1) * 9, this.field_2359) {
			@Override
			public void method_1826(double d, double e) {
				class_405.this.field_2366.proceed(false);
			}
		});
		this.method_2219(new class_339(1, this.field_2561 / 2 - 155 + 80, 124 + (this.field_2365.size() + 1) * 9, 150, 20, this.field_2362) {
			@Override
			public void method_1826(double d, double e) {
				class_405.this.field_2563.method_1507(class_405.this.field_2360);
			}
		});
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		this.method_1789(this.field_2554, this.field_2363, this.field_2561 / 2, 70, 16777215);
		int k = 90;

		for (String string : this.field_2365) {
			this.method_1789(this.field_2554, string, this.field_2561 / 2, k, 16777215);
			k += 9;
		}

		super.method_2214(i, j, f);
	}

	@Override
	public boolean method_16890() {
		return false;
	}

	@Override
	public boolean method_16805(int i, int j, int k) {
		if (i == 256) {
			this.field_2563.method_1507(this.field_2360);
			return true;
		} else {
			return super.method_16805(i, j, k);
		}
	}

	@Environment(EnvType.CLIENT)
	public interface class_406 {
		void proceed(boolean bl);
	}
}
