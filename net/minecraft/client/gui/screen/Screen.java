/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
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
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class Screen
extends AbstractParentElement
implements Drawable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<String> ALLOWED_PROTOCOLS = Sets.newHashSet("http", "https");
    protected final Component title;
    protected final List<Element> children = Lists.newArrayList();
    @Nullable
    protected MinecraftClient minecraft;
    protected ItemRenderer itemRenderer;
    public int width;
    public int height;
    protected final List<AbstractButtonWidget> buttons = Lists.newArrayList();
    public boolean passEvents;
    protected TextRenderer font;
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
        for (int k = 0; k < this.buttons.size(); ++k) {
            this.buttons.get(k).render(i, j, f);
        }
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        }
        if (i == 258) {
            boolean bl;
            boolean bl2 = bl = !Screen.hasShiftDown();
            if (!this.changeFocus(bl)) {
                this.changeFocus(bl);
            }
            return true;
        }
        return super.keyPressed(i, j, k);
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
        List<Component> list = itemStack.getTooltip(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
        ArrayList<String> list2 = Lists.newArrayList();
        for (Component component : list) {
            list2.add(component.getFormattedText());
        }
        return list2;
    }

    public void renderTooltip(String string, int i, int j) {
        this.renderTooltip(Arrays.asList(string), i, j);
    }

    public void renderTooltip(List<String> list, int i, int j) {
        int l;
        if (list.isEmpty()) {
            return;
        }
        GlStateManager.disableRescaleNormal();
        GuiLighting.disable();
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
        int k = 0;
        for (String string : list) {
            l = this.font.getStringWidth(string);
            if (l <= k) continue;
            k = l;
        }
        int m = i + 12;
        int n = j - 12;
        l = k;
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
        this.itemRenderer.zOffset = 300.0f;
        int p = -267386864;
        this.fillGradient(m - 3, n - 4, m + l + 3, n - 3, -267386864, -267386864);
        this.fillGradient(m - 3, n + o + 3, m + l + 3, n + o + 4, -267386864, -267386864);
        this.fillGradient(m - 3, n - 3, m + l + 3, n + o + 3, -267386864, -267386864);
        this.fillGradient(m - 4, n - 3, m - 3, n + o + 3, -267386864, -267386864);
        this.fillGradient(m + l + 3, n - 3, m + l + 4, n + o + 3, -267386864, -267386864);
        int q = 0x505000FF;
        int r = 1344798847;
        this.fillGradient(m - 3, n - 3 + 1, m - 3 + 1, n + o + 3 - 1, 0x505000FF, 1344798847);
        this.fillGradient(m + l + 2, n - 3 + 1, m + l + 3, n + o + 3 - 1, 0x505000FF, 1344798847);
        this.fillGradient(m - 3, n - 3, m + l + 3, n - 3 + 1, 0x505000FF, 0x505000FF);
        this.fillGradient(m - 3, n + o + 2, m + l + 3, n + o + 3, 1344798847, 1344798847);
        for (int s = 0; s < list.size(); ++s) {
            String string2 = list.get(s);
            this.font.drawWithShadow(string2, m, n, -1);
            if (s == 0) {
                n += 2;
            }
            n += 10;
        }
        this.blitOffset = 0;
        this.itemRenderer.zOffset = 0.0f;
        GlStateManager.enableLighting();
        GlStateManager.enableDepthTest();
        GuiLighting.enable();
        GlStateManager.enableRescaleNormal();
    }

    protected void renderComponentHoverEffect(Component component, int i, int j) {
        if (component == null || component.getStyle().getHoverEvent() == null) {
            return;
        }
        HoverEvent hoverEvent = component.getStyle().getHoverEvent();
        if (hoverEvent.getAction() == HoverEvent.Action.SHOW_ITEM) {
            ItemStack itemStack = ItemStack.EMPTY;
            try {
                CompoundTag tag = StringNbtReader.parse(hoverEvent.getValue().getString());
                if (tag instanceof CompoundTag) {
                    itemStack = ItemStack.fromTag(tag);
                }
            } catch (CommandSyntaxException tag) {
                // empty catch block
            }
            if (itemStack.isEmpty()) {
                this.renderTooltip((Object)((Object)ChatFormat.RED) + "Invalid Item!", i, j);
            } else {
                this.renderTooltip(itemStack, i, j);
            }
        } else if (hoverEvent.getAction() == HoverEvent.Action.SHOW_ENTITY) {
            if (this.minecraft.options.advancedItemTooltips) {
                try {
                    CompoundTag compoundTag = StringNbtReader.parse(hoverEvent.getValue().getString());
                    ArrayList<String> list = Lists.newArrayList();
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
                } catch (JsonSyntaxException | CommandSyntaxException exception) {
                    this.renderTooltip((Object)((Object)ChatFormat.RED) + "Invalid Entity!", i, j);
                }
            }
        } else if (hoverEvent.getAction() == HoverEvent.Action.SHOW_TEXT) {
            this.renderTooltip(this.minecraft.textRenderer.wrapStringToWidthAsList(hoverEvent.getValue().getFormattedText(), Math.max(this.width / 2, 200)), i, j);
        }
        GlStateManager.disableLighting();
    }

    protected void insertText(String string, boolean bl) {
    }

    public boolean handleComponentClicked(Component component) {
        if (component == null) {
            return false;
        }
        ClickEvent clickEvent = component.getStyle().getClickEvent();
        if (Screen.hasShiftDown()) {
            if (component.getStyle().getInsertion() != null) {
                this.insertText(component.getStyle().getInsertion(), false);
            }
        } else if (clickEvent != null) {
            block19: {
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
                            break block19;
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
                } else {
                    LOGGER.error("Don't know how to handle {}", (Object)clickEvent);
                }
            }
            return true;
        }
        return false;
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
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = 32.0f;
        bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
        bufferBuilder.vertex(0.0, this.height, 0.0).texture(0.0, (float)this.height / 32.0f + (float)i).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(this.width, this.height, 0.0).texture((float)this.width / 32.0f, (float)this.height / 32.0f + (float)i).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(this.width, 0.0, 0.0).texture((float)this.width / 32.0f, (double)i).color(64, 64, 64, 255).next();
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
        if (MinecraftClient.IS_SYSTEM_MAC) {
            return InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 343) || InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 347);
        }
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 341) || InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 345);
    }

    public static boolean hasShiftDown() {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 340) || InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 344);
    }

    public static boolean hasAltDown() {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 342) || InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 346);
    }

    public static boolean isCut(int i) {
        return i == 88 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isPaste(int i) {
        return i == 86 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isCopy(int i) {
        return i == 67 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isSelectAll(int i) {
        return i == 65 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public void resize(MinecraftClient minecraftClient, int i, int j) {
        this.init(minecraftClient, i, j);
    }

    public static void wrapScreenError(Runnable runnable, String string, String string2) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, string);
            CrashReportSection crashReportSection = crashReport.addElement("Affected screen");
            crashReportSection.add("Screen name", () -> string2);
            throw new CrashException(crashReport);
        }
    }

    protected boolean isValidCharacterForName(String string, char c, int i) {
        int j = string.indexOf(58);
        int k = string.indexOf(47);
        if (c == ':') {
            return (k == -1 || i <= k) && j == -1;
        }
        if (c == '/') {
            return i > j;
        }
        return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '.';
    }

    @Override
    public boolean isMouseOver(double d, double e) {
        return true;
    }
}

