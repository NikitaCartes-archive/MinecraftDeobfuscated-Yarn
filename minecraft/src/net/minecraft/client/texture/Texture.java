package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;

@Environment(EnvType.CLIENT)
public interface Texture {
	void pushFilter(boolean bl, boolean bl2);

	void popFilter();

	void load(ResourceManager resourceManager) throws IOException;

	int getGlId();

	default void bindTexture() {
		GlStateManager.bindTexture(this.getGlId());
	}
}
