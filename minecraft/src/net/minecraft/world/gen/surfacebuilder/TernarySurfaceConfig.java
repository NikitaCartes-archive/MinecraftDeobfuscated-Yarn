package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;

public class TernarySurfaceConfig implements SurfaceConfig {
	public static final Codec<TernarySurfaceConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockState.field_24734.fieldOf("top_material").forGetter(ternarySurfaceConfig -> ternarySurfaceConfig.topMaterial),
					BlockState.field_24734.fieldOf("under_material").forGetter(ternarySurfaceConfig -> ternarySurfaceConfig.underMaterial),
					BlockState.field_24734.fieldOf("underwater_material").forGetter(ternarySurfaceConfig -> ternarySurfaceConfig.underwaterMaterial)
				)
				.apply(instance, TernarySurfaceConfig::new)
	);
	private final BlockState topMaterial;
	private final BlockState underMaterial;
	private final BlockState underwaterMaterial;

	public TernarySurfaceConfig(BlockState topMaterial, BlockState underMaterial, BlockState underwaterMaterial) {
		this.topMaterial = topMaterial;
		this.underMaterial = underMaterial;
		this.underwaterMaterial = underwaterMaterial;
	}

	@Override
	public BlockState getTopMaterial() {
		return this.topMaterial;
	}

	@Override
	public BlockState getUnderMaterial() {
		return this.underMaterial;
	}

	public BlockState getUnderwaterMaterial() {
		return this.underwaterMaterial;
	}
}
