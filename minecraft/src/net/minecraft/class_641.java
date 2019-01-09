package net.minecraft;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_641 {
	private static final Logger field_3751 = LogManager.getLogger();
	private final class_310 field_3750;
	private final List<class_642> field_3749 = Lists.<class_642>newArrayList();

	public class_641(class_310 arg) {
		this.field_3750 = arg;
		this.method_2981();
	}

	public void method_2981() {
		try {
			this.field_3749.clear();
			class_2487 lv = class_2507.method_10633(new File(this.field_3750.field_1697, "servers.dat"));
			if (lv == null) {
				return;
			}

			class_2499 lv2 = lv.method_10554("servers", 10);

			for (int i = 0; i < lv2.size(); i++) {
				this.field_3749.add(class_642.method_2993(lv2.method_10602(i)));
			}
		} catch (Exception var4) {
			field_3751.error("Couldn't load server list", (Throwable)var4);
		}
	}

	public void method_2987() {
		try {
			class_2499 lv = new class_2499();

			for (class_642 lv2 : this.field_3749) {
				lv.method_10606(lv2.method_2992());
			}

			class_2487 lv3 = new class_2487();
			lv3.method_10566("servers", lv);
			class_2507.method_10632(lv3, new File(this.field_3750.field_1697, "servers.dat"));
		} catch (Exception var4) {
			field_3751.error("Couldn't save server list", (Throwable)var4);
		}
	}

	public class_642 method_2982(int i) {
		return (class_642)this.field_3749.get(i);
	}

	public void method_2983(int i) {
		this.field_3749.remove(i);
	}

	public void method_2988(class_642 arg) {
		this.field_3749.add(arg);
	}

	public int method_2984() {
		return this.field_3749.size();
	}

	public void method_2985(int i, int j) {
		class_642 lv = this.method_2982(i);
		this.field_3749.set(i, this.method_2982(j));
		this.field_3749.set(j, lv);
		this.method_2987();
	}

	public void method_2980(int i, class_642 arg) {
		this.field_3749.set(i, arg);
	}

	public static void method_2986(class_642 arg) {
		class_641 lv = new class_641(class_310.method_1551());
		lv.method_2981();

		for (int i = 0; i < lv.method_2984(); i++) {
			class_642 lv2 = lv.method_2982(i);
			if (lv2.field_3752.equals(arg.field_3752) && lv2.field_3761.equals(arg.field_3761)) {
				lv.method_2980(i, arg);
				break;
			}
		}

		lv.method_2987();
	}
}
