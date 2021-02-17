package net.minecraft.item;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class BlockItem extends Item {
	@Deprecated
	private final Block block;

	public BlockItem(Block block, Item.Settings settings) {
		super(settings);
		this.block = block;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		ActionResult actionResult = this.place(new ItemPlacementContext(context));
		return !actionResult.isAccepted() && this.isFood() ? this.use(context.getWorld(), context.getPlayer(), context.getHand()).getResult() : actionResult;
	}

	public ActionResult place(ItemPlacementContext context) {
		if (!context.canPlace()) {
			return ActionResult.FAIL;
		} else {
			ItemPlacementContext itemPlacementContext = this.getPlacementContext(context);
			if (itemPlacementContext == null) {
				return ActionResult.FAIL;
			} else {
				BlockState blockState = this.getPlacementState(itemPlacementContext);
				if (blockState == null) {
					return ActionResult.FAIL;
				} else if (!this.place(itemPlacementContext, blockState)) {
					return ActionResult.FAIL;
				} else {
					BlockPos blockPos = itemPlacementContext.getBlockPos();
					World world = itemPlacementContext.getWorld();
					PlayerEntity playerEntity = itemPlacementContext.getPlayer();
					ItemStack itemStack = itemPlacementContext.getStack();
					BlockState blockState2 = world.getBlockState(blockPos);
					if (blockState2.isOf(blockState.getBlock())) {
						blockState2 = this.placeFromTag(blockPos, world, itemStack, blockState2);
						this.postPlacement(blockPos, world, playerEntity, itemStack, blockState2);
						blockState2.getBlock().onPlaced(world, blockPos, blockState2, playerEntity, itemStack);
						if (playerEntity instanceof ServerPlayerEntity) {
							Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos, itemStack);
						}
					}

					BlockSoundGroup blockSoundGroup = blockState2.getSoundGroup();
					world.playSound(
						playerEntity,
						blockPos,
						this.getPlaceSound(blockState2),
						SoundCategory.BLOCKS,
						(blockSoundGroup.getVolume() + 1.0F) / 2.0F,
						blockSoundGroup.getPitch() * 0.8F
					);
					world.emitGameEvent(playerEntity, GameEvent.BLOCK_PLACE, blockPos);
					if (playerEntity == null || !playerEntity.getAbilities().creativeMode) {
						itemStack.decrement(1);
					}

					return ActionResult.success(world.isClient);
				}
			}
		}
	}

	protected SoundEvent getPlaceSound(BlockState state) {
		return state.getSoundGroup().getPlaceSound();
	}

	@Nullable
	public ItemPlacementContext getPlacementContext(ItemPlacementContext context) {
		return context;
	}

	protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
		return writeTagToBlockEntity(world, player, pos, stack);
	}

	@Nullable
	protected BlockState getPlacementState(ItemPlacementContext context) {
		BlockState blockState = this.getBlock().getPlacementState(context);
		return blockState != null && this.canPlace(context, blockState) ? blockState : null;
	}

	private BlockState placeFromTag(BlockPos pos, World world, ItemStack stack, BlockState state) {
		BlockState blockState = state;
		CompoundTag compoundTag = stack.getTag();
		if (compoundTag != null) {
			CompoundTag compoundTag2 = compoundTag.getCompound("BlockStateTag");
			StateManager<Block, BlockState> stateManager = state.getBlock().getStateManager();

			for (String string : compoundTag2.getKeys()) {
				Property<?> property = stateManager.getProperty(string);
				if (property != null) {
					String string2 = compoundTag2.get(string).asString();
					blockState = with(blockState, property, string2);
				}
			}
		}

		if (blockState != state) {
			world.setBlockState(pos, blockState, 2);
		}

		return blockState;
	}

	private static <T extends Comparable<T>> BlockState with(BlockState state, Property<T> property, String name) {
		return (BlockState)property.parse(name).map(value -> state.with(property, value)).orElse(state);
	}

	protected boolean canPlace(ItemPlacementContext context, BlockState state) {
		PlayerEntity playerEntity = context.getPlayer();
		ShapeContext shapeContext = playerEntity == null ? ShapeContext.absent() : ShapeContext.of(playerEntity);
		return (!this.checkStatePlacement() || state.canPlaceAt(context.getWorld(), context.getBlockPos()))
			&& context.getWorld().canPlace(state, context.getBlockPos(), shapeContext);
	}

	protected boolean checkStatePlacement() {
		return true;
	}

	protected boolean place(ItemPlacementContext context, BlockState state) {
		return context.getWorld().setBlockState(context.getBlockPos(), state, 11);
	}

	public static boolean writeTagToBlockEntity(World world, @Nullable PlayerEntity player, BlockPos pos, ItemStack stack) {
		MinecraftServer minecraftServer = world.getServer();
		if (minecraftServer == null) {
			return false;
		} else {
			CompoundTag compoundTag = stack.getSubTag("BlockEntityTag");
			if (compoundTag != null) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity != null) {
					if (!world.isClient && blockEntity.copyItemDataRequiresOperator() && (player == null || !player.isCreativeLevelTwoOp())) {
						return false;
					}

					CompoundTag compoundTag2 = blockEntity.writeNbt(new CompoundTag());
					CompoundTag compoundTag3 = compoundTag2.copy();
					compoundTag2.copyFrom(compoundTag);
					compoundTag2.putInt("x", pos.getX());
					compoundTag2.putInt("y", pos.getY());
					compoundTag2.putInt("z", pos.getZ());
					if (!compoundTag2.equals(compoundTag3)) {
						blockEntity.readNbt(compoundTag2);
						blockEntity.markDirty();
						return true;
					}
				}
			}

			return false;
		}
	}

	@Override
	public String getTranslationKey() {
		return this.getBlock().getTranslationKey();
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
		if (this.isIn(group)) {
			this.getBlock().addStacksForDisplay(group, stacks);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		this.getBlock().appendTooltip(stack, world, tooltip, context);
	}

	public Block getBlock() {
		return this.block;
	}

	public void appendBlocks(Map<Block, Item> map, Item item) {
		map.put(this.getBlock(), item);
	}

	@Override
	public boolean hasStoredInventory() {
		return !(this.block instanceof ShulkerBoxBlock);
	}

	@Override
	public void onItemEntityDestroyed(ItemEntity entity) {
		if (this.block instanceof ShulkerBoxBlock) {
			CompoundTag compoundTag = entity.getStack().getTag();
			if (compoundTag != null) {
				ListTag listTag = compoundTag.getCompound("BlockEntityTag").getList("Items", 10);
				ItemUsage.spawnItemContents(entity, listTag.stream().map(CompoundTag.class::cast).map(ItemStack::fromNbt));
			}
		}
	}
}
