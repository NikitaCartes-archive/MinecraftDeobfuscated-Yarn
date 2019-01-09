package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_1917 {
	private static final Logger field_9156 = LogManager.getLogger();
	private int field_9154 = 20;
	private final List<class_1952> field_9152 = Lists.<class_1952>newArrayList();
	private class_1952 field_9155 = new class_1952();
	private double field_9161;
	private double field_9159;
	private int field_9151 = 200;
	private int field_9150 = 800;
	private int field_9149 = 4;
	private class_1297 field_9153;
	private int field_9160 = 6;
	private int field_9158 = 16;
	private int field_9157 = 4;

	@Nullable
	private class_2960 method_8281() {
		String string = this.field_9155.method_8678().method_10558("id");

		try {
			return class_3544.method_15438(string) ? null : new class_2960(string);
		} catch (class_151 var4) {
			class_2338 lv2 = this.method_8276();
			field_9156.warn(
				"Invalid entity id '{}' at spawner {}:[{},{},{}]",
				string,
				this.method_8271().field_9247.method_12460(),
				lv2.method_10263(),
				lv2.method_10264(),
				lv2.method_10260()
			);
			return null;
		}
	}

	public void method_8274(class_1299<?> arg) {
		this.field_9155.method_8678().method_10582("id", class_2378.field_11145.method_10221(arg).toString());
	}

	private boolean method_8284() {
		class_2338 lv = this.method_8276();
		return this.method_8271()
			.method_8523((double)lv.method_10263() + 0.5, (double)lv.method_10264() + 0.5, (double)lv.method_10260() + 0.5, (double)this.field_9158);
	}

	public void method_8285() {
		if (!this.method_8284()) {
			this.field_9159 = this.field_9161;
		} else {
			class_2338 lv = this.method_8276();
			if (this.method_8271().field_9236) {
				double d = (double)((float)lv.method_10263() + this.method_8271().field_9229.nextFloat());
				double e = (double)((float)lv.method_10264() + this.method_8271().field_9229.nextFloat());
				double f = (double)((float)lv.method_10260() + this.method_8271().field_9229.nextFloat());
				this.method_8271().method_8406(class_2398.field_11251, d, e, f, 0.0, 0.0, 0.0);
				this.method_8271().method_8406(class_2398.field_11240, d, e, f, 0.0, 0.0, 0.0);
				if (this.field_9154 > 0) {
					this.field_9154--;
				}

				this.field_9159 = this.field_9161;
				this.field_9161 = (this.field_9161 + (double)(1000.0F / ((float)this.field_9154 + 200.0F))) % 360.0;
			} else {
				if (this.field_9154 == -1) {
					this.method_8282();
				}

				if (this.field_9154 > 0) {
					this.field_9154--;
					return;
				}

				boolean bl = false;

				for (int i = 0; i < this.field_9149; i++) {
					class_2487 lv2 = this.field_9155.method_8678();
					class_2499 lv3 = lv2.method_10554("Pos", 6);
					class_1937 lv4 = this.method_8271();
					int j = lv3.size();
					double g = j >= 1
						? lv3.method_10611(0)
						: (double)lv.method_10263() + (lv4.field_9229.nextDouble() - lv4.field_9229.nextDouble()) * (double)this.field_9157 + 0.5;
					double h = j >= 2 ? lv3.method_10611(1) : (double)(lv.method_10264() + lv4.field_9229.nextInt(3) - 1);
					double k = j >= 3
						? lv3.method_10611(2)
						: (double)lv.method_10260() + (lv4.field_9229.nextDouble() - lv4.field_9229.nextDouble()) * (double)this.field_9157 + 0.5;
					class_1297 lv5 = class_2852.method_12399(lv2, lv4, g, h, k, false);
					if (lv5 == null) {
						this.method_8282();
						return;
					}

					int l = lv4.method_8403(
							lv5.getClass(),
							new class_238(
									(double)lv.method_10263(),
									(double)lv.method_10264(),
									(double)lv.method_10260(),
									(double)(lv.method_10263() + 1),
									(double)(lv.method_10264() + 1),
									(double)(lv.method_10260() + 1)
								)
								.method_1014((double)this.field_9157)
						)
						.size();
					if (l >= this.field_9160) {
						this.method_8282();
						return;
					}

					class_1308 lv6 = lv5 instanceof class_1308 ? (class_1308)lv5 : null;
					lv5.method_5808(lv5.field_5987, lv5.field_6010, lv5.field_6035, lv4.field_9229.nextFloat() * 360.0F, 0.0F);
					if (lv6 == null || lv6.method_5979(lv4, class_3730.field_16469) && lv6.method_5950()) {
						if (this.field_9155.method_8678().method_10546() == 1 && this.field_9155.method_8678().method_10573("id", 8) && lv5 instanceof class_1308) {
							((class_1308)lv5).method_5943(lv4, lv4.method_8404(new class_2338(lv5)), class_3730.field_16469, null, null);
						}

						class_2852.method_12394(lv5, lv4);
						lv4.method_8535(2004, lv, 0);
						if (lv6 != null) {
							lv6.method_5990();
						}

						bl = true;
					}
				}

				if (bl) {
					this.method_8282();
				}
			}
		}
	}

	private void method_8282() {
		if (this.field_9150 <= this.field_9151) {
			this.field_9154 = this.field_9151;
		} else {
			this.field_9154 = this.field_9151 + this.method_8271().field_9229.nextInt(this.field_9150 - this.field_9151);
		}

		if (!this.field_9152.isEmpty()) {
			this.method_8277(class_3549.method_15446(this.method_8271().field_9229, this.field_9152));
		}

		this.method_8273(1);
	}

	public void method_8280(class_2487 arg) {
		this.field_9154 = arg.method_10568("Delay");
		this.field_9152.clear();
		if (arg.method_10573("SpawnPotentials", 9)) {
			class_2499 lv = arg.method_10554("SpawnPotentials", 10);

			for (int i = 0; i < lv.size(); i++) {
				this.field_9152.add(new class_1952(lv.method_10602(i)));
			}
		}

		if (arg.method_10573("SpawnData", 10)) {
			this.method_8277(new class_1952(1, arg.method_10562("SpawnData")));
		} else if (!this.field_9152.isEmpty()) {
			this.method_8277(class_3549.method_15446(this.method_8271().field_9229, this.field_9152));
		}

		if (arg.method_10573("MinSpawnDelay", 99)) {
			this.field_9151 = arg.method_10568("MinSpawnDelay");
			this.field_9150 = arg.method_10568("MaxSpawnDelay");
			this.field_9149 = arg.method_10568("SpawnCount");
		}

		if (arg.method_10573("MaxNearbyEntities", 99)) {
			this.field_9160 = arg.method_10568("MaxNearbyEntities");
			this.field_9158 = arg.method_10568("RequiredPlayerRange");
		}

		if (arg.method_10573("SpawnRange", 99)) {
			this.field_9157 = arg.method_10568("SpawnRange");
		}

		if (this.method_8271() != null) {
			this.field_9153 = null;
		}
	}

	public class_2487 method_8272(class_2487 arg) {
		class_2960 lv = this.method_8281();
		if (lv == null) {
			return arg;
		} else {
			arg.method_10575("Delay", (short)this.field_9154);
			arg.method_10575("MinSpawnDelay", (short)this.field_9151);
			arg.method_10575("MaxSpawnDelay", (short)this.field_9150);
			arg.method_10575("SpawnCount", (short)this.field_9149);
			arg.method_10575("MaxNearbyEntities", (short)this.field_9160);
			arg.method_10575("RequiredPlayerRange", (short)this.field_9158);
			arg.method_10575("SpawnRange", (short)this.field_9157);
			arg.method_10566("SpawnData", this.field_9155.method_8678().method_10553());
			class_2499 lv2 = new class_2499();
			if (this.field_9152.isEmpty()) {
				lv2.method_10606(this.field_9155.method_8679());
			} else {
				for (class_1952 lv3 : this.field_9152) {
					lv2.method_10606(lv3.method_8679());
				}
			}

			arg.method_10566("SpawnPotentials", lv2);
			return arg;
		}
	}

	@Environment(EnvType.CLIENT)
	public class_1297 method_8283() {
		if (this.field_9153 == null) {
			this.field_9153 = class_2852.method_12378(this.field_9155.method_8678(), this.method_8271(), false);
			if (this.field_9155.method_8678().method_10546() == 1 && this.field_9155.method_8678().method_10573("id", 8) && this.field_9153 instanceof class_1308) {
				((class_1308)this.field_9153)
					.method_5943(this.method_8271(), this.method_8271().method_8404(new class_2338(this.field_9153)), class_3730.field_16469, null, null);
			}
		}

		return this.field_9153;
	}

	public boolean method_8275(int i) {
		if (i == 1 && this.method_8271().field_9236) {
			this.field_9154 = this.field_9151;
			return true;
		} else {
			return false;
		}
	}

	public void method_8277(class_1952 arg) {
		this.field_9155 = arg;
	}

	public abstract void method_8273(int i);

	public abstract class_1937 method_8271();

	public abstract class_2338 method_8276();

	@Environment(EnvType.CLIENT)
	public double method_8278() {
		return this.field_9161;
	}

	@Environment(EnvType.CLIENT)
	public double method_8279() {
		return this.field_9159;
	}
}
