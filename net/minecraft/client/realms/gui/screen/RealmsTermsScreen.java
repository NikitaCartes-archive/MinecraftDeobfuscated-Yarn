/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import java.util.concurrent.locks.ReentrantLock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.task.RealmsGetServerDetailsTask;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsTermsScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Text TITLE = Text.translatable("mco.terms.title");
    private static final Text SENTENCE_ONE_TEXT = Text.translatable("mco.terms.sentence.1");
    private static final Text SENTENCE_TWO_TEXT = Text.literal(" ").append(Text.translatable("mco.terms.sentence.2").fillStyle(Style.EMPTY.withUnderline(true)));
    private final Screen parent;
    private final RealmsMainScreen mainScreen;
    private final RealmsServer realmsServer;
    private boolean onLink;
    private final String realmsToSUrl = "https://aka.ms/MinecraftRealmsTerms";

    public RealmsTermsScreen(Screen parent, RealmsMainScreen mainScreen, RealmsServer realmsServer) {
        super(TITLE);
        this.parent = parent;
        this.mainScreen = mainScreen;
        this.realmsServer = realmsServer;
    }

    @Override
    public void init() {
        this.client.keyboard.setRepeatEvents(true);
        int i = this.width / 4 - 2;
        this.addDrawableChild(new ButtonWidget(this.width / 4, RealmsTermsScreen.row(12), i, 20, Text.translatable("mco.terms.buttons.agree"), button -> this.agreedToTos()));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 4, RealmsTermsScreen.row(12), i, 20, Text.translatable("mco.terms.buttons.disagree"), button -> this.client.setScreen(this.parent)));
    }

    @Override
    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.setScreen(this.parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void agreedToTos() {
        RealmsClient realmsClient = RealmsClient.method_44616();
        try {
            realmsClient.agreeToTos();
            this.client.setScreen(new RealmsLongRunningMcoTaskScreen(this.parent, new RealmsGetServerDetailsTask(this.mainScreen, this.parent, this.realmsServer, new ReentrantLock())));
        } catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't agree to TOS");
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.onLink) {
            this.client.keyboard.setClipboard("https://aka.ms/MinecraftRealmsTerms");
            Util.getOperatingSystem().open("https://aka.ms/MinecraftRealmsTerms");
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences(super.getNarratedTitle(), SENTENCE_ONE_TEXT).append(" ").append(SENTENCE_TWO_TEXT);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        RealmsTermsScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 17, 0xFFFFFF);
        this.textRenderer.draw(matrices, SENTENCE_ONE_TEXT, (float)(this.width / 2 - 120), (float)RealmsTermsScreen.row(5), 0xFFFFFF);
        int i = this.textRenderer.getWidth(SENTENCE_ONE_TEXT);
        int j = this.width / 2 - 121 + i;
        int k = RealmsTermsScreen.row(5);
        int l = j + this.textRenderer.getWidth(SENTENCE_TWO_TEXT) + 1;
        int m = k + 1 + this.textRenderer.fontHeight;
        this.onLink = j <= mouseX && mouseX <= l && k <= mouseY && mouseY <= m;
        this.textRenderer.draw(matrices, SENTENCE_TWO_TEXT, (float)(this.width / 2 - 120 + i), (float)RealmsTermsScreen.row(5), this.onLink ? 7107012 : 0x3366BB);
        super.render(matrices, mouseX, mouseY, delta);
    }
}

