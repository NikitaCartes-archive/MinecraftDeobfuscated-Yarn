package net.minecraft.client.gui.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.class_452;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.AbstractListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.block.BlockItem;
import net.minecraft.server.network.packet.ClientStatusC2SPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class StatsScreen extends Screen implements class_452 {
	protected final Screen field_2648;
	protected String field_2649 = "Select world";
	private StatsScreen.class_4198 field_2644;
	private StatsScreen.class_4200 field_2642;
	private StatsScreen.class_4202 field_2646;
	private final StatHandler field_2647;
	@Nullable
	private AbstractListWidget field_2643;
	private boolean field_2645 = true;

	public StatsScreen(Screen screen, StatHandler statHandler) {
		this.field_2648 = screen;
		this.field_2647 = statHandler;
	}

	@Override
	protected void onInitialized() {
		this.field_2649 = I18n.translate("gui.stats");
		this.field_2645 = true;
		this.client.method_1562().method_2883(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.field_12775));
	}

	public void method_2270() {
		this.field_2644 = new StatsScreen.class_4198(this.client);
		this.field_2642 = new StatsScreen.class_4200(this.client);
		this.field_2646 = new StatsScreen.class_4202(this.client);
	}

	public void method_2267() {
		this.addButton(new class_4185(this.screenWidth / 2 - 100, this.screenHeight - 28, I18n.translate("gui.done")) {
			@Override
			public void method_1826() {
				StatsScreen.this.client.method_1507(StatsScreen.this.field_2648);
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 - 120, this.screenHeight - 52, 80, 20, I18n.translate("stat.generalButton")) {
			@Override
			public void method_1826() {
				StatsScreen.this.method_19390(StatsScreen.this.field_2644);
			}
		});
		class_4185 lv = this.addButton(new class_4185(this.screenWidth / 2 - 40, this.screenHeight - 52, 80, 20, I18n.translate("stat.itemsButton")) {
			@Override
			public void method_1826() {
				StatsScreen.this.method_19390(StatsScreen.this.field_2642);
			}
		});
		class_4185 lv2 = this.addButton(new class_4185(this.screenWidth / 2 + 40, this.screenHeight - 52, 80, 20, I18n.translate("stat.mobsButton")) {
			@Override
			public void method_1826() {
				StatsScreen.this.method_19390(StatsScreen.this.field_2646);
			}
		});
		if (this.field_2642.getInputListeners().isEmpty()) {
			lv.enabled = false;
		}

		if (this.field_2646.getInputListeners().isEmpty()) {
			lv2.enabled = false;
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		if (this.field_2645) {
			this.drawBackground();
			this.drawStringCentered(this.fontRenderer, I18n.translate("multiplayer.downloadingStats"), this.screenWidth / 2, this.screenHeight / 2, 16777215);
			this.drawStringCentered(
				this.fontRenderer,
				field_2668[(int)(SystemUtil.getMeasuringTimeMs() / 150L % (long)field_2668.length)],
				this.screenWidth / 2,
				this.screenHeight / 2 + 9 * 2,
				16777215
			);
		} else {
			this.method_19399().draw(i, j, f);
			this.drawStringCentered(this.fontRenderer, this.field_2649, this.screenWidth / 2, 20, 16777215);
			super.draw(i, j, f);
		}
	}

	@Override
	public void method_2300() {
		if (this.field_2645) {
			this.method_2270();
			this.method_2267();
			this.method_19390(this.field_2644);
			this.field_2645 = false;
		}
	}

	@Override
	public boolean isPauseScreen() {
		return !this.field_2645;
	}

	@Nullable
	public AbstractListWidget method_19399() {
		return this.field_2643;
	}

	public void method_19390(@Nullable AbstractListWidget abstractListWidget) {
		this.listeners.remove(this.field_2644);
		this.listeners.remove(this.field_2642);
		this.listeners.remove(this.field_2646);
		if (abstractListWidget != null) {
			this.listeners.add(abstractListWidget);
			this.field_2643 = abstractListWidget;
		}
	}

	private int method_2285(int i) {
		return 115 + 40 * i;
	}

	private void method_2289(int i, int j, Item item) {
		this.method_2272(i + 1, j + 1);
		GlStateManager.enableRescaleNormal();
		GuiLighting.enableForItems();
		this.field_2560.renderGuiItemIcon(item.method_7854(), i + 2, j + 2);
		GuiLighting.disable();
		GlStateManager.disableRescaleNormal();
	}

	private void method_2272(int i, int j) {
		this.method_2282(i, j, 0, 0);
	}

	private void method_2282(int i, int j, int k, int l) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.method_1531().method_4618(field_2052);
		float f = 0.0078125F;
		float g = 0.0078125F;
		int m = 18;
		int n = 18;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.method_1328(7, VertexFormats.field_1585);
		bufferBuilder.vertex((double)(i + 0), (double)(j + 18), (double)this.zOffset)
			.texture((double)((float)(k + 0) * 0.0078125F), (double)((float)(l + 18) * 0.0078125F))
			.next();
		bufferBuilder.vertex((double)(i + 18), (double)(j + 18), (double)this.zOffset)
			.texture((double)((float)(k + 18) * 0.0078125F), (double)((float)(l + 18) * 0.0078125F))
			.next();
		bufferBuilder.vertex((double)(i + 18), (double)(j + 0), (double)this.zOffset)
			.texture((double)((float)(k + 18) * 0.0078125F), (double)((float)(l + 0) * 0.0078125F))
			.next();
		bufferBuilder.vertex((double)(i + 0), (double)(j + 0), (double)this.zOffset)
			.texture((double)((float)(k + 0) * 0.0078125F), (double)((float)(l + 0) * 0.0078125F))
			.next();
		tessellator.draw();
	}

	@Environment(EnvType.CLIENT)
	class class_4197 extends EntryListWidget.Entry<StatsScreen.class_4197> {
		private final Stat<Identifier> field_18749;

		private class_4197(Stat<Identifier> stat) {
			this.field_18749 = stat;
		}

		@Override
		public void draw(int i, int j, int k, int l, boolean bl, float f) {
			TextComponent textComponent = new TranslatableTextComponent("stat." + this.field_18749.getValue().toString().replace(':', '.'))
				.applyFormat(TextFormat.field_1080);
			int m = this.getX();
			int n = this.getY();
			StatsScreen.this.drawString(StatsScreen.this.fontRenderer, textComponent.getString(), m + 2, n + 1, this.field_2143 % 2 == 0 ? 16777215 : 9474192);
			String string = this.field_18749.format(StatsScreen.this.field_2647.getStat(this.field_18749));
			StatsScreen.this.drawString(
				StatsScreen.this.fontRenderer,
				string,
				m + 2 + 213 - StatsScreen.this.fontRenderer.getStringWidth(string),
				n + 1,
				this.field_2143 % 2 == 0 ? 16777215 : 9474192
			);
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4198 extends EntryListWidget<StatsScreen.class_4197> {
		public class_4198(MinecraftClient minecraftClient) {
			super(minecraftClient, StatsScreen.this.screenWidth, StatsScreen.this.screenHeight, 32, StatsScreen.this.screenHeight - 64, 10);
			this.method_1943(false);

			for (Stat<Identifier> stat : Stats.field_15419) {
				this.addEntry(StatsScreen.this.new class_4197(stat));
			}
		}

		@Override
		protected void drawBackground() {
			StatsScreen.this.drawBackground();
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4199 extends EntryListWidget.Entry<StatsScreen.class_4199> {
		private class_4199() {
		}

		@Override
		public void draw(int i, int j, int k, int l, boolean bl, float f) {
			int m = this.getX();
			int n = this.getY();
			Item item = (Item)StatsScreen.this.field_2642.field_18757.get(this.field_2143);
			StatsScreen.this.method_2289(m + 40, n, item);

			for (int o = 0; o < StatsScreen.this.field_2642.field_18754.size(); o++) {
				Stat<Block> stat;
				if (item instanceof BlockItem) {
					stat = ((StatType)StatsScreen.this.field_2642.field_18754.get(o)).getOrCreateStat(((BlockItem)item).method_7711());
				} else {
					stat = null;
				}

				this.method_19405(stat, m + StatsScreen.this.method_2285(o), n, this.field_2143 % 2 == 0);
			}

			for (int o = 0; o < StatsScreen.this.field_2642.field_18755.size(); o++) {
				this.method_19405(
					((StatType)StatsScreen.this.field_2642.field_18755.get(o)).getOrCreateStat(item),
					m + StatsScreen.this.method_2285(o + StatsScreen.this.field_2642.field_18754.size()),
					n,
					this.field_2143 % 2 == 0
				);
			}
		}

		protected void method_19405(@Nullable Stat<?> stat, int i, int j, boolean bl) {
			String string = stat == null ? "-" : stat.format(StatsScreen.this.field_2647.getStat(stat));
			StatsScreen.this.drawString(StatsScreen.this.fontRenderer, string, i - StatsScreen.this.fontRenderer.getStringWidth(string), j + 5, bl ? 16777215 : 9474192);
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4200 extends EntryListWidget<StatsScreen.class_4199> {
		protected final List<StatType<Block>> field_18754;
		protected final List<StatType<Item>> field_18755;
		private final int[] field_18753 = new int[]{3, 4, 1, 2, 5, 6};
		protected int field_18756 = -1;
		protected final List<Item> field_18757;
		protected final Comparator<Item> field_18758 = new StatsScreen.class_4200.class_450();
		@Nullable
		protected StatType<?> field_18759;
		protected int field_18760;

		public class_4200(MinecraftClient minecraftClient) {
			super(minecraftClient, StatsScreen.this.screenWidth, StatsScreen.this.screenHeight, 32, StatsScreen.this.screenHeight - 64, 20);
			this.field_18754 = Lists.<StatType<Block>>newArrayList();
			this.field_18754.add(Stats.field_15427);
			this.field_18755 = Lists.<StatType<Item>>newArrayList(Stats.field_15383, Stats.field_15370, Stats.field_15372, Stats.field_15392, Stats.field_15405);
			this.method_1943(false);
			this.method_1927(true, 20);
			Set<Item> set = Sets.newIdentityHashSet();

			for (Item item : Registry.ITEM) {
				boolean bl = false;

				for (StatType<Item> statType : this.field_18755) {
					if (statType.hasStat(item) && StatsScreen.this.field_2647.getStat(statType.getOrCreateStat(item)) > 0) {
						bl = true;
					}
				}

				if (bl) {
					set.add(item);
				}
			}

			for (Block block : Registry.BLOCK) {
				boolean bl = false;

				for (StatType<Block> statTypex : this.field_18754) {
					if (statTypex.hasStat(block) && StatsScreen.this.field_2647.getStat(statTypex.getOrCreateStat(block)) > 0) {
						bl = true;
					}
				}

				if (bl) {
					set.add(block.getItem());
				}
			}

			set.remove(Items.AIR);
			this.field_18757 = Lists.<Item>newArrayList(set);

			for (int i = 0; i < this.field_18757.size(); i++) {
				this.addEntry(StatsScreen.this.new class_4199());
			}
		}

		@Override
		protected void method_1940(int i, int j, Tessellator tessellator) {
			if (!this.client.field_1729.method_1608()) {
				this.field_18756 = -1;
			}

			for (int k = 0; k < this.field_18753.length; k++) {
				StatsScreen.this.method_2282(i + StatsScreen.this.method_2285(k) - 18, j + 1, 0, this.field_18756 == k ? 0 : 18);
			}

			if (this.field_18759 != null) {
				int k = StatsScreen.this.method_2285(this.method_19409(this.field_18759)) - 36;
				int l = this.field_18760 == 1 ? 2 : 1;
				StatsScreen.this.method_2282(i + k, j + 1, 18 * l, 0);
			}

			for (int k = 0; k < this.field_18753.length; k++) {
				int l = this.field_18756 == k ? 1 : 0;
				StatsScreen.this.method_2282(i + StatsScreen.this.method_2285(k) - 18 + l, j + 1 + l, 18 * this.field_18753[k], 18);
			}
		}

		@Override
		public int getEntryWidth() {
			return 375;
		}

		@Override
		protected int getScrollbarPosition() {
			return this.width / 2 + 140;
		}

		@Override
		protected void drawBackground() {
			StatsScreen.this.drawBackground();
		}

		@Override
		protected void method_1929(int i, int j) {
			this.field_18756 = -1;

			for (int k = 0; k < this.field_18753.length; k++) {
				int l = i - StatsScreen.this.method_2285(k);
				if (l >= -36 && l <= 0) {
					this.field_18756 = k;
					break;
				}
			}

			if (this.field_18756 >= 0) {
				this.method_19408(this.method_19410(this.field_18756));
				this.client.method_1483().play(PositionedSoundInstance.method_4758(SoundEvents.field_15015, 1.0F));
			}
		}

		private StatType<?> method_19410(int i) {
			return i < this.field_18754.size() ? (StatType)this.field_18754.get(i) : (StatType)this.field_18755.get(i - this.field_18754.size());
		}

		private int method_19409(StatType<?> statType) {
			int i = this.field_18754.indexOf(statType);
			if (i >= 0) {
				return i;
			} else {
				int j = this.field_18755.indexOf(statType);
				return j >= 0 ? j + this.field_18754.size() : -1;
			}
		}

		@Override
		protected void method_1942(int i, int j) {
			if (j >= this.y1 && j <= this.y2) {
				int k = this.getSelectedEntry((double)i, (double)j);
				int l = (this.width - this.getEntryWidth()) / 2;
				if (k >= 0) {
					if (i < l + 40 || i > l + 40 + 20) {
						return;
					}

					Item item = (Item)this.field_18757.get(k);
					this.method_19407(this.method_19406(item), i, j);
				} else {
					TextComponent textComponent = null;
					int m = i - l;

					for (int n = 0; n < this.field_18753.length; n++) {
						int o = StatsScreen.this.method_2285(n);
						if (m >= o - 18 && m <= o) {
							textComponent = new TranslatableTextComponent(this.method_19410(n).getTranslationKey());
							break;
						}
					}

					this.method_19407(textComponent, i, j);
				}
			}
		}

		protected void method_19407(@Nullable TextComponent textComponent, int i, int j) {
			if (textComponent != null) {
				String string = textComponent.getFormattedText();
				int k = i + 12;
				int l = j - 12;
				int m = StatsScreen.this.fontRenderer.getStringWidth(string);
				this.drawGradientRect(k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
				StatsScreen.this.fontRenderer.drawWithShadow(string, (float)k, (float)l, -1);
			}
		}

		protected TextComponent method_19406(Item item) {
			return item.method_7848();
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

		@Environment(EnvType.CLIENT)
		class class_450 implements Comparator<Item> {
			private class_450() {
			}

			public int method_2297(Item item, Item item2) {
				int i;
				int j;
				if (class_4200.this.field_18759 == null) {
					i = 0;
					j = 0;
				} else if (class_4200.this.field_18754.contains(class_4200.this.field_18759)) {
					StatType<Block> statType = (StatType<Block>)class_4200.this.field_18759;
					i = item instanceof BlockItem ? StatsScreen.this.field_2647.method_15024(statType, ((BlockItem)item).method_7711()) : -1;
					j = item2 instanceof BlockItem ? StatsScreen.this.field_2647.method_15024(statType, ((BlockItem)item2).method_7711()) : -1;
				} else {
					StatType<Item> statType = (StatType<Item>)class_4200.this.field_18759;
					i = StatsScreen.this.field_2647.method_15024(statType, item);
					j = StatsScreen.this.field_2647.method_15024(statType, item2);
				}

				return i == j
					? class_4200.this.field_18760 * Integer.compare(Item.getRawIdByItem(item), Item.getRawIdByItem(item2))
					: class_4200.this.field_18760 * Integer.compare(i, j);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4201 extends EntryListWidget.Entry<StatsScreen.class_4201> {
		private final EntityType<?> field_18762;

		public class_4201(EntityType<?> entityType) {
			this.field_18762 = entityType;
		}

		@Override
		public void draw(int i, int j, int k, int l, boolean bl, float f) {
			String string = I18n.translate(SystemUtil.method_646("entity", EntityType.method_5890(this.field_18762)));
			int m = StatsScreen.this.field_2647.getStat(Stats.field_15403.getOrCreateStat(this.field_18762));
			int n = StatsScreen.this.field_2647.getStat(Stats.field_15411.getOrCreateStat(this.field_18762));
			int o = this.getX();
			int p = this.getY();
			StatsScreen.this.drawString(StatsScreen.this.fontRenderer, string, o + 2 - 10, p + 1, 16777215);
			StatsScreen.this.drawString(StatsScreen.this.fontRenderer, this.method_19411(string, m), o + 2, p + 1 + 9, m == 0 ? 6316128 : 9474192);
			StatsScreen.this.drawString(StatsScreen.this.fontRenderer, this.method_19412(string, n), o + 2, p + 1 + 9 * 2, n == 0 ? 6316128 : 9474192);
		}

		private String method_19411(String string, int i) {
			String string2 = Stats.field_15403.getTranslationKey();
			return i == 0 ? I18n.translate(string2 + ".none", string) : I18n.translate(string2, i, string);
		}

		private String method_19412(String string, int i) {
			String string2 = Stats.field_15411.getTranslationKey();
			return i == 0 ? I18n.translate(string2 + ".none", string) : I18n.translate(string2, string, i);
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4202 extends EntryListWidget<StatsScreen.class_4201> {
		public class_4202(MinecraftClient minecraftClient) {
			super(minecraftClient, StatsScreen.this.screenWidth, StatsScreen.this.screenHeight, 32, StatsScreen.this.screenHeight - 64, 9 * 4);
			this.method_1943(false);

			for (EntityType<?> entityType : Registry.ENTITY_TYPE) {
				if (StatsScreen.this.field_2647.getStat(Stats.field_15403.getOrCreateStat(entityType)) > 0
					|| StatsScreen.this.field_2647.getStat(Stats.field_15411.getOrCreateStat(entityType)) > 0) {
					this.addEntry(StatsScreen.this.new class_4201(entityType));
				}
			}
		}

		@Override
		protected void drawBackground() {
			StatsScreen.this.drawBackground();
		}
	}
}
