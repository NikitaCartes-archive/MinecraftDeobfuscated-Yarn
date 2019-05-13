/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.StatsListener;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.network.packet.ClientStatusC2SPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class StatsScreen
extends Screen
implements StatsListener {
    protected final Screen parent;
    private CustomStatsListWidget generalButton;
    private ItemStatsListWidget itemsButton;
    private EntityStatsListWidget mobsButton;
    private final StatHandler statHandler;
    @Nullable
    private AlwaysSelectedEntryListWidget<?> listWidget;
    private boolean field_2645 = true;

    public StatsScreen(Screen screen, StatHandler statHandler) {
        super(new TranslatableComponent("gui.stats", new Object[0]));
        this.parent = screen;
        this.statHandler = statHandler;
    }

    @Override
    protected void init() {
        this.field_2645 = true;
        this.minecraft.getNetworkHandler().sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS));
    }

    public void method_2270() {
        this.generalButton = new CustomStatsListWidget(this.minecraft);
        this.itemsButton = new ItemStatsListWidget(this.minecraft);
        this.mobsButton = new EntityStatsListWidget(this.minecraft);
    }

    public void method_2267() {
        this.addButton(new ButtonWidget(this.width / 2 - 120, this.height - 52, 80, 20, I18n.translate("stat.generalButton", new Object[0]), buttonWidget -> this.method_19390(this.generalButton)));
        ButtonWidget buttonWidget2 = this.addButton(new ButtonWidget(this.width / 2 - 40, this.height - 52, 80, 20, I18n.translate("stat.itemsButton", new Object[0]), buttonWidget -> this.method_19390(this.itemsButton)));
        ButtonWidget buttonWidget22 = this.addButton(new ButtonWidget(this.width / 2 + 40, this.height - 52, 80, 20, I18n.translate("stat.mobsButton", new Object[0]), buttonWidget -> this.method_19390(this.mobsButton)));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 28, 200, 20, I18n.translate("gui.done", new Object[0]), buttonWidget -> this.minecraft.openScreen(this.parent)));
        if (this.itemsButton.children().isEmpty()) {
            buttonWidget2.active = false;
        }
        if (this.mobsButton.children().isEmpty()) {
            buttonWidget22.active = false;
        }
    }

    @Override
    public void render(int i, int j, float f) {
        if (this.field_2645) {
            this.renderBackground();
            this.drawCenteredString(this.font, I18n.translate("multiplayer.downloadingStats", new Object[0]), this.width / 2, this.height / 2, 0xFFFFFF);
            this.drawCenteredString(this.font, PROGRESS_BAR_STAGES[(int)(SystemUtil.getMeasuringTimeMs() / 150L % (long)PROGRESS_BAR_STAGES.length)], this.width / 2, this.height / 2 + this.font.fontHeight * 2, 0xFFFFFF);
        } else {
            this.method_19399().render(i, j, f);
            this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, 0xFFFFFF);
            super.render(i, j, f);
        }
    }

    @Override
    public void onStatsReady() {
        if (this.field_2645) {
            this.method_2270();
            this.method_2267();
            this.method_19390(this.generalButton);
            this.field_2645 = false;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return !this.field_2645;
    }

    @Nullable
    public AlwaysSelectedEntryListWidget<?> method_19399() {
        return this.listWidget;
    }

    public void method_19390(@Nullable AlwaysSelectedEntryListWidget<?> alwaysSelectedEntryListWidget) {
        this.children.remove(this.generalButton);
        this.children.remove(this.itemsButton);
        this.children.remove(this.mobsButton);
        if (alwaysSelectedEntryListWidget != null) {
            this.children.add(0, alwaysSelectedEntryListWidget);
            this.listWidget = alwaysSelectedEntryListWidget;
        }
    }

    private int method_2285(int i) {
        return 115 + 40 * i;
    }

    private void method_2289(int i, int j, Item item) {
        this.method_2282(i + 1, j + 1, 0, 0);
        GlStateManager.enableRescaleNormal();
        GuiLighting.enableForItems();
        this.itemRenderer.renderGuiItemIcon(item.getDefaultStack(), i + 2, j + 2);
        GuiLighting.disable();
        GlStateManager.disableRescaleNormal();
    }

    private void method_2282(int i, int j, int k, int l) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(STATS_ICON_LOCATION);
        StatsScreen.blit(i, j, this.blitOffset, k, l, 18, 18, 128, 128);
    }

    @Environment(value=EnvType.CLIENT)
    class EntityStatsListWidget
    extends AlwaysSelectedEntryListWidget<EntityStatItem> {
        public EntityStatsListWidget(MinecraftClient minecraftClient) {
            super(minecraftClient, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, ((StatsScreen)StatsScreen.this).font.fontHeight * 4);
            for (EntityType entityType : Registry.ENTITY_TYPE) {
                if (StatsScreen.this.statHandler.getStat(Stats.KILLED.getOrCreateStat(entityType)) <= 0 && StatsScreen.this.statHandler.getStat(Stats.KILLED_BY.getOrCreateStat(entityType)) <= 0) continue;
                this.addEntry(new EntityStatItem(entityType));
            }
        }

        @Override
        protected void renderBackground() {
            StatsScreen.this.renderBackground();
        }

        @Environment(value=EnvType.CLIENT)
        class EntityStatItem
        extends AlwaysSelectedEntryListWidget.Entry<EntityStatItem> {
            private final EntityType<?> field_18762;

            public EntityStatItem(EntityType<?> entityType) {
                this.field_18762 = entityType;
            }

            @Override
            public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
                String string = I18n.translate(SystemUtil.createTranslationKey("entity", EntityType.getId(this.field_18762)), new Object[0]);
                int p = StatsScreen.this.statHandler.getStat(Stats.KILLED.getOrCreateStat(this.field_18762));
                int q = StatsScreen.this.statHandler.getStat(Stats.KILLED_BY.getOrCreateStat(this.field_18762));
                EntityStatsListWidget.this.drawString(StatsScreen.this.font, string, k + 2, j + 1, 0xFFFFFF);
                EntityStatsListWidget.this.drawString(StatsScreen.this.font, this.method_19411(string, p), k + 2 + 10, j + 1 + ((StatsScreen)StatsScreen.this).font.fontHeight, p == 0 ? 0x606060 : 0x909090);
                EntityStatsListWidget.this.drawString(StatsScreen.this.font, this.method_19412(string, q), k + 2 + 10, j + 1 + ((StatsScreen)StatsScreen.this).font.fontHeight * 2, q == 0 ? 0x606060 : 0x909090);
            }

            private String method_19411(String string, int i) {
                String string2 = Stats.KILLED.getTranslationKey();
                if (i == 0) {
                    return I18n.translate(string2 + ".none", string);
                }
                return I18n.translate(string2, i, string);
            }

            private String method_19412(String string, int i) {
                String string2 = Stats.KILLED_BY.getTranslationKey();
                if (i == 0) {
                    return I18n.translate(string2 + ".none", string);
                }
                return I18n.translate(string2, string, i);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class ItemStatsListWidget
    extends AlwaysSelectedEntryListWidget<ItemStatItem> {
        protected final List<StatType<Block>> field_18754;
        protected final List<StatType<Item>> field_18755;
        private final int[] field_18753;
        protected int field_18756;
        protected final List<Item> field_18757;
        protected final Comparator<Item> field_18758;
        @Nullable
        protected StatType<?> field_18759;
        protected int field_18760;

        public ItemStatsListWidget(MinecraftClient minecraftClient) {
            boolean bl;
            super(minecraftClient, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 20);
            this.field_18753 = new int[]{3, 4, 1, 2, 5, 6};
            this.field_18756 = -1;
            this.field_18758 = new class_450();
            this.field_18754 = Lists.newArrayList();
            this.field_18754.add(Stats.MINED);
            this.field_18755 = Lists.newArrayList(Stats.BROKEN, Stats.CRAFTED, Stats.USED, Stats.PICKED_UP, Stats.DROPPED);
            this.setRenderHeader(true, 20);
            Set<Item> set = Sets.newIdentityHashSet();
            for (Item item : Registry.ITEM) {
                bl = false;
                for (StatType<Item> statType : this.field_18755) {
                    if (!statType.hasStat(item) || StatsScreen.this.statHandler.getStat(statType.getOrCreateStat(item)) <= 0) continue;
                    bl = true;
                }
                if (!bl) continue;
                set.add(item);
            }
            for (Block block : Registry.BLOCK) {
                bl = false;
                for (StatType<ItemConvertible> statType : this.field_18754) {
                    if (!statType.hasStat(block) || StatsScreen.this.statHandler.getStat(statType.getOrCreateStat(block)) <= 0) continue;
                    bl = true;
                }
                if (!bl) continue;
                set.add(block.asItem());
            }
            set.remove(Items.AIR);
            this.field_18757 = Lists.newArrayList(set);
            for (int i = 0; i < this.field_18757.size(); ++i) {
                this.addEntry(new ItemStatItem());
            }
        }

        @Override
        protected void renderHeader(int i, int j, Tessellator tessellator) {
            int l;
            int k;
            if (!this.minecraft.mouse.wasLeftButtonClicked()) {
                this.field_18756 = -1;
            }
            for (k = 0; k < this.field_18753.length; ++k) {
                StatsScreen.this.method_2282(i + StatsScreen.this.method_2285(k) - 18, j + 1, 0, this.field_18756 == k ? 0 : 18);
            }
            if (this.field_18759 != null) {
                k = StatsScreen.this.method_2285(this.method_19409(this.field_18759)) - 36;
                l = this.field_18760 == 1 ? 2 : 1;
                StatsScreen.this.method_2282(i + k, j + 1, 18 * l, 0);
            }
            for (k = 0; k < this.field_18753.length; ++k) {
                l = this.field_18756 == k ? 1 : 0;
                StatsScreen.this.method_2282(i + StatsScreen.this.method_2285(k) - 18 + l, j + 1 + l, 18 * this.field_18753[k], 18);
            }
        }

        @Override
        public int getRowWidth() {
            return 375;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.width / 2 + 140;
        }

        @Override
        protected void renderBackground() {
            StatsScreen.this.renderBackground();
        }

        @Override
        protected void clickedHeader(int i, int j) {
            this.field_18756 = -1;
            for (int k = 0; k < this.field_18753.length; ++k) {
                int l = i - StatsScreen.this.method_2285(k);
                if (l < -36 || l > 0) continue;
                this.field_18756 = k;
                break;
            }
            if (this.field_18756 >= 0) {
                this.method_19408(this.method_19410(this.field_18756));
                this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
        }

        private StatType<?> method_19410(int i) {
            return i < this.field_18754.size() ? this.field_18754.get(i) : this.field_18755.get(i - this.field_18754.size());
        }

        private int method_19409(StatType<?> statType) {
            int i = this.field_18754.indexOf(statType);
            if (i >= 0) {
                return i;
            }
            int j = this.field_18755.indexOf(statType);
            if (j >= 0) {
                return j + this.field_18754.size();
            }
            return -1;
        }

        @Override
        protected void renderDecorations(int i, int j) {
            if (j < this.top || j > this.bottom) {
                return;
            }
            ItemStatItem itemStatItem = (ItemStatItem)this.getEntryAtPosition(i, j);
            int k = (this.width - this.getRowWidth()) / 2;
            if (itemStatItem != null) {
                if (i < k + 40 || i > k + 40 + 20) {
                    return;
                }
                Item item = this.field_18757.get(this.children().indexOf(itemStatItem));
                this.method_19407(this.method_19406(item), i, j);
            } else {
                TranslatableComponent component = null;
                int l = i - k;
                for (int m = 0; m < this.field_18753.length; ++m) {
                    int n = StatsScreen.this.method_2285(m);
                    if (l < n - 18 || l > n) continue;
                    component = new TranslatableComponent(this.method_19410(m).getTranslationKey(), new Object[0]);
                    break;
                }
                this.method_19407(component, i, j);
            }
        }

        protected void method_19407(@Nullable Component component, int i, int j) {
            if (component == null) {
                return;
            }
            String string = component.getFormattedText();
            int k = i + 12;
            int l = j - 12;
            int m = StatsScreen.this.font.getStringWidth(string);
            this.fillGradient(k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
            StatsScreen.this.font.drawWithShadow(string, k, l, -1);
        }

        protected Component method_19406(Item item) {
            return item.getTextComponent();
        }

        protected void method_19408(StatType<?> statType) {
            if (statType != this.field_18759) {
                this.field_18759 = statType;
                this.field_18760 = -1;
            } else if (this.field_18760 == -1) {
                this.field_18760 = 1;
            } else {
                this.field_18759 = null;
                this.field_18760 = 0;
            }
            this.field_18757.sort(this.field_18758);
        }

        @Environment(value=EnvType.CLIENT)
        class ItemStatItem
        extends AlwaysSelectedEntryListWidget.Entry<ItemStatItem> {
            private ItemStatItem() {
            }

            @Override
            public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
                int p;
                Item item = ((StatsScreen)StatsScreen.this).itemsButton.field_18757.get(i);
                StatsScreen.this.method_2289(k + 40, j, item);
                for (p = 0; p < ((StatsScreen)StatsScreen.this).itemsButton.field_18754.size(); ++p) {
                    Stat<Block> stat = item instanceof BlockItem ? ((StatsScreen)StatsScreen.this).itemsButton.field_18754.get(p).getOrCreateStat(((BlockItem)item).getBlock()) : null;
                    this.method_19405(stat, k + StatsScreen.this.method_2285(p), j, i % 2 == 0);
                }
                for (p = 0; p < ((StatsScreen)StatsScreen.this).itemsButton.field_18755.size(); ++p) {
                    this.method_19405(((StatsScreen)StatsScreen.this).itemsButton.field_18755.get(p).getOrCreateStat(item), k + StatsScreen.this.method_2285(p + ((StatsScreen)StatsScreen.this).itemsButton.field_18754.size()), j, i % 2 == 0);
                }
            }

            protected void method_19405(@Nullable Stat<?> stat, int i, int j, boolean bl) {
                String string = stat == null ? "-" : stat.format(StatsScreen.this.statHandler.getStat(stat));
                ItemStatsListWidget.this.drawString(StatsScreen.this.font, string, i - StatsScreen.this.font.getStringWidth(string), j + 5, bl ? 0xFFFFFF : 0x909090);
            }
        }

        @Environment(value=EnvType.CLIENT)
        class class_450
        implements Comparator<Item> {
            private class_450() {
            }

            public int method_2297(Item item, Item item2) {
                int j;
                int i;
                if (ItemStatsListWidget.this.field_18759 == null) {
                    i = 0;
                    j = 0;
                } else if (ItemStatsListWidget.this.field_18754.contains(ItemStatsListWidget.this.field_18759)) {
                    StatType<?> statType = ItemStatsListWidget.this.field_18759;
                    i = item instanceof BlockItem ? StatsScreen.this.statHandler.getStat(statType, ((BlockItem)item).getBlock()) : -1;
                    j = item2 instanceof BlockItem ? StatsScreen.this.statHandler.getStat(statType, ((BlockItem)item2).getBlock()) : -1;
                } else {
                    StatType<?> statType = ItemStatsListWidget.this.field_18759;
                    i = StatsScreen.this.statHandler.getStat(statType, item);
                    j = StatsScreen.this.statHandler.getStat(statType, item2);
                }
                if (i == j) {
                    return ItemStatsListWidget.this.field_18760 * Integer.compare(Item.getRawIdByItem(item), Item.getRawIdByItem(item2));
                }
                return ItemStatsListWidget.this.field_18760 * Integer.compare(i, j);
            }

            @Override
            public /* synthetic */ int compare(Object object, Object object2) {
                return this.method_2297((Item)object, (Item)object2);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class CustomStatsListWidget
    extends AlwaysSelectedEntryListWidget<CustomStatItem> {
        public CustomStatsListWidget(MinecraftClient minecraftClient) {
            super(minecraftClient, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 10);
            for (Stat<Identifier> stat : Stats.CUSTOM) {
                this.addEntry(new CustomStatItem(stat));
            }
        }

        @Override
        protected void renderBackground() {
            StatsScreen.this.renderBackground();
        }

        @Environment(value=EnvType.CLIENT)
        class CustomStatItem
        extends AlwaysSelectedEntryListWidget.Entry<CustomStatItem> {
            private final Stat<Identifier> field_18749;

            private CustomStatItem(Stat<Identifier> stat) {
                this.field_18749 = stat;
            }

            @Override
            public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
                Component component = new TranslatableComponent("stat." + this.field_18749.getValue().toString().replace(':', '.'), new Object[0]).applyFormat(ChatFormat.GRAY);
                CustomStatsListWidget.this.drawString(StatsScreen.this.font, component.getString(), k + 2, j + 1, i % 2 == 0 ? 0xFFFFFF : 0x909090);
                String string = this.field_18749.format(StatsScreen.this.statHandler.getStat(this.field_18749));
                CustomStatsListWidget.this.drawString(StatsScreen.this.font, string, k + 2 + 213 - StatsScreen.this.font.getStringWidth(string), j + 1, i % 2 == 0 ? 0xFFFFFF : 0x909090);
            }
        }
    }
}

