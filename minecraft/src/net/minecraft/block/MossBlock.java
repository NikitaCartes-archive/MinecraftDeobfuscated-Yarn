package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class MossBlock extends Block implements Fertilizable {
	public static final MapCodec<MossBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					RegistryKey.createCodec(RegistryKeys.CONFIGURED_FEATURE).fieldOf("feature").forGetter(block -> block.feature), createSettingsCodec()
				)
				.apply(instance, MossBlock::new)
	);
	private final RegistryKey<ConfiguredFeature<?, ?>> feature;

	@Override
	public MapCodec<MossBlock> getCodec() {
		return CODEC;
	}

	public MossBlock(RegistryKey<ConfiguredFeature<?, ?>> feature, AbstractBlock.Settings settings) {
		super(settings);
		this.feature = feature;
	}

	@Override
	public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
		return world.getBlockState(pos.up()).isAir();
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		world.getRegistryManager()
			.getOptional(RegistryKeys.CONFIGURED_FEATURE)
			.flatMap(registry -> registry.getOptional(this.feature))
			.ifPresent(entry -> ((ConfiguredFeature)entry.value()).generate(world, world.getChunkManager().getChunkGenerator(), random, pos.up()));
	}

	@Override
	public Fertilizable.FertilizableType getFertilizableType() {
		return Fertilizable.FertilizableType.NEIGHBOR_SPREADER;
	}
}
