package net.minecraft.client.texture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class AsyncTexture extends ResourceTexture {
	private CompletableFuture<ResourceTexture.TextureData> future;

	public AsyncTexture(ResourceManager resourceManager, Identifier identifier, Executor executor) {
		super(identifier);
		this.future = CompletableFuture.supplyAsync(() -> ResourceTexture.TextureData.method_18156(resourceManager, identifier), executor);
	}

	@Override
	protected ResourceTexture.TextureData method_18153(ResourceManager resourceManager) {
		if (this.future != null) {
			ResourceTexture.TextureData textureData = (ResourceTexture.TextureData)this.future.join();
			this.future = null;
			return textureData;
		} else {
			return ResourceTexture.TextureData.method_18156(resourceManager, this.field_5224);
		}
	}

	public CompletableFuture<Void> method_18148() {
		return this.future == null ? CompletableFuture.completedFuture(null) : this.future.thenApply(textureData -> null);
	}

	@Override
	public void method_18169(TextureManager textureManager, ResourceManager resourceManager, Identifier identifier, Executor executor) {
		this.future = CompletableFuture.supplyAsync(
			() -> ResourceTexture.TextureData.method_18156(resourceManager, this.field_5224), SystemUtil.getServerWorkerExecutor()
		);
		this.future.thenRunAsync(() -> textureManager.method_4616(this.field_5224, this), executor);
	}
}
