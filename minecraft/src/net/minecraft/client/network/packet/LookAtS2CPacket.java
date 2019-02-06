package net.minecraft.client.network.packet;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LookAtS2CPacket implements Packet<ClientPlayPacketListener> {
	private double targetX;
	private double targetY;
	private double targetZ;
	private int entityId;
	private EntityAnchorArgumentType.EntityAnchor selfAnchor;
	private EntityAnchorArgumentType.EntityAnchor targetAnchor;
	private boolean lookAtEntity;

	public LookAtS2CPacket() {
	}

	public LookAtS2CPacket(EntityAnchorArgumentType.EntityAnchor entityAnchor, double d, double e, double f) {
		this.selfAnchor = entityAnchor;
		this.targetX = d;
		this.targetY = e;
		this.targetZ = f;
	}

	public LookAtS2CPacket(EntityAnchorArgumentType.EntityAnchor entityAnchor, Entity entity, EntityAnchorArgumentType.EntityAnchor entityAnchor2) {
		this.selfAnchor = entityAnchor;
		this.entityId = entity.getEntityId();
		this.targetAnchor = entityAnchor2;
		Vec3d vec3d = entityAnchor2.positionAt(entity);
		this.targetX = vec3d.x;
		this.targetY = vec3d.y;
		this.targetZ = vec3d.z;
		this.lookAtEntity = true;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.selfAnchor = packetByteBuf.readEnumConstant(EntityAnchorArgumentType.EntityAnchor.class);
		this.targetX = packetByteBuf.readDouble();
		this.targetY = packetByteBuf.readDouble();
		this.targetZ = packetByteBuf.readDouble();
		if (packetByteBuf.readBoolean()) {
			this.lookAtEntity = true;
			this.entityId = packetByteBuf.readVarInt();
			this.targetAnchor = packetByteBuf.readEnumConstant(EntityAnchorArgumentType.EntityAnchor.class);
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.selfAnchor);
		packetByteBuf.writeDouble(this.targetX);
		packetByteBuf.writeDouble(this.targetY);
		packetByteBuf.writeDouble(this.targetZ);
		packetByteBuf.writeBoolean(this.lookAtEntity);
		if (this.lookAtEntity) {
			packetByteBuf.writeVarInt(this.entityId);
			packetByteBuf.writeEnumConstant(this.targetAnchor);
		}
	}

	public void method_11731(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11092(this);
	}

	@Environment(EnvType.CLIENT)
	public EntityAnchorArgumentType.EntityAnchor getSelfAnchor() {
		return this.selfAnchor;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Vec3d getTargetPosition(World world) {
		if (this.lookAtEntity) {
			Entity entity = world.getEntityById(this.entityId);
			return entity == null ? new Vec3d(this.targetX, this.targetY, this.targetZ) : this.targetAnchor.positionAt(entity);
		} else {
			return new Vec3d(this.targetX, this.targetY, this.targetZ);
		}
	}
}
