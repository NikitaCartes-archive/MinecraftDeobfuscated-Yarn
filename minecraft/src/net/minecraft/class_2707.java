package net.minecraft;

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

public class class_2707 implements Packet<ClientPlayPacketListener> {
	private double field_12386;
	private double field_12384;
	private double field_12383;
	private int field_12388;
	private EntityAnchorArgumentType.EntityAnchor field_12385;
	private EntityAnchorArgumentType.EntityAnchor field_12389;
	private boolean field_12387;

	public class_2707() {
	}

	public class_2707(EntityAnchorArgumentType.EntityAnchor entityAnchor, double d, double e, double f) {
		this.field_12385 = entityAnchor;
		this.field_12386 = d;
		this.field_12384 = e;
		this.field_12383 = f;
	}

	public class_2707(EntityAnchorArgumentType.EntityAnchor entityAnchor, Entity entity, EntityAnchorArgumentType.EntityAnchor entityAnchor2) {
		this.field_12385 = entityAnchor;
		this.field_12388 = entity.getEntityId();
		this.field_12389 = entityAnchor2;
		Vec3d vec3d = entityAnchor2.positionAt(entity);
		this.field_12386 = vec3d.x;
		this.field_12384 = vec3d.y;
		this.field_12383 = vec3d.z;
		this.field_12387 = true;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12385 = packetByteBuf.readEnumConstant(EntityAnchorArgumentType.EntityAnchor.class);
		this.field_12386 = packetByteBuf.readDouble();
		this.field_12384 = packetByteBuf.readDouble();
		this.field_12383 = packetByteBuf.readDouble();
		if (packetByteBuf.readBoolean()) {
			this.field_12387 = true;
			this.field_12388 = packetByteBuf.readVarInt();
			this.field_12389 = packetByteBuf.readEnumConstant(EntityAnchorArgumentType.EntityAnchor.class);
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.field_12385);
		packetByteBuf.writeDouble(this.field_12386);
		packetByteBuf.writeDouble(this.field_12384);
		packetByteBuf.writeDouble(this.field_12383);
		packetByteBuf.writeBoolean(this.field_12387);
		if (this.field_12387) {
			packetByteBuf.writeVarInt(this.field_12388);
			packetByteBuf.writeEnumConstant(this.field_12389);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11092(this);
	}

	@Environment(EnvType.CLIENT)
	public EntityAnchorArgumentType.EntityAnchor method_11730() {
		return this.field_12385;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Vec3d method_11732(World world) {
		if (this.field_12387) {
			Entity entity = world.getEntityById(this.field_12388);
			return entity == null ? new Vec3d(this.field_12386, this.field_12384, this.field_12383) : this.field_12389.positionAt(entity);
		} else {
			return new Vec3d(this.field_12386, this.field_12384, this.field_12383);
		}
	}
}
