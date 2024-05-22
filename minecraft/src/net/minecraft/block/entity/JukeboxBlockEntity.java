package net.minecraft.block.entity;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import net.minecraft.class_9793;
import net.minecraft.class_9794;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.JukeboxBlock;
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
	public static final String field_52064 = "RecordItem";
	public static final String field_52065 = "ticks_since_song_started";
	private ItemStack recordStack = ItemStack.EMPTY;
	private final class_9794 field_52066 = new class_9794(this::method_60785, this.getPos());

	public JukeboxBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.JUKEBOX, pos, state);
	}

	public class_9794 method_60784() {
		return this.field_52066;
	}

	public void method_60785() {
		this.world.updateNeighborsAlways(this.getPos(), this.getCachedState().getBlock());
		this.markDirty();
	}

	private void method_60782(boolean bl) {
		if (this.world != null && this.world.getBlockState(this.getPos()) == this.getCachedState()) {
			this.world.setBlockState(this.getPos(), this.getCachedState().with(JukeboxBlock.HAS_RECORD, Boolean.valueOf(bl)), Block.NOTIFY_LISTENERS);
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
		blockEntity.field_52066.method_60760(world, state);
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
			class_9793.method_60753(registryLookup, this.recordStack)
				.ifPresent(registryEntry -> this.field_52066.method_60758(registryEntry, nbt.getLong("ticks_since_song_started")));
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
		if (!this.getStack().isEmpty()) {
			nbt.put("RecordItem", this.getStack().encode(registryLookup));
		}

		if (this.field_52066.method_60759() != null) {
			nbt.putLong("ticks_since_song_started", this.field_52066.method_60761());
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
		Optional<RegistryEntry<class_9793>> optional = class_9793.method_60753(this.world.getRegistryManager(), this.recordStack);
		this.method_60782(bl);
		if (bl && optional.isPresent()) {
			this.field_52066.method_60757(this.world, (RegistryEntry<class_9793>)optional.get());
		} else {
			this.field_52066.method_60755(this.world, this.getCachedState());
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
		class_9793.method_60753(this.world.getRegistryManager(), stack).ifPresent(registryEntry -> this.field_52066.method_60758(registryEntry, 0L));
		this.world.updateNeighborsAlways(this.getPos(), this.getCachedState().getBlock());
		this.markDirty();
	}

	@VisibleForTesting
	public void method_60786() {
		class_9793.method_60753(this.world.getRegistryManager(), this.getStack())
			.ifPresent(registryEntry -> this.field_52066.method_60757(this.world, registryEntry));
	}
}
