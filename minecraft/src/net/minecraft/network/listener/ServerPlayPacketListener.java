package net.minecraft.network.listener;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ButtonClickServerPacket;
import net.minecraft.server.network.packet.AdvancementTabC2SPacket;
import net.minecraft.server.network.packet.BoatPaddleStateC2SPacket;
import net.minecraft.server.network.packet.BookUpdateC2SPacket;
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
import net.minecraft.server.network.packet.PlayerInteractBlockC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractEntityC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractItemC2SPacket;
import net.minecraft.server.network.packet.PlayerLookC2SPacket;
import net.minecraft.server.network.packet.PlayerMoveServerMessage;
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
import net.minecraft.server.network.packet.UpdateJigsawC2SPacket;
import net.minecraft.server.network.packet.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.server.network.packet.UpdateSelectedSlotC2SPacket;
import net.minecraft.server.network.packet.UpdateSignC2SPacket;
import net.minecraft.server.network.packet.UpdateStructureBlockC2SPacket;
import net.minecraft.server.network.packet.VehicleMoveC2SPacket;

public interface ServerPlayPacketListener extends PacketListener {
	void method_12052(HandSwingC2SPacket handSwingC2SPacket);

	void method_12048(ChatMessageC2SPacket chatMessageC2SPacket);

	void method_12068(ClientStatusC2SPacket clientStatusC2SPacket);

	void method_12069(ClientSettingsC2SPacket clientSettingsC2SPacket);

	void method_12079(GuiActionConfirmC2SPacket guiActionConfirmC2SPacket);

	void onButtonClick(ButtonClickServerPacket buttonClickServerPacket);

	void method_12076(ClickWindowC2SPacket clickWindowC2SPacket);

	void method_12061(CraftRequestC2SPacket craftRequestC2SPacket);

	void method_12054(GuiCloseC2SPacket guiCloseC2SPacket);

	void method_12075(CustomPayloadC2SPacket customPayloadC2SPacket);

	void method_12062(PlayerInteractEntityC2SPacket playerInteractEntityC2SPacket);

	void method_12082(KeepAliveC2SPacket keepAliveC2SPacket);

	void onPlayerMove(PlayerMoveServerMessage playerMoveServerMessage);

	void method_12083(UpdatePlayerAbilitiesC2SPacket updatePlayerAbilitiesC2SPacket);

	void method_12066(PlayerActionC2SPacket playerActionC2SPacket);

	void method_12045(ClientCommandC2SPacket clientCommandC2SPacket);

	void method_12067(PlayerLookC2SPacket playerLookC2SPacket);

	void method_12056(UpdateSelectedSlotC2SPacket updateSelectedSlotC2SPacket);

	void method_12070(CreativeInventoryActionC2SPacket creativeInventoryActionC2SPacket);

	void method_12071(UpdateSignC2SPacket updateSignC2SPacket);

	void method_12046(PlayerInteractBlockC2SPacket playerInteractBlockC2SPacket);

	void method_12065(PlayerInteractItemC2SPacket playerInteractItemC2SPacket);

	void method_12073(SpectatorTeleportC2SPacket spectatorTeleportC2SPacket);

	void method_12081(ResourcePackStatusC2SPacket resourcePackStatusC2SPacket);

	void method_12064(BoatPaddleStateC2SPacket boatPaddleStateC2SPacket);

	void method_12078(VehicleMoveC2SPacket vehicleMoveC2SPacket);

	void method_12050(TeleportConfirmC2SPacket teleportConfirmC2SPacket);

	void method_12047(RecipeBookDataC2SPacket recipeBookDataC2SPacket);

	void method_12058(AdvancementTabC2SPacket advancementTabC2SPacket);

	void method_12059(RequestCommandCompletionsC2SPacket requestCommandCompletionsC2SPacket);

	void method_12077(UpdateCommandBlockC2SPacket updateCommandBlockC2SPacket);

	void method_12049(UpdateCommandBlockMinecartC2SPacket updateCommandBlockMinecartC2SPacket);

	void method_12084(PickFromInventoryC2SPacket pickFromInventoryC2SPacket);

	void method_12060(RenameItemC2SPacket renameItemC2SPacket);

	void method_12057(UpdateBeaconC2SPacket updateBeaconC2SPacket);

	void method_12051(UpdateStructureBlockC2SPacket updateStructureBlockC2SPacket);

	void method_12080(SelectVillagerTradeC2SPacket selectVillagerTradeC2SPacket);

	void method_12053(BookUpdateC2SPacket bookUpdateC2SPacket);

	void method_12074(QueryEntityNbtC2SPacket queryEntityNbtC2SPacket);

	void method_12072(QueryBlockNbtC2SPacket queryBlockNbtC2SPacket);

	@Environment(EnvType.CLIENT)
	void method_16383(UpdateJigsawC2SPacket updateJigsawC2SPacket);
}
