package net.minecraft;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class class_3002 extends class_3213 {
	private final class_2960 field_13441;
	private final Set<UUID> field_13440 = Sets.<UUID>newHashSet();
	private int field_13443;
	private int field_13442 = 100;

	public class_3002(class_2960 arg, class_2561 arg2) {
		super(arg2, class_1259.class_1260.field_5786, class_1259.class_1261.field_5795);
		this.field_13441 = arg;
		this.method_5408(0.0F);
	}

	public class_2960 method_12959() {
		return this.field_13441;
	}

	@Override
	public void method_14088(class_3222 arg) {
		super.method_14088(arg);
		this.field_13440.add(arg.method_5667());
	}

	public void method_12964(UUID uUID) {
		this.field_13440.add(uUID);
	}

	@Override
	public void method_14089(class_3222 arg) {
		super.method_14089(arg);
		this.field_13440.remove(arg.method_5667());
	}

	@Override
	public void method_14094() {
		super.method_14094();
		this.field_13440.clear();
	}

	public int method_12955() {
		return this.field_13443;
	}

	public int method_12960() {
		return this.field_13442;
	}

	public void method_12954(int i) {
		this.field_13443 = i;
		this.method_5408(class_3532.method_15363((float)i / (float)this.field_13442, 0.0F, 1.0F));
	}

	public void method_12956(int i) {
		this.field_13442 = i;
		this.method_5408(class_3532.method_15363((float)this.field_13443 / (float)i, 0.0F, 1.0F));
	}

	public final class_2561 method_12965() {
		return class_2564.method_10885(this.method_5414())
			.method_10859(
				arg -> arg.method_10977(this.method_5420().method_5423())
						.method_10949(new class_2568(class_2568.class_2569.field_11762, new class_2585(this.method_12959().toString())))
						.method_10975(this.method_12959().toString())
			);
	}

	public boolean method_12962(Collection<class_3222> collection) {
		Set<UUID> set = Sets.<UUID>newHashSet();
		Set<class_3222> set2 = Sets.<class_3222>newHashSet();

		for (UUID uUID : this.field_13440) {
			boolean bl = false;

			for (class_3222 lv : collection) {
				if (lv.method_5667().equals(uUID)) {
					bl = true;
					break;
				}
			}

			if (!bl) {
				set.add(uUID);
			}
		}

		for (class_3222 lv2 : collection) {
			boolean bl = false;

			for (UUID uUID2 : this.field_13440) {
				if (lv2.method_5667().equals(uUID2)) {
					bl = true;
					break;
				}
			}

			if (!bl) {
				set2.add(lv2);
			}
		}

		for (UUID uUID : set) {
			for (class_3222 lv3 : this.method_14092()) {
				if (lv3.method_5667().equals(uUID)) {
					this.method_14089(lv3);
					break;
				}
			}

			this.field_13440.remove(uUID);
		}

		for (class_3222 lv2 : set2) {
			this.method_14088(lv2);
		}

		return !set.isEmpty() || !set2.isEmpty();
	}

	public class_2487 method_12963() {
		class_2487 lv = new class_2487();
		lv.method_10582("Name", class_2561.class_2562.method_10867(this.field_5777));
		lv.method_10556("Visible", this.method_14093());
		lv.method_10569("Value", this.field_13443);
		lv.method_10569("Max", this.field_13442);
		lv.method_10582("Color", this.method_5420().method_5421());
		lv.method_10582("Overlay", this.method_5415().method_5425());
		lv.method_10556("DarkenScreen", this.method_5417());
		lv.method_10556("PlayBossMusic", this.method_5418());
		lv.method_10556("CreateWorldFog", this.method_5419());
		class_2499 lv2 = new class_2499();

		for (UUID uUID : this.field_13440) {
			lv2.method_10606(class_2512.method_10689(uUID));
		}

		lv.method_10566("Players", lv2);
		return lv;
	}

	public static class_3002 method_12966(class_2487 arg, class_2960 arg2) {
		class_3002 lv = new class_3002(arg2, class_2561.class_2562.method_10877(arg.method_10558("Name")));
		lv.method_14091(arg.method_10577("Visible"));
		lv.method_12954(arg.method_10550("Value"));
		lv.method_12956(arg.method_10550("Max"));
		lv.method_5416(class_1259.class_1260.method_5422(arg.method_10558("Color")));
		lv.method_5409(class_1259.class_1261.method_5424(arg.method_10558("Overlay")));
		lv.method_5406(arg.method_10577("DarkenScreen"));
		lv.method_5410(arg.method_10577("PlayBossMusic"));
		lv.method_5411(arg.method_10577("CreateWorldFog"));
		class_2499 lv2 = arg.method_10554("Players", 10);

		for (int i = 0; i < lv2.size(); i++) {
			lv.method_12964(class_2512.method_10690(lv2.method_10602(i)));
		}

		return lv;
	}

	public void method_12957(class_3222 arg) {
		if (this.field_13440.contains(arg.method_5667())) {
			this.method_14088(arg);
		}
	}

	public void method_12961(class_3222 arg) {
		super.method_14089(arg);
	}
}
