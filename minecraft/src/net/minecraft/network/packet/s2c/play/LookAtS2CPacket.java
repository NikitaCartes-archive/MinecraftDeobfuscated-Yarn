package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LookAtS2CPacket implements Packet<ClientPlayPacketListener> {
	private final double targetX;
	private final double targetY;
	private final double targetZ;
	private final int entityId;
	private final EntityAnchorArgumentType.EntityAnchor selfAnchor;
	private final EntityAnchorArgumentType.EntityAnchor targetAnchor;
	private final boolean lookAtEntity;

	public LookAtS2CPacket(EntityAnchorArgumentType.EntityAnchor selfAnchor, double targetX, double targetY, double targetZ) {
		this.selfAnchor = selfAnchor;
		this.targetX = targetX;
		this.targetY = targetY;
		this.targetZ = targetZ;
		this.entityId = 0;
		this.lookAtEntity = false;
		this.targetAnchor = null;
	}

	public LookAtS2CPacket(EntityAnchorArgumentType.EntityAnchor selfAnchor, Entity entity, EntityAnchorArgumentType.EntityAnchor targetAnchor) {
		this.selfAnchor = selfAnchor;
		this.entityId = entity.getId();
		this.targetAnchor = targetAnchor;
		Vec3d vec3d = targetAnchor.positionAt(entity);
		this.targetX = vec3d.x;
		this.targetY = vec3d.y;
		this.targetZ = vec3d.z;
		this.lookAtEntity = true;
	}

	public LookAtS2CPacket(PacketByteBuf buf) {
		this.selfAnchor = buf.readEnumConstant(EntityAnchorArgumentType.EntityAnchor.class);
		this.targetX = buf.readDouble();
		this.targetY = buf.readDouble();
		this.targetZ = buf.readDouble();
		this.lookAtEntity = buf.readBoolean();
		if (this.lookAtEntity) {
			this.entityId = buf.readVarInt();
			this.targetAnchor = buf.readEnumConstant(EntityAnchorArgumentType.EntityAnchor.class);
		} else {
			this.entityId = 0;
			this.targetAnchor = null;
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeEnumConstant(this.selfAnchor);
		buf.writeDouble(this.targetX);
		buf.writeDouble(this.targetY);
		buf.writeDouble(this.targetZ);
		buf.writeBoolean(this.lookAtEntity);
		if (this.lookAtEntity) {
			buf.writeVarInt(this.entityId);
			buf.writeEnumConstant(this.targetAnchor);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onLookAt(this);
	}

	public EntityAnchorArgumentType.EntityAnchor getSelfAnchor() {
		return this.selfAnchor;
	}

	@Nullable
	public Vec3d getTargetPosition(World world) {
		if (this.lookAtEntity) {
			Entity entity = world.getEntityById(this.entityId);
			return entity == null ? new Vec3d(this.targetX, this.targetY, this.targetZ) : this.targetAnchor.positionAt(entity);
		} else {
			return new Vec3d(this.targetX, this.targetY, this.targetZ);
		}
	}
}
