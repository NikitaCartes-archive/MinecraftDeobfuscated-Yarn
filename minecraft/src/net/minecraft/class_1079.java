package net.minecraft;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1079 {
	public static final class_1081 field_5337 = new class_1081();
	private final List<class_1080> field_5339;
	private final int field_5338;
	private final int field_5336;
	private final int field_5334;
	private final boolean field_5335;

	public class_1079(List<class_1080> list, int i, int j, int k, boolean bl) {
		this.field_5339 = list;
		this.field_5338 = i;
		this.field_5336 = j;
		this.field_5334 = k;
		this.field_5335 = bl;
	}

	public int method_4686() {
		return this.field_5336;
	}

	public int method_4687() {
		return this.field_5338;
	}

	public int method_4682() {
		return this.field_5339.size();
	}

	public int method_4684() {
		return this.field_5334;
	}

	public boolean method_4685() {
		return this.field_5335;
	}

	private class_1080 method_4681(int i) {
		return (class_1080)this.field_5339.get(i);
	}

	public int method_4683(int i) {
		class_1080 lv = this.method_4681(i);
		return lv.method_4689() ? this.field_5334 : lv.method_4691();
	}

	public int method_4680(int i) {
		return ((class_1080)this.field_5339.get(i)).method_4690();
	}

	public Set<Integer> method_4688() {
		Set<Integer> set = Sets.<Integer>newHashSet();

		for (class_1080 lv : this.field_5339) {
			set.add(lv.method_4690());
		}

		return set;
	}
}
