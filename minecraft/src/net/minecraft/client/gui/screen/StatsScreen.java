package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
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

	public StatsScreen(Screen parent, StatHandler statHandler) {
		super(new TranslatableText("gui.stats"));
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
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 120, this.height - 52, 80, 20, new TranslatableText("stat.generalButton"), buttonWidgetx -> this.selectStatList(this.generalStats)
			)
		);
		ButtonWidget buttonWidget = this.addButton(
			new ButtonWidget(
				this.width / 2 - 40, this.height - 52, 80, 20, new TranslatableText("stat.itemsButton"), buttonWidgetx -> this.selectStatList(this.itemStats)
			)
		);
		ButtonWidget buttonWidget2 = this.addButton(
			new ButtonWidget(this.width / 2 + 40, this.height - 52, 80, 20, new TranslatableText("stat.mobsButton"), buttonWidgetx -> this.selectStatList(this.mobStats))
		);
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 28, 200, 20, ScreenTexts.DONE, buttonWidgetx -> this.client.openScreen(this.parent)));
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
			this.drawCenteredString(matrices, this.textRenderer, I18n.translate("multiplayer.downloadingStats"), this.width / 2, this.height / 2, 16777215);
			this.drawCenteredString(
				matrices,
				this.textRenderer,
				PROGRESS_BAR_STAGES[(int)(Util.getMeasuringTimeMs() / 150L % (long)PROGRESS_BAR_STAGES.length)],
				this.width / 2,
				this.height / 2 + 9 * 2,
				16777215
			);
		} else {
			this.getSelectedStatList().render(matrices, mouseX, mouseY, delta);
			this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
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

	private static String method_27027(Stat<Identifier> stat) {
		return "stat." + stat.getValue().toString().replace(':', '.');
	}

	private int getColumnX(int index) {
		return 115 + 40 * index;
	}

	private void renderStatItem(MatrixStack matrixStack, int i, int j, Item item) {
		this.renderIcon(matrixStack, i + 1, j + 1, 0, 0);
		RenderSystem.enableRescaleNormal();
		this.itemRenderer.renderGuiItemIcon(item.getStackForRender(), i + 2, j + 2);
		RenderSystem.disableRescaleNormal();
	}

	private void renderIcon(MatrixStack matrixStack, int i, int j, int k, int l) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(STATS_ICON_TEXTURE);
		drawTexture(matrixStack, i, j, this.getZOffset(), (float)k, (float)l, 18, 18, 128, 128);
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
		protected void renderBackground(MatrixStack matrixStack) {
			StatsScreen.this.renderBackground(matrixStack);
		}

		@Environment(EnvType.CLIENT)
		class Entry extends AlwaysSelectedEntryListWidget.Entry<StatsScreen.EntityStatsListWidget.Entry> {
			private final EntityType<?> entityType;

			public Entry(EntityType<?> entityType) {
				this.entityType = entityType;
			}

			@Override
			public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				String string = I18n.translate(Util.createTranslationKey("entity", EntityType.getId(this.entityType)));
				int i = StatsScreen.this.statHandler.getStat(Stats.KILLED.getOrCreateStat(this.entityType));
				int j = StatsScreen.this.statHandler.getStat(Stats.KILLED_BY.getOrCreateStat(this.entityType));
				EntityStatsListWidget.this.drawStringWithShadow(matrices, StatsScreen.this.textRenderer, string, x + 2, y + 1, 16777215);
				EntityStatsListWidget.this.drawStringWithShadow(
					matrices, StatsScreen.this.textRenderer, this.getKilledString(string, i), x + 2 + 10, y + 1 + 9, i == 0 ? 6316128 : 9474192
				);
				EntityStatsListWidget.this.drawStringWithShadow(
					matrices, StatsScreen.this.textRenderer, this.getKilledByString(string, j), x + 2 + 10, y + 1 + 9 * 2, j == 0 ? 6316128 : 9474192
				);
			}

			private String getKilledString(String entityName, int killCount) {
				String string = Stats.KILLED.getTranslationKey();
				return killCount == 0 ? I18n.translate(string + ".none", entityName) : I18n.translate(string, killCount, entityName);
			}

			private String getKilledByString(String entityName, int killCount) {
				String string = Stats.KILLED_BY.getTranslationKey();
				return killCount == 0 ? I18n.translate(string + ".none", entityName) : I18n.translate(string, entityName, killCount);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class GeneralStatsListWidget extends AlwaysSelectedEntryListWidget<StatsScreen.GeneralStatsListWidget.Entry> {
		public GeneralStatsListWidget(MinecraftClient minecraftClient) {
			super(minecraftClient, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 10);
			ObjectArrayList<Stat<Identifier>> objectArrayList = new ObjectArrayList<>(Stats.CUSTOM.iterator());
			objectArrayList.sort(Comparator.comparing(statx -> I18n.translate(StatsScreen.method_27027(statx))));

			for (Stat<Identifier> stat : objectArrayList) {
				this.addEntry(new StatsScreen.GeneralStatsListWidget.Entry(stat));
			}
		}

		@Override
		protected void renderBackground(MatrixStack matrixStack) {
			StatsScreen.this.renderBackground(matrixStack);
		}

		@Environment(EnvType.CLIENT)
		class Entry extends AlwaysSelectedEntryListWidget.Entry<StatsScreen.GeneralStatsListWidget.Entry> {
			private final Stat<Identifier> stat;

			private Entry(Stat<Identifier> stat) {
				this.stat = stat;
			}

			@Override
			public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				Text text = new TranslatableText(StatsScreen.method_27027(this.stat)).formatted(Formatting.GRAY);
				GeneralStatsListWidget.this.drawStringWithShadow(
					matrices, StatsScreen.this.textRenderer, text.getString(), x + 2, y + 1, index % 2 == 0 ? 16777215 : 9474192
				);
				String string = this.stat.format(StatsScreen.this.statHandler.getStat(this.stat));
				GeneralStatsListWidget.this.drawStringWithShadow(
					matrices, StatsScreen.this.textRenderer, string, x + 2 + 213 - StatsScreen.this.textRenderer.getWidth(string), y + 1, index % 2 == 0 ? 16777215 : 9474192
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

		public ItemStatsListWidget(MinecraftClient client) {
			super(client, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 20);
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
		protected void renderHeader(MatrixStack matrixStack, int y, int i, Tessellator tessellator) {
			if (!this.client.mouse.wasLeftButtonClicked()) {
				this.selectedHeaderColumn = -1;
			}

			for (int j = 0; j < this.HEADER_ICON_SPRITE_INDICES.length; j++) {
				StatsScreen.this.renderIcon(matrixStack, y + StatsScreen.this.getColumnX(j) - 18, i + 1, 0, this.selectedHeaderColumn == j ? 0 : 18);
			}

			if (this.selectedStatType != null) {
				int j = StatsScreen.this.getColumnX(this.getHeaderIndex(this.selectedStatType)) - 36;
				int k = this.field_18760 == 1 ? 2 : 1;
				StatsScreen.this.renderIcon(matrixStack, y + j, i + 1, 18 * k, 0);
			}

			for (int j = 0; j < this.HEADER_ICON_SPRITE_INDICES.length; j++) {
				int k = this.selectedHeaderColumn == j ? 1 : 0;
				StatsScreen.this.renderIcon(matrixStack, y + StatsScreen.this.getColumnX(j) - 18 + k, i + 1 + k, 18 * this.HEADER_ICON_SPRITE_INDICES[j], 18);
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
		protected void renderBackground(MatrixStack matrixStack) {
			StatsScreen.this.renderBackground(matrixStack);
		}

		@Override
		protected void clickedHeader(int x, int y) {
			this.selectedHeaderColumn = -1;

			for (int i = 0; i < this.HEADER_ICON_SPRITE_INDICES.length; i++) {
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
		protected void renderDecorations(MatrixStack matrixStack, int i, int j) {
			if (j >= this.top && j <= this.bottom) {
				StatsScreen.ItemStatsListWidget.Entry entry = this.getEntryAtPosition((double)i, (double)j);
				int k = (this.width - this.getRowWidth()) / 2;
				if (entry != null) {
					if (i < k + 40 || i > k + 40 + 20) {
						return;
					}

					Item item = (Item)this.items.get(this.children().indexOf(entry));
					this.render(matrixStack, this.getText(item), i, j);
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

					this.render(matrixStack, text, i, j);
				}
			}
		}

		protected void render(MatrixStack matrixStack, @Nullable Text text, int i, int j) {
			if (text != null) {
				int k = i + 12;
				int l = j - 12;
				int m = StatsScreen.this.textRenderer.getWidth(text);
				this.fillGradient(matrixStack, k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
				RenderSystem.pushMatrix();
				RenderSystem.translatef(0.0F, 0.0F, 400.0F);
				StatsScreen.this.textRenderer.drawWithShadow(matrixStack, text, (float)k, (float)l, -1);
				RenderSystem.popMatrix();
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
			public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				Item item = (Item)StatsScreen.this.itemStats.items.get(index);
				StatsScreen.this.renderStatItem(matrices, x + 40, y, item);

				for (int i = 0; i < StatsScreen.this.itemStats.blockStatTypes.size(); i++) {
					Stat<Block> stat;
					if (item instanceof BlockItem) {
						stat = ((StatType)StatsScreen.this.itemStats.blockStatTypes.get(i)).getOrCreateStat(((BlockItem)item).getBlock());
					} else {
						stat = null;
					}

					this.render(matrices, stat, x + StatsScreen.this.getColumnX(i), y, index % 2 == 0);
				}

				for (int i = 0; i < StatsScreen.this.itemStats.itemStatTypes.size(); i++) {
					this.render(
						matrices,
						((StatType)StatsScreen.this.itemStats.itemStatTypes.get(i)).getOrCreateStat(item),
						x + StatsScreen.this.getColumnX(i + StatsScreen.this.itemStats.blockStatTypes.size()),
						y,
						index % 2 == 0
					);
				}
			}

			protected void render(MatrixStack matrixStack, @Nullable Stat<?> stat, int i, int j, boolean bl) {
				String string = stat == null ? "-" : stat.format(StatsScreen.this.statHandler.getStat(stat));
				ItemStatsListWidget.this.drawStringWithShadow(
					matrixStack, StatsScreen.this.textRenderer, string, i - StatsScreen.this.textRenderer.getWidth(string), j + 5, bl ? 16777215 : 9474192
				);
			}
		}

		@Environment(EnvType.CLIENT)
		class ItemComparator implements Comparator<Item> {
			private ItemComparator() {
			}

			public int compare(Item item, Item item2) {
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
