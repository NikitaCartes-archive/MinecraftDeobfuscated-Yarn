package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.FeatureConfig;

public class class_4638 implements FeatureConfig {
	public final class_4651 field_21237;
	public final class_4629 field_21238;
	public final Set<Block> field_21239;
	public final Set<BlockState> field_21240;
	public final int field_21241;
	public final int field_21242;
	public final int field_21243;
	public final int field_21244;
	public final boolean field_21245;
	public final boolean field_21246;
	public final boolean field_21247;

	private class_4638(class_4651 arg, class_4629 arg2, Set<Block> set, Set<BlockState> set2, int i, int j, int k, int l, boolean bl, boolean bl2, boolean bl3) {
		this.field_21237 = arg;
		this.field_21238 = arg2;
		this.field_21239 = set;
		this.field_21240 = set2;
		this.field_21241 = i;
		this.field_21242 = j;
		this.field_21243 = k;
		this.field_21244 = l;
		this.field_21245 = bl;
		this.field_21246 = bl2;
		this.field_21247 = bl3;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(dynamicOps.createString("state_provider"), this.field_21237.serialize(dynamicOps))
			.put(dynamicOps.createString("block_placer"), this.field_21238.serialize(dynamicOps))
			.put(
				dynamicOps.createString("whitelist"),
				dynamicOps.createList(this.field_21239.stream().map(block -> BlockState.serialize(dynamicOps, block.getDefaultState()).getValue()))
			)
			.put(
				dynamicOps.createString("blacklist"),
				dynamicOps.createList(this.field_21240.stream().map(blockState -> BlockState.serialize(dynamicOps, blockState).getValue()))
			)
			.put(dynamicOps.createString("tries"), dynamicOps.createInt(this.field_21241))
			.put(dynamicOps.createString("xspread"), dynamicOps.createInt(this.field_21242))
			.put(dynamicOps.createString("yspread"), dynamicOps.createInt(this.field_21243))
			.put(dynamicOps.createString("zspread"), dynamicOps.createInt(this.field_21244))
			.put(dynamicOps.createString("can_replace"), dynamicOps.createBoolean(this.field_21245))
			.put(dynamicOps.createString("project"), dynamicOps.createBoolean(this.field_21246))
			.put(dynamicOps.createString("need_water"), dynamicOps.createBoolean(this.field_21247));
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(builder.build()));
	}

	public static <T> class_4638 method_23413(Dynamic<T> dynamic) {
		class_4652<?> lv = Registry.BLOCK_STATE_PROVIDER_TYPE
			.get(new Identifier((String)dynamic.get("state_provider").get("type").asString().orElseThrow(RuntimeException::new)));
		class_4630<?> lv2 = Registry.BLOCK_PLACER_TYPE
			.get(new Identifier((String)dynamic.get("block_placer").get("type").asString().orElseThrow(RuntimeException::new)));
		return new class_4638(
			lv.method_23456(dynamic.get("state_provider").orElseEmptyMap()),
			lv2.method_23404(dynamic.get("block_placer").orElseEmptyMap()),
			(Set<Block>)dynamic.get("whitelist").asList(BlockState::deserialize).stream().map(BlockState::getBlock).collect(Collectors.toSet()),
			Sets.<BlockState>newHashSet(dynamic.get("blacklist").asList(BlockState::deserialize)),
			dynamic.get("tries").asInt(128),
			dynamic.get("xspread").asInt(7),
			dynamic.get("yspread").asInt(3),
			dynamic.get("zspread").asInt(7),
			dynamic.get("can_replace").asBoolean(false),
			dynamic.get("project").asBoolean(true),
			dynamic.get("need_water").asBoolean(false)
		);
	}

	public static class class_4639 {
		private final class_4651 field_21248;
		private final class_4629 field_21249;
		private Set<Block> field_21250 = ImmutableSet.of();
		private Set<BlockState> field_21251 = ImmutableSet.of();
		private int field_21252 = 64;
		private int field_21253 = 7;
		private int field_21254 = 3;
		private int field_21255 = 7;
		private boolean field_21256;
		private boolean field_21257 = true;
		private boolean field_21258 = false;

		public class_4639(class_4651 arg, class_4629 arg2) {
			this.field_21248 = arg;
			this.field_21249 = arg2;
		}

		public class_4638.class_4639 method_23418(Set<Block> set) {
			this.field_21250 = set;
			return this;
		}

		public class_4638.class_4639 method_23421(Set<BlockState> set) {
			this.field_21251 = set;
			return this;
		}

		public class_4638.class_4639 method_23417(int i) {
			this.field_21252 = i;
			return this;
		}

		public class_4638.class_4639 method_23420(int i) {
			this.field_21253 = i;
			return this;
		}

		public class_4638.class_4639 method_23423(int i) {
			this.field_21254 = i;
			return this;
		}

		public class_4638.class_4639 method_23425(int i) {
			this.field_21255 = i;
			return this;
		}

		public class_4638.class_4639 method_23416() {
			this.field_21256 = true;
			return this;
		}

		public class_4638.class_4639 method_23419() {
			this.field_21257 = false;
			return this;
		}

		public class_4638.class_4639 method_23422() {
			this.field_21258 = true;
			return this;
		}

		public class_4638 method_23424() {
			return new class_4638(
				this.field_21248,
				this.field_21249,
				this.field_21250,
				this.field_21251,
				this.field_21252,
				this.field_21253,
				this.field_21254,
				this.field_21255,
				this.field_21256,
				this.field_21257,
				this.field_21258
			);
		}
	}
}
