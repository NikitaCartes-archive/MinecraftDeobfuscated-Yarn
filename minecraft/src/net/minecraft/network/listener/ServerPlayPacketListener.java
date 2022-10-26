package net.minecraft.network.listener;

import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.network.packet.c2s.play.BoatPaddleStateC2SPacket;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.JigsawGeneratingC2SPacket;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.MessageAcknowledgmentC2SPacket;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayPongC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerSessionC2SPacket;
import net.minecraft.network.packet.c2s.play.QueryBlockNbtC2SPacket;
import net.minecraft.network.packet.c2s.play.QueryEntityNbtC2SPacket;
import net.minecraft.network.packet.c2s.play.RecipeBookDataC2SPacket;
import net.minecraft.network.packet.c2s.play.RecipeCategoryOptionsC2SPacket;
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockMinecartC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyLockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateJigsawC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateStructureBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;

/**
 * A server side packet listener where play stage packets from a client are processed.
 */
public interface ServerPlayPacketListener extends ServerPacketListener {
	void onHandSwing(HandSwingC2SPacket packet);

	void onChatMessage(ChatMessageC2SPacket packet);

	void onCommandExecution(CommandExecutionC2SPacket packet);

	void onMessageAcknowledgment(MessageAcknowledgmentC2SPacket packet);

	void onClientStatus(ClientStatusC2SPacket packet);

	void onClientSettings(ClientSettingsC2SPacket packet);

	void onButtonClick(ButtonClickC2SPacket packet);

	void onClickSlot(ClickSlotC2SPacket packet);

	void onCraftRequest(CraftRequestC2SPacket packet);

	void onCloseHandledScreen(CloseHandledScreenC2SPacket packet);

	void onCustomPayload(CustomPayloadC2SPacket packet);

	void onPlayerInteractEntity(PlayerInteractEntityC2SPacket packet);

	void onKeepAlive(KeepAliveC2SPacket packet);

	void onPlayerMove(PlayerMoveC2SPacket packet);

	void onPong(PlayPongC2SPacket packet);

	void onUpdatePlayerAbilities(UpdatePlayerAbilitiesC2SPacket packet);

	void onPlayerAction(PlayerActionC2SPacket packet);

	void onClientCommand(ClientCommandC2SPacket packet);

	void onPlayerInput(PlayerInputC2SPacket packet);

	void onUpdateSelectedSlot(UpdateSelectedSlotC2SPacket packet);

	void onCreativeInventoryAction(CreativeInventoryActionC2SPacket packet);

	void onUpdateSign(UpdateSignC2SPacket packet);

	void onPlayerInteractBlock(PlayerInteractBlockC2SPacket packet);

	void onPlayerInteractItem(PlayerInteractItemC2SPacket packet);

	void onSpectatorTeleport(SpectatorTeleportC2SPacket packet);

	void onResourcePackStatus(ResourcePackStatusC2SPacket packet);

	void onBoatPaddleState(BoatPaddleStateC2SPacket packet);

	void onVehicleMove(VehicleMoveC2SPacket packet);

	void onTeleportConfirm(TeleportConfirmC2SPacket packet);

	void onRecipeBookData(RecipeBookDataC2SPacket packet);

	void onRecipeCategoryOptions(RecipeCategoryOptionsC2SPacket packet);

	void onAdvancementTab(AdvancementTabC2SPacket packet);

	void onRequestCommandCompletions(RequestCommandCompletionsC2SPacket packet);

	void onUpdateCommandBlock(UpdateCommandBlockC2SPacket packet);

	void onUpdateCommandBlockMinecart(UpdateCommandBlockMinecartC2SPacket packet);

	void onPickFromInventory(PickFromInventoryC2SPacket packet);

	void onRenameItem(RenameItemC2SPacket packet);

	void onUpdateBeacon(UpdateBeaconC2SPacket packet);

	void onUpdateStructureBlock(UpdateStructureBlockC2SPacket packet);

	void onSelectMerchantTrade(SelectMerchantTradeC2SPacket packet);

	void onBookUpdate(BookUpdateC2SPacket packet);

	void onQueryEntityNbt(QueryEntityNbtC2SPacket packet);

	void onQueryBlockNbt(QueryBlockNbtC2SPacket packet);

	void onUpdateJigsaw(UpdateJigsawC2SPacket packet);

	void onJigsawGenerating(JigsawGeneratingC2SPacket packet);

	void onUpdateDifficulty(UpdateDifficultyC2SPacket packet);

	void onUpdateDifficultyLock(UpdateDifficultyLockC2SPacket packet);

	void onPlayerSession(PlayerSessionC2SPacket packet);
}
