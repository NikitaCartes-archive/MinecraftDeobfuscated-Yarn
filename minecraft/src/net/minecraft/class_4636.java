package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.world.gen.decorator.TreeDecorator;

public class class_4636 extends class_4643 {
	public final int field_21233;

	protected class_4636(class_4651 arg, class_4651 arg2, List<TreeDecorator> list, int i, int j) {
		super(arg, arg2, list, i);
		this.field_21233 = j;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		Dynamic<T> dynamic = new Dynamic<>(
			dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("height_interval"), dynamicOps.createInt(this.field_21233)))
		);
		return dynamic.merge(super.serialize(dynamicOps));
	}

	public static <T> class_4636 method_23408(Dynamic<T> dynamic) {
		class_4643 lv = class_4643.method_23444(dynamic);
		return new class_4636(lv.field_21288, lv.field_21289, lv.field_21290, lv.field_21291, dynamic.get("height_interval").asInt(0));
	}

	public static class class_4637 extends class_4643.class_4644 {
		private List<TreeDecorator> field_21234 = ImmutableList.of();
		private int field_21235;
		private int field_21236;

		public class_4637(class_4651 arg, class_4651 arg2) {
			super(arg, arg2);
		}

		public class_4636.class_4637 method_23411(List<TreeDecorator> list) {
			this.field_21234 = list;
			return this;
		}

		public class_4636.class_4637 method_23410(int i) {
			this.field_21235 = i;
			return this;
		}

		public class_4636.class_4637 method_23412(int i) {
			this.field_21236 = i;
			return this;
		}

		public class_4636 method_23409() {
			return new class_4636(this.field_21292, this.field_21293, this.field_21234, this.field_21235, this.field_21236);
		}
	}
}
