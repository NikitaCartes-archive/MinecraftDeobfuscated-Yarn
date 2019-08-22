/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.listener;

import net.minecraft.network.listener.PacketListener;
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

public interface ServerPlayPacketListener
extends PacketListener {
    public void onHandSwing(HandSwingC2SPacket var1);

    public void onChatMessage(ChatMessageC2SPacket var1);

    public void onClientStatus(ClientStatusC2SPacket var1);

    public void onClientSettings(ClientSettingsC2SPacket var1);

    public void onConfirmTransaction(GuiActionConfirmC2SPacket var1);

    public void onButtonClick(ButtonClickC2SPacket var1);

    public void onClickWindow(ClickWindowC2SPacket var1);

    public void onCraftRequest(CraftRequestC2SPacket var1);

    public void onGuiClose(GuiCloseC2SPacket var1);

    public void onCustomPayload(CustomPayloadC2SPacket var1);

    public void onPlayerInteractEntity(PlayerInteractEntityC2SPacket var1);

    public void onKeepAlive(KeepAliveC2SPacket var1);

    public void onPlayerMove(PlayerMoveC2SPacket var1);

    public void onPlayerAbilities(UpdatePlayerAbilitiesC2SPacket var1);

    public void onPlayerAction(PlayerActionC2SPacket var1);

    public void onClientCommand(ClientCommandC2SPacket var1);

    public void onPlayerInput(PlayerInputC2SPacket var1);

    public void onUpdateSelectedSlot(UpdateSelectedSlotC2SPacket var1);

    public void onCreativeInventoryAction(CreativeInventoryActionC2SPacket var1);

    public void onSignUpdate(UpdateSignC2SPacket var1);

    public void onPlayerInteractBlock(PlayerInteractBlockC2SPacket var1);

    public void onPlayerInteractItem(PlayerInteractItemC2SPacket var1);

    public void onSpectatorTeleport(SpectatorTeleportC2SPacket var1);

    public void onResourcePackStatus(ResourcePackStatusC2SPacket var1);

    public void onBoatPaddleState(BoatPaddleStateC2SPacket var1);

    public void onVehicleMove(VehicleMoveC2SPacket var1);

    public void onTeleportConfirm(TeleportConfirmC2SPacket var1);

    public void onRecipeBookData(RecipeBookDataC2SPacket var1);

    public void onAdvancementTab(AdvancementTabC2SPacket var1);

    public void onRequestCommandCompletions(RequestCommandCompletionsC2SPacket var1);

    public void onUpdateCommandBlock(UpdateCommandBlockC2SPacket var1);

    public void onUpdateCommandBlockMinecart(UpdateCommandBlockMinecartC2SPacket var1);

    public void onPickFromInventory(PickFromInventoryC2SPacket var1);

    public void onRenameItem(RenameItemC2SPacket var1);

    public void onUpdateBeacon(UpdateBeaconC2SPacket var1);

    public void onStructureBlockUpdate(UpdateStructureBlockC2SPacket var1);

    public void onVillagerTradeSelect(SelectVillagerTradeC2SPacket var1);

    public void onBookUpdate(BookUpdateC2SPacket var1);

    public void onQueryEntityNbt(QueryEntityNbtC2SPacket var1);

    public void onQueryBlockNbt(QueryBlockNbtC2SPacket var1);

    public void onJigsawUpdate(UpdateJigsawC2SPacket var1);

    public void onUpdateDifficulty(UpdateDifficultyC2SPacket var1);

    public void onUpdateDifficultyLock(UpdateDifficultyLockC2SPacket var1);
}

