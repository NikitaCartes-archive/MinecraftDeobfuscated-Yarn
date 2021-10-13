package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.Resource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class CreditsScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier MINECRAFT_TITLE_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
	private static final Identifier EDITION_TITLE_TEXTURE = new Identifier("textures/gui/title/edition.png");
	private static final Identifier VIGNETTE_TEXTURE = new Identifier("textures/misc/vignette.png");
	private static final Text SEPARATOR_LINE = new LiteralText("============").formatted(Formatting.WHITE);
	private static final String CENTERED_LINE_PREFIX = "           ";
	private static final String OBFUSCATION_PLACEHOLDER = "" + Formatting.WHITE + Formatting.OBFUSCATED + Formatting.GREEN + Formatting.AQUA;
	private static final int MAX_WIDTH = 274;
	private static final float SPACE_BAR_SPEED_MULTIPLIER = 5.0F;
	private static final float CTRL_KEY_SPEED_MULTIPLIER = 15.0F;
	private final boolean endCredits;
	private final Runnable finishAction;
	private float time;
	private List<OrderedText> credits;
	private IntSet centeredLines;
	private int creditsHeight;
	private boolean spaceKeyPressed;
	private final IntSet pressedCtrlKeys = new IntOpenHashSet();
	private float speed;
	private final float baseSpeed;

	public CreditsScreen(boolean endCredits, Runnable finishAction) {
		super(NarratorManager.EMPTY);
		this.endCredits = endCredits;
		this.finishAction = finishAction;
		if (!endCredits) {
			this.baseSpeed = 0.75F;
		} else {
			this.baseSpeed = 0.5F;
		}

		this.speed = this.baseSpeed;
	}

	private float getSpeed() {
		return this.spaceKeyPressed ? this.baseSpeed * (5.0F + (float)this.pressedCtrlKeys.size() * 15.0F) : this.baseSpeed;
	}

	@Override
	public void tick() {
		this.client.getMusicTracker().tick();
		this.client.getSoundManager().tick(false);
		float f = (float)(this.creditsHeight + this.height + this.height + 24);
		if (this.time > f) {
			this.close();
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL) {
			this.pressedCtrlKeys.add(keyCode);
		} else if (keyCode == GLFW.GLFW_KEY_SPACE) {
			this.spaceKeyPressed = true;
		}

		this.speed = this.getSpeed();
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_SPACE) {
			this.spaceKeyPressed = false;
		} else if (keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL) {
			this.pressedCtrlKeys.remove(keyCode);
		}

		this.speed = this.getSpeed();
		return super.keyReleased(keyCode, scanCode, modifiers);
	}

	@Override
	public void onClose() {
		this.close();
	}

	private void close() {
		this.finishAction.run();
		this.client.setScreen(null);
	}

	@Override
	protected void init() {
		if (this.credits == null) {
			this.credits = Lists.<OrderedText>newArrayList();
			this.centeredLines = new IntOpenHashSet();
			Resource resource = null;

			try {
				if (this.endCredits) {
					resource = this.client.getResourceManager().getResource(new Identifier("texts/end.txt"));
					InputStream inputStream = resource.getInputStream();
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
					Random random = new Random(8124371L);

					String string;
					while ((string = bufferedReader.readLine()) != null) {
						string = string.replaceAll("PLAYERNAME", this.client.getSession().getUsername());

						int i;
						while ((i = string.indexOf(OBFUSCATION_PLACEHOLDER)) != -1) {
							String string2 = string.substring(0, i);
							String string3 = string.substring(i + OBFUSCATION_PLACEHOLDER.length());
							string = string2 + Formatting.WHITE + Formatting.OBFUSCATED + "XXXXXXXX".substring(0, random.nextInt(4) + 3) + string3;
						}

						this.addText(string);
						this.addEmptyLine();
					}

					inputStream.close();

					for (int i = 0; i < 8; i++) {
						this.addEmptyLine();
					}
				}

				resource = this.client.getResourceManager().getResource(new Identifier("texts/credits.json"));
				JsonArray jsonArray = JsonHelper.deserializeArray(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

				for (JsonElement jsonElement : jsonArray.getAsJsonArray()) {
					JsonObject jsonObject = jsonElement.getAsJsonObject();
					String string2 = jsonObject.get("section").getAsString();
					this.addText(SEPARATOR_LINE, true);
					this.addText(new LiteralText(string2).formatted(Formatting.YELLOW), true);
					this.addText(SEPARATOR_LINE, true);
					this.addEmptyLine();
					this.addEmptyLine();

					for (JsonElement jsonElement2 : jsonObject.getAsJsonArray("titles")) {
						JsonObject jsonObject2 = jsonElement2.getAsJsonObject();
						String string4 = jsonObject2.get("title").getAsString();
						JsonArray jsonArray4 = jsonObject2.getAsJsonArray("names");
						this.addText(new LiteralText(string4).formatted(Formatting.GRAY), false);

						for (JsonElement jsonElement3 : jsonArray4) {
							String string5 = jsonElement3.getAsString();
							this.addText(new LiteralText("           ").append(string5).formatted(Formatting.WHITE), false);
						}

						this.addEmptyLine();
						this.addEmptyLine();
					}
				}

				this.creditsHeight = this.credits.size() * 12;
			} catch (Exception var20) {
				LOGGER.error("Couldn't load credits", (Throwable)var20);
			} finally {
				IOUtils.closeQuietly(resource);
			}
		}
	}

	private void addEmptyLine() {
		this.credits.add(OrderedText.EMPTY);
	}

	private void addText(String text) {
		this.credits.addAll(this.client.textRenderer.wrapLines(new LiteralText(text), 274));
	}

	private void addText(Text text, boolean centered) {
		if (centered) {
			this.centeredLines.add(this.credits.size());
		}

		this.credits.add(text.asOrderedText());
	}

	private void renderBackground() {
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
		int i = this.width;
		float f = -this.time * 0.5F;
		float g = (float)this.height - 0.5F * this.time;
		float h = 0.015625F;
		float j = this.time / this.baseSpeed;
		float k = j * 0.02F;
		float l = (float)(this.creditsHeight + this.height + this.height + 24) / this.baseSpeed;
		float m = (l - 20.0F - j) * 0.005F;
		if (m < k) {
			k = m;
		}

		if (k > 1.0F) {
			k = 1.0F;
		}

		k *= k;
		k = k * 96.0F / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(0.0, (double)this.height, (double)this.getZOffset()).texture(0.0F, f * 0.015625F).color(k, k, k, 1.0F).next();
		bufferBuilder.vertex((double)i, (double)this.height, (double)this.getZOffset()).texture((float)i * 0.015625F, f * 0.015625F).color(k, k, k, 1.0F).next();
		bufferBuilder.vertex((double)i, 0.0, (double)this.getZOffset()).texture((float)i * 0.015625F, g * 0.015625F).color(k, k, k, 1.0F).next();
		bufferBuilder.vertex(0.0, 0.0, (double)this.getZOffset()).texture(0.0F, g * 0.015625F).color(k, k, k, 1.0F).next();
		tessellator.draw();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.time = this.time + delta * this.speed;
		this.renderBackground();
		int i = this.width / 2 - 137;
		int j = this.height + 50;
		float f = -this.time;
		matrices.push();
		matrices.translate(0.0, (double)f, 0.0);
		RenderSystem.setShaderTexture(0, MINECRAFT_TITLE_TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableBlend();
		this.drawWithOutline(i, j, (x, y) -> {
			this.drawTexture(matrices, x + 0, y, 0, 0, 155, 44);
			this.drawTexture(matrices, x + 155, y, 0, 45, 155, 44);
		});
		RenderSystem.disableBlend();
		RenderSystem.setShaderTexture(0, EDITION_TITLE_TEXTURE);
		drawTexture(matrices, i + 88, j + 37, 0.0F, 0.0F, 98, 14, 128, 16);
		int k = j + 100;

		for (int l = 0; l < this.credits.size(); l++) {
			if (l == this.credits.size() - 1) {
				float g = (float)k + f - (float)(this.height / 2 - 6);
				if (g < 0.0F) {
					matrices.translate(0.0, (double)(-g), 0.0);
				}
			}

			if ((float)k + f + 12.0F + 8.0F > 0.0F && (float)k + f < (float)this.height) {
				OrderedText orderedText = (OrderedText)this.credits.get(l);
				if (this.centeredLines.contains(l)) {
					this.textRenderer.drawWithShadow(matrices, orderedText, (float)(i + (274 - this.textRenderer.getWidth(orderedText)) / 2), (float)k, 16777215);
				} else {
					this.textRenderer.drawWithShadow(matrices, orderedText, (float)i, (float)k, 16777215);
				}
			}

			k += 12;
		}

		matrices.pop();
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, VIGNETTE_TEXTURE);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR);
		int l = this.width;
		int m = this.height;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(0.0, (double)m, (double)this.getZOffset()).texture(0.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		bufferBuilder.vertex((double)l, (double)m, (double)this.getZOffset()).texture(1.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		bufferBuilder.vertex((double)l, 0.0, (double)this.getZOffset()).texture(1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		bufferBuilder.vertex(0.0, 0.0, (double)this.getZOffset()).texture(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		tessellator.draw();
		RenderSystem.disableBlend();
		super.render(matrices, mouseX, mouseY, delta);
	}
}
