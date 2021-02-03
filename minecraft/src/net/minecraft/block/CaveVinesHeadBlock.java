package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CaveVinesHeadBlock extends AbstractPlantStemBlock implements Fertilizable, CaveVines {
	public CaveVinesHeadBlock(AbstractBlock.Settings settings) {
		super(settings, Direction.DOWN, SHAPE, false, 0.1);
		this.setDefaultState(this.stateManager.getDefaultState().with(AGE, Integer.valueOf(0)).with(BERRIES, Boolean.valueOf(false)));
	}

	@Override
	protected int getGrowthLength(Random random) {
		return 1;
	}

	@Override
	protected boolean chooseStemState(BlockState state) {
		return state.isAir();
	}

	@Override
	protected Block getPlant() {
		return Blocks.CAVE_VINES_BODY;
	}

	@Override
	protected BlockState copyState(BlockState from, BlockState to) {
		return to.with(BERRIES, from.get(BERRIES));
	}

	@Override
	protected BlockState age(BlockState state, Random random) {
		return super.age(state, random).with(BERRIES, Boolean.valueOf(random.nextFloat() < 0.11F));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(Items.GLOW_BERRIES);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return CaveVines.pickBerries(state, world, pos);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(BERRIES);
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		return !(Boolean)state.get(BERRIES);
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state.with(BERRIES, Boolean.valueOf(true)), 2);
	}
}
