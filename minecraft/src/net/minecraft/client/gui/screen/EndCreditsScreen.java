package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
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
public class EndCreditsScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier MINECRAFT_TITLE_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
	private static final Identifier EDITION_TITLE_TEXTURE = new Identifier("textures/gui/title/edition.png");
	private static final Identifier VIGNETTE_TEXTURE = new Identifier("textures/misc/vignette.png");
	private final boolean inGame;
	private final Runnable respawn;
	private float field_2628;
	private List<String> field_2634;
	private int field_2629;
	private float field_2635 = 0.5F;

	public EndCreditsScreen(boolean bl, Runnable runnable) {
		super(NarratorManager.EMPTY);
		this.inGame = bl;
		this.respawn = runnable;
		if (!bl) {
			this.field_2635 = 0.75F;
		}
	}

	@Override
	public void tick() {
		this.minecraft.method_1538().tick();
		this.minecraft.method_1483().tick(false);
		float f = (float)(this.field_2629 + this.height + this.height + 24) / this.field_2635;
		if (this.field_2628 > f) {
			this.respawn();
		}
	}

	@Override
	public void onClose() {
		this.respawn();
	}

	private void respawn() {
		this.respawn.run();
		this.minecraft.method_1507(null);
	}

	@Override
	protected void init() {
		if (this.field_2634 == null) {
			this.field_2634 = Lists.<String>newArrayList();
			Resource resource = null;

			try {
				String string = "" + Formatting.field_1068 + Formatting.field_1051 + Formatting.field_1060 + Formatting.field_1075;
				int i = 274;
				if (this.inGame) {
					resource = this.minecraft.getResourceManager().getResource(new Identifier("texts/end.txt"));
					InputStream inputStream = resource.getInputStream();
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
					Random random = new Random(8124371L);

					String string2;
					while ((string2 = bufferedReader.readLine()) != null) {
						string2 = string2.replaceAll("PLAYERNAME", this.minecraft.method_1548().getUsername());

						while (string2.contains(string)) {
							int j = string2.indexOf(string);
							String string3 = string2.substring(0, j);
							String string4 = string2.substring(j + string.length());
							string2 = string3 + Formatting.field_1068 + Formatting.field_1051 + "XXXXXXXX".substring(0, random.nextInt(4) + 3) + string4;
						}

						this.field_2634.addAll(this.minecraft.field_1772.wrapStringToWidthAsList(string2, 274));
						this.field_2634.add("");
					}

					inputStream.close();

					for (int j = 0; j < 8; j++) {
						this.field_2634.add("");
					}
				}

				InputStream inputStream = this.minecraft.getResourceManager().getResource(new Identifier("texts/credits.txt")).getInputStream();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

				String string5;
				while ((string5 = bufferedReader.readLine()) != null) {
					string5 = string5.replaceAll("PLAYERNAME", this.minecraft.method_1548().getUsername());
					string5 = string5.replaceAll("\t", "    ");
					this.field_2634.addAll(this.minecraft.field_1772.wrapStringToWidthAsList(string5, 274));
					this.field_2634.add("");
				}

				inputStream.close();
				this.field_2629 = this.field_2634.size() * 12;
			} catch (Exception var14) {
				LOGGER.error("Couldn't load credits", (Throwable)var14);
			} finally {
				IOUtils.closeQuietly(resource);
			}
		}
	}

	private void method_2258(int i, int j, float f) {
		this.minecraft.method_1531().bindTexture(DrawableHelper.BACKGROUND_LOCATION);
		int k = this.width;
		float g = -this.field_2628 * 0.5F * this.field_2635;
		float h = (float)this.height - this.field_2628 * 0.5F * this.field_2635;
		float l = 0.015625F;
		float m = this.field_2628 * 0.02F;
		float n = (float)(this.field_2629 + this.height + this.height + 24) / this.field_2635;
		float o = (n - 20.0F - this.field_2628) * 0.005F;
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
		bufferBuilder.method_1328(7, VertexFormats.field_1575);
		bufferBuilder.vertex(0.0, (double)this.height, (double)this.blitOffset).texture(0.0, (double)(g * 0.015625F)).color(m, m, m, 1.0F).next();
		bufferBuilder.vertex((double)k, (double)this.height, (double)this.blitOffset)
			.texture((double)((float)k * 0.015625F), (double)(g * 0.015625F))
			.color(m, m, m, 1.0F)
			.next();
		bufferBuilder.vertex((double)k, 0.0, (double)this.blitOffset).texture((double)((float)k * 0.015625F), (double)(h * 0.015625F)).color(m, m, m, 1.0F).next();
		bufferBuilder.vertex(0.0, 0.0, (double)this.blitOffset).texture(0.0, (double)(h * 0.015625F)).color(m, m, m, 1.0F).next();
		tessellator.draw();
	}

	@Override
	public void render(int i, int j, float f) {
		this.method_2258(i, j, f);
		int k = 274;
		int l = this.width / 2 - 137;
		int m = this.height + 50;
		this.field_2628 += f;
		float g = -this.field_2628 * this.field_2635;
		GlStateManager.pushMatrix();
		GlStateManager.translatef(0.0F, g, 0.0F);
		this.minecraft.method_1531().bindTexture(MINECRAFT_TITLE_TEXTURE);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableAlphaTest();
		this.blit(l, m, 0, 0, 155, 44);
		this.blit(l + 155, m, 0, 45, 155, 44);
		this.minecraft.method_1531().bindTexture(EDITION_TITLE_TEXTURE);
		blit(l + 88, m + 37, 0.0F, 0.0F, 98, 14, 128, 16);
		GlStateManager.disableAlphaTest();
		int n = m + 100;

		for (int o = 0; o < this.field_2634.size(); o++) {
			if (o == this.field_2634.size() - 1) {
				float h = (float)n + g - (float)(this.height / 2 - 6);
				if (h < 0.0F) {
					GlStateManager.translatef(0.0F, -h, 0.0F);
				}
			}

			if ((float)n + g + 12.0F + 8.0F > 0.0F && (float)n + g < (float)this.height) {
				String string = (String)this.field_2634.get(o);
				if (string.startsWith("[C]")) {
					this.font.drawWithShadow(string.substring(3), (float)(l + (274 - this.font.getStringWidth(string.substring(3))) / 2), (float)n, 16777215);
				} else {
					this.font.random.setSeed((long)((float)((long)o * 4238972211L) + this.field_2628 / 4.0F));
					this.font.drawWithShadow(string, (float)l, (float)n, 16777215);
				}
			}

			n += 12;
		}

		GlStateManager.popMatrix();
		this.minecraft.method_1531().bindTexture(VIGNETTE_TEXTURE);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
		int o = this.width;
		int p = this.height;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.method_1328(7, VertexFormats.field_1575);
		bufferBuilder.vertex(0.0, (double)p, (double)this.blitOffset).texture(0.0, 1.0).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		bufferBuilder.vertex((double)o, (double)p, (double)this.blitOffset).texture(1.0, 1.0).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		bufferBuilder.vertex((double)o, 0.0, (double)this.blitOffset).texture(1.0, 0.0).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		bufferBuilder.vertex(0.0, 0.0, (double)this.blitOffset).texture(0.0, 0.0).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		tessellator.draw();
		GlStateManager.disableBlend();
		super.render(i, j, f);
	}
}
