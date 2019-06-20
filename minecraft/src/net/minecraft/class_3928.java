package net.minecraft;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3928 extends class_437 {
	private final class_3953 field_17406;
	private long field_19101 = -1L;
	private static final Object2IntMap<class_2806> field_17407 = class_156.method_654(new Object2IntOpenHashMap<>(), object2IntOpenHashMap -> {
		object2IntOpenHashMap.defaultReturnValue(0);
		object2IntOpenHashMap.put(class_2806.field_12798, 5526612);
		object2IntOpenHashMap.put(class_2806.field_16423, 10066329);
		object2IntOpenHashMap.put(class_2806.field_16422, 6250897);
		object2IntOpenHashMap.put(class_2806.field_12794, 8434258);
		object2IntOpenHashMap.put(class_2806.field_12804, 13750737);
		object2IntOpenHashMap.put(class_2806.field_12796, 7497737);
		object2IntOpenHashMap.put(class_2806.field_12801, 7169628);
		object2IntOpenHashMap.put(class_2806.field_12790, 3159410);
		object2IntOpenHashMap.put(class_2806.field_12795, 2213376);
		object2IntOpenHashMap.put(class_2806.field_12805, 13421772);
		object2IntOpenHashMap.put(class_2806.field_12786, 15884384);
		object2IntOpenHashMap.put(class_2806.field_12800, 15658734);
		object2IntOpenHashMap.put(class_2806.field_12803, 16777215);
	});

	public class_3928(class_3953 arg) {
		super(class_333.field_18967);
		this.field_17406 = arg;
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public void removed() {
		class_333.field_2054.method_19788(class_1074.method_4662("narrator.loading.done"));
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		String string = class_3532.method_15340(this.field_17406.method_17679(), 0, 100) + "%";
		long l = class_156.method_658();
		if (l - this.field_19101 > 2000L) {
			this.field_19101 = l;
			class_333.field_2054.method_19788(new class_2588("narrator.loading", string).getString());
		}

		int k = this.width / 2;
		int m = this.height / 2;
		int n = 30;
		method_17538(this.field_17406, k, m + 30, 2, 0);
		this.drawCenteredString(this.font, string, k, m - 9 / 2 - 30, 16777215);
	}

	public static void method_17538(class_3953 arg, int i, int j, int k, int l) {
		int m = k + l;
		int n = arg.method_17677();
		int o = n * m - l;
		int p = arg.method_17678();
		int q = p * m - l;
		int r = i - q / 2;
		int s = j - q / 2;
		int t = o / 2 + 1;
		int u = -16772609;
		if (l != 0) {
			fill(i - t, j - t, i - t + 1, j + t, -16772609);
			fill(i + t - 1, j - t, i + t, j + t, -16772609);
			fill(i - t, j - t, i + t, j - t + 1, -16772609);
			fill(i - t, j + t - 1, i + t, j + t, -16772609);
		}

		for (int v = 0; v < p; v++) {
			for (int w = 0; w < p; w++) {
				class_2806 lv = arg.method_17676(v, w);
				int x = r + v * m;
				int y = s + w * m;
				fill(x, y, x + k, y + k, field_17407.getInt(lv) | 0xFF000000);
			}
		}
	}
}
