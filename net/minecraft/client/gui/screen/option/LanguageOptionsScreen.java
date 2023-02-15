/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(value=EnvType.CLIENT)
public class LanguageOptionsScreen
extends GameOptionsScreen {
    private static final Text LANGUAGE_WARNING_TEXT = Text.literal("(").append(Text.translatable("options.languageWarning")).append(")").formatted(Formatting.GRAY);
    private LanguageSelectionListWidget languageSelectionList;
    final LanguageManager languageManager;

    public LanguageOptionsScreen(Screen parent, GameOptions options, LanguageManager languageManager) {
        super(parent, options, Text.translatable("options.language"));
        this.languageManager = languageManager;
    }

    @Override
    protected void init() {
        this.languageSelectionList = new LanguageSelectionListWidget(this.client);
        this.addSelectableChild(this.languageSelectionList);
        this.addDrawableChild(this.gameOptions.getForceUnicodeFont().createWidget(this.gameOptions, this.width / 2 - 155, this.height - 38, 150));
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> {
            LanguageSelectionListWidget.LanguageEntry languageEntry = (LanguageSelectionListWidget.LanguageEntry)this.languageSelectionList.getSelectedOrNull();
            if (languageEntry != null && !languageEntry.languageCode.equals(this.languageManager.getLanguage())) {
                this.languageManager.setLanguage(languageEntry.languageCode);
                this.gameOptions.language = languageEntry.languageCode;
                this.client.reloadResources();
                this.gameOptions.write();
            }
            this.client.setScreen(this.parent);
        }).dimensions(this.width / 2 - 155 + 160, this.height - 38, 150, 20).build());
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.languageSelectionList.render(matrices, mouseX, mouseY, delta);
        LanguageOptionsScreen.drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 16, 0xFFFFFF);
        LanguageOptionsScreen.drawCenteredTextWithShadow(matrices, this.textRenderer, LANGUAGE_WARNING_TEXT, this.width / 2, this.height - 56, 0x808080);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Environment(value=EnvType.CLIENT)
    class LanguageSelectionListWidget
    extends AlwaysSelectedEntryListWidget<LanguageEntry> {
        public LanguageSelectionListWidget(MinecraftClient client) {
            super(client, LanguageOptionsScreen.this.width, LanguageOptionsScreen.this.height, 32, LanguageOptionsScreen.this.height - 65 + 4, 18);
            String string = LanguageOptionsScreen.this.languageManager.getLanguage();
            LanguageOptionsScreen.this.languageManager.getAllLanguages().forEach((languageCode, languageDefinition) -> {
                LanguageEntry languageEntry = new LanguageEntry((String)languageCode, (LanguageDefinition)languageDefinition);
                this.addEntry(languageEntry);
                if (string.equals(languageCode)) {
                    this.setSelected(languageEntry);
                }
            });
            if (this.getSelectedOrNull() != null) {
                this.centerScrollOn((LanguageEntry)this.getSelectedOrNull());
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
        protected void renderBackground(MatrixStack matrices) {
            LanguageOptionsScreen.this.renderBackground(matrices);
        }

        @Environment(value=EnvType.CLIENT)
        public class LanguageEntry
        extends AlwaysSelectedEntryListWidget.Entry<LanguageEntry> {
            final String languageCode;
            private final Text languageDefinition;

            public LanguageEntry(String languageCode, LanguageDefinition languageDefinition) {
                this.languageCode = languageCode;
                this.languageDefinition = languageDefinition.getDisplayText();
            }

            @Override
            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                LanguageOptionsScreen.this.textRenderer.drawWithShadow(matrices, this.languageDefinition, (float)(LanguageSelectionListWidget.this.width / 2 - LanguageOptionsScreen.this.textRenderer.getWidth(this.languageDefinition) / 2), (float)(y + 1), 0xFFFFFF);
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

            @Override
            public Text getNarration() {
                return Text.translatable("narrator.select", this.languageDefinition);
            }
        }
    }
}

