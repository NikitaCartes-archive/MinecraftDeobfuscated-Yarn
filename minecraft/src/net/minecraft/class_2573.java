package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2573 extends class_2586 implements class_1275 {
	private class_2561 field_11772;
	private class_1767 field_11774 = class_1767.field_7952;
	private class_2499 field_11773;
	private boolean field_11770;
	private List<class_2582> field_11769;
	private List<class_1767> field_11771;
	private String field_11775;

	public class_2573() {
		super(class_2591.field_11905);
	}

	public class_2573(class_1767 arg) {
		this();
		this.field_11774 = arg;
	}

	@Environment(EnvType.CLIENT)
	public void method_10913(class_1799 arg, class_1767 arg2) {
		this.field_11773 = null;
		class_2487 lv = arg.method_7941("BlockEntityTag");
		if (lv != null && lv.method_10573("Patterns", 9)) {
			this.field_11773 = lv.method_10554("Patterns", 10).method_10612();
		}

		this.field_11774 = arg2;
		this.field_11769 = null;
		this.field_11771 = null;
		this.field_11775 = "";
		this.field_11770 = true;
		this.field_11772 = arg.method_7938() ? arg.method_7964() : null;
	}

	@Override
	public class_2561 method_5477() {
		return (class_2561)(this.field_11772 != null ? this.field_11772 : new class_2588("block.minecraft.banner"));
	}

	@Nullable
	@Override
	public class_2561 method_5797() {
		return this.field_11772;
	}

	public void method_16842(class_2561 arg) {
		this.field_11772 = arg;
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		super.method_11007(arg);
		if (this.field_11773 != null) {
			arg.method_10566("Patterns", this.field_11773);
		}

		if (this.field_11772 != null) {
			arg.method_10582("CustomName", class_2561.class_2562.method_10867(this.field_11772));
		}

		return arg;
	}

	@Override
	public void method_11014(class_2487 arg) {
		super.method_11014(arg);
		if (arg.method_10573("CustomName", 8)) {
			this.field_11772 = class_2561.class_2562.method_10877(arg.method_10558("CustomName"));
		}

		if (this.method_11002()) {
			this.field_11774 = ((class_2185)this.method_11010().method_11614()).method_9303();
		} else {
			this.field_11774 = null;
		}

		this.field_11773 = arg.method_10554("Patterns", 10);
		this.field_11769 = null;
		this.field_11771 = null;
		this.field_11775 = null;
		this.field_11770 = true;
	}

	@Nullable
	@Override
	public class_2622 method_16886() {
		return new class_2622(this.field_11867, 6, this.method_16887());
	}

	@Override
	public class_2487 method_16887() {
		return this.method_11007(new class_2487());
	}

	public static int method_10910(class_1799 arg) {
		class_2487 lv = arg.method_7941("BlockEntityTag");
		return lv != null && lv.method_10545("Patterns") ? lv.method_10554("Patterns", 10).size() : 0;
	}

	@Environment(EnvType.CLIENT)
	public List<class_2582> method_10911() {
		this.method_10914();
		return this.field_11769;
	}

	@Environment(EnvType.CLIENT)
	public List<class_1767> method_10909() {
		this.method_10914();
		return this.field_11771;
	}

	@Environment(EnvType.CLIENT)
	public String method_10915() {
		this.method_10914();
		return this.field_11775;
	}

	@Environment(EnvType.CLIENT)
	private void method_10914() {
		if (this.field_11769 == null || this.field_11771 == null || this.field_11775 == null) {
			if (!this.field_11770) {
				this.field_11775 = "";
			} else {
				this.field_11769 = Lists.<class_2582>newArrayList();
				this.field_11771 = Lists.<class_1767>newArrayList();
				class_1767 lv = this.method_10908(this::method_11010);
				if (lv == null) {
					this.field_11775 = "banner_missing";
				} else {
					this.field_11769.add(class_2582.field_11834);
					this.field_11771.add(lv);
					this.field_11775 = "b" + lv.method_7789();
					if (this.field_11773 != null) {
						for (int i = 0; i < this.field_11773.size(); i++) {
							class_2487 lv2 = this.field_11773.method_10602(i);
							class_2582 lv3 = class_2582.method_10946(lv2.method_10558("Pattern"));
							if (lv3 != null) {
								this.field_11769.add(lv3);
								int j = lv2.method_10550("Color");
								this.field_11771.add(class_1767.method_7791(j));
								this.field_11775 = this.field_11775 + lv3.method_10945() + j;
							}
						}
					}
				}
			}
		}
	}

	public static void method_10905(class_1799 arg) {
		class_2487 lv = arg.method_7941("BlockEntityTag");
		if (lv != null && lv.method_10573("Patterns", 9)) {
			class_2499 lv2 = lv.method_10554("Patterns", 10);
			if (!lv2.isEmpty()) {
				lv2.method_10536(lv2.size() - 1);
				if (lv2.isEmpty()) {
					arg.method_7983("BlockEntityTag");
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public class_1799 method_10907(class_2680 arg) {
		class_1799 lv = new class_1799(class_2215.method_9398(this.method_10908(() -> arg)));
		if (this.field_11773 != null && !this.field_11773.isEmpty()) {
			lv.method_7911("BlockEntityTag").method_10566("Patterns", this.field_11773.method_10612());
		}

		if (this.field_11772 != null) {
			lv.method_7977(this.field_11772);
		}

		return lv;
	}

	public class_1767 method_10908(Supplier<class_2680> supplier) {
		if (this.field_11774 == null) {
			this.field_11774 = ((class_2185)((class_2680)supplier.get()).method_11614()).method_9303();
		}

		return this.field_11774;
	}
}
