package net.minecraft.world;

import java.io.File;
import java.io.IOException;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PersistentState {
	private static final Logger LOGGER = LogManager.getLogger();
	private boolean dirty;

	public abstract CompoundTag toNbt(CompoundTag tag);

	public void markDirty() {
		this.setDirty(true);
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public void save(File file) {
		if (this.isDirty()) {
			CompoundTag compoundTag = new CompoundTag();
			compoundTag.put("data", this.toNbt(new CompoundTag()));
			compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());

			try {
				NbtIo.writeCompressed(compoundTag, file);
			} catch (IOException var4) {
				LOGGER.error("Could not save data {}", this, var4);
			}

			this.setDirty(false);
		}
	}
}
