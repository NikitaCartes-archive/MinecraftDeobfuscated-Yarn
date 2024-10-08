package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.tick.ScheduledTickView;

public class BeehiveBlock extends BlockWithEntity {
	public static final MapCodec<BeehiveBlock> CODEC = createCodec(BeehiveBlock::new);
	public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
	public static final IntProperty HONEY_LEVEL = Properties.HONEY_LEVEL;
	public static final int FULL_HONEY_LEVEL = 5;
	private static final int DROPPED_HONEYCOMB_COUNT = 3;

	@Override
	public MapCodec<BeehiveBlock> getCodec() {
		return CODEC;
	}

	public BeehiveBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(HONEY_LEVEL, Integer.valueOf(0)).with(FACING, Direction.NORTH));
	}

	@Override
	protected boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return (Integer)state.get(HONEY_LEVEL);
	}

	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
		super.afterBreak(world, player, pos, state, blockEntity, tool);
		if (!world.isClient && blockEntity instanceof BeehiveBlockEntity beehiveBlockEntity) {
			if (!EnchantmentHelper.hasAnyEnchantmentsIn(tool, EnchantmentTags.PREVENTS_BEE_SPAWNS_WHEN_MINING)) {
				beehiveBlockEntity.angerBees(player, state, BeehiveBlockEntity.BeeState.EMERGENCY);
				world.updateComparators(pos, this);
				this.angerNearbyBees(world, pos);
			}

			Criteria.BEE_NEST_DESTROYED.trigger((ServerPlayerEntity)player, state, tool, beehiveBlockEntity.getBeeCount());
		}
	}

	@Override
	protected void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
		super.onExploded(state, world, pos, explosion, stackMerger);
		this.angerNearbyBees(world, pos);
	}

	private void angerNearbyBees(World world, BlockPos pos) {
		Box box = new Box(pos).expand(8.0, 6.0, 8.0);
		List<BeeEntity> list = world.getNonSpectatingEntities(BeeEntity.class, box);
		if (!list.isEmpty()) {
			List<PlayerEntity> list2 = world.getNonSpectatingEntities(PlayerEntity.class, box);
			if (list2.isEmpty()) {
				return;
			}

			for (BeeEntity beeEntity : list) {
				if (beeEntity.getTarget() == null) {
					PlayerEntity playerEntity = Util.getRandom(list2, world.random);
					beeEntity.setTarget(playerEntity);
				}
			}
		}
	}

	public static void dropHoneycomb(World world, BlockPos pos) {
		dropStack(world, pos, new ItemStack(Items.HONEYCOMB, 3));
	}

	@Override
	protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		int i = (Integer)state.get(HONEY_LEVEL);
		boolean bl = false;
		if (i >= 5) {
			Item item = stack.getItem();
			if (stack.isOf(Items.SHEARS)) {
				world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
				dropHoneycomb(world, pos);
				stack.damage(1, player, LivingEntity.getSlotForHand(hand));
				bl = true;
				world.emitGameEvent(player, GameEvent.SHEAR, pos);
			} else if (stack.isOf(Items.GLASS_BOTTLE)) {
				stack.decrement(1);
				world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
				if (stack.isEmpty()) {
					player.setStackInHand(hand, new ItemStack(Items.HONEY_BOTTLE));
				} else if (!player.getInventory().insertStack(new ItemStack(Items.HONEY_BOTTLE))) {
					player.dropItem(new ItemStack(Items.HONEY_BOTTLE), false);
				}

				bl = true;
				world.emitGameEvent(player, GameEvent.FLUID_PICKUP, pos);
			}

			if (!world.isClient() && bl) {
				player.incrementStat(Stats.USED.getOrCreateStat(item));
			}
		}

		if (bl) {
			if (!CampfireBlock.isLitCampfireInRange(world, pos)) {
				if (this.hasBees(world, pos)) {
					this.angerNearbyBees(world, pos);
				}

				this.takeHoney(world, state, pos, player, BeehiveBlockEntity.BeeState.EMERGENCY);
			} else {
				this.takeHoney(world, state, pos);
			}

			return ActionResult.SUCCESS;
		} else {
			return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
		}
	}

	private boolean hasBees(World world, BlockPos pos) {
		return world.getBlockEntity(pos) instanceof BeehiveBlockEntity beehiveBlockEntity ? !beehiveBlockEntity.hasNoBees() : false;
	}

	public void takeHoney(World world, BlockState state, BlockPos pos, @Nullable PlayerEntity player, BeehiveBlockEntity.BeeState beeState) {
		this.takeHoney(world, state, pos);
		if (world.getBlockEntity(pos) instanceof BeehiveBlockEntity beehiveBlockEntity) {
			beehiveBlockEntity.angerBees(player, state, beeState);
		}
	}

	public void takeHoney(World world, BlockState state, BlockPos pos) {
		world.setBlockState(pos, state.with(HONEY_LEVEL, Integer.valueOf(0)), Block.NOTIFY_ALL);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if ((Integer)state.get(HONEY_LEVEL) >= 5) {
			for (int i = 0; i < random.nextInt(1) + 1; i++) {
				this.spawnHoneyParticles(world, pos, state);
			}
		}
	}

	private void spawnHoneyParticles(World world, BlockPos pos, BlockState state) {
		if (state.getFluidState().isEmpty() && !(world.random.nextFloat() < 0.3F)) {
			VoxelShape voxelShape = state.getCollisionShape(world, pos);
			double d = voxelShape.getMax(Direction.Axis.Y);
			if (d >= 1.0 && !state.isIn(BlockTags.IMPERMEABLE)) {
				double e = voxelShape.getMin(Direction.Axis.Y);
				if (e > 0.0) {
					this.addHoneyParticle(world, pos, voxelShape, (double)pos.getY() + e - 0.05);
				} else {
					BlockPos blockPos = pos.down();
					BlockState blockState = world.getBlockState(blockPos);
					VoxelShape voxelShape2 = blockState.getCollisionShape(world, blockPos);
					double f = voxelShape2.getMax(Direction.Axis.Y);
					if ((f < 1.0 || !blockState.isFullCube(world, blockPos)) && blockState.getFluidState().isEmpty()) {
						this.addHoneyParticle(world, pos, voxelShape, (double)pos.getY() - 0.05);
					}
				}
			}
		}
	}

	private void addHoneyParticle(World world, BlockPos pos, VoxelShape shape, double height) {
		this.addHoneyParticle(
			world,
			(double)pos.getX() + shape.getMin(Direction.Axis.X),
			(double)pos.getX() + shape.getMax(Direction.Axis.X),
			(double)pos.getZ() + shape.getMin(Direction.Axis.Z),
			(double)pos.getZ() + shape.getMax(Direction.Axis.Z),
			height
		);
	}

	private void addHoneyParticle(World world, double minX, double maxX, double minZ, double maxZ, double height) {
		world.addParticle(
			ParticleTypes.DRIPPING_HONEY,
			MathHelper.lerp(world.random.nextDouble(), minX, maxX),
			height,
			MathHelper.lerp(world.random.nextDouble(), minZ, maxZ),
			0.0,
			0.0,
			0.0
		);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(HONEY_LEVEL, FACING);
	}

	@Override
	protected BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new BeehiveBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient ? null : validateTicker(type, BlockEntityType.BEEHIVE, BeehiveBlockEntity::serverTick);
	}

	@Override
	public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (world instanceof ServerWorld serverWorld
			&& player.isCreative()
			&& serverWorld.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)
			&& world.getBlockEntity(pos) instanceof BeehiveBlockEntity beehiveBlockEntity) {
			int i = (Integer)state.get(HONEY_LEVEL);
			boolean bl = !beehiveBlockEntity.hasNoBees();
			if (bl || i > 0) {
				ItemStack itemStack = new ItemStack(this);
				itemStack.applyComponentsFrom(beehiveBlockEntity.createComponentMap());
				itemStack.set(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT.with(HONEY_LEVEL, i));
				ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), itemStack);
				itemEntity.setToDefaultPickupDelay();
				world.spawnEntity(itemEntity);
			}
		}

		return super.onBreak(world, pos, state, player);
	}

	@Override
	protected List<ItemStack> getDroppedStacks(BlockState state, LootWorldContext.Builder builder) {
		Entity entity = builder.getOptional(LootContextParameters.THIS_ENTITY);
		if (entity instanceof TntEntity
			|| entity instanceof CreeperEntity
			|| entity instanceof WitherSkullEntity
			|| entity instanceof WitherEntity
			|| entity instanceof TntMinecartEntity) {
			BlockEntity blockEntity = builder.getOptional(LootContextParameters.BLOCK_ENTITY);
			if (blockEntity instanceof BeehiveBlockEntity beehiveBlockEntity) {
				beehiveBlockEntity.angerBees(null, state, BeehiveBlockEntity.BeeState.EMERGENCY);
			}
		}

		return super.getDroppedStacks(state, builder);
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state,
		WorldView world,
		ScheduledTickView tickView,
		BlockPos pos,
		Direction direction,
		BlockPos neighborPos,
		BlockState neighborState,
		Random random
	) {
		if (world.getBlockState(neighborPos).getBlock() instanceof FireBlock && world.getBlockEntity(pos) instanceof BeehiveBlockEntity beehiveBlockEntity) {
			beehiveBlockEntity.angerBees(null, state, BeehiveBlockEntity.BeeState.EMERGENCY);
		}

		return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
		super.appendTooltip(stack, context, tooltip, options);
		BlockStateComponent blockStateComponent = stack.getOrDefault(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT);
		int i = (Integer)Objects.requireNonNullElse((Integer)blockStateComponent.getValue(HONEY_LEVEL), 0);
		int j = stack.getOrDefault(DataComponentTypes.BEES, List.of()).size();
		tooltip.add(Text.translatable("container.beehive.bees", j, 3).formatted(Formatting.GRAY));
		tooltip.add(Text.translatable("container.beehive.honey", i, 5).formatted(Formatting.GRAY));
	}
}
