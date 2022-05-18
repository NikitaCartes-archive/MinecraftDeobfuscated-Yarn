/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.StatsListener;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class StatsScreen
extends Screen
implements StatsListener {
    private static final Text DOWNLOADING_STATS_TEXT = Text.translatable("multiplayer.downloadingStats");
    protected final Screen parent;
    private GeneralStatsListWidget generalStats;
    ItemStatsListWidget itemStats;
    private EntityStatsListWidget mobStats;
    final StatHandler statHandler;
    @Nullable
    private AlwaysSelectedEntryListWidget<?> selectedList;
    private boolean downloadingStats = true;
    private static final int field_32281 = 128;
    private static final int field_32282 = 18;
    private static final int field_32283 = 20;
    private static final int field_32284 = 1;
    private static final int field_32285 = 1;
    private static final int field_32274 = 2;
    private static final int field_32275 = 2;
    private static final int field_32276 = 40;
    private static final int field_32277 = 5;
    private static final int field_32278 = 0;
    private static final int field_32279 = -1;
    private static final int field_32280 = 1;

    public StatsScreen(Screen parent, StatHandler statHandler) {
        super(Text.translatable("gui.stats"));
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
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 120, this.height - 52, 80, 20, Text.translatable("stat.generalButton"), button -> this.selectStatList(this.generalStats)));
        ButtonWidget buttonWidget = this.addDrawableChild(new ButtonWidget(this.width / 2 - 40, this.height - 52, 80, 20, Text.translatable("stat.itemsButton"), button -> this.selectStatList(this.itemStats)));
        ButtonWidget buttonWidget2 = this.addDrawableChild(new ButtonWidget(this.width / 2 + 40, this.height - 52, 80, 20, Text.translatable("stat.mobsButton"), button -> this.selectStatList(this.mobStats)));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 28, 200, 20, ScreenTexts.DONE, button -> this.client.setScreen(this.parent)));
        if (this.itemStats.children().isEmpty()) {
            buttonWidget.active = false;
        }
        if (this.mobStats.children().isEmpty()) {
            buttonWidget2.active = false;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.downloadingStats) {
            this.renderBackground(matrices);
            StatsScreen.drawCenteredText(matrices, this.textRenderer, DOWNLOADING_STATS_TEXT, this.width / 2, this.height / 2, 0xFFFFFF);
            StatsScreen.drawCenteredText(matrices, this.textRenderer, PROGRESS_BAR_STAGES[(int)(Util.getMeasuringTimeMs() / 150L % (long)PROGRESS_BAR_STAGES.length)], this.width / 2, this.height / 2 + this.textRenderer.fontHeight * 2, 0xFFFFFF);
        } else {
            this.getSelectedStatList().render(matrices, mouseX, mouseY, delta);
            StatsScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
            super.render(matrices, mouseX, mouseY, delta);
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
    public boolean shouldPause() {
        return !this.downloadingStats;
    }

    @Nullable
    public AlwaysSelectedEntryListWidget<?> getSelectedStatList() {
        return this.selectedList;
    }

    public void selectStatList(@Nullable AlwaysSelectedEntryListWidget<?> list) {
        if (this.selectedList != null) {
            this.remove(this.selectedList);
        }
        if (list != null) {
            this.addSelectableChild(list);
            this.selectedList = list;
        }
    }

    static String getStatTranslationKey(Stat<Identifier> stat) {
        return "stat." + stat.getValue().toString().replace(':', '.');
    }

    int getColumnX(int index) {
        return 115 + 40 * index;
    }

    void renderStatItem(MatrixStack matrices, int x, int y, Item item) {
        this.renderIcon(matrices, x + 1, y + 1, 0, 0);
        this.itemRenderer.renderGuiItemIcon(item.getDefaultStack(), x + 2, y + 2);
    }

    void renderIcon(MatrixStack matrices, int x, int y, int u, int v) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, STATS_ICON_TEXTURE);
        StatsScreen.drawTexture(matrices, x, y, this.getZOffset(), u, v, 18, 18, 128, 128);
    }

    @Environment(value=EnvType.CLIENT)
    class GeneralStatsListWidget
    extends AlwaysSelectedEntryListWidget<Entry> {
        public GeneralStatsListWidget(MinecraftClient client) {
            super(client, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 10);
            ObjectArrayList<Stat<Identifier>> objectArrayList = new ObjectArrayList<Stat<Identifier>>(Stats.CUSTOM.iterator());
            objectArrayList.sort((Comparator<Stat<Identifier>>)Comparator.comparing(stat -> I18n.translate(StatsScreen.getStatTranslationKey(stat), new Object[0])));
            for (Stat stat2 : objectArrayList) {
                this.addEntry(new Entry(stat2));
            }
        }

        @Override
        protected void renderBackground(MatrixStack matrices) {
            StatsScreen.this.renderBackground(matrices);
        }

        @Environment(value=EnvType.CLIENT)
        class Entry
        extends AlwaysSelectedEntryListWidget.Entry<Entry> {
            private final Stat<Identifier> stat;
            private final Text displayName;

            Entry(Stat<Identifier> stat) {
                this.stat = stat;
                this.displayName = Text.translatable(StatsScreen.getStatTranslationKey(stat));
            }

            private String getFormatted() {
                return this.stat.format(StatsScreen.this.statHandler.getStat(this.stat));
            }

            @Override
            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                DrawableHelper.drawTextWithShadow(matrices, StatsScreen.this.textRenderer, this.displayName, x + 2, y + 1, index % 2 == 0 ? 0xFFFFFF : 0x909090);
                String string = this.getFormatted();
                DrawableHelper.drawStringWithShadow(matrices, StatsScreen.this.textRenderer, string, x + 2 + 213 - StatsScreen.this.textRenderer.getWidth(string), y + 1, index % 2 == 0 ? 0xFFFFFF : 0x909090);
            }

            @Override
            public Text getNarration() {
                return Text.translatable("narrator.select", Text.empty().append(this.displayName).append(" ").append(this.getFormatted()));
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
        protected final Comparator<Entry> comparator;
        @Nullable
        protected StatType<?> selectedStatType;
        protected int listOrder;

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
            for (Item item : set) {
                this.addEntry(new Entry(item));
            }
        }

        @Override
        protected void renderHeader(MatrixStack matrices, int x, int y, Tessellator tessellator) {
            int j;
            int i;
            if (!this.client.mouse.wasLeftButtonClicked()) {
                this.selectedHeaderColumn = -1;
            }
            for (i = 0; i < this.HEADER_ICON_SPRITE_INDICES.length; ++i) {
                StatsScreen.this.renderIcon(matrices, x + StatsScreen.this.getColumnX(i) - 18, y + 1, 0, this.selectedHeaderColumn == i ? 0 : 18);
            }
            if (this.selectedStatType != null) {
                i = StatsScreen.this.getColumnX(this.getHeaderIndex(this.selectedStatType)) - 36;
                j = this.listOrder == 1 ? 2 : 1;
                StatsScreen.this.renderIcon(matrices, x + i, y + 1, 18 * j, 0);
            }
            for (i = 0; i < this.HEADER_ICON_SPRITE_INDICES.length; ++i) {
                j = this.selectedHeaderColumn == i ? 1 : 0;
                StatsScreen.this.renderIcon(matrices, x + StatsScreen.this.getColumnX(i) - 18 + j, y + 1 + j, 18 * this.HEADER_ICON_SPRITE_INDICES[i], 18);
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
        protected void renderBackground(MatrixStack matrices) {
            StatsScreen.this.renderBackground(matrices);
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
        protected void renderDecorations(MatrixStack matrices, int mouseX, int mouseY) {
            if (mouseY < this.top || mouseY > this.bottom) {
                return;
            }
            Entry entry = (Entry)this.getHoveredEntry();
            int i = (this.width - this.getRowWidth()) / 2;
            if (entry != null) {
                if (mouseX < i + 40 || mouseX > i + 40 + 20) {
                    return;
                }
                Item item = entry.getItem();
                this.render(matrices, this.getText(item), mouseX, mouseY);
            } else {
                Text text = null;
                int j = mouseX - i;
                for (int k = 0; k < this.HEADER_ICON_SPRITE_INDICES.length; ++k) {
                    int l = StatsScreen.this.getColumnX(k);
                    if (j < l - 18 || j > l) continue;
                    text = this.getStatType(k).getName();
                    break;
                }
                this.render(matrices, text, mouseX, mouseY);
            }
        }

        protected void render(MatrixStack matrices, @Nullable Text text, int mouseX, int mouseY) {
            if (text == null) {
                return;
            }
            int i = mouseX + 12;
            int j = mouseY - 12;
            int k = StatsScreen.this.textRenderer.getWidth(text);
            this.fillGradient(matrices, i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
            matrices.push();
            matrices.translate(0.0, 0.0, 400.0);
            StatsScreen.this.textRenderer.drawWithShadow(matrices, text, (float)i, (float)j, -1);
            matrices.pop();
        }

        protected Text getText(Item item) {
            return item.getName();
        }

        protected void selectStatType(StatType<?> statType) {
            if (statType != this.selectedStatType) {
                this.selectedStatType = statType;
                this.listOrder = -1;
            } else if (this.listOrder == -1) {
                this.listOrder = 1;
            } else {
                this.selectedStatType = null;
                this.listOrder = 0;
            }
            this.children().sort(this.comparator);
        }

        @Environment(value=EnvType.CLIENT)
        class ItemComparator
        implements Comparator<Entry> {
            ItemComparator() {
            }

            @Override
            public int compare(Entry entry, Entry entry2) {
                int j;
                int i;
                Item item = entry.getItem();
                Item item2 = entry2.getItem();
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
                    return ItemStatsListWidget.this.listOrder * Integer.compare(Item.getRawId(item), Item.getRawId(item2));
                }
                return ItemStatsListWidget.this.listOrder * Integer.compare(i, j);
            }

            @Override
            public /* synthetic */ int compare(Object a, Object b) {
                return this.compare((Entry)a, (Entry)b);
            }
        }

        @Environment(value=EnvType.CLIENT)
        class Entry
        extends AlwaysSelectedEntryListWidget.Entry<Entry> {
            private final Item item;

            Entry(Item item) {
                this.item = item;
            }

            public Item getItem() {
                return this.item;
            }

            @Override
            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                int i;
                StatsScreen.this.renderStatItem(matrices, x + 40, y, this.item);
                for (i = 0; i < StatsScreen.this.itemStats.blockStatTypes.size(); ++i) {
                    Stat<Block> stat = this.item instanceof BlockItem ? StatsScreen.this.itemStats.blockStatTypes.get(i).getOrCreateStat(((BlockItem)this.item).getBlock()) : null;
                    this.render(matrices, stat, x + StatsScreen.this.getColumnX(i), y, index % 2 == 0);
                }
                for (i = 0; i < StatsScreen.this.itemStats.itemStatTypes.size(); ++i) {
                    this.render(matrices, StatsScreen.this.itemStats.itemStatTypes.get(i).getOrCreateStat(this.item), x + StatsScreen.this.getColumnX(i + StatsScreen.this.itemStats.blockStatTypes.size()), y, index % 2 == 0);
                }
            }

            protected void render(MatrixStack matrices, @Nullable Stat<?> stat, int x, int y, boolean white) {
                String string = stat == null ? "-" : stat.format(StatsScreen.this.statHandler.getStat(stat));
                DrawableHelper.drawStringWithShadow(matrices, StatsScreen.this.textRenderer, string, x - StatsScreen.this.textRenderer.getWidth(string), y + 5, white ? 0xFFFFFF : 0x909090);
            }

            @Override
            public Text getNarration() {
                return Text.translatable("narrator.select", this.item.getName());
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class EntityStatsListWidget
    extends AlwaysSelectedEntryListWidget<Entry> {
        public EntityStatsListWidget(MinecraftClient client) {
            super(client, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, ((StatsScreen)StatsScreen.this).textRenderer.fontHeight * 4);
            for (EntityType entityType : Registry.ENTITY_TYPE) {
                if (StatsScreen.this.statHandler.getStat(Stats.KILLED.getOrCreateStat(entityType)) <= 0 && StatsScreen.this.statHandler.getStat(Stats.KILLED_BY.getOrCreateStat(entityType)) <= 0) continue;
                this.addEntry(new Entry(entityType));
            }
        }

        @Override
        protected void renderBackground(MatrixStack matrices) {
            StatsScreen.this.renderBackground(matrices);
        }

        @Environment(value=EnvType.CLIENT)
        class Entry
        extends AlwaysSelectedEntryListWidget.Entry<Entry> {
            private final Text entityTypeName;
            private final Text killedText;
            private final boolean killedAny;
            private final Text killedByText;
            private final boolean killedByAny;

            public Entry(EntityType<?> entityType) {
                this.entityTypeName = entityType.getName();
                int i = StatsScreen.this.statHandler.getStat(Stats.KILLED.getOrCreateStat(entityType));
                if (i == 0) {
                    this.killedText = Text.translatable("stat_type.minecraft.killed.none", this.entityTypeName);
                    this.killedAny = false;
                } else {
                    this.killedText = Text.translatable("stat_type.minecraft.killed", i, this.entityTypeName);
                    this.killedAny = true;
                }
                int j = StatsScreen.this.statHandler.getStat(Stats.KILLED_BY.getOrCreateStat(entityType));
                if (j == 0) {
                    this.killedByText = Text.translatable("stat_type.minecraft.killed_by.none", this.entityTypeName);
                    this.killedByAny = false;
                } else {
                    this.killedByText = Text.translatable("stat_type.minecraft.killed_by", this.entityTypeName, j);
                    this.killedByAny = true;
                }
            }

            @Override
            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                DrawableHelper.drawTextWithShadow(matrices, StatsScreen.this.textRenderer, this.entityTypeName, x + 2, y + 1, 0xFFFFFF);
                DrawableHelper.drawTextWithShadow(matrices, StatsScreen.this.textRenderer, this.killedText, x + 2 + 10, y + 1 + ((StatsScreen)StatsScreen.this).textRenderer.fontHeight, this.killedAny ? 0x909090 : 0x606060);
                DrawableHelper.drawTextWithShadow(matrices, StatsScreen.this.textRenderer, this.killedByText, x + 2 + 10, y + 1 + ((StatsScreen)StatsScreen.this).textRenderer.fontHeight * 2, this.killedByAny ? 0x909090 : 0x606060);
            }

            @Override
            public Text getNarration() {
                return Text.translatable("narrator.select", ScreenTexts.joinSentences(this.killedText, this.killedByText));
            }
        }
    }
}

