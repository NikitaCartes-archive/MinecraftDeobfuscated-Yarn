package net.minecraft;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.RealmsObjectSelectionList;

@Environment(EnvType.CLIENT)
public abstract class class_4371 {
	public final int field_19690;
	public final int field_19691;
	public final int field_19692;
	public final int field_19693;

	public class_4371(int i, int j, int k, int l) {
		this.field_19690 = i;
		this.field_19691 = j;
		this.field_19692 = k;
		this.field_19693 = l;
	}

	public void method_21111(int i, int j, int k, int l) {
		int m = i + this.field_19692;
		int n = j + this.field_19693;
		boolean bl = false;
		if (k >= m && k <= m + this.field_19690 && l >= n && l <= n + this.field_19691) {
			bl = true;
		}

		this.method_21112(m, n, bl);
	}

	protected abstract void method_21112(int i, int j, boolean bl);

	public int method_21109() {
		return this.field_19692 + this.field_19690;
	}

	public int method_21115() {
		return this.field_19693 + this.field_19691;
	}

	public abstract void method_21110(int i);

	public static void method_21113(List<class_4371> list, RealmsObjectSelectionList realmsObjectSelectionList, int i, int j, int k, int l) {
		for (class_4371 lv : list) {
			if (realmsObjectSelectionList.getRowWidth() > lv.method_21109()) {
				lv.method_21111(i, j, k, l);
			}
		}
	}

	public static void method_21114(
		RealmsObjectSelectionList realmsObjectSelectionList, RealmListEntry realmListEntry, List<class_4371> list, int i, double d, double e
	) {
		if (i == 0) {
			int j = realmsObjectSelectionList.children().indexOf(realmListEntry);
			if (j > -1) {
				realmsObjectSelectionList.selectItem(j);
				int k = realmsObjectSelectionList.getRowLeft();
				int l = realmsObjectSelectionList.getRowTop(j);
				int m = (int)(d - (double)k);
				int n = (int)(e - (double)l);

				for (class_4371 lv : list) {
					if (m >= lv.field_19692 && m <= lv.method_21109() && n >= lv.field_19693 && n <= lv.method_21115()) {
						lv.method_21110(j);
					}
				}
			}
		}
	}
}
