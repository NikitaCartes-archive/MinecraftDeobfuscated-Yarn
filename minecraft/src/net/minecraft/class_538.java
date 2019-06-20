package net.minecraft;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_538 implements class_535, class_537 {
	private static final Ordering<class_640> field_3267 = Ordering.from(
		(arg, arg2) -> ComparisonChain.start().compare(arg.method_2966().getId(), arg2.method_2966().getId()).result()
	);
	private final List<class_537> field_3268 = Lists.<class_537>newArrayList();

	public class_538() {
		this(field_3267.<class_640>sortedCopy(class_310.method_1551().method_1562().method_2880()));
	}

	public class_538(Collection<class_640> collection) {
		for (class_640 lv : field_3267.sortedCopy(collection)) {
			if (lv.method_2958() != class_1934.field_9219) {
				this.field_3268.add(new class_530(lv.method_2966()));
			}
		}
	}

	@Override
	public List<class_537> method_2780() {
		return this.field_3268;
	}

	@Override
	public class_2561 method_2781() {
		return new class_2588("spectatorMenu.teleport.prompt");
	}

	@Override
	public void method_2783(class_531 arg) {
		arg.method_2778(this);
	}

	@Override
	public class_2561 method_16892() {
		return new class_2588("spectatorMenu.teleport");
	}

	@Override
	public void method_2784(float f, int i) {
		class_310.method_1551().method_1531().method_4618(class_365.field_2199);
		class_332.blit(0, 0, 0.0F, 0.0F, 16, 16, 256, 256);
	}

	@Override
	public boolean method_16893() {
		return !this.field_3268.isEmpty();
	}
}
