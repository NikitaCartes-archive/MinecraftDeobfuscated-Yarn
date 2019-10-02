package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.resource.Resource;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class CreditsScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier MINECRAFT_TITLE_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
	private static final Identifier EDITION_TITLE_TEXTURE = new Identifier("textures/gui/title/edition.png");
	private static final Identifier VIGNETTE_TEXTURE = new Identifier("textures/misc/vignette.png");
	private final boolean endCredits;
	private final Runnable finishAction;
	private float time;
	private List<String> credits;
	private int creditsHeight;
	private float speed = 0.5F;

	public CreditsScreen(boolean bl, Runnable runnable) {
		super(NarratorManager.EMPTY);
		this.endCredits = bl;
		this.finishAction = runnable;
		if (!bl) {
			this.speed = 0.75F;
		}
	}

	@Override
	public void tick() {
		this.minecraft.getMusicTracker().tick();
		this.minecraft.getSoundManager().tick(false);
		float f = (float)(this.creditsHeight + this.height + this.height + 24) / this.speed;
		if (this.time > f) {
			this.close();
		}
	}

	@Override
	public void onClose() {
		this.close();
	}

	private void close() {
		this.finishAction.run();
		this.minecraft.openScreen(null);
	}

	@Override
	protected void init() {
		if (this.credits == null) {
			this.credits = Lists.<String>newArrayList();
			Resource resource = null;

			try {
				String string = "" + Formatting.WHITE + Formatting.OBFUSCATED + Formatting.GREEN + Formatting.AQUA;
				int i = 274;
				if (this.endCredits) {
					resource = this.minecraft.getResourceManager().getResource(new Identifier("texts/end.txt"));
					InputStream inputStream = resource.getInputStream();
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
					Random random = new Random(8124371L);

					String string2;
					while ((string2 = bufferedReader.readLine()) != null) {
						string2 = string2.replaceAll("PLAYERNAME", this.minecraft.getSession().getUsername());

						while (string2.contains(string)) {
							int j = string2.indexOf(string);
							String string3 = string2.substring(0, j);
							String string4 = string2.substring(j + string.length());
							string2 = string3 + Formatting.WHITE + Formatting.OBFUSCATED + "XXXXXXXX".substring(0, random.nextInt(4) + 3) + string4;
						}

						this.credits.addAll(this.minecraft.textRenderer.wrapStringToWidthAsList(string2, 274));
						this.credits.add("");
					}

					inputStream.close();

					for (int j = 0; j < 8; j++) {
						this.credits.add("");
					}
				}

				InputStream inputStream = this.minecraft.getResourceManager().getResource(new Identifier("texts/credits.txt")).getInputStream();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

				String string5;
				while ((string5 = bufferedReader.readLine()) != null) {
					string5 = string5.replaceAll("PLAYERNAME", this.minecraft.getSession().getUsername());
					string5 = string5.replaceAll("\t", "    ");
					this.credits.addAll(this.minecraft.textRenderer.wrapStringToWidthAsList(string5, 274));
					this.credits.add("");
				}

				inputStream.close();
				this.creditsHeight = this.credits.size() * 12;
			} catch (Exception var14) {
				LOGGER.error("Couldn't load credits", (Throwable)var14);
			} finally {
				IOUtils.closeQuietly(resource);
			}
		}
	}

	private void renderBackground(int i, int j, float f) {
		this.minecraft.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_LOCATION);
		int k = this.width;
		float g = -this.time * 0.5F * this.speed;
		float h = (float)this.height - this.time * 0.5F * this.speed;
		float l = 0.015625F;
		float m = this.time * 0.02F;
		float n = (float)(this.creditsHeight + this.height + this.height + 24) / this.speed;
		float o = (n - 20.0F - this.time) * 0.005F;
		if (o < m) {
			m = o;
		}

		if (m > 1.0F) {
			m = 1.0F;
		}

		m *= m;
		m = m * 96.0F / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
		bufferBuilder.vertex(0.0, (double)this.height, (double)this.getBlitOffset()).texture(0.0F, g * 0.015625F).color(m, m, m, 1.0F).next();
		bufferBuilder.vertex((double)k, (double)this.height, (double)this.getBlitOffset()).texture((float)k * 0.015625F, g * 0.015625F).color(m, m, m, 1.0F).next();
		bufferBuilder.vertex((double)k, 0.0, (double)this.getBlitOffset()).texture((float)k * 0.015625F, h * 0.015625F).color(m, m, m, 1.0F).next();
		bufferBuilder.vertex(0.0, 0.0, (double)this.getBlitOffset()).texture(0.0F, h * 0.015625F).color(m, m, m, 1.0F).next();
		tessellator.draw();
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground(i, j, f);
		int k = 274;
		int l = this.width / 2 - 137;
		int m = this.height + 50;
		this.time += f;
		float g = -this.time * this.speed;
		RenderSystem.pushMatrix();
		RenderSystem.translatef(0.0F, g, 0.0F);
		this.minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableAlphaTest();
		this.blit(l, m, 0, 0, 155, 44);
		this.blit(l + 155, m, 0, 45, 155, 44);
		this.minecraft.getTextureManager().bindTexture(EDITION_TITLE_TEXTURE);
		blit(l + 88, m + 37, 0.0F, 0.0F, 98, 14, 128, 16);
		RenderSystem.disableAlphaTest();
		int n = m + 100;

		for (int o = 0; o < this.credits.size(); o++) {
			if (o == this.credits.size() - 1) {
				float h = (float)n + g - (float)(this.height / 2 - 6);
				if (h < 0.0F) {
					RenderSystem.translatef(0.0F, -h, 0.0F);
				}
			}

			if ((float)n + g + 12.0F + 8.0F > 0.0F && (float)n + g < (float)this.height) {
				String string = (String)this.credits.get(o);
				if (string.startsWith("[C]")) {
					this.font.drawWithShadow(string.substring(3), (float)(l + (274 - this.font.getStringWidth(string.substring(3))) / 2), (float)n, 16777215);
				} else {
					this.font.random.setSeed((long)((float)((long)o * 4238972211L) + this.time / 4.0F));
					this.font.drawWithShadow(string, (float)l, (float)n, 16777215);
				}
			}

			n += 12;
		}

		RenderSystem.popMatrix();
		this.minecraft.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.class_4535.ZERO, GlStateManager.class_4534.ONE_MINUS_SRC_COLOR);
		int o = this.width;
		int p = this.height;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
		bufferBuilder.vertex(0.0, (double)p, (double)this.getBlitOffset()).texture(0.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		bufferBuilder.vertex((double)o, (double)p, (double)this.getBlitOffset()).texture(1.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		bufferBuilder.vertex((double)o, 0.0, (double)this.getBlitOffset()).texture(1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		bufferBuilder.vertex(0.0, 0.0, (double)this.getBlitOffset()).texture(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		tessellator.draw();
		RenderSystem.disableBlend();
		super.render(i, j, f);
	}
}
