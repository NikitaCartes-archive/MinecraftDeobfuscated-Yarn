/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.options;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.options.GameOptionsScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class LanguageOptionsScreen
extends GameOptionsScreen {
    private LanguageSelectionListWidget languageSelectionList;
    private final LanguageManager languageManager;
    private OptionButtonWidget forceUnicodeButton;
    private ButtonWidget doneButton;

    public LanguageOptionsScreen(Screen parent, GameOptions options, LanguageManager languageManager) {
        super(parent, options, new TranslatableText("options.language"));
        this.languageManager = languageManager;
    }

    @Override
    protected void init() {
        this.languageSelectionList = new LanguageSelectionListWidget(this.client);
        this.children.add(this.languageSelectionList);
        this.forceUnicodeButton = this.addButton(new OptionButtonWidget(this.width / 2 - 155, this.height - 38, 150, 20, Option.FORCE_UNICODE_FONT, Option.FORCE_UNICODE_FONT.getDisplayString(this.gameOptions), buttonWidget -> {
            Option.FORCE_UNICODE_FONT.set(this.gameOptions);
            this.gameOptions.write();
            buttonWidget.setMessage(Option.FORCE_UNICODE_FONT.getDisplayString(this.gameOptions));
            this.client.onResolutionChanged();
        }));
        this.doneButton = this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 38, 150, 20, ScreenTexts.DONE, buttonWidget -> {
            LanguageSelectionListWidget.LanguageEntry languageEntry = (LanguageSelectionListWidget.LanguageEntry)this.languageSelectionList.getSelected();
            if (languageEntry != null && !languageEntry.languageDefinition.getCode().equals(this.languageManager.getLanguage().getCode())) {
                this.languageManager.setLanguage(languageEntry.languageDefinition);
                this.gameOptions.language = languageEntry.languageDefinition.getCode();
                this.client.reloadResources();
                this.doneButton.setMessage(ScreenTexts.DONE);
                this.forceUnicodeButton.setMessage(Option.FORCE_UNICODE_FONT.getDisplayString(this.gameOptions));
                this.gameOptions.write();
            }
            this.client.openScreen(this.parent);
        }));
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.languageSelectionList.render(matrices, mouseX, mouseY, delta);
        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 16, 0xFFFFFF);
        this.drawCenteredString(matrices, this.textRenderer, "(" + I18n.translate("options.languageWarning", new Object[0]) + ")", this.width / 2, this.height - 56, 0x808080);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Environment(value=EnvType.CLIENT)
    class LanguageSelectionListWidget
    extends AlwaysSelectedEntryListWidget<LanguageEntry> {
        public LanguageSelectionListWidget(MinecraftClient client) {
            super(client, LanguageOptionsScreen.this.width, LanguageOptionsScreen.this.height, 32, LanguageOptionsScreen.this.height - 65 + 4, 18);
            for (LanguageDefinition languageDefinition : LanguageOptionsScreen.this.languageManager.getAllLanguages()) {
                LanguageEntry languageEntry = new LanguageEntry(languageDefinition);
                this.addEntry(languageEntry);
                if (!LanguageOptionsScreen.this.languageManager.getLanguage().getCode().equals(languageDefinition.getCode())) continue;
                this.setSelected(languageEntry);
            }
            if (this.getSelected() != null) {
                this.centerScrollOn(this.getSelected());
            }
        }

        @Override
        protected int getScrollbarPositionX() {
            return super.getScrollbarPositionX() + 20;
        }

        @Override
        public int getRowWidth() {
            return super.getRowWidth() + 50;
        }

        @Override
        public void setSelected(@Nullable LanguageEntry languageEntry) {
            super.setSelected(languageEntry);
            if (languageEntry != null) {
                NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.select", languageEntry.languageDefinition).getString());
            }
        }

        @Override
        protected void renderBackground(MatrixStack matrixStack) {
            LanguageOptionsScreen.this.renderBackground(matrixStack);
        }

        @Override
        protected boolean isFocused() {
            return LanguageOptionsScreen.this.getFocused() == this;
        }

        @Environment(value=EnvType.CLIENT)
        public class LanguageEntry
        extends AlwaysSelectedEntryListWidget.Entry<LanguageEntry> {
            private final LanguageDefinition languageDefinition;

            public LanguageEntry(LanguageDefinition languageDefinition) {
                this.languageDefinition = languageDefinition;
            }

            @Override
            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                String string = this.languageDefinition.toString();
                LanguageOptionsScreen.this.textRenderer.drawWithShadow(matrices, string, LanguageSelectionListWidget.this.width / 2 - LanguageOptionsScreen.this.textRenderer.getWidth(string) / 2, y + 1, 0xFFFFFF, true);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0) {
                    this.onPressed();
                    return true;
                }
                return false;
            }

            private void onPressed() {
                LanguageSelectionListWidget.this.setSelected(this);
            }
        }
    }
}

