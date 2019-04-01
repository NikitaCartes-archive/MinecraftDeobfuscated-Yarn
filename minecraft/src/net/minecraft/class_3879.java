package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3879 {
	public final List<class_630> field_17137 = Lists.<class_630>newArrayList();
	public int field_17138 = 64;
	public int field_17139 = 32;

	public class_630 method_17101(Random random) {
		return (class_630)this.field_17137.get(random.nextInt(this.field_17137.size()));
	}
}
