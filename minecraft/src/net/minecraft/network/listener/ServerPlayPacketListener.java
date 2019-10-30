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
	void onHandSwing(HandSwingC2SPacket packet);

	void onChatMessage(ChatMessageC2SPacket packet);

	void onClientStatus(ClientStatusC2SPacket packet);

	void onClientSettings(ClientSettingsC2SPacket packet);

	void onConfirmTransaction(GuiActionConfirmC2SPacket packet);

	void onButtonClick(ButtonClickC2SPacket packet);

	void onClickWindow(ClickWindowC2SPacket packet);

	void onCraftRequest(CraftRequestC2SPacket packet);

	void onGuiClose(GuiCloseC2SPacket packet);

	void onCustomPayload(CustomPayloadC2SPacket packet);

	void onPlayerInteractEntity(PlayerInteractEntityC2SPacket rpacket);

	void onKeepAlive(KeepAliveC2SPacket packet);

	void onPlayerMove(PlayerMoveC2SPacket packet);

	void onPlayerAbilities(UpdatePlayerAbilitiesC2SPacket packet);

	void onPlayerAction(PlayerActionC2SPacket packet);

	void onClientCommand(ClientCommandC2SPacket packet);

	void onPlayerInput(PlayerInputC2SPacket packet);

	void onUpdateSelectedSlot(UpdateSelectedSlotC2SPacket packet);

	void onCreativeInventoryAction(CreativeInventoryActionC2SPacket packet);

	void onSignUpdate(UpdateSignC2SPacket packet);

	void onPlayerInteractBlock(PlayerInteractBlockC2SPacket packet);

	void onPlayerInteractItem(PlayerInteractItemC2SPacket packet);

	void onSpectatorTeleport(SpectatorTeleportC2SPacket packet);

	void onResourcePackStatus(ResourcePackStatusC2SPacket packet);

	void onBoatPaddleState(BoatPaddleStateC2SPacket packet);

	void onVehicleMove(VehicleMoveC2SPacket packet);

	void onTeleportConfirm(TeleportConfirmC2SPacket packet);

	void onRecipeBookData(RecipeBookDataC2SPacket packet);

	void onAdvancementTab(AdvancementTabC2SPacket packet);

	void onRequestCommandCompletions(RequestCommandCompletionsC2SPacket packet);

	void onUpdateCommandBlock(UpdateCommandBlockC2SPacket packet);

	void onUpdateCommandBlockMinecart(UpdateCommandBlockMinecartC2SPacket packet);

	void onPickFromInventory(PickFromInventoryC2SPacket packet);

	void onRenameItem(RenameItemC2SPacket packet);

	void onUpdateBeacon(UpdateBeaconC2SPacket packet);

	void onStructureBlockUpdate(UpdateStructureBlockC2SPacket packet);

	void onVillagerTradeSelect(SelectVillagerTradeC2SPacket packet);

	void onBookUpdate(BookUpdateC2SPacket packet);

	void onQueryEntityNbt(QueryEntityNbtC2SPacket packet);

	void onQueryBlockNbt(QueryBlockNbtC2SPacket packet);

	void onJigsawUpdate(UpdateJigsawC2SPacket packet);

	void onUpdateDifficulty(UpdateDifficultyC2SPacket updateDifficultyC2SPacket);

	void onUpdateDifficultyLock(UpdateDifficultyLockC2SPacket updateDifficultyLockC2SPacket);
}
