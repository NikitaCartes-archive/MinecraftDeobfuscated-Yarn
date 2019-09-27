/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.BiomeSourceType;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CustomizeBuffetLevelScreen
extends Screen {
    private static final List<Identifier> CHUNK_GENERATOR_TYPES = Registry.CHUNK_GENERATOR_TYPE.getIds().stream().filter(identifier -> Registry.CHUNK_GENERATOR_TYPE.get((Identifier)identifier).isBuffetScreenOption()).collect(Collectors.toList());
    private final CreateWorldScreen parent;
    private final CompoundTag generatorOptionsTag;
    private BuffetBiomesListWidget biomeSelectionList;
    private int biomeListLength;
    private ButtonWidget confirmButton;

    public CustomizeBuffetLevelScreen(CreateWorldScreen createWorldScreen, CompoundTag compoundTag) {
        super(new TranslatableText("createWorld.customize.buffet.title", new Object[0]));
        this.parent = createWorldScreen;
        this.generatorOptionsTag = compoundTag;
    }

    @Override
    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        this.addButton(new ButtonWidget((this.width - 200) / 2, 40, 200, 20, I18n.translate("createWorld.customize.buffet.generatortype", new Object[0]) + " " + I18n.translate(SystemUtil.createTranslationKey("generator", CHUNK_GENERATOR_TYPES.get(this.biomeListLength)), new Object[0]), buttonWidget -> {
            ++this.biomeListLength;
            if (this.biomeListLength >= CHUNK_GENERATOR_TYPES.size()) {
                this.biomeListLength = 0;
            }
            buttonWidget.setMessage(I18n.translate("createWorld.customize.buffet.generatortype", new Object[0]) + " " + I18n.translate(SystemUtil.createTranslationKey("generator", CHUNK_GENERATOR_TYPES.get(this.biomeListLength)), new Object[0]));
        }));
        this.biomeSelectionList = new BuffetBiomesListWidget();
        this.children.add(this.biomeSelectionList);
        this.confirmButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("gui.done", new Object[0]), buttonWidget -> {
            this.parent.generatorOptionsTag = this.getGeneratorTag();
            this.minecraft.openScreen(this.parent);
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel", new Object[0]), buttonWidget -> this.minecraft.openScreen(this.parent)));
        this.initListSelectLogic();
        this.refreshConfirmButton();
    }

    private void initListSelectLogic() {
        int i;
        if (this.generatorOptionsTag.contains("chunk_generator", 10) && this.generatorOptionsTag.getCompound("chunk_generator").contains("type", 8)) {
            Identifier identifier = new Identifier(this.generatorOptionsTag.getCompound("chunk_generator").getString("type"));
            for (i = 0; i < CHUNK_GENERATOR_TYPES.size(); ++i) {
                if (!CHUNK_GENERATOR_TYPES.get(i).equals(identifier)) continue;
                this.biomeListLength = i;
                break;
            }
        }
        if (this.generatorOptionsTag.contains("biome_source", 10) && this.generatorOptionsTag.getCompound("biome_source").contains("biomes", 9)) {
            ListTag listTag = this.generatorOptionsTag.getCompound("biome_source").getList("biomes", 8);
            for (i = 0; i < listTag.size(); ++i) {
                Identifier identifier2 = new Identifier(listTag.getString(i));
                this.biomeSelectionList.method_20089(this.biomeSelectionList.children().stream().filter(buffetBiomeItem -> Objects.equals(((BuffetBiomesListWidget.BuffetBiomeItem)buffetBiomeItem).biome, identifier2)).findFirst().orElse(null));
            }
        }
        this.generatorOptionsTag.remove("chunk_generator");
        this.generatorOptionsTag.remove("biome_source");
    }

    private CompoundTag getGeneratorTag() {
        CompoundTag compoundTag = new CompoundTag();
        CompoundTag compoundTag2 = new CompoundTag();
        compoundTag2.putString("type", Registry.BIOME_SOURCE_TYPE.getId(BiomeSourceType.FIXED).toString());
        CompoundTag compoundTag3 = new CompoundTag();
        ListTag listTag = new ListTag();
        listTag.add(StringTag.of(((BuffetBiomesListWidget.BuffetBiomeItem)this.biomeSelectionList.getSelected()).biome.toString()));
        compoundTag3.put("biomes", listTag);
        compoundTag2.put("options", compoundTag3);
        CompoundTag compoundTag4 = new CompoundTag();
        CompoundTag compoundTag5 = new CompoundTag();
        compoundTag4.putString("type", CHUNK_GENERATOR_TYPES.get(this.biomeListLength).toString());
        compoundTag5.putString("default_block", "minecraft:stone");
        compoundTag5.putString("default_fluid", "minecraft:water");
        compoundTag4.put("options", compoundTag5);
        compoundTag.put("biome_source", compoundTag2);
        compoundTag.put("chunk_generator", compoundTag4);
        return compoundTag;
    }

    public void refreshConfirmButton() {
        this.confirmButton.active = this.biomeSelectionList.getSelected() != null;
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderDirtBackground(0);
        this.biomeSelectionList.render(i, j, f);
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 8, 0xFFFFFF);
        this.drawCenteredString(this.font, I18n.translate("createWorld.customize.buffet.generator", new Object[0]), this.width / 2, 30, 0xA0A0A0);
        this.drawCenteredString(this.font, I18n.translate("createWorld.customize.buffet.biome", new Object[0]), this.width / 2, 68, 0xA0A0A0);
        super.render(i, j, f);
    }

    @Environment(value=EnvType.CLIENT)
    class BuffetBiomesListWidget
    extends AlwaysSelectedEntryListWidget<BuffetBiomeItem> {
        private BuffetBiomesListWidget() {
            super(CustomizeBuffetLevelScreen.this.minecraft, CustomizeBuffetLevelScreen.this.width, CustomizeBuffetLevelScreen.this.height, 80, CustomizeBuffetLevelScreen.this.height - 37, 16);
            Registry.BIOME.getIds().stream().sorted(Comparator.comparing(identifier -> Registry.BIOME.get((Identifier)identifier).getName().getString())).forEach(identifier -> this.addEntry(new BuffetBiomeItem((Identifier)identifier)));
        }

        @Override
        protected boolean isFocused() {
            return CustomizeBuffetLevelScreen.this.getFocused() == this;
        }

        public void method_20089(@Nullable BuffetBiomeItem buffetBiomeItem) {
            super.setSelected(buffetBiomeItem);
            if (buffetBiomeItem != null) {
                NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.select", Registry.BIOME.get(buffetBiomeItem.biome).getName().getString()).getString());
            }
        }

        @Override
        protected void moveSelection(int i) {
            super.moveSelection(i);
            CustomizeBuffetLevelScreen.this.refreshConfirmButton();
        }

        @Override
        public /* synthetic */ void setSelected(@Nullable EntryListWidget.Entry entry) {
            this.method_20089((BuffetBiomeItem)entry);
        }

        @Environment(value=EnvType.CLIENT)
        class BuffetBiomeItem
        extends AlwaysSelectedEntryListWidget.Entry<BuffetBiomeItem> {
            private final Identifier biome;

            public BuffetBiomeItem(Identifier identifier) {
                this.biome = identifier;
            }

            @Override
            public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
                BuffetBiomesListWidget.this.drawString(CustomizeBuffetLevelScreen.this.font, Registry.BIOME.get(this.biome).getName().getString(), k + 5, j + 2, 0xFFFFFF);
            }

            @Override
            public boolean mouseClicked(double d, double e, int i) {
                if (i == 0) {
                    BuffetBiomesListWidget.this.method_20089(this);
                    CustomizeBuffetLevelScreen.this.refreshConfirmButton();
                    return true;
                }
                return false;
            }
        }
    }
}

