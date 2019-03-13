package net.minecraft.block;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.explosion.Explosion;

public class BedBlock extends HorizontalFacingBlock implements BlockEntityProvider {
	public static final EnumProperty<BedPart> field_9967 = Properties.field_12483;
	public static final BooleanProperty field_9968 = Properties.field_12528;
	protected static final VoxelShape field_16788 = Block.method_9541(0.0, 3.0, 0.0, 16.0, 9.0, 16.0);
	protected static final VoxelShape field_16782 = Block.method_9541(0.0, 0.0, 0.0, 3.0, 3.0, 3.0);
	protected static final VoxelShape field_16784 = Block.method_9541(0.0, 0.0, 13.0, 3.0, 3.0, 16.0);
	protected static final VoxelShape field_16786 = Block.method_9541(13.0, 0.0, 0.0, 16.0, 3.0, 3.0);
	protected static final VoxelShape field_16789 = Block.method_9541(13.0, 0.0, 13.0, 16.0, 3.0, 16.0);
	protected static final VoxelShape field_16787 = VoxelShapes.method_17786(field_16788, field_16782, field_16786);
	protected static final VoxelShape field_16785 = VoxelShapes.method_17786(field_16788, field_16784, field_16789);
	protected static final VoxelShape field_16783 = VoxelShapes.method_17786(field_16788, field_16782, field_16784);
	protected static final VoxelShape field_16790 = VoxelShapes.method_17786(field_16788, field_16786, field_16789);
	private final DyeColor color;

	public BedBlock(DyeColor dyeColor, Block.Settings settings) {
		super(settings);
		this.color = dyeColor;
		this.method_9590(this.field_10647.method_11664().method_11657(field_9967, BedPart.field_12557).method_11657(field_9968, Boolean.valueOf(false)));
	}

	public static boolean method_19284(BlockState blockState) {
		return blockState.method_11602(BlockTags.field_16443) && blockState.method_11654(field_9967) == BedPart.field_12557;
	}

	@Override
	public MaterialColor method_9602(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.method_11654(field_9967) == BedPart.field_12557 ? this.color.method_7794() : MaterialColor.WEB;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static Direction method_18476(BlockView blockView, BlockPos blockPos) {
		BlockState blockState = blockView.method_8320(blockPos);
		return blockState.getBlock() instanceof BedBlock ? blockState.method_11654(field_11177) : null;
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (world.isClient) {
			return true;
		} else {
			if (blockState.method_11654(field_9967) != BedPart.field_12560) {
				blockPos = blockPos.method_10093(blockState.method_11654(field_11177));
				blockState = world.method_8320(blockPos);
				if (blockState.getBlock() != this) {
					return true;
				}
			}

			if (world.field_9247.canPlayersSleep() && world.method_8310(blockPos) != Biomes.field_9461) {
				if ((Boolean)blockState.method_11654(field_9968)) {
					if (!this.method_18477(world, blockPos)) {
						playerEntity.method_7353(new TranslatableTextComponent("block.minecraft.bed.occupied"), true);
						return true;
					}

					blockState = blockState.method_11657(field_9968, Boolean.valueOf(false));
					world.method_8652(blockPos, blockState, 4);
				}

				playerEntity.method_7269(blockPos).ifLeft(sleepResult -> {
					if (sleepResult != null) {
						playerEntity.method_7353(sleepResult.method_19206(), true);
					}
				});
				return true;
			} else {
				world.method_8650(blockPos);
				BlockPos blockPos2 = blockPos.method_10093(((Direction)blockState.method_11654(field_11177)).getOpposite());
				if (world.method_8320(blockPos2).getBlock() == this) {
					world.method_8650(blockPos2);
				}

				world.createExplosion(
					null,
					DamageSource.netherBed(),
					(double)blockPos.getX() + 0.5,
					(double)blockPos.getY() + 0.5,
					(double)blockPos.getZ() + 0.5,
					5.0F,
					true,
					Explosion.class_4179.field_18687
				);
				return true;
			}
		}
	}

	private boolean method_18477(World world, BlockPos blockPos) {
		return world.method_8390(
				LivingEntity.class,
				new BoundingBox(
					(double)blockPos.getX(),
					(double)blockPos.getY(),
					(double)blockPos.getZ(),
					(double)(blockPos.getX() + 1),
					(double)(blockPos.getY() + 2),
					(double)(blockPos.getZ() + 1)
				),
				LivingEntity::isSleeping
			)
			.isEmpty();
	}

	@Override
	public void method_9554(World world, BlockPos blockPos, Entity entity, float f) {
		super.method_9554(world, blockPos, entity, f * 0.5F);
	}

	@Override
	public void onEntityLand(BlockView blockView, Entity entity) {
		if (entity.isSneaking()) {
			super.onEntityLand(blockView, entity);
		} else {
			Vec3d vec3d = entity.method_18798();
			if (vec3d.y < 0.0) {
				double d = entity instanceof LivingEntity ? 1.0 : 0.8;
				entity.setVelocity(vec3d.x, -vec3d.y * 0.66F * d, vec3d.z);
			}
		}
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (direction == method_9488(blockState.method_11654(field_9967), blockState.method_11654(field_11177))) {
			return blockState2.getBlock() == this && blockState2.method_11654(field_9967) != blockState.method_11654(field_9967)
				? blockState.method_11657(field_9968, blockState2.method_11654(field_9968))
				: Blocks.field_10124.method_9564();
		} else {
			return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}

	private static Direction method_9488(BedPart bedPart, Direction direction) {
		return bedPart == BedPart.field_12557 ? direction : direction.getOpposite();
	}

	@Override
	public void method_9556(
		World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack
	) {
		super.method_9556(world, playerEntity, blockPos, Blocks.field_10124.method_9564(), blockEntity, itemStack);
	}

	@Override
	public void method_9576(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		BedPart bedPart = blockState.method_11654(field_9967);
		BlockPos blockPos2 = blockPos.method_10093(method_9488(bedPart, blockState.method_11654(field_11177)));
		BlockState blockState2 = world.method_8320(blockPos2);
		if (blockState2.getBlock() == this && blockState2.method_11654(field_9967) != bedPart) {
			world.method_8652(blockPos2, Blocks.field_10124.method_9564(), 35);
			world.method_8444(playerEntity, 2001, blockPos2, Block.method_9507(blockState2));
			if (!world.isClient && !playerEntity.isCreative()) {
				ItemStack itemStack = playerEntity.method_6047();
				method_9511(blockState, world, blockPos, null, playerEntity, itemStack);
				method_9511(blockState2, world, blockPos2, null, playerEntity, itemStack);
			}

			playerEntity.method_7259(Stats.field_15427.getOrCreateStat(this));
		}

		super.method_9576(world, blockPos, blockState, playerEntity);
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		Direction direction = itemPlacementContext.method_8042();
		BlockPos blockPos = itemPlacementContext.method_8037();
		BlockPos blockPos2 = blockPos.method_10093(direction);
		return itemPlacementContext.method_8045().method_8320(blockPos2).method_11587(itemPlacementContext)
			? this.method_9564().method_11657(field_11177, direction)
			: null;
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		Direction direction = blockState.method_11654(field_11177);
		Direction direction2 = blockState.method_11654(field_9967) == BedPart.field_12560 ? direction : direction.getOpposite();
		switch (direction2) {
			case NORTH:
				return field_16787;
			case SOUTH:
				return field_16785;
			case WEST:
				return field_16783;
			default:
				return field_16790;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_9589(BlockState blockState) {
		return true;
	}

	@Nullable
	public static BlockPos method_9484(BlockView blockView, BlockPos blockPos, int i) {
		Direction direction = blockView.method_8320(blockPos).method_11654(field_11177);
		int j = blockPos.getX();
		int k = blockPos.getY();
		int l = blockPos.getZ();

		for (int m = 0; m <= 1; m++) {
			int n = j - direction.getOffsetX() * m - 1;
			int o = l - direction.getOffsetZ() * m - 1;
			int p = n + 2;
			int q = o + 2;

			for (int r = n; r <= p; r++) {
				for (int s = o; s <= q; s++) {
					BlockPos blockPos2 = new BlockPos(r, k, s);
					if (method_9486(blockView, blockPos2)) {
						if (i <= 0) {
							return blockPos2;
						}

						i--;
					}
				}
			}
		}

		return null;
	}

	protected static boolean method_9486(BlockView blockView, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		return blockView.method_8320(blockPos2).method_11631(blockView, blockPos2)
			&& !blockView.method_8320(blockPos).method_11620().method_15799()
			&& !blockView.method_8320(blockPos.up()).method_11620().method_15799();
	}

	@Override
	public PistonBehavior method_9527(BlockState blockState) {
		return PistonBehavior.field_15971;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11456;
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11177, field_9967, field_9968);
	}

	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return new BedBlockEntity(this.color);
	}

	@Override
	public void method_9567(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
		super.method_9567(world, blockPos, blockState, livingEntity, itemStack);
		if (!world.isClient) {
			BlockPos blockPos2 = blockPos.method_10093(blockState.method_11654(field_11177));
			world.method_8652(blockPos2, blockState.method_11657(field_9967, BedPart.field_12560), 3);
			world.method_8408(blockPos, Blocks.field_10124);
			blockState.method_11635(world, blockPos, 3);
		}
	}

	@Environment(EnvType.CLIENT)
	public DyeColor getColor() {
		return this.color;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public long method_9535(BlockState blockState, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.method_10079(blockState.method_11654(field_11177), blockState.method_11654(field_9967) == BedPart.field_12560 ? 0 : 1);
		return MathHelper.hashCode(blockPos2.getX(), blockPos.getY(), blockPos2.getZ());
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
