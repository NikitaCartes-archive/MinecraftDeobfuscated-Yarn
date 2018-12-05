package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.font.FontRenderer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public abstract class BlockEntityRenderer<T extends BlockEntity> {
	public static final Identifier[] DESTROY_STAGE_TEXTURES = new Identifier[]{
		new Identifier("textures/" + ModelLoader.field_5377.getPath() + ".png"),
		new Identifier("textures/" + ModelLoader.field_5385.getPath() + ".png"),
		new Identifier("textures/" + ModelLoader.field_5375.getPath() + ".png"),
		new Identifier("textures/" + ModelLoader.field_5403.getPath() + ".png"),
		new Identifier("textures/" + ModelLoader.field_5393.getPath() + ".png"),
		new Identifier("textures/" + ModelLoader.field_5386.getPath() + ".png"),
		new Identifier("textures/" + ModelLoader.field_5369.getPath() + ".png"),
		new Identifier("textures/" + ModelLoader.field_5401.getPath() + ".png"),
		new Identifier("textures/" + ModelLoader.field_5392.getPath() + ".png"),
		new Identifier("textures/" + ModelLoader.field_5382.getPath() + ".png")
	};
	protected BlockEntityRenderDispatcher renderManager;

	public void render(T blockEntity, double d, double e, double f, float g, int i) {
		if (blockEntity instanceof Nameable && this.renderManager.hitResult != null && blockEntity.getPos().equals(this.renderManager.hitResult.getBlockPos())) {
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

	public FontRenderer getFontRenderer() {
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
			WorldRenderer.method_3179(this.getFontRenderer(), string, (float)d + 0.5F, (float)e + 1.5F, (float)f + 0.5F, 0, h, j, false, false);
		}
	}
}
