/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.FocusedRect;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.tooltip.FocusedTooltipPositioner;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.gui.tooltip.WidgetTooltipPositioner;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

/**
 * A clickable widget is a GUI element that has many methods to handle different
 * mouse actions. In addition, it allows a message to be rendered on the widget
 * and narrated when the widget is selected.
 */
@Environment(value=EnvType.CLIENT)
public abstract class ClickableWidget
extends DrawableHelper
implements Drawable,
Element,
Widget,
Selectable {
    public static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");
    public static final Identifier ACCESSIBILITY_TEXTURE = new Identifier("textures/gui/accessibility.png");
    protected static final int field_41797 = 46;
    protected static final int field_42118 = 200;
    protected static final int field_42119 = 20;
    protected static final int field_42120 = 4;
    private static final int field_42485 = 2;
    protected int width;
    protected int height;
    private int x;
    private int y;
    private Text message;
    protected boolean hovered;
    public boolean active = true;
    public boolean visible = true;
    protected float alpha = 1.0f;
    private int navigationOrder;
    private boolean focused;
    @Nullable
    private Tooltip tooltip;
    private int tooltipDelay;
    private long lastHoveredTime;
    private boolean wasHovered;

    public ClickableWidget(int x, int y, int width, int height, Text message) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.message = message;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    protected Identifier getTexture() {
        return WIDGETS_TEXTURE;
    }

    protected int getYImage() {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (this.isHovered()) {
            i = 2;
        }
        return 46 + i * 20;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!this.visible) {
            return;
        }
        this.hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
        this.renderButton(matrices, mouseX, mouseY, delta);
        this.applyTooltip();
    }

    private void applyTooltip() {
        Screen screen;
        boolean bl;
        if (this.tooltip == null) {
            return;
        }
        boolean bl2 = bl = this.hovered || this.isFocused() && MinecraftClient.getInstance().getNavigationType().isKeyboard();
        if (bl != this.wasHovered) {
            if (bl) {
                this.lastHoveredTime = Util.getMeasuringTimeMs();
            }
            this.wasHovered = bl;
        }
        if (bl && Util.getMeasuringTimeMs() - this.lastHoveredTime > (long)this.tooltipDelay && (screen = MinecraftClient.getInstance().currentScreen) != null) {
            screen.setTooltip(this.tooltip, this.getTooltipPositioner(), this.isFocused());
        }
    }

    protected TooltipPositioner getTooltipPositioner() {
        if (!this.hovered && this.isFocused() && MinecraftClient.getInstance().getNavigationType().isKeyboard()) {
            return new FocusedTooltipPositioner(this);
        }
        return new WidgetTooltipPositioner(this);
    }

    public void setTooltip(@Nullable Tooltip tooltip) {
        this.tooltip = tooltip;
    }

    public void setTooltipDelay(int delay) {
        this.tooltipDelay = delay;
    }

    protected MutableText getNarrationMessage() {
        return ClickableWidget.getNarrationMessage(this.getMessage());
    }

    public static MutableText getNarrationMessage(Text message) {
        return Text.translatable("gui.narrate.button", message);
    }

    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderButton(matrices, mouseX, mouseY);
    }

    protected void renderButton(MatrixStack matrices, int mouseX, int mouseY) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, this.getTexture());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.drawNineSlicedTexture(matrices, this.getX(), this.getY(), this.width, this.height, 4, 200, 20, 0, this.getYImage());
        this.renderBackground(matrices, minecraftClient, mouseX, mouseY);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        int i = this.active ? 0xFFFFFF : 0xA0A0A0;
        TextRenderer textRenderer = minecraftClient.textRenderer;
        int j = textRenderer.getWidth(this.message);
        int k = this.width - 4;
        if (j > k) {
            double d = (double)Util.getMeasuringTimeMs() / 1000.0;
            double e = Math.sin(1.5707963267948966 * Math.cos(d));
            int l = j - k;
            ClickableWidget.enableScissor(this.x + 2, this.y + 2, this.x + this.width - 2, this.y + this.height - 2);
            this.drawMessage(matrices, textRenderer, this.getX() + this.width / 2 - (int)(e * (double)l), this.getY() + (this.height - 8) / 2, i);
            ClickableWidget.disableScissor();
        } else {
            this.drawMessage(matrices, textRenderer, this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, i);
        }
    }

    public void drawMessage(MatrixStack matrices, TextRenderer textRenderer, int centerX, int y, int color) {
        ClickableWidget.drawCenteredTextWithShadow(matrices, textRenderer, this.getMessage(), centerX, y, color | MathHelper.ceil(this.alpha * 255.0f) << 24);
    }

    public void drawTexture(MatrixStack matrices, Identifier texture, int x, int y, int u, int v, int hoveredVOffset, int width, int height, int textureWidth, int textureHeight) {
        RenderSystem.setShaderTexture(0, texture);
        int i = v;
        if (!this.isNarratable()) {
            i += hoveredVOffset * 2;
        } else if (this.isHovered()) {
            i += hoveredVOffset;
        }
        RenderSystem.enableDepthTest();
        ClickableWidget.drawTexture(matrices, x, y, u, i, width, height, textureWidth, textureHeight);
    }

    protected void renderBackground(MatrixStack matrices, MinecraftClient client, int mouseX, int mouseY) {
    }

    public void onClick(double mouseX, double mouseY) {
    }

    public void onRelease(double mouseX, double mouseY) {
    }

    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean bl;
        if (!this.active || !this.visible) {
            return false;
        }
        if (this.isValidClickButton(button) && (bl = this.clicked(mouseX, mouseY))) {
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            this.onClick(mouseX, mouseY);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.isValidClickButton(button)) {
            this.onRelease(mouseX, mouseY);
            return true;
        }
        return false;
    }

    protected boolean isValidClickButton(int button) {
        return button == 0;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.isValidClickButton(button)) {
            this.onDrag(mouseX, mouseY, deltaX, deltaY);
            return true;
        }
        return false;
    }

    protected boolean clicked(double mouseX, double mouseY) {
        return this.active && this.visible && mouseX >= (double)this.getX() && mouseY >= (double)this.getY() && mouseX < (double)(this.getX() + this.width) && mouseY < (double)(this.getY() + this.height);
    }

    public boolean isHovered() {
        return this.hovered || this.isFocused();
    }

    @Override
    @Nullable
    public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        if (!this.active || !this.visible) {
            return null;
        }
        if (!this.isFocused()) {
            return GuiNavigationPath.of(this);
        }
        return null;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.active && this.visible && mouseX >= (double)this.getX() && mouseY >= (double)this.getY() && mouseX < (double)(this.getX() + this.width) && mouseY < (double)(this.getY() + this.height);
    }

    public void playDownSound(SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void setMessage(Text message) {
        this.message = message;
    }

    public Text getMessage() {
        return this.message;
    }

    @Override
    public boolean isFocused() {
        return this.focused;
    }

    @Override
    public boolean isNarratable() {
        return this.visible && this.active;
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public Selectable.SelectionType getType() {
        if (this.isFocused()) {
            return Selectable.SelectionType.FOCUSED;
        }
        if (this.hovered) {
            return Selectable.SelectionType.HOVERED;
        }
        return Selectable.SelectionType.NONE;
    }

    @Override
    public final void appendNarrations(NarrationMessageBuilder builder) {
        this.appendClickableNarrations(builder);
        if (this.tooltip != null) {
            this.tooltip.appendNarrations(builder);
        }
    }

    protected abstract void appendClickableNarrations(NarrationMessageBuilder var1);

    protected void appendDefaultNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, (Text)this.getNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                builder.put(NarrationPart.USAGE, (Text)Text.translatable("narration.button.usage.focused"));
            } else {
                builder.put(NarrationPart.USAGE, (Text)Text.translatable("narration.button.usage.hovered"));
            }
        }
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {
        consumer.accept(this);
    }

    @Override
    public FocusedRect getNavigationFocus() {
        return new FocusedRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    @Override
    public int getNavigationOrder() {
        return this.navigationOrder;
    }

    public void setNavigationOrder(int navigationOrder) {
        this.navigationOrder = navigationOrder;
    }
}

