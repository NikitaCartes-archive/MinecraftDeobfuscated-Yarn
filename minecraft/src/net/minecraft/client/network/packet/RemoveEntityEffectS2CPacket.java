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

	public RemoveEntityEffectS2CPacket(int entityId, StatusEffect effectType) {
		this.entityId = entityId;
		this.effectType = effectType;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.entityId = buf.readVarInt();
		this.effectType = StatusEffect.byRawId(buf.readUnsignedByte());
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.entityId);
		buf.writeByte(StatusEffect.getRawId(this.effectType));
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onRemoveEntityEffect(this);
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
