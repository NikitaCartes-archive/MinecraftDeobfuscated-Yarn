package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;

public class EndGatewayFeatureConfig implements FeatureConfig {
	private final Optional<BlockPos> field_17735;
	private final boolean exitsAtSpawn;

	private EndGatewayFeatureConfig(Optional<BlockPos> optional, boolean bl) {
		this.field_17735 = optional;
		this.exitsAtSpawn = bl;
	}

	public static EndGatewayFeatureConfig method_18034(BlockPos blockPos, boolean bl) {
		return new EndGatewayFeatureConfig(Optional.of(blockPos), bl);
	}

	public static EndGatewayFeatureConfig method_18030() {
		return new EndGatewayFeatureConfig(Optional.empty(), false);
	}

	public Optional<BlockPos> method_18036() {
		return this.field_17735;
	}

	public boolean exitsAtSpawn() {
		return this.exitsAtSpawn;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			(T)this.field_17735
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
								dynamicOps.createBoolean(this.exitsAtSpawn)
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
