package net.minecraft.block.entity;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.jukebox.JukeboxManager;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class JukeboxBlockEntity extends BlockEntity implements Clearable, SingleStackInventory.SingleStackBlockEntityInventory {
	public static final String RECORD_ITEM_NBT_KEY = "RecordItem";
	public static final String TICKS_SINCE_SONG_STARTED_NBT_KEY = "ticks_since_song_started";
	private ItemStack recordStack = ItemStack.EMPTY;
	private final JukeboxManager manager = new JukeboxManager(this::onManagerChange, this.getPos());

	public JukeboxBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.JUKEBOX, pos, state);
	}

	public JukeboxManager getManager() {
		return this.manager;
	}

	public void onManagerChange() {
		this.world.updateNeighborsAlways(this.getPos(), this.getCachedState().getBlock());
		this.markDirty();
	}

	private void onRecordStackChanged(boolean hasRecord) {
		if (this.world != null && this.world.getBlockState(this.getPos()) == this.getCachedState()) {
			this.world.setBlockState(this.getPos(), this.getCachedState().with(JukeboxBlock.HAS_RECORD, Boolean.valueOf(hasRecord)), Block.NOTIFY_LISTENERS);
			this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of(this.getCachedState()));
		}
	}

	public void dropRecord() {
		if (this.world != null && !this.world.isClient) {
			BlockPos blockPos = this.getPos();
			ItemStack itemStack = this.getStack();
			if (!itemStack.isEmpty()) {
				this.emptyStack();
				Vec3d vec3d = Vec3d.add(blockPos, 0.5, 1.01, 0.5).addRandom(this.world.random, 0.7F);
				ItemStack itemStack2 = itemStack.copy();
				ItemEntity itemEntity = new ItemEntity(this.world, vec3d.getX(), vec3d.getY(), vec3d.getZ(), itemStack2);
				itemEntity.setToDefaultPickupDelay();
				this.world.spawnEntity(itemEntity);
			}
		}
	}

	public static void tick(World world, BlockPos pos, BlockState state, JukeboxBlockEntity blockEntity) {
		blockEntity.manager.tick(world, state);
	}

	public int getComparatorOutput() {
		return (Integer)JukeboxSong.getSongEntryFromStack(this.world.getRegistryManager(), this.recordStack)
			.map(RegistryEntry::value)
			.map(JukeboxSong::comparatorOutput)
			.orElse(0);
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		if (nbt.contains("RecordItem", NbtElement.COMPOUND_TYPE)) {
			this.recordStack = (ItemStack)ItemStack.fromNbt(registryLookup, nbt.getCompound("RecordItem")).orElse(ItemStack.EMPTY);
		} else {
			this.recordStack = ItemStack.EMPTY;
		}

		if (nbt.contains("ticks_since_song_started", NbtElement.LONG_TYPE)) {
			JukeboxSong.getSongEntryFromStack(registryLookup, this.recordStack).ifPresent(song -> this.manager.setValues(song, nbt.getLong("ticks_since_song_started")));
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
		if (!this.getStack().isEmpty()) {
			nbt.put("RecordItem", this.getStack().encode(registryLookup));
		}

		if (this.manager.getSong() != null) {
			nbt.putLong("ticks_since_song_started", this.manager.getTicksSinceSongStarted());
		}
	}

	@Override
	public ItemStack getStack() {
		return this.recordStack;
	}

	@Override
	public ItemStack decreaseStack(int count) {
		ItemStack itemStack = this.recordStack;
		this.setStack(ItemStack.EMPTY);
		return itemStack;
	}

	@Override
	public void setStack(ItemStack stack) {
		this.recordStack = stack;
		boolean bl = !this.recordStack.isEmpty();
		Optional<RegistryEntry<JukeboxSong>> optional = JukeboxSong.getSongEntryFromStack(this.world.getRegistryManager(), this.recordStack);
		this.onRecordStackChanged(bl);
		if (bl && optional.isPresent()) {
			this.manager.startPlaying(this.world, (RegistryEntry<JukeboxSong>)optional.get());
		} else {
			this.manager.stopPlaying(this.world, this.getCachedState());
		}
	}

	@Override
	public int getMaxCountPerStack() {
		return 1;
	}

	@Override
	public BlockEntity asBlockEntity() {
		return this;
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return stack.contains(DataComponentTypes.JUKEBOX_PLAYABLE) && this.getStack(slot).isEmpty();
	}

	@Override
	public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
		return hopperInventory.containsAny(ItemStack::isEmpty);
	}

	@VisibleForTesting
	public void setDisc(ItemStack stack) {
		this.recordStack = stack;
		JukeboxSong.getSongEntryFromStack(this.world.getRegistryManager(), stack).ifPresent(song -> this.manager.setValues(song, 0L));
		this.world.updateNeighborsAlways(this.getPos(), this.getCachedState().getBlock());
		this.markDirty();
	}

	@VisibleForTesting
	public void reloadDisc() {
		JukeboxSong.getSongEntryFromStack(this.world.getRegistryManager(), this.getStack()).ifPresent(song -> this.manager.startPlaying(this.world, song));
	}
}
