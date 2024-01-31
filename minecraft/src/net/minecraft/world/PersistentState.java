package net.minecraft.world;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.registry.RegistryWrapper;
import org.slf4j.Logger;

public abstract class PersistentState {
	private static final Logger LOGGER = LogUtils.getLogger();
	private boolean dirty;

	public abstract NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup);

	public void markDirty() {
		this.setDirty(true);
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public void save(File file, RegistryWrapper.WrapperLookup registryLookup) {
		if (this.isDirty()) {
			NbtCompound nbtCompound = new NbtCompound();
			nbtCompound.put("data", this.writeNbt(new NbtCompound(), registryLookup));
			NbtHelper.putDataVersion(nbtCompound);

			try {
				NbtIo.writeCompressed(nbtCompound, file.toPath());
			} catch (IOException var5) {
				LOGGER.error("Could not save data {}", this, var5);
			}

			this.setDirty(false);
		}
	}

	public static record Type<T extends PersistentState>(
		Supplier<T> constructor, BiFunction<NbtCompound, RegistryWrapper.WrapperLookup, T> deserializer, DataFixTypes type
	) {
	}
}
