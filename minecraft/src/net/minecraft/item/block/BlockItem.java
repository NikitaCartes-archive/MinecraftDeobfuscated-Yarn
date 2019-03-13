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
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.VerticalEntityPosition;
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
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.text.TextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockItem extends Item {
	@Deprecated
	private final Block field_7901;

	public BlockItem(Block block, Item.Settings settings) {
		super(settings);
		this.field_7901 = block;
	}

	@Override
	public ActionResult method_7884(ItemUsageContext itemUsageContext) {
		return this.method_7712(new ItemPlacementContext(itemUsageContext));
	}

	public ActionResult method_7712(ItemPlacementContext itemPlacementContext) {
		if (!itemPlacementContext.canPlace()) {
			return ActionResult.field_5814;
		} else {
			ItemPlacementContext itemPlacementContext2 = this.method_16356(itemPlacementContext);
			if (itemPlacementContext2 == null) {
				return ActionResult.field_5814;
			} else {
				BlockState blockState = this.method_7707(itemPlacementContext2);
				if (blockState == null) {
					return ActionResult.field_5814;
				} else if (!this.method_7708(itemPlacementContext2, blockState)) {
					return ActionResult.field_5814;
				} else {
					BlockPos blockPos = itemPlacementContext2.method_8037();
					World world = itemPlacementContext2.method_8045();
					PlayerEntity playerEntity = itemPlacementContext2.getPlayer();
					ItemStack itemStack = itemPlacementContext2.getItemStack();
					BlockState blockState2 = world.method_8320(blockPos);
					Block block = blockState2.getBlock();
					if (block == blockState.getBlock()) {
						blockState2 = this.method_18084(blockPos, world, itemStack, blockState2);
						this.method_7710(blockPos, world, playerEntity, itemStack, blockState2);
						block.method_9567(world, blockPos, blockState2, playerEntity, itemStack);
						if (playerEntity instanceof ServerPlayerEntity) {
							Criterions.PLACED_BLOCK.method_9087((ServerPlayerEntity)playerEntity, blockPos, itemStack);
						}
					}

					BlockSoundGroup blockSoundGroup = blockState2.getSoundGroup();
					world.method_8396(
						playerEntity,
						blockPos,
						this.method_19260(blockState2),
						SoundCategory.field_15245,
						(blockSoundGroup.getVolume() + 1.0F) / 2.0F,
						blockSoundGroup.getPitch() * 0.8F
					);
					itemStack.subtractAmount(1);
					return ActionResult.field_5812;
				}
			}
		}
	}

	protected SoundEvent method_19260(BlockState blockState) {
		return blockState.getSoundGroup().method_10598();
	}

	@Nullable
	public ItemPlacementContext method_16356(ItemPlacementContext itemPlacementContext) {
		return itemPlacementContext;
	}

	protected boolean method_7710(BlockPos blockPos, World world, @Nullable PlayerEntity playerEntity, ItemStack itemStack, BlockState blockState) {
		return method_7714(world, playerEntity, blockPos, itemStack);
	}

	@Nullable
	protected BlockState method_7707(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = this.method_7711().method_9605(itemPlacementContext);
		return blockState != null && this.method_7709(itemPlacementContext, blockState) ? blockState : null;
	}

	private BlockState method_18084(BlockPos blockPos, World world, ItemStack itemStack, BlockState blockState) {
		BlockState blockState2 = blockState;
		CompoundTag compoundTag = itemStack.method_7969();
		if (compoundTag != null) {
			CompoundTag compoundTag2 = compoundTag.getCompound("BlockStateTag");
			StateFactory<Block, BlockState> stateFactory = blockState.getBlock().method_9595();

			for (String string : compoundTag2.getKeys()) {
				Property<?> property = stateFactory.method_11663(string);
				if (property != null) {
					String string2 = compoundTag2.method_10580(string).asString();
					blockState2 = method_18083(blockState2, property, string2);
				}
			}
		}

		if (blockState2 != blockState) {
			world.method_8652(blockPos, blockState2, 2);
		}

		return blockState2;
	}

	private static <T extends Comparable<T>> BlockState method_18083(BlockState blockState, Property<T> property, String string) {
		return (BlockState)property.getValue(string).map(comparable -> blockState.method_11657(property, comparable)).orElse(blockState);
	}

	protected boolean method_7709(ItemPlacementContext itemPlacementContext, BlockState blockState) {
		PlayerEntity playerEntity = itemPlacementContext.getPlayer();
		VerticalEntityPosition verticalEntityPosition = playerEntity == null ? VerticalEntityPosition.minValue() : VerticalEntityPosition.fromEntity(playerEntity);
		return blockState.method_11591(itemPlacementContext.method_8045(), itemPlacementContext.method_8037())
			&& itemPlacementContext.method_8045().method_8628(blockState, itemPlacementContext.method_8037(), verticalEntityPosition);
	}

	protected boolean method_7708(ItemPlacementContext itemPlacementContext, BlockState blockState) {
		return itemPlacementContext.method_8045().method_8652(itemPlacementContext.method_8037(), blockState, 11);
	}

	public static boolean method_7714(World world, @Nullable PlayerEntity playerEntity, BlockPos blockPos, ItemStack itemStack) {
		MinecraftServer minecraftServer = world.getServer();
		if (minecraftServer == null) {
			return false;
		} else {
			CompoundTag compoundTag = itemStack.method_7941("BlockEntityTag");
			if (compoundTag != null) {
				BlockEntity blockEntity = world.method_8321(blockPos);
				if (blockEntity != null) {
					if (!world.isClient && blockEntity.shouldNotCopyTagFromItem() && (playerEntity == null || !playerEntity.isCreativeLevelTwoOp())) {
						return false;
					}

					CompoundTag compoundTag2 = blockEntity.method_11007(new CompoundTag());
					CompoundTag compoundTag3 = compoundTag2.method_10553();
					compoundTag2.copyFrom(compoundTag);
					compoundTag2.putInt("x", blockPos.getX());
					compoundTag2.putInt("y", blockPos.getY());
					compoundTag2.putInt("z", blockPos.getZ());
					if (!compoundTag2.equals(compoundTag3)) {
						blockEntity.method_11014(compoundTag2);
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
		return this.method_7711().getTranslationKey();
	}

	@Override
	public void method_7850(ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		if (this.isInItemGroup(itemGroup)) {
			this.method_7711().method_9578(itemGroup, defaultedList);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipContext tooltipContext) {
		super.method_7851(itemStack, world, list, tooltipContext);
		this.method_7711().buildTooltip(itemStack, world, list, tooltipContext);
	}

	public Block method_7711() {
		return this.field_7901;
	}

	public void method_7713(Map<Block, Item> map, Item item) {
		map.put(this.method_7711(), item);
	}
}
