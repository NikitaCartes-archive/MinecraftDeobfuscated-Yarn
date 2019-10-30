package net.minecraft.client.network.packet;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ExplosionS2CPacket implements Packet<ClientPlayPacketListener> {
	private double x;
	private double y;
	private double z;
	private float radius;
	private List<BlockPos> affectedBlocks;
	private float playerVelocityX;
	private float playerVelocityY;
	private float playerVelocityZ;

	public ExplosionS2CPacket() {
	}

	public ExplosionS2CPacket(double x, double y, double z, float radius, List<BlockPos> affectedBlocks, Vec3d playerVelocity) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.radius = radius;
		this.affectedBlocks = Lists.<BlockPos>newArrayList(affectedBlocks);
		if (playerVelocity != null) {
			this.playerVelocityX = (float)playerVelocity.x;
			this.playerVelocityY = (float)playerVelocity.y;
			this.playerVelocityZ = (float)playerVelocity.z;
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.x = (double)buf.readFloat();
		this.y = (double)buf.readFloat();
		this.z = (double)buf.readFloat();
		this.radius = buf.readFloat();
		int i = buf.readInt();
		this.affectedBlocks = Lists.<BlockPos>newArrayListWithCapacity(i);
		int j = MathHelper.floor(this.x);
		int k = MathHelper.floor(this.y);
		int l = MathHelper.floor(this.z);

		for (int m = 0; m < i; m++) {
			int n = buf.readByte() + j;
			int o = buf.readByte() + k;
			int p = buf.readByte() + l;
			this.affectedBlocks.add(new BlockPos(n, o, p));
		}

		this.playerVelocityX = buf.readFloat();
		this.playerVelocityY = buf.readFloat();
		this.playerVelocityZ = buf.readFloat();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeFloat((float)this.x);
		buf.writeFloat((float)this.y);
		buf.writeFloat((float)this.z);
		buf.writeFloat(this.radius);
		buf.writeInt(this.affectedBlocks.size());
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.y);
		int k = MathHelper.floor(this.z);

		for (BlockPos blockPos : this.affectedBlocks) {
			int l = blockPos.getX() - i;
			int m = blockPos.getY() - j;
			int n = blockPos.getZ() - k;
			buf.writeByte(l);
			buf.writeByte(m);
			buf.writeByte(n);
		}

		buf.writeFloat(this.playerVelocityX);
		buf.writeFloat(this.playerVelocityY);
		buf.writeFloat(this.playerVelocityZ);
	}

	public void method_11480(ClientPlayPacketListener clientPlayPacketListener) {
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
