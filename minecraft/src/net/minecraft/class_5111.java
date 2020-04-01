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
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.resource.ResourceReloadMonitor;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class class_5111 extends Overlay {
	private static final Identifier field_23607 = new Identifier("textures/gui/mojang_logo.png");
	private static final Identifier field_23608 = new Identifier("textures/gui/mojang_text.png");
	private class_5111.class_5113 field_23609 = class_5111.class_5113.field_23626;
	private long field_23610 = -1L;
	private float field_23611;
	private long field_23612;
	private SoundInstance field_23613;
	private final MinecraftClient field_23614;
	private final ResourceReloadMonitor field_23615;
	private final Consumer<Optional<Throwable>> field_23616;
	private float field_23617;
	private final class_5111.class_5112 field_23618;

	public class_5111(MinecraftClient minecraftClient, ResourceReloadMonitor resourceReloadMonitor, Consumer<Optional<Throwable>> consumer) {
		this.field_23614 = minecraftClient;
		this.field_23615 = resourceReloadMonitor;
		this.field_23616 = consumer;
		class_5111.class_5112[] lvs = class_5111.class_5112.values();
		this.field_23618 = lvs[(int)(System.currentTimeMillis() % (long)lvs.length)];
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		int i = this.field_23614.getWindow().getScaledWidth();
		int j = this.field_23614.getWindow().getScaledHeight();
		RenderSystem.enableTexture();
		RenderSystem.disableCull();
		RenderSystem.enableBlend();
		RenderSystem.disableAlphaTest();
		RenderSystem.blendFuncSeparate(
			GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
		);
		RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.clear(16640, MinecraftClient.IS_SYSTEM_MAC);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		long l = Util.getMeasuringTimeMs();
		long m = l - this.field_23612;
		this.field_23612 = l;
		if (this.field_23609 == class_5111.class_5113.field_23626) {
			this.field_23609 = class_5111.class_5113.field_23627;
			this.field_23611 = 10.0F;
			this.field_23610 = -1L;
		} else {
			if (this.field_23609 == class_5111.class_5113.field_23627) {
				this.field_23611 -= (float)m / 500.0F;
				if (this.field_23611 <= 0.0F) {
					this.field_23609 = class_5111.class_5113.field_23628;
				}
			} else if (this.field_23609 == class_5111.class_5113.field_23628) {
				if (this.field_23615.isApplyStageComplete()) {
					this.field_23610 = l;
					this.field_23613 = new PositionedSoundInstance(
						SoundEvents.AWESOME_INTRO.getId(), SoundCategory.MASTER, 0.25F, 1.0F, false, 0, SoundInstance.AttenuationType.NONE, 0.0F, 0.0F, 0.0F, true
					);
					this.field_23614.getSoundManager().play(this.field_23613);
					this.field_23609 = class_5111.class_5113.field_23629;
				}
			} else if (!this.field_23614.getSoundManager().isPlaying(this.field_23613)) {
				this.field_23614.setOverlay(null);

				try {
					this.field_23615.throwExceptions();
					this.field_23616.accept(Optional.empty());
				} catch (Throwable var16) {
					this.field_23616.accept(Optional.of(var16));
				}

				if (this.field_23614.currentScreen != null) {
					this.field_23614.currentScreen.init(this.field_23614, this.field_23614.getWindow().getScaledWidth(), this.field_23614.getWindow().getScaledHeight());
				}

				this.field_23609 = class_5111.class_5113.field_23626;
			}

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
			int k = MathHelper.clamp((int)(l - this.field_23610), 0, 255);
			if (this.field_23610 != -1L) {
				this.field_23614.getTextureManager().bindTexture(field_23608);
				this.method_26694(bufferBuilder, i / 2, j - j / 8, 208, 38, k);
			}

			tessellator.draw();
			RenderSystem.pushMatrix();
			RenderSystem.translatef((float)i / 2.0F, (float)j / 2.0F, 0.0F);
			switch (this.field_23618) {
				case field_23620: {
					float f = 20.0F * this.field_23611;
					float g = 100.0F * MathHelper.sin(this.field_23611);
					RenderSystem.rotatef(f, 0.0F, 0.0F, 1.0F);
					RenderSystem.translatef(g, 0.0F, 0.0F);
					float h = 1.0F / (2.0F * this.field_23611 + 1.0F);
					RenderSystem.rotatef(1.5F * this.field_23611, 0.0F, 0.0F, 1.0F);
					RenderSystem.scalef(h, h, 1.0F);
					break;
				}
				case field_23621: {
					float f = 40.0F * ((float)Math.exp((double)(this.field_23611 / 3.0F)) - 1.0F) * MathHelper.sin(this.field_23611);
					RenderSystem.translatef(f, 0.0F, 0.0F);
					break;
				}
				case field_23622: {
					float f = (float)Math.exp((double)this.field_23611) - 1.0F;
					RenderSystem.rotatef(f, 1.0F, 0.0F, 0.0F);
					break;
				}
				case field_23623: {
					float f = MathHelper.cos(this.field_23611 / 10.0F * (float) Math.PI);
					RenderSystem.scalef(f, f, 1.0F);
					break;
				}
				case field_23624: {
					float f = (1.0F - this.field_23611 / 10.0F) * 0.75F;
					float g = 2.0F * MathHelper.sin(f * (float) Math.PI);
					RenderSystem.scalef(g, g, 1.0F);
				}
			}

			this.field_23614.getTextureManager().bindTexture(field_23607);
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
			this.method_26694(bufferBuilder, 0, 0, 78, 76, 255);
			tessellator.draw();
			float f = this.field_23615.getProgress();
			this.field_23617 = MathHelper.clamp(this.field_23617 * 0.99F + f * 0.00999999F, 0.0F, 1.0F);
			this.method_26693(-39, 38, 39, 48, this.field_23617 != 1.0F ? 1.0F : 0.0F);
			RenderSystem.popMatrix();
		}
	}

	private void method_26694(BufferBuilder bufferBuilder, int i, int j, int k, int l, int m) {
		int n = k / 2;
		int o = l / 2;
		bufferBuilder.vertex((double)(i - n), (double)(j + o), 0.0).texture(0.0F, 1.0F).color(255, 255, 255, m).next();
		bufferBuilder.vertex((double)(i + n), (double)(j + o), 0.0).texture(1.0F, 1.0F).color(255, 255, 255, m).next();
		bufferBuilder.vertex((double)(i + n), (double)(j - o), 0.0).texture(1.0F, 0.0F).color(255, 255, 255, m).next();
		bufferBuilder.vertex((double)(i - n), (double)(j - o), 0.0).texture(0.0F, 0.0F).color(255, 255, 255, m).next();
	}

	private void method_26693(int i, int j, int k, int l, float f) {
		int m = MathHelper.ceil((float)(k - i - 1) * this.field_23617);
		fill(i - 1, j - 1, k + 1, l + 1, 0xFF000000 | Math.round((1.0F - f) * 255.0F) << 16 | Math.round((1.0F - f) * 255.0F) << 8 | Math.round((1.0F - f) * 255.0F));
		fill(i, j, k, l, -1);
		fill(
			i + 1,
			j + 1,
			i + m,
			l - 1,
			0xFF000000
				| (int)MathHelper.lerp(1.0F - f, 226.0F, 255.0F) << 16
				| (int)MathHelper.lerp(1.0F - f, 40.0F, 255.0F) << 8
				| (int)MathHelper.lerp(1.0F - f, 55.0F, 255.0F)
		);
	}

	@Environment(EnvType.CLIENT)
	static enum class_5112 {
		field_23620,
		field_23621,
		field_23622,
		field_23623,
		field_23624;
	}

	@Environment(EnvType.CLIENT)
	static enum class_5113 {
		field_23626,
		field_23627,
		field_23628,
		field_23629;
	}
}
