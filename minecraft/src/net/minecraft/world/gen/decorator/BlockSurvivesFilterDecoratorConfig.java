package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.lang.runtime.ObjectMethods;
import net.minecraft.block.BlockState;

public final class BlockSurvivesFilterDecoratorConfig extends Record implements DecoratorConfig {
	private final BlockState state;
	public static final Codec<BlockSurvivesFilterDecoratorConfig> CODEC = BlockState.CODEC
		.fieldOf("state")
		.<BlockSurvivesFilterDecoratorConfig>xmap(BlockSurvivesFilterDecoratorConfig::new, BlockSurvivesFilterDecoratorConfig::state)
		.codec();

	public BlockSurvivesFilterDecoratorConfig(BlockState blockState) {
		this.state = blockState;
	}

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",BlockSurvivesFilterDecoratorConfig,"state",BlockSurvivesFilterDecoratorConfig::state>(this);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",BlockSurvivesFilterDecoratorConfig,"state",BlockSurvivesFilterDecoratorConfig::state>(this);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",BlockSurvivesFilterDecoratorConfig,"state",BlockSurvivesFilterDecoratorConfig::state>(this, object);
	}

	public BlockState state() {
		return this.state;
	}
}
