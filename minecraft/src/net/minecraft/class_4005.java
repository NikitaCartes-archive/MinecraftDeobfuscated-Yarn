package net.minecraft;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class class_4005 extends ResourceTexture {
	private CompletableFuture<ResourceTexture.class_4006> field_17894;

	public class_4005(ResourceManager resourceManager, Identifier identifier, Executor executor) {
		super(identifier);
		this.field_17894 = CompletableFuture.supplyAsync(() -> ResourceTexture.class_4006.method_18156(resourceManager, identifier), executor);
	}

	@Override
	protected ResourceTexture.class_4006 method_18153(ResourceManager resourceManager) {
		if (this.field_17894 != null) {
			ResourceTexture.class_4006 lv = (ResourceTexture.class_4006)this.field_17894.join();
			this.field_17894 = null;
			return lv;
		} else {
			return ResourceTexture.class_4006.method_18156(resourceManager, this.location);
		}
	}

	public CompletableFuture<Void> method_18148() {
		return this.field_17894 == null ? CompletableFuture.completedFuture(null) : this.field_17894.thenApply(arg -> null);
	}

	@Override
	public void method_18169(TextureManager textureManager, ResourceManager resourceManager, Identifier identifier, Executor executor) {
		this.field_17894 = CompletableFuture.supplyAsync(() -> ResourceTexture.class_4006.method_18156(resourceManager, this.location));
		this.field_17894.thenRunAsync(() -> textureManager.registerTexture(this.location, this), executor);
	}
}
