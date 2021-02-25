package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ExplosionS2CPacket implements Packet<ClientPlayPacketListener> {
	private final double x;
	private final double y;
	private final double z;
	private final float radius;
	private final List<BlockPos> affectedBlocks;
	private final float playerVelocityX;
	private final float playerVelocityY;
	private final float playerVelocityZ;

	public ExplosionS2CPacket(double x, double y, double z, float radius, List<BlockPos> affectedBlocks, @Nullable Vec3d playerVelocity) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.radius = radius;
		this.affectedBlocks = Lists.<BlockPos>newArrayList(affectedBlocks);
		if (playerVelocity != null) {
			this.playerVelocityX = (float)playerVelocity.x;
			this.playerVelocityY = (float)playerVelocity.y;
			this.playerVelocityZ = (float)playerVelocity.z;
		} else {
			this.playerVelocityX = 0.0F;
			this.playerVelocityY = 0.0F;
			this.playerVelocityZ = 0.0F;
		}
	}

	public ExplosionS2CPacket(PacketByteBuf buf) {
		this.x = (double)buf.readFloat();
		this.y = (double)buf.readFloat();
		this.z = (double)buf.readFloat();
		this.radius = buf.readFloat();
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.y);
		int k = MathHelper.floor(this.z);
		this.affectedBlocks = buf.readList(packetByteBuf -> {
			int l = packetByteBuf.readByte() + i;
			int m = packetByteBuf.readByte() + j;
			int n = packetByteBuf.readByte() + k;
			return new BlockPos(l, m, n);
		});
		this.playerVelocityX = buf.readFloat();
		this.playerVelocityY = buf.readFloat();
		this.playerVelocityZ = buf.readFloat();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeFloat((float)this.x);
		buf.writeFloat((float)this.y);
		buf.writeFloat((float)this.z);
		buf.writeFloat(this.radius);
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.y);
		int k = MathHelper.floor(this.z);
		buf.writeCollection(this.affectedBlocks, (bufx, pos) -> {
			int l = pos.getX() - i;
			int m = pos.getY() - j;
			int n = pos.getZ() - k;
			bufx.writeByte(l);
			bufx.writeByte(m);
			bufx.writeByte(n);
		});
		buf.writeFloat(this.playerVelocityX);
		buf.writeFloat(this.playerVelocityY);
		buf.writeFloat(this.playerVelocityZ);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onExplosion(this);
	}

	@Environment(EnvType.CLIENT)
	public float getPlayerVelocityX() {
		return this.playerVelocityX;
	}

	@Environment(EnvType.CLIENT)
	public float getPlayerVelocityY() {
		return this.playerVelocityY;
	}

	@Environment(EnvType.CLIENT)
	public float getPlayerVelocityZ() {
		return this.playerVelocityZ;
	}

	@Environment(EnvType.CLIENT)
	public double getX() {
		return this.x;
	}

	@Environment(EnvType.CLIENT)
	public double getY() {
		return this.y;
	}

	@Environment(EnvType.CLIENT)
	public double getZ() {
		return this.z;
	}

	@Environment(EnvType.CLIENT)
	public float getRadius() {
		return this.radius;
	}

	@Environment(EnvType.CLIENT)
	public List<BlockPos> getAffectedBlocks() {
		return this.affectedBlocks;
	}
}
