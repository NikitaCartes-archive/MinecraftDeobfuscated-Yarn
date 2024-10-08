package net.minecraft.network.packet.s2c.play;

import java.util.List;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.recipe.RecipeDisplayEntry;

public record RecipeBookAddS2CPacket(List<RecipeBookAddS2CPacket.Entry> entries, boolean replace) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, RecipeBookAddS2CPacket> CODEC = PacketCodec.tuple(
		RecipeBookAddS2CPacket.Entry.PACKET_CODEC.collect(PacketCodecs.toList()),
		RecipeBookAddS2CPacket::entries,
		PacketCodecs.BOOL,
		RecipeBookAddS2CPacket::replace,
		RecipeBookAddS2CPacket::new
	);

	@Override
	public PacketType<RecipeBookAddS2CPacket> getPacketId() {
		return PlayPackets.RECIPE_BOOK_ADD;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onRecipeBookAdd(this);
	}

	public static record Entry(RecipeDisplayEntry contents, byte flags) {
		/**
		 * If set, shows a toast for the unlocked recipe. Has value {@value}.
		 */
		public static final byte SHOW_NOTIFICATION = 1;
		/**
		 * If set, plays an animation when the recipe is first viewed on the
		 * recipe book. Has value {@value}.
		 */
		public static final byte HIGHLIGHTED = 2;
		public static final PacketCodec<RegistryByteBuf, RecipeBookAddS2CPacket.Entry> PACKET_CODEC = PacketCodec.tuple(
			RecipeDisplayEntry.PACKET_CODEC,
			RecipeBookAddS2CPacket.Entry::contents,
			PacketCodecs.BYTE,
			RecipeBookAddS2CPacket.Entry::flags,
			RecipeBookAddS2CPacket.Entry::new
		);

		public Entry(RecipeDisplayEntry display, boolean showNotification, boolean highlighted) {
			this(display, (byte)((showNotification ? 1 : 0) | (highlighted ? 2 : 0)));
		}

		public boolean shouldShowNotification() {
			return (this.flags & 1) != 0;
		}

		public boolean isHighlighted() {
			return (this.flags & 2) != 0;
		}
	}
}
