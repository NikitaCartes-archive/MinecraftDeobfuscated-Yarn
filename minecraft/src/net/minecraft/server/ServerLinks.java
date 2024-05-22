package net.minecraft.server;

import com.mojang.datafixers.util.Either;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.function.ValueLists;

public record ServerLinks(List<ServerLinks.Entry> entries) {
	public static final ServerLinks EMPTY = new ServerLinks(List.of());
	public static final PacketCodec<ByteBuf, ServerLinks> CODEC = PacketCodec.tuple(
		ServerLinks.Entry.CODEC.collect(PacketCodecs.toList()), ServerLinks::entries, ServerLinks::new
	);

	public boolean isEmpty() {
		return this.entries.isEmpty();
	}

	public Optional<ServerLinks.Entry> getEntryFor(ServerLinks.Known known) {
		return this.entries.stream().filter(entry -> entry.type.<Boolean>map(type -> type == known, text -> false)).findFirst();
	}

	public static record Entry(Either<ServerLinks.Known, Text> type, String url) {
		public static final PacketCodec<ByteBuf, Either<ServerLinks.Known, Text>> TYPE_CODEC = PacketCodecs.either(ServerLinks.Known.CODEC, TextCodecs.PACKET_CODEC);
		public static final PacketCodec<ByteBuf, ServerLinks.Entry> CODEC = PacketCodec.tuple(
			TYPE_CODEC, ServerLinks.Entry::type, PacketCodecs.STRING, ServerLinks.Entry::url, ServerLinks.Entry::new
		);

		public static ServerLinks.Entry create(ServerLinks.Known known, String url) {
			return new ServerLinks.Entry(Either.left(known), url);
		}

		public static ServerLinks.Entry create(Text name, String url) {
			return new ServerLinks.Entry(Either.right(name), url);
		}

		public Text getText() {
			return this.type.map(ServerLinks.Known::getText, text -> text);
		}
	}

	public static enum Known {
		BUG_REPORT(0, "report_bug");

		private static final IntFunction<ServerLinks.Known> FROM_ID = ValueLists.createIdToValueFunction(
			known -> known.id, values(), ValueLists.OutOfBoundsHandling.ZERO
		);
		public static final PacketCodec<ByteBuf, ServerLinks.Known> CODEC = PacketCodecs.indexed(FROM_ID, known -> known.id);
		private final int id;
		private final String name;

		private Known(final int id, final String name) {
			this.id = id;
			this.name = name;
		}

		private Text getText() {
			return Text.translatable("known_server_link." + this.name);
		}

		public ServerLinks.Entry createEntry(String url) {
			return ServerLinks.Entry.create(this, url);
		}
	}
}
