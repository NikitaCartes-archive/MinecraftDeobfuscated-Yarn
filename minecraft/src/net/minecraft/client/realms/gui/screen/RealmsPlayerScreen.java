package net.minecraft.client.realms.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsObjectSelectionList;
import net.minecraft.client.realms.dto.Ops;
import net.minecraft.client.realms.dto.PlayerInfo;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.util.RealmsTextureManager;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsPlayerScreen extends RealmsScreen {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Identifier OP_ICON = new Identifier("realms", "textures/gui/realms/op_icon.png");
	private static final Identifier USER_ICON = new Identifier("realms", "textures/gui/realms/user_icon.png");
	private static final Identifier CROSS_PLAYER_ICON = new Identifier("realms", "textures/gui/realms/cross_player_icon.png");
	private static final Identifier OPTIONS_BACKGROUND = new Identifier("minecraft", "textures/gui/options_background.png");
	private static final Text NORMAL_TOOLTIP = Text.translatable("mco.configure.world.invites.normal.tooltip");
	private static final Text OPERATOR_TOOLTIP = Text.translatable("mco.configure.world.invites.ops.tooltip");
	private static final Text REMOVE_TOOLTIP = Text.translatable("mco.configure.world.invites.remove.tooltip");
	private static final Text INVITED_TEXT = Text.translatable("mco.configure.world.invited");
	@Nullable
	private Text tooltip;
	private final RealmsConfigureWorldScreen parent;
	final RealmsServer serverData;
	private RealmsPlayerScreen.InvitedObjectSelectionList invitedObjectSelectionList;
	int column1_x;
	int column_width;
	private int column2_x;
	private ButtonWidget removeButton;
	private ButtonWidget opdeopButton;
	private int selectedInvitedIndex = -1;
	private String selectedInvited;
	int player = -1;
	private boolean stateChanged;
	RealmsPlayerScreen.PlayerOperation operation = RealmsPlayerScreen.PlayerOperation.NONE;

	public RealmsPlayerScreen(RealmsConfigureWorldScreen parent, RealmsServer serverData) {
		super(Text.translatable("mco.configure.world.players.title"));
		this.parent = parent;
		this.serverData = serverData;
	}

	@Override
	public void init() {
		this.column1_x = this.width / 2 - 160;
		this.column_width = 150;
		this.column2_x = this.width / 2 + 12;
		this.client.keyboard.setRepeatEvents(true);
		this.invitedObjectSelectionList = new RealmsPlayerScreen.InvitedObjectSelectionList();
		this.invitedObjectSelectionList.setLeftPos(this.column1_x);
		this.addSelectableChild(this.invitedObjectSelectionList);

		for (PlayerInfo playerInfo : this.serverData.players) {
			this.invitedObjectSelectionList.addEntry(playerInfo);
		}

		this.addDrawableChild(
			ButtonWidget.createBuilder(
					Text.translatable("mco.configure.world.buttons.invite"), button -> this.client.setScreen(new RealmsInviteScreen(this.parent, this, this.serverData))
				)
				.setPositionAndSize(this.column2_x, row(1), this.column_width + 10, 20)
				.build()
		);
		this.removeButton = this.addDrawableChild(
			ButtonWidget.createBuilder(Text.translatable("mco.configure.world.invites.remove.tooltip"), button -> this.uninvite(this.player))
				.setPositionAndSize(this.column2_x, row(7), this.column_width + 10, 20)
				.build()
		);
		this.opdeopButton = this.addDrawableChild(ButtonWidget.createBuilder(Text.translatable("mco.configure.world.invites.ops.tooltip"), button -> {
			if (((PlayerInfo)this.serverData.players.get(this.player)).isOperator()) {
				this.deop(this.player);
			} else {
				this.op(this.player);
			}
		}).setPositionAndSize(this.column2_x, row(9), this.column_width + 10, 20).build());
		this.addDrawableChild(
			ButtonWidget.createBuilder(ScreenTexts.BACK, button -> this.backButtonClicked())
				.setPositionAndSize(this.column2_x + this.column_width / 2 + 2, row(12), this.column_width / 2 + 10 - 2, 20)
				.build()
		);
		this.updateButtonStates();
	}

	void updateButtonStates() {
		this.removeButton.visible = this.shouldRemoveAndOpdeopButtonBeVisible(this.player);
		this.opdeopButton.visible = this.shouldRemoveAndOpdeopButtonBeVisible(this.player);
	}

	private boolean shouldRemoveAndOpdeopButtonBeVisible(int player) {
		return player != -1;
	}

	@Override
	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
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
		this.updateButtonStates();
		RealmsClient realmsClient = RealmsClient.create();
		String string = ((PlayerInfo)this.serverData.players.get(index)).getUuid();

		try {
			this.updateOps(realmsClient.op(this.serverData.id, string));
		} catch (RealmsServiceException var5) {
			LOGGER.error("Couldn't op the user");
		}
	}

	void deop(int index) {
		this.updateButtonStates();
		RealmsClient realmsClient = RealmsClient.create();
		String string = ((PlayerInfo)this.serverData.players.get(index)).getUuid();

		try {
			this.updateOps(realmsClient.deop(this.serverData.id, string));
		} catch (RealmsServiceException var5) {
			LOGGER.error("Couldn't deop the user");
		}
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
			this.selectedInvited = playerInfo.getUuid();
			this.selectedInvitedIndex = index;
			RealmsConfirmScreen realmsConfirmScreen = new RealmsConfirmScreen(confirmed -> {
				if (confirmed) {
					RealmsClient realmsClient = RealmsClient.create();

					try {
						realmsClient.uninvite(this.serverData.id, this.selectedInvited);
					} catch (RealmsServiceException var4) {
						LOGGER.error("Couldn't uninvite user");
					}

					this.deleteFromInvitedList(this.selectedInvitedIndex);
					this.player = -1;
					this.updateButtonStates();
				}

				this.stateChanged = true;
				this.client.setScreen(this);
			}, Text.literal("Question"), Text.translatable("mco.configure.world.uninvite.question").append(" '").append(playerInfo.getName()).append("' ?"));
			this.client.setScreen(realmsConfirmScreen);
		}
	}

	private void deleteFromInvitedList(int selectedInvitedIndex) {
		this.serverData.players.remove(selectedInvitedIndex);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.tooltip = null;
		this.operation = RealmsPlayerScreen.PlayerOperation.NONE;
		this.renderBackground(matrices);
		if (this.invitedObjectSelectionList != null) {
			this.invitedObjectSelectionList.render(matrices, mouseX, mouseY, delta);
		}

		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 17, 16777215);
		int i = row(12) + 20;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
		RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(0.0, (double)this.height, 0.0).texture(0.0F, (float)(this.height - i) / 32.0F + 0.0F).color(64, 64, 64, 255).next();
		bufferBuilder.vertex((double)this.width, (double)this.height, 0.0)
			.texture((float)this.width / 32.0F, (float)(this.height - i) / 32.0F + 0.0F)
			.color(64, 64, 64, 255)
			.next();
		bufferBuilder.vertex((double)this.width, (double)i, 0.0).texture((float)this.width / 32.0F, 0.0F).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(0.0, (double)i, 0.0).texture(0.0F, 0.0F).color(64, 64, 64, 255).next();
		tessellator.draw();
		if (this.serverData != null && this.serverData.players != null) {
			this.textRenderer
				.draw(
					matrices,
					Text.empty().append(INVITED_TEXT).append(" (").append(Integer.toString(this.serverData.players.size())).append(")"),
					(float)this.column1_x,
					(float)row(0),
					10526880
				);
		} else {
			this.textRenderer.draw(matrices, INVITED_TEXT, (float)this.column1_x, (float)row(0), 10526880);
		}

		super.render(matrices, mouseX, mouseY, delta);
		if (this.serverData != null) {
			this.renderMousehoverTooltip(matrices, this.tooltip, mouseX, mouseY);
		}
	}

	protected void renderMousehoverTooltip(MatrixStack matrices, @Nullable Text tooltip, int mouseX, int mouseY) {
		if (tooltip != null) {
			int i = mouseX + 12;
			int j = mouseY - 12;
			int k = this.textRenderer.getWidth(tooltip);
			this.fillGradient(matrices, i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
			this.textRenderer.drawWithShadow(matrices, tooltip, (float)i, (float)j, 16777215);
		}
	}

	void drawRemoveIcon(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		boolean bl = mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 9 && mouseY < row(12) + 20 && mouseY > row(1);
		RenderSystem.setShaderTexture(0, CROSS_PLAYER_ICON);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		float f = bl ? 7.0F : 0.0F;
		DrawableHelper.drawTexture(matrices, x, y, 0.0F, f, 8, 7, 8, 14);
		if (bl) {
			this.tooltip = REMOVE_TOOLTIP;
			this.operation = RealmsPlayerScreen.PlayerOperation.REMOVE;
		}
	}

	void drawOpped(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		boolean bl = mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 9 && mouseY < row(12) + 20 && mouseY > row(1);
		RenderSystem.setShaderTexture(0, OP_ICON);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		float f = bl ? 8.0F : 0.0F;
		DrawableHelper.drawTexture(matrices, x, y, 0.0F, f, 8, 8, 8, 16);
		if (bl) {
			this.tooltip = OPERATOR_TOOLTIP;
			this.operation = RealmsPlayerScreen.PlayerOperation.TOGGLE_OP;
		}
	}

	void drawNormal(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		boolean bl = mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 9 && mouseY < row(12) + 20 && mouseY > row(1);
		RenderSystem.setShaderTexture(0, USER_ICON);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		float f = bl ? 8.0F : 0.0F;
		DrawableHelper.drawTexture(matrices, x, y, 0.0F, f, 8, 8, 8, 16);
		if (bl) {
			this.tooltip = NORMAL_TOOLTIP;
			this.operation = RealmsPlayerScreen.PlayerOperation.TOGGLE_OP;
		}
	}

	@Environment(EnvType.CLIENT)
	class InvitedObjectSelectionList extends RealmsObjectSelectionList<RealmsPlayerScreen.InvitedObjectSelectionListEntry> {
		public InvitedObjectSelectionList() {
			super(RealmsPlayerScreen.this.column_width + 10, RealmsPlayerScreen.row(12) + 20, RealmsPlayerScreen.row(1), RealmsPlayerScreen.row(12) + 20, 13);
		}

		public void addEntry(PlayerInfo playerInfo) {
			this.addEntry(RealmsPlayerScreen.this.new InvitedObjectSelectionListEntry(playerInfo));
		}

		@Override
		public int getRowWidth() {
			return (int)((double)this.width * 1.0);
		}

		@Override
		public boolean isFocused() {
			return RealmsPlayerScreen.this.getFocused() == this;
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (button == 0 && mouseX < (double)this.getScrollbarPositionX() && mouseY >= (double)this.top && mouseY <= (double)this.bottom) {
				int i = RealmsPlayerScreen.this.column1_x;
				int j = RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width;
				int k = (int)Math.floor(mouseY - (double)this.top) - this.headerHeight + (int)this.getScrollAmount() - 4;
				int l = k / this.itemHeight;
				if (mouseX >= (double)i && mouseX <= (double)j && l >= 0 && k >= 0 && l < this.getEntryCount()) {
					this.setSelected(l);
					this.itemClicked(k, l, mouseX, mouseY, this.width);
				}

				return true;
			} else {
				return super.mouseClicked(mouseX, mouseY, button);
			}
		}

		@Override
		public void itemClicked(int cursorY, int selectionIndex, double mouseX, double mouseY, int listWidth) {
			if (selectionIndex >= 0
				&& selectionIndex <= RealmsPlayerScreen.this.serverData.players.size()
				&& RealmsPlayerScreen.this.operation != RealmsPlayerScreen.PlayerOperation.NONE) {
				if (RealmsPlayerScreen.this.operation == RealmsPlayerScreen.PlayerOperation.TOGGLE_OP) {
					if (((PlayerInfo)RealmsPlayerScreen.this.serverData.players.get(selectionIndex)).isOperator()) {
						RealmsPlayerScreen.this.deop(selectionIndex);
					} else {
						RealmsPlayerScreen.this.op(selectionIndex);
					}
				} else if (RealmsPlayerScreen.this.operation == RealmsPlayerScreen.PlayerOperation.REMOVE) {
					RealmsPlayerScreen.this.uninvite(selectionIndex);
				}
			}
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
		public void renderBackground(MatrixStack matrices) {
			RealmsPlayerScreen.this.renderBackground(matrices);
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
		private final PlayerInfo playerInfo;

		public InvitedObjectSelectionListEntry(PlayerInfo playerInfo) {
			this.playerInfo = playerInfo;
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.renderInvitedItem(matrices, this.playerInfo, x, y, mouseX, mouseY);
		}

		private void renderInvitedItem(MatrixStack matrices, PlayerInfo playerInfo, int x, int y, int mouseX, int mouseY) {
			int i;
			if (!playerInfo.isAccepted()) {
				i = 10526880;
			} else if (playerInfo.isOnline()) {
				i = 8388479;
			} else {
				i = 16777215;
			}

			RealmsPlayerScreen.this.textRenderer.draw(matrices, playerInfo.getName(), (float)(RealmsPlayerScreen.this.column1_x + 3 + 12), (float)(y + 1), i);
			if (playerInfo.isOperator()) {
				RealmsPlayerScreen.this.drawOpped(matrices, RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width - 10, y + 1, mouseX, mouseY);
			} else {
				RealmsPlayerScreen.this.drawNormal(matrices, RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width - 10, y + 1, mouseX, mouseY);
			}

			RealmsPlayerScreen.this.drawRemoveIcon(matrices, RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width - 22, y + 2, mouseX, mouseY);
			RealmsTextureManager.withBoundFace(playerInfo.getUuid(), () -> {
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				PlayerSkinDrawer.draw(matrices, RealmsPlayerScreen.this.column1_x + 2 + 2, y + 1, 8);
			});
		}

		@Override
		public Text getNarration() {
			return Text.translatable("narrator.select", this.playerInfo.getName());
		}
	}

	@Environment(EnvType.CLIENT)
	static enum PlayerOperation {
		TOGGLE_OP,
		REMOVE,
		NONE;
	}
}
