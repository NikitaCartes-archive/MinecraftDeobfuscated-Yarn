package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.network.packet.ClientStatusC2SPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class StatsScreen extends Screen implements StatsListener {
	protected final Screen parent;
	private StatsScreen.GeneralStatsListWidget generalStats;
	private StatsScreen.ItemStatsListWidget itemStats;
	private StatsScreen.EntityStatsListWidget mobStats;
	private final StatHandler statHandler;
	@Nullable
	private AlwaysSelectedEntryListWidget<?> selectedList;
	private boolean downloadingStats = true;

	public StatsScreen(Screen screen, StatHandler statHandler) {
		super(new TranslatableText("gui.stats"));
		this.parent = screen;
		this.statHandler = statHandler;
	}

	@Override
	protected void init() {
		this.downloadingStats = true;
		this.minecraft.getNetworkHandler().sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS));
	}

	public void createLists() {
		this.generalStats = new StatsScreen.GeneralStatsListWidget(this.minecraft);
		this.itemStats = new StatsScreen.ItemStatsListWidget(this.minecraft);
		this.mobStats = new StatsScreen.EntityStatsListWidget(this.minecraft);
	}

	public void createButtons() {
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 120, this.height - 52, 80, 20, I18n.translate("stat.generalButton"), buttonWidgetx -> this.selectStatList(this.generalStats)
			)
		);
		ButtonWidget buttonWidget = this.addButton(
			new ButtonWidget(this.width / 2 - 40, this.height - 52, 80, 20, I18n.translate("stat.itemsButton"), buttonWidgetx -> this.selectStatList(this.itemStats))
		);
		ButtonWidget buttonWidget2 = this.addButton(
			new ButtonWidget(this.width / 2 + 40, this.height - 52, 80, 20, I18n.translate("stat.mobsButton"), buttonWidgetx -> this.selectStatList(this.mobStats))
		);
		this.addButton(
			new ButtonWidget(this.width / 2 - 100, this.height - 28, 200, 20, I18n.translate("gui.done"), buttonWidgetx -> this.minecraft.openScreen(this.parent))
		);
		if (this.itemStats.children().isEmpty()) {
			buttonWidget.active = false;
		}

		if (this.mobStats.children().isEmpty()) {
			buttonWidget2.active = false;
		}
	}

	@Override
	public void render(int i, int j, float f) {
		if (this.downloadingStats) {
			this.renderBackground();
			this.drawCenteredString(this.font, I18n.translate("multiplayer.downloadingStats"), this.width / 2, this.height / 2, 16777215);
			this.drawCenteredString(
				this.font,
				PROGRESS_BAR_STAGES[(int)(SystemUtil.getMeasuringTimeMs() / 150L % (long)PROGRESS_BAR_STAGES.length)],
				this.width / 2,
				this.height / 2 + 9 * 2,
				16777215
			);
		} else {
			this.getSelectedStatList().render(i, j, f);
			this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 20, 16777215);
			super.render(i, j, f);
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

	public void selectStatList(@Nullable AlwaysSelectedEntryListWidget<?> alwaysSelectedEntryListWidget) {
		this.children.remove(this.generalStats);
		this.children.remove(this.itemStats);
		this.children.remove(this.mobStats);
		if (alwaysSelectedEntryListWidget != null) {
			this.children.add(0, alwaysSelectedEntryListWidget);
			this.selectedList = alwaysSelectedEntryListWidget;
		}
	}

	private int getColumnX(int i) {
		return 115 + 40 * i;
	}

	private void renderStatItem(int i, int j, Item item) {
		this.renderIcon(i + 1, j + 1, 0, 0);
		RenderSystem.enableRescaleNormal();
		GuiLighting.enableForItems();
		this.itemRenderer.renderGuiItemIcon(item.getStackForRender(), i + 2, j + 2);
		GuiLighting.disable();
		RenderSystem.disableRescaleNormal();
	}

	private void renderIcon(int i, int j, int k, int l) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(STATS_ICON_LOCATION);
		blit(i, j, this.blitOffset, (float)k, (float)l, 18, 18, 128, 128);
	}

	@Environment(EnvType.CLIENT)
	class EntityStatsListWidget extends AlwaysSelectedEntryListWidget<StatsScreen.EntityStatsListWidget.Entry> {
		public EntityStatsListWidget(MinecraftClient minecraftClient) {
			super(minecraftClient, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 9 * 4);

			for (EntityType<?> entityType : Registry.ENTITY_TYPE) {
				if (StatsScreen.this.statHandler.getStat(Stats.KILLED.getOrCreateStat(entityType)) > 0
					|| StatsScreen.this.statHandler.getStat(Stats.KILLED_BY.getOrCreateStat(entityType)) > 0) {
					this.addEntry(new StatsScreen.EntityStatsListWidget.Entry(entityType));
				}
			}
		}

		@Override
		protected void renderBackground() {
			StatsScreen.this.renderBackground();
		}

		@Environment(EnvType.CLIENT)
		class Entry extends AlwaysSelectedEntryListWidget.Entry<StatsScreen.EntityStatsListWidget.Entry> {
			private final EntityType<?> entityType;

			public Entry(EntityType<?> entityType) {
				this.entityType = entityType;
			}

			@Override
			public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
				String string = I18n.translate(SystemUtil.createTranslationKey("entity", EntityType.getId(this.entityType)));
				int p = StatsScreen.this.statHandler.getStat(Stats.KILLED.getOrCreateStat(this.entityType));
				int q = StatsScreen.this.statHandler.getStat(Stats.KILLED_BY.getOrCreateStat(this.entityType));
				EntityStatsListWidget.this.drawString(StatsScreen.this.font, string, k + 2, j + 1, 16777215);
				EntityStatsListWidget.this.drawString(StatsScreen.this.font, this.getKilledString(string, p), k + 2 + 10, j + 1 + 9, p == 0 ? 6316128 : 9474192);
				EntityStatsListWidget.this.drawString(StatsScreen.this.font, this.getKilledByString(string, q), k + 2 + 10, j + 1 + 9 * 2, q == 0 ? 6316128 : 9474192);
			}

			private String getKilledString(String string, int i) {
				String string2 = Stats.KILLED.getTranslationKey();
				return i == 0 ? I18n.translate(string2 + ".none", string) : I18n.translate(string2, i, string);
			}

			private String getKilledByString(String string, int i) {
				String string2 = Stats.KILLED_BY.getTranslationKey();
				return i == 0 ? I18n.translate(string2 + ".none", string) : I18n.translate(string2, string, i);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class GeneralStatsListWidget extends AlwaysSelectedEntryListWidget<StatsScreen.GeneralStatsListWidget.Entry> {
		public GeneralStatsListWidget(MinecraftClient minecraftClient) {
			super(minecraftClient, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 10);

			for (Stat<Identifier> stat : Stats.CUSTOM) {
				this.addEntry(new StatsScreen.GeneralStatsListWidget.Entry(stat));
			}
		}

		@Override
		protected void renderBackground() {
			StatsScreen.this.renderBackground();
		}

		@Environment(EnvType.CLIENT)
		class Entry extends AlwaysSelectedEntryListWidget.Entry<StatsScreen.GeneralStatsListWidget.Entry> {
			private final Stat<Identifier> stat;

			private Entry(Stat<Identifier> stat) {
				this.stat = stat;
			}

			@Override
			public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
				Text text = new TranslatableText("stat." + this.stat.getValue().toString().replace(':', '.')).formatted(Formatting.GRAY);
				GeneralStatsListWidget.this.drawString(StatsScreen.this.font, text.getString(), k + 2, j + 1, i % 2 == 0 ? 16777215 : 9474192);
				String string = this.stat.format(StatsScreen.this.statHandler.getStat(this.stat));
				GeneralStatsListWidget.this.drawString(
					StatsScreen.this.font, string, k + 2 + 213 - StatsScreen.this.font.getStringWidth(string), j + 1, i % 2 == 0 ? 16777215 : 9474192
				);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class ItemStatsListWidget extends AlwaysSelectedEntryListWidget<StatsScreen.ItemStatsListWidget.Entry> {
		protected final List<StatType<Block>> blockStatTypes;
		protected final List<StatType<Item>> itemStatTypes;
		private final int[] HEADER_ICON_SPRITE_INDICES = new int[]{3, 4, 1, 2, 5, 6};
		protected int selectedHeaderColumn = -1;
		protected final List<Item> items;
		protected final Comparator<Item> comparator = new StatsScreen.ItemStatsListWidget.ItemComparator();
		@Nullable
		protected StatType<?> selectedStatType;
		protected int field_18760;

		public ItemStatsListWidget(MinecraftClient minecraftClient) {
			super(minecraftClient, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 20);
			this.blockStatTypes = Lists.<StatType<Block>>newArrayList();
			this.blockStatTypes.add(Stats.MINED);
			this.itemStatTypes = Lists.<StatType<Item>>newArrayList(Stats.BROKEN, Stats.CRAFTED, Stats.USED, Stats.PICKED_UP, Stats.DROPPED);
			this.setRenderHeader(true, 20);
			Set<Item> set = Sets.newIdentityHashSet();

			for (Item item : Registry.ITEM) {
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

			for (Block block : Registry.BLOCK) {
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
			this.items = Lists.<Item>newArrayList(set);

			for (int i = 0; i < this.items.size(); i++) {
				this.addEntry(new StatsScreen.ItemStatsListWidget.Entry());
			}
		}

		@Override
		protected void renderHeader(int i, int j, Tessellator tessellator) {
			if (!this.minecraft.mouse.wasLeftButtonClicked()) {
				this.selectedHeaderColumn = -1;
			}

			for (int k = 0; k < this.HEADER_ICON_SPRITE_INDICES.length; k++) {
				StatsScreen.this.renderIcon(i + StatsScreen.this.getColumnX(k) - 18, j + 1, 0, this.selectedHeaderColumn == k ? 0 : 18);
			}

			if (this.selectedStatType != null) {
				int k = StatsScreen.this.getColumnX(this.getHeaderIndex(this.selectedStatType)) - 36;
				int l = this.field_18760 == 1 ? 2 : 1;
				StatsScreen.this.renderIcon(i + k, j + 1, 18 * l, 0);
			}

			for (int k = 0; k < this.HEADER_ICON_SPRITE_INDICES.length; k++) {
				int l = this.selectedHeaderColumn == k ? 1 : 0;
				StatsScreen.this.renderIcon(i + StatsScreen.this.getColumnX(k) - 18 + l, j + 1 + l, 18 * this.HEADER_ICON_SPRITE_INDICES[k], 18);
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
			this.selectedHeaderColumn = -1;

			for (int k = 0; k < this.HEADER_ICON_SPRITE_INDICES.length; k++) {
				int l = i - StatsScreen.this.getColumnX(k);
				if (l >= -36 && l <= 0) {
					this.selectedHeaderColumn = k;
					break;
				}
			}

			if (this.selectedHeaderColumn >= 0) {
				this.selectStatType(this.getStatType(this.selectedHeaderColumn));
				this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			}
		}

		private StatType<?> getStatType(int i) {
			return i < this.blockStatTypes.size() ? (StatType)this.blockStatTypes.get(i) : (StatType)this.itemStatTypes.get(i - this.blockStatTypes.size());
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
		protected void renderDecorations(int i, int j) {
			if (j >= this.top && j <= this.bottom) {
				StatsScreen.ItemStatsListWidget.Entry entry = this.getEntryAtPosition((double)i, (double)j);
				int k = (this.width - this.getRowWidth()) / 2;
				if (entry != null) {
					if (i < k + 40 || i > k + 40 + 20) {
						return;
					}

					Item item = (Item)this.items.get(this.children().indexOf(entry));
					this.render(this.getText(item), i, j);
				} else {
					Text text = null;
					int l = i - k;

					for (int m = 0; m < this.HEADER_ICON_SPRITE_INDICES.length; m++) {
						int n = StatsScreen.this.getColumnX(m);
						if (l >= n - 18 && l <= n) {
							text = new TranslatableText(this.getStatType(m).getTranslationKey());
							break;
						}
					}

					this.render(text, i, j);
				}
			}
		}

		protected void render(@Nullable Text text, int i, int j) {
			if (text != null) {
				String string = text.asFormattedString();
				int k = i + 12;
				int l = j - 12;
				int m = StatsScreen.this.font.getStringWidth(string);
				this.fillGradient(k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
				StatsScreen.this.font.drawWithShadow(string, (float)k, (float)l, -1);
			}
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

		@Environment(EnvType.CLIENT)
		class Entry extends AlwaysSelectedEntryListWidget.Entry<StatsScreen.ItemStatsListWidget.Entry> {
			private Entry() {
			}

			@Override
			public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
				Item item = (Item)StatsScreen.this.itemStats.items.get(i);
				StatsScreen.this.renderStatItem(k + 40, j, item);

				for (int p = 0; p < StatsScreen.this.itemStats.blockStatTypes.size(); p++) {
					Stat<Block> stat;
					if (item instanceof BlockItem) {
						stat = ((StatType)StatsScreen.this.itemStats.blockStatTypes.get(p)).getOrCreateStat(((BlockItem)item).getBlock());
					} else {
						stat = null;
					}

					this.render(stat, k + StatsScreen.this.getColumnX(p), j, i % 2 == 0);
				}

				for (int p = 0; p < StatsScreen.this.itemStats.itemStatTypes.size(); p++) {
					this.render(
						((StatType)StatsScreen.this.itemStats.itemStatTypes.get(p)).getOrCreateStat(item),
						k + StatsScreen.this.getColumnX(p + StatsScreen.this.itemStats.blockStatTypes.size()),
						j,
						i % 2 == 0
					);
				}
			}

			protected void render(@Nullable Stat<?> stat, int i, int j, boolean bl) {
				String string = stat == null ? "-" : stat.format(StatsScreen.this.statHandler.getStat(stat));
				ItemStatsListWidget.this.drawString(StatsScreen.this.font, string, i - StatsScreen.this.font.getStringWidth(string), j + 5, bl ? 16777215 : 9474192);
			}
		}

		@Environment(EnvType.CLIENT)
		class ItemComparator implements Comparator<Item> {
			private ItemComparator() {
			}

			public int method_2297(Item item, Item item2) {
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
					? ItemStatsListWidget.this.field_18760 * Integer.compare(Item.getRawId(item), Item.getRawId(item2))
					: ItemStatsListWidget.this.field_18760 * Integer.compare(i, j);
			}
		}
	}
}
