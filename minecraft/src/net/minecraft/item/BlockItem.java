package net.minecraft.item;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockItem extends Item {
	@Deprecated
	private final Block block;

	public BlockItem(Block block, Item.Settings settings) {
		super(settings);
		this.block = block;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		ActionResult actionResult = this.place(new ItemPlacementContext(itemUsageContext));
		return actionResult != ActionResult.SUCCESS && this.isFood()
			? this.use(itemUsageContext.world, itemUsageContext.player, itemUsageContext.hand).getResult()
			: actionResult;
	}

	public ActionResult place(ItemPlacementContext itemPlacementContext) {
		if (!itemPlacementContext.canPlace()) {
			return ActionResult.FAIL;
		} else {
			ItemPlacementContext itemPlacementContext2 = this.getPlacementContext(itemPlacementContext);
			if (itemPlacementContext2 == null) {
				return ActionResult.FAIL;
			} else {
				BlockState blockState = this.getPlacementState(itemPlacementContext2);
				if (blockState == null) {
					return ActionResult.FAIL;
				} else if (!this.place(itemPlacementContext2, blockState)) {
					return ActionResult.FAIL;
				} else {
					BlockPos blockPos = itemPlacementContext2.getBlockPos();
					World world = itemPlacementContext2.getWorld();
					PlayerEntity playerEntity = itemPlacementContext2.getPlayer();
					ItemStack itemStack = itemPlacementContext2.getStack();
					BlockState blockState2 = world.getBlockState(blockPos);
					Block block = blockState2.getBlock();
					if (block == blockState.getBlock()) {
						blockState2 = this.placeFromTag(blockPos, world, itemStack, blockState2);
						this.postPlacement(blockPos, world, playerEntity, itemStack, blockState2);
						block.onPlaced(world, blockPos, blockState2, playerEntity, itemStack);
						if (playerEntity instanceof ServerPlayerEntity) {
							Criterions.PLACED_BLOCK.handle((ServerPlayerEntity)playerEntity, blockPos, itemStack);
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
					itemStack.decrement(1);
					return ActionResult.SUCCESS;
				}
			}
		}
	}

	protected SoundEvent getPlaceSound(BlockState blockState) {
		return blockState.getSoundGroup().getPlaceSound();
	}

	@Nullable
	public ItemPlacementContext getPlacementContext(ItemPlacementContext itemPlacementContext) {
		return itemPlacementContext;
	}

	protected boolean postPlacement(BlockPos blockPos, World world, @Nullable PlayerEntity playerEntity, ItemStack itemStack, BlockState blockState) {
		return writeTagToBlockEntity(world, playerEntity, blockPos, itemStack);
	}

	@Nullable
	protected BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = this.getBlock().getPlacementState(itemPlacementContext);
		return blockState != null && this.canPlace(itemPlacementContext, blockState) ? blockState : null;
	}

	private BlockState placeFromTag(BlockPos blockPos, World world, ItemStack itemStack, BlockState blockState) {
		BlockState blockState2 = blockState;
		CompoundTag compoundTag = itemStack.getTag();
		if (compoundTag != null) {
			CompoundTag compoundTag2 = compoundTag.getCompound("BlockStateTag");
			StateManager<Block, BlockState> stateManager = blockState.getBlock().getStateFactory();

			for (String string : compoundTag2.getKeys()) {
				Property<?> property = stateManager.getProperty(string);
				if (property != null) {
					String string2 = compoundTag2.get(string).asString();
					blockState2 = with(blockState2, property, string2);
				}
			}
		}

		if (blockState2 != blockState) {
			world.setBlockState(blockPos, blockState2, 2);
		}

		return blockState2;
	}

	private static <T extends Comparable<T>> BlockState with(BlockState blockState, Property<T> property, String string) {
		return (BlockState)property.parse(string).map(comparable -> blockState.with(property, comparable)).orElse(blockState);
	}

	protected boolean canPlace(ItemPlacementContext itemPlacementContext, BlockState blockState) {
		PlayerEntity playerEntity = itemPlacementContext.getPlayer();
		EntityContext entityContext = playerEntity == null ? EntityContext.absent() : EntityContext.of(playerEntity);
		return (!this.checkStatePlacement() || blockState.canPlaceAt(itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos()))
			&& itemPlacementContext.getWorld().canPlace(blockState, itemPlacementContext.getBlockPos(), entityContext);
	}

	protected boolean checkStatePlacement() {
		return true;
	}

	protected boolean place(ItemPlacementContext itemPlacementContext, BlockState blockState) {
		return itemPlacementContext.getWorld().setBlockState(itemPlacementContext.getBlockPos(), blockState, 11);
	}

	public static boolean writeTagToBlockEntity(World world, @Nullable PlayerEntity playerEntity, BlockPos blockPos, ItemStack itemStack) {
		MinecraftServer minecraftServer = world.getServer();
		if (minecraftServer == null) {
			return false;
		} else {
			CompoundTag compoundTag = itemStack.getSubTag("BlockEntityTag");
			if (compoundTag != null) {
				BlockEntity blockEntity = world.getBlockEntity(blockPos);
				if (blockEntity != null) {
					if (!world.isClient && blockEntity.shouldNotCopyTagFromItem() && (playerEntity == null || !playerEntity.isCreativeLevelTwoOp())) {
						return false;
					}

					CompoundTag compoundTag2 = blockEntity.toTag(new CompoundTag());
					CompoundTag compoundTag3 = compoundTag2.method_10553();
					compoundTag2.copyFrom(compoundTag);
					compoundTag2.putInt("x", blockPos.getX());
					compoundTag2.putInt("y", blockPos.getY());
					compoundTag2.putInt("z", blockPos.getZ());
					if (!compoundTag2.equals(compoundTag3)) {
						blockEntity.fromTag(compoundTag2);
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
	public void appendStacks(ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		if (this.isIn(itemGroup)) {
			this.getBlock().addStacksForDisplay(itemGroup, defaultedList);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Text> list, TooltipContext tooltipContext) {
		super.appendTooltip(itemStack, world, list, tooltipContext);
		this.getBlock().buildTooltip(itemStack, world, list, tooltipContext);
	}

	public Block getBlock() {
		return this.block;
	}

	public void appendBlocks(Map<Block, Item> map, Item item) {
		map.put(this.getBlock(), item);
	}
}
