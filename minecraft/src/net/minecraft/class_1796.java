package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1796 {
	private final Map<class_1792, class_1796.class_1797> field_8024 = Maps.<class_1792, class_1796.class_1797>newHashMap();
	private int field_8025;

	public boolean method_7904(class_1792 arg) {
		return this.method_7905(arg, 0.0F) > 0.0F;
	}

	public float method_7905(class_1792 arg, float f) {
		class_1796.class_1797 lv = (class_1796.class_1797)this.field_8024.get(arg);
		if (lv != null) {
			float g = (float)(lv.field_8027 - lv.field_8028);
			float h = (float)lv.field_8027 - ((float)this.field_8025 + f);
			return class_3532.method_15363(h / g, 0.0F, 1.0F);
		} else {
			return 0.0F;
		}
	}

	public void method_7903() {
		this.field_8025++;
		if (!this.field_8024.isEmpty()) {
			Iterator<Entry<class_1792, class_1796.class_1797>> iterator = this.field_8024.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<class_1792, class_1796.class_1797> entry = (Entry<class_1792, class_1796.class_1797>)iterator.next();
				if (((class_1796.class_1797)entry.getValue()).field_8027 <= this.field_8025) {
					iterator.remove();
					this.method_7901((class_1792)entry.getKey());
				}
			}
		}
	}

	public void method_7906(class_1792 arg, int i) {
		this.field_8024.put(arg, new class_1796.class_1797(this.field_8025, this.field_8025 + i));
		this.method_7902(arg, i);
	}

	@Environment(EnvType.CLIENT)
	public void method_7900(class_1792 arg) {
		this.field_8024.remove(arg);
		this.method_7901(arg);
	}

	protected void method_7902(class_1792 arg, int i) {
	}

	protected void method_7901(class_1792 arg) {
	}

	class class_1797 {
		private final int field_8028;
		private final int field_8027;

		private class_1797(int i, int j) {
			this.field_8028 = i;
			this.field_8027 = j;
		}
	}
}
