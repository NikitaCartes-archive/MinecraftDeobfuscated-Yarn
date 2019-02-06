package net.minecraft.client.network.packet;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.World;

public class RemoveEntityEffectS2CPacket implements Packet<ClientPlayPacketListener> {
	private int entityId;
	private StatusEffect effectType;

	public RemoveEntityEffectS2CPacket() {
	}

	public RemoveEntityEffectS2CPacket(int i, StatusEffect statusEffect) {
		this.entityId = i;
		this.effectType = statusEffect;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.entityId = packetByteBuf.readVarInt();
		this.effectType = StatusEffect.byRawId(packetByteBuf.readUnsignedByte());
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.entityId);
		packetByteBuf.writeByte(StatusEffect.getRawId(this.effectType));
	}

	public void method_11769(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11119(this);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Entity getEntity(World world) {
		return world.getEntityById(this.entityId);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public StatusEffect getEffectType() {
		return this.effectType;
	}
}
