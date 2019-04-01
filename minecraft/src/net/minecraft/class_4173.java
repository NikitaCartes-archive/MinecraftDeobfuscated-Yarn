package net.minecraft;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import java.util.List;

public class class_4173 {
	private final List<class_4169> field_18612 = Lists.<class_4169>newArrayList();
	private int field_18613;

	public class_4173 method_19227(int i, float f) {
		this.field_18612.add(new class_4169(i, f));
		this.method_19229();
		return this;
	}

	private void method_19229() {
		Int2ObjectSortedMap<class_4169> int2ObjectSortedMap = new Int2ObjectAVLTreeMap<>();
		this.field_18612.forEach(arg -> {
			class_4169 var10000 = int2ObjectSortedMap.put(arg.method_19211(), arg);
		});
		this.field_18612.clear();
		this.field_18612.addAll(int2ObjectSortedMap.values());
		this.field_18613 = 0;
	}

	public float method_19226(int i) {
		if (this.field_18612.size() <= 0) {
			return 0.0F;
		} else {
			class_4169 lv = (class_4169)this.field_18612.get(this.field_18613);
			class_4169 lv2 = (class_4169)this.field_18612.get(this.field_18612.size() - 1);
			boolean bl = i < lv.method_19211();
			int j = bl ? 0 : this.field_18613;
			float f = bl ? lv2.method_19212() : lv.method_19212();

			for (int k = j; k < this.field_18612.size(); k++) {
				class_4169 lv3 = (class_4169)this.field_18612.get(k);
				if (lv3.method_19211() > i) {
					break;
				}

				this.field_18613 = k;
				f = lv3.method_19212();
			}

			return f;
		}
	}
}
