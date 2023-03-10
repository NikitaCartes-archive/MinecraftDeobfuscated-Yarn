/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

@Environment(value=EnvType.CLIENT)
public class DialogScreen
extends Screen {
    private static final int BUTTON_PADDING = 20;
    private static final int BUTTON_MARGIN = 5;
    private static final int BUTTON_HEIGHT = 20;
    private final Text narrationMessage;
    private final StringVisitable message;
    private final ImmutableList<ChoiceButton> choiceButtons;
    private MultilineText lines = MultilineText.EMPTY;
    private int linesY;
    private int buttonWidth;

    protected DialogScreen(Text title, List<Text> messages, ImmutableList<ChoiceButton> choiceButtons) {
        super(title);
        this.message = StringVisitable.concat(messages);
        this.narrationMessage = ScreenTexts.joinSentences(title, Texts.join(messages, ScreenTexts.EMPTY));
        this.choiceButtons = choiceButtons;
    }

    @Override
    public Text getNarratedTitle() {
        return this.narrationMessage;
    }

    @Override
    public void init() {
        for (ChoiceButton choiceButton : this.choiceButtons) {
            this.buttonWidth = Math.max(this.buttonWidth, 20 + this.textRenderer.getWidth(choiceButton.message) + 20);
        }
        int i = 5 + this.buttonWidth + 5;
        int j = i * this.choiceButtons.size();
        this.lines = MultilineText.create(this.textRenderer, this.message, j);
        int k = this.lines.count() * this.textRenderer.fontHeight;
        this.linesY = (int)((double)this.height / 2.0 - (double)k / 2.0);
        int l = this.linesY + k + this.textRenderer.fontHeight * 2;
        int m = (int)((double)this.width / 2.0 - (double)j / 2.0);
        for (ChoiceButton choiceButton2 : this.choiceButtons) {
            this.addDrawableChild(ButtonWidget.builder(choiceButton2.message, choiceButton2.pressAction).dimensions(m, l, this.buttonWidth, 20).build());
            m += i;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(matrices);
        DialogScreen.drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, this.linesY - this.textRenderer.fontHeight * 2, -1);
        this.lines.drawCenterWithShadow(matrices, this.width / 2, this.linesY);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class ChoiceButton {
        final Text message;
        final ButtonWidget.PressAction pressAction;

        public ChoiceButton(Text message, ButtonWidget.PressAction pressAction) {
            this.message = message;
            this.pressAction = pressAction;
        }
    }
}

