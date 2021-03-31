package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Matrix4f;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public abstract class Screen extends AbstractParentElement implements TickableElement, Drawable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Set<String> ALLOWED_PROTOCOLS = Sets.<String>newHashSet("http", "https");
	private static final int field_32270 = 2;
	protected final Text title;
	protected final List<Element> children = Lists.<Element>newArrayList();
	@Nullable
	protected MinecraftClient client;
	protected ItemRenderer itemRenderer;
	public int width;
	public int height;
	protected final List<AbstractButtonWidget> buttons = Lists.<AbstractButtonWidget>newArrayList();
	public boolean passEvents;
	public TextRenderer textRenderer;
	private URI clickedLink;

	protected Screen(Text title) {
		this.title = title;
	}

	public Text getTitle() {
		return this.title;
	}

	public String getNarrationMessage() {
		return this.getTitle().getString();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		for (int i = 0; i < this.buttons.size(); i++) {
			((AbstractButtonWidget)this.buttons.get(i)).render(matrices, mouseX, mouseY, delta);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE && this.shouldCloseOnEsc()) {
			this.onClose();
			return true;
		} else if (keyCode == GLFW.GLFW_KEY_TAB) {
			boolean bl = !hasShiftDown();
			if (!this.changeFocus(bl)) {
				this.changeFocus(bl);
			}

			return false;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	/**
	 * Checks whether this screen should be closed when the escape key is pressed.
	 */
	public boolean shouldCloseOnEsc() {
		return true;
	}

	public void onClose() {
		this.client.openScreen(null);
	}

	/**
	 * Adds a button to this screen.
	 * This method should be preferred over {@link Screen#addChild(Element)} since buttons are automatically rendered when added to a screen.
	 */
	protected <T extends AbstractButtonWidget> T addButton(T button) {
		this.buttons.add(button);
		return this.addChild(button);
	}

	/**
	 * Adds a child element to this screen.
	 * If the child element is an {@link net.minecraft.client.gui.widget.AbstractButtonWidget}, you should use {@link Screen#addButton(AbstractButtonWidget)} instead.
	 * 
	 * <p>Adding a child element to a screen does not guarantee the widget is rendered or ticked.
	 * @see net.minecraft.client.gui.screen.Screen#addButton(AbstractButtonWidget)
	 */
	protected <T extends Element> T addChild(T child) {
		this.children.add(child);
		return child;
	}

	protected void renderTooltip(MatrixStack matrices, ItemStack stack, int x, int y) {
		this.renderTooltip(matrices, this.getTooltipFromItem(stack), stack.getTooltipData(), x, y);
	}

	public void renderTooltip(MatrixStack matrices, List<Text> lines, Optional<TooltipData> data, int x, int y) {
		List<TooltipComponent> list = (List<TooltipComponent>)lines.stream().map(Text::asOrderedText).map(TooltipComponent::of).collect(Collectors.toList());
		data.ifPresent(tooltipData -> list.add(1, TooltipComponent.of(tooltipData)));
		this.renderTooltipFromComponents(matrices, list, x, y);
	}

	public List<Text> getTooltipFromItem(ItemStack stack) {
		return stack.getTooltip(this.client.player, this.client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
	}

	public void renderTooltip(MatrixStack matrices, Text text, int x, int y) {
		this.renderOrderedTooltip(matrices, Arrays.asList(text.asOrderedText()), x, y);
	}

	public void renderTooltip(MatrixStack matrices, List<Text> lines, int x, int y) {
		this.renderOrderedTooltip(matrices, Lists.transform(lines, Text::asOrderedText), x, y);
	}

	public void renderOrderedTooltip(MatrixStack matrices, List<? extends OrderedText> lines, int x, int y) {
		this.renderTooltipFromComponents(matrices, (List<TooltipComponent>)lines.stream().map(TooltipComponent::of).collect(Collectors.toList()), x, y);
	}

	private void renderTooltipFromComponents(MatrixStack matrices, List<TooltipComponent> components, int x, int y) {
		if (!components.isEmpty()) {
			int i = 0;
			int j = components.size() == 1 ? -2 : 0;

			for (TooltipComponent tooltipComponent : components) {
				int k = tooltipComponent.getWidth(this.textRenderer);
				if (k > i) {
					i = k;
				}

				j += tooltipComponent.getHeight();
			}

			int l = x + 12;
			int m = y - 12;
			if (l + i > this.width) {
				l -= 28 + i;
			}

			if (m + j + 6 > this.height) {
				m = this.height - j - 6;
			}

			matrices.push();
			int o = -267386864;
			int p = 1347420415;
			int q = 1344798847;
			int r = 400;
			float f = this.itemRenderer.zOffset;
			this.itemRenderer.zOffset = 400.0F;
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			Matrix4f matrix4f = matrices.peek().getModel();
			fillGradient(matrix4f, bufferBuilder, l - 3, m - 4, l + i + 3, m - 3, 400, -267386864, -267386864);
			fillGradient(matrix4f, bufferBuilder, l - 3, m + j + 3, l + i + 3, m + j + 4, 400, -267386864, -267386864);
			fillGradient(matrix4f, bufferBuilder, l - 3, m - 3, l + i + 3, m + j + 3, 400, -267386864, -267386864);
			fillGradient(matrix4f, bufferBuilder, l - 4, m - 3, l - 3, m + j + 3, 400, -267386864, -267386864);
			fillGradient(matrix4f, bufferBuilder, l + i + 3, m - 3, l + i + 4, m + j + 3, 400, -267386864, -267386864);
			fillGradient(matrix4f, bufferBuilder, l - 3, m - 3 + 1, l - 3 + 1, m + j + 3 - 1, 400, 1347420415, 1344798847);
			fillGradient(matrix4f, bufferBuilder, l + i + 2, m - 3 + 1, l + i + 3, m + j + 3 - 1, 400, 1347420415, 1344798847);
			fillGradient(matrix4f, bufferBuilder, l - 3, m - 3, l + i + 3, m - 3 + 1, 400, 1347420415, 1347420415);
			fillGradient(matrix4f, bufferBuilder, l - 3, m + j + 2, l + i + 3, m + j + 3, 400, 1344798847, 1344798847);
			RenderSystem.enableDepthTest();
			RenderSystem.disableTexture();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			bufferBuilder.end();
			BufferRenderer.draw(bufferBuilder);
			RenderSystem.disableBlend();
			RenderSystem.enableTexture();
			VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
			matrices.translate(0.0, 0.0, 400.0);
			int s = m;

			for (int t = 0; t < components.size(); t++) {
				TooltipComponent tooltipComponent2 = (TooltipComponent)components.get(t);
				tooltipComponent2.drawText(this.textRenderer, l, s, matrix4f, immediate);
				s += tooltipComponent2.getHeight() + (t == 0 ? 2 : 0);
			}

			immediate.draw();
			matrices.pop();
			s = m;

			for (int t = 0; t < components.size(); t++) {
				TooltipComponent tooltipComponent2 = (TooltipComponent)components.get(t);
				tooltipComponent2.drawItems(this.textRenderer, l, s, matrices, this.itemRenderer, 400, this.client.getTextureManager());
				s += tooltipComponent2.getHeight() + (t == 0 ? 2 : 0);
			}

			this.itemRenderer.zOffset = f;
		}
	}

	protected void renderTextHoverEffect(MatrixStack matrices, @Nullable Style style, int x, int y) {
		if (style != null && style.getHoverEvent() != null) {
			HoverEvent hoverEvent = style.getHoverEvent();
			HoverEvent.ItemStackContent itemStackContent = hoverEvent.getValue(HoverEvent.Action.SHOW_ITEM);
			if (itemStackContent != null) {
				this.renderTooltip(matrices, itemStackContent.asStack(), x, y);
			} else {
				HoverEvent.EntityContent entityContent = hoverEvent.getValue(HoverEvent.Action.SHOW_ENTITY);
				if (entityContent != null) {
					if (this.client.options.advancedItemTooltips) {
						this.renderTooltip(matrices, entityContent.asTooltip(), x, y);
					}
				} else {
					Text text = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);
					if (text != null) {
						this.renderOrderedTooltip(matrices, this.client.textRenderer.wrapLines(text, Math.max(this.width / 2, 200)), x, y);
					}
				}
			}
		}
	}

	protected void insertText(String text, boolean override) {
	}

	public boolean handleTextClick(@Nullable Style style) {
		if (style == null) {
			return false;
		} else {
			ClickEvent clickEvent = style.getClickEvent();
			if (hasShiftDown()) {
				if (style.getInsertion() != null) {
					this.insertText(style.getInsertion(), false);
				}
			} else if (clickEvent != null) {
				if (clickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
					if (!this.client.options.chatLinks) {
						return false;
					}

					try {
						URI uRI = new URI(clickEvent.getValue());
						String string = uRI.getScheme();
						if (string == null) {
							throw new URISyntaxException(clickEvent.getValue(), "Missing protocol");
						}

						if (!ALLOWED_PROTOCOLS.contains(string.toLowerCase(Locale.ROOT))) {
							throw new URISyntaxException(clickEvent.getValue(), "Unsupported protocol: " + string.toLowerCase(Locale.ROOT));
						}

						if (this.client.options.chatLinksPrompt) {
							this.clickedLink = uRI;
							this.client.openScreen(new ConfirmChatLinkScreen(this::confirmLink, clickEvent.getValue(), false));
						} else {
							this.openLink(uRI);
						}
					} catch (URISyntaxException var5) {
						LOGGER.error("Can't open url for {}", clickEvent, var5);
					}
				} else if (clickEvent.getAction() == ClickEvent.Action.OPEN_FILE) {
					URI uRIx = new File(clickEvent.getValue()).toURI();
					this.openLink(uRIx);
				} else if (clickEvent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
					this.insertText(clickEvent.getValue(), true);
				} else if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
					this.sendMessage(clickEvent.getValue(), false);
				} else if (clickEvent.getAction() == ClickEvent.Action.COPY_TO_CLIPBOARD) {
					this.client.keyboard.setClipboard(clickEvent.getValue());
				} else {
					LOGGER.error("Don't know how to handle {}", clickEvent);
				}

				return true;
			}

			return false;
		}
	}

	public void sendMessage(String message) {
		this.sendMessage(message, true);
	}

	public void sendMessage(String message, boolean toHud) {
		if (toHud) {
			this.client.inGameHud.getChatHud().addToMessageHistory(message);
		}

		this.client.player.sendChatMessage(message);
	}

	public void init(MinecraftClient client, int width, int height) {
		this.client = client;
		this.itemRenderer = client.getItemRenderer();
		this.textRenderer = client.textRenderer;
		this.width = width;
		this.height = height;
		this.buttons.clear();
		this.children.clear();
		this.setFocused(null);
		this.init();
	}

	@Override
	public List<? extends Element> children() {
		return this.children;
	}

	/**
	 * Called when a screen should be initialized.
	 * 
	 * <p>This method is called when this screen is {@linkplain net.minecraft.client.MinecraftClient#openScreen(Screen) opened} or resized.
	 */
	protected void init() {
	}

	@Override
	public void tick() {
	}

	public void removed() {
	}

	/**
	 * Renders the background of this screen.
	 * 
	 * <p>If the client is in a world, renders the translucent background gradient.
	 * Otherwise {@linkplain #renderBackgroundTexture(int) renders the background texture}.
	 */
	public void renderBackground(MatrixStack matrices) {
		this.renderBackground(matrices, 0);
	}

	/**
	 * Renders the background of this screen.
	 * 
	 * <p>If the client is in a world, renders the translucent background gradient.
	 * Otherwise {@linkplain #renderBackgroundTexture(int) renders the background texture}.
	 * 
	 * @param vOffset an offset applied to the V coordinate of the background texture
	 */
	public void renderBackground(MatrixStack matrices, int vOffset) {
		if (this.client.world != null) {
			this.fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);
		} else {
			this.renderBackgroundTexture(vOffset);
		}
	}

	/**
	 * Renders the fullscreen {@linkplain net.minecraft.client.gui.DrawableHelper#OPTIONS_BACKGROUND_TEXTURE background texture} of this screen.
	 * 
	 * @param vOffset an offset applied to the V coordinate of the background texture
	 */
	public void renderBackgroundTexture(int vOffset) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND_TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(0.0, (double)this.height, 0.0).texture(0.0F, (float)this.height / 32.0F + (float)vOffset).color(64, 64, 64, 255).next();
		bufferBuilder.vertex((double)this.width, (double)this.height, 0.0)
			.texture((float)this.width / 32.0F, (float)this.height / 32.0F + (float)vOffset)
			.color(64, 64, 64, 255)
			.next();
		bufferBuilder.vertex((double)this.width, 0.0, 0.0).texture((float)this.width / 32.0F, (float)vOffset).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(0.0, 0.0, 0.0).texture(0.0F, (float)vOffset).color(64, 64, 64, 255).next();
		tessellator.draw();
	}

	public boolean isPauseScreen() {
		return true;
	}

	private void confirmLink(boolean open) {
		if (open) {
			this.openLink(this.clickedLink);
		}

		this.clickedLink = null;
		this.client.openScreen(this);
	}

	private void openLink(URI link) {
		Util.getOperatingSystem().open(link);
	}

	public static boolean hasControlDown() {
		return MinecraftClient.IS_SYSTEM_MAC
			? InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 343)
				|| InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 347)
			: InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL)
				|| InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_CONTROL);
	}

	public static boolean hasShiftDown() {
		return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT)
			|| InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT);
	}

	public static boolean hasAltDown() {
		return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_ALT)
			|| InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_ALT);
	}

	public static boolean isCut(int code) {
		return code == 88 && hasControlDown() && !hasShiftDown() && !hasAltDown();
	}

	public static boolean isPaste(int code) {
		return code == 86 && hasControlDown() && !hasShiftDown() && !hasAltDown();
	}

	public static boolean isCopy(int code) {
		return code == 67 && hasControlDown() && !hasShiftDown() && !hasAltDown();
	}

	public static boolean isSelectAll(int code) {
		return code == 65 && hasControlDown() && !hasShiftDown() && !hasAltDown();
	}

	public void resize(MinecraftClient client, int width, int height) {
		this.init(client, width, height);
	}

	public static void wrapScreenError(Runnable task, String errorTitle, String screenName) {
		try {
			task.run();
		} catch (Throwable var6) {
			CrashReport crashReport = CrashReport.create(var6, errorTitle);
			CrashReportSection crashReportSection = crashReport.addElement("Affected screen");
			crashReportSection.add("Screen name", (CrashCallable<String>)(() -> screenName));
			throw new CrashException(crashReport);
		}
	}

	protected boolean isValidCharacterForName(String name, char character, int cursorPos) {
		int i = name.indexOf(58);
		int j = name.indexOf(47);
		if (character == ':') {
			return (j == -1 || cursorPos <= j) && i == -1;
		} else {
			return character == '/'
				? cursorPos > i
				: character == '_' || character == '-' || character >= 'a' && character <= 'z' || character >= '0' && character <= '9' || character == '.';
		}
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return true;
	}

	public void filesDragged(List<Path> paths) {
	}
}
