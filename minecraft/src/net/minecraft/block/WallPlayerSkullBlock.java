package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WallPlayerSkullBlock extends WallSkullBlock {
	public static final MapCodec<WallPlayerSkullBlock> CODEC = createCodec(WallPlayerSkullBlock::new);

	@Override
	public MapCodec<WallPlayerSkullBlock> getCodec() {
		return CODEC;
	}

	protected WallPlayerSkullBlock(AbstractBlock.Settings settings) {
		super(SkullBlock.Type.PLAYER, settings);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		PlayerSkullBlock.resolveSkullOwner(world, pos, itemStack);
	}
}
