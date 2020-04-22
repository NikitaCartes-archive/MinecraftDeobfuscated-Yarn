package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Matrix4f;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class Screen extends AbstractParentElement implements Drawable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Set<String> ALLOWED_PROTOCOLS = Sets.<String>newHashSet("http", "https");
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
		if (keyCode == 256 && this.shouldCloseOnEsc()) {
			this.onClose();
			return true;
		} else if (keyCode == 258) {
			boolean bl = !hasShiftDown();
			if (!this.changeFocus(bl)) {
				this.changeFocus(bl);
			}

			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	public boolean shouldCloseOnEsc() {
		return true;
	}

	public void onClose() {
		this.client.openScreen(null);
	}

	protected <T extends AbstractButtonWidget> T addButton(T button) {
		this.buttons.add(button);
		return this.addChild(button);
	}

	protected <T extends Element> T addChild(T element) {
		this.children.add(element);
		return element;
	}

	protected void renderTooltip(MatrixStack matrixStack, ItemStack itemStack, int i, int j) {
		this.renderTooltip(matrixStack, this.getTooltipFromItem(itemStack), i, j);
	}

	public List<Text> getTooltipFromItem(ItemStack stack) {
		return stack.getTooltip(this.client.player, this.client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
	}

	public void renderTooltip(MatrixStack matrixStack, Text text, int i, int j) {
		this.renderTooltip(matrixStack, Arrays.asList(text), i, j);
	}

	public void renderTooltip(MatrixStack matrixStack, List<Text> list, int i, int j) {
		if (!list.isEmpty()) {
			RenderSystem.disableRescaleNormal();
			RenderSystem.disableDepthTest();
			int k = 0;

			for (Text text : list) {
				int l = this.textRenderer.getWidth(text);
				if (l > k) {
					k = l;
				}
			}

			int m = i + 12;
			int n = j - 12;
			int o = 8;
			if (list.size() > 1) {
				o += 2 + (list.size() - 1) * 10;
			}

			if (m + k > this.width) {
				m -= 28 + k;
			}

			if (n + o + 6 > this.height) {
				n = this.height - o - 6;
			}

			this.setZOffset(300);
			this.itemRenderer.zOffset = 300.0F;
			int p = -267386864;
			this.fillGradient(matrixStack, m - 3, n - 4, m + k + 3, n - 3, -267386864, -267386864);
			this.fillGradient(matrixStack, m - 3, n + o + 3, m + k + 3, n + o + 4, -267386864, -267386864);
			this.fillGradient(matrixStack, m - 3, n - 3, m + k + 3, n + o + 3, -267386864, -267386864);
			this.fillGradient(matrixStack, m - 4, n - 3, m - 3, n + o + 3, -267386864, -267386864);
			this.fillGradient(matrixStack, m + k + 3, n - 3, m + k + 4, n + o + 3, -267386864, -267386864);
			int q = 1347420415;
			int r = 1344798847;
			this.fillGradient(matrixStack, m - 3, n - 3 + 1, m - 3 + 1, n + o + 3 - 1, 1347420415, 1344798847);
			this.fillGradient(matrixStack, m + k + 2, n - 3 + 1, m + k + 3, n + o + 3 - 1, 1347420415, 1344798847);
			this.fillGradient(matrixStack, m - 3, n - 3, m + k + 3, n - 3 + 1, 1347420415, 1347420415);
			this.fillGradient(matrixStack, m - 3, n + o + 2, m + k + 3, n + o + 3, 1344798847, 1344798847);
			VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
			matrixStack.translate(0.0, 0.0, (double)this.itemRenderer.zOffset);
			Matrix4f matrix4f = matrixStack.peek().getModel();

			for (int s = 0; s < list.size(); s++) {
				Text text2 = (Text)list.get(s);
				if (text2 != null) {
					this.textRenderer.draw(text2, (float)m, (float)n, -1, true, matrix4f, immediate, false, 0, 15728880);
				}

				if (s == 0) {
					n += 2;
				}

				n += 10;
			}

			immediate.draw();
			this.setZOffset(0);
			this.itemRenderer.zOffset = 0.0F;
			RenderSystem.enableDepthTest();
			RenderSystem.enableRescaleNormal();
		}
	}

	protected void renderTextHoverEffect(MatrixStack matrixStack, @Nullable Text text, int i, int j) {
		if (text != null && text.getStyle().getHoverEvent() != null) {
			HoverEvent hoverEvent = text.getStyle().getHoverEvent();
			HoverEvent.ItemStackContent itemStackContent = hoverEvent.getValue(HoverEvent.Action.SHOW_ITEM);
			if (itemStackContent != null) {
				this.renderTooltip(matrixStack, itemStackContent.asStack(), i, j);
			} else {
				HoverEvent.EntityContent entityContent = hoverEvent.getValue(HoverEvent.Action.SHOW_ENTITY);
				if (entityContent != null) {
					if (this.client.options.advancedItemTooltips) {
						this.renderTooltip(matrixStack, entityContent.asTooltip(), i, j);
					}
				} else {
					Text text2 = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);
					if (text2 != null) {
						this.renderTooltip(matrixStack, this.client.textRenderer.wrapLines(text2, Math.max(this.width / 2, 200)), i, j);
					}
				}
			}
		}
	}

	protected void insertText(String text, boolean override) {
	}

	public boolean handleTextClick(Text text) {
		if (text == null) {
			return false;
		} else {
			ClickEvent clickEvent = text.getStyle().getClickEvent();
			if (hasShiftDown()) {
				if (text.getStyle().getInsertion() != null) {
					this.insertText(text.getStyle().getInsertion(), false);
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

	protected void init() {
	}

	public void tick() {
	}

	public void removed() {
	}

	public void renderBackground(MatrixStack matrixStack) {
		this.renderBackground(matrixStack, 0);
	}

	public void renderBackground(MatrixStack matrixStack, int i) {
		if (this.client.world != null) {
			this.fillGradient(matrixStack, 0, 0, this.width, this.height, -1072689136, -804253680);
		} else {
			this.renderDirtBackground(i);
		}
	}

	public void renderDirtBackground(int alpha) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		this.client.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(0.0, (double)this.height, 0.0).texture(0.0F, (float)this.height / 32.0F + (float)alpha).color(64, 64, 64, 255).next();
		bufferBuilder.vertex((double)this.width, (double)this.height, 0.0)
			.texture((float)this.width / 32.0F, (float)this.height / 32.0F + (float)alpha)
			.color(64, 64, 64, 255)
			.next();
		bufferBuilder.vertex((double)this.width, 0.0, 0.0).texture((float)this.width / 32.0F, (float)alpha).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(0.0, 0.0, 0.0).texture(0.0F, (float)alpha).color(64, 64, 64, 255).next();
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
			: InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 341)
				|| InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 345);
	}

	public static boolean hasShiftDown() {
		return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 340)
			|| InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 344);
	}

	public static boolean hasAltDown() {
		return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 342)
			|| InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 346);
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
}
