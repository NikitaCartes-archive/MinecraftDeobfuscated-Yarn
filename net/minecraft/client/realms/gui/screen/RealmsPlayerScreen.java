/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsObjectSelectionList;
import net.minecraft.client.realms.dto.Ops;
import net.minecraft.client.realms.dto.PlayerInfo;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsConfirmScreen;
import net.minecraft.client.realms.gui.screen.RealmsInviteScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsPlayerScreen
extends RealmsScreen {
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
    private InvitedObjectSelectionList invitedObjectSelectionList;
    int column1_x;
    int column_width;
    private int column2_x;
    private ButtonWidget removeButton;
    private ButtonWidget opdeopButton;
    private int selectedInvitedIndex = -1;
    private String selectedInvited;
    int player = -1;
    private boolean stateChanged;
    PlayerOperation operation = PlayerOperation.NONE;

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
        this.invitedObjectSelectionList = new InvitedObjectSelectionList();
        this.invitedObjectSelectionList.setLeftPos(this.column1_x);
        this.addSelectableChild(this.invitedObjectSelectionList);
        for (PlayerInfo playerInfo : this.serverData.players) {
            this.invitedObjectSelectionList.addEntry(playerInfo);
        }
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("mco.configure.world.buttons.invite"), button -> this.client.setScreen(new RealmsInviteScreen(this.parent, this, this.serverData))).dimensions(this.column2_x, RealmsPlayerScreen.row(1), this.column_width + 10, 20).build());
        this.removeButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("mco.configure.world.invites.remove.tooltip"), button -> this.uninvite(this.player)).dimensions(this.column2_x, RealmsPlayerScreen.row(7), this.column_width + 10, 20).build());
        this.opdeopButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("mco.configure.world.invites.ops.tooltip"), button -> {
            if (this.serverData.players.get(this.player).isOperator()) {
                this.deop(this.player);
            } else {
                this.op(this.player);
            }
        }).dimensions(this.column2_x, RealmsPlayerScreen.row(9), this.column_width + 10, 20).build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> this.backButtonClicked()).dimensions(this.column2_x + this.column_width / 2 + 2, RealmsPlayerScreen.row(12), this.column_width / 2 + 10 - 2, 20).build());
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.backButtonClicked();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
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
        String string = this.serverData.players.get(index).getUuid();
        try {
            this.updateOps(realmsClient.op(this.serverData.id, string));
        } catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't op the user");
        }
    }

    void deop(int index) {
        this.updateButtonStates();
        RealmsClient realmsClient = RealmsClient.create();
        String string = this.serverData.players.get(index).getUuid();
        try {
            this.updateOps(realmsClient.deop(this.serverData.id, string));
        } catch (RealmsServiceException realmsServiceException) {
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
            PlayerInfo playerInfo = this.serverData.players.get(index);
            this.selectedInvited = playerInfo.getUuid();
            this.selectedInvitedIndex = index;
            RealmsConfirmScreen realmsConfirmScreen = new RealmsConfirmScreen(confirmed -> {
                if (confirmed) {
                    RealmsClient realmsClient = RealmsClient.create();
                    try {
                        realmsClient.uninvite(this.serverData.id, this.selectedInvited);
                    } catch (RealmsServiceException realmsServiceException) {
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
        this.operation = PlayerOperation.NONE;
        this.renderBackground(matrices);
        if (this.invitedObjectSelectionList != null) {
            this.invitedObjectSelectionList.render(matrices, mouseX, mouseY, delta);
        }
        RealmsPlayerScreen.drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 17, 0xFFFFFF);
        int i = RealmsPlayerScreen.row(12) + 20;
        RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND);
        RenderSystem.setShaderColor(0.25f, 0.25f, 0.25f, 1.0f);
        RealmsPlayerScreen.drawTexture(matrices, 0, i, 0.0f, 0.0f, this.width, this.height - i, 32, 32);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.serverData != null && this.serverData.players != null) {
            this.textRenderer.draw(matrices, Text.empty().append(INVITED_TEXT).append(" (").append(Integer.toString(this.serverData.players.size())).append(")"), (float)this.column1_x, (float)RealmsPlayerScreen.row(0), 0xA0A0A0);
        } else {
            this.textRenderer.draw(matrices, INVITED_TEXT, (float)this.column1_x, (float)RealmsPlayerScreen.row(0), 0xA0A0A0);
        }
        super.render(matrices, mouseX, mouseY, delta);
        if (this.serverData == null) {
            return;
        }
        this.renderMousehoverTooltip(matrices, this.tooltip, mouseX, mouseY);
    }

    protected void renderMousehoverTooltip(MatrixStack matrices, @Nullable Text tooltip, int mouseX, int mouseY) {
        if (tooltip == null) {
            return;
        }
        int i = mouseX + 12;
        int j = mouseY - 12;
        int k = this.textRenderer.getWidth(tooltip);
        RealmsPlayerScreen.fillGradient(matrices, i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
        this.textRenderer.drawWithShadow(matrices, tooltip, (float)i, (float)j, 0xFFFFFF);
    }

    void drawRemoveIcon(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        boolean bl = mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 9 && mouseY < RealmsPlayerScreen.row(12) + 20 && mouseY > RealmsPlayerScreen.row(1);
        RenderSystem.setShaderTexture(0, CROSS_PLAYER_ICON);
        float f = bl ? 7.0f : 0.0f;
        DrawableHelper.drawTexture(matrices, x, y, 0.0f, f, 8, 7, 8, 14);
        if (bl) {
            this.tooltip = REMOVE_TOOLTIP;
            this.operation = PlayerOperation.REMOVE;
        }
    }

    void drawOpped(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        boolean bl = mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 9 && mouseY < RealmsPlayerScreen.row(12) + 20 && mouseY > RealmsPlayerScreen.row(1);
        RenderSystem.setShaderTexture(0, OP_ICON);
        float f = bl ? 8.0f : 0.0f;
        DrawableHelper.drawTexture(matrices, x, y, 0.0f, f, 8, 8, 8, 16);
        if (bl) {
            this.tooltip = OPERATOR_TOOLTIP;
            this.operation = PlayerOperation.TOGGLE_OP;
        }
    }

    void drawNormal(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        boolean bl = mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 9 && mouseY < RealmsPlayerScreen.row(12) + 20 && mouseY > RealmsPlayerScreen.row(1);
        RenderSystem.setShaderTexture(0, USER_ICON);
        float f = bl ? 8.0f : 0.0f;
        DrawableHelper.drawTexture(matrices, x, y, 0.0f, f, 8, 8, 8, 16);
        if (bl) {
            this.tooltip = NORMAL_TOOLTIP;
            this.operation = PlayerOperation.TOGGLE_OP;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static enum PlayerOperation {
        TOGGLE_OP,
        REMOVE,
        NONE;

    }

    @Environment(value=EnvType.CLIENT)
    class InvitedObjectSelectionList
    extends RealmsObjectSelectionList<InvitedObjectSelectionListEntry> {
        public InvitedObjectSelectionList() {
            super(RealmsPlayerScreen.this.column_width + 10, RealmsPlayerScreen.row(12) + 20, RealmsPlayerScreen.row(1), RealmsPlayerScreen.row(12) + 20, 13);
        }

        public void addEntry(PlayerInfo playerInfo) {
            this.addEntry(new InvitedObjectSelectionListEntry(playerInfo));
        }

        @Override
        public int getRowWidth() {
            return (int)((double)this.width * 1.0);
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
                    this.itemClicked(k, l, mouseX, mouseY, this.width, button);
                }
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void itemClicked(int cursorY, int selectionIndex, double mouseX, double mouseY, int listWidth, int i) {
            if (selectionIndex < 0 || selectionIndex > RealmsPlayerScreen.this.serverData.players.size() || RealmsPlayerScreen.this.operation == PlayerOperation.NONE) {
                return;
            }
            if (RealmsPlayerScreen.this.operation == PlayerOperation.TOGGLE_OP) {
                if (RealmsPlayerScreen.this.serverData.players.get(selectionIndex).isOperator()) {
                    RealmsPlayerScreen.this.deop(selectionIndex);
                } else {
                    RealmsPlayerScreen.this.op(selectionIndex);
                }
            } else if (RealmsPlayerScreen.this.operation == PlayerOperation.REMOVE) {
                RealmsPlayerScreen.this.uninvite(selectionIndex);
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

        @Override
        public void setSelected(@Nullable InvitedObjectSelectionListEntry invitedObjectSelectionListEntry) {
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

    @Environment(value=EnvType.CLIENT)
    class InvitedObjectSelectionListEntry
    extends AlwaysSelectedEntryListWidget.Entry<InvitedObjectSelectionListEntry> {
        private final PlayerInfo playerInfo;

        public InvitedObjectSelectionListEntry(PlayerInfo playerInfo) {
            this.playerInfo = playerInfo;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.renderInvitedItem(matrices, this.playerInfo, x, y, mouseX, mouseY);
        }

        private void renderInvitedItem(MatrixStack matrices, PlayerInfo playerInfo, int x, int y, int mouseX, int mouseY) {
            int i = !playerInfo.isAccepted() ? 0xA0A0A0 : (playerInfo.isOnline() ? 0x7FFF7F : 0xFFFFFF);
            RealmsPlayerScreen.this.textRenderer.draw(matrices, playerInfo.getName(), (float)(RealmsPlayerScreen.this.column1_x + 3 + 12), (float)(y + 1), i);
            if (playerInfo.isOperator()) {
                RealmsPlayerScreen.this.drawOpped(matrices, RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width - 10, y + 1, mouseX, mouseY);
            } else {
                RealmsPlayerScreen.this.drawNormal(matrices, RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width - 10, y + 1, mouseX, mouseY);
            }
            RealmsPlayerScreen.this.drawRemoveIcon(matrices, RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width - 22, y + 2, mouseX, mouseY);
            RealmsUtil.drawPlayerHead(matrices, RealmsPlayerScreen.this.column1_x + 2 + 2, y + 1, 8, playerInfo.getUuid());
        }

        @Override
        public Text getNarration() {
            return Text.translatable("narrator.select", this.playerInfo.getName());
        }
    }
}

