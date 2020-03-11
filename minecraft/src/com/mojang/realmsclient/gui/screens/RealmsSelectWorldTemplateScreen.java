package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Either;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.TextRenderingUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsSelectWorldTemplateScreen extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier field_22719 = new Identifier("realms", "textures/gui/realms/link_icons.png");
	private static final Identifier field_22720 = new Identifier("realms", "textures/gui/realms/trailer_icons.png");
	private static final Identifier field_22721 = new Identifier("realms", "textures/gui/realms/slot_frame.png");
	private final RealmsScreenWithCallback lastScreen;
	private RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList field_20071;
	private int selectedTemplate = -1;
	private String title;
	private ButtonWidget selectButton;
	private ButtonWidget trailerButton;
	private ButtonWidget publisherButton;
	private String toolTip;
	private String currentLink;
	private final RealmsServer.WorldType worldType;
	private int clicks;
	private String warning;
	private String warningURL;
	private boolean displayWarning;
	private boolean hoverWarning;
	private List<TextRenderingUtils.Line> noTemplatesMessage;

	public RealmsSelectWorldTemplateScreen(RealmsScreenWithCallback realmsScreenWithCallback, RealmsServer.WorldType worldType) {
		this(realmsScreenWithCallback, worldType, null);
	}

	public RealmsSelectWorldTemplateScreen(
		RealmsScreenWithCallback realmsScreenWithCallback, RealmsServer.WorldType worldType, @Nullable WorldTemplatePaginatedList worldTemplatePaginatedList
	) {
		this.lastScreen = realmsScreenWithCallback;
		this.worldType = worldType;
		if (worldTemplatePaginatedList == null) {
			this.field_20071 = new RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList();
			this.method_21415(new WorldTemplatePaginatedList(10));
		} else {
			this.field_20071 = new RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList(
				Lists.<WorldTemplate>newArrayList(worldTemplatePaginatedList.templates)
			);
			this.method_21415(worldTemplatePaginatedList);
		}

		this.title = I18n.translate("mco.template.title");
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setWarning(String string) {
		this.warning = string;
		this.displayWarning = true;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.hoverWarning && this.warningURL != null) {
			Util.getOperatingSystem().open("https://beta.minecraft.net/realms/adventure-maps-in-1-9");
			return true;
		} else {
			return super.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	public void init() {
		this.client.keyboard.enableRepeatEvents(true);
		this.field_20071 = new RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList(this.field_20071.method_21450());
		this.trailerButton = this.addButton(
			new ButtonWidget(this.width / 2 - 206, this.height - 32, 100, 20, I18n.translate("mco.template.button.trailer"), buttonWidgetx -> this.onTrailer())
		);
		this.selectButton = this.addButton(
			new ButtonWidget(this.width / 2 - 100, this.height - 32, 100, 20, I18n.translate("mco.template.button.select"), buttonWidgetx -> this.selectTemplate())
		);
		String string = this.worldType == RealmsServer.WorldType.MINIGAME ? "gui.cancel" : "gui.back";
		ButtonWidget buttonWidget = new ButtonWidget(this.width / 2 + 6, this.height - 32, 100, 20, I18n.translate(string), buttonWidgetx -> this.backButtonClicked());
		this.addButton(buttonWidget);
		this.publisherButton = this.addButton(
			new ButtonWidget(this.width / 2 + 112, this.height - 32, 100, 20, I18n.translate("mco.template.button.publisher"), buttonWidgetx -> this.onPublish())
		);
		this.selectButton.active = false;
		this.trailerButton.visible = false;
		this.publisherButton.visible = false;
		this.addChild(this.field_20071);
		this.focusOn(this.field_20071);
		Realms.narrateNow((Iterable<String>)Stream.of(this.title, this.warning).filter(Objects::nonNull).collect(Collectors.toList()));
	}

	private void updateButtonStates() {
		this.publisherButton.visible = this.shouldPublisherBeVisible();
		this.trailerButton.visible = this.shouldTrailerBeVisible();
		this.selectButton.active = this.shouldSelectButtonBeActive();
	}

	private boolean shouldSelectButtonBeActive() {
		return this.selectedTemplate != -1;
	}

	private boolean shouldPublisherBeVisible() {
		return this.selectedTemplate != -1 && !this.method_21434().link.isEmpty();
	}

	private WorldTemplate method_21434() {
		return this.field_20071.method_21447(this.selectedTemplate);
	}

	private boolean shouldTrailerBeVisible() {
		return this.selectedTemplate != -1 && !this.method_21434().trailer.isEmpty();
	}

	@Override
	public void tick() {
		super.tick();
		this.clicks--;
		if (this.clicks < 0) {
			this.clicks = 0;
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.backButtonClicked();
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	private void backButtonClicked() {
		this.lastScreen.callback(null);
		this.client.openScreen(this.lastScreen);
	}

	private void selectTemplate() {
		if (this.method_25247()) {
			this.lastScreen.callback(this.method_21434());
		}
	}

	private boolean method_25247() {
		return this.selectedTemplate >= 0 && this.selectedTemplate < this.field_20071.getItemCount();
	}

	private void onTrailer() {
		if (this.method_25247()) {
			WorldTemplate worldTemplate = this.method_21434();
			if (!"".equals(worldTemplate.trailer)) {
				Util.getOperatingSystem().open(worldTemplate.trailer);
			}
		}
	}

	private void onPublish() {
		if (this.method_25247()) {
			WorldTemplate worldTemplate = this.method_21434();
			if (!"".equals(worldTemplate.link)) {
				Util.getOperatingSystem().open(worldTemplate.link);
			}
		}
	}

	private void method_21415(WorldTemplatePaginatedList worldTemplatePaginatedList) {
		(new Thread("realms-template-fetcher") {
				public void run() {
					WorldTemplatePaginatedList worldTemplatePaginatedList = worldTemplatePaginatedList;
					RealmsClient realmsClient = RealmsClient.createRealmsClient();

					while (worldTemplatePaginatedList != null) {
						Either<WorldTemplatePaginatedList, String> either = RealmsSelectWorldTemplateScreen.this.method_21416(worldTemplatePaginatedList, realmsClient);
						worldTemplatePaginatedList = (WorldTemplatePaginatedList)RealmsSelectWorldTemplateScreen.this.client
							.submit(
								() -> {
									if (either.right().isPresent()) {
										RealmsSelectWorldTemplateScreen.LOGGER.error("Couldn't fetch templates: {}", either.right().get());
										if (RealmsSelectWorldTemplateScreen.this.field_20071.method_21446()) {
											RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose(I18n.translate("mco.template.select.failure"));
										}

										return null;
									} else {
										WorldTemplatePaginatedList worldTemplatePaginatedListxxx = (WorldTemplatePaginatedList)either.left().get();

										for (WorldTemplate worldTemplate : worldTemplatePaginatedListxxx.templates) {
											RealmsSelectWorldTemplateScreen.this.field_20071.addEntry(worldTemplate);
										}

										if (worldTemplatePaginatedListxxx.templates.isEmpty()) {
											if (RealmsSelectWorldTemplateScreen.this.field_20071.method_21446()) {
												String string = I18n.translate("mco.template.select.none", "%link");
												TextRenderingUtils.LineSegment lineSegment = TextRenderingUtils.LineSegment.link(
													I18n.translate("mco.template.select.none.linkTitle"), "https://minecraft.net/realms/content-creator/"
												);
												RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose(string, lineSegment);
											}

											return null;
										} else {
											return worldTemplatePaginatedListxxx;
										}
									}
								}
							)
							.join();
					}
				}
			})
			.start();
	}

	private Either<WorldTemplatePaginatedList, String> method_21416(WorldTemplatePaginatedList worldTemplatePaginatedList, RealmsClient realmsClient) {
		try {
			return Either.left(realmsClient.fetchWorldTemplates(worldTemplatePaginatedList.page + 1, worldTemplatePaginatedList.size, this.worldType));
		} catch (RealmsServiceException var4) {
			return Either.right(var4.getMessage());
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.toolTip = null;
		this.currentLink = null;
		this.hoverWarning = false;
		this.renderBackground();
		this.field_20071.render(mouseX, mouseY, delta);
		if (this.noTemplatesMessage != null) {
			this.method_21414(mouseX, mouseY, this.noTemplatesMessage);
		}

		this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 13, 16777215);
		if (this.displayWarning) {
			String[] strings = this.warning.split("\\\\n");

			for (int i = 0; i < strings.length; i++) {
				int j = this.textRenderer.getStringWidth(strings[i]);
				int k = this.width / 2 - j / 2;
				int l = row(-1 + i);
				if (mouseX >= k && mouseX <= k + j && mouseY >= l && mouseY <= l + 9) {
					this.hoverWarning = true;
				}
			}

			for (int ix = 0; ix < strings.length; ix++) {
				String string = strings[ix];
				int k = 10526880;
				if (this.warningURL != null) {
					if (this.hoverWarning) {
						k = 7107012;
						string = Formatting.STRIKETHROUGH + string;
					} else {
						k = 3368635;
					}
				}

				this.drawCenteredString(this.textRenderer, string, this.width / 2, row(-1 + ix), k);
			}
		}

		super.render(mouseX, mouseY, delta);
		if (this.toolTip != null) {
			this.renderMousehoverTooltip(this.toolTip, mouseX, mouseY);
		}
	}

	private void method_21414(int i, int j, List<TextRenderingUtils.Line> list) {
		for (int k = 0; k < list.size(); k++) {
			TextRenderingUtils.Line line = (TextRenderingUtils.Line)list.get(k);
			int l = row(4 + k);
			int m = line.segments.stream().mapToInt(lineSegmentx -> this.textRenderer.getStringWidth(lineSegmentx.renderedText())).sum();
			int n = this.width / 2 - m / 2;

			for (TextRenderingUtils.LineSegment lineSegment : line.segments) {
				int o = lineSegment.isLink() ? 3368635 : 16777215;
				int p = this.textRenderer.drawWithShadow(lineSegment.renderedText(), (float)n, (float)l, o);
				if (lineSegment.isLink() && i > n && i < p && j > l - 3 && j < l + 8) {
					this.toolTip = lineSegment.getLinkUrl();
					this.currentLink = lineSegment.getLinkUrl();
				}

				n = p;
			}
		}
	}

	protected void renderMousehoverTooltip(String msg, int x, int y) {
		if (msg != null) {
			int i = x + 12;
			int j = y - 12;
			int k = this.textRenderer.getStringWidth(msg);
			this.fillGradient(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
			this.textRenderer.drawWithShadow(msg, (float)i, (float)j, 16777215);
		}
	}

	@Environment(EnvType.CLIENT)
	class WorldTemplateObjectSelectionList extends RealmsObjectSelectionList<RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionListEntry> {
		public WorldTemplateObjectSelectionList() {
			this(Collections.emptyList());
		}

		public WorldTemplateObjectSelectionList(Iterable<WorldTemplate> iterable) {
			super(
				RealmsSelectWorldTemplateScreen.this.width,
				RealmsSelectWorldTemplateScreen.this.height,
				RealmsSelectWorldTemplateScreen.this.displayWarning ? RealmsSelectWorldTemplateScreen.row(1) : 32,
				RealmsSelectWorldTemplateScreen.this.height - 40,
				46
			);
			iterable.forEach(this::addEntry);
		}

		public void addEntry(WorldTemplate template) {
			this.addEntry(RealmsSelectWorldTemplateScreen.this.new WorldTemplateObjectSelectionListEntry(template));
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (button == 0 && mouseY >= (double)this.top && mouseY <= (double)this.bottom) {
				int i = this.width / 2 - 150;
				if (RealmsSelectWorldTemplateScreen.this.currentLink != null) {
					Util.getOperatingSystem().open(RealmsSelectWorldTemplateScreen.this.currentLink);
				}

				int j = (int)Math.floor(mouseY - (double)this.top) - this.headerHeight + (int)this.getScrollAmount() - 4;
				int k = j / this.itemHeight;
				if (mouseX >= (double)i && mouseX < (double)this.getScrollbarPositionX() && k >= 0 && j >= 0 && k < this.getItemCount()) {
					this.setSelected(k);
					this.itemClicked(j, k, mouseX, mouseY, this.width);
					if (k >= RealmsSelectWorldTemplateScreen.this.field_20071.getItemCount()) {
						return super.mouseClicked(mouseX, mouseY, button);
					}

					RealmsSelectWorldTemplateScreen.this.clicks = RealmsSelectWorldTemplateScreen.this.clicks + 7;
					if (RealmsSelectWorldTemplateScreen.this.clicks >= 10) {
						RealmsSelectWorldTemplateScreen.this.selectTemplate();
					}

					return true;
				}
			}

			return super.mouseClicked(mouseX, mouseY, button);
		}

		@Override
		public void setSelected(int i) {
			this.setSelectedItem(i);
			if (i != -1) {
				WorldTemplate worldTemplate = RealmsSelectWorldTemplateScreen.this.field_20071.method_21447(i);
				String string = I18n.translate("narrator.select.list.position", i + 1, RealmsSelectWorldTemplateScreen.this.field_20071.getItemCount());
				String string2 = I18n.translate("mco.template.select.narrate.version", worldTemplate.version);
				String string3 = I18n.translate("mco.template.select.narrate.authors", worldTemplate.author);
				String string4 = Realms.joinNarrations(Arrays.asList(worldTemplate.name, string3, worldTemplate.recommendedPlayers, string2, string));
				Realms.narrateNow(I18n.translate("narrator.select", string4));
			}
		}

		public void setSelected(@Nullable RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionListEntry worldTemplateObjectSelectionListEntry) {
			super.setSelected(worldTemplateObjectSelectionListEntry);
			RealmsSelectWorldTemplateScreen.this.selectedTemplate = this.children().indexOf(worldTemplateObjectSelectionListEntry);
			RealmsSelectWorldTemplateScreen.this.updateButtonStates();
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
			RealmsSelectWorldTemplateScreen.this.renderBackground();
		}

		@Override
		public boolean isFocused() {
			return RealmsSelectWorldTemplateScreen.this.getFocused() == this;
		}

		public boolean method_21446() {
			return this.getItemCount() == 0;
		}

		public WorldTemplate method_21447(int i) {
			return ((RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionListEntry)this.children().get(i)).mTemplate;
		}

		public List<WorldTemplate> method_21450() {
			return (List<WorldTemplate>)this.children()
				.stream()
				.map(worldTemplateObjectSelectionListEntry -> worldTemplateObjectSelectionListEntry.mTemplate)
				.collect(Collectors.toList());
		}
	}

	@Environment(EnvType.CLIENT)
	class WorldTemplateObjectSelectionListEntry extends AlwaysSelectedEntryListWidget.Entry<RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionListEntry> {
		private final WorldTemplate mTemplate;

		public WorldTemplateObjectSelectionListEntry(WorldTemplate template) {
			this.mTemplate = template;
		}

		@Override
		public void render(int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
			this.renderWorldTemplateItem(this.mTemplate, x, y, mouseX, mouseY);
		}

		private void renderWorldTemplateItem(WorldTemplate worldTemplate, int x, int y, int mouseX, int mouseY) {
			int i = x + 45 + 20;
			RealmsSelectWorldTemplateScreen.this.textRenderer.draw(worldTemplate.name, (float)i, (float)(y + 2), 16777215);
			RealmsSelectWorldTemplateScreen.this.textRenderer.draw(worldTemplate.author, (float)i, (float)(y + 15), 7105644);
			RealmsSelectWorldTemplateScreen.this.textRenderer
				.draw(
					worldTemplate.version, (float)(i + 227 - RealmsSelectWorldTemplateScreen.this.textRenderer.getStringWidth(worldTemplate.version)), (float)(y + 1), 7105644
				);
			if (!"".equals(worldTemplate.link) || !"".equals(worldTemplate.trailer) || !"".equals(worldTemplate.recommendedPlayers)) {
				this.drawIcons(i - 1, y + 25, mouseX, mouseY, worldTemplate.link, worldTemplate.trailer, worldTemplate.recommendedPlayers);
			}

			this.drawImage(x, y + 1, mouseX, mouseY, worldTemplate);
		}

		private void drawImage(int x, int y, int xm, int ym, WorldTemplate worldTemplate) {
			RealmsTextureManager.bindWorldTemplate(worldTemplate.id, worldTemplate.image);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			DrawableHelper.drawTexture(x + 1, y + 1, 0.0F, 0.0F, 38, 38, 38, 38);
			RealmsSelectWorldTemplateScreen.this.client.getTextureManager().bindTexture(RealmsSelectWorldTemplateScreen.field_22721);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			DrawableHelper.drawTexture(x, y, 0.0F, 0.0F, 40, 40, 40, 40);
		}

		private void drawIcons(int x, int y, int xm, int ym, String link, String trailerLink, String recommendedPlayers) {
			if (!"".equals(recommendedPlayers)) {
				RealmsSelectWorldTemplateScreen.this.textRenderer.draw(recommendedPlayers, (float)x, (float)(y + 4), 5000268);
			}

			int i = "".equals(recommendedPlayers) ? 0 : RealmsSelectWorldTemplateScreen.this.textRenderer.getStringWidth(recommendedPlayers) + 2;
			boolean bl = false;
			boolean bl2 = false;
			if (xm >= x + i && xm <= x + i + 32 && ym >= y && ym <= y + 15 && ym < RealmsSelectWorldTemplateScreen.this.height - 15 && ym > 32) {
				if (xm <= x + 15 + i && xm > i) {
					if ("".equals(link)) {
						bl2 = true;
					} else {
						bl = true;
					}
				} else if (!"".equals(link)) {
					bl2 = true;
				}
			}

			if (!"".equals(link)) {
				RealmsSelectWorldTemplateScreen.this.client.getTextureManager().bindTexture(RealmsSelectWorldTemplateScreen.field_22719);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.pushMatrix();
				RenderSystem.scalef(1.0F, 1.0F, 1.0F);
				float f = bl ? 15.0F : 0.0F;
				DrawableHelper.drawTexture(x + i, y, f, 0.0F, 15, 15, 30, 15);
				RenderSystem.popMatrix();
			}

			if (!"".equals(trailerLink)) {
				RealmsSelectWorldTemplateScreen.this.client.getTextureManager().bindTexture(RealmsSelectWorldTemplateScreen.field_22720);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.pushMatrix();
				RenderSystem.scalef(1.0F, 1.0F, 1.0F);
				int j = x + i + ("".equals(link) ? 0 : 17);
				float g = bl2 ? 15.0F : 0.0F;
				DrawableHelper.drawTexture(j, y, g, 0.0F, 15, 15, 30, 15);
				RenderSystem.popMatrix();
			}

			if (bl && !"".equals(link)) {
				RealmsSelectWorldTemplateScreen.this.toolTip = I18n.translate("mco.template.info.tooltip");
				RealmsSelectWorldTemplateScreen.this.currentLink = link;
			} else if (bl2 && !"".equals(trailerLink)) {
				RealmsSelectWorldTemplateScreen.this.toolTip = I18n.translate("mco.template.trailer.tooltip");
				RealmsSelectWorldTemplateScreen.this.currentLink = trailerLink;
			}
		}
	}
}
