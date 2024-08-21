package net.minecraft.world;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryWrapper;

public abstract class PersistentState {
	private boolean dirty;

	public abstract NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries);

	public void markDirty() {
		this.setDirty(true);
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.put("data", this.writeNbt(new NbtCompound(), registries));
		NbtHelper.putDataVersion(nbtCompound);
		this.setDirty(false);
		return nbtCompound;
	}

	public static record Type<T extends PersistentState>(
		Supplier<T> constructor, BiFunction<NbtCompound, RegistryWrapper.WrapperLookup, T> deserializer, DataFixTypes type
	) {
	}
}
