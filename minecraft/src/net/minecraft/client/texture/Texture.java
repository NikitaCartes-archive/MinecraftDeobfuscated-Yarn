package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface Texture {
	void pushFilter(boolean bl, boolean bl2);

	void popFilter();

	void load(ResourceManager resourceManager) throws IOException;

	int getGlId();

	default void bindTexture() {
		GlStateManager.bindTexture(this.getGlId());
	}

	default void method_18169(TextureManager textureManager, ResourceManager resourceManager, Identifier identifier, Executor executor) {
		textureManager.registerTexture(identifier, this);
	}
}
