package net.minecraft;

import com.google.common.collect.ComparisonChain;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1293 implements Comparable<class_1293> {
	private static final Logger field_5897 = LogManager.getLogger();
	private final class_1291 field_5896;
	private int field_5895;
	private int field_5893;
	private boolean field_5894;
	private boolean field_5892;
	@Environment(EnvType.CLIENT)
	private boolean field_5891;
	private boolean field_5890;
	private boolean field_5889;

	public class_1293(class_1291 arg) {
		this(arg, 0, 0);
	}

	public class_1293(class_1291 arg, int i) {
		this(arg, i, 0);
	}

	public class_1293(class_1291 arg, int i, int j) {
		this(arg, i, j, false, true);
	}

	public class_1293(class_1291 arg, int i, int j, boolean bl, boolean bl2) {
		this(arg, i, j, bl, bl2, bl2);
	}

	public class_1293(class_1291 arg, int i, int j, boolean bl, boolean bl2, boolean bl3) {
		this.field_5896 = arg;
		this.field_5895 = i;
		this.field_5893 = j;
		this.field_5892 = bl;
		this.field_5890 = bl2;
		this.field_5889 = bl3;
	}

	public class_1293(class_1293 arg) {
		this.field_5896 = arg.field_5896;
		this.field_5895 = arg.field_5895;
		this.field_5893 = arg.field_5893;
		this.field_5892 = arg.field_5892;
		this.field_5890 = arg.field_5890;
		this.field_5889 = arg.field_5889;
	}

	public boolean method_5590(class_1293 arg) {
		if (this.field_5896 != arg.field_5896) {
			field_5897.warn("This method should only be called for matching effects!");
		}

		boolean bl = false;
		if (arg.field_5893 > this.field_5893) {
			this.field_5893 = arg.field_5893;
			this.field_5895 = arg.field_5895;
			bl = true;
		} else if (arg.field_5893 == this.field_5893 && this.field_5895 < arg.field_5895) {
			this.field_5895 = arg.field_5895;
			bl = true;
		}

		if (!arg.field_5892 && this.field_5892 || bl) {
			this.field_5892 = arg.field_5892;
			bl = true;
		}

		if (arg.field_5890 != this.field_5890) {
			this.field_5890 = arg.field_5890;
			bl = true;
		}

		if (arg.field_5889 != this.field_5889) {
			this.field_5889 = arg.field_5889;
			bl = true;
		}

		return bl;
	}

	public class_1291 method_5579() {
		return this.field_5896;
	}

	public int method_5584() {
		return this.field_5895;
	}

	public int method_5578() {
		return this.field_5893;
	}

	public boolean method_5591() {
		return this.field_5892;
	}

	public boolean method_5581() {
		return this.field_5890;
	}

	public boolean method_5592() {
		return this.field_5889;
	}

	public boolean method_5585(class_1309 arg) {
		if (this.field_5895 > 0) {
			if (this.field_5896.method_5552(this.field_5895, this.field_5893)) {
				this.method_5589(arg);
			}

			this.method_5588();
		}

		return this.field_5895 > 0;
	}

	private int method_5588() {
		return --this.field_5895;
	}

	public void method_5589(class_1309 arg) {
		if (this.field_5895 > 0) {
			this.field_5896.method_5572(arg, this.field_5893);
		}
	}

	public String method_5586() {
		return this.field_5896.method_5567();
	}

	public String toString() {
		String string;
		if (this.field_5893 > 0) {
			string = this.method_5586() + " x " + (this.field_5893 + 1) + ", Duration: " + this.field_5895;
		} else {
			string = this.method_5586() + ", Duration: " + this.field_5895;
		}

		if (this.field_5894) {
			string = string + ", Splash: true";
		}

		if (!this.field_5890) {
			string = string + ", Particles: false";
		}

		if (!this.field_5889) {
			string = string + ", Show Icon: false";
		}

		return string;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_1293)) {
			return false;
		} else {
			class_1293 lv = (class_1293)object;
			return this.field_5895 == lv.field_5895
				&& this.field_5893 == lv.field_5893
				&& this.field_5894 == lv.field_5894
				&& this.field_5892 == lv.field_5892
				&& this.field_5896.equals(lv.field_5896);
		}
	}

	public int hashCode() {
		int i = this.field_5896.hashCode();
		i = 31 * i + this.field_5895;
		i = 31 * i + this.field_5893;
		i = 31 * i + (this.field_5894 ? 1 : 0);
		return 31 * i + (this.field_5892 ? 1 : 0);
	}

	public class_2487 method_5582(class_2487 arg) {
		arg.method_10567("Id", (byte)class_1291.method_5554(this.method_5579()));
		arg.method_10567("Amplifier", (byte)this.method_5578());
		arg.method_10569("Duration", this.method_5584());
		arg.method_10556("Ambient", this.method_5591());
		arg.method_10556("ShowParticles", this.method_5581());
		arg.method_10556("ShowIcon", this.method_5592());
		return arg;
	}

	public static class_1293 method_5583(class_2487 arg) {
		int i = arg.method_10571("Id");
		class_1291 lv = class_1291.method_5569(i);
		if (lv == null) {
			return null;
		} else {
			int j = arg.method_10571("Amplifier");
			int k = arg.method_10550("Duration");
			boolean bl = arg.method_10577("Ambient");
			boolean bl2 = true;
			if (arg.method_10573("ShowParticles", 1)) {
				bl2 = arg.method_10577("ShowParticles");
			}

			boolean bl3 = bl2;
			if (arg.method_10573("ShowIcon", 1)) {
				bl3 = arg.method_10577("ShowIcon");
			}

			return new class_1293(lv, k, j < 0 ? 0 : j, bl, bl2, bl3);
		}
	}

	@Environment(EnvType.CLIENT)
	public void method_5580(boolean bl) {
		this.field_5891 = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_5593() {
		return this.field_5891;
	}

	public int method_5587(class_1293 arg) {
		int i = 32147;
		return (this.method_5584() <= 32147 || arg.method_5584() <= 32147) && (!this.method_5591() || !arg.method_5591())
			? ComparisonChain.start()
				.compare(this.method_5591(), arg.method_5591())
				.compare(this.method_5584(), arg.method_5584())
				.compare(this.method_5579().method_5556(), arg.method_5579().method_5556())
				.result()
			: ComparisonChain.start().compare(this.method_5591(), arg.method_5591()).compare(this.method_5579().method_5556(), arg.method_5579().method_5556()).result();
	}
}
