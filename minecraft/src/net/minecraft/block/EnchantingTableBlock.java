package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.client.network.ClientDummyContainerProvider;
import net.minecraft.container.BlockContext;
import net.minecraft.container.EnchantingTableContainer;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Nameable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EnchantingTableBlock extends BlockWithEntity {
	protected static final VoxelShape field_10951 = Block.method_9541(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

	protected EnchantingTableBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean method_9526(BlockState blockState) {
		return true;
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return field_10951;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(BlockState blockState, World world, BlockPos blockPos, Random random) {
		super.method_9496(blockState, world, blockPos, random);

		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				if (i > -2 && i < 2 && j == -1) {
					j = 2;
				}

				if (random.nextInt(16) == 0) {
					for (int k = 0; k <= 1; k++) {
						BlockPos blockPos2 = blockPos.add(i, k, j);
						if (world.method_8320(blockPos2).getBlock() == Blocks.field_10504) {
							if (!world.isAir(blockPos.add(i / 2, 0, j / 2))) {
								break;
							}

							world.addParticle(
								ParticleTypes.field_11215,
								(double)blockPos.getX() + 0.5,
								(double)blockPos.getY() + 2.0,
								(double)blockPos.getZ() + 0.5,
								(double)((float)i + random.nextFloat()) - 0.5,
								(double)((float)k - random.nextFloat() - 1.0F),
								(double)((float)j + random.nextFloat()) - 0.5
							);
						}
					}
				}
			}
		}
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return new EnchantingTableBlockEntity();
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (world.isClient) {
			return true;
		} else {
			playerEntity.openContainer(blockState.createContainerProvider(world, blockPos));
			return true;
		}
	}

	@Nullable
	@Override
	public NameableContainerProvider method_17454(BlockState blockState, World world, BlockPos blockPos) {
		BlockEntity blockEntity = world.method_8321(blockPos);
		if (blockEntity instanceof EnchantingTableBlockEntity) {
			Text text = ((Nameable)blockEntity).getDisplayName();
			return new ClientDummyContainerProvider(
				(i, playerInventory, playerEntity) -> new EnchantingTableContainer(i, playerInventory, BlockContext.method_17392(world, blockPos)), text
			);
		} else {
			return null;
		}
	}

	@Override
	public void method_9567(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof EnchantingTableBlockEntity) {
				((EnchantingTableBlockEntity)blockEntity).setCustomName(itemStack.getName());
			}
		}
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
