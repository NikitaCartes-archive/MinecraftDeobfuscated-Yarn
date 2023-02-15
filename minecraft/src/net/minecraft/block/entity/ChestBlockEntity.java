package net.minecraft.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ChestBlockEntity extends LootableContainerBlockEntity implements LidOpenable {
	private static final int VIEWER_COUNT_UPDATE_EVENT_TYPE = 1;
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
	private final ViewerCountManager stateManager = new ViewerCountManager() {
		@Override
		protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
			ChestBlockEntity.playSound(world, pos, state, SoundEvents.BLOCK_CHEST_OPEN);
		}

		@Override
		protected void onContainerClose(World world, BlockPos pos, BlockState state) {
			ChestBlockEntity.playSound(world, pos, state, SoundEvents.BLOCK_CHEST_CLOSE);
		}

		@Override
		protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
			ChestBlockEntity.this.onViewerCountUpdate(world, pos, state, oldViewerCount, newViewerCount);
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
		return Text.translatable("container.chest");
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		if (!this.deserializeLootTable(nbt)) {
			Inventories.readNbt(nbt, this.inventory);
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		if (!this.serializeLootTable(nbt)) {
			Inventories.writeNbt(nbt, this.inventory);
		}
	}

	public static void clientTick(World world, BlockPos pos, BlockState state, ChestBlockEntity blockEntity) {
		blockEntity.lidAnimator.step();
	}

	static void playSound(World world, BlockPos pos, BlockState state, SoundEvent soundEvent) {
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
		if (!this.removed && !player.isSpectator()) {
			this.stateManager.openContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
		}
	}

	@Override
	public void onClose(PlayerEntity player) {
		if (!this.removed && !player.isSpectator()) {
			this.stateManager.closeContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
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
		if (!this.removed) {
			this.stateManager.updateViewerCount(this.getWorld(), this.getPos(), this.getCachedState());
		}
	}

	protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
		Block block = state.getBlock();
		world.addSyncedBlockEvent(pos, block, 1, newViewerCount);
	}
}
