package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CampfireBlock extends BlockWithEntity implements Waterloggable {
	protected static final VoxelShape field_17351 = Block.method_9541(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);
	public static final BooleanProperty field_17352 = Properties.field_12548;
	public static final BooleanProperty field_17353 = Properties.field_17394;
	public static final BooleanProperty field_17354 = Properties.field_12508;
	public static final DirectionProperty field_17564 = Properties.field_12481;

	public CampfireBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_17352, Boolean.valueOf(true))
				.method_11657(field_17353, Boolean.valueOf(false))
				.method_11657(field_17354, Boolean.valueOf(false))
				.method_11657(field_17564, Direction.field_11043)
		);
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if ((Boolean)blockState.method_11654(field_17352)) {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof CampfireBlockEntity) {
				CampfireBlockEntity campfireBlockEntity = (CampfireBlockEntity)blockEntity;
				ItemStack itemStack = playerEntity.getStackInHand(hand);
				Optional<CampfireCookingRecipe> optional = campfireBlockEntity.getRecipeFor(itemStack);
				if (optional.isPresent()) {
					if (!world.isClient
						&& campfireBlockEntity.addItem(playerEntity.abilities.creativeMode ? itemStack.copy() : itemStack, ((CampfireCookingRecipe)optional.get()).getCookTime())
						)
					 {
						playerEntity.incrementStat(Stats.field_17486);
					}

					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void method_9548(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (!entity.isFireImmune()
			&& (Boolean)blockState.method_11654(field_17352)
			&& entity instanceof LivingEntity
			&& !EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
			entity.damage(DamageSource.IN_FIRE, 1.0F);
		}

		super.method_9548(blockState, world, blockPos, entity);
	}

	@Override
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof CampfireBlockEntity) {
				ItemScatterer.method_17349(world, blockPos, ((CampfireBlockEntity)blockEntity).getItemsBeingCooked());
			}

			super.method_9536(blockState, world, blockPos, blockState2, bl);
		}
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		IWorld iWorld = itemPlacementContext.method_8045();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		boolean bl = iWorld.method_8316(blockPos).getFluid() == Fluids.WATER;
		return this.method_9564()
			.method_11657(field_17354, Boolean.valueOf(bl))
			.method_11657(field_17353, Boolean.valueOf(this.method_17456(iWorld.method_8320(blockPos.down()))))
			.method_11657(field_17352, Boolean.valueOf(!bl))
			.method_11657(field_17564, itemPlacementContext.getPlayerFacing());
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if ((Boolean)blockState.method_11654(field_17354)) {
			iWorld.method_8405().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		return direction == Direction.field_11033
			? blockState.method_11657(field_17353, Boolean.valueOf(this.method_17456(blockState2)))
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	private boolean method_17456(BlockState blockState) {
		return blockState.getBlock() == Blocks.field_10359;
	}

	@Override
	public int method_9593(BlockState blockState) {
		return blockState.method_11654(field_17352) ? super.method_9593(blockState) : 0;
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return field_17351;
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9174;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.method_11654(field_17352)) {
			if (random.nextInt(10) == 0) {
				world.playSound(
					(double)((float)blockPos.getX() + 0.5F),
					(double)((float)blockPos.getY() + 0.5F),
					(double)((float)blockPos.getZ() + 0.5F),
					SoundEvents.field_17483,
					SoundCategory.field_15245,
					0.5F + random.nextFloat(),
					random.nextFloat() * 0.7F + 0.6F,
					false
				);
			}

			if (random.nextInt(5) == 0) {
				for (int i = 0; i < random.nextInt(1) + 1; i++) {
					world.addParticle(
						ParticleTypes.field_11239,
						(double)((float)blockPos.getX() + 0.5F),
						(double)((float)blockPos.getY() + 0.5F),
						(double)((float)blockPos.getZ() + 0.5F),
						(double)(random.nextFloat() / 2.0F),
						5.0E-5,
						(double)(random.nextFloat() / 2.0F)
					);
				}
			}
		}
	}

	@Override
	public boolean method_10311(IWorld iWorld, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
		if (!(Boolean)blockState.method_11654(Properties.field_12508) && fluidState.getFluid() == Fluids.WATER) {
			boolean bl = (Boolean)blockState.method_11654(field_17352);
			if (bl) {
				if (iWorld.isClient()) {
					for (int i = 0; i < 20; i++) {
						spawnSmokeParticle(iWorld.getWorld(), blockPos, (Boolean)blockState.method_11654(field_17353), true);
					}
				} else {
					iWorld.playSound(null, blockPos, SoundEvents.field_15222, SoundCategory.field_15245, 1.0F, 1.0F);
				}

				BlockEntity blockEntity = iWorld.method_8321(blockPos);
				if (blockEntity instanceof CampfireBlockEntity) {
					((CampfireBlockEntity)blockEntity).spawnItemsBeingCooked();
				}
			}

			iWorld.method_8652(blockPos, blockState.method_11657(field_17354, Boolean.valueOf(true)).method_11657(field_17352, Boolean.valueOf(false)), 3);
			iWorld.method_8405().schedule(blockPos, fluidState.getFluid(), fluidState.getFluid().getTickRate(iWorld));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void method_19286(World world, BlockState blockState, BlockHitResult blockHitResult, Entity entity) {
		if (!world.isClient && entity instanceof ProjectileEntity) {
			ProjectileEntity projectileEntity = (ProjectileEntity)entity;
			if (projectileEntity.isOnFire() && !(Boolean)blockState.method_11654(field_17352) && !(Boolean)blockState.method_11654(field_17354)) {
				BlockPos blockPos = blockHitResult.getBlockPos();
				world.method_8652(blockPos, blockState.method_11657(Properties.field_12548, Boolean.valueOf(true)), 11);
			}
		}
	}

	public static void spawnSmokeParticle(World world, BlockPos blockPos, boolean bl, boolean bl2) {
		Random random = world.getRandom();
		DefaultParticleType defaultParticleType = bl ? ParticleTypes.field_17431 : ParticleTypes.field_17430;
		world.addImportantParticle(
			defaultParticleType,
			true,
			(double)blockPos.getX() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1),
			(double)blockPos.getY() + random.nextDouble() + random.nextDouble(),
			(double)blockPos.getZ() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1),
			0.0,
			0.07,
			0.0
		);
		if (bl2) {
			world.addParticle(
				ParticleTypes.field_11251,
				(double)blockPos.getX() + 0.25 + random.nextDouble() / 2.0 * (double)(random.nextBoolean() ? 1 : -1),
				(double)blockPos.getY() + 0.4,
				(double)blockPos.getZ() + 0.25 + random.nextDouble() / 2.0 * (double)(random.nextBoolean() ? 1 : -1),
				0.0,
				0.005,
				0.0
			);
		}
	}

	@Override
	public FluidState method_9545(BlockState blockState) {
		return blockState.method_11654(field_17354) ? Fluids.WATER.method_15729(false) : super.method_9545(blockState);
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		return blockState.method_11657(field_17564, blockRotation.rotate(blockState.method_11654(field_17564)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.method_10345(blockState.method_11654(field_17564)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_17352, field_17353, field_17354, field_17564);
	}

	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return new CampfireBlockEntity();
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
