package net.minecraft.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = ChestAnimationProgress.class
	)})
public class ChestBlockEntity extends LootableContainerBlockEntity implements ChestAnimationProgress {
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
	private final ChestStateManager stateManager = new ChestStateManager() {
		@Override
		protected void onChestOpened(World world, BlockPos pos, BlockState state) {
			ChestBlockEntity.playSound(world, pos, state, SoundEvents.BLOCK_CHEST_OPEN);
		}

		@Override
		protected void onChestClosed(World world, BlockPos pos, BlockState state) {
			ChestBlockEntity.playSound(world, pos, state, SoundEvents.BLOCK_CHEST_CLOSE);
		}

		@Override
		protected void onInteracted(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
			ChestBlockEntity.this.onInvOpenOrClose(world, pos, state, oldViewerCount, newViewerCount);
		}

		@Override
		protected boolean isPlayerViewing(PlayerEntity player) {
			if (!(player.currentScreenHandler instanceof GenericContainerScreenHandler)) {
				return false;
			} else {
				Inventory inventory = ((GenericContainerScreenHandler)player.currentScreenHandler).getInventory();
				return inventory == ChestBlockEntity.this || inventory instanceof DoubleInventory && ((DoubleInventory)inventory).isPart(ChestBlockEntity.this);
			}
		}
	};
	private final ChestLidAnimator lidAnimator = new ChestLidAnimator();

	protected ChestBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
	}

	public ChestBlockEntity(BlockPos pos, BlockState state) {
		this(BlockEntityType.CHEST, pos, state);
	}

	@Override
	public int size() {
		return 27;
	}

	@Override
	protected Text getContainerName() {
		return new TranslatableText("container.chest");
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		if (!this.deserializeLootTable(tag)) {
			Inventories.fromTag(tag, this.inventory);
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		if (!this.serializeLootTable(tag)) {
			Inventories.toTag(tag, this.inventory);
		}

		return tag;
	}

	public static void clientTick(World world, BlockPos pos, BlockState state, ChestBlockEntity blockEntity) {
		blockEntity.lidAnimator.step();
	}

	private static void playSound(World world, BlockPos pos, BlockState state, SoundEvent soundEvent) {
		ChestType chestType = state.get(ChestBlock.CHEST_TYPE);
		if (chestType != ChestType.LEFT) {
			double d = (double)pos.getX() + 0.5;
			double e = (double)pos.getY() + 0.5;
			double f = (double)pos.getZ() + 0.5;
			if (chestType == ChestType.RIGHT) {
				Direction direction = ChestBlock.getFacing(state);
				d += (double)direction.getOffsetX() * 0.5;
				f += (double)direction.getOffsetZ() * 0.5;
			}

			world.playSound(null, d, e, f, soundEvent, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
		}
	}

	@Override
	public boolean onSyncedBlockEvent(int type, int data) {
		if (type == 1) {
			this.lidAnimator.setOpen(data > 0);
			return true;
		} else {
			return super.onSyncedBlockEvent(type, data);
		}
	}

	@Override
	public void onOpen(PlayerEntity player) {
		if (!player.isSpectator()) {
			this.stateManager.openChest(player, this.getWorld(), this.getPos(), this.getCachedState());
		}
	}

	@Override
	public void onClose(PlayerEntity player) {
		if (!player.isSpectator()) {
			this.stateManager.closeChest(player, this.getWorld(), this.getPos(), this.getCachedState());
		}
	}

	@Override
	protected DefaultedList<ItemStack> getInvStackList() {
		return this.inventory;
	}

	@Override
	protected void setInvStackList(DefaultedList<ItemStack> list) {
		this.inventory = list;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public float getAnimationProgress(float tickDelta) {
		return this.lidAnimator.getProgress(tickDelta);
	}

	public static int getPlayersLookingInChestCount(BlockView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.hasBlockEntity()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof ChestBlockEntity) {
				return ((ChestBlockEntity)blockEntity).stateManager.getViewerCount();
			}
		}

		return 0;
	}

	public static void copyInventory(ChestBlockEntity from, ChestBlockEntity to) {
		DefaultedList<ItemStack> defaultedList = from.getInvStackList();
		from.setInvStackList(to.getInvStackList());
		to.setInvStackList(defaultedList);
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
	}

	public void onScheduledTick() {
		this.stateManager.updateViewerCount(this.getWorld(), this.getPos(), this.getCachedState());
	}

	protected void onInvOpenOrClose(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
		Block block = state.getBlock();
		world.addSyncedBlockEvent(pos, block, 1, newViewerCount);
	}
}
