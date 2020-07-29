/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
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
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Matrix4f;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class Screen
extends AbstractParentElement
implements TickableElement,
Drawable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<String> ALLOWED_PROTOCOLS = Sets.newHashSet("http", "https");
    protected final Text title;
    protected final List<Element> children = Lists.newArrayList();
    @Nullable
    protected MinecraftClient client;
    protected ItemRenderer itemRenderer;
    public int width;
    public int height;
    protected final List<AbstractButtonWidget> buttons = Lists.newArrayList();
    public boolean passEvents;
    protected TextRenderer textRenderer;
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
        for (int i = 0; i < this.buttons.size(); ++i) {
            this.buttons.get(i).render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        }
        if (keyCode == 258) {
            boolean bl;
            boolean bl2 = bl = !Screen.hasShiftDown();
            if (!this.changeFocus(bl)) {
                this.changeFocus(bl);
            }
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
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

    protected <T extends Element> T addChild(T child) {
        this.children.add(child);
        return child;
    }

    protected void renderTooltip(MatrixStack matrices, ItemStack stack, int x, int y) {
        this.method_30901(matrices, this.getTooltipFromItem(stack), x, y);
    }

    public List<Text> getTooltipFromItem(ItemStack stack) {
        return stack.getTooltip(this.client.player, this.client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
    }

    public void renderTooltip(MatrixStack matrices, Text text, int x, int y) {
        this.renderTooltip(matrices, Arrays.asList(text.asOrderedText()), x, y);
    }

    public void method_30901(MatrixStack matrixStack, List<Text> list, int i, int j) {
        this.renderTooltip(matrixStack, Lists.transform(list, Text::asOrderedText), i, j);
    }

    /*
     * WARNING - void declaration
     */
    public void renderTooltip(MatrixStack matrices, List<? extends OrderedText> lines, int x, int y) {
        int n;
        int j;
        if (lines.isEmpty()) {
            return;
        }
        int i = 0;
        for (OrderedText orderedText : lines) {
            j = this.textRenderer.getWidth(orderedText);
            if (j <= i) continue;
            i = j;
        }
        int k = x + 12;
        int n2 = y - 12;
        j = i;
        int m = 8;
        if (lines.size() > 1) {
            m += 2 + (lines.size() - 1) * 10;
        }
        if (k + i > this.width) {
            k -= 28 + i;
        }
        if (n2 + m + 6 > this.height) {
            n = this.height - m - 6;
        }
        matrices.push();
        int n3 = -267386864;
        int o = 0x505000FF;
        int p = 1344798847;
        int q = 400;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        Matrix4f matrix4f = matrices.peek().getModel();
        Screen.fillGradient(matrix4f, bufferBuilder, k - 3, n - 4, k + j + 3, n - 3, 400, -267386864, -267386864);
        Screen.fillGradient(matrix4f, bufferBuilder, k - 3, n + m + 3, k + j + 3, n + m + 4, 400, -267386864, -267386864);
        Screen.fillGradient(matrix4f, bufferBuilder, k - 3, n - 3, k + j + 3, n + m + 3, 400, -267386864, -267386864);
        Screen.fillGradient(matrix4f, bufferBuilder, k - 4, n - 3, k - 3, n + m + 3, 400, -267386864, -267386864);
        Screen.fillGradient(matrix4f, bufferBuilder, k + j + 3, n - 3, k + j + 4, n + m + 3, 400, -267386864, -267386864);
        Screen.fillGradient(matrix4f, bufferBuilder, k - 3, n - 3 + 1, k - 3 + 1, n + m + 3 - 1, 400, 0x505000FF, 1344798847);
        Screen.fillGradient(matrix4f, bufferBuilder, k + j + 2, n - 3 + 1, k + j + 3, n + m + 3 - 1, 400, 0x505000FF, 1344798847);
        Screen.fillGradient(matrix4f, bufferBuilder, k - 3, n - 3, k + j + 3, n - 3 + 1, 400, 0x505000FF, 0x505000FF);
        Screen.fillGradient(matrix4f, bufferBuilder, k - 3, n + m + 2, k + j + 3, n + m + 3, 400, 1344798847, 1344798847);
        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        matrices.translate(0.0, 0.0, 400.0);
        for (int r = 0; r < lines.size(); ++r) {
            OrderedText orderedText2 = lines.get(r);
            if (orderedText2 != null) {
                void var7_11;
                this.textRenderer.draw(orderedText2, (float)k, (float)var7_11, -1, true, matrix4f, (VertexConsumerProvider)immediate, false, 0, 0xF000F0);
            }
            if (r == 0) {
                var7_11 += 2;
            }
            var7_11 += 10;
        }
        immediate.draw();
        matrices.pop();
    }

    protected void renderTextHoverEffect(MatrixStack matrices, @Nullable Style style, int i, int j) {
        if (style == null || style.getHoverEvent() == null) {
            return;
        }
        HoverEvent hoverEvent = style.getHoverEvent();
        HoverEvent.ItemStackContent itemStackContent = hoverEvent.getValue(HoverEvent.Action.SHOW_ITEM);
        if (itemStackContent != null) {
            this.renderTooltip(matrices, itemStackContent.asStack(), i, j);
        } else {
            HoverEvent.EntityContent entityContent = hoverEvent.getValue(HoverEvent.Action.SHOW_ENTITY);
            if (entityContent != null) {
                if (this.client.options.advancedItemTooltips) {
                    this.method_30901(matrices, entityContent.asTooltip(), i, j);
                }
            } else {
                Text text = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);
                if (text != null) {
                    this.renderTooltip(matrices, this.client.textRenderer.wrapLines(text, Math.max(this.width / 2, 200)), i, j);
                }
            }
        }
    }

    protected void insertText(String text, boolean override) {
    }

    public boolean handleTextClick(@Nullable Style style) {
        if (style == null) {
            return false;
        }
        ClickEvent clickEvent = style.getClickEvent();
        if (Screen.hasShiftDown()) {
            if (style.getInsertion() != null) {
                this.insertText(style.getInsertion(), false);
            }
        } else if (clickEvent != null) {
            block21: {
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
                            break block21;
                        }
                        this.openLink(uRI);
                    } catch (URISyntaxException uRISyntaxException) {
                        LOGGER.error("Can't open url for {}", (Object)clickEvent, (Object)uRISyntaxException);
                    }
                } else if (clickEvent.getAction() == ClickEvent.Action.OPEN_FILE) {
                    URI uRI = new File(clickEvent.getValue()).toURI();
                    this.openLink(uRI);
                } else if (clickEvent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                    this.insertText(clickEvent.getValue(), true);
                } else if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                    this.sendMessage(clickEvent.getValue(), false);
                } else if (clickEvent.getAction() == ClickEvent.Action.COPY_TO_CLIPBOARD) {
                    this.client.keyboard.setClipboard(clickEvent.getValue());
                } else {
                    LOGGER.error("Don't know how to handle {}", (Object)clickEvent);
                }
            }
            return true;
        }
        return false;
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
     * Renders the fullscreen {@linkplain #BACKGROUND_TEXTURE background texture} of this screen.
     * 
     * @param vOffset an offset applied to the V coordinate of the background texture
     */
    public void renderBackgroundTexture(int vOffset) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        this.client.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = 32.0f;
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0, this.height, 0.0).texture(0.0f, (float)this.height / 32.0f + (float)vOffset).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(this.width, this.height, 0.0).texture((float)this.width / 32.0f, (float)this.height / 32.0f + (float)vOffset).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(this.width, 0.0, 0.0).texture((float)this.width / 32.0f, vOffset).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(0.0, 0.0, 0.0).texture(0.0f, vOffset).color(64, 64, 64, 255).next();
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
        if (MinecraftClient.IS_SYSTEM_MAC) {
            return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 343) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 347);
        }
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 341) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 345);
    }

    public static boolean hasShiftDown() {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 340) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 344);
    }

    public static boolean hasAltDown() {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 342) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 346);
    }

    public static boolean isCut(int code) {
        return code == 88 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isPaste(int code) {
        return code == 86 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isCopy(int code) {
        return code == 67 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isSelectAll(int code) {
        return code == 65 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public void resize(MinecraftClient client, int width, int height) {
        this.init(client, width, height);
    }

    public static void wrapScreenError(Runnable task, String errorTitle, String screenName) {
        try {
            task.run();
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, errorTitle);
            CrashReportSection crashReportSection = crashReport.addElement("Affected screen");
            crashReportSection.add("Screen name", () -> screenName);
            throw new CrashException(crashReport);
        }
    }

    protected boolean isValidCharacterForName(String name, char character, int cursorPos) {
        int i = name.indexOf(58);
        int j = name.indexOf(47);
        if (character == ':') {
            return (j == -1 || cursorPos <= j) && i == -1;
        }
        if (character == '/') {
            return cursorPos > i;
        }
        return character == '_' || character == '-' || character >= 'a' && character <= 'z' || character >= '0' && character <= '9' || character == '.';
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return true;
    }

    public void filesDragged(List<Path> paths) {
    }
}

