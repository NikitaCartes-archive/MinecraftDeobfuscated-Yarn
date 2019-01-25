package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontRenderer;
import net.minecraft.client.gui.ingame.ConfirmChatLinkScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.LabelWidget;
import net.minecraft.client.item.TooltipOptions;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.YesNoCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sortme.JsonLikeTagParser;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.event.ClickEvent;
import net.minecraft.text.event.HoverEvent;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ICrashCallable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class Screen extends DrawableContainer implements YesNoCallback {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Set<String> PROTOCOLS = Sets.<String>newHashSet("http", "https");
	protected final List<GuiEventListener> listeners = Lists.<GuiEventListener>newArrayList();
	public MinecraftClient client;
	public ItemRenderer itemRenderer;
	public int width;
	public int height;
	protected final List<ButtonWidget> buttons = Lists.<ButtonWidget>newArrayList();
	protected final List<LabelWidget> labelWidgets = Lists.<LabelWidget>newArrayList();
	public boolean field_2558;
	public FontRenderer fontRenderer;
	private URI uri;

	public void draw(int i, int j, float f) {
		for (int k = 0; k < this.buttons.size(); k++) {
			((ButtonWidget)this.buttons.get(k)).draw(i, j, f);
		}

		for (int k = 0; k < this.labelWidgets.size(); k++) {
			((LabelWidget)this.labelWidgets.get(k)).draw(i, j, f);
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256 && this.doesEscapeKeyClose()) {
			this.close();
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	public boolean doesEscapeKeyClose() {
		return true;
	}

	public void close() {
		this.client.openScreen(null);
	}

	protected <T extends ButtonWidget> T addButton(T buttonWidget) {
		this.buttons.add(buttonWidget);
		this.listeners.add(buttonWidget);
		return buttonWidget;
	}

	protected void drawStackTooltip(ItemStack itemStack, int i, int j) {
		this.drawTooltip(this.getStackTooltip(itemStack), i, j);
	}

	public List<String> getStackTooltip(ItemStack itemStack) {
		List<TextComponent> list = itemStack.getTooltipText(
			this.client.player, this.client.options.advancedItemTooltips ? TooltipOptions.Instance.ADVANCED : TooltipOptions.Instance.NORMAL
		);
		List<String> list2 = Lists.<String>newArrayList();

		for (TextComponent textComponent : list) {
			list2.add(textComponent.getFormattedText());
		}

		return list2;
	}

	public void drawTooltip(String string, int i, int j) {
		this.drawTooltip(Arrays.asList(string), i, j);
	}

	public void drawTooltip(List<String> list, int i, int j) {
		if (!list.isEmpty()) {
			GlStateManager.disableRescaleNormal();
			GuiLighting.disable();
			GlStateManager.disableLighting();
			GlStateManager.disableDepthTest();
			int k = 0;

			for (String string : list) {
				int l = this.fontRenderer.getStringWidth(string);
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

			this.zOffset = 300.0F;
			this.itemRenderer.zOffset = 300.0F;
			int p = -267386864;
			this.drawGradientRect(m - 3, n - 4, m + k + 3, n - 3, -267386864, -267386864);
			this.drawGradientRect(m - 3, n + o + 3, m + k + 3, n + o + 4, -267386864, -267386864);
			this.drawGradientRect(m - 3, n - 3, m + k + 3, n + o + 3, -267386864, -267386864);
			this.drawGradientRect(m - 4, n - 3, m - 3, n + o + 3, -267386864, -267386864);
			this.drawGradientRect(m + k + 3, n - 3, m + k + 4, n + o + 3, -267386864, -267386864);
			int q = 1347420415;
			int r = 1344798847;
			this.drawGradientRect(m - 3, n - 3 + 1, m - 3 + 1, n + o + 3 - 1, 1347420415, 1344798847);
			this.drawGradientRect(m + k + 2, n - 3 + 1, m + k + 3, n + o + 3 - 1, 1347420415, 1344798847);
			this.drawGradientRect(m - 3, n - 3, m + k + 3, n - 3 + 1, 1347420415, 1347420415);
			this.drawGradientRect(m - 3, n + o + 2, m + k + 3, n + o + 3, 1344798847, 1344798847);

			for (int s = 0; s < list.size(); s++) {
				String string2 = (String)list.get(s);
				this.fontRenderer.drawWithShadow(string2, (float)m, (float)n, -1);
				if (s == 0) {
					n += 2;
				}

				n += 10;
			}

			this.zOffset = 0.0F;
			this.itemRenderer.zOffset = 0.0F;
			GlStateManager.enableLighting();
			GlStateManager.enableDepthTest();
			GuiLighting.enable();
			GlStateManager.enableRescaleNormal();
		}
	}

	protected void drawTextComponentHover(TextComponent textComponent, int i, int j) {
		if (textComponent != null && textComponent.getStyle().getHoverEvent() != null) {
			HoverEvent hoverEvent = textComponent.getStyle().getHoverEvent();
			if (hoverEvent.getAction() == HoverEvent.Action.SHOW_ITEM) {
				ItemStack itemStack = ItemStack.EMPTY;

				try {
					Tag tag = JsonLikeTagParser.parse(hoverEvent.getValue().getString());
					if (tag instanceof CompoundTag) {
						itemStack = ItemStack.fromTag((CompoundTag)tag);
					}
				} catch (CommandSyntaxException var10) {
				}

				if (itemStack.isEmpty()) {
					this.drawTooltip(TextFormat.RED + "Invalid Item!", i, j);
				} else {
					this.drawStackTooltip(itemStack, i, j);
				}
			} else if (hoverEvent.getAction() == HoverEvent.Action.SHOW_ENTITY) {
				if (this.client.options.advancedItemTooltips) {
					try {
						CompoundTag compoundTag = JsonLikeTagParser.parse(hoverEvent.getValue().getString());
						List<String> list = Lists.<String>newArrayList();
						TextComponent textComponent2 = TextComponent.Serializer.fromJsonString(compoundTag.getString("name"));
						if (textComponent2 != null) {
							list.add(textComponent2.getFormattedText());
						}

						if (compoundTag.containsKey("type", 8)) {
							String string = compoundTag.getString("type");
							list.add("Type: " + string);
						}

						list.add(compoundTag.getString("id"));
						this.drawTooltip(list, i, j);
					} catch (CommandSyntaxException | JsonSyntaxException var9) {
						this.drawTooltip(TextFormat.RED + "Invalid Entity!", i, j);
					}
				}
			} else if (hoverEvent.getAction() == HoverEvent.Action.SHOW_TEXT) {
				this.drawTooltip(this.client.fontRenderer.wrapStringToWidthAsList(hoverEvent.getValue().getFormattedText(), Math.max(this.width / 2, 200)), i, j);
			}

			GlStateManager.disableLighting();
		}
	}

	protected void method_2237(String string, boolean bl) {
	}

	public boolean handleTextComponentClick(TextComponent textComponent) {
		if (textComponent == null) {
			return false;
		} else {
			ClickEvent clickEvent = textComponent.getStyle().getClickEvent();
			if (isShiftPressed()) {
				if (textComponent.getStyle().getInsertion() != null) {
					this.method_2237(textComponent.getStyle().getInsertion(), false);
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

						if (!PROTOCOLS.contains(string.toLowerCase(Locale.ROOT))) {
							throw new URISyntaxException(clickEvent.getValue(), "Unsupported protocol: " + string.toLowerCase(Locale.ROOT));
						}

						if (this.client.options.chatLinksPrompt) {
							this.uri = uRI;
							this.client.openScreen(new ConfirmChatLinkScreen(this, clickEvent.getValue(), 31102009, false));
						} else {
							this.openUri(uRI);
						}
					} catch (URISyntaxException var5) {
						LOGGER.error("Can't open url for {}", clickEvent, var5);
					}
				} else if (clickEvent.getAction() == ClickEvent.Action.OPEN_FILE) {
					URI uRIx = new File(clickEvent.getValue()).toURI();
					this.openUri(uRIx);
				} else if (clickEvent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
					this.method_2237(clickEvent.getValue(), true);
				} else if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
					this.method_2213(clickEvent.getValue(), false);
				} else {
					LOGGER.error("Don't know how to handle {}", clickEvent);
				}

				return true;
			}

			return false;
		}
	}

	public void method_2230(String string) {
		this.method_2213(string, true);
	}

	public void method_2213(String string, boolean bl) {
		if (bl) {
			this.client.inGameHud.getHudChat().method_1803(string);
		}

		this.client.player.sendChatMessage(string);
	}

	public void initialize(MinecraftClient minecraftClient, int i, int j) {
		this.client = minecraftClient;
		this.itemRenderer = minecraftClient.getItemRenderer();
		this.fontRenderer = minecraftClient.fontRenderer;
		this.width = i;
		this.height = j;
		this.buttons.clear();
		this.listeners.clear();
		this.onInitialized();
	}

	@Override
	public List<? extends GuiEventListener> getEntries() {
		return this.listeners;
	}

	protected void onInitialized() {
		this.listeners.addAll(this.labelWidgets);
	}

	public void update() {
	}

	public void onClosed() {
	}

	public void drawBackground() {
		this.drawBackground(0);
	}

	public void drawBackground(int i) {
		if (this.client.world != null) {
			this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
		} else {
			this.drawTextureBackground(i);
		}
	}

	public void drawTextureBackground(int i) {
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		this.client.getTextureManager().bindTexture(OPTIONS_BG);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
		bufferBuilder.vertex(0.0, (double)this.height, 0.0).texture(0.0, (double)((float)this.height / 32.0F + (float)i)).color(64, 64, 64, 255).next();
		bufferBuilder.vertex((double)this.width, (double)this.height, 0.0)
			.texture((double)((float)this.width / 32.0F), (double)((float)this.height / 32.0F + (float)i))
			.color(64, 64, 64, 255)
			.next();
		bufferBuilder.vertex((double)this.width, 0.0, 0.0).texture((double)((float)this.width / 32.0F), (double)i).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(0.0, 0.0, 0.0).texture(0.0, (double)i).color(64, 64, 64, 255).next();
		tessellator.draw();
	}

	public boolean isPauseScreen() {
		return true;
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		if (i == 31102009) {
			if (bl) {
				this.openUri(this.uri);
			}

			this.uri = null;
			this.client.openScreen(this);
		}
	}

	private void openUri(URI uRI) {
		SystemUtil.getOperatingSystem().open(uRI);
	}

	public static boolean isControlPressed() {
		return MinecraftClient.isSystemMac
			? InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 343)
				|| InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 347)
			: InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 341)
				|| InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 345);
	}

	public static boolean isShiftPressed() {
		return InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 340)
			|| InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 344);
	}

	public static boolean isAltPressed() {
		return InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 342)
			|| InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 346);
	}

	public static boolean isCutShortcutPressed(int i) {
		return i == 88 && isControlPressed() && !isShiftPressed() && !isAltPressed();
	}

	public static boolean isPasteShortcutPressed(int i) {
		return i == 86 && isControlPressed() && !isShiftPressed() && !isAltPressed();
	}

	public static boolean isCopyShortcutPressed(int i) {
		return i == 67 && isControlPressed() && !isShiftPressed() && !isAltPressed();
	}

	public static boolean isSelectAllShortcutPressed(int i) {
		return i == 65 && isControlPressed() && !isShiftPressed() && !isAltPressed();
	}

	public void onScaleChanged(MinecraftClient minecraftClient, int i, int j) {
		this.initialize(minecraftClient, i, j);
	}

	public static void method_2217(Runnable runnable, String string, String string2) {
		try {
			runnable.run();
		} catch (Throwable var6) {
			CrashReport crashReport = CrashReport.create(var6, string);
			CrashReportSection crashReportSection = crashReport.addElement("Affected screen");
			crashReportSection.add("Screen name", (ICrashCallable<String>)(() -> string2));
			throw new CrashException(crashReport);
		}
	}

	protected boolean method_16016(String string, char c, int i) {
		int j = string.indexOf(58);
		int k = string.indexOf(47);
		if (c == ':') {
			return (k == -1 || i <= k) && j == -1;
		} else {
			return c == '/' ? i > j : c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '.';
		}
	}
}
