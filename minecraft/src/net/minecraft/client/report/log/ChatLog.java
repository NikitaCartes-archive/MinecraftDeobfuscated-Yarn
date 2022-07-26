package net.minecraft.client.report.log;

import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collection;
import java.util.Objects;
import java.util.Spliterators;
import java.util.PrimitiveIterator.OfInt;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A chat log holds received message entries with sequential indices, where
 * newer entries receive bigger indices. An implementation using fixed-size array
 * is available at {@link ChatLogImpl}.
 * 
 * <p>There are two types of entries. {@link HeaderEntry} is an entry containing only
 * the message's header, and is used for censored messages. {@link ReceivedMessage}
 * is an entry for full chat or game messages.
 */
@Environment(EnvType.CLIENT)
public interface ChatLog {
	int MISSING_NEXT_INDEX = -1;

	/**
	 * Adds {@code entry} to the log.
	 */
	void add(ChatLogEntry entry);

	/**
	 * {@return the entry with index {@code index}, or {@code null} if there is no
	 * such entry in the log}
	 */
	@Nullable
	ChatLogEntry get(int index);

	/**
	 * {@return the indexed entry with index {@code index}, or {@code null} if there is no
	 * such entry in the log}
	 */
	@Nullable
	default ChatLog.IndexedEntry<ChatLogEntry> getIndexed(int index) {
		ChatLogEntry chatLogEntry = this.get(index);
		return chatLogEntry != null ? new ChatLog.IndexedEntry<>(index, chatLogEntry) : null;
	}

	/**
	 * {@return whether the log contains an entry with index {@code index}}
	 */
	default boolean contains(int index) {
		return this.get(index) != null;
	}

	/**
	 * {@return the index offset by {@code offset} if there is an entry with that index, or
	 * {@value #MISSING_NEXT_INDEX} if there is no entry with the offset index}
	 */
	int getOffsetIndex(int index, int offset);

	/**
	 * {@return the index offset by {@code -1} if there is an entry with that index, or
	 * {@value #MISSING_NEXT_INDEX} if there is no entry with the offset index}
	 * 
	 * @see #getOffsetIndex
	 */
	default int getPreviousIndex(int index) {
		return this.getOffsetIndex(index, -1);
	}

	/**
	 * {@return the index offset by {@code 1} if there is an entry with that index, or
	 * {@value #MISSING_NEXT_INDEX} if there is no entry with the offset index}
	 * 
	 * @see #getOffsetIndex
	 */
	default int getNextIndex(int index) {
		return this.getOffsetIndex(index, 1);
	}

	/**
	 * {@return the maximum index currently used within the log}
	 * 
	 * <p>This value changes every time a new entry gets added. The entry
	 * associated with this index is the newest one in the log.
	 */
	int getMaxIndex();

	/**
	 * {@return the minimum index currently used within the log}
	 * 
	 * <p>This value can change every time a new entry gets added. The entry
	 * associated with this index is the oldest one in the log.
	 */
	int getMinIndex();

	/**
	 * {@return the streams starting from {@linkplain #getMinIndex the smallest index
	 * in the log} with entires ordered chronologically (in ascending order)}
	 */
	default ChatLog.Streams streamForward() {
		return this.streamForward(this.getMinIndex());
	}

	/**
	 * {@return the streams starting from {@linkplain #getMaxIndex the biggest index
	 * in the log} with entires ordered antichronologically (in descending order)}
	 */
	default ChatLog.Streams streamBackward() {
		return this.streamBackward(this.getMaxIndex());
	}

	/**
	 * {@return the streams starting from {@code startIndex} with entires ordered
	 * chronologically (in ascending order)}
	 * 
	 * @implNote If {@code startIndex} is not in the log, this returns {@link #emptyStreams}.
	 */
	default ChatLog.Streams streamForward(int startIndex) {
		return this.stream(startIndex, this::getNextIndex);
	}

	/**
	 * {@return the streams starting from {@code startIndex} with entires ordered
	 * antichronologically (in descending order)}
	 * 
	 * @implNote If {@code startIndex} is not in the log, this returns {@link #emptyStreams}.
	 */
	default ChatLog.Streams streamBackward(int startIndex) {
		return this.stream(startIndex, this::getPreviousIndex);
	}

	/**
	 * {@return the streams starting from {@code startIndex} with entires ordered
	 * chronologically (in ascending order) up to and including {@code endIndex}}
	 * 
	 * @implNote If either {@code startIndex} or {@code endIndex} is not in the log,
	 * this returns {@link #emptyStreams}.
	 */
	default ChatLog.Streams streamForward(int startIndex, int endIndex) {
		return this.contains(startIndex) && this.contains(endIndex)
			? this.stream(startIndex, currentIndex -> currentIndex == endIndex ? -1 : this.getNextIndex(currentIndex))
			: this.emptyStreams();
	}

	/**
	 * {@return the streams starting from {@code startIndex}}
	 * 
	 * @implNote If {@code startIndex} is not in the log, this returns {@link #emptyStreams}.
	 * 
	 * @param nextIndexGetter a function that, when given an index, returns the next index
	 * or {@value #MISSING_NEXT_INDEX} to indicate the end of the stream
	 */
	default ChatLog.Streams stream(int startIndex, IntUnaryOperator nextIndexGetter) {
		return !this.contains(startIndex) ? this.emptyStreams() : new ChatLog.Streams(this, new OfInt() {
			private int nextIndex = startIndex;

			public int nextInt() {
				int i = this.nextIndex;
				this.nextIndex = nextIndexGetter.applyAsInt(i);
				return i;
			}

			public boolean hasNext() {
				return this.nextIndex != -1;
			}
		});
	}

	/**
	 * {@return the empty stream}
	 */
	private ChatLog.Streams emptyStreams() {
		return new ChatLog.Streams(this, IntList.of().iterator());
	}

	/**
	 * A pair of the entry's index and the entry itself.
	 * 
	 * @see ChatLog
	 */
	@Environment(EnvType.CLIENT)
	public static record IndexedEntry<T extends ChatLogEntry>(int index, T entry) {
		/**
		 * {@return the indexed entry with the entry instance cast to {@code clazz},
		 * or {@code null} if it cannot be cast}
		 */
		@Nullable
		public <U extends ChatLogEntry> ChatLog.IndexedEntry<U> cast(Class<U> clazz) {
			return clazz.isInstance(this.entry) ? new ChatLog.IndexedEntry<>(this.index, (U)clazz.cast(this.entry)) : null;
		}
	}

	/**
	 * A set of streams of log entries.
	 */
	@Environment(EnvType.CLIENT)
	public static class Streams {
		private static final int CHARACTERISTICS = 1041;
		private final ChatLog log;
		private final OfInt indicesIterator;

		Streams(ChatLog log, OfInt indicesIterator) {
			this.log = log;
			this.indicesIterator = indicesIterator;
		}

		/**
		 * {@return the stream of entry indices}
		 */
		public IntStream streamIndices() {
			return StreamSupport.intStream(Spliterators.spliteratorUnknownSize(this.indicesIterator, 1041), false);
		}

		/**
		 * {@return the stream of log entries}
		 */
		public Stream<ChatLogEntry> streamLogEntries() {
			return this.streamIndices().mapToObj(this.log::get).filter(Objects::nonNull);
		}

		/**
		 * {@return the collection of profiles of message senders}
		 * 
		 * <p>This ignores game messages, and the returned collection has no duplicates.
		 */
		public Collection<GameProfile> collectSenderProfiles() {
			return this.streamLogEntries().map(message -> {
				if (message instanceof ReceivedMessage.ChatMessage chatMessage && chatMessage.isSentFrom(chatMessage.profile().getId())) {
					return chatMessage.profile();
				}

				return null;
			}).filter(Objects::nonNull).distinct().toList();
		}

		/**
		 * {@return the stream of indexed log entries}
		 */
		public Stream<ChatLog.IndexedEntry<ChatLogEntry>> streamIndexedEntries() {
			return this.streamIndices().mapToObj(this.log::getIndexed).filter(Objects::nonNull);
		}
	}
}
