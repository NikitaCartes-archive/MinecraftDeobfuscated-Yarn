package net.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class class_5900 implements Packet<ClientPlayPacketListener> {
	private final int field_29151;
	private final String field_29152;
	private final Collection<String> field_29153;
	private final Optional<class_5900.class_5902> field_29154;

	private class_5900(String string, int i, Optional<class_5900.class_5902> optional, Collection<String> collection) {
		this.field_29152 = string;
		this.field_29151 = i;
		this.field_29154 = optional;
		this.field_29153 = ImmutableList.<String>copyOf(collection);
	}

	public static class_5900 method_34172(Team team, boolean bl) {
		return new class_5900(
			team.getName(), bl ? 0 : 2, Optional.of(new class_5900.class_5902(team)), (Collection<String>)(bl ? team.getPlayerList() : ImmutableList.<String>of())
		);
	}

	public static class_5900 method_34170(Team team) {
		return new class_5900(team.getName(), 1, Optional.empty(), ImmutableList.<String>of());
	}

	public static class_5900 method_34171(Team team, String string, class_5900.class_5901 arg) {
		return new class_5900(team.getName(), arg == class_5900.class_5901.field_29155 ? 3 : 1, Optional.empty(), ImmutableList.<String>of(string));
	}

	public class_5900(PacketByteBuf packetByteBuf) {
		this.field_29152 = packetByteBuf.readString(16);
		this.field_29151 = packetByteBuf.readByte();
		if (method_34175(this.field_29151)) {
			this.field_29154 = Optional.of(new class_5900.class_5902(packetByteBuf));
		} else {
			this.field_29154 = Optional.empty();
		}

		if (method_34169(this.field_29151)) {
			this.field_29153 = packetByteBuf.<String>method_34066(PacketByteBuf::readString);
		} else {
			this.field_29153 = ImmutableList.<String>of();
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.field_29152);
		buf.writeByte(this.field_29151);
		if (method_34175(this.field_29151)) {
			((class_5900.class_5902)this.field_29154.orElseThrow(() -> new IllegalStateException("Parameters not present, but method is" + this.field_29151)))
				.method_34182(buf);
		}

		if (method_34169(this.field_29151)) {
			buf.method_34062(this.field_29153, PacketByteBuf::writeString);
		}
	}

	private static boolean method_34169(int i) {
		return i == 0 || i == 3 || i == 4;
	}

	private static boolean method_34175(int i) {
		return i == 0 || i == 2;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public class_5900.class_5901 method_34174() {
		switch (this.field_29151) {
			case 0:
			case 3:
				return class_5900.class_5901.field_29155;
			case 1:
			case 2:
			default:
				return null;
			case 4:
				return class_5900.class_5901.field_29156;
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public class_5900.class_5901 method_34176() {
		switch (this.field_29151) {
			case 0:
				return class_5900.class_5901.field_29155;
			case 1:
				return class_5900.class_5901.field_29156;
			default:
				return null;
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onTeam(this);
	}

	@Environment(EnvType.CLIENT)
	public String method_34177() {
		return this.field_29152;
	}

	@Environment(EnvType.CLIENT)
	public Collection<String> method_34178() {
		return this.field_29153;
	}

	@Environment(EnvType.CLIENT)
	public Optional<class_5900.class_5902> method_34179() {
		return this.field_29154;
	}

	public static enum class_5901 {
		field_29155,
		field_29156;
	}

	public static class class_5902 {
		private final Text field_29158;
		private final Text field_29159;
		private final Text field_29160;
		private final String field_29161;
		private final String field_29162;
		private final Formatting field_29163;
		private final int field_29164;

		public class_5902(Team team) {
			this.field_29158 = team.getDisplayName();
			this.field_29164 = team.getFriendlyFlagsBitwise();
			this.field_29161 = team.getNameTagVisibilityRule().name;
			this.field_29162 = team.getCollisionRule().name;
			this.field_29163 = team.getColor();
			this.field_29159 = team.getPrefix();
			this.field_29160 = team.getSuffix();
		}

		public class_5902(PacketByteBuf packetByteBuf) {
			this.field_29158 = packetByteBuf.readText();
			this.field_29164 = packetByteBuf.readByte();
			this.field_29161 = packetByteBuf.readString(40);
			this.field_29162 = packetByteBuf.readString(40);
			this.field_29163 = packetByteBuf.readEnumConstant(Formatting.class);
			this.field_29159 = packetByteBuf.readText();
			this.field_29160 = packetByteBuf.readText();
		}

		@Environment(EnvType.CLIENT)
		public Text method_34181() {
			return this.field_29158;
		}

		@Environment(EnvType.CLIENT)
		public int method_34183() {
			return this.field_29164;
		}

		@Environment(EnvType.CLIENT)
		public Formatting method_34184() {
			return this.field_29163;
		}

		@Environment(EnvType.CLIENT)
		public String method_34185() {
			return this.field_29161;
		}

		@Environment(EnvType.CLIENT)
		public String method_34186() {
			return this.field_29162;
		}

		@Environment(EnvType.CLIENT)
		public Text method_34187() {
			return this.field_29159;
		}

		@Environment(EnvType.CLIENT)
		public Text method_34188() {
			return this.field_29160;
		}

		public void method_34182(PacketByteBuf packetByteBuf) {
			packetByteBuf.writeText(this.field_29158);
			packetByteBuf.writeByte(this.field_29164);
			packetByteBuf.writeString(this.field_29161);
			packetByteBuf.writeString(this.field_29162);
			packetByteBuf.writeEnumConstant(this.field_29163);
			packetByteBuf.writeText(this.field_29159);
			packetByteBuf.writeText(this.field_29160);
		}
	}
}
