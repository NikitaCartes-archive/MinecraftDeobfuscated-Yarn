package net.minecraft.network.packet.c2s.play;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class PlayerInteractEntityC2SPacket implements Packet<ServerPlayPacketListener> {
	private final int entityId;
	private final PlayerInteractEntityC2SPacket.InteractTypeHandler type;
	private final boolean playerSneaking;
	static final PlayerInteractEntityC2SPacket.InteractTypeHandler ATTACK = new PlayerInteractEntityC2SPacket.InteractTypeHandler() {
		@Override
		public PlayerInteractEntityC2SPacket.InteractType getType() {
			return PlayerInteractEntityC2SPacket.InteractType.ATTACK;
		}

		@Override
		public void handle(PlayerInteractEntityC2SPacket.Handler handler) {
			handler.attack();
		}

		@Override
		public void write(PacketByteBuf buf) {
		}
	};

	private PlayerInteractEntityC2SPacket(int entityId, boolean playerSneaking, PlayerInteractEntityC2SPacket.InteractTypeHandler type) {
		this.entityId = entityId;
		this.type = type;
		this.playerSneaking = playerSneaking;
	}

	public static PlayerInteractEntityC2SPacket attack(Entity entity, boolean playerSneaking) {
		return new PlayerInteractEntityC2SPacket(entity.getId(), playerSneaking, ATTACK);
	}

	public static PlayerInteractEntityC2SPacket interact(Entity entity, boolean playerSneaking, Hand hand) {
		return new PlayerInteractEntityC2SPacket(entity.getId(), playerSneaking, new PlayerInteractEntityC2SPacket.InteractHandler(hand));
	}

	public static PlayerInteractEntityC2SPacket interactAt(Entity entity, boolean playerSneaking, Hand hand, Vec3d pos) {
		return new PlayerInteractEntityC2SPacket(entity.getId(), playerSneaking, new PlayerInteractEntityC2SPacket.InteractAtHandler(hand, pos));
	}

	public PlayerInteractEntityC2SPacket(PacketByteBuf buf) {
		this.entityId = buf.readVarInt();
		PlayerInteractEntityC2SPacket.InteractType interactType = buf.readEnumConstant(PlayerInteractEntityC2SPacket.InteractType.class);
		this.type = (PlayerInteractEntityC2SPacket.InteractTypeHandler)interactType.handlerGetter.apply(buf);
		this.playerSneaking = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeEnumConstant(this.type.getType());
		this.type.write(buf);
		buf.writeBoolean(this.playerSneaking);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPlayerInteractEntity(this);
	}

	@Nullable
	public Entity getEntity(ServerWorld world) {
		return world.getDragonPart(this.entityId);
	}

	public boolean isPlayerSneaking() {
		return this.playerSneaking;
	}

	public void handle(PlayerInteractEntityC2SPacket.Handler handler) {
		this.type.handle(handler);
	}

	public interface Handler {
		void interact(Hand hand);

		void interactAt(Hand hand, Vec3d pos);

		void attack();
	}

	static class InteractAtHandler implements PlayerInteractEntityC2SPacket.InteractTypeHandler {
		private final Hand hand;
		private final Vec3d pos;

		InteractAtHandler(Hand hand, Vec3d pos) {
			this.hand = hand;
			this.pos = pos;
		}

		private InteractAtHandler(PacketByteBuf buf) {
			this.pos = new Vec3d((double)buf.readFloat(), (double)buf.readFloat(), (double)buf.readFloat());
			this.hand = buf.readEnumConstant(Hand.class);
		}

		@Override
		public PlayerInteractEntityC2SPacket.InteractType getType() {
			return PlayerInteractEntityC2SPacket.InteractType.INTERACT_AT;
		}

		@Override
		public void handle(PlayerInteractEntityC2SPacket.Handler handler) {
			handler.interactAt(this.hand, this.pos);
		}

		@Override
		public void write(PacketByteBuf buf) {
			buf.writeFloat((float)this.pos.x);
			buf.writeFloat((float)this.pos.y);
			buf.writeFloat((float)this.pos.z);
			buf.writeEnumConstant(this.hand);
		}
	}

	static class InteractHandler implements PlayerInteractEntityC2SPacket.InteractTypeHandler {
		private final Hand hand;

		InteractHandler(Hand hand) {
			this.hand = hand;
		}

		private InteractHandler(PacketByteBuf buf) {
			this.hand = buf.readEnumConstant(Hand.class);
		}

		@Override
		public PlayerInteractEntityC2SPacket.InteractType getType() {
			return PlayerInteractEntityC2SPacket.InteractType.INTERACT;
		}

		@Override
		public void handle(PlayerInteractEntityC2SPacket.Handler handler) {
			handler.interact(this.hand);
		}

		@Override
		public void write(PacketByteBuf buf) {
			buf.writeEnumConstant(this.hand);
		}
	}

	static enum InteractType {
		INTERACT(PlayerInteractEntityC2SPacket.InteractHandler::new),
		ATTACK(packetByteBuf -> PlayerInteractEntityC2SPacket.ATTACK),
		INTERACT_AT(PlayerInteractEntityC2SPacket.InteractAtHandler::new);

		final Function<PacketByteBuf, PlayerInteractEntityC2SPacket.InteractTypeHandler> handlerGetter;

		private InteractType(Function<PacketByteBuf, PlayerInteractEntityC2SPacket.InteractTypeHandler> handlerGetter) {
			this.handlerGetter = handlerGetter;
		}
	}

	interface InteractTypeHandler {
		PlayerInteractEntityC2SPacket.InteractType getType();

		void handle(PlayerInteractEntityC2SPacket.Handler handler);

		void write(PacketByteBuf buf);
	}
}
