package net.minecraft.item.block;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipOptions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.TextComponent;
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
		return this.place(new ItemPlacementContext(itemUsageContext));
	}

	public ActionResult place(ItemPlacementContext itemPlacementContext) {
		if (!itemPlacementContext.canPlace()) {
			return ActionResult.FAILURE;
		} else {
			ItemPlacementContext itemPlacementContext2 = this.getPlacementContext(itemPlacementContext);
			if (itemPlacementContext2 == null) {
				return ActionResult.FAILURE;
			} else {
				BlockState blockState = this.getBlockState(itemPlacementContext2);
				if (blockState == null) {
					return ActionResult.FAILURE;
				} else if (!this.setBlockState(itemPlacementContext2, blockState)) {
					return ActionResult.FAILURE;
				} else {
					BlockPos blockPos = itemPlacementContext2.getPos();
					World world = itemPlacementContext2.getWorld();
					PlayerEntity playerEntity = itemPlacementContext2.getPlayer();
					ItemStack itemStack = itemPlacementContext2.getItemStack();
					BlockState blockState2 = world.getBlockState(blockPos);
					Block block = blockState2.getBlock();
					if (block == blockState.getBlock()) {
						this.afterBlockPlaced(blockPos, world, playerEntity, itemStack, blockState2);
						block.onPlaced(world, blockPos, blockState2, playerEntity, itemStack);
						if (playerEntity instanceof ServerPlayerEntity) {
							Criterions.PLACED_BLOCK.handle((ServerPlayerEntity)playerEntity, blockPos, itemStack);
						}
					}

					BlockSoundGroup blockSoundGroup = blockState2.getSoundGroup();
					world.playSound(
						playerEntity,
						blockPos,
						blockSoundGroup.getPlaceSound(),
						SoundCategory.field_15245,
						(blockSoundGroup.getVolume() + 1.0F) / 2.0F,
						blockSoundGroup.getPitch() * 0.8F
					);
					itemStack.subtractAmount(1);
					return ActionResult.SUCCESS;
				}
			}
		}
	}

	@Nullable
	public ItemPlacementContext getPlacementContext(ItemPlacementContext itemPlacementContext) {
		return itemPlacementContext;
	}

	protected boolean afterBlockPlaced(BlockPos blockPos, World world, @Nullable PlayerEntity playerEntity, ItemStack itemStack, BlockState blockState) {
		return deserializeBlockEntityTag(world, playerEntity, blockPos, itemStack);
	}

	@Nullable
	protected BlockState getBlockState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = this.getBlock().getPlacementState(itemPlacementContext);
		return blockState != null && this.canPlace(itemPlacementContext, blockState) ? blockState : null;
	}

	protected boolean canPlace(ItemPlacementContext itemPlacementContext, BlockState blockState) {
		return blockState.canPlaceAt(itemPlacementContext.getWorld(), itemPlacementContext.getPos())
			&& itemPlacementContext.getWorld().method_8628(blockState, itemPlacementContext.getPos());
	}

	protected boolean setBlockState(ItemPlacementContext itemPlacementContext, BlockState blockState) {
		return itemPlacementContext.getWorld().setBlockState(itemPlacementContext.getPos(), blockState, 11);
	}

	public static boolean deserializeBlockEntityTag(World world, @Nullable PlayerEntity playerEntity, BlockPos blockPos, ItemStack itemStack) {
		MinecraftServer minecraftServer = world.getServer();
		if (minecraftServer == null) {
			return false;
		} else {
			CompoundTag compoundTag = itemStack.getSubCompoundTag("BlockEntityTag");
			if (compoundTag != null) {
				BlockEntity blockEntity = world.getBlockEntity(blockPos);
				if (blockEntity != null) {
					if (!world.isClient && blockEntity.method_11011() && (playerEntity == null || !playerEntity.method_7338())) {
						return false;
					}

					CompoundTag compoundTag2 = blockEntity.toTag(new CompoundTag());
					CompoundTag compoundTag3 = compoundTag2.copy();
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
	public void addStacksForDisplay(ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		if (this.isInItemGroup(itemGroup)) {
			this.getBlock().addStacksForDisplay(itemGroup, defaultedList);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void buildTooltip(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipOptions tooltipOptions) {
		super.buildTooltip(itemStack, world, list, tooltipOptions);
		this.getBlock().addInformation(itemStack, world, list, tooltipOptions);
	}

	public Block getBlock() {
		return this.block;
	}

	public void registerBlockItemMap(Map<Block, Item> map, Item item) {
		map.put(this.getBlock(), item);
	}
}
