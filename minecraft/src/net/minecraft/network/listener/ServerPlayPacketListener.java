package net.minecraft.network.listener;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ButtonClickServerPacket;
import net.minecraft.server.network.packet.AdvancementTabServerPacket;
import net.minecraft.server.network.packet.BoatPaddleStateServerPacket;
import net.minecraft.server.network.packet.BookUpdateServerPacket;
import net.minecraft.server.network.packet.ChatMessageServerPacket;
import net.minecraft.server.network.packet.ClickWindowServerPacket;
import net.minecraft.server.network.packet.ClientCommandServerPacket;
import net.minecraft.server.network.packet.ClientSettingsServerPacket;
import net.minecraft.server.network.packet.ClientStatusServerPacket;
import net.minecraft.server.network.packet.CraftRequestServerPacket;
import net.minecraft.server.network.packet.CreativeInventoryActionServerPacket;
import net.minecraft.server.network.packet.CustomPayloadServerPacket;
import net.minecraft.server.network.packet.GuiActionConfirmServerPacket;
import net.minecraft.server.network.packet.GuiCloseServerPacket;
import net.minecraft.server.network.packet.HandSwingServerPacket;
import net.minecraft.server.network.packet.KeepAliveServerPacket;
import net.minecraft.server.network.packet.PickFromInventoryServerPacket;
import net.minecraft.server.network.packet.PlayerActionServerPacket;
import net.minecraft.server.network.packet.PlayerInteractBlockServerPacket;
import net.minecraft.server.network.packet.PlayerInteractEntityServerPacket;
import net.minecraft.server.network.packet.PlayerInteractItemServerPacket;
import net.minecraft.server.network.packet.PlayerLookServerPacket;
import net.minecraft.server.network.packet.PlayerMoveServerMessage;
import net.minecraft.server.network.packet.QueryBlockNbtServerPacket;
import net.minecraft.server.network.packet.QueryEntityNbtServerPacket;
import net.minecraft.server.network.packet.RecipeBookDataServerPacket;
import net.minecraft.server.network.packet.RenameItemServerPacket;
import net.minecraft.server.network.packet.RequestCommandCompletionsServerPacket;
import net.minecraft.server.network.packet.ResourcePackStatusServerPacket;
import net.minecraft.server.network.packet.SelectVillagerTradeServerPacket;
import net.minecraft.server.network.packet.SpectatorTeleportServerPacket;
import net.minecraft.server.network.packet.TeleportConfirmServerPacket;
import net.minecraft.server.network.packet.UpdateBeaconServerPacket;
import net.minecraft.server.network.packet.UpdateCommandBlockMinecartServerPacket;
import net.minecraft.server.network.packet.UpdateCommandBlockServerPacket;
import net.minecraft.server.network.packet.UpdateJigsawServerPacket;
import net.minecraft.server.network.packet.UpdatePlayerAbilitiesServerPacket;
import net.minecraft.server.network.packet.UpdateSelectedSlotServerPacket;
import net.minecraft.server.network.packet.UpdateSignServerPacket;
import net.minecraft.server.network.packet.UpdateStructureBlockServerPacket;
import net.minecraft.server.network.packet.VehicleMoveServerPacket;

public interface ServerPlayPacketListener extends PacketListener {
	void onHandSwing(HandSwingServerPacket handSwingServerPacket);

	void onChatMessage(ChatMessageServerPacket chatMessageServerPacket);

	void onClientStatus(ClientStatusServerPacket clientStatusServerPacket);

	void onClientSettings(ClientSettingsServerPacket clientSettingsServerPacket);

	void onConfirmTransaction(GuiActionConfirmServerPacket guiActionConfirmServerPacket);

	void onButtonClick(ButtonClickServerPacket buttonClickServerPacket);

	void onClickWindow(ClickWindowServerPacket clickWindowServerPacket);

	void onCraftRequest(CraftRequestServerPacket craftRequestServerPacket);

	void onGuiClose(GuiCloseServerPacket guiCloseServerPacket);

	void onCustomPayload(CustomPayloadServerPacket customPayloadServerPacket);

	void onPlayerInteractEntity(PlayerInteractEntityServerPacket playerInteractEntityServerPacket);

	void onKeepAlive(KeepAliveServerPacket keepAliveServerPacket);

	void onPlayerMove(PlayerMoveServerMessage playerMoveServerMessage);

	void onPlayerAbilities(UpdatePlayerAbilitiesServerPacket updatePlayerAbilitiesServerPacket);

	void onPlayerAction(PlayerActionServerPacket playerActionServerPacket);

	void onClientCommand(ClientCommandServerPacket clientCommandServerPacket);

	void onPlayerLook(PlayerLookServerPacket playerLookServerPacket);

	void onUpdateSelectedSlot(UpdateSelectedSlotServerPacket updateSelectedSlotServerPacket);

	void onCreativeInventoryAction(CreativeInventoryActionServerPacket creativeInventoryActionServerPacket);

	void onSignUpdate(UpdateSignServerPacket updateSignServerPacket);

	void onPlayerInteractBlock(PlayerInteractBlockServerPacket playerInteractBlockServerPacket);

	void onPlayerInteractItem(PlayerInteractItemServerPacket playerInteractItemServerPacket);

	void onSpectatorTeleport(SpectatorTeleportServerPacket spectatorTeleportServerPacket);

	void onResourcePackStatus(ResourcePackStatusServerPacket resourcePackStatusServerPacket);

	void onBoatPaddleState(BoatPaddleStateServerPacket boatPaddleStateServerPacket);

	void onVehicleMove(VehicleMoveServerPacket vehicleMoveServerPacket);

	void onTeleportConfirm(TeleportConfirmServerPacket teleportConfirmServerPacket);

	void onRecipeBookData(RecipeBookDataServerPacket recipeBookDataServerPacket);

	void onAdvancementTab(AdvancementTabServerPacket advancementTabServerPacket);

	void onRequestCommandCompletions(RequestCommandCompletionsServerPacket requestCommandCompletionsServerPacket);

	void onUpdateCommandBlock(UpdateCommandBlockServerPacket updateCommandBlockServerPacket);

	void onUpdateCommandBlockMinecart(UpdateCommandBlockMinecartServerPacket updateCommandBlockMinecartServerPacket);

	void onPickFromInventory(PickFromInventoryServerPacket pickFromInventoryServerPacket);

	void onRenameItem(RenameItemServerPacket renameItemServerPacket);

	void onUpdateBeacon(UpdateBeaconServerPacket updateBeaconServerPacket);

	void onStructureBlockUpdate(UpdateStructureBlockServerPacket updateStructureBlockServerPacket);

	void onVillagerTradeSelect(SelectVillagerTradeServerPacket selectVillagerTradeServerPacket);

	void onBookUpdate(BookUpdateServerPacket bookUpdateServerPacket);

	void onQueryEntityNbt(QueryEntityNbtServerPacket queryEntityNbtServerPacket);

	void onQueryBlockNbt(QueryBlockNbtServerPacket queryBlockNbtServerPacket);

	@Environment(EnvType.CLIENT)
	void onJigsawUpdate(UpdateJigsawServerPacket updateJigsawServerPacket);
}
