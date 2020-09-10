package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.Resource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
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
	private static final String OBFUSCATION_PLACEHOLDER = "" + Formatting.WHITE + Formatting.OBFUSCATED + Formatting.GREEN + Formatting.AQUA;
	private final boolean endCredits;
	private final Runnable finishAction;
	private float time;
	private List<OrderedText> credits;
	private IntSet centeredLines;
	private int creditsHeight;
	private float speed = 0.5F;

	public CreditsScreen(boolean endCredits, Runnable finishAction) {
		super(NarratorManager.EMPTY);
		this.endCredits = endCredits;
		this.finishAction = finishAction;
		if (!endCredits) {
			this.speed = 0.75F;
		}
	}

	@Override
	public void tick() {
		this.client.getMusicTracker().tick();
		this.client.getSoundManager().tick(false);
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
		this.client.openScreen(null);
	}

	@Override
	protected void init() {
		if (this.credits == null) {
			this.credits = Lists.<OrderedText>newArrayList();
			this.centeredLines = new IntOpenHashSet();
			Resource resource = null;

			try {
				int i = 274;
				if (this.endCredits) {
					resource = this.client.getResourceManager().getResource(new Identifier("texts/end.txt"));
					InputStream inputStream = resource.getInputStream();
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
					Random random = new Random(8124371L);

					String string;
					while ((string = bufferedReader.readLine()) != null) {
						string = string.replaceAll("PLAYERNAME", this.client.getSession().getUsername());

						int j;
						while ((j = string.indexOf(OBFUSCATION_PLACEHOLDER)) != -1) {
							String string2 = string.substring(0, j);
							String string3 = string.substring(j + OBFUSCATION_PLACEHOLDER.length());
							string = string2 + Formatting.WHITE + Formatting.OBFUSCATED + "XXXXXXXX".substring(0, random.nextInt(4) + 3) + string3;
						}

						this.credits.addAll(this.client.textRenderer.wrapLines(new LiteralText(string), 274));
						this.credits.add(OrderedText.EMPTY);
					}

					inputStream.close();

					for (int j = 0; j < 8; j++) {
						this.credits.add(OrderedText.EMPTY);
					}
				}

				InputStream inputStream = this.client.getResourceManager().getResource(new Identifier("texts/credits.txt")).getInputStream();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

				String string4;
				while ((string4 = bufferedReader.readLine()) != null) {
					string4 = string4.replaceAll("PLAYERNAME", this.client.getSession().getUsername());
					string4 = string4.replaceAll("\t", "    ");
					boolean bl;
					if (string4.startsWith("[C]")) {
						string4 = string4.substring(3);
						bl = true;
					} else {
						bl = false;
					}

					for (OrderedText orderedText : this.client.textRenderer.wrapLines(new LiteralText(string4), 274)) {
						if (bl) {
							this.centeredLines.add(this.credits.size());
						}

						this.credits.add(orderedText);
					}

					this.credits.add(OrderedText.EMPTY);
				}

				inputStream.close();
				this.creditsHeight = this.credits.size() * 12;
			} catch (Exception var13) {
				LOGGER.error("Couldn't load credits", (Throwable)var13);
			} finally {
				IOUtils.closeQuietly(resource);
			}
		}
	}

	private void renderBackground(int mouseX, int mouseY, float tickDelta) {
		this.client.getTextureManager().bindTexture(DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
		int i = this.width;
		float f = -this.time * 0.5F * this.speed;
		float g = (float)this.height - this.time * 0.5F * this.speed;
		float h = 0.015625F;
		float j = this.time * 0.02F;
		float k = (float)(this.creditsHeight + this.height + this.height + 24) / this.speed;
		float l = (k - 20.0F - this.time) * 0.005F;
		if (l < j) {
			j = l;
		}

		if (j > 1.0F) {
			j = 1.0F;
		}

		j *= j;
		j = j * 96.0F / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(0.0, (double)this.height, (double)this.getZOffset()).texture(0.0F, f * 0.015625F).color(j, j, j, 1.0F).next();
		bufferBuilder.vertex((double)i, (double)this.height, (double)this.getZOffset()).texture((float)i * 0.015625F, f * 0.015625F).color(j, j, j, 1.0F).next();
		bufferBuilder.vertex((double)i, 0.0, (double)this.getZOffset()).texture((float)i * 0.015625F, g * 0.015625F).color(j, j, j, 1.0F).next();
		bufferBuilder.vertex(0.0, 0.0, (double)this.getZOffset()).texture(0.0F, g * 0.015625F).color(j, j, j, 1.0F).next();
		tessellator.draw();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(mouseX, mouseY, delta);
		int i = 274;
		int j = this.width / 2 - 137;
		int k = this.height + 50;
		this.time += delta;
		float f = -this.time * this.speed;
		RenderSystem.pushMatrix();
		RenderSystem.translatef(0.0F, f, 0.0F);
		this.client.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableAlphaTest();
		RenderSystem.enableBlend();
		this.method_29343(j, k, (integer, integer2) -> {
			this.drawTexture(matrices, integer + 0, integer2, 0, 0, 155, 44);
			this.drawTexture(matrices, integer + 155, integer2, 0, 45, 155, 44);
		});
		RenderSystem.disableBlend();
		this.client.getTextureManager().bindTexture(EDITION_TITLE_TEXTURE);
		drawTexture(matrices, j + 88, k + 37, 0.0F, 0.0F, 98, 14, 128, 16);
		RenderSystem.disableAlphaTest();
		int l = k + 100;

		for (int m = 0; m < this.credits.size(); m++) {
			if (m == this.credits.size() - 1) {
				float g = (float)l + f - (float)(this.height / 2 - 6);
				if (g < 0.0F) {
					RenderSystem.translatef(0.0F, -g, 0.0F);
				}
			}

			if ((float)l + f + 12.0F + 8.0F > 0.0F && (float)l + f < (float)this.height) {
				OrderedText orderedText = (OrderedText)this.credits.get(m);
				if (this.centeredLines.contains(m)) {
					this.textRenderer.drawWithShadow(matrices, orderedText, (float)(j + (274 - this.textRenderer.getWidth(orderedText)) / 2), (float)l, 16777215);
				} else {
					this.textRenderer.random.setSeed((long)((float)((long)m * 4238972211L) + this.time / 4.0F));
					this.textRenderer.drawWithShadow(matrices, orderedText, (float)j, (float)l, 16777215);
				}
			}

			l += 12;
		}

		RenderSystem.popMatrix();
		this.client.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR);
		int m = this.width;
		int n = this.height;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(0.0, (double)n, (double)this.getZOffset()).texture(0.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		bufferBuilder.vertex((double)m, (double)n, (double)this.getZOffset()).texture(1.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		bufferBuilder.vertex((double)m, 0.0, (double)this.getZOffset()).texture(1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		bufferBuilder.vertex(0.0, 0.0, (double)this.getZOffset()).texture(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		tessellator.draw();
		RenderSystem.disableBlend();
		super.render(matrices, mouseX, mouseY, delta);
	}
}
