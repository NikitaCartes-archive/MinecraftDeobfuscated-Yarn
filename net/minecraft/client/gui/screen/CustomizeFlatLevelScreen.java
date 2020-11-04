/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.PresetsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CustomizeFlatLevelScreen
extends Screen {
    protected final CreateWorldScreen parent;
    private final Consumer<FlatChunkGeneratorConfig> field_24565;
    private FlatChunkGeneratorConfig config;
    private Text tileText;
    private Text heightText;
    private SuperflatLayersListWidget layers;
    private ButtonWidget widgetButtonRemoveLayer;

    public CustomizeFlatLevelScreen(CreateWorldScreen createWorldScreen, Consumer<FlatChunkGeneratorConfig> consumer, FlatChunkGeneratorConfig flatChunkGeneratorConfig) {
        super(new TranslatableText("createWorld.customize.flat.title"));
        this.parent = createWorldScreen;
        this.field_24565 = consumer;
        this.config = flatChunkGeneratorConfig;
    }

    public FlatChunkGeneratorConfig method_29055() {
        return this.config;
    }

    public void method_29054(FlatChunkGeneratorConfig flatChunkGeneratorConfig) {
        this.config = flatChunkGeneratorConfig;
    }

    @Override
    protected void init() {
        this.tileText = new TranslatableText("createWorld.customize.flat.tile");
        this.heightText = new TranslatableText("createWorld.customize.flat.height");
        this.layers = new SuperflatLayersListWidget();
        this.children.add(this.layers);
        this.widgetButtonRemoveLayer = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 52, 150, 20, new TranslatableText("createWorld.customize.flat.removeLayer"), buttonWidget -> {
            if (!this.method_2147()) {
                return;
            }
            List<FlatChunkGeneratorLayer> list = this.config.getLayers();
            int i = this.layers.children().indexOf(this.layers.getSelected());
            int j = list.size() - i - 1;
            list.remove(j);
            this.layers.setSelected(list.isEmpty() ? null : (SuperflatLayersListWidget.SuperflatLayerItem)this.layers.children().get(Math.min(i, list.size() - 1)));
            this.config.updateLayerBlocks();
            this.layers.method_19372();
            this.method_2145();
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 52, 150, 20, new TranslatableText("createWorld.customize.presets"), buttonWidget -> {
            this.client.openScreen(new PresetsScreen(this));
            this.config.updateLayerBlocks();
            this.method_2145();
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, ScreenTexts.DONE, buttonWidget -> {
            this.field_24565.accept(this.config);
            this.client.openScreen(this.parent);
            this.config.updateLayerBlocks();
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, buttonWidget -> {
            this.client.openScreen(this.parent);
            this.config.updateLayerBlocks();
        }));
        this.config.updateLayerBlocks();
        this.method_2145();
    }

    private void method_2145() {
        this.widgetButtonRemoveLayer.active = this.method_2147();
    }

    private boolean method_2147() {
        return this.layers.getSelected() != null;
    }

    @Override
    public void onClose() {
        this.client.openScreen(this.parent);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.layers.render(matrices, mouseX, mouseY, delta);
        CustomizeFlatLevelScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        int i = this.width / 2 - 92 - 16;
        CustomizeFlatLevelScreen.drawTextWithShadow(matrices, this.textRenderer, this.tileText, i, 32, 0xFFFFFF);
        CustomizeFlatLevelScreen.drawTextWithShadow(matrices, this.textRenderer, this.heightText, i + 2 + 213 - this.textRenderer.getWidth(this.heightText), 32, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Environment(value=EnvType.CLIENT)
    class SuperflatLayersListWidget
    extends AlwaysSelectedEntryListWidget<SuperflatLayerItem> {
        public SuperflatLayersListWidget() {
            super(CustomizeFlatLevelScreen.this.client, CustomizeFlatLevelScreen.this.width, CustomizeFlatLevelScreen.this.height, 43, CustomizeFlatLevelScreen.this.height - 60, 24);
            for (int i = 0; i < CustomizeFlatLevelScreen.this.config.getLayers().size(); ++i) {
                this.addEntry(new SuperflatLayerItem());
            }
        }

        @Override
        public void setSelected(@Nullable SuperflatLayerItem superflatLayerItem) {
            FlatChunkGeneratorLayer flatChunkGeneratorLayer;
            ItemStack itemStack;
            super.setSelected(superflatLayerItem);
            if (superflatLayerItem != null && !(itemStack = new ItemStack((flatChunkGeneratorLayer = CustomizeFlatLevelScreen.this.config.getLayers().get(CustomizeFlatLevelScreen.this.config.getLayers().size() - this.children().indexOf(superflatLayerItem) - 1)).getBlockState().getBlock())).isEmpty()) {
                NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.select", itemStack.getItem().getName(itemStack)).getString());
            }
            CustomizeFlatLevelScreen.this.method_2145();
        }

        @Override
        protected boolean isFocused() {
            return CustomizeFlatLevelScreen.this.getFocused() == this;
        }

        @Override
        protected int getScrollbarPositionX() {
            return this.width - 70;
        }

        public void method_19372() {
            int i = this.children().indexOf(this.getSelected());
            this.clearEntries();
            for (int j = 0; j < CustomizeFlatLevelScreen.this.config.getLayers().size(); ++j) {
                this.addEntry(new SuperflatLayerItem());
            }
            List list = this.children();
            if (i >= 0 && i < list.size()) {
                this.setSelected((SuperflatLayerItem)list.get(i));
            }
        }

        @Environment(value=EnvType.CLIENT)
        class SuperflatLayerItem
        extends AlwaysSelectedEntryListWidget.Entry<SuperflatLayerItem> {
            private SuperflatLayerItem() {
            }

            @Override
            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                FlatChunkGeneratorLayer flatChunkGeneratorLayer = CustomizeFlatLevelScreen.this.config.getLayers().get(CustomizeFlatLevelScreen.this.config.getLayers().size() - index - 1);
                BlockState blockState = flatChunkGeneratorLayer.getBlockState();
                Item item = blockState.getBlock().asItem();
                if (item == Items.AIR) {
                    if (blockState.isOf(Blocks.WATER)) {
                        item = Items.WATER_BUCKET;
                    } else if (blockState.isOf(Blocks.LAVA)) {
                        item = Items.LAVA_BUCKET;
                    }
                }
                ItemStack itemStack = new ItemStack(item);
                this.method_19375(matrices, x, y, itemStack);
                CustomizeFlatLevelScreen.this.textRenderer.draw(matrices, item.getName(itemStack), (float)(x + 18 + 5), (float)(y + 3), 0xFFFFFF);
                String string = index == 0 ? I18n.translate("createWorld.customize.flat.layer.top", flatChunkGeneratorLayer.getThickness()) : (index == CustomizeFlatLevelScreen.this.config.getLayers().size() - 1 ? I18n.translate("createWorld.customize.flat.layer.bottom", flatChunkGeneratorLayer.getThickness()) : I18n.translate("createWorld.customize.flat.layer", flatChunkGeneratorLayer.getThickness()));
                CustomizeFlatLevelScreen.this.textRenderer.draw(matrices, string, (float)(x + 2 + 213 - CustomizeFlatLevelScreen.this.textRenderer.getWidth(string)), (float)(y + 3), 0xFFFFFF);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0) {
                    SuperflatLayersListWidget.this.setSelected(this);
                    return true;
                }
                return false;
            }

            private void method_19375(MatrixStack matrixStack, int i, int j, ItemStack itemStack) {
                this.method_19373(matrixStack, i + 1, j + 1);
                RenderSystem.enableRescaleNormal();
                if (!itemStack.isEmpty()) {
                    CustomizeFlatLevelScreen.this.itemRenderer.renderGuiItemIcon(itemStack, i + 2, j + 2);
                }
                RenderSystem.disableRescaleNormal();
            }

            private void method_19373(MatrixStack matrixStack, int i, int j) {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                SuperflatLayersListWidget.this.client.getTextureManager().bindTexture(DrawableHelper.STATS_ICON_TEXTURE);
                DrawableHelper.drawTexture(matrixStack, i, j, CustomizeFlatLevelScreen.this.getZOffset(), 0.0f, 0.0f, 18, 18, 128, 128);
            }
        }
    }
}

