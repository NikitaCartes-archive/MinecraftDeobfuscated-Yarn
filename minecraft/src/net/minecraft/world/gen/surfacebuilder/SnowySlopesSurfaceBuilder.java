package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;

public class SnowySlopesSurfaceBuilder extends GroveSurfaceBuilder {
	private final NewMountainSurfaceBuilder.class_6474 field_34260 = new NewMountainSurfaceBuilder.class_6474(
		Blocks.STONE.getDefaultState(), true, false, false, true
	);

	public SnowySlopesSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

	@Nullable
	@Override
	protected NewMountainSurfaceBuilder.class_6474 method_37775() {
		return this.field_34260;
	}
}
