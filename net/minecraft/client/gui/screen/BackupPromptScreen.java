/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.chat.Component;

@Environment(value=EnvType.CLIENT)
public class BackupPromptScreen
extends Screen {
    private final Screen parent;
    protected final Callback callback;
    private final Component subtitle;
    private final boolean showEraseCacheCheckbox;
    private final List<String> wrappedText = Lists.newArrayList();
    private final String eraseCacheText;
    private final String confirmText;
    private final String skipText;
    private final String cancelText;
    private CheckboxWidget eraseCacheCheckbox;

    public BackupPromptScreen(Screen screen, Callback callback, Component component, Component component2, boolean bl) {
        super(component);
        this.parent = screen;
        this.callback = callback;
        this.subtitle = component2;
        this.showEraseCacheCheckbox = bl;
        this.eraseCacheText = I18n.translate("selectWorld.backupEraseCache", new Object[0]);
        this.confirmText = I18n.translate("selectWorld.backupJoinConfirmButton", new Object[0]);
        this.skipText = I18n.translate("selectWorld.backupJoinSkipButton", new Object[0]);
        this.cancelText = I18n.translate("gui.cancel", new Object[0]);
    }

    @Override
    protected void init() {
        super.init();
        this.wrappedText.clear();
        this.wrappedText.addAll(this.font.wrapStringToWidthAsList(this.subtitle.getFormattedText(), this.width - 50));
        int i = (this.wrappedText.size() + 1) * this.font.fontHeight;
        this.addButton(new ButtonWidget(this.width / 2 - 155, 100 + i, 150, 20, this.confirmText, buttonWidget -> this.callback.proceed(true, this.eraseCacheCheckbox.isChecked())));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, 100 + i, 150, 20, this.skipText, buttonWidget -> this.callback.proceed(false, this.eraseCacheCheckbox.isChecked())));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 80, 124 + i, 150, 20, this.cancelText, buttonWidget -> this.minecraft.openScreen(this.parent)));
        this.eraseCacheCheckbox = new CheckboxWidget(this.width / 2 - 155 + 80, 76 + i, 150, 20, this.eraseCacheText, false);
        if (this.showEraseCacheCheckbox) {
            this.addButton(this.eraseCacheCheckbox);
        }
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 50, 0xFFFFFF);
        int k = 70;
        for (String string : this.wrappedText) {
            this.drawCenteredString(this.font, string, this.width / 2, k, 0xFFFFFF);
            k += this.font.fontHeight;
        }
        super.render(i, j, f);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256) {
            this.minecraft.openScreen(this.parent);
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Callback {
        public void proceed(boolean var1, boolean var2);
    }
}

