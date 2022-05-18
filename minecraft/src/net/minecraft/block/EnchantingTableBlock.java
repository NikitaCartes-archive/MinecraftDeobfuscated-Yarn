package net.minecraft.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Nameable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EnchantingTableBlock extends BlockWithEntity {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
	public static final List<BlockPos> BOOKSHELF_OFFSETS = BlockPos.stream(-2, 0, -2, 2, 1, 2)
		.filter(pos -> Math.abs(pos.getX()) == 2 || Math.abs(pos.getZ()) == 2)
		.map(BlockPos::toImmutable)
		.toList();

	protected EnchantingTableBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	public static boolean canAccessBookshelf(World world, BlockPos tablePos, BlockPos bookshelfOffset) {
		return world.getBlockState(tablePos.add(bookshelfOffset)).isOf(Blocks.BOOKSHELF)
			&& world.isAir(tablePos.add(bookshelfOffset.getX() / 2, bookshelfOffset.getY(), bookshelfOffset.getZ() / 2));
	}

	@Override
	public boolean hasSidedTransparency(BlockState state) {
		return true;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		super.randomDisplayTick(state, world, pos, random);

		for (BlockPos blockPos : BOOKSHELF_OFFSETS) {
			if (random.nextInt(16) == 0 && canAccessBookshelf(world, pos, blockPos)) {
				world.addParticle(
					ParticleTypes.ENCHANT,
					(double)pos.getX() + 0.5,
					(double)pos.getY() + 2.0,
					(double)pos.getZ() + 0.5,
					(double)((float)blockPos.getX() + random.nextFloat()) - 0.5,
					(double)((float)blockPos.getY() - random.nextFloat() - 1.0F),
					(double)((float)blockPos.getZ() + random.nextFloat()) - 0.5
				);
			}
		}
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new EnchantingTableBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient ? checkType(type, BlockEntityType.ENCHANTING_TABLE, EnchantingTableBlockEntity::tick) : null;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
			return ActionResult.CONSUME;
		}
	}

	@Nullable
	@Override
	public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof EnchantingTableBlockEntity) {
			Text text = ((Nameable)blockEntity).getDisplayName();
			return new SimpleNamedScreenHandlerFactory(
				(syncId, inventory, player) -> new EnchantmentScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos)), text
			);
		} else {
			return null;
		}
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof EnchantingTableBlockEntity) {
				((EnchantingTableBlockEntity)blockEntity).setCustomName(itemStack.getName());
			}
		}
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}
}
