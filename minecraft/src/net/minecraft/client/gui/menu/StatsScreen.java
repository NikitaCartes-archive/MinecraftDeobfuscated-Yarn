package net.minecraft.client.gui.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_452;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.InputListener;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.AbstractListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.block.BlockItem;
import net.minecraft.server.network.packet.ClientStatusServerPacket;
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
	private StatsScreen.class_448 field_2644;
	private StatsScreen.class_449 field_2642;
	private StatsScreen.class_451 field_2646;
	private final StatHandler field_2647;
	private AbstractListWidget field_2643;
	private boolean field_2645 = true;

	public StatsScreen(Screen screen, StatHandler statHandler) {
		this.field_2648 = screen;
		this.field_2647 = statHandler;
	}

	@Override
	public GuiEventListener getFocused() {
		return this.field_2643;
	}

	@Override
	protected void onInitialized() {
		this.field_2649 = I18n.translate("gui.stats");
		this.field_2645 = true;
		this.client.getNetworkHandler().sendPacket(new ClientStatusServerPacket(ClientStatusServerPacket.Mode.field_12775));
	}

	public void method_2270() {
		this.field_2644 = new StatsScreen.class_448(this.client);
		this.field_2642 = new StatsScreen.class_449(this.client);
		this.field_2646 = new StatsScreen.class_451(this.client);
	}

	public void method_2267() {
		this.addButton(new ButtonWidget(0, this.width / 2 - 100, this.height - 28, I18n.translate("gui.done")) {
			@Override
			public void onPressed(double d, double e) {
				StatsScreen.this.client.openScreen(StatsScreen.this.field_2648);
			}
		});
		this.addButton(new ButtonWidget(1, this.width / 2 - 120, this.height - 52, 80, 20, I18n.translate("stat.generalButton")) {
			@Override
			public void onPressed(double d, double e) {
				StatsScreen.this.field_2643 = StatsScreen.this.field_2644;
			}
		});
		ButtonWidget buttonWidget = this.addButton(new ButtonWidget(3, this.width / 2 - 40, this.height - 52, 80, 20, I18n.translate("stat.itemsButton")) {
			@Override
			public void onPressed(double d, double e) {
				StatsScreen.this.field_2643 = StatsScreen.this.field_2642;
			}
		});
		ButtonWidget buttonWidget2 = this.addButton(new ButtonWidget(4, this.width / 2 + 40, this.height - 52, 80, 20, I18n.translate("stat.mobsButton")) {
			@Override
			public void onPressed(double d, double e) {
				StatsScreen.this.field_2643 = StatsScreen.this.field_2646;
			}
		});
		if (this.field_2642.getEntryCount() == 0) {
			buttonWidget.enabled = false;
		}

		if (this.field_2646.getEntryCount() == 0) {
			buttonWidget2.enabled = false;
		}

		this.listeners.add((InputListener)() -> this.field_2643);
	}

	@Override
	public void draw(int i, int j, float f) {
		if (this.field_2645) {
			this.drawBackground();
			this.drawStringCentered(this.fontRenderer, I18n.translate("multiplayer.downloadingStats"), this.width / 2, this.height / 2, 16777215);
			this.drawStringCentered(
				this.fontRenderer,
				SPOOKY_PROGRESS_STAGES[(int)(SystemUtil.getMeasuringTimeMs() / 150L % (long)SPOOKY_PROGRESS_STAGES.length)],
				this.width / 2,
				this.height / 2 + 9 * 2,
				16777215
			);
		} else {
			this.field_2643.draw(i, j, f);
			this.drawStringCentered(this.fontRenderer, this.field_2649, this.width / 2, 20, 16777215);
			super.draw(i, j, f);
		}
	}

	@Override
	public void method_2300() {
		if (this.field_2645) {
			this.method_2270();
			this.method_2267();
			this.field_2643 = this.field_2644;
			this.field_2645 = false;
		}
	}

	@Override
	public boolean isPauseScreen() {
		return !this.field_2645;
	}

	private int method_2285(int i) {
		return 115 + 40 * i;
	}

	private void method_2289(int i, int j, Item item) {
		this.method_2272(i + 1, j + 1);
		GlStateManager.enableRescaleNormal();
		GuiLighting.enableForItems();
		this.itemRenderer.renderGuiItemIcon(item.getDefaultStack(), i + 2, j + 2);
		GuiLighting.disable();
		GlStateManager.disableRescaleNormal();
	}

	private void method_2272(int i, int j) {
		this.method_2282(i, j, 0, 0);
	}

	private void method_2282(int i, int j, int k, int l) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(STAT_ICONS);
		float f = 0.0078125F;
		float g = 0.0078125F;
		int m = 18;
		int n = 18;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.begin(7, VertexFormats.POSITION_UV);
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
	class class_448 extends AbstractListWidget {
		private Iterator<Stat<Identifier>> field_2650;

		public class_448(MinecraftClient minecraftClient) {
			super(minecraftClient, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 10);
			this.method_1943(false);
		}

		@Override
		protected int getEntryCount() {
			return Stats.field_15419.getStatCount();
		}

		@Override
		protected boolean isSelectedEntry(int i) {
			return false;
		}

		@Override
		protected int getMaxScrollPosition() {
			return this.getEntryCount() * 10;
		}

		@Override
		protected void drawBackground() {
			StatsScreen.this.drawBackground();
		}

		@Override
		protected void drawEntry(int i, int j, int k, int l, int m, int n, float f) {
			if (i == 0) {
				this.field_2650 = Stats.field_15419.iterator();
			}

			Stat<Identifier> stat = (Stat<Identifier>)this.field_2650.next();
			TextComponent textComponent = new TranslatableTextComponent("stat." + stat.getValue().toString().replace(':', '.')).applyFormat(TextFormat.GRAY);
			this.drawString(StatsScreen.this.fontRenderer, textComponent.getString(), j + 2, k + 1, i % 2 == 0 ? 16777215 : 9474192);
			String string = stat.format(StatsScreen.this.field_2647.getStat(stat));
			this.drawString(
				StatsScreen.this.fontRenderer, string, j + 2 + 213 - StatsScreen.this.fontRenderer.getStringWidth(string), k + 1, i % 2 == 0 ? 16777215 : 9474192
			);
		}
	}

	@Environment(EnvType.CLIENT)
	class class_449 extends AbstractListWidget {
		protected final List<StatType<Block>> field_2656;
		protected final List<StatType<Item>> field_2655;
		private final int[] field_2654 = new int[]{3, 4, 1, 2, 5, 6};
		protected int field_2653 = -1;
		protected final List<Item> field_2658;
		protected final Comparator<Item> field_2661 = new StatsScreen.class_449.class_450();
		@Nullable
		protected StatType<?> field_2659;
		protected int field_2657;

		public class_449(MinecraftClient minecraftClient) {
			super(minecraftClient, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 20);
			this.field_2656 = Lists.<StatType<Block>>newArrayList();
			this.field_2656.add(Stats.field_15427);
			this.field_2655 = Lists.<StatType<Item>>newArrayList(Stats.field_15383, Stats.field_15370, Stats.field_15372, Stats.field_15392, Stats.field_15405);
			this.method_1943(false);
			this.method_1927(true, 20);
			Set<Item> set = Sets.newIdentityHashSet();

			for (Item item : Registry.ITEM) {
				boolean bl = false;

				for (StatType<Item> statType : this.field_2655) {
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

				for (StatType<Block> statTypex : this.field_2656) {
					if (statTypex.hasStat(block) && StatsScreen.this.field_2647.getStat(statTypex.getOrCreateStat(block)) > 0) {
						bl = true;
					}
				}

				if (bl) {
					set.add(block.getItem());
				}
			}

			set.remove(Items.AIR);
			this.field_2658 = Lists.<Item>newArrayList(set);
		}

		@Override
		protected void method_1940(int i, int j, Tessellator tessellator) {
			if (!this.client.mouse.method_1608()) {
				this.field_2653 = -1;
			}

			for (int k = 0; k < this.field_2654.length; k++) {
				StatsScreen.this.method_2282(i + StatsScreen.this.method_2285(k) - 18, j + 1, 0, this.field_2653 == k ? 0 : 18);
			}

			if (this.field_2659 != null) {
				int k = StatsScreen.this.method_2285(this.method_2294(this.field_2659)) - 36;
				int l = this.field_2657 == 1 ? 2 : 1;
				StatsScreen.this.method_2282(i + k, j + 1, 18 * l, 0);
			}

			for (int k = 0; k < this.field_2654.length; k++) {
				int l = this.field_2653 == k ? 1 : 0;
				StatsScreen.this.method_2282(i + StatsScreen.this.method_2285(k) - 18 + l, j + 1 + l, 18 * this.field_2654[k], 18);
			}
		}

		@Override
		protected void drawEntry(int i, int j, int k, int l, int m, int n, float f) {
			Item item = this.method_2296(i);
			StatsScreen.this.method_2289(j + 40, k, item);

			for (int o = 0; o < this.field_2656.size(); o++) {
				Stat<Block> stat;
				if (item instanceof BlockItem) {
					stat = ((StatType)this.field_2656.get(o)).getOrCreateStat(((BlockItem)item).getBlock());
				} else {
					stat = null;
				}

				this.method_2292(stat, j + StatsScreen.this.method_2285(o), k, i % 2 == 0);
			}

			for (int o = 0; o < this.field_2655.size(); o++) {
				this.method_2292(((StatType)this.field_2655.get(o)).getOrCreateStat(item), j + StatsScreen.this.method_2285(o + this.field_2656.size()), k, i % 2 == 0);
			}
		}

		@Override
		protected boolean isSelectedEntry(int i) {
			return false;
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
			this.field_2653 = -1;

			for (int k = 0; k < this.field_2654.length; k++) {
				int l = i - StatsScreen.this.method_2285(k);
				if (l >= -36 && l <= 0) {
					this.field_2653 = k;
					break;
				}
			}

			if (this.field_2653 >= 0) {
				this.method_2295(this.method_2290(this.field_2653));
				this.client.getSoundLoader().play(PositionedSoundInstance.master(SoundEvents.field_15015, 1.0F));
			}
		}

		private StatType<?> method_2290(int i) {
			return i < this.field_2656.size() ? (StatType)this.field_2656.get(i) : (StatType)this.field_2655.get(i - this.field_2656.size());
		}

		private int method_2294(StatType<?> statType) {
			int i = this.field_2656.indexOf(statType);
			if (i >= 0) {
				return i;
			} else {
				int j = this.field_2655.indexOf(statType);
				return j >= 0 ? j + this.field_2656.size() : -1;
			}
		}

		@Override
		protected final int getEntryCount() {
			return this.field_2658.size();
		}

		protected final Item method_2296(int i) {
			return (Item)this.field_2658.get(i);
		}

		protected void method_2292(@Nullable Stat<?> stat, int i, int j, boolean bl) {
			String string = stat == null ? "-" : stat.format(StatsScreen.this.field_2647.getStat(stat));
			this.drawString(StatsScreen.this.fontRenderer, string, i - StatsScreen.this.fontRenderer.getStringWidth(string), j + 5, bl ? 16777215 : 9474192);
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

					Item item = this.method_2296(k);
					this.method_2293(this.method_2291(item), i, j);
				} else {
					TextComponent textComponent = null;
					int m = i - l;

					for (int n = 0; n < this.field_2654.length; n++) {
						int o = StatsScreen.this.method_2285(n);
						if (m >= o - 18 && m <= o) {
							textComponent = new TranslatableTextComponent(this.method_2290(n).getTranslationKey());
							break;
						}
					}

					this.method_2293(textComponent, i, j);
				}
			}
		}

		protected void method_2293(@Nullable TextComponent textComponent, int i, int j) {
			if (textComponent != null) {
				String string = textComponent.getFormattedText();
				int k = i + 12;
				int l = j - 12;
				int m = StatsScreen.this.fontRenderer.getStringWidth(string);
				this.drawGradientRect(k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
				StatsScreen.this.fontRenderer.drawWithShadow(string, (float)k, (float)l, -1);
			}
		}

		protected TextComponent method_2291(Item item) {
			return item.getTextComponent();
		}

		protected void method_2295(StatType<?> statType) {
			if (statType != this.field_2659) {
				this.field_2659 = statType;
				this.field_2657 = -1;
			} else if (this.field_2657 == -1) {
				this.field_2657 = 1;
			} else {
				this.field_2659 = null;
				this.field_2657 = 0;
			}

			this.field_2658.sort(this.field_2661);
		}

		@Environment(EnvType.CLIENT)
		class class_450 implements Comparator<Item> {
			private class_450() {
			}

			public int method_2297(Item item, Item item2) {
				int i;
				int j;
				if (class_449.this.field_2659 == null) {
					i = 0;
					j = 0;
				} else if (class_449.this.field_2656.contains(class_449.this.field_2659)) {
					StatType<Block> statType = (StatType<Block>)class_449.this.field_2659;
					i = item instanceof BlockItem ? StatsScreen.this.field_2647.method_15024(statType, ((BlockItem)item).getBlock()) : -1;
					j = item2 instanceof BlockItem ? StatsScreen.this.field_2647.method_15024(statType, ((BlockItem)item2).getBlock()) : -1;
				} else {
					StatType<Item> statType = (StatType<Item>)class_449.this.field_2659;
					i = StatsScreen.this.field_2647.method_15024(statType, item);
					j = StatsScreen.this.field_2647.method_15024(statType, item2);
				}

				return i == j
					? class_449.this.field_2657 * Integer.compare(Item.getRawIdByItem(item), Item.getRawIdByItem(item2))
					: class_449.this.field_2657 * Integer.compare(i, j);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class class_451 extends AbstractListWidget {
		private final List<EntityType<?>> field_2664 = Lists.<EntityType<?>>newArrayList();

		public class_451(MinecraftClient minecraftClient) {
			super(minecraftClient, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 9 * 4);
			this.method_1943(false);

			for (EntityType<?> entityType : Registry.ENTITY_TYPE) {
				if (StatsScreen.this.field_2647.getStat(Stats.field_15403.getOrCreateStat(entityType)) > 0
					|| StatsScreen.this.field_2647.getStat(Stats.field_15411.getOrCreateStat(entityType)) > 0) {
					this.field_2664.add(entityType);
				}
			}
		}

		@Override
		protected int getEntryCount() {
			return this.field_2664.size();
		}

		@Override
		protected boolean isSelectedEntry(int i) {
			return false;
		}

		@Override
		protected int getMaxScrollPosition() {
			return this.getEntryCount() * 9 * 4;
		}

		@Override
		protected void drawBackground() {
			StatsScreen.this.drawBackground();
		}

		@Override
		protected void drawEntry(int i, int j, int k, int l, int m, int n, float f) {
			EntityType<?> entityType = (EntityType<?>)this.field_2664.get(i);
			String string = I18n.translate(SystemUtil.createTranslationKey("entity", EntityType.getId(entityType)));
			int o = StatsScreen.this.field_2647.getStat(Stats.field_15403.getOrCreateStat(entityType));
			int p = StatsScreen.this.field_2647.getStat(Stats.field_15411.getOrCreateStat(entityType));
			this.drawString(StatsScreen.this.fontRenderer, string, j + 2 - 10, k + 1, 16777215);
			this.drawString(StatsScreen.this.fontRenderer, this.method_2299(string, o), j + 2, k + 1 + 9, o == 0 ? 6316128 : 9474192);
			this.drawString(StatsScreen.this.fontRenderer, this.method_2298(string, p), j + 2, k + 1 + 9 * 2, p == 0 ? 6316128 : 9474192);
		}

		private String method_2299(String string, int i) {
			String string2 = Stats.field_15403.getTranslationKey();
			return i == 0 ? I18n.translate(string2 + ".none", string) : I18n.translate(string2, i, string);
		}

		private String method_2298(String string, int i) {
			String string2 = Stats.field_15411.getTranslationKey();
			return i == 0 ? I18n.translate(string2 + ".none", string) : I18n.translate(string2, string, i);
		}
	}
}
