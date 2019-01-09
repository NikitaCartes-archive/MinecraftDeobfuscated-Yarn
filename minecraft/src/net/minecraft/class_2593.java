package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2593 extends class_2586 {
	private boolean field_11919;
	private boolean field_11918;
	private boolean field_11917;
	private boolean field_11916;
	private final class_1918 field_11920 = new class_1918() {
		@Override
		public void method_8286(String string) {
			super.method_8286(string);
			class_2593.this.method_5431();
		}

		@Override
		public class_3218 method_8293() {
			return (class_3218)class_2593.this.field_11863;
		}

		@Override
		public void method_8295() {
			class_2680 lv = class_2593.this.field_11863.method_8320(class_2593.this.field_11867);
			this.method_8293().method_8413(class_2593.this.field_11867, lv, lv, 3);
		}

		@Environment(EnvType.CLIENT)
		@Override
		public class_243 method_8300() {
			return new class_243(
				(double)class_2593.this.field_11867.method_10263() + 0.5,
				(double)class_2593.this.field_11867.method_10264() + 0.5,
				(double)class_2593.this.field_11867.method_10260() + 0.5
			);
		}

		@Override
		public class_2168 method_8303() {
			return new class_2168(
				this,
				new class_243(
					(double)class_2593.this.field_11867.method_10263() + 0.5,
					(double)class_2593.this.field_11867.method_10264() + 0.5,
					(double)class_2593.this.field_11867.method_10260() + 0.5
				),
				class_241.field_1340,
				this.method_8293(),
				2,
				this.method_8299().getString(),
				this.method_8299(),
				this.method_8293().method_8503(),
				null
			);
		}
	};

	public class_2593() {
		super(class_2591.field_11904);
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		super.method_11007(arg);
		this.field_11920.method_8297(arg);
		arg.method_10556("powered", this.method_11043());
		arg.method_10556("conditionMet", this.method_11044());
		arg.method_10556("auto", this.method_11042());
		return arg;
	}

	@Override
	public void method_11014(class_2487 arg) {
		super.method_11014(arg);
		this.field_11920.method_8305(arg);
		this.field_11919 = arg.method_10577("powered");
		this.field_11917 = arg.method_10577("conditionMet");
		this.method_11041(arg.method_10577("auto"));
	}

	@Nullable
	@Override
	public class_2622 method_16886() {
		if (this.method_11036()) {
			this.method_11037(false);
			class_2487 lv = this.method_11007(new class_2487());
			return new class_2622(this.field_11867, 2, lv);
		} else {
			return null;
		}
	}

	@Override
	public boolean method_11011() {
		return true;
	}

	public class_1918 method_11040() {
		return this.field_11920;
	}

	public void method_11038(boolean bl) {
		this.field_11919 = bl;
	}

	public boolean method_11043() {
		return this.field_11919;
	}

	public boolean method_11042() {
		return this.field_11918;
	}

	public void method_11041(boolean bl) {
		boolean bl2 = this.field_11918;
		this.field_11918 = bl;
		if (!bl2 && bl && !this.field_11919 && this.field_11863 != null && this.method_11039() != class_2593.class_2594.field_11922) {
			class_2248 lv = this.method_11010().method_11614();
			if (lv instanceof class_2288) {
				this.method_11045();
				this.field_11863.method_8397().method_8676(this.field_11867, lv, lv.method_9563(this.field_11863));
			}
		}
	}

	public boolean method_11044() {
		return this.field_11917;
	}

	public boolean method_11045() {
		this.field_11917 = true;
		if (this.method_11046()) {
			class_2338 lv = this.field_11867
				.method_10093(((class_2350)this.field_11863.method_8320(this.field_11867).method_11654(class_2288.field_10791)).method_10153());
			if (this.field_11863.method_8320(lv).method_11614() instanceof class_2288) {
				class_2586 lv2 = this.field_11863.method_8321(lv);
				this.field_11917 = lv2 instanceof class_2593 && ((class_2593)lv2).method_11040().method_8304() > 0;
			} else {
				this.field_11917 = false;
			}
		}

		return this.field_11917;
	}

	public boolean method_11036() {
		return this.field_11916;
	}

	public void method_11037(boolean bl) {
		this.field_11916 = bl;
	}

	public class_2593.class_2594 method_11039() {
		class_2248 lv = this.method_11010().method_11614();
		if (lv == class_2246.field_10525) {
			return class_2593.class_2594.field_11924;
		} else if (lv == class_2246.field_10263) {
			return class_2593.class_2594.field_11923;
		} else {
			return lv == class_2246.field_10395 ? class_2593.class_2594.field_11922 : class_2593.class_2594.field_11924;
		}
	}

	public boolean method_11046() {
		class_2680 lv = this.field_11863.method_8320(this.method_11016());
		return lv.method_11614() instanceof class_2288 ? (Boolean)lv.method_11654(class_2288.field_10793) : false;
	}

	@Override
	public void method_10996() {
		this.method_11000();
		super.method_10996();
	}

	public static enum class_2594 {
		field_11922,
		field_11923,
		field_11924;
	}
}
