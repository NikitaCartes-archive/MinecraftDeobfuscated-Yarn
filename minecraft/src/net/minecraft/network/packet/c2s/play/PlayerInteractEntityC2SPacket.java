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
		this.entityId = 0;
		this.type = PlayerInteractEntityC2SPacket.InteractionType.field_26783;
	}

	@Environment(EnvType.CLIENT)
	public PlayerInteractEntityC2SPacket(Entity target, boolean playerSneaking) {
		this.entityId = target.getEntityId();
		this.type = PlayerInteractEntityC2SPacket.InteractionType.field_12875;
		this.playerSneaking = playerSneaking;
	}

	@Environment(EnvType.CLIENT)
	public PlayerInteractEntityC2SPacket(Entity entity, Hand hand, boolean playerSneaking) {
		this.entityId = entity.getEntityId();
		this.type = PlayerInteractEntityC2SPacket.InteractionType.field_12876;
		this.hand = hand;
		this.playerSneaking = playerSneaking;
	}

	@Environment(EnvType.CLIENT)
	public PlayerInteractEntityC2SPacket(Entity entity, Hand hand, Vec3d hitPos, boolean playerSneaking) {
		this.entityId = entity.getEntityId();
		this.type = PlayerInteractEntityC2SPacket.InteractionType.field_12873;
		this.hand = hand;
		this.hitPos = hitPos;
		this.playerSneaking = playerSneaking;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.entityId = buf.readVarInt();
		this.type = buf.readEnumConstant(PlayerInteractEntityC2SPacket.InteractionType.class);
		if (this.type == PlayerInteractEntityC2SPacket.InteractionType.field_12873) {
			this.hitPos = new Vec3d((double)buf.readFloat(), (double)buf.readFloat(), (double)buf.readFloat());
		}

		if (this.type == PlayerInteractEntityC2SPacket.InteractionType.field_12876 || this.type == PlayerInteractEntityC2SPacket.InteractionType.field_12873) {
			this.hand = buf.readEnumConstant(Hand.class);
		}

		this.playerSneaking = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.entityId);
		buf.writeEnumConstant(this.type);
		if (this.type == PlayerInteractEntityC2SPacket.InteractionType.field_12873) {
			buf.writeFloat((float)this.hitPos.x);
			buf.writeFloat((float)this.hitPos.y);
			buf.writeFloat((float)this.hitPos.z);
		}

		if (this.type == PlayerInteractEntityC2SPacket.InteractionType.field_12876 || this.type == PlayerInteractEntityC2SPacket.InteractionType.field_12873) {
			buf.writeEnumConstant(this.hand);
		}

		buf.writeBoolean(this.playerSneaking);
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
		field_12876,
		field_12875,
		field_12873,
		field_26783;
	}
}
