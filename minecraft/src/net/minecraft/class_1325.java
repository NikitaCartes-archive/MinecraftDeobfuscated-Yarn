package net.minecraft;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public abstract class class_1325 {
	protected final Map<class_1320, class_1324> field_6334 = Maps.<class_1320, class_1324>newHashMap();
	protected final Map<String, class_1324> field_6335 = new class_3522();
	protected final Multimap<class_1320, class_1320> field_6336 = HashMultimap.create();

	@Nullable
	public class_1324 method_6205(class_1320 arg) {
		return (class_1324)this.field_6334.get(arg);
	}

	@Nullable
	public class_1324 method_6207(String string) {
		return (class_1324)this.field_6335.get(string);
	}

	public class_1324 method_6208(class_1320 arg) {
		if (this.field_6335.containsKey(arg.method_6167())) {
			throw new IllegalArgumentException("Attribute is already registered!");
		} else {
			class_1324 lv = this.method_6206(arg);
			this.field_6335.put(arg.method_6167(), lv);
			this.field_6334.put(arg, lv);

			for (class_1320 lv2 = arg.method_6166(); lv2 != null; lv2 = lv2.method_6166()) {
				this.field_6336.put(lv2, arg);
			}

			return lv;
		}
	}

	protected abstract class_1324 method_6206(class_1320 arg);

	public Collection<class_1324> method_6204() {
		return this.field_6335.values();
	}

	public void method_6211(class_1324 arg) {
	}

	public void method_6209(Multimap<String, class_1322> multimap) {
		for (Entry<String, class_1322> entry : multimap.entries()) {
			class_1324 lv = this.method_6207((String)entry.getKey());
			if (lv != null) {
				lv.method_6202((class_1322)entry.getValue());
			}
		}
	}

	public void method_6210(Multimap<String, class_1322> multimap) {
		for (Entry<String, class_1322> entry : multimap.entries()) {
			class_1324 lv = this.method_6207((String)entry.getKey());
			if (lv != null) {
				lv.method_6202((class_1322)entry.getValue());
				lv.method_6197((class_1322)entry.getValue());
			}
		}
	}
}
