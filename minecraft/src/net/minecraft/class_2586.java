package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_2586 {
	private static final Logger field_11868 = LogManager.getLogger();
	private final class_2591<?> field_11864;
	protected class_1937 field_11863;
	protected class_2338 field_11867 = class_2338.field_10980;
	protected boolean field_11865;
	@Nullable
	private class_2680 field_11866;

	public class_2586(class_2591<?> arg) {
		this.field_11864 = arg;
	}

	@Nullable
	public class_1937 method_10997() {
		return this.field_11863;
	}

	public void method_11009(class_1937 arg) {
		this.field_11863 = arg;
	}

	public boolean method_11002() {
		return this.field_11863 != null;
	}

	public void method_11014(class_2487 arg) {
		this.field_11867 = new class_2338(arg.method_10550("x"), arg.method_10550("y"), arg.method_10550("z"));
	}

	public class_2487 method_11007(class_2487 arg) {
		return this.method_10999(arg);
	}

	private class_2487 method_10999(class_2487 arg) {
		class_2960 lv = class_2591.method_11033(this.method_11017());
		if (lv == null) {
			throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
		} else {
			arg.method_10582("id", lv.toString());
			arg.method_10569("x", this.field_11867.method_10263());
			arg.method_10569("y", this.field_11867.method_10264());
			arg.method_10569("z", this.field_11867.method_10260());
			return arg;
		}
	}

	@Nullable
	public static class_2586 method_11005(class_2487 arg) {
		String string = arg.method_10558("id");
		return (class_2586)class_2378.field_11137.method_17966(new class_2960(string)).map(argx -> {
			try {
				return argx.method_11032();
			} catch (Throwable var3) {
				field_11868.error("Failed to create block entity {}", string, var3);
				return null;
			}
		}).map(arg2 -> {
			try {
				arg2.method_11014(arg);
				return arg2;
			} catch (Throwable var4) {
				field_11868.error("Failed to load data for block entity {}", string, var4);
				return null;
			}
		}).orElseGet(() -> {
			field_11868.warn("Skipping BlockEntity with id {}", string);
			return null;
		});
	}

	public void method_5431() {
		if (this.field_11863 != null) {
			this.field_11866 = this.field_11863.method_8320(this.field_11867);
			this.field_11863.method_8524(this.field_11867, this);
			if (!this.field_11866.method_11588()) {
				this.field_11863.method_8455(this.field_11867, this.field_11866.method_11614());
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public double method_11008(double d, double e, double f) {
		double g = (double)this.field_11867.method_10263() + 0.5 - d;
		double h = (double)this.field_11867.method_10264() + 0.5 - e;
		double i = (double)this.field_11867.method_10260() + 0.5 - f;
		return g * g + h * h + i * i;
	}

	@Environment(EnvType.CLIENT)
	public double method_11006() {
		return 4096.0;
	}

	public class_2338 method_11016() {
		return this.field_11867;
	}

	public class_2680 method_11010() {
		if (this.field_11866 == null) {
			this.field_11866 = this.field_11863.method_8320(this.field_11867);
		}

		return this.field_11866;
	}

	@Nullable
	public class_2622 method_16886() {
		return null;
	}

	public class_2487 method_16887() {
		return this.method_10999(new class_2487());
	}

	public boolean method_11015() {
		return this.field_11865;
	}

	public void method_11012() {
		this.field_11865 = true;
	}

	public void method_10996() {
		this.field_11865 = false;
	}

	public boolean method_11004(int i, int j) {
		return false;
	}

	public void method_11000() {
		this.field_11866 = null;
	}

	public void method_11003(class_129 arg) {
		arg.method_577("Name", () -> class_2378.field_11137.method_10221(this.method_11017()) + " // " + this.getClass().getCanonicalName());
		if (this.field_11863 != null) {
			class_129.method_586(arg, this.field_11867, this.method_11010());
			class_129.method_586(arg, this.field_11867, this.field_11863.method_8320(this.field_11867));
		}
	}

	public void method_10998(class_2338 arg) {
		this.field_11867 = arg.method_10062();
	}

	public boolean method_11011() {
		return false;
	}

	public void method_11013(class_2470 arg) {
	}

	public void method_11001(class_2415 arg) {
	}

	public class_2591<?> method_11017() {
		return this.field_11864;
	}
}
