package net.minecraft.network.listener;

import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.common.CustomReportDetailsS2CPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackRemoveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.common.ServerLinksS2CPacket;
import net.minecraft.network.packet.s2c.common.ServerTransferS2CPacket;
import net.minecraft.network.packet.s2c.common.StoreCookieS2CPacket;
import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;

public interface ClientCommonPacketListener extends ClientCookieRequestPacketListener, ClientPacketListener {
	void onKeepAlive(KeepAliveS2CPacket packet);

	void onPing(CommonPingS2CPacket packet);

	void onCustomPayload(CustomPayloadS2CPacket packet);

	void onDisconnect(DisconnectS2CPacket packet);

	void onResourcePackSend(ResourcePackSendS2CPacket packet);

	void onResourcePackRemove(ResourcePackRemoveS2CPacket packet);

	void onSynchronizeTags(SynchronizeTagsS2CPacket packet);

	void onStoreCookie(StoreCookieS2CPacket packet);

	void onServerTransfer(ServerTransferS2CPacket packet);

	void onCustomReportDetails(CustomReportDetailsS2CPacket packet);

	void onServerLinks(ServerLinksS2CPacket packet);
}
