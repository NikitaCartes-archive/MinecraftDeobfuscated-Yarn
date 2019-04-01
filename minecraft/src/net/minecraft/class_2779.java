package net.minecraft;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2779 implements class_2596<class_2602> {
	private boolean field_12718;
	private Map<class_2960, class_161.class_162> field_12717;
	private Set<class_2960> field_12715;
	private Map<class_2960, class_167> field_12716;

	public class_2779() {
	}

	public class_2779(boolean bl, Collection<class_161> collection, Set<class_2960> set, Map<class_2960, class_167> map) {
		this.field_12718 = bl;
		this.field_12717 = Maps.<class_2960, class_161.class_162>newHashMap();

		for (class_161 lv : collection) {
			this.field_12717.put(lv.method_688(), lv.method_689());
		}

		this.field_12715 = set;
		this.field_12716 = Maps.<class_2960, class_167>newHashMap(map);
	}

	public void method_11925(class_2602 arg) {
		arg.method_11130(this);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12718 = arg.readBoolean();
		this.field_12717 = Maps.<class_2960, class_161.class_162>newHashMap();
		this.field_12715 = Sets.<class_2960>newLinkedHashSet();
		this.field_12716 = Maps.<class_2960, class_167>newHashMap();
		int i = arg.method_10816();

		for (int j = 0; j < i; j++) {
			class_2960 lv = arg.method_10810();
			class_161.class_162 lv2 = class_161.class_162.method_696(arg);
			this.field_12717.put(lv, lv2);
		}

		i = arg.method_10816();

		for (int j = 0; j < i; j++) {
			class_2960 lv = arg.method_10810();
			this.field_12715.add(lv);
		}

		i = arg.method_10816();

		for (int j = 0; j < i; j++) {
			class_2960 lv = arg.method_10810();
			this.field_12716.put(lv, class_167.method_732(arg));
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeBoolean(this.field_12718);
		arg.method_10804(this.field_12717.size());

		for (Entry<class_2960, class_161.class_162> entry : this.field_12717.entrySet()) {
			class_2960 lv = (class_2960)entry.getKey();
			class_161.class_162 lv2 = (class_161.class_162)entry.getValue();
			arg.method_10812(lv);
			lv2.method_699(arg);
		}

		arg.method_10804(this.field_12715.size());

		for (class_2960 lv3 : this.field_12715) {
			arg.method_10812(lv3);
		}

		arg.method_10804(this.field_12716.size());

		for (Entry<class_2960, class_167> entry : this.field_12716.entrySet()) {
			arg.method_10812((class_2960)entry.getKey());
			((class_167)entry.getValue()).method_733(arg);
		}
	}

	@Environment(EnvType.CLIENT)
	public Map<class_2960, class_161.class_162> method_11928() {
		return this.field_12717;
	}

	@Environment(EnvType.CLIENT)
	public Set<class_2960> method_11926() {
		return this.field_12715;
	}

	@Environment(EnvType.CLIENT)
	public Map<class_2960, class_167> method_11927() {
		return this.field_12716;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11924() {
		return this.field_12718;
	}
}
