package net.minecraft.client.gui.screen.narration;

import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ScreenNarrator {
	int currentMessageIndex;
	final Map<ScreenNarrator.PartIndex, ScreenNarrator.Message> narrations = Maps.newTreeMap(
		Comparator.comparing(partIndex -> partIndex.part).thenComparing(partIndex -> partIndex.messageIndex)
	);

	public void buildNarrations(Consumer<NarrationMessageBuilder> builderConsumer) {
		this.currentMessageIndex++;
		builderConsumer.accept(new ScreenNarrator.MessageBuilder(0));
	}

	public String buildNarratorText(boolean forceTransform) {
		final StringBuilder stringBuilder = new StringBuilder();
		Consumer<String> consumer = new Consumer<String>() {
			private boolean first = true;

			public void accept(String string) {
				if (!this.first) {
					stringBuilder.append(". ");
				}

				this.first = false;
				stringBuilder.append(string);
			}
		};
		this.narrations.forEach((partIndex, message) -> {
			if (message.index == this.currentMessageIndex && (forceTransform || !message.transformed)) {
				message.narration.forEachSentence(consumer);
				message.transformed = true;
			}
		});
		return stringBuilder.toString();
	}

	@Environment(EnvType.CLIENT)
	static class Message {
		Narration<?> narration = Narration.EMPTY;
		int index = -1;
		boolean transformed;

		public ScreenNarrator.Message setNarration(int index, Narration<?> narration) {
			if (!this.narration.equals(narration)) {
				this.narration = narration;
				this.transformed = false;
			} else if (this.index + 1 != index) {
				this.transformed = false;
			}

			this.index = index;
			return this;
		}
	}

	@Environment(EnvType.CLIENT)
	class MessageBuilder implements NarrationMessageBuilder {
		private final int messageIndex;

		MessageBuilder(int startIndex) {
			this.messageIndex = startIndex;
		}

		@Override
		public void put(NarrationPart part, Narration<?> narration) {
			((ScreenNarrator.Message)ScreenNarrator.this.narrations
					.computeIfAbsent(new ScreenNarrator.PartIndex(part, this.messageIndex), partIndex -> new ScreenNarrator.Message()))
				.setNarration(ScreenNarrator.this.currentMessageIndex, narration);
		}

		@Override
		public NarrationMessageBuilder nextMessage() {
			return ScreenNarrator.this.new MessageBuilder(this.messageIndex + 1);
		}
	}

	@Environment(EnvType.CLIENT)
	static class PartIndex {
		final NarrationPart part;
		final int messageIndex;

		PartIndex(NarrationPart part, int messageIndex) {
			this.part = part;
			this.messageIndex = messageIndex;
		}
	}
}
