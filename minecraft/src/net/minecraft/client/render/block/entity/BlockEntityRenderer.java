package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public abstract class BlockEntityRenderer<T extends BlockEntity> {
	protected static final List<Identifier> DESTROY_STAGE_TEXTURES = (List<Identifier>)ModelLoader.field_20848
		.stream()
		.map(identifier -> new Identifier("textures/" + identifier.getPath() + ".png"))
		.collect(Collectors.toList());
	protected BlockEntityRenderDispatcher renderManager;

	public void method_22747(
		T blockEntity, double d, double e, double f, float g, int i, BufferBuilder bufferBuilder, BlockRenderLayer blockRenderLayer, BlockPos blockPos
	) {
		GuiLighting.enable();
		int j = blockEntity.getWorld().getLightmapIndex(blockEntity.getPos());
		int k = j % 65536;
		int l = j / 65536;
		RenderSystem.glMultiTexCoord2f(33985, (float)k, (float)l);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.render(blockEntity, d, e, f, g, i, blockRenderLayer);
	}

	public abstract void render(T blockEntity, double d, double e, double f, float g, int i, BlockRenderLayer blockRenderLayer);

	protected void method_22746(T blockEntity, double d, double e, double f) {
		HitResult hitResult = this.renderManager.hitResult;
		if (blockEntity instanceof Nameable
			&& hitResult != null
			&& hitResult.getType() == HitResult.Type.BLOCK
			&& blockEntity.getPos().equals(((BlockHitResult)hitResult).getBlockPos())) {
			this.disableLightmap(true);
			this.renderName(blockEntity, ((Nameable)blockEntity).getDisplayName().asFormattedString(), d, e, f, 12);
			this.disableLightmap(false);
		}
	}

	protected void disableLightmap(boolean bl) {
		RenderSystem.activeTexture(33985);
		if (bl) {
			RenderSystem.disableTexture();
		} else {
			RenderSystem.enableTexture();
		}

		RenderSystem.activeTexture(33984);
	}

	protected void bindTexture(Identifier identifier) {
		TextureManager textureManager = this.renderManager.textureManager;
		if (textureManager != null) {
			textureManager.bindTexture(identifier);
		}
	}

	protected World getWorld() {
		return this.renderManager.world;
	}

	public void setRenderManager(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		this.renderManager = blockEntityRenderDispatcher;
	}

	public TextRenderer getFontRenderer() {
		return this.renderManager.getFontRenderer();
	}

	public boolean method_3563(T blockEntity) {
		return false;
	}

	protected void renderName(T blockEntity, String string, double d, double e, double f, int i) {
		Camera camera = this.renderManager.cameraEntity;
		double g = blockEntity.getSquaredDistance(camera.getPos().x, camera.getPos().y, camera.getPos().z);
		if (!(g > (double)(i * i))) {
			float h = camera.getYaw();
			float j = camera.getPitch();
			GameRenderer.renderFloatingText(this.getFontRenderer(), string, (float)d + 0.5F, (float)e + 1.5F, (float)f + 0.5F, 0, h, j, false);
		}
	}
}
