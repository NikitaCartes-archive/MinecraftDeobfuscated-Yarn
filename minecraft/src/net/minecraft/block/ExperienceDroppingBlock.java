package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;

public class ExperienceDroppingBlock extends Block {
	public static final MapCodec<ExperienceDroppingBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(IntProvider.createValidatingCodec(0, 10).fieldOf("experience").forGetter(block -> block.experienceDropped), createSettingsCodec())
				.apply(instance, ExperienceDroppingBlock::new)
	);
	private final IntProvider experienceDropped;

	@Override
	public MapCodec<? extends ExperienceDroppingBlock> getCodec() {
		return CODEC;
	}

	public ExperienceDroppingBlock(IntProvider experienceDropped, AbstractBlock.Settings settings) {
		super(settings);
		this.experienceDropped = experienceDropped;
	}

	@Override
	protected void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
		super.onStacksDropped(state, world, pos, tool, dropExperience);
		if (dropExperience) {
			this.dropExperienceWhenMined(world, pos, tool, this.experienceDropped);
		}
	}
}
