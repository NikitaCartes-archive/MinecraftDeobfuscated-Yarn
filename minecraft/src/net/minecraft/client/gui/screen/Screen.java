package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.Tag;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class Screen extends AbstractParentElement implements Drawable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Set<String> ALLOWED_PROTOCOLS = Sets.<String>newHashSet("http", "https");
	protected final Text title;
	protected final List<Element> children = Lists.<Element>newArrayList();
	@Nullable
	protected MinecraftClient minecraft;
	protected ItemRenderer itemRenderer;
	public int width;
	public int height;
	protected final List<AbstractButtonWidget> buttons = Lists.<AbstractButtonWidget>newArrayList();
	public boolean passEvents;
	public TextRenderer font;
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
	public void render(int mouseX, int mouseY, float delta) {
		for (int i = 0; i < this.buttons.size(); i++) {
			((AbstractButtonWidget)this.buttons.get(i)).render(mouseX, mouseY, delta);
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
		this.minecraft.openScreen(null);
	}

	protected <T extends AbstractButtonWidget> T addButton(T button) {
		this.buttons.add(button);
		this.children.add(button);
		return button;
	}

	protected void renderTooltip(ItemStack stack, int x, int y) {
		this.renderTooltip(this.getTooltipFromItem(stack), x, y);
	}

	public List<String> getTooltipFromItem(ItemStack stack) {
		List<Text> list = stack.getTooltip(
			this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL
		);
		List<String> list2 = Lists.<String>newArrayList();

		for (Text text : list) {
			list2.add(text.asFormattedString());
		}

		return list2;
	}

	public void renderTooltip(String text, int x, int y) {
		this.renderTooltip(Arrays.asList(text), x, y);
	}

	public void renderTooltip(List<String> text, int x, int y) {
		if (!text.isEmpty()) {
			RenderSystem.disableRescaleNormal();
			RenderSystem.disableDepthTest();
			int i = 0;

			for (String string : text) {
				int j = this.font.getStringWidth(string);
				if (j > i) {
					i = j;
				}
			}

			int k = x + 12;
			int l = y - 12;
			int m = 8;
			if (text.size() > 1) {
				m += 2 + (text.size() - 1) * 10;
			}

			if (k + i > this.width) {
				k -= 28 + i;
			}

			if (l + m + 6 > this.height) {
				l = this.height - m - 6;
			}

			this.setBlitOffset(300);
			this.itemRenderer.zOffset = 300.0F;
			int n = -267386864;
			this.fillGradient(k - 3, l - 4, k + i + 3, l - 3, -267386864, -267386864);
			this.fillGradient(k - 3, l + m + 3, k + i + 3, l + m + 4, -267386864, -267386864);
			this.fillGradient(k - 3, l - 3, k + i + 3, l + m + 3, -267386864, -267386864);
			this.fillGradient(k - 4, l - 3, k - 3, l + m + 3, -267386864, -267386864);
			this.fillGradient(k + i + 3, l - 3, k + i + 4, l + m + 3, -267386864, -267386864);
			int o = 1347420415;
			int p = 1344798847;
			this.fillGradient(k - 3, l - 3 + 1, k - 3 + 1, l + m + 3 - 1, 1347420415, 1344798847);
			this.fillGradient(k + i + 2, l - 3 + 1, k + i + 3, l + m + 3 - 1, 1347420415, 1344798847);
			this.fillGradient(k - 3, l - 3, k + i + 3, l - 3 + 1, 1347420415, 1347420415);
			this.fillGradient(k - 3, l + m + 2, k + i + 3, l + m + 3, 1344798847, 1344798847);
			MatrixStack matrixStack = new MatrixStack();
			VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
			matrixStack.translate(0.0, 0.0, (double)this.itemRenderer.zOffset);
			Matrix4f matrix4f = matrixStack.peekModel();

			for (int q = 0; q < text.size(); q++) {
				String string2 = (String)text.get(q);
				if (string2 != null) {
					this.font.draw(string2, (float)k, (float)l, -1, true, matrix4f, immediate, false, 0, 15728880);
				}

				if (q == 0) {
					l += 2;
				}

				l += 10;
			}

			immediate.draw();
			this.setBlitOffset(0);
			this.itemRenderer.zOffset = 0.0F;
			RenderSystem.enableDepthTest();
			RenderSystem.enableRescaleNormal();
		}
	}

	protected void renderComponentHoverEffect(Text component, int x, int y) {
		if (component != null && component.getStyle().getHoverEvent() != null) {
			HoverEvent hoverEvent = component.getStyle().getHoverEvent();
			if (hoverEvent.getAction() == HoverEvent.Action.SHOW_ITEM) {
				ItemStack itemStack = ItemStack.EMPTY;

				try {
					Tag tag = StringNbtReader.parse(hoverEvent.getValue().getString());
					if (tag instanceof CompoundTag) {
						itemStack = ItemStack.fromTag((CompoundTag)tag);
					}
				} catch (CommandSyntaxException var10) {
				}

				if (itemStack.isEmpty()) {
					this.renderTooltip(Formatting.RED + "Invalid Item!", x, y);
				} else {
					this.renderTooltip(itemStack, x, y);
				}
			} else if (hoverEvent.getAction() == HoverEvent.Action.SHOW_ENTITY) {
				if (this.minecraft.options.advancedItemTooltips) {
					try {
						CompoundTag compoundTag = StringNbtReader.parse(hoverEvent.getValue().getString());
						List<String> list = Lists.<String>newArrayList();
						Text text = Text.Serializer.fromJson(compoundTag.getString("name"));
						if (text != null) {
							list.add(text.asFormattedString());
						}

						if (compoundTag.contains("type", 8)) {
							String string = compoundTag.getString("type");
							list.add("Type: " + string);
						}

						list.add(compoundTag.getString("id"));
						this.renderTooltip(list, x, y);
					} catch (CommandSyntaxException | JsonSyntaxException var9) {
						this.renderTooltip(Formatting.RED + "Invalid Entity!", x, y);
					}
				}
			} else if (hoverEvent.getAction() == HoverEvent.Action.SHOW_TEXT) {
				this.renderTooltip(this.minecraft.textRenderer.wrapStringToWidthAsList(hoverEvent.getValue().asFormattedString(), Math.max(this.width / 2, 200)), x, y);
			}
		}
	}

	protected void insertText(String text, boolean bl) {
	}

	public boolean handleComponentClicked(Text text) {
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
					if (!this.minecraft.options.chatLinks) {
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

						if (this.minecraft.options.chatLinksPrompt) {
							this.clickedLink = uRI;
							this.minecraft.openScreen(new ConfirmChatLinkScreen(this::confirmLink, clickEvent.getValue(), false));
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
					this.minecraft.keyboard.setClipboard(clickEvent.getValue());
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
			this.minecraft.inGameHud.getChatHud().addToMessageHistory(message);
		}

		this.minecraft.player.sendChatMessage(message);
	}

	public void init(MinecraftClient client, int width, int height) {
		this.minecraft = client;
		this.itemRenderer = client.getItemRenderer();
		this.font = client.textRenderer;
		this.width = width;
		this.height = height;
		this.buttons.clear();
		this.children.clear();
		this.setFocused(null);
		this.init();
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
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

	public void renderBackground() {
		this.renderBackground(0);
	}

	public void renderBackground(int alpha) {
		if (this.minecraft.world != null) {
			this.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
		} else {
			this.renderDirtBackground(alpha);
		}
	}

	public void renderDirtBackground(int alpha) {
		RenderSystem.disableFog();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		this.minecraft.getTextureManager().bindTexture(BACKGROUND_LOCATION);
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
		this.minecraft.openScreen(this);
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
