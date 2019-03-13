package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4184;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public abstract class BlockEntityRenderer<T extends BlockEntity> {
	public static final Identifier[] field_4368 = new Identifier[]{
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
		HitResult hitResult = this.renderManager.hitResult;
		if (blockEntity instanceof Nameable
			&& hitResult != null
			&& hitResult.getType() == HitResult.Type.BLOCK
			&& blockEntity.method_11016().equals(((BlockHitResult)hitResult).method_17777())) {
			this.method_3570(true);
			this.method_3567(blockEntity, ((Nameable)blockEntity).method_5476().getFormattedText(), d, e, f, 12);
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

	protected void method_3566(Identifier identifier) {
		TextureManager textureManager = this.renderManager.field_4347;
		if (textureManager != null) {
			textureManager.method_4618(identifier);
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
		class_4184 lv = this.renderManager.cameraEntity;
		double g = blockEntity.getSquaredDistance(lv.method_19326().x, lv.method_19326().y, lv.method_19326().z);
		if (!(g > (double)(i * i))) {
			float h = lv.method_19330();
			float j = lv.method_19329();
			GameRenderer.method_3179(this.getFontRenderer(), string, (float)d + 0.5F, (float)e + 1.5F, (float)f + 0.5F, 0, h, j, false);
		}
	}
}
