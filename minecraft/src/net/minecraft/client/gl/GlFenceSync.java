package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GlFenceSync implements AutoCloseable {
	private long handle = GlStateManager._glFenceSync(37143, 0);

	public void close() {
		if (this.handle != 0L) {
			GlStateManager._glDeleteSync(this.handle);
			this.handle = 0L;
		}
	}

	public boolean wait(long timeoutNanos) {
		if (this.handle == 0L) {
			return true;
		} else {
			int i = GlStateManager._glClientWaitSync(this.handle, 0, timeoutNanos);
			if (i == 37147) {
				return false;
			} else if (i == 37149) {
				throw new IllegalStateException("Failed to complete gpu fence");
			} else {
				return true;
			}
		}
	}
}
