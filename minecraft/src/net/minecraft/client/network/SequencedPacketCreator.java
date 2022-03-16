package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface SequencedPacketCreator {
	Packet<ServerPlayPacketListener> predict(int sequence);
}
