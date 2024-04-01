package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PedestalBlock extends Block {
	private static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape TOP_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
	private static final VoxelShape MAIN_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
	private static final VoxelShape SHAPE = VoxelShapes.union(BOTTOM_SHAPE, MAIN_SHAPE, TOP_SHAPE);
	public static final MapCodec<PedestalBlock> CODEC = createCodec(PedestalBlock::new);

	@Override
	public MapCodec<PedestalBlock> getCodec() {
		return CODEC;
	}

	public PedestalBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
		return SHAPE;
	}

	@Override
	protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (stack.isOf(Items.POISONOUS_POTATO)
			&& world.getBlockState(pos.up()).isReplaceable()
			&& world instanceof ServerWorld serverWorld
			&& world.getDimension().natural()) {
			world.setBlockState(pos.up(), Blocks.POTATO_PORTAL.getDefaultState(), Block.NOTIFY_ALL);
			stack.decrementUnlessCreative(1, player);
			serverWorld.spawnParticles(
				ParticleTypes.ELECTRIC_SPARK, (double)pos.getX() + 0.5, (double)pos.getY() + 1.5, (double)pos.getZ() + 0.5, 100, 0.5, 0.5, 0.5, 0.2
			);
			serverWorld.playSound(null, pos, SoundEvents.ENTITY_PLAGUEWHALE_AMBIENT, SoundCategory.BLOCKS, 1.0F, 1.0F);
			if (!player.method_58934("portal_opened")) {
				player.method_58932("portal_opened");
			}

			return ItemActionResult.SUCCESS;
		}

		return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
	}
}
