package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class CarverDebugConfig {
	public static final CarverDebugConfig DEFAULT = new CarverDebugConfig(false, Blocks.ACACIA_BUTTON.getDefaultState());
	public static final Codec<CarverDebugConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.BOOL.optionalFieldOf("debug_mode", Boolean.valueOf(false)).forGetter(CarverDebugConfig::isDebugMode),
					BlockState.CODEC.optionalFieldOf("air_state", DEFAULT.getDebugState()).forGetter(CarverDebugConfig::getDebugState)
				)
				.apply(instance, CarverDebugConfig::new)
	);
	private boolean debugMode;
	private final BlockState debugState;

	public static CarverDebugConfig create(boolean debugMode, BlockState debugState) {
		return new CarverDebugConfig(debugMode, debugState);
	}

	private CarverDebugConfig(boolean debugMode, BlockState debugState) {
		this.debugMode = debugMode;
		this.debugState = debugState;
	}

	public boolean isDebugMode() {
		return this.debugMode;
	}

	public BlockState getDebugState() {
		return this.debugState;
	}
}
