/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.gui.screen;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class RealmsGenericErrorScreen
extends RealmsScreen {
    private final Screen parent;
    private final Pair<Text, Text> field_36321;
    private MultilineText field_36322 = MultilineText.EMPTY;

    public RealmsGenericErrorScreen(RealmsServiceException realmsServiceException, Screen screen) {
        super(NarratorManager.EMPTY);
        this.parent = screen;
        this.field_36321 = RealmsGenericErrorScreen.method_39981(realmsServiceException);
    }

    public RealmsGenericErrorScreen(Text line2, Screen screen) {
        super(NarratorManager.EMPTY);
        this.parent = screen;
        this.field_36321 = RealmsGenericErrorScreen.errorMessage(line2);
    }

    public RealmsGenericErrorScreen(Text line1, Text line2, Screen screen) {
        super(NarratorManager.EMPTY);
        this.parent = screen;
        this.field_36321 = RealmsGenericErrorScreen.errorMessage(line1, line2);
    }

    private static Pair<Text, Text> method_39981(RealmsServiceException realmsServiceException) {
        if (realmsServiceException.field_36320 == null) {
            return Pair.of(new LiteralText("An error occurred (" + realmsServiceException.httpResultCode + "):"), new LiteralText(realmsServiceException.field_36319));
        }
        String string = "mco.errorMessage." + realmsServiceException.field_36320.getErrorCode();
        return Pair.of(new LiteralText("Realms (" + realmsServiceException.field_36320 + "):"), I18n.hasTranslation(string) ? new TranslatableText(string) : Text.of(realmsServiceException.field_36320.getErrorMessage()));
    }

    private static Pair<Text, Text> errorMessage(Text text) {
        return Pair.of(new LiteralText("An error occurred: "), text);
    }

    private static Pair<Text, Text> errorMessage(Text text, Text text2) {
        return Pair.of(text, text2);
    }

    @Override
    public void init() {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 52, 200, 20, new LiteralText("Ok"), button -> this.client.setScreen(this.parent)));
        this.field_36322 = MultilineText.create(this.textRenderer, (StringVisitable)this.field_36321.getSecond(), this.width * 3 / 4);
    }

    @Override
    public Text getNarratedTitle() {
        return new LiteralText("").append(this.field_36321.getFirst()).append(": ").append(this.field_36321.getSecond());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        RealmsGenericErrorScreen.drawCenteredText(matrices, this.textRenderer, this.field_36321.getFirst(), this.width / 2, 80, 0xFFFFFF);
        this.field_36322.drawCenterWithShadow(matrices, this.width / 2, 100, this.client.textRenderer.fontHeight, 0xFF0000);
        super.render(matrices, mouseX, mouseY, delta);
    }
}

