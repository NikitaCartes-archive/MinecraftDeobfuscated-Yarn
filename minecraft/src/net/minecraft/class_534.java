package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_534 implements class_535 {
	private final List<class_537> field_3266 = Lists.<class_537>newArrayList();

	public class_534() {
		this.field_3266.add(new class_538());
		this.field_3266.add(new class_540());
	}

	@Override
	public List<class_537> method_2780() {
		return this.field_3266;
	}

	@Override
	public class_2561 method_2781() {
		return new class_2588("spectatorMenu.root.prompt");
	}
}
