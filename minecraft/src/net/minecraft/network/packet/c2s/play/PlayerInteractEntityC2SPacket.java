package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PlayerInteractEntityC2SPacket implements Packet<ServerPlayPacketListener> {
	private int entityId;
	private PlayerInteractEntityC2SPacket.InteractionType type;
	private Vec3d hitPos;
	private Hand hand;
	private boolean playerSneaking;

	public PlayerInteractEntityC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public PlayerInteractEntityC2SPacket(Entity target, boolean playerSneaking) {
		this.entityId = target.getEntityId();
		this.type = PlayerInteractEntityC2SPacket.InteractionType.ATTACK;
		this.playerSneaking = playerSneaking;
	}

	@Environment(EnvType.CLIENT)
	public PlayerInteractEntityC2SPacket(Entity entity, Hand hand, boolean playerSneaking) {
		this.entityId = entity.getEntityId();
		this.type = PlayerInteractEntityC2SPacket.InteractionType.INTERACT;
		this.hand = hand;
		this.playerSneaking = playerSneaking;
	}

	@Environment(EnvType.CLIENT)
	public PlayerInteractEntityC2SPacket(Entity entity, Hand hand, Vec3d hitPos, boolean playerSneaking) {
		this.entityId = entity.getEntityId();
		this.type = PlayerInteractEntityC2SPacket.InteractionType.INTERACT_AT;
		this.hand = hand;
		this.hitPos = hitPos;
		this.playerSneaking = playerSneaking;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.entityId = buf.readVarInt();
		this.type = buf.readEnumConstant(PlayerInteractEntityC2SPacket.InteractionType.class);
		if (this.type == PlayerInteractEntityC2SPacket.InteractionType.INTERACT_AT) {
			this.hitPos = new Vec3d((double)buf.readFloat(), (double)buf.readFloat(), (double)buf.readFloat());
		}

		if (this.type == PlayerInteractEntityC2SPacket.InteractionType.INTERACT || this.type == PlayerInteractEntityC2SPacket.InteractionType.INTERACT_AT) {
			this.hand = buf.readEnumConstant(Hand.class);
		}

		this.playerSneaking = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.entityId);
		buf.writeEnumConstant(this.type);
		if (this.type == PlayerInteractEntityC2SPacket.InteractionType.INTERACT_AT) {
			buf.writeFloat((float)this.hitPos.x);
			buf.writeFloat((float)this.hitPos.y);
			buf.writeFloat((float)this.hitPos.z);
		}

		if (this.type == PlayerInteractEntityC2SPacket.InteractionType.INTERACT || this.type == PlayerInteractEntityC2SPacket.InteractionType.INTERACT_AT) {
			buf.writeEnumConstant(this.hand);
		}

		buf.writeBoolean(this.playerSneaking);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPlayerInteractEntity(this);
	}

	@Nullable
	public Entity getEntity(World world) {
		return world.getEntityById(this.entityId);
	}

	public PlayerInteractEntityC2SPacket.InteractionType getType() {
		return this.type;
	}

	@Nullable
	public Hand getHand() {
		return this.hand;
	}

	public Vec3d getHitPosition() {
		return this.hitPos;
	}

	public boolean isPlayerSneaking() {
		return this.playerSneaking;
	}

	public static enum InteractionType {
		INTERACT,
		ATTACK,
		INTERACT_AT;
	}
}
