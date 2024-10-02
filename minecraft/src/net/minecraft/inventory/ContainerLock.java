package net.minecraft.inventory;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryWrapper;

public record ContainerLock(ItemPredicate predicate) {
	/**
	 * An empty container lock that can always be opened.
	 */
	public static final ContainerLock EMPTY = new ContainerLock(ItemPredicate.Builder.create().build());
	public static final Codec<ContainerLock> CODEC = ItemPredicate.CODEC.xmap(ContainerLock::new, ContainerLock::predicate);
	public static final String LOCK_KEY = "lock";

	/**
	 * Returns true if this lock can be opened with the key item stack.
	 * <p>
	 * An item stack is a valid key if the stack name matches the key string of this lock,
	 * or if the key string is empty.
	 */
	public boolean canOpen(ItemStack stack) {
		return this.predicate.test(stack);
	}

	/**
	 * Inserts the key string of this lock into the {@code Lock} key of the NBT compound.
	 */
	public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		if (this != EMPTY) {
			DataResult<NbtElement> dataResult = CODEC.encode(this, registries.getOps(NbtOps.INSTANCE), new NbtCompound());
			dataResult.result().ifPresent(lock -> nbt.put("lock", lock));
		}
	}

	/**
	 * Creates a new {@code ContainerLock} from the {@code Lock} key of the NBT compound.
	 * <p>
	 * If the {@code Lock} key is not present, returns an empty lock.
	 */
	public static ContainerLock fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		if (nbt.contains("lock", NbtElement.COMPOUND_TYPE)) {
			DataResult<Pair<ContainerLock, NbtElement>> dataResult = CODEC.decode(registries.getOps(NbtOps.INSTANCE), nbt.get("lock"));
			if (dataResult.isSuccess()) {
				return dataResult.getOrThrow().getFirst();
			}
		}

		return EMPTY;
	}
}
