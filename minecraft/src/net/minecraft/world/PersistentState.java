package net.minecraft.world;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PersistentState {
	private static final Logger LOGGER = LogManager.getLogger();
	private final String key;
	private boolean dirty;

	public PersistentState(String string) {
		this.key = string;
	}

	public abstract void method_77(CompoundTag compoundTag);

	public abstract CompoundTag method_75(CompoundTag compoundTag);

	public void markDirty() {
		this.setDirty(true);
	}

	public void setDirty(boolean bl) {
		this.dirty = bl;
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public String getId() {
		return this.key;
	}

	public void method_17919(File file) {
		if (this.isDirty()) {
			CompoundTag compoundTag = new CompoundTag();
			compoundTag.method_10566("data", this.method_75(new CompoundTag()));
			compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());

			try {
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				Throwable var4 = null;

				try {
					NbtIo.writeCompressed(compoundTag, fileOutputStream);
				} catch (Throwable var14) {
					var4 = var14;
					throw var14;
				} finally {
					if (fileOutputStream != null) {
						if (var4 != null) {
							try {
								fileOutputStream.close();
							} catch (Throwable var13) {
								var4.addSuppressed(var13);
							}
						} else {
							fileOutputStream.close();
						}
					}
				}
			} catch (IOException var16) {
				LOGGER.error("Could not save data {}", this, var16);
			}

			this.setDirty(false);
		}
	}
}
