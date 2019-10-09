package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.TreeDecorator;

public class class_4640 extends class_4643 {
	public final class_4647 field_21259;
	public final int field_21260;
	public final int field_21261;
	public final int field_21262;
	public final int field_21263;
	public final int field_21264;
	public final int field_21265;
	public final int field_21266;
	public final int field_21267;
	public final int field_21268;
	public final boolean field_21269;

	protected class_4640(
		class_4651 arg, class_4651 arg2, class_4647 arg3, List<TreeDecorator> list, int i, int j, int k, int l, int m, int n, int o, int p, int q, int r, boolean bl
	) {
		super(arg, arg2, list, i);
		this.field_21259 = arg3;
		this.field_21260 = j;
		this.field_21261 = k;
		this.field_21262 = l;
		this.field_21263 = m;
		this.field_21264 = n;
		this.field_21265 = o;
		this.field_21266 = p;
		this.field_21267 = q;
		this.field_21268 = r;
		this.field_21269 = bl;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(dynamicOps.createString("foliage_placer"), this.field_21259.serialize(dynamicOps))
			.put(dynamicOps.createString("height_rand_a"), dynamicOps.createInt(this.field_21260))
			.put(dynamicOps.createString("height_rand_b"), dynamicOps.createInt(this.field_21261))
			.put(dynamicOps.createString("trunk_height"), dynamicOps.createInt(this.field_21262))
			.put(dynamicOps.createString("trunk_height_random"), dynamicOps.createInt(this.field_21263))
			.put(dynamicOps.createString("trunk_top_offset"), dynamicOps.createInt(this.field_21264))
			.put(dynamicOps.createString("trunk_top_offset_random"), dynamicOps.createInt(this.field_21265))
			.put(dynamicOps.createString("foliage_height"), dynamicOps.createInt(this.field_21266))
			.put(dynamicOps.createString("foliage_height_random"), dynamicOps.createInt(this.field_21267))
			.put(dynamicOps.createString("max_water_depth"), dynamicOps.createInt(this.field_21268))
			.put(dynamicOps.createString("ignore_vines"), dynamicOps.createBoolean(this.field_21269));
		Dynamic<T> dynamic = new Dynamic<>(dynamicOps, dynamicOps.createMap(builder.build()));
		return dynamic.merge(super.serialize(dynamicOps));
	}

	public static <T> class_4640 method_23426(Dynamic<T> dynamic) {
		class_4643 lv = class_4643.method_23444(dynamic);
		class_4648<?> lv2 = Registry.FOLIAGE_PLACER_TYPE
			.get(new Identifier((String)dynamic.get("foliage_placer").get("type").asString().orElseThrow(RuntimeException::new)));
		return new class_4640(
			lv.field_21288,
			lv.field_21289,
			lv2.method_23453(dynamic.get("foliage_placer").orElseEmptyMap()),
			lv.field_21290,
			lv.field_21291,
			dynamic.get("height_rand_a").asInt(0),
			dynamic.get("height_rand_b").asInt(0),
			dynamic.get("trunk_height").asInt(-1),
			dynamic.get("trunk_height_random").asInt(0),
			dynamic.get("trunk_top_offset").asInt(0),
			dynamic.get("trunk_top_offset_random").asInt(0),
			dynamic.get("foliage_height").asInt(-1),
			dynamic.get("foliage_height_random").asInt(0),
			dynamic.get("max_water_depth").asInt(0),
			dynamic.get("ignore_vines").asBoolean(false)
		);
	}

	public static class class_4641 extends class_4643.class_4644 {
		private final class_4647 field_21270;
		private List<TreeDecorator> field_21271 = ImmutableList.of();
		private int field_21272;
		private int field_21273;
		private int field_21274;
		private int field_21275 = -1;
		private int field_21276;
		private int field_21277;
		private int field_21278;
		private int field_21279 = -1;
		private int field_21280;
		private int field_21281;
		private boolean field_21282;

		public class_4641(class_4651 arg, class_4651 arg2, class_4647 arg3) {
			super(arg, arg2);
			this.field_21270 = arg3;
		}

		public class_4640.class_4641 method_23429(List<TreeDecorator> list) {
			this.field_21271 = list;
			return this;
		}

		public class_4640.class_4641 method_23428(int i) {
			this.field_21272 = i;
			return this;
		}

		public class_4640.class_4641 method_23430(int i) {
			this.field_21273 = i;
			return this;
		}

		public class_4640.class_4641 method_23432(int i) {
			this.field_21274 = i;
			return this;
		}

		public class_4640.class_4641 method_23433(int i) {
			this.field_21275 = i;
			return this;
		}

		public class_4640.class_4641 method_23434(int i) {
			this.field_21276 = i;
			return this;
		}

		public class_4640.class_4641 method_23435(int i) {
			this.field_21277 = i;
			return this;
		}

		public class_4640.class_4641 method_23436(int i) {
			this.field_21278 = i;
			return this;
		}

		public class_4640.class_4641 method_23437(int i) {
			this.field_21279 = i;
			return this;
		}

		public class_4640.class_4641 method_23438(int i) {
			this.field_21280 = i;
			return this;
		}

		public class_4640.class_4641 method_23439(int i) {
			this.field_21281 = i;
			return this;
		}

		public class_4640.class_4641 method_23427() {
			this.field_21282 = true;
			return this;
		}

		public class_4640 method_23431() {
			return new class_4640(
				this.field_21292,
				this.field_21293,
				this.field_21270,
				this.field_21271,
				this.field_21272,
				this.field_21273,
				this.field_21274,
				this.field_21275,
				this.field_21276,
				this.field_21277,
				this.field_21278,
				this.field_21279,
				this.field_21280,
				this.field_21281,
				this.field_21282
			);
		}
	}
}
