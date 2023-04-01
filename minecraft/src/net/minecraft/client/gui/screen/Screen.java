package net.minecraft.client.gui.screen;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_8293;
import net.minecraft.class_8440;
import net.minecraft.class_8441;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.navigation.Navigable;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.screen.narration.ScreenNarrator;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.gui.widget.ClickableWidget;
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
import net.minecraft.sound.MusicSound;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.joml.Matrix4f;
import org.joml.Vector2ic;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class Screen extends AbstractParentElement implements Drawable {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Set<String> ALLOWED_PROTOCOLS = Sets.<String>newHashSet("http", "https");
	private static final int field_32270 = 2;
	private static final Text SCREEN_USAGE_TEXT = Text.translatable("narrator.screen.usage");
	private static final int field_44305 = 10;
	private static final MutableText TOOLTIP_TOO_LONG_TEXT = Text.translatable("tooltip.too_long").formatted(Formatting.AQUA);
	protected final Text title;
	private final List<Element> children = Lists.<Element>newArrayList();
	private final List<Selectable> selectables = Lists.<Selectable>newArrayList();
	@Nullable
	protected MinecraftClient client;
	private boolean screenInitialized;
	protected ItemRenderer itemRenderer;
	public int width;
	public int height;
	private final List<Drawable> drawables = Lists.<Drawable>newArrayList();
	public boolean passEvents;
	public TextRenderer textRenderer;
	@Nullable
	private URI clickedLink;
	private static final long SCREEN_INIT_NARRATION_DELAY = TimeUnit.SECONDS.toMillis(2L);
	private static final long NARRATOR_MODE_CHANGE_DELAY = SCREEN_INIT_NARRATION_DELAY;
	private static final long MOUSE_MOVE_NARRATION_DELAY = 750L;
	private static final long MOUSE_PRESS_SCROLL_NARRATION_DELAY = 200L;
	private static final long KEY_PRESS_NARRATION_DELAY = 200L;
	private final ScreenNarrator narrator = new ScreenNarrator();
	private long elementNarrationStartTime = Long.MIN_VALUE;
	private long screenNarrationStartTime = Long.MAX_VALUE;
	@Nullable
	private Selectable selected;
	@Nullable
	private Screen.PositionedTooltip tooltip;

	protected Screen(Text title) {
		this.title = title;
	}

	public Text getTitle() {
		return this.title;
	}

	public Text getNarratedTitle() {
		return this.getTitle();
	}

	public final void renderWithTooltip(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.render(matrices, mouseX, mouseY, delta);
		if (this.tooltip != null) {
			this.renderPositionedTooltip(matrices, this.tooltip, mouseX, mouseY);
			this.tooltip = null;
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		for (Drawable drawable : this.drawables) {
			drawable.render(matrices, mouseX, mouseY, delta);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE && this.shouldCloseOnEsc()) {
			this.close();
			return true;
		} else if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else {
			GuiNavigation guiNavigation = (GuiNavigation)(switch (keyCode) {
				case 258 -> this.getTabNavigation();
				default -> null;
				case 262 -> this.getArrowNavigation(NavigationDirection.RIGHT);
				case 263 -> this.getArrowNavigation(NavigationDirection.LEFT);
				case 264 -> this.getArrowNavigation(NavigationDirection.DOWN);
				case 265 -> this.getArrowNavigation(NavigationDirection.UP);
			});
			if (guiNavigation != null) {
				GuiNavigationPath guiNavigationPath = super.getNavigationPath(guiNavigation);
				if (guiNavigationPath == null && guiNavigation instanceof GuiNavigation.Tab) {
					this.blur();
					guiNavigationPath = super.getNavigationPath(guiNavigation);
				}

				if (guiNavigationPath != null) {
					this.switchFocus(guiNavigationPath);
				}
			}

			return false;
		}
	}

	private GuiNavigation.Tab getTabNavigation() {
		boolean bl = !hasShiftDown();
		return new GuiNavigation.Tab(bl);
	}

	private GuiNavigation.Arrow getArrowNavigation(NavigationDirection direction) {
		return new GuiNavigation.Arrow(direction);
	}

	/**
	 * Sets the initial focus of this screen. This should be called inside the overridden
	 * {@link #init()} method by screen implementations.
	 */
	protected void setInitialFocus(Element element) {
		GuiNavigationPath guiNavigationPath = GuiNavigationPath.of(this, element.getNavigationPath(new GuiNavigation.Down()));
		if (guiNavigationPath != null) {
			this.switchFocus(guiNavigationPath);
		}
	}

	private void blur() {
		GuiNavigationPath guiNavigationPath = this.getFocusedPath();
		if (guiNavigationPath != null) {
			guiNavigationPath.setFocused(false);
		}
	}

	/**
	 * Switches focus from the currently focused element, if any, to {@code path}.
	 */
	@VisibleForTesting
	protected void switchFocus(GuiNavigationPath path) {
		this.blur();
		path.setFocused(true);
	}

	/**
	 * Checks whether this screen should be closed when the escape key is pressed.
	 */
	public boolean shouldCloseOnEsc() {
		return true;
	}

	public void close() {
		this.client.setScreen(null);
	}

	protected <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
		this.drawables.add(drawableElement);
		return this.addSelectableChild(drawableElement);
	}

	protected <T extends Drawable> T addDrawable(T drawable) {
		this.drawables.add(drawable);
		return drawable;
	}

	protected <T extends Element & Selectable> T addSelectableChild(T child) {
		this.children.add(child);
		this.selectables.add(child);
		return child;
	}

	protected void remove(Element child) {
		if (child instanceof Drawable) {
			this.drawables.remove((Drawable)child);
		}

		if (child instanceof Selectable) {
			this.selectables.remove((Selectable)child);
		}

		this.children.remove(child);
	}

	protected void clearChildren() {
		this.drawables.clear();
		this.children.clear();
		this.selectables.clear();
	}

	protected void renderTooltip(MatrixStack matrices, ItemStack stack, int x, int y) {
		this.renderTooltip(matrices, this.getTooltipFromItem(stack), stack.getTooltipData(), x, y);
	}

	public void renderTooltip(MatrixStack matrices, List<Text> lines, Optional<TooltipData> data, int x, int y) {
		List<TooltipComponent> list = (List<TooltipComponent>)lines.stream().map(Text::asOrderedText).map(TooltipComponent::of).collect(Collectors.toList());
		data.ifPresent(datax -> list.add(1, TooltipComponent.of(datax)));
		this.renderTooltipFromComponents(matrices, list, x, y, HoveredTooltipPositioner.INSTANCE);
	}

	public List<Text> getTooltipFromItem(ItemStack stack) {
		return (List<Text>)(class_8293.field_43508.method_50116()
			? new ArrayList()
			: stack.getTooltip(this.client.player, this.client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.BASIC));
	}

	public void renderTooltip(MatrixStack matrices, Text text, int x, int y) {
		this.renderOrderedTooltip(matrices, Arrays.asList(text.asOrderedText()), x, y);
	}

	public void renderTooltip(MatrixStack matrices, List<Text> lines, int x, int y) {
		this.renderOrderedTooltip(matrices, Lists.transform(lines, Text::asOrderedText), x, y);
	}

	public void renderOrderedTooltip(MatrixStack matrices, List<? extends OrderedText> lines, int x, int y) {
		this.renderTooltipFromComponents(
			matrices, (List<TooltipComponent>)lines.stream().map(TooltipComponent::of).collect(Collectors.toList()), x, y, HoveredTooltipPositioner.INSTANCE
		);
	}

	private void renderPositionedTooltip(MatrixStack matrices, Screen.PositionedTooltip tooltip, int x, int y) {
		this.renderTooltipFromComponents(
			matrices, (List<TooltipComponent>)tooltip.tooltip().stream().map(TooltipComponent::of).collect(Collectors.toList()), x, y, tooltip.positioner()
		);
	}

	private void renderTooltipFromComponents(MatrixStack matrices, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner) {
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

			Vector2ic vector2ic = positioner.getPosition(this, x, y, i, j);
			int n = vector2ic.x();
			int o = vector2ic.y();
			matrices.push();
			int p = 400;
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			RenderSystem.setShader(GameRenderer::getPositionColorProgram);
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			Matrix4f matrix4f = matrices.peek().getPositionMatrix();
			TooltipBackgroundRenderer.render(
				(matrix, builder, startX, startY, endX, endY, z, colorStart, colorEnd) -> DrawableHelper.fillGradient(
						matrix, builder, startX, startY, endX, endY, z, colorStart, colorEnd
					),
				matrix4f,
				bufferBuilder,
				n,
				o,
				i,
				j,
				400
			);
			RenderSystem.enableDepthTest();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
			VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
			matrices.translate(0.0F, 0.0F, 400.0F);
			int q = o;

			for (int r = 0; r < components.size(); r++) {
				TooltipComponent tooltipComponent2 = (TooltipComponent)components.get(r);
				tooltipComponent2.drawText(this.textRenderer, n, q, matrix4f, immediate);
				q += tooltipComponent2.getHeight() + (r == 0 ? 2 : 0);
			}

			immediate.draw();
			q = o;

			for (int r = 0; r < components.size(); r++) {
				TooltipComponent tooltipComponent2 = (TooltipComponent)components.get(r);
				tooltipComponent2.drawItems(this.textRenderer, n, q, matrices, this.itemRenderer);
				q += tooltipComponent2.getHeight() + (r == 0 ? 2 : 0);
			}

			matrices.pop();
		}
	}

	protected void renderTextHoverEffect(MatrixStack matrices, @Nullable Style style, int x, int y) {
		if (style != null && style.getHoverEvent() != null) {
			HoverEvent hoverEvent = style.getHoverEvent();
			int i = Math.max(this.width / 2, 200);
			List<OrderedText> list = this.getTooltipFromHoverEvent(hoverEvent).stream().flatMap(text -> this.client.textRenderer.wrapLines(text, i).stream()).toList();
			if (list.size() > 10) {
				List<OrderedText> list2 = new ArrayList(11);
				list2.addAll(list.subList(0, 10));
				list2.add(TOOLTIP_TOO_LONG_TEXT.asOrderedText());
				list = list2;
			}

			if (!list.isEmpty()) {
				this.renderOrderedTooltip(matrices, list, x, y);
			}
		}
	}

	private List<class_8440> method_50947(HoverEvent hoverEvent) {
		List<Text> list = this.getTooltipFromHoverEvent(hoverEvent);
		return class_8440.method_50950(this.client.textRenderer, list.stream(), Math.max(this.width / 2, 200)).toList();
	}

	private List<Text> getTooltipFromHoverEvent(HoverEvent hoverEvent) {
		HoverEvent.ItemStackContent itemStackContent = hoverEvent.getValue(HoverEvent.Action.SHOW_ITEM);
		if (itemStackContent != null) {
			return this.getTooltipFromItem(itemStackContent.asStack());
		} else {
			HoverEvent.EntityContent entityContent = hoverEvent.getValue(HoverEvent.Action.SHOW_ENTITY);
			if (entityContent != null) {
				return this.client.options.advancedItemTooltips ? entityContent.asTooltip() : List.of();
			} else {
				Text text = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);
				return text != null ? List.of(text) : List.of();
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
			if (hasControlDown()) {
				HoverEvent hoverEvent = style.getHoverEvent();
				if (hoverEvent != null) {
					List<class_8440> list = this.method_50947(hoverEvent);
					if (!list.isEmpty()) {
						this.client.setScreen(new class_8441(Text.translatable("tooltip.more_like_cooltip"), this, list));
						return true;
					}
				}
			} else if (hasShiftDown()) {
				if (style.getInsertion() != null) {
					this.insertText(style.getInsertion(), false);
				}
			} else if (clickEvent != null) {
				if (clickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
					if (!this.client.options.getChatLinks().getValue()) {
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

						if (this.client.options.getChatLinksPrompt().getValue()) {
							this.clickedLink = uRI;
							this.client.setScreen(new ConfirmLinkScreen(this::confirmLink, clickEvent.getValue(), false));
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
					this.insertText(SharedConstants.stripInvalidChars(clickEvent.getValue()), true);
				} else if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
					String string2 = SharedConstants.stripInvalidChars(clickEvent.getValue());
					if (string2.startsWith("/")) {
						if (!this.client.player.networkHandler.sendCommand(string2.substring(1))) {
							LOGGER.error("Not allowed to run command with signed argument from click event: '{}'", string2);
						}
					} else {
						LOGGER.error("Failed to run command without '/' prefix from click event: '{}'", string2);
					}
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

	public final void init(MinecraftClient client, int width, int height) {
		this.client = client;
		this.itemRenderer = client.getItemRenderer();
		this.textRenderer = client.textRenderer;
		this.width = width;
		this.height = height;
		if (!this.screenInitialized) {
			this.init();
		} else {
			this.initTabNavigation();
		}

		this.screenInitialized = true;
		this.narrateScreenIfNarrationEnabled(false);
		this.setElementNarrationDelay(SCREEN_INIT_NARRATION_DELAY);
	}

	protected void clearAndInit() {
		this.clearChildren();
		this.blur();
		this.init();
	}

	@Override
	public List<? extends Element> children() {
		return this.children;
	}

	/**
	 * Called when a screen should be initialized.
	 * 
	 * <p>This method is called when this screen is {@linkplain net.minecraft.client.MinecraftClient#setScreen(Screen) opened} or resized.
	 * 
	 * <p>This should call {@link #setInitialFocus} to set the element that is initially focused.
	 */
	protected void init() {
	}

	public void tick() {
	}

	public void removed() {
	}

	/**
	 * Called when the screen is displayed using {@link MinecraftClient#setScreen}
	 * before {@link #init()} or {@link #initTabNavigation()} is called.
	 */
	public void onDisplayed() {
	}

	/**
	 * Renders the background of this screen.
	 * 
	 * <p>If the client is in a world, renders the translucent background gradient.
	 * Otherwise {@linkplain #renderBackgroundTexture renders the background texture}.
	 */
	public void renderBackground(MatrixStack matrices) {
		if (this.client.world != null) {
			fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);
		} else {
			this.renderBackgroundTexture(matrices);
		}
	}

	/**
	 * Renders the fullscreen {@linkplain net.minecraft.client.gui.DrawableHelper#OPTIONS_BACKGROUND_TEXTURE background texture} of this screen.
	 */
	public void renderBackgroundTexture(MatrixStack matrices) {
		RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND_TEXTURE);
		RenderSystem.setShaderColor(0.25F, 0.25F, 0.25F, 1.0F);
		int i = 32;
		drawTexture(matrices, 0, 0, 0, 0.0F, 0.0F, this.width, this.height, 32, 32);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public boolean shouldPause() {
		return true;
	}

	private void confirmLink(boolean open) {
		if (open) {
			this.openLink(this.clickedLink);
		}

		this.clickedLink = null;
		this.client.setScreen(this);
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

	protected void initTabNavigation() {
		this.clearAndInit();
	}

	public void resize(MinecraftClient client, int width, int height) {
		this.width = width;
		this.height = height;
		this.initTabNavigation();
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

	private void setScreenNarrationDelay(long delayMs, boolean restartElementNarration) {
		this.screenNarrationStartTime = Util.getMeasuringTimeMs() + delayMs;
		if (restartElementNarration) {
			this.elementNarrationStartTime = Long.MIN_VALUE;
		}
	}

	private void setElementNarrationDelay(long delayMs) {
		this.elementNarrationStartTime = Util.getMeasuringTimeMs() + delayMs;
	}

	public void applyMouseMoveNarratorDelay() {
		this.setScreenNarrationDelay(750L, false);
	}

	public void applyMousePressScrollNarratorDelay() {
		this.setScreenNarrationDelay(200L, true);
	}

	public void applyKeyPressNarratorDelay() {
		this.setScreenNarrationDelay(200L, true);
	}

	private boolean isNarratorActive() {
		return this.client.getNarratorManager().isActive();
	}

	public void updateNarrator() {
		if (this.isNarratorActive()) {
			long l = Util.getMeasuringTimeMs();
			if (l > this.screenNarrationStartTime && l > this.elementNarrationStartTime) {
				this.narrateScreen(true);
				this.screenNarrationStartTime = Long.MAX_VALUE;
			}
		}
	}

	/**
	 * If narration is enabled, narrates the elements of this screen.
	 * 
	 * @param onlyChangedNarrations if {@code true}, the text will not include unchanged narrations that have
	 * already been narrated previously
	 */
	public void narrateScreenIfNarrationEnabled(boolean onlyChangedNarrations) {
		if (this.isNarratorActive()) {
			this.narrateScreen(onlyChangedNarrations);
		}
	}

	private void narrateScreen(boolean onlyChangedNarrations) {
		this.narrator.buildNarrations(this::addScreenNarrations);
		String string = this.narrator.buildNarratorText(!onlyChangedNarrations);
		if (!string.isEmpty()) {
			this.client.getNarratorManager().narrate(string);
		}
	}

	protected boolean hasUsageText() {
		return true;
	}

	protected void addScreenNarrations(NarrationMessageBuilder messageBuilder) {
		messageBuilder.put(NarrationPart.TITLE, this.getNarratedTitle());
		if (this.hasUsageText()) {
			messageBuilder.put(NarrationPart.USAGE, SCREEN_USAGE_TEXT);
		}

		this.addElementNarrations(messageBuilder);
	}

	protected void addElementNarrations(NarrationMessageBuilder builder) {
		List<Selectable> list = (List<Selectable>)this.selectables.stream().filter(Selectable::isNarratable).collect(Collectors.toList());
		Collections.sort(list, Comparator.comparingInt(Navigable::getNavigationOrder));
		Screen.SelectedElementNarrationData selectedElementNarrationData = findSelectedElementData(list, this.selected);
		if (selectedElementNarrationData != null) {
			if (selectedElementNarrationData.selectType.isFocused()) {
				this.selected = selectedElementNarrationData.selectable;
			}

			if (list.size() > 1) {
				builder.put(NarrationPart.POSITION, Text.translatable("narrator.position.screen", selectedElementNarrationData.index + 1, list.size()));
				if (selectedElementNarrationData.selectType == Selectable.SelectionType.FOCUSED) {
					builder.put(NarrationPart.USAGE, Text.translatable("narration.component_list.usage"));
				}
			}

			selectedElementNarrationData.selectable.appendNarrations(builder.nextMessage());
		}
	}

	@Nullable
	public static Screen.SelectedElementNarrationData findSelectedElementData(List<? extends Selectable> selectables, @Nullable Selectable selectable) {
		Screen.SelectedElementNarrationData selectedElementNarrationData = null;
		Screen.SelectedElementNarrationData selectedElementNarrationData2 = null;
		int i = 0;

		for (int j = selectables.size(); i < j; i++) {
			Selectable selectable2 = (Selectable)selectables.get(i);
			Selectable.SelectionType selectionType = selectable2.getType();
			if (selectionType.isFocused()) {
				if (selectable2 != selectable) {
					return new Screen.SelectedElementNarrationData(selectable2, i, selectionType);
				}

				selectedElementNarrationData2 = new Screen.SelectedElementNarrationData(selectable2, i, selectionType);
			} else if (selectionType.compareTo(selectedElementNarrationData != null ? selectedElementNarrationData.selectType : Selectable.SelectionType.NONE) > 0) {
				selectedElementNarrationData = new Screen.SelectedElementNarrationData(selectable2, i, selectionType);
			}
		}

		return selectedElementNarrationData != null ? selectedElementNarrationData : selectedElementNarrationData2;
	}

	public void applyNarratorModeChangeDelay() {
		this.setScreenNarrationDelay(NARRATOR_MODE_CHANGE_DELAY, false);
	}

	public void setTooltip(List<OrderedText> tooltip) {
		this.setTooltip(tooltip, HoveredTooltipPositioner.INSTANCE, true);
	}

	public void setTooltip(List<OrderedText> tooltip, TooltipPositioner positioner, boolean focused) {
		if (this.tooltip == null || focused) {
			this.tooltip = new Screen.PositionedTooltip(tooltip, positioner);
		}
	}

	protected void setTooltip(Text tooltip) {
		this.setTooltip(Tooltip.wrapLines(this.client, tooltip));
	}

	public void setTooltip(Tooltip tooltip, TooltipPositioner positioner, boolean focused) {
		this.setTooltip(tooltip.getLines(this.client), positioner, focused);
	}

	protected static void hide(ClickableWidget... widgets) {
		for (ClickableWidget clickableWidget : widgets) {
			clickableWidget.visible = false;
		}
	}

	@Override
	public ScreenRect getNavigationFocus() {
		return new ScreenRect(0, 0, this.width, this.height);
	}

	@Nullable
	public MusicSound getMusic() {
		return null;
	}

	@Environment(EnvType.CLIENT)
	static record PositionedTooltip(List<OrderedText> tooltip, TooltipPositioner positioner) {
	}

	@Environment(EnvType.CLIENT)
	public static class SelectedElementNarrationData {
		public final Selectable selectable;
		public final int index;
		public final Selectable.SelectionType selectType;

		public SelectedElementNarrationData(Selectable selectable, int index, Selectable.SelectionType selectType) {
			this.selectable = selectable;
			this.index = index;
			this.selectType = selectType;
		}
	}
}
