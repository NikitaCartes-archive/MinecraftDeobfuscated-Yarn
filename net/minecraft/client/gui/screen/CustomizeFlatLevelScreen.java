/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.PresetsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import net.minecraft.world.level.LevelGeneratorOptions;
import net.minecraft.world.level.LevelGeneratorType;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CustomizeFlatLevelScreen
extends Screen {
    private final CreateWorldScreen parent;
    private FlatChunkGeneratorConfig config = FlatChunkGeneratorConfig.getDefaultConfig();
    private String tileText;
    private String heightText;
    private SuperflatLayersListWidget layers;
    private ButtonWidget widgetButtonRemoveLayer;

    public CustomizeFlatLevelScreen(CreateWorldScreen parent, LevelGeneratorOptions levelGeneratorOptions) {
        super(new TranslatableText("createWorld.customize.flat.title", new Object[0]));
        this.parent = parent;
        if (levelGeneratorOptions.getType() == LevelGeneratorType.FLAT) {
            this.config = FlatChunkGeneratorConfig.fromDynamic(levelGeneratorOptions.getDynamic());
        }
    }

    public String getConfigString() {
        return this.config.toString();
    }

    public void setConfigString(String config) {
        this.config = FlatChunkGeneratorConfig.fromString(config);
    }

    @Override
    protected void init() {
        this.tileText = I18n.translate("createWorld.customize.flat.tile", new Object[0]);
        this.heightText = I18n.translate("createWorld.customize.flat.height", new Object[0]);
        this.layers = new SuperflatLayersListWidget();
        this.children.add(this.layers);
        this.widgetButtonRemoveLayer = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 52, 150, 20, I18n.translate("createWorld.customize.flat.removeLayer", new Object[0]), buttonWidget -> {
            if (!this.method_2147()) {
                return;
            }
            List<FlatChunkGeneratorLayer> list = this.config.getLayers();
            int i = this.layers.children().indexOf(this.layers.getSelected());
            int j = list.size() - i - 1;
            list.remove(j);
            this.layers.setSelected(list.isEmpty() ? null : (SuperflatLayersListWidget.SuperflatLayerItem)this.layers.children().get(Math.min(i, list.size() - 1)));
            this.config.updateLayerBlocks();
            this.method_2145();
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 52, 150, 20, I18n.translate("createWorld.customize.presets", new Object[0]), buttonWidget -> {
            this.client.openScreen(new PresetsScreen(this));
            this.config.updateLayerBlocks();
            this.method_2145();
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("gui.done", new Object[0]), buttonWidget -> {
            this.parent.generatorOptions = LevelGeneratorType.FLAT.loadOptions(this.config.toDynamic(NbtOps.INSTANCE));
            this.client.openScreen(this.parent);
            this.config.updateLayerBlocks();
            this.method_2145();
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel", new Object[0]), buttonWidget -> {
            this.client.openScreen(this.parent);
            this.config.updateLayerBlocks();
            this.method_2145();
        }));
        this.config.updateLayerBlocks();
        this.method_2145();
    }

    public void method_2145() {
        this.widgetButtonRemoveLayer.active = this.method_2147();
        this.layers.method_19372();
    }

    private boolean method_2147() {
        return this.layers.getSelected() != null;
    }

    @Override
    public void onClose() {
        this.client.openScreen(this.parent);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.layers.render(mouseX, mouseY, delta);
        this.drawCenteredString(this.textRenderer, this.title.asFormattedString(), this.width / 2, 8, 0xFFFFFF);
        int i = this.width / 2 - 92 - 16;
        this.drawString(this.textRenderer, this.tileText, i, 32, 0xFFFFFF);
        this.drawString(this.textRenderer, this.heightText, i + 2 + 213 - this.textRenderer.getStringWidth(this.heightText), 32, 0xFFFFFF);
        super.render(mouseX, mouseY, delta);
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
            Item item;
            super.setSelected(superflatLayerItem);
            if (superflatLayerItem != null && (item = (flatChunkGeneratorLayer = CustomizeFlatLevelScreen.this.config.getLayers().get(CustomizeFlatLevelScreen.this.config.getLayers().size() - this.children().indexOf(superflatLayerItem) - 1)).getBlockState().getBlock().asItem()) != Items.AIR) {
                NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.select", item.getName(new ItemStack(item))).getString());
            }
        }

        @Override
        protected void moveSelection(int amount) {
            super.moveSelection(amount);
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
            public void render(int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
                FlatChunkGeneratorLayer flatChunkGeneratorLayer = CustomizeFlatLevelScreen.this.config.getLayers().get(CustomizeFlatLevelScreen.this.config.getLayers().size() - index - 1);
                BlockState blockState = flatChunkGeneratorLayer.getBlockState();
                Block block = blockState.getBlock();
                Item item = block.asItem();
                if (item == Items.AIR) {
                    if (block == Blocks.WATER) {
                        item = Items.WATER_BUCKET;
                    } else if (block == Blocks.LAVA) {
                        item = Items.LAVA_BUCKET;
                    }
                }
                ItemStack itemStack = new ItemStack(item);
                String string = item.getName(itemStack).asFormattedString();
                this.method_19375(x, y, itemStack);
                CustomizeFlatLevelScreen.this.textRenderer.draw(string, x + 18 + 5, y + 3, 0xFFFFFF);
                String string2 = index == 0 ? I18n.translate("createWorld.customize.flat.layer.top", flatChunkGeneratorLayer.getThickness()) : (index == CustomizeFlatLevelScreen.this.config.getLayers().size() - 1 ? I18n.translate("createWorld.customize.flat.layer.bottom", flatChunkGeneratorLayer.getThickness()) : I18n.translate("createWorld.customize.flat.layer", flatChunkGeneratorLayer.getThickness()));
                CustomizeFlatLevelScreen.this.textRenderer.draw(string2, x + 2 + 213 - CustomizeFlatLevelScreen.this.textRenderer.getStringWidth(string2), y + 3, 0xFFFFFF);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0) {
                    SuperflatLayersListWidget.this.setSelected(this);
                    CustomizeFlatLevelScreen.this.method_2145();
                    return true;
                }
                return false;
            }

            private void method_19375(int x, int y, ItemStack itemStack) {
                this.method_19373(x + 1, y + 1);
                RenderSystem.enableRescaleNormal();
                if (!itemStack.isEmpty()) {
                    CustomizeFlatLevelScreen.this.itemRenderer.renderGuiItemIcon(itemStack, x + 2, y + 2);
                }
                RenderSystem.disableRescaleNormal();
            }

            private void method_19373(int x, int y) {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                SuperflatLayersListWidget.this.client.getTextureManager().bindTexture(DrawableHelper.STATS_ICON_TEXTURE);
                DrawableHelper.drawTexture(x, y, CustomizeFlatLevelScreen.this.getZOffset(), 0.0f, 0.0f, 18, 18, 128, 128);
            }
        }
    }
}

