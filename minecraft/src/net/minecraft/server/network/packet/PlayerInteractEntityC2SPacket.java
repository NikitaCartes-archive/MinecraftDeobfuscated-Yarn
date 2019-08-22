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

public class PlayerInteractEntityC2SPacket implements Packet<ServerPlayPacketListener> {
	private int entityId;
	private PlayerInteractEntityC2SPacket.InteractionType type;
	private Vec3d hitPos;
	private Hand hand;

	public PlayerInteractEntityC2SPacket() {
	}

	public PlayerInteractEntityC2SPacket(Entity entity) {
		this.entityId = entity.getEntityId();
		this.type = PlayerInteractEntityC2SPacket.InteractionType.ATTACK;
	}

	@Environment(EnvType.CLIENT)
	public PlayerInteractEntityC2SPacket(Entity entity, Hand hand) {
		this.entityId = entity.getEntityId();
		this.type = PlayerInteractEntityC2SPacket.InteractionType.INTERACT;
		this.hand = hand;
	}

	@Environment(EnvType.CLIENT)
	public PlayerInteractEntityC2SPacket(Entity entity, Hand hand, Vec3d vec3d) {
		this.entityId = entity.getEntityId();
		this.type = PlayerInteractEntityC2SPacket.InteractionType.INTERACT_AT;
		this.hand = hand;
		this.hitPos = vec3d;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.entityId = packetByteBuf.readVarInt();
		this.type = packetByteBuf.readEnumConstant(PlayerInteractEntityC2SPacket.InteractionType.class);
		if (this.type == PlayerInteractEntityC2SPacket.InteractionType.INTERACT_AT) {
			this.hitPos = new Vec3d((double)packetByteBuf.readFloat(), (double)packetByteBuf.readFloat(), (double)packetByteBuf.readFloat());
		}

		if (this.type == PlayerInteractEntityC2SPacket.InteractionType.INTERACT || this.type == PlayerInteractEntityC2SPacket.InteractionType.INTERACT_AT) {
			this.hand = packetByteBuf.readEnumConstant(Hand.class);
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.entityId);
		packetByteBuf.writeEnumConstant(this.type);
		if (this.type == PlayerInteractEntityC2SPacket.InteractionType.INTERACT_AT) {
			packetByteBuf.writeFloat((float)this.hitPos.x);
			packetByteBuf.writeFloat((float)this.hitPos.y);
			packetByteBuf.writeFloat((float)this.hitPos.z);
		}

		if (this.type == PlayerInteractEntityC2SPacket.InteractionType.INTERACT || this.type == PlayerInteractEntityC2SPacket.InteractionType.INTERACT_AT) {
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

	public PlayerInteractEntityC2SPacket.InteractionType getType() {
		return this.type;
	}

	public Hand getHand() {
		return this.hand;
	}

	public Vec3d getHitPosition() {
		return this.hitPos;
	}

	public static enum InteractionType {
		INTERACT,
		ATTACK,
		INTERACT_AT;
	}
}
