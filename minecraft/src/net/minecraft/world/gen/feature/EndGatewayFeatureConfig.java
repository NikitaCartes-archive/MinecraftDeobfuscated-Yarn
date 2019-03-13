package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;

public class EndGatewayFeatureConfig implements FeatureConfig {
	private final Optional<BlockPos> exitPos;
	private final boolean exact;

	private EndGatewayFeatureConfig(Optional<BlockPos> optional, boolean bl) {
		this.exitPos = optional;
		this.exact = bl;
	}

	public static EndGatewayFeatureConfig method_18034(BlockPos blockPos, boolean bl) {
		return new EndGatewayFeatureConfig(Optional.of(blockPos), bl);
	}

	public static EndGatewayFeatureConfig createConfig() {
		return new EndGatewayFeatureConfig(Optional.empty(), false);
	}

	public Optional<BlockPos> getExitPos() {
		return this.exitPos;
	}

	public boolean isExact() {
		return this.exact;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			(T)this.exitPos
				.map(
					blockPos -> dynamicOps.createMap(
							ImmutableMap.of(
								dynamicOps.createString("exit_x"),
								dynamicOps.createInt(blockPos.getX()),
								dynamicOps.createString("exit_y"),
								dynamicOps.createInt(blockPos.getY()),
								dynamicOps.createString("exit_z"),
								dynamicOps.createInt(blockPos.getZ()),
								dynamicOps.createString("exact"),
								dynamicOps.createBoolean(this.exact)
							)
						)
				)
				.orElse(dynamicOps.emptyMap())
		);
	}

	public static <T> EndGatewayFeatureConfig deserialize(Dynamic<T> dynamic) {
		Optional<BlockPos> optional = dynamic.get("exit_x")
			.asNumber()
			.flatMap(
				number -> dynamic.get("exit_y")
						.asNumber()
						.flatMap(number2 -> dynamic.get("exit_z").asNumber().map(number3 -> new BlockPos(number.intValue(), number2.intValue(), number3.intValue())))
			);
		boolean bl = dynamic.get("exact").asBoolean(false);
		return new EndGatewayFeatureConfig(optional, bl);
	}
}
