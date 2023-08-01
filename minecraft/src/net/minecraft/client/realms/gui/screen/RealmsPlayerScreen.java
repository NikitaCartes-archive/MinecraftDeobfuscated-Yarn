package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsObjectSelectionList;
import net.minecraft.client.realms.dto.Ops;
import net.minecraft.client.realms.dto.PlayerInfo;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsPlayerScreen extends RealmsScreen {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Identifier OPTIONS_BACKGROUND = new Identifier("minecraft", "textures/gui/options_background.png");
	private static final Text QUESTION_TEXT = Text.translatable("mco.question");
	static final Text NORMAL_TOOLTIP = Text.translatable("mco.configure.world.invites.normal.tooltip");
	static final Text OPERATOR_TOOLTIP = Text.translatable("mco.configure.world.invites.ops.tooltip");
	static final Text REMOVE_TOOLTIP = Text.translatable("mco.configure.world.invites.remove.tooltip");
	private static final int field_44530 = -1;
	private final RealmsConfigureWorldScreen parent;
	final RealmsServer serverData;
	RealmsPlayerScreen.InvitedObjectSelectionList invitedObjectSelectionList;
	int column1_x;
	int column_width;
	private ButtonWidget removeButton;
	private ButtonWidget opdeopButton;
	int player = -1;
	private boolean stateChanged;

	public RealmsPlayerScreen(RealmsConfigureWorldScreen parent, RealmsServer serverData) {
		super(Text.translatable("mco.configure.world.players.title"));
		this.parent = parent;
		this.serverData = serverData;
	}

	@Override
	public void init() {
		this.column1_x = this.width / 2 - 160;
		this.column_width = 150;
		int i = this.width / 2 + 12;
		this.invitedObjectSelectionList = new RealmsPlayerScreen.InvitedObjectSelectionList();
		this.invitedObjectSelectionList.setLeftPos(this.column1_x);
		this.addSelectableChild(this.invitedObjectSelectionList);

		for (PlayerInfo playerInfo : this.serverData.players) {
			this.invitedObjectSelectionList.addEntry(playerInfo);
		}

		this.player = -1;
		this.addDrawableChild(
			ButtonWidget.builder(
					Text.translatable("mco.configure.world.buttons.invite"), button -> this.client.setScreen(new RealmsInviteScreen(this.parent, this, this.serverData))
				)
				.dimensions(i, row(1), this.column_width + 10, 20)
				.build()
		);
		this.removeButton = this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("mco.configure.world.invites.remove.tooltip"), button -> this.uninvite(this.player))
				.dimensions(i, row(7), this.column_width + 10, 20)
				.build()
		);
		this.opdeopButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("mco.configure.world.invites.ops.tooltip"), button -> {
			if (((PlayerInfo)this.serverData.players.get(this.player)).isOperator()) {
				this.deop(this.player);
			} else {
				this.op(this.player);
			}
		}).dimensions(i, row(9), this.column_width + 10, 20).build());
		this.addDrawableChild(
			ButtonWidget.builder(ScreenTexts.BACK, button -> this.backButtonClicked())
				.dimensions(i + this.column_width / 2 + 2, row(12), this.column_width / 2 + 10 - 2, 20)
				.build()
		);
		this.updateButtonStates();
	}

	void updateButtonStates() {
		this.removeButton.visible = this.shouldRemoveAndOpdeopButtonBeVisible(this.player);
		this.opdeopButton.visible = this.shouldRemoveAndOpdeopButtonBeVisible(this.player);
		this.invitedObjectSelectionList.updateButtonStates();
	}

	private boolean shouldRemoveAndOpdeopButtonBeVisible(int player) {
		return player != -1;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.backButtonClicked();
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	private void backButtonClicked() {
		if (this.stateChanged) {
			this.client.setScreen(this.parent.getNewScreen());
		} else {
			this.client.setScreen(this.parent);
		}
	}

	void op(int index) {
		RealmsClient realmsClient = RealmsClient.create();
		UUID uUID = ((PlayerInfo)this.serverData.players.get(index)).getUuid();

		try {
			this.updateOps(realmsClient.op(this.serverData.id, uUID));
		} catch (RealmsServiceException var5) {
			LOGGER.error("Couldn't op the user", (Throwable)var5);
		}

		this.updateButtonStates();
	}

	void deop(int index) {
		RealmsClient realmsClient = RealmsClient.create();
		UUID uUID = ((PlayerInfo)this.serverData.players.get(index)).getUuid();

		try {
			this.updateOps(realmsClient.deop(this.serverData.id, uUID));
		} catch (RealmsServiceException var5) {
			LOGGER.error("Couldn't deop the user", (Throwable)var5);
		}

		this.updateButtonStates();
	}

	private void updateOps(Ops ops) {
		for (PlayerInfo playerInfo : this.serverData.players) {
			playerInfo.setOperator(ops.ops.contains(playerInfo.getName()));
		}
	}

	void uninvite(int index) {
		this.updateButtonStates();
		if (index >= 0 && index < this.serverData.players.size()) {
			PlayerInfo playerInfo = (PlayerInfo)this.serverData.players.get(index);
			RealmsConfirmScreen realmsConfirmScreen = new RealmsConfirmScreen(confirmed -> {
				if (confirmed) {
					RealmsClient realmsClient = RealmsClient.create();

					try {
						realmsClient.uninvite(this.serverData.id, playerInfo.getUuid());
					} catch (RealmsServiceException var5) {
						LOGGER.error("Couldn't uninvite user", (Throwable)var5);
					}

					this.serverData.players.remove(this.player);
					this.player = -1;
					this.updateButtonStates();
				}

				this.stateChanged = true;
				this.client.setScreen(this);
			}, QUESTION_TEXT, Text.translatable("mco.configure.world.uninvite.player", playerInfo.getName()));
			this.client.setScreen(realmsConfirmScreen);
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.invitedObjectSelectionList.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 17, -1);
		int i = row(12) + 20;
		context.setShaderColor(0.25F, 0.25F, 0.25F, 1.0F);
		context.drawTexture(OPTIONS_BACKGROUND, 0, i, 0.0F, 0.0F, this.width, this.height - i, 32, 32);
		context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		String string = this.serverData.players != null ? Integer.toString(this.serverData.players.size()) : "0";
		context.drawText(this.textRenderer, Text.translatable("mco.configure.world.invited.number", string), this.column1_x, row(0), -6250336, false);
	}

	@Environment(EnvType.CLIENT)
	class InvitedObjectSelectionList extends RealmsObjectSelectionList<RealmsPlayerScreen.InvitedObjectSelectionListEntry> {
		public InvitedObjectSelectionList() {
			super(RealmsPlayerScreen.this.column_width + 10, RealmsPlayerScreen.row(12) + 20, RealmsPlayerScreen.row(1), RealmsPlayerScreen.row(12) + 20, 13);
		}

		public void updateButtonStates() {
			if (RealmsPlayerScreen.this.player != -1) {
				this.getEntry(RealmsPlayerScreen.this.player).updateButtonStates();
			}
		}

		public void addEntry(PlayerInfo playerInfo) {
			this.addEntry(RealmsPlayerScreen.this.new InvitedObjectSelectionListEntry(playerInfo));
		}

		@Override
		public int getRowWidth() {
			return (int)((double)this.width * 1.0);
		}

		@Override
		public void setSelected(int index) {
			super.setSelected(index);
			this.selectInviteListItem(index);
		}

		public void selectInviteListItem(int item) {
			RealmsPlayerScreen.this.player = item;
			RealmsPlayerScreen.this.updateButtonStates();
		}

		public void setSelected(@Nullable RealmsPlayerScreen.InvitedObjectSelectionListEntry invitedObjectSelectionListEntry) {
			super.setSelected(invitedObjectSelectionListEntry);
			RealmsPlayerScreen.this.player = this.children().indexOf(invitedObjectSelectionListEntry);
			RealmsPlayerScreen.this.updateButtonStates();
		}

		@Override
		public int getScrollbarPositionX() {
			return RealmsPlayerScreen.this.column1_x + this.width - 5;
		}

		@Override
		public int getMaxPosition() {
			return this.getEntryCount() * 13;
		}
	}

	@Environment(EnvType.CLIENT)
	class InvitedObjectSelectionListEntry extends AlwaysSelectedEntryListWidget.Entry<RealmsPlayerScreen.InvitedObjectSelectionListEntry> {
		private static final int field_44531 = 3;
		private static final int field_44532 = 1;
		private static final int field_44533 = 8;
		private static final int field_44534 = 7;
		private static final ButtonTextures REMOVE_PLAYER_TEXTURES = new ButtonTextures(
			new Identifier("player_list/remove_player"), new Identifier("player_list/remove_player_highlighted")
		);
		private static final ButtonTextures MAKE_OPERATOR_TEXTURES = new ButtonTextures(
			new Identifier("player_list/make_operator"), new Identifier("player_list/make_operator_highlighted")
		);
		private static final ButtonTextures REMOVE_OPERATOR_TEXTURES = new ButtonTextures(
			new Identifier("player_list/remove_operator"), new Identifier("player_list/remove_operator_highlighted")
		);
		private final PlayerInfo playerInfo;
		private final List<ClickableWidget> buttons = new ArrayList();
		private final TexturedButtonWidget uninviteButton;
		private final TexturedButtonWidget opButton;
		private final TexturedButtonWidget deopButton;

		public InvitedObjectSelectionListEntry(PlayerInfo playerInfo) {
			this.playerInfo = playerInfo;
			int i = RealmsPlayerScreen.this.serverData.players.indexOf(this.playerInfo);
			int j = RealmsPlayerScreen.this.invitedObjectSelectionList.getRowRight() - 16 - 9;
			int k = RealmsPlayerScreen.this.invitedObjectSelectionList.getRowTop(i) + 1;
			this.uninviteButton = new TexturedButtonWidget(j, k, 8, 7, REMOVE_PLAYER_TEXTURES, button -> RealmsPlayerScreen.this.uninvite(i), ScreenTexts.EMPTY);
			this.uninviteButton.setTooltip(Tooltip.of(RealmsPlayerScreen.REMOVE_TOOLTIP));
			this.buttons.add(this.uninviteButton);
			j += 11;
			this.opButton = new TexturedButtonWidget(j, k, 8, 7, MAKE_OPERATOR_TEXTURES, button -> RealmsPlayerScreen.this.op(i), ScreenTexts.EMPTY);
			this.opButton.setTooltip(Tooltip.of(RealmsPlayerScreen.NORMAL_TOOLTIP));
			this.buttons.add(this.opButton);
			this.deopButton = new TexturedButtonWidget(j, k, 8, 7, REMOVE_OPERATOR_TEXTURES, button -> RealmsPlayerScreen.this.deop(i), ScreenTexts.EMPTY);
			this.deopButton.setTooltip(Tooltip.of(RealmsPlayerScreen.OPERATOR_TOOLTIP));
			this.buttons.add(this.deopButton);
			this.updateButtonStates();
		}

		public void updateButtonStates() {
			this.opButton.visible = !this.playerInfo.isOperator();
			this.deopButton.visible = !this.opButton.visible;
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (!this.opButton.mouseClicked(mouseX, mouseY, button)) {
				this.deopButton.mouseClicked(mouseX, mouseY, button);
			}

			this.uninviteButton.mouseClicked(mouseX, mouseY, button);
			return true;
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			int i;
			if (!this.playerInfo.isAccepted()) {
				i = -6250336;
			} else if (this.playerInfo.isOnline()) {
				i = 8388479;
			} else {
				i = -1;
			}

			RealmsUtil.drawPlayerHead(context, RealmsPlayerScreen.this.column1_x + 2 + 2, y + 1, 8, this.playerInfo.getUuid());
			context.drawText(RealmsPlayerScreen.this.textRenderer, this.playerInfo.getName(), RealmsPlayerScreen.this.column1_x + 3 + 12, y + 1, i, false);
			this.buttons.forEach(button -> {
				button.setY(y + 1);
				button.render(context, mouseX, mouseY, tickDelta);
			});
		}

		@Override
		public Text getNarration() {
			return Text.translatable("narrator.select", this.playerInfo.getName());
		}
	}
}
