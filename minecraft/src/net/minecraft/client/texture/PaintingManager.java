package net.minecraft.client.texture;

import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class PaintingManager implements ResourceReloadListener {
	private static final Identifier PAINTING_BACK_ID = new Identifier("back");
	private final SpriteAtlasTexture paintingsAtlas = new SpriteAtlasTexture("textures/painting");

	public PaintingManager(TextureManager textureManager) {
		textureManager.registerTextureUpdateable(SpriteAtlasTexture.PAINTING_ATLAS_TEX, this.paintingsAtlas);
	}

	@Override
	public CompletableFuture<Void> apply(
		ResourceReloadListener.Helper helper, ResourceManager resourceManager, Profiler profiler, Profiler profiler2, Executor executor, Executor executor2
	) {
		return CompletableFuture.supplyAsync(
				() -> {
					profiler.startTick();
					profiler.push("paintings_stitching");
					SpriteAtlasTexture.Data data = this.paintingsAtlas
						.stitch(resourceManager, Iterables.concat(Registry.MOTIVE.getIds(), Collections.singleton(PAINTING_BACK_ID)), profiler);
					profiler.pop();
					profiler.endTick();
					return data;
				},
				executor
			)
			.thenCompose(helper::waitForAll)
			.thenAcceptAsync(data -> {
				profiler2.startTick();
				profiler2.push("paintings_upload");
				this.paintingsAtlas.upload(data);
				profiler2.pop();
				profiler2.endTick();
			}, executor2);
	}

	public Sprite getPaintingSprite(PaintingMotive paintingMotive) {
		return this.paintingsAtlas.getSprite(Registry.MOTIVE.getId(paintingMotive));
	}

	public Sprite getBackSprite() {
		return this.paintingsAtlas.getSprite(PAINTING_BACK_ID);
	}
}
