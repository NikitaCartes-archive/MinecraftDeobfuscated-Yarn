package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class class_5105 implements FeatureConfig {
	public final BlockStateProvider field_23581;
	public final class_5105.class_5106 field_23582;
	public final float field_23583;
	public final float field_23584;

	public class_5105(BlockStateProvider blockStateProvider, class_5105.class_5106 arg, float f, float g) {
		this.field_23581 = blockStateProvider;
		this.field_23582 = arg;
		this.field_23583 = f;
		this.field_23584 = g;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("metric"),
					ops.createString(this.field_23582.field_23588),
					ops.createString("material"),
					this.field_23581.serialize(ops),
					ops.createString("radiusMin"),
					ops.createFloat(this.field_23583),
					ops.createString("radiusMax"),
					ops.createFloat(this.field_23584)
				)
			)
		);
	}

	public static <T> class_5105 method_26632(Dynamic<T> dynamic) {
		class_5105.class_5106 lv = (class_5105.class_5106)dynamic.get("metric").asString().map(class_5105.class_5106::method_26638).get();
		BlockStateProvider blockStateProvider = (BlockStateProvider)dynamic.get("material")
			.map(
				dynamicx -> {
					Identifier identifier = (Identifier)dynamicx.get("type").asString().map(Identifier::new).get();
					BlockStateProviderType<?> blockStateProviderType = (BlockStateProviderType<?>)Registry.BLOCK_STATE_PROVIDER_TYPE
						.getOrEmpty(identifier)
						.orElseThrow(() -> new IllegalStateException(identifier.toString()));
					return blockStateProviderType.deserialize(dynamicx);
				}
			)
			.orElseThrow(IllegalStateException::new);
		float f = dynamic.get("radiusMin").asFloat(0.0F);
		float g = dynamic.get("radiusMax").asFloat(0.0F);
		return new class_5105(blockStateProvider, lv, f, g);
	}

	public static class_5105 method_26633(Random random) {
		class_5105.class_5106 lv = Util.method_26721(random, class_5105.class_5106.values());
		BlockStateProvider blockStateProvider = BlockStateProvider.method_26659(random);
		float f = 1.0F + random.nextFloat() * 5.0F;
		float g = Math.min(f + random.nextFloat() * 10.0F, 15.0F);
		return new class_5105(blockStateProvider, lv, f, g);
	}

	public static enum class_5106 {
		field_23585("euclidian") {
			@Override
			public float method_26637(BlockPos blockPos, BlockPos blockPos2) {
				return MathHelper.sqrt(blockPos.getSquaredDistance(blockPos2));
			}
		},
		field_23586("taxicab") {
			@Override
			public float method_26637(BlockPos blockPos, BlockPos blockPos2) {
				return (float)blockPos.getManhattanDistance(blockPos2);
			}
		},
		field_23587("chessboard") {
			@Override
			public float method_26637(BlockPos blockPos, BlockPos blockPos2) {
				float f = (float)Math.abs(blockPos2.getX() - blockPos.getX());
				float g = (float)Math.abs(blockPos2.getY() - blockPos.getY());
				float h = (float)Math.abs(blockPos2.getZ() - blockPos.getZ());
				return Math.max(f, Math.max(g, h));
			}
		};

		private final String field_23588;

		private class_5106(String string2) {
			this.field_23588 = string2;
		}

		public abstract float method_26637(BlockPos blockPos, BlockPos blockPos2);

		public static class_5105.class_5106 method_26638(String string) {
			return (class_5105.class_5106)Stream.of(values())
				.filter(arg -> arg.field_23588.equals(string))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException(string));
		}
	}
}
