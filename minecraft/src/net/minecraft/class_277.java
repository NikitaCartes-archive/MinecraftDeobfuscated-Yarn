package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_277 {
	private static class_277 field_1484;
	private final int field_1491;
	private final int field_1490;
	private final int field_1489;
	private final int field_1488;
	private final int field_1486;
	private final boolean field_1487;
	private final boolean field_1485;

	private class_277(boolean bl, boolean bl2, int i, int j, int k, int l, int m) {
		this.field_1487 = bl;
		this.field_1491 = i;
		this.field_1489 = j;
		this.field_1490 = k;
		this.field_1488 = l;
		this.field_1485 = bl2;
		this.field_1486 = m;
	}

	public class_277() {
		this(false, true, 1, 0, 1, 0, 32774);
	}

	public class_277(int i, int j, int k) {
		this(false, false, i, j, i, j, k);
	}

	public class_277(int i, int j, int k, int l, int m) {
		this(true, false, i, j, k, l, m);
	}

	public void method_1244() {
		if (!this.equals(field_1484)) {
			if (field_1484 == null || this.field_1485 != field_1484.method_1245()) {
				field_1484 = this;
				if (this.field_1485) {
					GlStateManager.disableBlend();
					return;
				}

				GlStateManager.enableBlend();
			}

			GlStateManager.blendEquation(this.field_1486);
			if (this.field_1487) {
				GlStateManager.blendFuncSeparate(this.field_1491, this.field_1489, this.field_1490, this.field_1488);
			} else {
				GlStateManager.blendFunc(this.field_1491, this.field_1489);
			}
		}
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_277)) {
			return false;
		} else {
			class_277 lv = (class_277)object;
			if (this.field_1486 != lv.field_1486) {
				return false;
			} else if (this.field_1488 != lv.field_1488) {
				return false;
			} else if (this.field_1489 != lv.field_1489) {
				return false;
			} else if (this.field_1485 != lv.field_1485) {
				return false;
			} else if (this.field_1487 != lv.field_1487) {
				return false;
			} else {
				return this.field_1490 != lv.field_1490 ? false : this.field_1491 == lv.field_1491;
			}
		}
	}

	public int hashCode() {
		int i = this.field_1491;
		i = 31 * i + this.field_1490;
		i = 31 * i + this.field_1489;
		i = 31 * i + this.field_1488;
		i = 31 * i + this.field_1486;
		i = 31 * i + (this.field_1487 ? 1 : 0);
		return 31 * i + (this.field_1485 ? 1 : 0);
	}

	public boolean method_1245() {
		return this.field_1485;
	}

	public static int method_1247(String string) {
		String string2 = string.trim().toLowerCase(Locale.ROOT);
		if ("add".equals(string2)) {
			return 32774;
		} else if ("subtract".equals(string2)) {
			return 32778;
		} else if ("reversesubtract".equals(string2)) {
			return 32779;
		} else if ("reverse_subtract".equals(string2)) {
			return 32779;
		} else if ("min".equals(string2)) {
			return 32775;
		} else {
			return "max".equals(string2) ? 32776 : 32774;
		}
	}

	public static int method_1243(String string) {
		String string2 = string.trim().toLowerCase(Locale.ROOT);
		string2 = string2.replaceAll("_", "");
		string2 = string2.replaceAll("one", "1");
		string2 = string2.replaceAll("zero", "0");
		string2 = string2.replaceAll("minus", "-");
		if ("0".equals(string2)) {
			return 0;
		} else if ("1".equals(string2)) {
			return 1;
		} else if ("srccolor".equals(string2)) {
			return 768;
		} else if ("1-srccolor".equals(string2)) {
			return 769;
		} else if ("dstcolor".equals(string2)) {
			return 774;
		} else if ("1-dstcolor".equals(string2)) {
			return 775;
		} else if ("srcalpha".equals(string2)) {
			return 770;
		} else if ("1-srcalpha".equals(string2)) {
			return 771;
		} else if ("dstalpha".equals(string2)) {
			return 772;
		} else {
			return "1-dstalpha".equals(string2) ? 773 : -1;
		}
	}
}
