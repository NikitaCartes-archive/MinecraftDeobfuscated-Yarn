package net.minecraft.server.network.packet;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PlayerInteractEntityServerPacket implements Packet<ServerPlayPacketListener> {
	private int entityId;
	private PlayerInteractEntityServerPacket.InteractionType type;
	private Vec3d hitPos;
	private Hand hand;

	public PlayerInteractEntityServerPacket() {
	}

	public PlayerInteractEntityServerPacket(Entity entity) {
		this.entityId = entity.getEntityId();
		this.type = PlayerInteractEntityServerPacket.InteractionType.field_12875;
	}

	@Environment(EnvType.CLIENT)
	public PlayerInteractEntityServerPacket(Entity entity, Hand hand) {
		this.entityId = entity.getEntityId();
		this.type = PlayerInteractEntityServerPacket.InteractionType.field_12876;
		this.hand = hand;
	}

	@Environment(EnvType.CLIENT)
	public PlayerInteractEntityServerPacket(Entity entity, Hand hand, Vec3d vec3d) {
		this.entityId = entity.getEntityId();
		this.type = PlayerInteractEntityServerPacket.InteractionType.field_12873;
		this.hand = hand;
		this.hitPos = vec3d;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.entityId = packetByteBuf.readVarInt();
		this.type = packetByteBuf.readEnumConstant(PlayerInteractEntityServerPacket.InteractionType.class);
		if (this.type == PlayerInteractEntityServerPacket.InteractionType.field_12873) {
			this.hitPos = new Vec3d((double)packetByteBuf.readFloat(), (double)packetByteBuf.readFloat(), (double)packetByteBuf.readFloat());
		}

		if (this.type == PlayerInteractEntityServerPacket.InteractionType.field_12876 || this.type == PlayerInteractEntityServerPacket.InteractionType.field_12873) {
			this.hand = packetByteBuf.readEnumConstant(Hand.class);
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.entityId);
		packetByteBuf.writeEnumConstant(this.type);
		if (this.type == PlayerInteractEntityServerPacket.InteractionType.field_12873) {
			packetByteBuf.writeFloat((float)this.hitPos.x);
			packetByteBuf.writeFloat((float)this.hitPos.y);
			packetByteBuf.writeFloat((float)this.hitPos.z);
		}

		if (this.type == PlayerInteractEntityServerPacket.InteractionType.field_12876 || this.type == PlayerInteractEntityServerPacket.InteractionType.field_12873) {
			packetByteBuf.writeEnumConstant(this.hand);
		}
	}

	public void method_12251(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPlayerInteractEntity(this);
	}

	@Nullable
	public Entity getEntity(World world) {
		return world.getEntityById(this.entityId);
	}

	public PlayerInteractEntityServerPacket.InteractionType getType() {
		return this.type;
	}

	public Hand getHand() {
		return this.hand;
	}

	public Vec3d getHitPosition() {
		return this.hitPos;
	}

	public static enum InteractionType {
		field_12876,
		field_12875,
		field_12873;
	}
}
