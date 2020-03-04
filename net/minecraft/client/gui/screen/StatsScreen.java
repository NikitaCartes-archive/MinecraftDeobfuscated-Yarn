/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.StatsListener;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class StatsScreen
extends Screen
implements StatsListener {
    protected final Screen parent;
    private GeneralStatsListWidget generalStats;
    private ItemStatsListWidget itemStats;
    private EntityStatsListWidget mobStats;
    private final StatHandler statHandler;
    @Nullable
    private AlwaysSelectedEntryListWidget<?> selectedList;
    private boolean downloadingStats = true;

    public StatsScreen(Screen parent, StatHandler statHandler) {
        super(new TranslatableText("gui.stats", new Object[0]));
        this.parent = parent;
        this.statHandler = statHandler;
    }

    @Override
    protected void init() {
        this.downloadingStats = true;
        this.client.getNetworkHandler().sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS));
    }

    public void createLists() {
        this.generalStats = new GeneralStatsListWidget(this.client);
        this.itemStats = new ItemStatsListWidget(this.client);
        this.mobStats = new EntityStatsListWidget(this.client);
    }

    public void createButtons() {
        this.addButton(new ButtonWidget(this.width / 2 - 120, this.height - 52, 80, 20, I18n.translate("stat.generalButton", new Object[0]), buttonWidget -> this.selectStatList(this.generalStats)));
        ButtonWidget buttonWidget2 = this.addButton(new ButtonWidget(this.width / 2 - 40, this.height - 52, 80, 20, I18n.translate("stat.itemsButton", new Object[0]), buttonWidget -> this.selectStatList(this.itemStats)));
        ButtonWidget buttonWidget22 = this.addButton(new ButtonWidget(this.width / 2 + 40, this.height - 52, 80, 20, I18n.translate("stat.mobsButton", new Object[0]), buttonWidget -> this.selectStatList(this.mobStats)));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 28, 200, 20, I18n.translate("gui.done", new Object[0]), buttonWidget -> this.client.openScreen(this.parent)));
        if (this.itemStats.children().isEmpty()) {
            buttonWidget2.active = false;
        }
        if (this.mobStats.children().isEmpty()) {
            buttonWidget22.active = false;
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        if (this.downloadingStats) {
            this.renderBackground();
            this.drawCenteredString(this.textRenderer, I18n.translate("multiplayer.downloadingStats", new Object[0]), this.width / 2, this.height / 2, 0xFFFFFF);
            this.drawCenteredString(this.textRenderer, PROGRESS_BAR_STAGES[(int)(Util.getMeasuringTimeMs() / 150L % (long)PROGRESS_BAR_STAGES.length)], this.width / 2, this.height / 2 + this.textRenderer.fontHeight * 2, 0xFFFFFF);
        } else {
            this.getSelectedStatList().render(mouseX, mouseY, delta);
            this.drawCenteredString(this.textRenderer, this.title.asFormattedString(), this.width / 2, 20, 0xFFFFFF);
            super.render(mouseX, mouseY, delta);
        }
    }

    @Override
    public void onStatsReady() {
        if (this.downloadingStats) {
            this.createLists();
            this.createButtons();
            this.selectStatList(this.generalStats);
            this.downloadingStats = false;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return !this.downloadingStats;
    }

    @Nullable
    public AlwaysSelectedEntryListWidget<?> getSelectedStatList() {
        return this.selectedList;
    }

    public void selectStatList(@Nullable AlwaysSelectedEntryListWidget<?> list) {
        this.children.remove(this.generalStats);
        this.children.remove(this.itemStats);
        this.children.remove(this.mobStats);
        if (list != null) {
            this.children.add(0, list);
            this.selectedList = list;
        }
    }

    private int getColumnX(int index) {
        return 115 + 40 * index;
    }

    private void renderStatItem(int x, int y, Item item) {
        this.renderIcon(x + 1, y + 1, 0, 0);
        RenderSystem.enableRescaleNormal();
        this.itemRenderer.renderGuiItemIcon(item.getStackForRender(), x + 2, y + 2);
        RenderSystem.disableRescaleNormal();
    }

    private void renderIcon(int x, int y, int u, int v) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(STATS_ICON_LOCATION);
        StatsScreen.blit(x, y, this.getZOffset(), u, v, 18, 18, 128, 128);
    }

    @Environment(value=EnvType.CLIENT)
    class EntityStatsListWidget
    extends AlwaysSelectedEntryListWidget<Entry> {
        public EntityStatsListWidget(MinecraftClient minecraftClient) {
            super(minecraftClient, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, ((StatsScreen)StatsScreen.this).textRenderer.fontHeight * 4);
            for (EntityType entityType : Registry.ENTITY_TYPE) {
                if (StatsScreen.this.statHandler.getStat(Stats.KILLED.getOrCreateStat(entityType)) <= 0 && StatsScreen.this.statHandler.getStat(Stats.KILLED_BY.getOrCreateStat(entityType)) <= 0) continue;
                this.addEntry(new Entry(entityType));
            }
        }

        @Override
        protected void renderBackground() {
            StatsScreen.this.renderBackground();
        }

        @Environment(value=EnvType.CLIENT)
        class Entry
        extends AlwaysSelectedEntryListWidget.Entry<Entry> {
            private final EntityType<?> entityType;

            public Entry(EntityType<?> entityType) {
                this.entityType = entityType;
            }

            @Override
            public void render(int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
                String string = I18n.translate(Util.createTranslationKey("entity", EntityType.getId(this.entityType)), new Object[0]);
                int i = StatsScreen.this.statHandler.getStat(Stats.KILLED.getOrCreateStat(this.entityType));
                int j = StatsScreen.this.statHandler.getStat(Stats.KILLED_BY.getOrCreateStat(this.entityType));
                EntityStatsListWidget.this.drawString(StatsScreen.this.textRenderer, string, x + 2, y + 1, 0xFFFFFF);
                EntityStatsListWidget.this.drawString(StatsScreen.this.textRenderer, this.getKilledString(string, i), x + 2 + 10, y + 1 + ((StatsScreen)StatsScreen.this).textRenderer.fontHeight, i == 0 ? 0x606060 : 0x909090);
                EntityStatsListWidget.this.drawString(StatsScreen.this.textRenderer, this.getKilledByString(string, j), x + 2 + 10, y + 1 + ((StatsScreen)StatsScreen.this).textRenderer.fontHeight * 2, j == 0 ? 0x606060 : 0x909090);
            }

            private String getKilledString(String entityName, int killCount) {
                String string = Stats.KILLED.getTranslationKey();
                if (killCount == 0) {
                    return I18n.translate(string + ".none", entityName);
                }
                return I18n.translate(string, killCount, entityName);
            }

            private String getKilledByString(String entityName, int killCount) {
                String string = Stats.KILLED_BY.getTranslationKey();
                if (killCount == 0) {
                    return I18n.translate(string + ".none", entityName);
                }
                return I18n.translate(string, entityName, killCount);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class ItemStatsListWidget
    extends AlwaysSelectedEntryListWidget<Entry> {
        protected final List<StatType<Block>> blockStatTypes;
        protected final List<StatType<Item>> itemStatTypes;
        private final int[] HEADER_ICON_SPRITE_INDICES;
        protected int selectedHeaderColumn;
        protected final List<Item> items;
        protected final Comparator<Item> comparator;
        @Nullable
        protected StatType<?> selectedStatType;
        protected int field_18760;

        public ItemStatsListWidget(MinecraftClient client) {
            boolean bl;
            super(client, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 20);
            this.HEADER_ICON_SPRITE_INDICES = new int[]{3, 4, 1, 2, 5, 6};
            this.selectedHeaderColumn = -1;
            this.comparator = new ItemComparator();
            this.blockStatTypes = Lists.newArrayList();
            this.blockStatTypes.add(Stats.MINED);
            this.itemStatTypes = Lists.newArrayList(Stats.BROKEN, Stats.CRAFTED, Stats.USED, Stats.PICKED_UP, Stats.DROPPED);
            this.setRenderHeader(true, 20);
            Set<Item> set = Sets.newIdentityHashSet();
            for (Item item : Registry.ITEM) {
                bl = false;
                for (StatType<Item> statType : this.itemStatTypes) {
                    if (!statType.hasStat(item) || StatsScreen.this.statHandler.getStat(statType.getOrCreateStat(item)) <= 0) continue;
                    bl = true;
                }
                if (!bl) continue;
                set.add(item);
            }
            for (Block block : Registry.BLOCK) {
                bl = false;
                for (StatType<ItemConvertible> statType : this.blockStatTypes) {
                    if (!statType.hasStat(block) || StatsScreen.this.statHandler.getStat(statType.getOrCreateStat(block)) <= 0) continue;
                    bl = true;
                }
                if (!bl) continue;
                set.add(block.asItem());
            }
            set.remove(Items.AIR);
            this.items = Lists.newArrayList(set);
            for (int i = 0; i < this.items.size(); ++i) {
                this.addEntry(new Entry());
            }
        }

        @Override
        protected void renderHeader(int x, int y, Tessellator tessellator) {
            int j;
            int i;
            if (!this.client.mouse.wasLeftButtonClicked()) {
                this.selectedHeaderColumn = -1;
            }
            for (i = 0; i < this.HEADER_ICON_SPRITE_INDICES.length; ++i) {
                StatsScreen.this.renderIcon(x + StatsScreen.this.getColumnX(i) - 18, y + 1, 0, this.selectedHeaderColumn == i ? 0 : 18);
            }
            if (this.selectedStatType != null) {
                i = StatsScreen.this.getColumnX(this.getHeaderIndex(this.selectedStatType)) - 36;
                j = this.field_18760 == 1 ? 2 : 1;
                StatsScreen.this.renderIcon(x + i, y + 1, 18 * j, 0);
            }
            for (i = 0; i < this.HEADER_ICON_SPRITE_INDICES.length; ++i) {
                j = this.selectedHeaderColumn == i ? 1 : 0;
                StatsScreen.this.renderIcon(x + StatsScreen.this.getColumnX(i) - 18 + j, y + 1 + j, 18 * this.HEADER_ICON_SPRITE_INDICES[i], 18);
            }
        }

        @Override
        public int getRowWidth() {
            return 375;
        }

        @Override
        protected int getScrollbarPositionX() {
            return this.width / 2 + 140;
        }

        @Override
        protected void renderBackground() {
            StatsScreen.this.renderBackground();
        }

        @Override
        protected void clickedHeader(int x, int y) {
            this.selectedHeaderColumn = -1;
            for (int i = 0; i < this.HEADER_ICON_SPRITE_INDICES.length; ++i) {
                int j = x - StatsScreen.this.getColumnX(i);
                if (j < -36 || j > 0) continue;
                this.selectedHeaderColumn = i;
                break;
            }
            if (this.selectedHeaderColumn >= 0) {
                this.selectStatType(this.getStatType(this.selectedHeaderColumn));
                this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
        }

        private StatType<?> getStatType(int headerColumn) {
            return headerColumn < this.blockStatTypes.size() ? this.blockStatTypes.get(headerColumn) : this.itemStatTypes.get(headerColumn - this.blockStatTypes.size());
        }

        private int getHeaderIndex(StatType<?> statType) {
            int i = this.blockStatTypes.indexOf(statType);
            if (i >= 0) {
                return i;
            }
            int j = this.itemStatTypes.indexOf(statType);
            if (j >= 0) {
                return j + this.blockStatTypes.size();
            }
            return -1;
        }

        @Override
        protected void renderDecorations(int mouseX, int mouseY) {
            if (mouseY < this.top || mouseY > this.bottom) {
                return;
            }
            Entry entry = (Entry)this.getEntryAtPosition(mouseX, mouseY);
            int i = (this.width - this.getRowWidth()) / 2;
            if (entry != null) {
                if (mouseX < i + 40 || mouseX > i + 40 + 20) {
                    return;
                }
                Item item = this.items.get(this.children().indexOf(entry));
                this.render(this.getText(item), mouseX, mouseY);
            } else {
                TranslatableText text = null;
                int j = mouseX - i;
                for (int k = 0; k < this.HEADER_ICON_SPRITE_INDICES.length; ++k) {
                    int l = StatsScreen.this.getColumnX(k);
                    if (j < l - 18 || j > l) continue;
                    text = new TranslatableText(this.getStatType(k).getTranslationKey(), new Object[0]);
                    break;
                }
                this.render(text, mouseX, mouseY);
            }
        }

        protected void render(@Nullable Text text, int x, int y) {
            if (text == null) {
                return;
            }
            String string = text.asFormattedString();
            int i = x + 12;
            int j = y - 12;
            int k = StatsScreen.this.textRenderer.getStringWidth(string);
            this.fillGradient(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0.0f, 0.0f, 400.0f);
            StatsScreen.this.textRenderer.drawWithShadow(string, i, j, -1);
            RenderSystem.popMatrix();
        }

        protected Text getText(Item item) {
            return item.getName();
        }

        protected void selectStatType(StatType<?> statType) {
            if (statType != this.selectedStatType) {
                this.selectedStatType = statType;
                this.field_18760 = -1;
            } else if (this.field_18760 == -1) {
                this.field_18760 = 1;
            } else {
                this.selectedStatType = null;
                this.field_18760 = 0;
            }
            this.items.sort(this.comparator);
        }

        @Environment(value=EnvType.CLIENT)
        class Entry
        extends AlwaysSelectedEntryListWidget.Entry<Entry> {
            private Entry() {
            }

            @Override
            public void render(int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
                int i;
                Item item = ((StatsScreen)StatsScreen.this).itemStats.items.get(index);
                StatsScreen.this.renderStatItem(x + 40, y, item);
                for (i = 0; i < ((StatsScreen)StatsScreen.this).itemStats.blockStatTypes.size(); ++i) {
                    Stat<Block> stat = item instanceof BlockItem ? ((StatsScreen)StatsScreen.this).itemStats.blockStatTypes.get(i).getOrCreateStat(((BlockItem)item).getBlock()) : null;
                    this.render(stat, x + StatsScreen.this.getColumnX(i), y, index % 2 == 0);
                }
                for (i = 0; i < ((StatsScreen)StatsScreen.this).itemStats.itemStatTypes.size(); ++i) {
                    this.render(((StatsScreen)StatsScreen.this).itemStats.itemStatTypes.get(i).getOrCreateStat(item), x + StatsScreen.this.getColumnX(i + ((StatsScreen)StatsScreen.this).itemStats.blockStatTypes.size()), y, index % 2 == 0);
                }
            }

            protected void render(@Nullable Stat<?> stat, int x, int y, boolean light) {
                String string = stat == null ? "-" : stat.format(StatsScreen.this.statHandler.getStat(stat));
                ItemStatsListWidget.this.drawString(StatsScreen.this.textRenderer, string, x - StatsScreen.this.textRenderer.getStringWidth(string), y + 5, light ? 0xFFFFFF : 0x909090);
            }
        }

        @Environment(value=EnvType.CLIENT)
        class ItemComparator
        implements Comparator<Item> {
            private ItemComparator() {
            }

            @Override
            public int compare(Item item, Item item2) {
                int j;
                int i;
                if (ItemStatsListWidget.this.selectedStatType == null) {
                    i = 0;
                    j = 0;
                } else if (ItemStatsListWidget.this.blockStatTypes.contains(ItemStatsListWidget.this.selectedStatType)) {
                    StatType<?> statType = ItemStatsListWidget.this.selectedStatType;
                    i = item instanceof BlockItem ? StatsScreen.this.statHandler.getStat(statType, ((BlockItem)item).getBlock()) : -1;
                    j = item2 instanceof BlockItem ? StatsScreen.this.statHandler.getStat(statType, ((BlockItem)item2).getBlock()) : -1;
                } else {
                    StatType<?> statType = ItemStatsListWidget.this.selectedStatType;
                    i = StatsScreen.this.statHandler.getStat(statType, item);
                    j = StatsScreen.this.statHandler.getStat(statType, item2);
                }
                if (i == j) {
                    return ItemStatsListWidget.this.field_18760 * Integer.compare(Item.getRawId(item), Item.getRawId(item2));
                }
                return ItemStatsListWidget.this.field_18760 * Integer.compare(i, j);
            }

            @Override
            public /* synthetic */ int compare(Object object, Object object2) {
                return this.compare((Item)object, (Item)object2);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class GeneralStatsListWidget
    extends AlwaysSelectedEntryListWidget<Entry> {
        public GeneralStatsListWidget(MinecraftClient minecraftClient) {
            super(minecraftClient, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 10);
            for (Stat<Identifier> stat : Stats.CUSTOM) {
                this.addEntry(new Entry(stat));
            }
        }

        @Override
        protected void renderBackground() {
            StatsScreen.this.renderBackground();
        }

        @Environment(value=EnvType.CLIENT)
        class Entry
        extends AlwaysSelectedEntryListWidget.Entry<Entry> {
            private final Stat<Identifier> stat;

            private Entry(Stat<Identifier> stat) {
                this.stat = stat;
            }

            @Override
            public void render(int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
                Text text = new TranslatableText("stat." + this.stat.getValue().toString().replace(':', '.'), new Object[0]).formatted(Formatting.GRAY);
                GeneralStatsListWidget.this.drawString(StatsScreen.this.textRenderer, text.getString(), x + 2, y + 1, index % 2 == 0 ? 0xFFFFFF : 0x909090);
                String string = this.stat.format(StatsScreen.this.statHandler.getStat(this.stat));
                GeneralStatsListWidget.this.drawString(StatsScreen.this.textRenderer, string, x + 2 + 213 - StatsScreen.this.textRenderer.getStringWidth(string), y + 1, index % 2 == 0 ? 0xFFFFFF : 0x909090);
            }
        }
    }
}

