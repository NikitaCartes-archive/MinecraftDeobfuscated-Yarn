package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class CarverDebugConfig {
	public static final CarverDebugConfig DEFAULT = new CarverDebugConfig(
		false, Blocks.ACACIA_BUTTON.getDefaultState(), Blocks.CANDLE.getDefaultState(), Blocks.ORANGE_STAINED_GLASS.getDefaultState(), Blocks.GLASS.getDefaultState()
	);
	public static final Codec<CarverDebugConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.BOOL.optionalFieldOf("debug_mode", Boolean.valueOf(false)).forGetter(CarverDebugConfig::isDebugMode),
					BlockState.CODEC.optionalFieldOf("air_state", DEFAULT.getDebugState()).forGetter(CarverDebugConfig::getDebugState),
					BlockState.CODEC.optionalFieldOf("water_state", DEFAULT.getDebugState()).forGetter(CarverDebugConfig::method_36414),
					BlockState.CODEC.optionalFieldOf("lava_state", DEFAULT.getDebugState()).forGetter(CarverDebugConfig::method_36415),
					BlockState.CODEC.optionalFieldOf("barrier_state", DEFAULT.getDebugState()).forGetter(CarverDebugConfig::method_36416)
				)
				.apply(instance, CarverDebugConfig::new)
	);
	private boolean debugMode;
	private final BlockState debugState;
	private final BlockState field_33611;
	private final BlockState field_33612;
	private final BlockState field_33613;

	public static CarverDebugConfig method_36413(boolean bl, BlockState blockState, BlockState blockState2, BlockState blockState3, BlockState blockState4) {
		return new CarverDebugConfig(bl, blockState, blockState2, blockState3, blockState4);
	}

	public static CarverDebugConfig method_36412(BlockState blockState, BlockState blockState2, BlockState blockState3, BlockState blockState4) {
		return new CarverDebugConfig(false, blockState, blockState2, blockState3, blockState4);
	}

	public static CarverDebugConfig create(boolean debugMode, BlockState debugState) {
		return new CarverDebugConfig(debugMode, debugState, DEFAULT.method_36414(), DEFAULT.method_36415(), DEFAULT.method_36416());
	}

	private CarverDebugConfig(boolean debugMode, BlockState debugState, BlockState blockState, BlockState blockState2, BlockState blockState3) {
		this.debugMode = debugMode;
		this.debugState = debugState;
		this.field_33611 = blockState;
		this.field_33612 = blockState2;
		this.field_33613 = blockState3;
	}

	public boolean isDebugMode() {
		return this.debugMode;
	}

	public BlockState getDebugState() {
		return this.debugState;
	}

	public BlockState method_36414() {
		return this.field_33611;
	}

	public BlockState method_36415() {
		return this.field_33612;
	}

	public BlockState method_36416() {
		return this.field_33613;
	}
}
