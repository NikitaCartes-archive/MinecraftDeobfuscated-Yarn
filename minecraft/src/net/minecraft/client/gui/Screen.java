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
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.ingame.ConfirmChatLinkScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ICrashCallable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class Screen extends AbstractParentElement implements Drawable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Set<String> ALLOWED_PROTOCOLS = Sets.<String>newHashSet("http", "https");
	protected final Component title;
	protected final List<Element> children = Lists.<Element>newArrayList();
	@Nullable
	public MinecraftClient minecraft;
	public ItemRenderer itemRenderer;
	public int width;
	public int height;
	protected final List<AbstractButtonWidget> buttons = Lists.<AbstractButtonWidget>newArrayList();
	public boolean passEvents;
	public TextRenderer font;
	private URI clickedLink;

	protected Screen(Component component) {
		this.title = component;
	}

	public Component getTitle() {
		return this.title;
	}

	public String getNarrationMessage() {
		return this.getTitle().getString();
	}

	@Override
	public void render(int i, int j, float f) {
		for (int k = 0; k < this.buttons.size(); k++) {
			((AbstractButtonWidget)this.buttons.get(k)).render(i, j, f);
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256 && this.shouldCloseOnEsc()) {
			this.onClose();
			return true;
		} else if (i == 258) {
			boolean bl = !hasShiftDown();
			if (!this.changeFocus(bl)) {
				this.changeFocus(bl);
			}

			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	public boolean shouldCloseOnEsc() {
		return true;
	}

	public void onClose() {
		this.minecraft.openScreen(null);
	}

	protected <T extends AbstractButtonWidget> T addButton(T abstractButtonWidget) {
		this.buttons.add(abstractButtonWidget);
		this.children.add(abstractButtonWidget);
		return abstractButtonWidget;
	}

	protected void renderTooltip(ItemStack itemStack, int i, int j) {
		this.renderTooltip(this.getTooltipFromItem(itemStack), i, j);
	}

	public List<String> getTooltipFromItem(ItemStack itemStack) {
		List<Component> list = itemStack.getTooltipText(
			this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipContext.Default.field_8935 : TooltipContext.Default.field_8934
		);
		List<String> list2 = Lists.<String>newArrayList();

		for (Component component : list) {
			list2.add(component.getFormattedText());
		}

		return list2;
	}

	public void renderTooltip(String string, int i, int j) {
		this.renderTooltip(Arrays.asList(string), i, j);
	}

	public void renderTooltip(List<String> list, int i, int j) {
		if (!list.isEmpty()) {
			GlStateManager.disableRescaleNormal();
			GuiLighting.disable();
			GlStateManager.disableLighting();
			GlStateManager.disableDepthTest();
			int k = 0;

			for (String string : list) {
				int l = this.font.getStringWidth(string);
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

			this.blitOffset = 300;
			this.itemRenderer.zOffset = 300.0F;
			int p = -267386864;
			this.fillGradient(m - 3, n - 4, m + k + 3, n - 3, -267386864, -267386864);
			this.fillGradient(m - 3, n + o + 3, m + k + 3, n + o + 4, -267386864, -267386864);
			this.fillGradient(m - 3, n - 3, m + k + 3, n + o + 3, -267386864, -267386864);
			this.fillGradient(m - 4, n - 3, m - 3, n + o + 3, -267386864, -267386864);
			this.fillGradient(m + k + 3, n - 3, m + k + 4, n + o + 3, -267386864, -267386864);
			int q = 1347420415;
			int r = 1344798847;
			this.fillGradient(m - 3, n - 3 + 1, m - 3 + 1, n + o + 3 - 1, 1347420415, 1344798847);
			this.fillGradient(m + k + 2, n - 3 + 1, m + k + 3, n + o + 3 - 1, 1347420415, 1344798847);
			this.fillGradient(m - 3, n - 3, m + k + 3, n - 3 + 1, 1347420415, 1347420415);
			this.fillGradient(m - 3, n + o + 2, m + k + 3, n + o + 3, 1344798847, 1344798847);

			for (int s = 0; s < list.size(); s++) {
				String string2 = (String)list.get(s);
				this.font.drawWithShadow(string2, (float)m, (float)n, -1);
				if (s == 0) {
					n += 2;
				}

				n += 10;
			}

			this.blitOffset = 0;
			this.itemRenderer.zOffset = 0.0F;
			GlStateManager.enableLighting();
			GlStateManager.enableDepthTest();
			GuiLighting.enable();
			GlStateManager.enableRescaleNormal();
		}
	}

	protected void renderComponentHoverEffect(Component component, int i, int j) {
		if (component != null && component.getStyle().getHoverEvent() != null) {
			HoverEvent hoverEvent = component.getStyle().getHoverEvent();
			if (hoverEvent.getAction() == HoverEvent.Action.field_11757) {
				ItemStack itemStack = ItemStack.EMPTY;

				try {
					Tag tag = StringNbtReader.parse(hoverEvent.getValue().getString());
					if (tag instanceof CompoundTag) {
						itemStack = ItemStack.fromTag((CompoundTag)tag);
					}
				} catch (CommandSyntaxException var10) {
				}

				if (itemStack.isEmpty()) {
					this.renderTooltip(ChatFormat.field_1061 + "Invalid Item!", i, j);
				} else {
					this.renderTooltip(itemStack, i, j);
				}
			} else if (hoverEvent.getAction() == HoverEvent.Action.field_11761) {
				if (this.minecraft.options.advancedItemTooltips) {
					try {
						CompoundTag compoundTag = StringNbtReader.parse(hoverEvent.getValue().getString());
						List<String> list = Lists.<String>newArrayList();
						Component component2 = Component.Serializer.fromJsonString(compoundTag.getString("name"));
						if (component2 != null) {
							list.add(component2.getFormattedText());
						}

						if (compoundTag.containsKey("type", 8)) {
							String string = compoundTag.getString("type");
							list.add("Type: " + string);
						}

						list.add(compoundTag.getString("id"));
						this.renderTooltip(list, i, j);
					} catch (CommandSyntaxException | JsonSyntaxException var9) {
						this.renderTooltip(ChatFormat.field_1061 + "Invalid Entity!", i, j);
					}
				}
			} else if (hoverEvent.getAction() == HoverEvent.Action.field_11762) {
				this.renderTooltip(this.minecraft.textRenderer.wrapStringToWidthAsList(hoverEvent.getValue().getFormattedText(), Math.max(this.width / 2, 200)), i, j);
			}

			GlStateManager.disableLighting();
		}
	}

	protected void insertText(String string, boolean bl) {
	}

	public boolean handleComponentClicked(Component component) {
		if (component == null) {
			return false;
		} else {
			ClickEvent clickEvent = component.getStyle().getClickEvent();
			if (hasShiftDown()) {
				if (component.getStyle().getInsertion() != null) {
					this.insertText(component.getStyle().getInsertion(), false);
				}
			} else if (clickEvent != null) {
				if (clickEvent.getAction() == ClickEvent.Action.field_11749) {
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
				} else if (clickEvent.getAction() == ClickEvent.Action.field_11746) {
					URI uRIx = new File(clickEvent.getValue()).toURI();
					this.openLink(uRIx);
				} else if (clickEvent.getAction() == ClickEvent.Action.field_11745) {
					this.insertText(clickEvent.getValue(), true);
				} else if (clickEvent.getAction() == ClickEvent.Action.field_11750) {
					this.sendMessage(clickEvent.getValue(), false);
				} else {
					LOGGER.error("Don't know how to handle {}", clickEvent);
				}

				return true;
			}

			return false;
		}
	}

	public void sendMessage(String string) {
		this.sendMessage(string, true);
	}

	public void sendMessage(String string, boolean bl) {
		if (bl) {
			this.minecraft.inGameHud.getChatHud().method_1803(string);
		}

		this.minecraft.player.sendChatMessage(string);
	}

	public void init(MinecraftClient minecraftClient, int i, int j) {
		this.minecraft = minecraftClient;
		this.itemRenderer = minecraftClient.getItemRenderer();
		this.font = minecraftClient.textRenderer;
		this.width = i;
		this.height = j;
		this.buttons.clear();
		this.children.clear();
		this.setFocused(null);
		this.init();
	}

	public void setSize(int i, int j) {
		this.width = i;
		this.height = j;
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

	public void renderBackground(int i) {
		if (this.minecraft.world != null) {
			this.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
		} else {
			this.renderDirtBackground(i);
		}
	}

	public void renderDirtBackground(int i) {
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		this.minecraft.getTextureManager().bindTexture(BACKGROUND_LOCATION);
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

	private void confirmLink(boolean bl) {
		if (bl) {
			this.openLink(this.clickedLink);
		}

		this.clickedLink = null;
		this.minecraft.openScreen(this);
	}

	private void openLink(URI uRI) {
		SystemUtil.getOperatingSystem().open(uRI);
	}

	public static boolean hasControlDown() {
		return MinecraftClient.IS_SYSTEM_MAC
			? InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 343)
				|| InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 347)
			: InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 341)
				|| InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 345);
	}

	public static boolean hasShiftDown() {
		return InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 340)
			|| InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 344);
	}

	public static boolean hasAltDown() {
		return InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 342)
			|| InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 346);
	}

	public static boolean isCut(int i) {
		return i == 88 && hasControlDown() && !hasShiftDown() && !hasAltDown();
	}

	public static boolean isPaste(int i) {
		return i == 86 && hasControlDown() && !hasShiftDown() && !hasAltDown();
	}

	public static boolean isCopy(int i) {
		return i == 67 && hasControlDown() && !hasShiftDown() && !hasAltDown();
	}

	public static boolean isSelectAll(int i) {
		return i == 65 && hasControlDown() && !hasShiftDown() && !hasAltDown();
	}

	public void resize(MinecraftClient minecraftClient, int i, int j) {
		this.init(minecraftClient, i, j);
	}

	public static void wrapScreenError(Runnable runnable, String string, String string2) {
		try {
			runnable.run();
		} catch (Throwable var6) {
			CrashReport crashReport = CrashReport.create(var6, string);
			CrashReportSection crashReportSection = crashReport.addElement("Affected screen");
			crashReportSection.add("Screen name", (ICrashCallable<String>)(() -> string2));
			throw new CrashException(crashReport);
		}
	}

	protected boolean isValidCharacterForName(String string, char c, int i) {
		int j = string.indexOf(58);
		int k = string.indexOf(47);
		if (c == ':') {
			return (k == -1 || i <= k) && j == -1;
		} else {
			return c == '/' ? i > j : c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '.';
		}
	}

	@Override
	public boolean isMouseOver(double d, double e) {
		return true;
	}
}
