/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class WarningScreen
extends Screen {
    private final Text header;
    private final Text message;
    private final Text checkMessage;
    private final Text narratedText;
    protected final Screen parent;
    @Nullable
    protected CheckboxWidget checkbox;
    private MultilineText messageText = MultilineText.EMPTY;

    protected WarningScreen(Text header, Text message, Text checkMessage, Text narratedText, Screen parent) {
        super(NarratorManager.EMPTY);
        this.header = header;
        this.message = message;
        this.checkMessage = checkMessage;
        this.narratedText = narratedText;
        this.parent = parent;
    }

    protected abstract void initButtons(int var1);

    @Override
    protected void init() {
        super.init();
        this.messageText = MultilineText.create(this.textRenderer, (StringVisitable)this.message, this.width - 50);
        int i = (this.messageText.count() + 1) * this.textRenderer.fontHeight * 2;
        this.checkbox = new CheckboxWidget(this.width / 2 - 155 + 80, 76 + i, 150, 20, this.checkMessage, false);
        this.addDrawableChild(this.checkbox);
        this.initButtons(i);
    }

    @Override
    public Text getNarratedTitle() {
        return this.narratedText;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(0);
        WarningScreen.drawTextWithShadow(matrices, this.textRenderer, this.header, 25, 30, 0xFFFFFF);
        this.messageText.drawWithShadow(matrices, 25, 70, this.textRenderer.fontHeight * 2, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }
}

