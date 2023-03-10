/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class ConfirmLinkScreen
extends ConfirmScreen {
    private static final Text COPY = Text.translatable("chat.copy");
    private static final Text WARNING = Text.translatable("chat.link.warning");
    private final String link;
    private final boolean drawWarning;

    public ConfirmLinkScreen(BooleanConsumer callback, String link, boolean linkTrusted) {
        this(callback, ConfirmLinkScreen.getConfirmText(linkTrusted), Text.literal(link), link, linkTrusted ? ScreenTexts.CANCEL : ScreenTexts.NO, linkTrusted);
    }

    public ConfirmLinkScreen(BooleanConsumer callback, Text title, String link, boolean linkTrusted) {
        this(callback, title, link, linkTrusted ? ScreenTexts.CANCEL : ScreenTexts.NO, linkTrusted);
    }

    public ConfirmLinkScreen(BooleanConsumer callback, Text title, String link, Text noText, boolean linkTrusted) {
        this(callback, title, ConfirmLinkScreen.getConfirmText(linkTrusted, link), link, noText, linkTrusted);
    }

    public ConfirmLinkScreen(BooleanConsumer callback, Text title, Text message, String link, Text noText, boolean linkTrusted) {
        super(callback, title, message);
        this.yesText = linkTrusted ? Text.translatable("chat.link.open") : ScreenTexts.YES;
        this.noText = noText;
        this.drawWarning = !linkTrusted;
        this.link = link;
    }

    protected static MutableText getConfirmText(boolean linkTrusted, String link) {
        return ConfirmLinkScreen.getConfirmText(linkTrusted).append(ScreenTexts.SPACE).append(Text.literal(link));
    }

    protected static MutableText getConfirmText(boolean linkTrusted) {
        return Text.translatable(linkTrusted ? "chat.link.confirmTrusted" : "chat.link.confirm");
    }

    @Override
    protected void addButtons(int y) {
        this.addDrawableChild(ButtonWidget.builder(this.yesText, button -> this.callback.accept(true)).dimensions(this.width / 2 - 50 - 105, y, 100, 20).build());
        this.addDrawableChild(ButtonWidget.builder(COPY, button -> {
            this.copyToClipboard();
            this.callback.accept(false);
        }).dimensions(this.width / 2 - 50, y, 100, 20).build());
        this.addDrawableChild(ButtonWidget.builder(this.noText, button -> this.callback.accept(false)).dimensions(this.width / 2 - 50 + 105, y, 100, 20).build());
    }

    public void copyToClipboard() {
        this.client.keyboard.setClipboard(this.link);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        if (this.drawWarning) {
            ConfirmLinkScreen.drawCenteredTextWithShadow(matrices, this.textRenderer, WARNING, this.width / 2, 110, 0xFFCCCC);
        }
    }

    /**
     * Opens the confirmation screen to open {@code url}.
     * 
     * @see #opening
     */
    public static void open(String url, Screen parent, boolean linkTrusted) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.setScreen(new ConfirmLinkScreen(confirmed -> {
            if (confirmed) {
                Util.getOperatingSystem().open(url);
            }
            minecraftClient.setScreen(parent);
        }, url, linkTrusted));
    }

    /**
     * {@return the button press action that opens the confirmation screen to open {@code url}}
     * 
     * @see #open
     */
    public static ButtonWidget.PressAction opening(String url, Screen parent, boolean linkTrusted) {
        return button -> ConfirmLinkScreen.open(url, parent, linkTrusted);
    }
}

