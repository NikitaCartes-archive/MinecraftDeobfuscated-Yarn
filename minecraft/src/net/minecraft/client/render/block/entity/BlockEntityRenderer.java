package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public abstract class BlockEntityRenderer<T extends BlockEntity> {
	public static final Identifier[] DESTROY_STAGE_TEXTURES = new Identifier[]{
		new Identifier("textures/" + ModelLoader.DESTROY_STAGE_0.getPath() + ".png"),
		new Identifier("textures/" + ModelLoader.DESTROY_STAGE_1.getPath() + ".png"),
		new Identifier("textures/" + ModelLoader.DESTROY_STAGE_2.getPath() + ".png"),
		new Identifier("textures/" + ModelLoader.DESTROY_STAGE_3.getPath() + ".png"),
		new Identifier("textures/" + ModelLoader.DESTROY_STAGE_4.getPath() + ".png"),
		new Identifier("textures/" + ModelLoader.DESTROY_STAGE_5.getPath() + ".png"),
		new Identifier("textures/" + ModelLoader.DESTROY_STAGE_6.getPath() + ".png"),
		new Identifier("textures/" + ModelLoader.DESTROY_STAGE_7.getPath() + ".png"),
		new Identifier("textures/" + ModelLoader.DESTROY_STAGE_8.getPath() + ".png"),
		new Identifier("textures/" + ModelLoader.DESTROY_STAGE_9.getPath() + ".png")
	};
	protected BlockEntityRenderDispatcher renderManager;

	public void render(T blockEntity, double d, double e, double f, float g, int i) {
		HitResult hitResult = this.renderManager.hitResult;
		if (blockEntity instanceof Nameable
			&& hitResult != null
			&& hitResult.getType() == HitResult.Type.BLOCK
			&& blockEntity.getPos().equals(((BlockHitResult)hitResult).getBlockPos())) {
			this.method_3570(true);
			this.method_3567(blockEntity, ((Nameable)blockEntity).getDisplayName().getFormattedText(), d, e, f, 12);
			this.method_3570(false);
		}
	}

	protected void method_3570(boolean bl) {
		GlStateManager.activeTexture(GLX.GL_TEXTURE1);
		if (bl) {
			GlStateManager.disableTexture();
		} else {
			GlStateManager.enableTexture();
		}

		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
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

	protected void method_3567(T blockEntity, String string, double d, double e, double f, int i) {
		Entity entity = this.renderManager.cameraEntity;
		double g = blockEntity.getSquaredDistance(entity.x, entity.y, entity.z);
		if (!(g > (double)(i * i))) {
			float h = this.renderManager.cameraYaw;
			float j = this.renderManager.cameraPitch;
			boolean bl = false;
			GameRenderer.method_3179(this.getFontRenderer(), string, (float)d + 0.5F, (float)e + 1.5F, (float)f + 0.5F, 0, h, j, false, false);
		}
	}
}
