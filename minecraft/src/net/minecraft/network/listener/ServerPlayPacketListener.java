package net.minecraft.network.listener;

import net.minecraft.server.network.packet.AdvancementTabC2SPacket;
import net.minecraft.server.network.packet.BoatPaddleStateC2SPacket;
import net.minecraft.server.network.packet.BookUpdateC2SPacket;
import net.minecraft.server.network.packet.ButtonClickC2SPacket;
import net.minecraft.server.network.packet.ChatMessageC2SPacket;
import net.minecraft.server.network.packet.ClickWindowC2SPacket;
import net.minecraft.server.network.packet.ClientCommandC2SPacket;
import net.minecraft.server.network.packet.ClientSettingsC2SPacket;
import net.minecraft.server.network.packet.ClientStatusC2SPacket;
import net.minecraft.server.network.packet.CraftRequestC2SPacket;
import net.minecraft.server.network.packet.CreativeInventoryActionC2SPacket;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.server.network.packet.GuiActionConfirmC2SPacket;
import net.minecraft.server.network.packet.GuiCloseC2SPacket;
import net.minecraft.server.network.packet.HandSwingC2SPacket;
import net.minecraft.server.network.packet.KeepAliveC2SPacket;
import net.minecraft.server.network.packet.PickFromInventoryC2SPacket;
import net.minecraft.server.network.packet.PlayerActionC2SPacket;
import net.minecraft.server.network.packet.PlayerInputC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractBlockC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractEntityC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractItemC2SPacket;
import net.minecraft.server.network.packet.PlayerMoveC2SPacket;
import net.minecraft.server.network.packet.QueryBlockNbtC2SPacket;
import net.minecraft.server.network.packet.QueryEntityNbtC2SPacket;
import net.minecraft.server.network.packet.RecipeBookDataC2SPacket;
import net.minecraft.server.network.packet.RenameItemC2SPacket;
import net.minecraft.server.network.packet.RequestCommandCompletionsC2SPacket;
import net.minecraft.server.network.packet.ResourcePackStatusC2SPacket;
import net.minecraft.server.network.packet.SelectVillagerTradeC2SPacket;
import net.minecraft.server.network.packet.SpectatorTeleportC2SPacket;
import net.minecraft.server.network.packet.TeleportConfirmC2SPacket;
import net.minecraft.server.network.packet.UpdateBeaconC2SPacket;
import net.minecraft.server.network.packet.UpdateCommandBlockC2SPacket;
import net.minecraft.server.network.packet.UpdateCommandBlockMinecartC2SPacket;
import net.minecraft.server.network.packet.UpdateDifficultyC2SPacket;
import net.minecraft.server.network.packet.UpdateDifficultyLockC2SPacket;
import net.minecraft.server.network.packet.UpdateJigsawC2SPacket;
import net.minecraft.server.network.packet.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.server.network.packet.UpdateSelectedSlotC2SPacket;
import net.minecraft.server.network.packet.UpdateSignC2SPacket;
import net.minecraft.server.network.packet.UpdateStructureBlockC2SPacket;
import net.minecraft.server.network.packet.VehicleMoveC2SPacket;

public interface ServerPlayPacketListener extends PacketListener {
	void onHandSwing(HandSwingC2SPacket handSwingC2SPacket);

	void onChatMessage(ChatMessageC2SPacket chatMessageC2SPacket);

	void onClientStatus(ClientStatusC2SPacket clientStatusC2SPacket);

	void onClientSettings(ClientSettingsC2SPacket clientSettingsC2SPacket);

	void onConfirmTransaction(GuiActionConfirmC2SPacket guiActionConfirmC2SPacket);

	void onButtonClick(ButtonClickC2SPacket buttonClickC2SPacket);

	void onClickWindow(ClickWindowC2SPacket clickWindowC2SPacket);

	void onCraftRequest(CraftRequestC2SPacket craftRequestC2SPacket);

	void onGuiClose(GuiCloseC2SPacket guiCloseC2SPacket);

	void onCustomPayload(CustomPayloadC2SPacket customPayloadC2SPacket);

	void onPlayerInteractEntity(PlayerInteractEntityC2SPacket playerInteractEntityC2SPacket);

	void onKeepAlive(KeepAliveC2SPacket keepAliveC2SPacket);

	void onPlayerMove(PlayerMoveC2SPacket playerMoveC2SPacket);

	void onPlayerAbilities(UpdatePlayerAbilitiesC2SPacket updatePlayerAbilitiesC2SPacket);

	void onPlayerAction(PlayerActionC2SPacket playerActionC2SPacket);

	void onClientCommand(ClientCommandC2SPacket clientCommandC2SPacket);

	void onPlayerInput(PlayerInputC2SPacket playerInputC2SPacket);

	void onUpdateSelectedSlot(UpdateSelectedSlotC2SPacket updateSelectedSlotC2SPacket);

	void onCreativeInventoryAction(CreativeInventoryActionC2SPacket creativeInventoryActionC2SPacket);

	void onSignUpdate(UpdateSignC2SPacket updateSignC2SPacket);

	void onPlayerInteractBlock(PlayerInteractBlockC2SPacket playerInteractBlockC2SPacket);

	void onPlayerInteractItem(PlayerInteractItemC2SPacket playerInteractItemC2SPacket);

	void onSpectatorTeleport(SpectatorTeleportC2SPacket spectatorTeleportC2SPacket);

	void onResourcePackStatus(ResourcePackStatusC2SPacket resourcePackStatusC2SPacket);

	void onBoatPaddleState(BoatPaddleStateC2SPacket boatPaddleStateC2SPacket);

	void onVehicleMove(VehicleMoveC2SPacket vehicleMoveC2SPacket);

	void onTeleportConfirm(TeleportConfirmC2SPacket teleportConfirmC2SPacket);

	void onRecipeBookData(RecipeBookDataC2SPacket recipeBookDataC2SPacket);

	void onAdvancementTab(AdvancementTabC2SPacket advancementTabC2SPacket);

	void onRequestCommandCompletions(RequestCommandCompletionsC2SPacket requestCommandCompletionsC2SPacket);

	void onUpdateCommandBlock(UpdateCommandBlockC2SPacket updateCommandBlockC2SPacket);

	void onUpdateCommandBlockMinecart(UpdateCommandBlockMinecartC2SPacket updateCommandBlockMinecartC2SPacket);

	void onPickFromInventory(PickFromInventoryC2SPacket pickFromInventoryC2SPacket);

	void onRenameItem(RenameItemC2SPacket renameItemC2SPacket);

	void onUpdateBeacon(UpdateBeaconC2SPacket updateBeaconC2SPacket);

	void onStructureBlockUpdate(UpdateStructureBlockC2SPacket updateStructureBlockC2SPacket);

	void onVillagerTradeSelect(SelectVillagerTradeC2SPacket selectVillagerTradeC2SPacket);

	void onBookUpdate(BookUpdateC2SPacket bookUpdateC2SPacket);

	void onQueryEntityNbt(QueryEntityNbtC2SPacket queryEntityNbtC2SPacket);

	void onQueryBlockNbt(QueryBlockNbtC2SPacket queryBlockNbtC2SPacket);

	void onJigsawUpdate(UpdateJigsawC2SPacket updateJigsawC2SPacket);

	void onUpdateDifficulty(UpdateDifficultyC2SPacket updateDifficultyC2SPacket);

	void onUpdateDifficultyLock(UpdateDifficultyLockC2SPacket updateDifficultyLockC2SPacket);
}
