package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import java.util.function.Function;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;

public class BossBarS2CPacket implements Packet<ClientPlayPacketListener> {
	private static final int DARKEN_SKY_MASK = 1;
	private static final int DRAGON_MUSIC_MASK = 2;
	private static final int THICKEN_FOG_MASK = 4;
	private final UUID uuid;
	private final BossBarS2CPacket.Action action;
	static final BossBarS2CPacket.Action REMOVE_ACTION = new BossBarS2CPacket.Action() {
		@Override
		public BossBarS2CPacket.Type getType() {
			return BossBarS2CPacket.Type.REMOVE;
		}

		@Override
		public void accept(UUID uuid, BossBarS2CPacket.Consumer consumer) {
			consumer.remove(uuid);
		}

		@Override
		public void toPacket(PacketByteBuf buf) {
		}
	};

	private BossBarS2CPacket(UUID uuid, BossBarS2CPacket.Action action) {
		this.uuid = uuid;
		this.action = action;
	}

	public BossBarS2CPacket(PacketByteBuf buf) {
		this.uuid = buf.readUuid();
		BossBarS2CPacket.Type type = buf.readEnumConstant(BossBarS2CPacket.Type.class);
		this.action = (BossBarS2CPacket.Action)type.parser.apply(buf);
	}

	public static BossBarS2CPacket add(BossBar bar) {
		return new BossBarS2CPacket(bar.getUuid(), new BossBarS2CPacket.AddAction(bar));
	}

	public static BossBarS2CPacket remove(UUID uuid) {
		return new BossBarS2CPacket(uuid, REMOVE_ACTION);
	}

	public static BossBarS2CPacket updateProgress(BossBar bar) {
		return new BossBarS2CPacket(bar.getUuid(), new BossBarS2CPacket.UpdateProgressAction(bar.getPercent()));
	}

	public static BossBarS2CPacket updateName(BossBar bar) {
		return new BossBarS2CPacket(bar.getUuid(), new BossBarS2CPacket.UpdateNameAction(bar.getName()));
	}

	public static BossBarS2CPacket updateStyle(BossBar bar) {
		return new BossBarS2CPacket(bar.getUuid(), new BossBarS2CPacket.UpdateStyleAction(bar.getColor(), bar.getStyle()));
	}

	public static BossBarS2CPacket updateProperties(BossBar bar) {
		return new BossBarS2CPacket(bar.getUuid(), new BossBarS2CPacket.UpdatePropertiesAction(bar.shouldDarkenSky(), bar.hasDragonMusic(), bar.shouldThickenFog()));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeUuid(this.uuid);
		buf.writeEnumConstant(this.action.getType());
		this.action.toPacket(buf);
	}

	static int maskProperties(boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
		int i = 0;
		if (darkenSky) {
			i |= 1;
		}

		if (dragonMusic) {
			i |= 2;
		}

		if (thickenFog) {
			i |= 4;
		}

		return i;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onBossBar(this);
	}

	public void accept(BossBarS2CPacket.Consumer consumer) {
		this.action.accept(this.uuid, consumer);
	}

	interface Action {
		BossBarS2CPacket.Type getType();

		void accept(UUID uuid, BossBarS2CPacket.Consumer consumer);

		void toPacket(PacketByteBuf buf);
	}

	static class AddAction implements BossBarS2CPacket.Action {
		private final Text name;
		private final float percent;
		private final BossBar.Color color;
		private final BossBar.Style style;
		private final boolean darkenSky;
		private final boolean dragonMusic;
		private final boolean thickenFog;

		AddAction(BossBar bar) {
			this.name = bar.getName();
			this.percent = bar.getPercent();
			this.color = bar.getColor();
			this.style = bar.getStyle();
			this.darkenSky = bar.shouldDarkenSky();
			this.dragonMusic = bar.hasDragonMusic();
			this.thickenFog = bar.shouldThickenFog();
		}

		private AddAction(PacketByteBuf buf) {
			this.name = buf.readText();
			this.percent = buf.readFloat();
			this.color = buf.readEnumConstant(BossBar.Color.class);
			this.style = buf.readEnumConstant(BossBar.Style.class);
			int i = buf.readUnsignedByte();
			this.darkenSky = (i & 1) > 0;
			this.dragonMusic = (i & 2) > 0;
			this.thickenFog = (i & 4) > 0;
		}

		@Override
		public BossBarS2CPacket.Type getType() {
			return BossBarS2CPacket.Type.ADD;
		}

		@Override
		public void accept(UUID uuid, BossBarS2CPacket.Consumer consumer) {
			consumer.add(uuid, this.name, this.percent, this.color, this.style, this.darkenSky, this.dragonMusic, this.thickenFog);
		}

		@Override
		public void toPacket(PacketByteBuf buf) {
			buf.writeText(this.name);
			buf.writeFloat(this.percent);
			buf.writeEnumConstant(this.color);
			buf.writeEnumConstant(this.style);
			buf.writeByte(BossBarS2CPacket.maskProperties(this.darkenSky, this.dragonMusic, this.thickenFog));
		}
	}

	public interface Consumer {
		default void add(UUID uuid, Text name, float percent, BossBar.Color color, BossBar.Style style, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
		}

		default void remove(UUID uuid) {
		}

		default void updateProgress(UUID uuid, float percent) {
		}

		default void updateName(UUID uuid, Text name) {
		}

		default void updateStyle(UUID id, BossBar.Color color, BossBar.Style style) {
		}

		default void updateProperties(UUID uuid, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
		}
	}

	static enum Type {
		ADD(BossBarS2CPacket.AddAction::new),
		REMOVE(buf -> BossBarS2CPacket.REMOVE_ACTION),
		UPDATE_PROGRESS(BossBarS2CPacket.UpdateProgressAction::new),
		UPDATE_NAME(BossBarS2CPacket.UpdateNameAction::new),
		UPDATE_STYLE(BossBarS2CPacket.UpdateStyleAction::new),
		UPDATE_PROPERTIES(BossBarS2CPacket.UpdatePropertiesAction::new);

		final Function<PacketByteBuf, BossBarS2CPacket.Action> parser;

		private Type(Function<PacketByteBuf, BossBarS2CPacket.Action> parser) {
			this.parser = parser;
		}
	}

	static class UpdateNameAction implements BossBarS2CPacket.Action {
		private final Text name;

		UpdateNameAction(Text name) {
			this.name = name;
		}

		private UpdateNameAction(PacketByteBuf buf) {
			this.name = buf.readText();
		}

		@Override
		public BossBarS2CPacket.Type getType() {
			return BossBarS2CPacket.Type.UPDATE_NAME;
		}

		@Override
		public void accept(UUID uuid, BossBarS2CPacket.Consumer consumer) {
			consumer.updateName(uuid, this.name);
		}

		@Override
		public void toPacket(PacketByteBuf buf) {
			buf.writeText(this.name);
		}
	}

	static class UpdateProgressAction implements BossBarS2CPacket.Action {
		private final float percent;

		UpdateProgressAction(float percent) {
			this.percent = percent;
		}

		private UpdateProgressAction(PacketByteBuf buf) {
			this.percent = buf.readFloat();
		}

		@Override
		public BossBarS2CPacket.Type getType() {
			return BossBarS2CPacket.Type.UPDATE_PROGRESS;
		}

		@Override
		public void accept(UUID uuid, BossBarS2CPacket.Consumer consumer) {
			consumer.updateProgress(uuid, this.percent);
		}

		@Override
		public void toPacket(PacketByteBuf buf) {
			buf.writeFloat(this.percent);
		}
	}

	static class UpdatePropertiesAction implements BossBarS2CPacket.Action {
		private final boolean darkenSky;
		private final boolean dragonMusic;
		private final boolean thickenFog;

		UpdatePropertiesAction(boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
			this.darkenSky = darkenSky;
			this.dragonMusic = dragonMusic;
			this.thickenFog = thickenFog;
		}

		private UpdatePropertiesAction(PacketByteBuf buf) {
			int i = buf.readUnsignedByte();
			this.darkenSky = (i & 1) > 0;
			this.dragonMusic = (i & 2) > 0;
			this.thickenFog = (i & 4) > 0;
		}

		@Override
		public BossBarS2CPacket.Type getType() {
			return BossBarS2CPacket.Type.UPDATE_PROPERTIES;
		}

		@Override
		public void accept(UUID uuid, BossBarS2CPacket.Consumer consumer) {
			consumer.updateProperties(uuid, this.darkenSky, this.dragonMusic, this.thickenFog);
		}

		@Override
		public void toPacket(PacketByteBuf buf) {
			buf.writeByte(BossBarS2CPacket.maskProperties(this.darkenSky, this.dragonMusic, this.thickenFog));
		}
	}

	static class UpdateStyleAction implements BossBarS2CPacket.Action {
		private final BossBar.Color color;
		private final BossBar.Style style;

		UpdateStyleAction(BossBar.Color color, BossBar.Style style) {
			this.color = color;
			this.style = style;
		}

		private UpdateStyleAction(PacketByteBuf buf) {
			this.color = buf.readEnumConstant(BossBar.Color.class);
			this.style = buf.readEnumConstant(BossBar.Style.class);
		}

		@Override
		public BossBarS2CPacket.Type getType() {
			return BossBarS2CPacket.Type.UPDATE_STYLE;
		}

		@Override
		public void accept(UUID uuid, BossBarS2CPacket.Consumer consumer) {
			consumer.updateStyle(uuid, this.color, this.style);
		}

		@Override
		public void toPacket(PacketByteBuf buf) {
			buf.writeEnumConstant(this.color);
			buf.writeEnumConstant(this.style);
		}
	}
}
