package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class StatsScreen extends Screen implements StatsListener {
	static final Identifier SLOT_TEXTURE = new Identifier("container/slot");
	static final Identifier HEADER_TEXTURE = new Identifier("statistics/header");
	static final Identifier SORT_UP_TEXTURE = new Identifier("statistics/sort_up");
	static final Identifier SORT_DOWN_TEXTURE = new Identifier("statistics/sort_down");
	private static final Text DOWNLOADING_STATS_TEXT = Text.translatable("multiplayer.downloadingStats");
	protected final Screen parent;
	private StatsScreen.GeneralStatsListWidget generalStats;
	StatsScreen.ItemStatsListWidget itemStats;
	private StatsScreen.EntityStatsListWidget mobStats;
	final StatHandler statHandler;
	@Nullable
	private AlwaysSelectedEntryListWidget<?> selectedList;
	private boolean downloadingStats = true;
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
		this.generalStats = new StatsScreen.GeneralStatsListWidget(this.client);
		this.itemStats = new StatsScreen.ItemStatsListWidget(this.client);
		this.mobStats = new StatsScreen.EntityStatsListWidget(this.client);
	}

	public void createButtons() {
		this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("stat.generalButton"), button -> this.selectStatList(this.generalStats))
				.dimensions(this.width / 2 - 120, this.height - 52, 80, 20)
				.build()
		);
		ButtonWidget buttonWidget = this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("stat.itemsButton"), button -> this.selectStatList(this.itemStats))
				.dimensions(this.width / 2 - 40, this.height - 52, 80, 20)
				.build()
		);
		ButtonWidget buttonWidget2 = this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("stat.mobsButton"), button -> this.selectStatList(this.mobStats))
				.dimensions(this.width / 2 + 40, this.height - 52, 80, 20)
				.build()
		);
		this.addDrawableChild(
			ButtonWidget.builder(ScreenTexts.DONE, button -> this.client.setScreen(this.parent)).dimensions(this.width / 2 - 100, this.height - 28, 200, 20).build()
		);
		if (this.itemStats.children().isEmpty()) {
			buttonWidget.active = false;
		}

		if (this.mobStats.children().isEmpty()) {
			buttonWidget2.active = false;
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.downloadingStats) {
			this.renderBackground(context, mouseX, mouseY, delta);
			context.drawCenteredTextWithShadow(this.textRenderer, DOWNLOADING_STATS_TEXT, this.width / 2, this.height / 2, 16777215);
			context.drawCenteredTextWithShadow(
				this.textRenderer,
				PROGRESS_BAR_STAGES[(int)(Util.getMeasuringTimeMs() / 150L % (long)PROGRESS_BAR_STAGES.length)],
				this.width / 2,
				this.height / 2 + 9 * 2,
				16777215
			);
		} else {
			super.render(context, mouseX, mouseY, delta);
			this.getSelectedStatList().render(context, mouseX, mouseY, delta);
			context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);
		}
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(context);
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

	void renderStatItem(DrawContext context, int x, int y, Item item) {
		this.renderIcon(context, x + 1, y + 1, SLOT_TEXTURE);
		context.drawItemWithoutEntity(item.getDefaultStack(), x + 2, y + 2);
	}

	void renderIcon(DrawContext context, int x, int y, Identifier texture) {
		context.drawGuiTexture(texture, x, y, 0, 18, 18);
	}

	@Environment(EnvType.CLIENT)
	class EntityStatsListWidget extends AlwaysSelectedEntryListWidget<StatsScreen.EntityStatsListWidget.Entry> {
		public EntityStatsListWidget(MinecraftClient client) {
			super(client, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 9 * 4);

			for (EntityType<?> entityType : Registries.ENTITY_TYPE) {
				if (StatsScreen.this.statHandler.getStat(Stats.KILLED.getOrCreateStat(entityType)) > 0
					|| StatsScreen.this.statHandler.getStat(Stats.KILLED_BY.getOrCreateStat(entityType)) > 0) {
					this.addEntry(new StatsScreen.EntityStatsListWidget.Entry(entityType));
				}
			}
		}

		@Environment(EnvType.CLIENT)
		class Entry extends AlwaysSelectedEntryListWidget.Entry<StatsScreen.EntityStatsListWidget.Entry> {
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
			public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				context.drawTextWithShadow(StatsScreen.this.textRenderer, this.entityTypeName, x + 2, y + 1, 16777215);
				context.drawTextWithShadow(StatsScreen.this.textRenderer, this.killedText, x + 2 + 10, y + 1 + 9, this.killedAny ? 9474192 : 6316128);
				context.drawTextWithShadow(StatsScreen.this.textRenderer, this.killedByText, x + 2 + 10, y + 1 + 9 * 2, this.killedByAny ? 9474192 : 6316128);
			}

			@Override
			public Text getNarration() {
				return Text.translatable("narrator.select", ScreenTexts.joinSentences(this.killedText, this.killedByText));
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class GeneralStatsListWidget extends AlwaysSelectedEntryListWidget<StatsScreen.GeneralStatsListWidget.Entry> {
		public GeneralStatsListWidget(MinecraftClient client) {
			super(client, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 10);
			ObjectArrayList<Stat<Identifier>> objectArrayList = new ObjectArrayList<>(Stats.CUSTOM.iterator());
			objectArrayList.sort(Comparator.comparing(statx -> I18n.translate(StatsScreen.getStatTranslationKey(statx))));

			for (Stat<Identifier> stat : objectArrayList) {
				this.addEntry(new StatsScreen.GeneralStatsListWidget.Entry(stat));
			}
		}

		@Environment(EnvType.CLIENT)
		class Entry extends AlwaysSelectedEntryListWidget.Entry<StatsScreen.GeneralStatsListWidget.Entry> {
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
			public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				context.drawTextWithShadow(StatsScreen.this.textRenderer, this.displayName, x + 2, y + 1, index % 2 == 0 ? 16777215 : 9474192);
				String string = this.getFormatted();
				context.drawTextWithShadow(
					StatsScreen.this.textRenderer, string, x + 2 + 213 - StatsScreen.this.textRenderer.getWidth(string), y + 1, index % 2 == 0 ? 16777215 : 9474192
				);
			}

			@Override
			public Text getNarration() {
				return Text.translatable("narrator.select", Text.empty().append(this.displayName).append(ScreenTexts.SPACE).append(this.getFormatted()));
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class ItemStatsListWidget extends AlwaysSelectedEntryListWidget<StatsScreen.ItemStatsListWidget.Entry> {
		protected final List<StatType<Block>> blockStatTypes;
		protected final List<StatType<Item>> itemStatTypes;
		private final Identifier[] headerIconTextures = new Identifier[]{
			new Identifier("statistics/block_mined"),
			new Identifier("statistics/item_broken"),
			new Identifier("statistics/item_crafted"),
			new Identifier("statistics/item_used"),
			new Identifier("statistics/item_picked_up"),
			new Identifier("statistics/item_dropped")
		};
		protected int selectedHeaderColumn = -1;
		protected final Comparator<StatsScreen.ItemStatsListWidget.Entry> comparator = new StatsScreen.ItemStatsListWidget.ItemComparator();
		@Nullable
		protected StatType<?> selectedStatType;
		protected int listOrder;

		public ItemStatsListWidget(MinecraftClient client) {
			super(client, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 20);
			this.blockStatTypes = Lists.<StatType<Block>>newArrayList();
			this.blockStatTypes.add(Stats.MINED);
			this.itemStatTypes = Lists.<StatType<Item>>newArrayList(Stats.BROKEN, Stats.CRAFTED, Stats.USED, Stats.PICKED_UP, Stats.DROPPED);
			this.setRenderHeader(true, 20);
			Set<Item> set = Sets.newIdentityHashSet();

			for (Item item : Registries.ITEM) {
				boolean bl = false;

				for (StatType<Item> statType : this.itemStatTypes) {
					if (statType.hasStat(item) && StatsScreen.this.statHandler.getStat(statType.getOrCreateStat(item)) > 0) {
						bl = true;
					}
				}

				if (bl) {
					set.add(item);
				}
			}

			for (Block block : Registries.BLOCK) {
				boolean bl = false;

				for (StatType<Block> statTypex : this.blockStatTypes) {
					if (statTypex.hasStat(block) && StatsScreen.this.statHandler.getStat(statTypex.getOrCreateStat(block)) > 0) {
						bl = true;
					}
				}

				if (bl) {
					set.add(block.asItem());
				}
			}

			set.remove(Items.AIR);

			for (Item item : set) {
				this.addEntry(new StatsScreen.ItemStatsListWidget.Entry(item));
			}
		}

		@Override
		protected void renderHeader(DrawContext context, int x, int y) {
			if (!this.client.mouse.wasLeftButtonClicked()) {
				this.selectedHeaderColumn = -1;
			}

			for (int i = 0; i < this.headerIconTextures.length; i++) {
				Identifier identifier = this.selectedHeaderColumn == i ? StatsScreen.SLOT_TEXTURE : StatsScreen.HEADER_TEXTURE;
				StatsScreen.this.renderIcon(context, x + StatsScreen.this.getColumnX(i) - 18, y + 1, identifier);
			}

			if (this.selectedStatType != null) {
				int i = StatsScreen.this.getColumnX(this.getHeaderIndex(this.selectedStatType)) - 36;
				Identifier identifier = this.listOrder == 1 ? StatsScreen.SORT_UP_TEXTURE : StatsScreen.SORT_DOWN_TEXTURE;
				StatsScreen.this.renderIcon(context, x + i, y + 1, identifier);
			}

			for (int i = 0; i < this.headerIconTextures.length; i++) {
				int j = this.selectedHeaderColumn == i ? 1 : 0;
				StatsScreen.this.renderIcon(context, x + StatsScreen.this.getColumnX(i) - 18 + j, y + 1 + j, this.headerIconTextures[i]);
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
		protected void clickedHeader(int x, int y) {
			this.selectedHeaderColumn = -1;

			for (int i = 0; i < this.headerIconTextures.length; i++) {
				int j = x - StatsScreen.this.getColumnX(i);
				if (j >= -36 && j <= 0) {
					this.selectedHeaderColumn = i;
					break;
				}
			}

			if (this.selectedHeaderColumn >= 0) {
				this.selectStatType(this.getStatType(this.selectedHeaderColumn));
				this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			}
		}

		private StatType<?> getStatType(int headerColumn) {
			return headerColumn < this.blockStatTypes.size()
				? (StatType)this.blockStatTypes.get(headerColumn)
				: (StatType)this.itemStatTypes.get(headerColumn - this.blockStatTypes.size());
		}

		private int getHeaderIndex(StatType<?> statType) {
			int i = this.blockStatTypes.indexOf(statType);
			if (i >= 0) {
				return i;
			} else {
				int j = this.itemStatTypes.indexOf(statType);
				return j >= 0 ? j + this.blockStatTypes.size() : -1;
			}
		}

		@Override
		protected void renderDecorations(DrawContext context, int mouseX, int mouseY) {
			if (mouseY >= this.top && mouseY <= this.bottom) {
				StatsScreen.ItemStatsListWidget.Entry entry = this.getHoveredEntry();
				int i = (this.width - this.getRowWidth()) / 2;
				if (entry != null) {
					if (mouseX < i + 40 || mouseX > i + 40 + 20) {
						return;
					}

					Item item = entry.getItem();
					context.drawTooltip(StatsScreen.this.textRenderer, this.getText(item), mouseX, mouseY);
				} else {
					Text text = null;
					int j = mouseX - i;

					for (int k = 0; k < this.headerIconTextures.length; k++) {
						int l = StatsScreen.this.getColumnX(k);
						if (j >= l - 18 && j <= l) {
							text = this.getStatType(k).getName();
							break;
						}
					}

					if (text != null) {
						context.drawTooltip(StatsScreen.this.textRenderer, text, mouseX, mouseY);
					}
				}
			}
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

		@Environment(EnvType.CLIENT)
		class Entry extends AlwaysSelectedEntryListWidget.Entry<StatsScreen.ItemStatsListWidget.Entry> {
			private final Item item;

			Entry(Item item) {
				this.item = item;
			}

			public Item getItem() {
				return this.item;
			}

			@Override
			public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				StatsScreen.this.renderStatItem(context, x + 40, y, this.item);

				for (int i = 0; i < StatsScreen.this.itemStats.blockStatTypes.size(); i++) {
					Stat<Block> stat;
					if (this.item instanceof BlockItem) {
						stat = ((StatType)StatsScreen.this.itemStats.blockStatTypes.get(i)).getOrCreateStat(((BlockItem)this.item).getBlock());
					} else {
						stat = null;
					}

					this.render(context, stat, x + StatsScreen.this.getColumnX(i), y, index % 2 == 0);
				}

				for (int i = 0; i < StatsScreen.this.itemStats.itemStatTypes.size(); i++) {
					this.render(
						context,
						((StatType)StatsScreen.this.itemStats.itemStatTypes.get(i)).getOrCreateStat(this.item),
						x + StatsScreen.this.getColumnX(i + StatsScreen.this.itemStats.blockStatTypes.size()),
						y,
						index % 2 == 0
					);
				}
			}

			protected void render(DrawContext context, @Nullable Stat<?> stat, int x, int y, boolean white) {
				String string = stat == null ? "-" : stat.format(StatsScreen.this.statHandler.getStat(stat));
				context.drawTextWithShadow(StatsScreen.this.textRenderer, string, x - StatsScreen.this.textRenderer.getWidth(string), y + 5, white ? 16777215 : 9474192);
			}

			@Override
			public Text getNarration() {
				return Text.translatable("narrator.select", this.item.getName());
			}
		}

		@Environment(EnvType.CLIENT)
		class ItemComparator implements Comparator<StatsScreen.ItemStatsListWidget.Entry> {
			public int compare(StatsScreen.ItemStatsListWidget.Entry entry, StatsScreen.ItemStatsListWidget.Entry entry2) {
				Item item = entry.getItem();
				Item item2 = entry2.getItem();
				int i;
				int j;
				if (ItemStatsListWidget.this.selectedStatType == null) {
					i = 0;
					j = 0;
				} else if (ItemStatsListWidget.this.blockStatTypes.contains(ItemStatsListWidget.this.selectedStatType)) {
					StatType<Block> statType = (StatType<Block>)ItemStatsListWidget.this.selectedStatType;
					i = item instanceof BlockItem ? StatsScreen.this.statHandler.getStat(statType, ((BlockItem)item).getBlock()) : -1;
					j = item2 instanceof BlockItem ? StatsScreen.this.statHandler.getStat(statType, ((BlockItem)item2).getBlock()) : -1;
				} else {
					StatType<Item> statType = (StatType<Item>)ItemStatsListWidget.this.selectedStatType;
					i = StatsScreen.this.statHandler.getStat(statType, item);
					j = StatsScreen.this.statHandler.getStat(statType, item2);
				}

				return i == j
					? ItemStatsListWidget.this.listOrder * Integer.compare(Item.getRawId(item), Item.getRawId(item2))
					: ItemStatsListWidget.this.listOrder * Integer.compare(i, j);
			}
		}
	}
}
