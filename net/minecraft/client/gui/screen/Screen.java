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
implements Drawable {
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
            return true;
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
        this.renderTooltip(matrices, this.getTooltipFromItem(stack), x, y);
    }

    public List<Text> getTooltipFromItem(ItemStack stack) {
        return stack.getTooltip(this.client.player, this.client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
    }

    public void renderTooltip(MatrixStack matrices, Text line, int x, int y) {
        this.renderTooltip(matrices, Arrays.asList(line), x, y);
    }

    public void renderTooltip(MatrixStack matrices, List<Text> lines, int x, int y) {
        int j;
        if (lines.isEmpty()) {
            return;
        }
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableDepthTest();
        int i = 0;
        for (Text text : lines) {
            j = this.textRenderer.getStringWidth(text);
            if (j <= i) continue;
            i = j;
        }
        int k = x + 12;
        int l = y - 12;
        j = i;
        int m = 8;
        if (lines.size() > 1) {
            m += 2 + (lines.size() - 1) * 10;
        }
        if (k + i > this.width) {
            k -= 28 + i;
        }
        if (l + m + 6 > this.height) {
            l = this.height - m - 6;
        }
        this.setZOffset(300);
        this.itemRenderer.zOffset = 300.0f;
        int n = -267386864;
        this.fillGradient(matrices, k - 3, l - 4, k + j + 3, l - 3, -267386864, -267386864);
        this.fillGradient(matrices, k - 3, l + m + 3, k + j + 3, l + m + 4, -267386864, -267386864);
        this.fillGradient(matrices, k - 3, l - 3, k + j + 3, l + m + 3, -267386864, -267386864);
        this.fillGradient(matrices, k - 4, l - 3, k - 3, l + m + 3, -267386864, -267386864);
        this.fillGradient(matrices, k + j + 3, l - 3, k + j + 4, l + m + 3, -267386864, -267386864);
        int o = 0x505000FF;
        int p = 1344798847;
        this.fillGradient(matrices, k - 3, l - 3 + 1, k - 3 + 1, l + m + 3 - 1, 0x505000FF, 1344798847);
        this.fillGradient(matrices, k + j + 2, l - 3 + 1, k + j + 3, l + m + 3 - 1, 0x505000FF, 1344798847);
        this.fillGradient(matrices, k - 3, l - 3, k + j + 3, l - 3 + 1, 0x505000FF, 0x505000FF);
        this.fillGradient(matrices, k - 3, l + m + 2, k + j + 3, l + m + 3, 1344798847, 1344798847);
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        matrices.translate(0.0, 0.0, this.itemRenderer.zOffset);
        Matrix4f matrix4f = matrices.peek().getModel();
        for (int q = 0; q < lines.size(); ++q) {
            Text text2 = lines.get(q);
            if (text2 != null) {
                this.textRenderer.draw(text2, (float)k, (float)l, -1, true, matrix4f, (VertexConsumerProvider)immediate, false, 0, 0xF000F0);
            }
            if (q == 0) {
                l += 2;
            }
            l += 10;
        }
        immediate.draw();
        this.setZOffset(0);
        this.itemRenderer.zOffset = 0.0f;
        RenderSystem.enableDepthTest();
        RenderSystem.enableRescaleNormal();
    }

    protected void renderTextHoverEffect(MatrixStack matrices, @Nullable Text text, int i, int j) {
        if (text == null || text.getStyle().getHoverEvent() == null) {
            return;
        }
        HoverEvent hoverEvent = text.getStyle().getHoverEvent();
        HoverEvent.ItemStackContent itemStackContent = hoverEvent.getValue(HoverEvent.Action.SHOW_ITEM);
        if (itemStackContent != null) {
            this.renderTooltip(matrices, itemStackContent.asStack(), i, j);
        } else {
            HoverEvent.EntityContent entityContent = hoverEvent.getValue(HoverEvent.Action.SHOW_ENTITY);
            if (entityContent != null) {
                if (this.client.options.advancedItemTooltips) {
                    this.renderTooltip(matrices, entityContent.asTooltip(), i, j);
                }
            } else {
                Text text2 = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);
                if (text2 != null) {
                    this.renderTooltip(matrices, this.client.textRenderer.wrapLines(text2, Math.max(this.width / 2, 200)), i, j);
                }
            }
        }
    }

    protected void insertText(String text, boolean override) {
    }

    public boolean handleTextClick(Text text) {
        if (text == null) {
            return false;
        }
        ClickEvent clickEvent = text.getStyle().getClickEvent();
        if (Screen.hasShiftDown()) {
            if (text.getStyle().getInsertion() != null) {
                this.insertText(text.getStyle().getInsertion(), false);
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

    public void tick() {
    }

    public void removed() {
    }

    public void renderBackground(MatrixStack matrices) {
        this.renderBackground(matrices, 0);
    }

    public void renderBackground(MatrixStack matrices, int alpha) {
        if (this.client.world != null) {
            this.fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            this.renderDirtBackground(alpha);
        }
    }

    public void renderDirtBackground(int alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        this.client.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = 32.0f;
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0, this.height, 0.0).texture(0.0f, (float)this.height / 32.0f + (float)alpha).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(this.width, this.height, 0.0).texture((float)this.width / 32.0f, (float)this.height / 32.0f + (float)alpha).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(this.width, 0.0, 0.0).texture((float)this.width / 32.0f, alpha).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(0.0, 0.0, 0.0).texture(0.0f, alpha).color(64, 64, 64, 255).next();
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
}

