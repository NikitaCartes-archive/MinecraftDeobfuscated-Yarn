package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.RawFilteredPair;

public record WritableBookContentComponent(List<RawFilteredPair<String>> pages) implements BookContent<String, WritableBookContentComponent> {
	public static final WritableBookContentComponent DEFAULT = new WritableBookContentComponent(List.of());
	public static final int MAX_PAGE_LENGTH = 1024;
	public static final int MAX_PAGE_COUNT = 100;
	private static final Codec<RawFilteredPair<String>> PAGE_CODEC = RawFilteredPair.createCodec(Codec.string(0, 1024));
	public static final Codec<List<RawFilteredPair<String>>> PAGES_CODEC = PAGE_CODEC.sizeLimitedListOf(100);
	public static final Codec<WritableBookContentComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(PAGES_CODEC.optionalFieldOf("pages", List.of()).forGetter(WritableBookContentComponent::pages))
				.apply(instance, WritableBookContentComponent::new)
	);
	public static final PacketCodec<ByteBuf, WritableBookContentComponent> PACKET_CODEC = RawFilteredPair.createPacketCodec(PacketCodecs.string(1024))
		.collect(PacketCodecs.toList(100))
		.xmap(WritableBookContentComponent::new, WritableBookContentComponent::pages);

	public WritableBookContentComponent(List<RawFilteredPair<String>> pages) {
		if (pages.size() > 100) {
			throw new IllegalArgumentException("Got " + pages.size() + " pages, but maximum is 100");
		} else {
			this.pages = pages;
		}
	}

	public Stream<String> stream(boolean shouldFilter) {
		return this.pages.stream().map(page -> (String)page.get(shouldFilter));
	}

	public WritableBookContentComponent withPages(List<RawFilteredPair<String>> list) {
		return new WritableBookContentComponent(list);
	}
}
