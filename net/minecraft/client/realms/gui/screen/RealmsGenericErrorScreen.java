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
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class RealmsGenericErrorScreen
extends RealmsScreen {
    private final Screen parent;
    private final Pair<Text, Text> errorMessages;
    private MultilineText description = MultilineText.EMPTY;

    public RealmsGenericErrorScreen(RealmsServiceException realmsServiceException, Screen parent) {
        super(NarratorManager.EMPTY);
        this.parent = parent;
        this.errorMessages = RealmsGenericErrorScreen.getErrorMessages(realmsServiceException);
    }

    public RealmsGenericErrorScreen(Text description, Screen parent) {
        super(NarratorManager.EMPTY);
        this.parent = parent;
        this.errorMessages = RealmsGenericErrorScreen.getErrorMessages(description);
    }

    public RealmsGenericErrorScreen(Text title, Text description, Screen parent) {
        super(NarratorManager.EMPTY);
        this.parent = parent;
        this.errorMessages = RealmsGenericErrorScreen.getErrorMessages(title, description);
    }

    private static Pair<Text, Text> getErrorMessages(RealmsServiceException exception) {
        if (exception.error == null) {
            return Pair.of(Text.method_43470("An error occurred (" + exception.httpResultCode + "):"), Text.method_43470(exception.httpResponseText));
        }
        String string = "mco.errorMessage." + exception.error.getErrorCode();
        return Pair.of(Text.method_43470("Realms (" + exception.error + "):"), I18n.hasTranslation(string) ? Text.method_43471(string) : Text.of(exception.error.getErrorMessage()));
    }

    private static Pair<Text, Text> getErrorMessages(Text description) {
        return Pair.of(Text.method_43470("An error occurred: "), description);
    }

    private static Pair<Text, Text> getErrorMessages(Text title, Text description) {
        return Pair.of(title, description);
    }

    @Override
    public void init() {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 52, 200, 20, Text.method_43470("Ok"), button -> this.client.setScreen(this.parent)));
        this.description = MultilineText.create(this.textRenderer, (StringVisitable)this.errorMessages.getSecond(), this.width * 3 / 4);
    }

    @Override
    public Text getNarratedTitle() {
        return Text.method_43473().append(this.errorMessages.getFirst()).append(": ").append(this.errorMessages.getSecond());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        RealmsGenericErrorScreen.drawCenteredText(matrices, this.textRenderer, this.errorMessages.getFirst(), this.width / 2, 80, 0xFFFFFF);
        this.description.drawCenterWithShadow(matrices, this.width / 2, 100, this.client.textRenderer.fontHeight, 0xFF0000);
        super.render(matrices, mouseX, mouseY, delta);
    }
}

