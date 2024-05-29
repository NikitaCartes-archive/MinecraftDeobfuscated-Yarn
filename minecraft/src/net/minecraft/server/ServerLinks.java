package net.minecraft.server;

import com.mojang.datafixers.util.Either;
import io.netty.buffer.ByteBuf;
import java.net.URI;
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
	public static final PacketCodec<ByteBuf, Either<ServerLinks.Known, Text>> TYPE_CODEC = PacketCodecs.either(ServerLinks.Known.CODEC, TextCodecs.PACKET_CODEC);
	public static final PacketCodec<ByteBuf, List<ServerLinks.StringifiedEntry>> LIST_CODEC = ServerLinks.StringifiedEntry.CODEC.collect(PacketCodecs.toList());

	public boolean isEmpty() {
		return this.entries.isEmpty();
	}

	public Optional<ServerLinks.Entry> getEntryFor(ServerLinks.Known known) {
		return this.entries.stream().filter(entry -> entry.type.<Boolean>map(type -> type == known, text -> false)).findFirst();
	}

	public List<ServerLinks.StringifiedEntry> getLinks() {
		return this.entries.stream().map(entry -> new ServerLinks.StringifiedEntry(entry.type, entry.link.toString())).toList();
	}

	public static record Entry(Either<ServerLinks.Known, Text> type, URI link) {

		public static ServerLinks.Entry create(ServerLinks.Known known, URI link) {
			return new ServerLinks.Entry(Either.left(known), link);
		}

		public static ServerLinks.Entry create(Text name, URI link) {
			return new ServerLinks.Entry(Either.right(name), link);
		}

		public Text getText() {
			return this.type.map(ServerLinks.Known::getText, text -> text);
		}
	}

	public static enum Known {
		BUG_REPORT(0, "report_bug"),
		COMMUNITY_GUIDELINES(1, "community_guidelines"),
		SUPPORT(2, "support"),
		STATUS(3, "status"),
		FEEDBACK(4, "feedback"),
		COMMUNITY(5, "community"),
		WEBSITE(6, "website"),
		FORUMS(7, "forums"),
		NEWS(8, "news"),
		ANNOUNCEMENTS(9, "announcements");

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

		public ServerLinks.Entry createEntry(URI link) {
			return ServerLinks.Entry.create(this, link);
		}
	}

	public static record StringifiedEntry(Either<ServerLinks.Known, Text> type, String link) {
		public static final PacketCodec<ByteBuf, ServerLinks.StringifiedEntry> CODEC = PacketCodec.tuple(
			ServerLinks.TYPE_CODEC, ServerLinks.StringifiedEntry::type, PacketCodecs.STRING, ServerLinks.StringifiedEntry::link, ServerLinks.StringifiedEntry::new
		);
	}
}
