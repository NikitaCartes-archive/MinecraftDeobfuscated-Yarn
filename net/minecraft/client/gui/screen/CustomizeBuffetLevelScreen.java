/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CustomizeBuffetLevelScreen
extends Screen {
    private static final Text field_26535 = new TranslatableText("createWorld.customize.buffet.biome");
    private final Screen field_24562;
    private final Consumer<Biome> field_24563;
    private final MutableRegistry<Biome> field_25888;
    private BuffetBiomesListWidget biomeSelectionList;
    private Biome field_25040;
    private ButtonWidget confirmButton;

    public CustomizeBuffetLevelScreen(Screen screen, DynamicRegistryManager dynamicRegistryManager, Consumer<Biome> consumer, Biome biome) {
        super(new TranslatableText("createWorld.customize.buffet.title"));
        this.field_24562 = screen;
        this.field_24563 = consumer;
        this.field_25040 = biome;
        this.field_25888 = dynamicRegistryManager.get(Registry.BIOME_KEY);
    }

    @Override
    public void onClose() {
        this.client.openScreen(this.field_24562);
    }

    @Override
    protected void init() {
        this.client.keyboard.setRepeatEvents(true);
        this.biomeSelectionList = new BuffetBiomesListWidget();
        this.children.add(this.biomeSelectionList);
        this.confirmButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, ScreenTexts.DONE, buttonWidget -> {
            this.field_24563.accept(this.field_25040);
            this.client.openScreen(this.field_24562);
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, buttonWidget -> this.client.openScreen(this.field_24562)));
        this.biomeSelectionList.setSelected((BuffetBiomesListWidget.BuffetBiomeItem)this.biomeSelectionList.children().stream().filter(buffetBiomeItem -> Objects.equals(((BuffetBiomesListWidget.BuffetBiomeItem)buffetBiomeItem).field_24564, this.field_25040)).findFirst().orElse(null));
    }

    private void refreshConfirmButton() {
        this.confirmButton.active = this.biomeSelectionList.getSelected() != null;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(0);
        this.biomeSelectionList.render(matrices, mouseX, mouseY, delta);
        CustomizeBuffetLevelScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        CustomizeBuffetLevelScreen.drawCenteredText(matrices, this.textRenderer, field_26535, this.width / 2, 28, 0xA0A0A0);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Environment(value=EnvType.CLIENT)
    class BuffetBiomesListWidget
    extends AlwaysSelectedEntryListWidget<BuffetBiomeItem> {
        private BuffetBiomesListWidget() {
            super(CustomizeBuffetLevelScreen.this.client, CustomizeBuffetLevelScreen.this.width, CustomizeBuffetLevelScreen.this.height, 40, CustomizeBuffetLevelScreen.this.height - 37, 16);
            CustomizeBuffetLevelScreen.this.field_25888.getEntries().stream().sorted(Comparator.comparing(entry -> ((RegistryKey)entry.getKey()).getValue().toString())).forEach(entry -> this.addEntry(new BuffetBiomeItem((Biome)entry.getValue())));
        }

        @Override
        protected boolean isFocused() {
            return CustomizeBuffetLevelScreen.this.getFocused() == this;
        }

        @Override
        public void setSelected(@Nullable BuffetBiomeItem buffetBiomeItem) {
            super.setSelected(buffetBiomeItem);
            if (buffetBiomeItem != null) {
                CustomizeBuffetLevelScreen.this.field_25040 = buffetBiomeItem.field_24564;
                NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.select", CustomizeBuffetLevelScreen.this.field_25888.getId(buffetBiomeItem.field_24564)).getString());
            }
            CustomizeBuffetLevelScreen.this.refreshConfirmButton();
        }

        @Environment(value=EnvType.CLIENT)
        class BuffetBiomeItem
        extends AlwaysSelectedEntryListWidget.Entry<BuffetBiomeItem> {
            private final Biome field_24564;
            private final Text field_26536;

            public BuffetBiomeItem(Biome biome) {
                this.field_24564 = biome;
                Identifier identifier = CustomizeBuffetLevelScreen.this.field_25888.getId(biome);
                String string = "biome." + identifier.getNamespace() + "." + identifier.getPath();
                this.field_26536 = Language.getInstance().hasTranslation(string) ? new TranslatableText(string) : new LiteralText(identifier.toString());
            }

            @Override
            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                DrawableHelper.drawTextWithShadow(matrices, CustomizeBuffetLevelScreen.this.textRenderer, this.field_26536, x + 5, y + 2, 0xFFFFFF);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0) {
                    BuffetBiomesListWidget.this.setSelected(this);
                    return true;
                }
                return false;
            }
        }
    }
}

