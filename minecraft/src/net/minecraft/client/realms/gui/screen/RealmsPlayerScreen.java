package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.Ops;
import net.minecraft.client.realms.dto.PlayerInfo;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsPlayerScreen extends RealmsScreen {
	static final Logger LOGGER = LogUtils.getLogger();
	private static final Text TITLE = Text.translatable("mco.configure.world.players.title");
	static final Text QUESTION_TEXT = Text.translatable("mco.question");
	private static final int field_49462 = 8;
	final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
	private final RealmsConfigureWorldScreen parent;
	final RealmsServer serverData;
	@Nullable
	private RealmsPlayerScreen.InvitedObjectSelectionList selectionList;
	boolean stateChanged;

	public RealmsPlayerScreen(RealmsConfigureWorldScreen parent, RealmsServer serverData) {
		super(TITLE);
		this.parent = parent;
		this.serverData = serverData;
	}

	@Override
	public void init() {
		this.layout.addHeader(TITLE, this.textRenderer);
		this.selectionList = this.layout.addBody(new RealmsPlayerScreen.InvitedObjectSelectionList());
		this.refreshPlayers();
		DirectionalLayoutWidget directionalLayoutWidget = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
		directionalLayoutWidget.add(
			ButtonWidget.builder(
					Text.translatable("mco.configure.world.buttons.invite"), button -> this.client.setScreen(new RealmsInviteScreen(this.parent, this, this.serverData))
				)
				.build()
		);
		directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).build());
		this.layout.forEachChild(child -> {
			ClickableWidget var10000 = this.addDrawableChild(child);
		});
		this.initTabNavigation();
	}

	@Override
	protected void initTabNavigation() {
		this.layout.refreshPositions();
		if (this.selectionList != null) {
			this.selectionList.position(this.width, this.layout);
		}
	}

	void refreshPlayers() {
		if (this.selectionList != null) {
			this.selectionList.children().clear();

			for (PlayerInfo playerInfo : this.serverData.players) {
				this.selectionList.children().add(new RealmsPlayerScreen.InvitedObjectSelectionListEntry(playerInfo));
			}
		}
	}

	@Override
	public void close() {
		this.backButtonClicked();
	}

	private void backButtonClicked() {
		if (this.stateChanged) {
			this.client.setScreen(this.parent.getNewScreen());
		} else {
			this.client.setScreen(this.parent);
		}
	}

	@Environment(EnvType.CLIENT)
	class InvitedObjectSelectionList extends ElementListWidget<RealmsPlayerScreen.InvitedObjectSelectionListEntry> {
		private static final int field_49472 = 36;

		public InvitedObjectSelectionList() {
			super(
				MinecraftClient.getInstance(),
				RealmsPlayerScreen.this.width,
				RealmsPlayerScreen.this.layout.getContentHeight(),
				RealmsPlayerScreen.this.layout.getHeaderHeight(),
				36
			);
			this.setRenderHeader(true, (int)(9.0F * 1.5F));
		}

		@Override
		protected void renderHeader(DrawContext context, int x, int y) {
			String string = RealmsPlayerScreen.this.serverData.players != null ? Integer.toString(RealmsPlayerScreen.this.serverData.players.size()) : "0";
			Text text = Text.translatable("mco.configure.world.invited.number", string).formatted(Formatting.UNDERLINE);
			context.drawText(
				RealmsPlayerScreen.this.textRenderer, text, x + this.getRowWidth() / 2 - RealmsPlayerScreen.this.textRenderer.getWidth(text) / 2, y, Colors.WHITE, false
			);
		}

		@Override
		public int getMaxPosition() {
			return this.getEntryCount() * this.itemHeight + this.headerHeight;
		}

		@Override
		public int getRowWidth() {
			return 300;
		}
	}

	@Environment(EnvType.CLIENT)
	class InvitedObjectSelectionListEntry extends ElementListWidget.Entry<RealmsPlayerScreen.InvitedObjectSelectionListEntry> {
		private static final Text NORMAL_TOOLTIP_TEXT = Text.translatable("mco.configure.world.invites.normal.tooltip");
		private static final Text OPS_TOOLTIP_TEXT = Text.translatable("mco.configure.world.invites.ops.tooltip");
		private static final Text REMOVE_TOOLTIP_TEXT = Text.translatable("mco.configure.world.invites.remove.tooltip");
		private static final Identifier MAKE_OPERATOR_TEXTURE = Identifier.method_60656("player_list/make_operator");
		private static final Identifier REMOVE_OPERATOR_TEXTURE = Identifier.method_60656("player_list/remove_operator");
		private static final Identifier REMOVE_PLAYER_TEXTURE = Identifier.method_60656("player_list/remove_player");
		private static final int field_49470 = 8;
		private static final int field_49471 = 7;
		private final PlayerInfo playerInfo;
		private final ButtonWidget uninviteButton;
		private final ButtonWidget opButton;
		private final ButtonWidget deopButton;

		public InvitedObjectSelectionListEntry(final PlayerInfo playerInfo) {
			this.playerInfo = playerInfo;
			int i = RealmsPlayerScreen.this.serverData.players.indexOf(this.playerInfo);
			this.opButton = TextIconButtonWidget.builder(NORMAL_TOOLTIP_TEXT, button -> this.op(i), false)
				.texture(MAKE_OPERATOR_TEXTURE, 8, 7)
				.width(16 + RealmsPlayerScreen.this.textRenderer.getWidth(NORMAL_TOOLTIP_TEXT))
				.narration(
					supplier -> ScreenTexts.joinSentences(
							Text.translatable("mco.invited.player.narration", playerInfo.getName()),
							(Text)supplier.get(),
							Text.translatable("narration.cycle_button.usage.focused", OPS_TOOLTIP_TEXT)
						)
				)
				.build();
			this.deopButton = TextIconButtonWidget.builder(OPS_TOOLTIP_TEXT, button -> this.deop(i), false)
				.texture(REMOVE_OPERATOR_TEXTURE, 8, 7)
				.width(16 + RealmsPlayerScreen.this.textRenderer.getWidth(OPS_TOOLTIP_TEXT))
				.narration(
					supplier -> ScreenTexts.joinSentences(
							Text.translatable("mco.invited.player.narration", playerInfo.getName()),
							(Text)supplier.get(),
							Text.translatable("narration.cycle_button.usage.focused", NORMAL_TOOLTIP_TEXT)
						)
				)
				.build();
			this.uninviteButton = TextIconButtonWidget.builder(REMOVE_TOOLTIP_TEXT, button -> this.uninvite(i), false)
				.texture(REMOVE_PLAYER_TEXTURE, 8, 7)
				.width(16 + RealmsPlayerScreen.this.textRenderer.getWidth(REMOVE_TOOLTIP_TEXT))
				.narration(supplier -> ScreenTexts.joinSentences(Text.translatable("mco.invited.player.narration", playerInfo.getName()), (Text)supplier.get()))
				.build();
			this.refreshOpButtonsVisibility();
		}

		private void op(int index) {
			RealmsClient realmsClient = RealmsClient.create();
			UUID uUID = ((PlayerInfo)RealmsPlayerScreen.this.serverData.players.get(index)).getUuid();

			try {
				this.setOps(realmsClient.op(RealmsPlayerScreen.this.serverData.id, uUID));
			} catch (RealmsServiceException var5) {
				RealmsPlayerScreen.LOGGER.error("Couldn't op the user", (Throwable)var5);
			}

			this.refreshOpButtonsVisibility();
		}

		private void deop(int index) {
			RealmsClient realmsClient = RealmsClient.create();
			UUID uUID = ((PlayerInfo)RealmsPlayerScreen.this.serverData.players.get(index)).getUuid();

			try {
				this.setOps(realmsClient.deop(RealmsPlayerScreen.this.serverData.id, uUID));
			} catch (RealmsServiceException var5) {
				RealmsPlayerScreen.LOGGER.error("Couldn't deop the user", (Throwable)var5);
			}

			this.refreshOpButtonsVisibility();
		}

		private void uninvite(int index) {
			if (index >= 0 && index < RealmsPlayerScreen.this.serverData.players.size()) {
				PlayerInfo playerInfo = (PlayerInfo)RealmsPlayerScreen.this.serverData.players.get(index);
				RealmsConfirmScreen realmsConfirmScreen = new RealmsConfirmScreen(confirmed -> {
					if (confirmed) {
						RealmsClient realmsClient = RealmsClient.create();

						try {
							realmsClient.uninvite(RealmsPlayerScreen.this.serverData.id, playerInfo.getUuid());
						} catch (RealmsServiceException var6) {
							RealmsPlayerScreen.LOGGER.error("Couldn't uninvite user", (Throwable)var6);
						}

						RealmsPlayerScreen.this.serverData.players.remove(index);
						RealmsPlayerScreen.this.refreshPlayers();
					}

					RealmsPlayerScreen.this.stateChanged = true;
					RealmsPlayerScreen.this.client.setScreen(RealmsPlayerScreen.this);
				}, RealmsPlayerScreen.QUESTION_TEXT, Text.translatable("mco.configure.world.uninvite.player", playerInfo.getName()));
				RealmsPlayerScreen.this.client.setScreen(realmsConfirmScreen);
			}
		}

		private void setOps(Ops ops) {
			for (PlayerInfo playerInfo : RealmsPlayerScreen.this.serverData.players) {
				playerInfo.setOperator(ops.ops.contains(playerInfo.getName()));
			}
		}

		private void refreshOpButtonsVisibility() {
			this.opButton.visible = !this.playerInfo.isOperator();
			this.deopButton.visible = !this.opButton.visible;
		}

		private ButtonWidget getOpButton() {
			return this.opButton.visible ? this.opButton : this.deopButton;
		}

		@Override
		public List<? extends Element> children() {
			return ImmutableList.of(this.getOpButton(), this.uninviteButton);
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return ImmutableList.of(this.getOpButton(), this.uninviteButton);
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			int i;
			if (!this.playerInfo.isAccepted()) {
				i = -6250336;
			} else if (this.playerInfo.isOnline()) {
				i = 8388479;
			} else {
				i = Colors.WHITE;
			}

			int j = y + entryHeight / 2 - 16;
			RealmsUtil.drawPlayerHead(context, x, j, 32, this.playerInfo.getUuid());
			int k = y + entryHeight / 2 - 9 / 2;
			context.drawText(RealmsPlayerScreen.this.textRenderer, this.playerInfo.getName(), x + 8 + 32, k, i, false);
			int l = y + entryHeight / 2 - 10;
			int m = x + entryWidth - this.uninviteButton.getWidth();
			this.uninviteButton.setPosition(m, l);
			this.uninviteButton.render(context, mouseX, mouseY, tickDelta);
			int n = m - this.getOpButton().getWidth() - 8;
			this.opButton.setPosition(n, l);
			this.opButton.render(context, mouseX, mouseY, tickDelta);
			this.deopButton.setPosition(n, l);
			this.deopButton.render(context, mouseX, mouseY, tickDelta);
		}
	}
}
