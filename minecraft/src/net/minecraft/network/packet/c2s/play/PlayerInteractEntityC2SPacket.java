package net.minecraft.network.packet.c2s.play;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class PlayerInteractEntityC2SPacket implements Packet<ServerPlayPacketListener> {
	private final int entityId;
	private final PlayerInteractEntityC2SPacket.class_5906 type;
	private final boolean playerSneaking;
	private static final PlayerInteractEntityC2SPacket.class_5906 field_29170 = new PlayerInteractEntityC2SPacket.class_5906() {
		@Override
		public PlayerInteractEntityC2SPacket.class_5907 method_34211() {
			return PlayerInteractEntityC2SPacket.class_5907.field_29172;
		}

		@Override
		public void method_34213(PlayerInteractEntityC2SPacket.class_5908 arg) {
			arg.method_34218();
		}

		@Override
		public void method_34212(PacketByteBuf packetByteBuf) {
		}
	};

	@Environment(EnvType.CLIENT)
	private PlayerInteractEntityC2SPacket(int i, boolean bl, PlayerInteractEntityC2SPacket.class_5906 arg) {
		this.entityId = i;
		this.type = arg;
		this.playerSneaking = bl;
	}

	@Environment(EnvType.CLIENT)
	public static PlayerInteractEntityC2SPacket method_34206(Entity entity, boolean bl) {
		return new PlayerInteractEntityC2SPacket(entity.getId(), bl, field_29170);
	}

	@Environment(EnvType.CLIENT)
	public static PlayerInteractEntityC2SPacket method_34207(Entity entity, boolean bl, Hand hand) {
		return new PlayerInteractEntityC2SPacket(entity.getId(), bl, new PlayerInteractEntityC2SPacket.class_5909(hand));
	}

	@Environment(EnvType.CLIENT)
	public static PlayerInteractEntityC2SPacket method_34208(Entity entity, boolean bl, Hand hand, Vec3d vec3d) {
		return new PlayerInteractEntityC2SPacket(entity.getId(), bl, new PlayerInteractEntityC2SPacket.class_5910(hand, vec3d));
	}

	public PlayerInteractEntityC2SPacket(PacketByteBuf packetByteBuf) {
		this.entityId = packetByteBuf.readVarInt();
		PlayerInteractEntityC2SPacket.class_5907 lv = packetByteBuf.readEnumConstant(PlayerInteractEntityC2SPacket.class_5907.class);
		this.type = (PlayerInteractEntityC2SPacket.class_5906)lv.field_29174.apply(packetByteBuf);
		this.playerSneaking = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeEnumConstant(this.type.method_34211());
		this.type.method_34212(buf);
		buf.writeBoolean(this.playerSneaking);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPlayerInteractEntity(this);
	}

	@Nullable
	public Entity getEntity(ServerWorld serverWorld) {
		return serverWorld.method_31424(this.entityId);
	}

	public boolean isPlayerSneaking() {
		return this.playerSneaking;
	}

	public void method_34209(PlayerInteractEntityC2SPacket.class_5908 arg) {
		this.type.method_34213(arg);
	}

	interface class_5906 {
		PlayerInteractEntityC2SPacket.class_5907 method_34211();

		void method_34213(PlayerInteractEntityC2SPacket.class_5908 arg);

		void method_34212(PacketByteBuf packetByteBuf);
	}

	static enum class_5907 {
		field_29171(packetByteBuf -> new PlayerInteractEntityC2SPacket.class_5909(packetByteBuf)),
		field_29172(packetByteBuf -> PlayerInteractEntityC2SPacket.field_29170),
		field_29173(packetByteBuf -> new PlayerInteractEntityC2SPacket.class_5910(packetByteBuf));

		private final Function<PacketByteBuf, PlayerInteractEntityC2SPacket.class_5906> field_29174;

		private class_5907(Function<PacketByteBuf, PlayerInteractEntityC2SPacket.class_5906> function) {
			this.field_29174 = function;
		}
	}

	public interface class_5908 {
		void method_34219(Hand hand);

		void method_34220(Hand hand, Vec3d vec3d);

		void method_34218();
	}

	static class class_5909 implements PlayerInteractEntityC2SPacket.class_5906 {
		private final Hand field_29176;

		@Environment(EnvType.CLIENT)
		private class_5909(Hand hand) {
			this.field_29176 = hand;
		}

		private class_5909(PacketByteBuf packetByteBuf) {
			this.field_29176 = packetByteBuf.readEnumConstant(Hand.class);
		}

		@Override
		public PlayerInteractEntityC2SPacket.class_5907 method_34211() {
			return PlayerInteractEntityC2SPacket.class_5907.field_29171;
		}

		@Override
		public void method_34213(PlayerInteractEntityC2SPacket.class_5908 arg) {
			arg.method_34219(this.field_29176);
		}

		@Override
		public void method_34212(PacketByteBuf packetByteBuf) {
			packetByteBuf.writeEnumConstant(this.field_29176);
		}
	}

	static class class_5910 implements PlayerInteractEntityC2SPacket.class_5906 {
		private final Hand field_29177;
		private final Vec3d field_29178;

		@Environment(EnvType.CLIENT)
		private class_5910(Hand hand, Vec3d vec3d) {
			this.field_29177 = hand;
			this.field_29178 = vec3d;
		}

		private class_5910(PacketByteBuf packetByteBuf) {
			this.field_29178 = new Vec3d((double)packetByteBuf.readFloat(), (double)packetByteBuf.readFloat(), (double)packetByteBuf.readFloat());
			this.field_29177 = packetByteBuf.readEnumConstant(Hand.class);
		}

		@Override
		public PlayerInteractEntityC2SPacket.class_5907 method_34211() {
			return PlayerInteractEntityC2SPacket.class_5907.field_29173;
		}

		@Override
		public void method_34213(PlayerInteractEntityC2SPacket.class_5908 arg) {
			arg.method_34220(this.field_29177, this.field_29178);
		}

		@Override
		public void method_34212(PacketByteBuf packetByteBuf) {
			packetByteBuf.writeFloat((float)this.field_29178.x);
			packetByteBuf.writeFloat((float)this.field_29178.y);
			packetByteBuf.writeFloat((float)this.field_29178.z);
			packetByteBuf.writeEnumConstant(this.field_29177);
		}
	}
}
