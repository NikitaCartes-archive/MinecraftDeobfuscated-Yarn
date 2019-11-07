/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.CustomizeFlatLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PresetsScreen
extends Screen {
    private static final List<SuperflatPreset> presets = Lists.newArrayList();
    private final CustomizeFlatLevelScreen parent;
    private String shareText;
    private String listText;
    private SuperflatPresetsListWidget listWidget;
    private ButtonWidget selectPresetButton;
    private TextFieldWidget customPresetField;

    public PresetsScreen(CustomizeFlatLevelScreen customizeFlatLevelScreen) {
        super(new TranslatableText("createWorld.customize.presets.title", new Object[0]));
        this.parent = customizeFlatLevelScreen;
    }

    @Override
    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        this.shareText = I18n.translate("createWorld.customize.presets.share", new Object[0]);
        this.listText = I18n.translate("createWorld.customize.presets.list", new Object[0]);
        this.customPresetField = new TextFieldWidget(this.font, 50, 40, this.width - 100, 20, this.shareText);
        this.customPresetField.setMaxLength(1230);
        this.customPresetField.setText(this.parent.getConfigString());
        this.children.add(this.customPresetField);
        this.listWidget = new SuperflatPresetsListWidget();
        this.children.add(this.listWidget);
        this.selectPresetButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("createWorld.customize.presets.select", new Object[0]), buttonWidget -> {
            this.parent.setConfigString(this.customPresetField.getText());
            this.minecraft.openScreen(this.parent);
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel", new Object[0]), buttonWidget -> this.minecraft.openScreen(this.parent)));
        this.updateSelectButton(this.listWidget.getSelected() != null);
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f) {
        return this.listWidget.mouseScrolled(d, e, f);
    }

    @Override
    public void resize(MinecraftClient minecraftClient, int i, int j) {
        String string = this.customPresetField.getText();
        this.init(minecraftClient, i, j);
        this.customPresetField.setText(string);
    }

    @Override
    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderBackground();
        this.listWidget.render(i, j, f);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0f, 0.0f, 400.0f);
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 8, 0xFFFFFF);
        this.drawString(this.font, this.shareText, 50, 30, 0xA0A0A0);
        this.drawString(this.font, this.listText, 50, 70, 0xA0A0A0);
        RenderSystem.popMatrix();
        this.customPresetField.render(i, j, f);
        super.render(i, j, f);
    }

    @Override
    public void tick() {
        this.customPresetField.tick();
        super.tick();
    }

    public void updateSelectButton(boolean bl) {
        this.selectPresetButton.active = bl || this.customPresetField.getText().length() > 1;
    }

    private static void addPreset(String string, ItemConvertible itemConvertible, Biome biome, List<String> list, FlatChunkGeneratorLayer ... flatChunkGeneratorLayers) {
        FlatChunkGeneratorConfig flatChunkGeneratorConfig = ChunkGeneratorType.FLAT.createSettings();
        for (int i = flatChunkGeneratorLayers.length - 1; i >= 0; --i) {
            flatChunkGeneratorConfig.getLayers().add(flatChunkGeneratorLayers[i]);
        }
        flatChunkGeneratorConfig.setBiome(biome);
        flatChunkGeneratorConfig.updateLayerBlocks();
        for (String string2 : list) {
            flatChunkGeneratorConfig.getStructures().put(string2, Maps.newHashMap());
        }
        presets.add(new SuperflatPreset(itemConvertible.asItem(), string, flatChunkGeneratorConfig.toString()));
    }

    static {
        PresetsScreen.addPreset(I18n.translate("createWorld.customize.preset.classic_flat", new Object[0]), Blocks.GRASS_BLOCK, Biomes.PLAINS, Arrays.asList("village"), new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK), new FlatChunkGeneratorLayer(2, Blocks.DIRT), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
        PresetsScreen.addPreset(I18n.translate("createWorld.customize.preset.tunnelers_dream", new Object[0]), Blocks.STONE, Biomes.MOUNTAINS, Arrays.asList("biome_1", "dungeon", "decoration", "stronghold", "mineshaft"), new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK), new FlatChunkGeneratorLayer(5, Blocks.DIRT), new FlatChunkGeneratorLayer(230, Blocks.STONE), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
        PresetsScreen.addPreset(I18n.translate("createWorld.customize.preset.water_world", new Object[0]), Items.WATER_BUCKET, Biomes.DEEP_OCEAN, Arrays.asList("biome_1", "oceanmonument"), new FlatChunkGeneratorLayer(90, Blocks.WATER), new FlatChunkGeneratorLayer(5, Blocks.SAND), new FlatChunkGeneratorLayer(5, Blocks.DIRT), new FlatChunkGeneratorLayer(5, Blocks.STONE), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
        PresetsScreen.addPreset(I18n.translate("createWorld.customize.preset.overworld", new Object[0]), Blocks.GRASS, Biomes.PLAINS, Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon", "lake", "lava_lake", "pillager_outpost"), new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK), new FlatChunkGeneratorLayer(3, Blocks.DIRT), new FlatChunkGeneratorLayer(59, Blocks.STONE), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
        PresetsScreen.addPreset(I18n.translate("createWorld.customize.preset.snowy_kingdom", new Object[0]), Blocks.SNOW, Biomes.SNOWY_TUNDRA, Arrays.asList("village", "biome_1"), new FlatChunkGeneratorLayer(1, Blocks.SNOW), new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK), new FlatChunkGeneratorLayer(3, Blocks.DIRT), new FlatChunkGeneratorLayer(59, Blocks.STONE), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
        PresetsScreen.addPreset(I18n.translate("createWorld.customize.preset.bottomless_pit", new Object[0]), Items.FEATHER, Biomes.PLAINS, Arrays.asList("village", "biome_1"), new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK), new FlatChunkGeneratorLayer(3, Blocks.DIRT), new FlatChunkGeneratorLayer(2, Blocks.COBBLESTONE));
        PresetsScreen.addPreset(I18n.translate("createWorld.customize.preset.desert", new Object[0]), Blocks.SAND, Biomes.DESERT, Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon"), new FlatChunkGeneratorLayer(8, Blocks.SAND), new FlatChunkGeneratorLayer(52, Blocks.SANDSTONE), new FlatChunkGeneratorLayer(3, Blocks.STONE), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
        PresetsScreen.addPreset(I18n.translate("createWorld.customize.preset.redstone_ready", new Object[0]), Items.REDSTONE, Biomes.DESERT, Collections.emptyList(), new FlatChunkGeneratorLayer(52, Blocks.SANDSTONE), new FlatChunkGeneratorLayer(3, Blocks.STONE), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
        PresetsScreen.addPreset(I18n.translate("createWorld.customize.preset.the_void", new Object[0]), Blocks.BARRIER, Biomes.THE_VOID, Arrays.asList("decoration"), new FlatChunkGeneratorLayer(1, Blocks.AIR));
    }

    @Environment(value=EnvType.CLIENT)
    static class SuperflatPreset {
        public final Item icon;
        public final String name;
        public final String config;

        public SuperflatPreset(Item item, String string, String string2) {
            this.icon = item;
            this.name = string;
            this.config = string2;
        }
    }

    @Environment(value=EnvType.CLIENT)
    class SuperflatPresetsListWidget
    extends AlwaysSelectedEntryListWidget<SuperflatPresetEntry> {
        public SuperflatPresetsListWidget() {
            super(PresetsScreen.this.minecraft, PresetsScreen.this.width, PresetsScreen.this.height, 80, PresetsScreen.this.height - 37, 24);
            for (int i = 0; i < presets.size(); ++i) {
                this.addEntry(new SuperflatPresetEntry());
            }
        }

        public void method_20103(@Nullable SuperflatPresetEntry superflatPresetEntry) {
            super.setSelected(superflatPresetEntry);
            if (superflatPresetEntry != null) {
                NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.select", ((SuperflatPreset)presets.get((int)this.children().indexOf((Object)superflatPresetEntry))).name).getString());
            }
        }

        @Override
        protected void moveSelection(int i) {
            super.moveSelection(i);
            PresetsScreen.this.updateSelectButton(true);
        }

        @Override
        protected boolean isFocused() {
            return PresetsScreen.this.getFocused() == this;
        }

        @Override
        public boolean keyPressed(int i, int j, int k) {
            if (super.keyPressed(i, j, k)) {
                return true;
            }
            if ((i == 257 || i == 335) && this.getSelected() != null) {
                ((SuperflatPresetEntry)this.getSelected()).setPreset();
            }
            return false;
        }

        @Override
        public /* synthetic */ void setSelected(@Nullable EntryListWidget.Entry entry) {
            this.method_20103((SuperflatPresetEntry)entry);
        }

        @Environment(value=EnvType.CLIENT)
        public class SuperflatPresetEntry
        extends AlwaysSelectedEntryListWidget.Entry<SuperflatPresetEntry> {
            @Override
            public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
                SuperflatPreset superflatPreset = (SuperflatPreset)presets.get(i);
                this.method_2200(k, j, superflatPreset.icon);
                PresetsScreen.this.font.draw(superflatPreset.name, k + 18 + 5, j + 6, 0xFFFFFF);
            }

            @Override
            public boolean mouseClicked(double d, double e, int i) {
                if (i == 0) {
                    this.setPreset();
                }
                return false;
            }

            private void setPreset() {
                SuperflatPresetsListWidget.this.method_20103(this);
                PresetsScreen.this.updateSelectButton(true);
                PresetsScreen.this.customPresetField.setText(((SuperflatPreset)presets.get((int)SuperflatPresetsListWidget.this.children().indexOf((Object)this))).config);
                PresetsScreen.this.customPresetField.setCursorToStart();
            }

            private void method_2200(int i, int j, Item item) {
                this.method_2198(i + 1, j + 1);
                RenderSystem.enableRescaleNormal();
                PresetsScreen.this.itemRenderer.renderGuiItemIcon(new ItemStack(item), i + 2, j + 2);
                RenderSystem.disableRescaleNormal();
            }

            private void method_2198(int i, int j) {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                SuperflatPresetsListWidget.this.minecraft.getTextureManager().bindTexture(DrawableHelper.STATS_ICON_LOCATION);
                DrawableHelper.blit(i, j, PresetsScreen.this.getBlitOffset(), 0.0f, 0.0f, 18, 18, 128, 128);
            }
        }
    }
}

