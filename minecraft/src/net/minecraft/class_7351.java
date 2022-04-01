package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Optional;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceReload;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class class_7351 extends Overlay {
	private static final int field_38628 = 208;
	private static final int field_38629 = 38;
	private static final int field_38630 = 39;
	private static final int field_38631 = 38;
	private static final Identifier field_38632 = new Identifier("textures/gui/mojang_logo.png");
	private static final Identifier field_38633 = new Identifier("textures/gui/mojang_text.png");
	private static final float field_38634 = 0.99F;
	private class_7351.class_7353 field_38635 = class_7351.class_7353.INIT;
	private long field_38636 = -1L;
	private float field_38637;
	private long field_38638;
	private SoundInstance field_38639;
	private final MinecraftClient field_38640;
	private final ResourceReload field_38641;
	private final Consumer<Optional<Throwable>> field_38642;
	private float field_38643;
	private final class_7351.class_7352 field_38644;

	public class_7351(MinecraftClient minecraftClient, ResourceReload resourceReload, Consumer<Optional<Throwable>> consumer) {
		this.field_38640 = minecraftClient;
		this.field_38641 = resourceReload;
		this.field_38642 = consumer;
		class_7351.class_7352[] lvs = class_7351.class_7352.values();
		this.field_38644 = lvs[(int)(System.currentTimeMillis() % (long)lvs.length)];
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		int i = this.field_38640.getWindow().getScaledWidth();
		int j = this.field_38640.getWindow().getScaledHeight();
		RenderSystem.enableBlend();
		RenderSystem.blendEquation(32774);
		RenderSystem.blendFunc(770, 1);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.blendFuncSeparate(
			GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
		);
		RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 1.0F);
		RenderSystem.clear(16640, MinecraftClient.IS_SYSTEM_MAC);
		long l = Util.getMeasuringTimeMs();
		long m = l - this.field_38638;
		this.field_38638 = l;
		if (this.field_38635 == class_7351.class_7353.INIT) {
			this.field_38635 = class_7351.class_7353.FLY;
			this.field_38637 = 10.0F;
			this.field_38636 = -1L;
		} else {
			if (this.field_38635 == class_7351.class_7353.FLY) {
				this.field_38637 -= (float)m / 500.0F;
				if (this.field_38637 <= 0.0F) {
					this.field_38635 = class_7351.class_7353.WAIT_FOR_LOAD;
				}
			} else if (this.field_38635 == class_7351.class_7353.WAIT_FOR_LOAD) {
				if (this.field_38641.isComplete()) {
					this.field_38636 = l;
					this.field_38639 = new PositionedSoundInstance(
						SoundEvents.AWESOME_INTRO.getId(), SoundCategory.MASTER, 0.25F, 1.0F, false, 0, SoundInstance.AttenuationType.NONE, 0.0, 0.0, 0.0, true
					);
					this.field_38640.getSoundManager().play(this.field_38639);
					this.field_38635 = class_7351.class_7353.TEXT;
				}
			} else if (!this.field_38640.getSoundManager().isPlaying(this.field_38639)) {
				this.field_38640.setOverlay(null);

				try {
					this.field_38641.throwException();
					this.field_38642.accept(Optional.empty());
				} catch (Throwable var17) {
					this.field_38642.accept(Optional.of(var17));
				}

				if (this.field_38640.currentScreen != null) {
					this.field_38640.currentScreen.init(this.field_38640, this.field_38640.getWindow().getScaledWidth(), this.field_38640.getWindow().getScaledHeight());
				}

				this.field_38635 = class_7351.class_7353.INIT;
			}

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
			int k = MathHelper.clamp((int)(l - this.field_38636), 0, 255);
			if (this.field_38636 != -1L) {
				RenderSystem.setShaderTexture(0, field_38633);
				this.method_42981(matrices, bufferBuilder, i / 2, j - j / 8, 208, 38, k);
			}

			tessellator.draw();
			matrices.push();
			matrices.translate((double)((float)i / 2.0F), (double)((float)j / 2.0F), 0.0);
			switch (this.field_38644) {
				case CLASSIC: {
					float f = 20.0F * this.field_38637;
					float g = 100.0F * MathHelper.sin(this.field_38637);
					matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(f));
					matrices.translate((double)g, 0.0, 0.0);
					float h = 1.0F / (2.0F * this.field_38637 + 1.0F);
					matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(1.5F * this.field_38637));
					matrices.scale(h, h, 1.0F);
					break;
				}
				case SPRING: {
					float f = 40.0F * ((float)Math.exp((double)(this.field_38637 / 3.0F)) - 1.0F) * MathHelper.sin(this.field_38637);
					matrices.translate((double)f, 0.0, 0.0);
					break;
				}
				case SLOWDOWN: {
					float f = (float)Math.exp((double)this.field_38637) - 1.0F;
					matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(f));
					break;
				}
				case REVERSE: {
					float f = MathHelper.cos(this.field_38637 / 10.0F * (float) Math.PI);
					matrices.scale(f, f, 1.0F);
					break;
				}
				case GROW: {
					float f = (1.0F - this.field_38637 / 10.0F) * 0.75F;
					float g = 2.0F * MathHelper.sin(f * (float) Math.PI);
					matrices.scale(g, g, 1.0F);
				}
			}

			RenderSystem.setShaderTexture(0, field_38632);
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
			this.method_42981(matrices, bufferBuilder, 0, 0, 78, 76, 255);
			tessellator.draw();
			float f = this.field_38641.getProgress();
			this.field_38643 = MathHelper.clamp(this.field_38643 * 0.99F + f * 0.00999999F, 0.0F, 1.0F);
			this.method_42980(matrices, -39, 38, 39, 48, this.field_38643 != 1.0F ? 1.0F : 0.0F);
			matrices.pop();
		}
	}

	private void method_42981(MatrixStack matrixStack, BufferBuilder bufferBuilder, int i, int j, int k, int l, int m) {
		int n = k / 2;
		int o = l / 2;
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
		bufferBuilder.vertex(matrix4f, (float)(i - n), (float)(j + o), 0.0F).texture(0.0F, 1.0F).color(255, 255, 255, m).next();
		bufferBuilder.vertex(matrix4f, (float)(i + n), (float)(j + o), 0.0F).texture(1.0F, 1.0F).color(255, 255, 255, m).next();
		bufferBuilder.vertex(matrix4f, (float)(i + n), (float)(j - o), 0.0F).texture(1.0F, 0.0F).color(255, 255, 255, m).next();
		bufferBuilder.vertex(matrix4f, (float)(i - n), (float)(j - o), 0.0F).texture(0.0F, 0.0F).color(255, 255, 255, m).next();
	}

	private void method_42980(MatrixStack matrixStack, int i, int j, int k, int l, float f) {
		int m = MathHelper.ceil((float)(k - i - 2) * this.field_38643);
		int n = Math.round(f * 255.0F);
		int o = ColorHelper.Argb.getArgb(n, 255, 255, 255);
		fill(matrixStack, i + 2, j + 2, i + m, l - 2, o);
		fill(matrixStack, i + 1, j, k - 1, j + 1, o);
		fill(matrixStack, i + 1, l, k - 1, l - 1, o);
		fill(matrixStack, i, j, i + 1, l, o);
		fill(matrixStack, k, j, k - 1, l, o);
	}

	@Environment(EnvType.CLIENT)
	static enum class_7352 {
		CLASSIC,
		SPRING,
		SLOWDOWN,
		REVERSE,
		GROW;
	}

	@Environment(EnvType.CLIENT)
	static enum class_7353 {
		INIT,
		FLY,
		WAIT_FOR_LOAD,
		TEXT;
	}
}
