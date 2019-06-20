package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_337 extends class_332 {
	private static final class_2960 field_2059 = new class_2960("textures/gui/bars.png");
	private final class_310 field_2058;
	private final Map<UUID, class_345> field_2060 = Maps.<UUID, class_345>newLinkedHashMap();

	public class_337(class_310 arg) {
		this.field_2058 = arg;
	}

	public void method_1796() {
		if (!this.field_2060.isEmpty()) {
			int i = this.field_2058.field_1704.method_4486();
			int j = 12;

			for (class_345 lv : this.field_2060.values()) {
				int k = i / 2 - 91;
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.field_2058.method_1531().method_4618(field_2059);
				this.method_1799(k, j, lv);
				String string = lv.method_5414().method_10863();
				int m = this.field_2058.field_1772.method_1727(string);
				int n = i / 2 - m / 2;
				int o = j - 9;
				this.field_2058.field_1772.method_1720(string, (float)n, (float)o, 16777215);
				j += 10 + 9;
				if (j >= this.field_2058.field_1704.method_4502() / 3) {
					break;
				}
			}
		}
	}

	private void method_1799(int i, int j, class_1259 arg) {
		this.blit(i, j, 0, arg.method_5420().ordinal() * 5 * 2, 182, 5);
		if (arg.method_5415() != class_1259.class_1261.field_5795) {
			this.blit(i, j, 0, 80 + (arg.method_5415().ordinal() - 1) * 5 * 2, 182, 5);
		}

		int k = (int)(arg.method_5412() * 183.0F);
		if (k > 0) {
			this.blit(i, j, 0, arg.method_5420().ordinal() * 5 * 2 + 5, k, 5);
			if (arg.method_5415() != class_1259.class_1261.field_5795) {
				this.blit(i, j, 0, 80 + (arg.method_5415().ordinal() - 1) * 5 * 2 + 5, k, 5);
			}
		}
	}

	public void method_1795(class_2629 arg) {
		if (arg.method_11324() == class_2629.class_2630.field_12078) {
			this.field_2060.put(arg.method_11322(), new class_345(arg));
		} else if (arg.method_11324() == class_2629.class_2630.field_12082) {
			this.field_2060.remove(arg.method_11322());
		} else {
			((class_345)this.field_2060.get(arg.method_11322())).method_1894(arg);
		}
	}

	public void method_1801() {
		this.field_2060.clear();
	}

	public boolean method_1798() {
		if (!this.field_2060.isEmpty()) {
			for (class_1259 lv : this.field_2060.values()) {
				if (lv.method_5418()) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean method_1797() {
		if (!this.field_2060.isEmpty()) {
			for (class_1259 lv : this.field_2060.values()) {
				if (lv.method_5417()) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean method_1800() {
		if (!this.field_2060.isEmpty()) {
			for (class_1259 lv : this.field_2060.values()) {
				if (lv.method_5419()) {
					return true;
				}
			}
		}

		return false;
	}
}
