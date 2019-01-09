package net.minecraft;

import com.google.common.base.MoreObjects;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_539 {
	private final class_535 field_3270;
	private final List<class_537> field_3271;
	private final int field_3269;

	public class_539(class_535 arg, List<class_537> list, int i) {
		this.field_3270 = arg;
		this.field_3271 = list;
		this.field_3269 = i;
	}

	public class_537 method_2786(int i) {
		return i >= 0 && i < this.field_3271.size() ? MoreObjects.firstNonNull((class_537)this.field_3271.get(i), class_531.field_3260) : class_531.field_3260;
	}

	public int method_2787() {
		return this.field_3269;
	}
}
