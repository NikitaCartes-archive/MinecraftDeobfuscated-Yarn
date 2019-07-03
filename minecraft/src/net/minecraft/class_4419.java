package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.datafixers.util.Either;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_4419 extends RealmsScreen {
	private static final Logger field_20069 = LogManager.getLogger();
	private final class_4415<WorldTemplate> field_20070;
	private class_4419.class_4420 field_20071;
	private int field_20072 = -1;
	private String field_20073;
	private RealmsButton field_20074;
	private RealmsButton field_20075;
	private RealmsButton field_20076;
	private String field_20077;
	private String field_20078;
	private final RealmsServer.class_4321 field_20079;
	private int field_20080;
	private String field_20081;
	private String field_20082;
	private boolean field_20083;
	private boolean field_20084;
	private List<class_4450.class_4451> field_20085;

	public class_4419(class_4415<WorldTemplate> arg, RealmsServer.class_4321 arg2) {
		this(arg, arg2, null);
	}

	public class_4419(class_4415<WorldTemplate> arg, RealmsServer.class_4321 arg2, @Nullable WorldTemplatePaginatedList worldTemplatePaginatedList) {
		this.field_20070 = arg;
		this.field_20079 = arg2;
		if (worldTemplatePaginatedList == null) {
			this.field_20071 = new class_4419.class_4420();
			this.method_21415(new WorldTemplatePaginatedList(10));
		} else {
			this.field_20071 = new class_4419.class_4420(new ArrayList(worldTemplatePaginatedList.templates));
			this.method_21415(worldTemplatePaginatedList);
		}

		this.field_20073 = getLocalizedString("mco.template.title");
	}

	public void method_21423(String string) {
		this.field_20073 = string;
	}

	public void method_21429(String string) {
		this.field_20081 = string;
		this.field_20083 = true;
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.field_20084 && this.field_20082 != null) {
			class_4448.method_21570("https://beta.minecraft.net/realms/adventure-maps-in-1-9");
			return true;
		} else {
			return super.mouseClicked(d, e, i);
		}
	}

	@Override
	public void init() {
		this.setKeyboardHandlerSendRepeatsToGui(true);
		this.field_20071 = new class_4419.class_4420(this.field_20071.method_21450());
		this.buttonsAdd(
			this.field_20075 = new RealmsButton(2, this.width() / 2 - 206, this.height() - 32, 100, 20, getLocalizedString("mco.template.button.trailer")) {
				@Override
				public void onPress() {
					class_4419.this.method_21442();
				}
			}
		);
		this.buttonsAdd(
			this.field_20074 = new RealmsButton(1, this.width() / 2 - 100, this.height() - 32, 100, 20, getLocalizedString("mco.template.button.select")) {
				@Override
				public void onPress() {
					class_4419.this.method_21440();
				}
			}
		);
		this.buttonsAdd(
			new RealmsButton(
				0, this.width() / 2 + 6, this.height() - 32, 100, 20, getLocalizedString(this.field_20079 == RealmsServer.class_4321.MINIGAME ? "gui.cancel" : "gui.back")
			) {
				@Override
				public void onPress() {
					class_4419.this.method_21438();
				}
			}
		);
		this.field_20076 = new RealmsButton(3, this.width() / 2 + 112, this.height() - 32, 100, 20, getLocalizedString("mco.template.button.publisher")) {
			@Override
			public void onPress() {
				class_4419.this.method_21444();
			}
		};
		this.buttonsAdd(this.field_20076);
		this.field_20074.active(false);
		this.field_20075.setVisible(false);
		this.field_20076.setVisible(false);
		this.addWidget(this.field_20071);
		this.focusOn(this.field_20071);
		Realms.narrateNow((Iterable<String>)Stream.of(this.field_20073, this.field_20081).filter(Objects::nonNull).collect(Collectors.toList()));
	}

	private void method_21425() {
		this.field_20076.setVisible(this.method_21432());
		this.field_20075.setVisible(this.method_21436());
		this.field_20074.active(this.method_21430());
	}

	private boolean method_21430() {
		return this.field_20072 != -1;
	}

	private boolean method_21432() {
		return this.field_20072 != -1 && !this.method_21434().link.isEmpty();
	}

	private WorldTemplate method_21434() {
		return this.field_20071.method_21447(this.field_20072);
	}

	private boolean method_21436() {
		return this.field_20072 != -1 && !this.method_21434().trailer.isEmpty();
	}

	@Override
	public void tick() {
		super.tick();
		this.field_20080--;
		if (this.field_20080 < 0) {
			this.field_20080 = 0;
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		switch (i) {
			case 256:
				this.method_21438();
				return true;
			default:
				return super.keyPressed(i, j, k);
		}
	}

	private void method_21438() {
		this.field_20070.method_21395(null);
		Realms.setScreen(this.field_20070);
	}

	private void method_21440() {
		if (this.field_20072 >= 0 && this.field_20072 < this.field_20071.getItemCount()) {
			WorldTemplate worldTemplate = this.method_21434();
			this.field_20070.method_21395(worldTemplate);
		}
	}

	private void method_21442() {
		if (this.field_20072 >= 0 && this.field_20072 < this.field_20071.getItemCount()) {
			WorldTemplate worldTemplate = this.method_21434();
			if (!"".equals(worldTemplate.trailer)) {
				class_4448.method_21570(worldTemplate.trailer);
			}
		}
	}

	private void method_21444() {
		if (this.field_20072 >= 0 && this.field_20072 < this.field_20071.getItemCount()) {
			WorldTemplate worldTemplate = this.method_21434();
			if (!"".equals(worldTemplate.link)) {
				class_4448.method_21570(worldTemplate.link);
			}
		}
	}

	private void method_21415(WorldTemplatePaginatedList worldTemplatePaginatedList) {
		(new Thread("realms-template-fetcher") {
				public void run() {
					WorldTemplatePaginatedList worldTemplatePaginatedList = worldTemplatePaginatedList;
					class_4341 lv = class_4341.method_20989();

					while (worldTemplatePaginatedList != null) {
						Either<WorldTemplatePaginatedList, String> either = class_4419.this.method_21416(worldTemplatePaginatedList, lv);
						worldTemplatePaginatedList = (WorldTemplatePaginatedList)Realms.execute(
								(Supplier)(() -> {
									if (either.right().isPresent()) {
										class_4419.field_20069.error("Couldn't fetch templates: {}", either.right().get());
										if (class_4419.this.field_20071.method_21446()) {
											class_4419.this.field_20085 = class_4450.method_21578(RealmsScreen.getLocalizedString("mco.template.select.failure"));
										}

										return null;
									} else {
										assert either.left().isPresent();

										WorldTemplatePaginatedList worldTemplatePaginatedListxxx = (WorldTemplatePaginatedList)either.left().get();

										for (WorldTemplate worldTemplate : worldTemplatePaginatedListxxx.templates) {
											class_4419.this.field_20071.method_21448(worldTemplate);
										}

										if (worldTemplatePaginatedListxxx.templates.isEmpty()) {
											if (class_4419.this.field_20071.method_21446()) {
												String string = RealmsScreen.getLocalizedString("mco.template.select.none", "%link");
												class_4450.class_4452 lvx = class_4450.class_4452.method_21582(
													RealmsScreen.getLocalizedString("mco.template.select.none.linkTitle"), "https://minecraft.net/realms/content-creator/"
												);
												class_4419.this.field_20085 = class_4450.method_21578(string, lvx);
											}

											return null;
										} else {
											return worldTemplatePaginatedListxxx;
										}
									}
								})
							)
							.join();
					}
				}
			})
			.start();
	}

	private Either<WorldTemplatePaginatedList, String> method_21416(WorldTemplatePaginatedList worldTemplatePaginatedList, class_4341 arg) {
		try {
			return Either.left(arg.method_20990(worldTemplatePaginatedList.page + 1, worldTemplatePaginatedList.size, this.field_20079));
		} catch (class_4355 var4) {
			return Either.right(var4.getMessage());
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.field_20077 = null;
		this.field_20078 = null;
		this.field_20084 = false;
		this.renderBackground();
		this.field_20071.render(i, j, f);
		if (this.field_20085 != null) {
			this.method_21414(i, j, this.field_20085);
		}

		this.drawCenteredString(this.field_20073, this.width() / 2, 13, 16777215);
		if (this.field_20083) {
			String[] strings = this.field_20081.split("\\\\n");

			for (int k = 0; k < strings.length; k++) {
				int l = this.fontWidth(strings[k]);
				int m = this.width() / 2 - l / 2;
				int n = class_4359.method_21072(-1 + k);
				if (i >= m && i <= m + l && j >= n && j <= n + this.fontLineHeight()) {
					this.field_20084 = true;
				}
			}

			for (int kx = 0; kx < strings.length; kx++) {
				String string = strings[kx];
				int m = 10526880;
				if (this.field_20082 != null) {
					if (this.field_20084) {
						m = 7107012;
						string = "§n" + string;
					} else {
						m = 3368635;
					}
				}

				this.drawCenteredString(string, this.width() / 2, class_4359.method_21072(-1 + kx), m);
			}
		}

		super.render(i, j, f);
		if (this.field_20077 != null) {
			this.method_21424(this.field_20077, i, j);
		}
	}

	private void method_21414(int i, int j, List<class_4450.class_4451> list) {
		for (int k = 0; k < list.size(); k++) {
			class_4450.class_4451 lv = (class_4450.class_4451)list.get(k);
			int l = class_4359.method_21072(4 + k);
			int m = lv.field_20266.stream().mapToInt(arg -> this.fontWidth(arg.method_21580())).sum();
			int n = this.width() / 2 - m / 2;

			for (class_4450.class_4452 lv2 : lv.field_20266) {
				int o = lv2.method_21583() ? 3368635 : 16777215;
				int p = this.draw(lv2.method_21580(), n, l, o, true);
				if (lv2.method_21583() && i > n && i < p && j > l - 3 && j < l + 8) {
					this.field_20077 = lv2.method_21584();
					this.field_20078 = lv2.method_21584();
				}

				n = p;
			}
		}
	}

	protected void method_21424(String string, int i, int j) {
		if (string != null) {
			int k = i + 12;
			int l = j - 12;
			int m = this.fontWidth(string);
			this.fillGradient(k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
			this.fontDrawShadow(string, k, l, 16777215);
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4420 extends RealmsObjectSelectionList<class_4419.class_4421> {
		public class_4420() {
			this(Collections.emptyList());
		}

		public class_4420(Iterable<WorldTemplate> iterable) {
			super(class_4419.this.width(), class_4419.this.height(), class_4419.this.field_20083 ? class_4359.method_21072(1) : 32, class_4419.this.height() - 40, 46);
			iterable.forEach(this::method_21448);
		}

		public void method_21448(WorldTemplate worldTemplate) {
			this.addEntry(class_4419.this.new class_4421(worldTemplate));
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			if (i == 0 && e >= (double)this.y0() && e <= (double)this.y1()) {
				int j = this.width() / 2 - 150;
				if (class_4419.this.field_20078 != null) {
					class_4448.method_21570(class_4419.this.field_20078);
				}

				int k = (int)Math.floor(e - (double)this.y0()) - this.headerHeight() + this.getScroll() - 4;
				int l = k / this.itemHeight();
				if (d >= (double)j && d < (double)this.getScrollbarPosition() && l >= 0 && k >= 0 && l < this.getItemCount()) {
					this.selectItem(l);
					this.itemClicked(k, l, d, e, this.width());
					if (l >= class_4419.this.field_20071.getItemCount()) {
						return super.mouseClicked(d, e, i);
					}

					class_4419.this.field_20072 = l;
					class_4419.this.method_21425();
					class_4419.this.field_20080 = class_4419.this.field_20080 + 7;
					if (class_4419.this.field_20080 >= 10) {
						class_4419.this.method_21440();
					}

					return true;
				}
			}

			return super.mouseClicked(d, e, i);
		}

		@Override
		public void selectItem(int i) {
			class_4419.this.field_20072 = i;
			this.setSelected(i);
			if (i != -1) {
				WorldTemplate worldTemplate = class_4419.this.field_20071.method_21447(i);
				String string = RealmsScreen.getLocalizedString("narrator.select.list.position", i + 1, class_4419.this.field_20071.getItemCount());
				String string2 = RealmsScreen.getLocalizedString("mco.template.select.narrate.version", worldTemplate.version);
				String string3 = RealmsScreen.getLocalizedString("mco.template.select.narrate.authors", worldTemplate.author);
				String string4 = Realms.joinNarrations(Arrays.asList(worldTemplate.name, string3, worldTemplate.recommendedPlayers, string2, string));
				Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", string4));
			}

			class_4419.this.method_21425();
		}

		@Override
		public void itemClicked(int i, int j, double d, double e, int k) {
			if (j < class_4419.this.field_20071.getItemCount()) {
				;
			}
		}

		@Override
		public int getMaxPosition() {
			return this.getItemCount() * 46;
		}

		@Override
		public int getRowWidth() {
			return 300;
		}

		@Override
		public void renderBackground() {
			class_4419.this.renderBackground();
		}

		@Override
		public boolean isFocused() {
			return class_4419.this.isFocused(this);
		}

		public boolean method_21446() {
			return this.getItemCount() == 0;
		}

		public WorldTemplate method_21447(int i) {
			return ((class_4419.class_4421)this.children().get(i)).field_20094;
		}

		public List<WorldTemplate> method_21450() {
			return (List<WorldTemplate>)this.children().stream().map(arg -> arg.field_20094).collect(Collectors.toList());
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4421 extends RealmListEntry {
		final WorldTemplate field_20094;

		public class_4421(WorldTemplate worldTemplate) {
			this.field_20094 = worldTemplate;
		}

		@Override
		public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			this.method_21453(this.field_20094, k, j, n, o);
		}

		private void method_21453(WorldTemplate worldTemplate, int i, int j, int k, int l) {
			int m = i + 45 + 20;
			class_4419.this.drawString(worldTemplate.name, m, j + 2, 16777215);
			class_4419.this.drawString(worldTemplate.author, m, j + 15, 7105644);
			class_4419.this.drawString(worldTemplate.version, m + 227 - class_4419.this.fontWidth(worldTemplate.version), j + 1, 7105644);
			if (!"".equals(worldTemplate.link) || !"".equals(worldTemplate.trailer) || !"".equals(worldTemplate.recommendedPlayers)) {
				this.method_21452(m - 1, j + 25, k, l, worldTemplate.link, worldTemplate.trailer, worldTemplate.recommendedPlayers);
			}

			this.method_21451(i, j + 1, k, l, worldTemplate);
		}

		private void method_21451(int i, int j, int k, int l, WorldTemplate worldTemplate) {
			class_4446.method_21560(worldTemplate.id, worldTemplate.image);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RealmsScreen.blit(i + 1, j + 1, 0.0F, 0.0F, 38, 38, 38, 38);
			RealmsScreen.bind("realms:textures/gui/realms/slot_frame.png");
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RealmsScreen.blit(i, j, 0.0F, 0.0F, 40, 40, 40, 40);
		}

		private void method_21452(int i, int j, int k, int l, String string, String string2, String string3) {
			if (!"".equals(string3)) {
				class_4419.this.drawString(string3, i, j + 4, 5000268);
			}

			int m = "".equals(string3) ? 0 : class_4419.this.fontWidth(string3) + 2;
			boolean bl = false;
			boolean bl2 = false;
			if (k >= i + m && k <= i + m + 32 && l >= j && l <= j + 15 && l < class_4419.this.height() - 15 && l > 32) {
				if (k <= i + 15 + m && k > m) {
					if ("".equals(string)) {
						bl2 = true;
					} else {
						bl = true;
					}
				} else if (!"".equals(string)) {
					bl2 = true;
				}
			}

			if (!"".equals(string)) {
				RealmsScreen.bind("realms:textures/gui/realms/link_icons.png");
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.pushMatrix();
				GlStateManager.scalef(1.0F, 1.0F, 1.0F);
				RealmsScreen.blit(i + m, j, bl ? 15.0F : 0.0F, 0.0F, 15, 15, 30, 15);
				GlStateManager.popMatrix();
			}

			if (!"".equals(string2)) {
				RealmsScreen.bind("realms:textures/gui/realms/trailer_icons.png");
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.pushMatrix();
				GlStateManager.scalef(1.0F, 1.0F, 1.0F);
				RealmsScreen.blit(i + m + ("".equals(string) ? 0 : 17), j, bl2 ? 15.0F : 0.0F, 0.0F, 15, 15, 30, 15);
				GlStateManager.popMatrix();
			}

			if (bl && !"".equals(string)) {
				class_4419.this.field_20077 = RealmsScreen.getLocalizedString("mco.template.info.tooltip");
				class_4419.this.field_20078 = string;
			} else if (bl2 && !"".equals(string2)) {
				class_4419.this.field_20077 = RealmsScreen.getLocalizedString("mco.template.trailer.tooltip");
				class_4419.this.field_20078 = string2;
			}
		}
	}
}
