package net.minecraft;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_526 extends class_437 {
	private static final Logger field_3217 = LogManager.getLogger();
	protected final class_437 field_3221;
	protected String field_3223 = "Select world";
	private String field_3222;
	private class_339 field_3219;
	private class_339 field_3224;
	private class_339 field_3215;
	private class_339 field_3216;
	protected class_342 field_3220;
	private class_528 field_3218;

	public class_526(class_437 arg) {
		this.field_3221 = arg;
	}

	@Override
	public boolean method_16802(double d) {
		return this.field_3218.method_16802(d);
	}

	@Override
	public void method_2225() {
		this.field_3220.method_1865();
	}

	@Override
	protected void method_2224() {
		this.field_2563.field_1774.method_1462(true);
		this.field_3223 = class_1074.method_4662("selectWorld.title");
		this.field_3220 = new class_342(0, this.field_2554, this.field_2561 / 2 - 100, 22, 200, 20, this.field_3220) {
			@Override
			public void method_1876(boolean bl) {
				super.method_1876(true);
			}
		};
		this.field_3220.method_1863((integer, string) -> this.field_3218.method_2750(() -> string, false));
		this.field_3218 = new class_528(
			this, this.field_2563, this.field_2561, this.field_2559, 48, this.field_2559 - 64, 36, () -> this.field_3220.method_1882(), this.field_3218
		);
		this.field_3224 = this.method_2219(new class_339(1, this.field_2561 / 2 - 154, this.field_2559 - 52, 150, 20, class_1074.method_4662("selectWorld.select")) {
			@Override
			public void method_1826(double d, double e) {
				class_529 lv = class_526.this.field_3218.method_2753();
				if (lv != null) {
					lv.method_2768();
				}
			}
		});
		this.method_2219(new class_339(3, this.field_2561 / 2 + 4, this.field_2559 - 52, 150, 20, class_1074.method_4662("selectWorld.create")) {
			@Override
			public void method_1826(double d, double e) {
				class_526.this.field_2563.method_1507(new class_525(class_526.this));
			}
		});
		this.field_3215 = this.method_2219(new class_339(4, this.field_2561 / 2 - 154, this.field_2559 - 28, 72, 20, class_1074.method_4662("selectWorld.edit")) {
			@Override
			public void method_1826(double d, double e) {
				class_529 lv = class_526.this.field_3218.method_2753();
				if (lv != null) {
					lv.method_2756();
				}
			}
		});
		this.field_3219 = this.method_2219(new class_339(2, this.field_2561 / 2 - 76, this.field_2559 - 28, 72, 20, class_1074.method_4662("selectWorld.delete")) {
			@Override
			public void method_1826(double d, double e) {
				class_529 lv = class_526.this.field_3218.method_2753();
				if (lv != null) {
					lv.method_2755();
				}
			}
		});
		this.field_3216 = this.method_2219(new class_339(5, this.field_2561 / 2 + 4, this.field_2559 - 28, 72, 20, class_1074.method_4662("selectWorld.recreate")) {
			@Override
			public void method_1826(double d, double e) {
				class_529 lv = class_526.this.field_3218.method_2753();
				if (lv != null) {
					lv.method_2757();
				}
			}
		});
		this.method_2219(new class_339(0, this.field_2561 / 2 + 82, this.field_2559 - 28, 72, 20, class_1074.method_4662("gui.cancel")) {
			@Override
			public void method_1826(double d, double e) {
				class_526.this.field_2563.method_1507(class_526.this.field_3221);
			}
		});
		this.field_3224.field_2078 = false;
		this.field_3219.field_2078 = false;
		this.field_3215.field_2078 = false;
		this.field_3216.field_2078 = false;
		this.field_2557.add(this.field_3220);
		this.field_2557.add(this.field_3218);
		this.field_3220.method_1876(true);
		this.field_3220.method_1856(false);
	}

	@Override
	public boolean method_16805(int i, int j, int k) {
		return super.method_16805(i, j, k) ? true : this.field_3220.method_16805(i, j, k);
	}

	@Override
	public boolean method_16806(char c, int i) {
		return this.field_3220.method_16806(c, i);
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.field_3222 = null;
		this.field_3218.method_1930(i, j, f);
		this.field_3220.method_1857(i, j, f);
		this.method_1789(this.field_2554, this.field_3223, this.field_2561 / 2, 8, 16777215);
		super.method_2214(i, j, f);
		if (this.field_3222 != null) {
			this.method_2211(Lists.<String>newArrayList(Splitter.on("\n").split(this.field_3222)), i, j);
		}
	}

	public void method_2739(String string) {
		this.field_3222 = string;
	}

	public void method_2746(@Nullable class_529 arg) {
		boolean bl = arg != null;
		this.field_3224.field_2078 = bl;
		this.field_3219.field_2078 = bl;
		this.field_3215.field_2078 = bl;
		this.field_3216.field_2078 = bl;
	}

	@Override
	public void method_2234() {
		if (this.field_3218 != null) {
			this.field_3218.method_1968().forEach(class_529::close);
		}
	}
}
