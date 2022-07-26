/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.report.log;

import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collection;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.Spliterators;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.report.log.ChatLogEntry;
import net.minecraft.client.report.log.ReceivedMessage;
import org.jetbrains.annotations.Nullable;

/**
 * A chat log holds received message entries with sequential indices, where
 * newer entries receive bigger indices. An implementation using fixed-size array
 * is available at {@link ChatLogImpl}.
 * 
 * <p>There are two types of entries. {@link HeaderEntry} is an entry containing only
 * the message's header, and is used for censored messages. {@link ReceivedMessage}
 * is an entry for full chat or game messages.
 */
@Environment(value=EnvType.CLIENT)
public interface ChatLog {
    public static final int MISSING_NEXT_INDEX = -1;

    /**
     * Adds {@code entry} to the log.
     */
    public void add(ChatLogEntry var1);

    /**
     * {@return the entry with index {@code index}, or {@code null} if there is no
     * such entry in the log}
     */
    @Nullable
    public ChatLogEntry get(int var1);

    /**
     * {@return the indexed entry with index {@code index}, or {@code null} if there is no
     * such entry in the log}
     */
    @Nullable
    default public IndexedEntry<ChatLogEntry> getIndexed(int index) {
        ChatLogEntry chatLogEntry = this.get(index);
        return chatLogEntry != null ? new IndexedEntry<ChatLogEntry>(index, chatLogEntry) : null;
    }

    /**
     * {@return whether the log contains an entry with index {@code index}}
     */
    default public boolean contains(int index) {
        return this.get(index) != null;
    }

    /**
     * {@return the index offset by {@code offset} if there is an entry with that index, or
     * {@value #MISSING_NEXT_INDEX} if there is no entry with the offset index}
     */
    public int getOffsetIndex(int var1, int var2);

    /**
     * {@return the index offset by {@code -1} if there is an entry with that index, or
     * {@value #MISSING_NEXT_INDEX} if there is no entry with the offset index}
     * 
     * @see #getOffsetIndex
     */
    default public int getPreviousIndex(int index) {
        return this.getOffsetIndex(index, -1);
    }

    /**
     * {@return the index offset by {@code 1} if there is an entry with that index, or
     * {@value #MISSING_NEXT_INDEX} if there is no entry with the offset index}
     * 
     * @see #getOffsetIndex
     */
    default public int getNextIndex(int index) {
        return this.getOffsetIndex(index, 1);
    }

    /**
     * {@return the maximum index currently used within the log}
     * 
     * <p>This value changes every time a new entry gets added. The entry
     * associated with this index is the newest one in the log.
     */
    public int getMaxIndex();

    /**
     * {@return the minimum index currently used within the log}
     * 
     * <p>This value can change every time a new entry gets added. The entry
     * associated with this index is the oldest one in the log.
     */
    public int getMinIndex();

    /**
     * {@return the streams starting from {@linkplain #getMinIndex the smallest index
     * in the log} with entires ordered chronologically (in ascending order)}
     */
    default public Streams streamForward() {
        return this.streamForward(this.getMinIndex());
    }

    /**
     * {@return the streams starting from {@linkplain #getMaxIndex the biggest index
     * in the log} with entires ordered antichronologically (in descending order)}
     */
    default public Streams streamBackward() {
        return this.streamBackward(this.getMaxIndex());
    }

    /**
     * {@return the streams starting from {@code startIndex} with entires ordered
     * chronologically (in ascending order)}
     * 
     * @implNote If {@code startIndex} is not in the log, this returns {@link #emptyStreams}.
     */
    default public Streams streamForward(int startIndex) {
        return this.stream(startIndex, this::getNextIndex);
    }

    /**
     * {@return the streams starting from {@code startIndex} with entires ordered
     * antichronologically (in descending order)}
     * 
     * @implNote If {@code startIndex} is not in the log, this returns {@link #emptyStreams}.
     */
    default public Streams streamBackward(int startIndex) {
        return this.stream(startIndex, this::getPreviousIndex);
    }

    /**
     * {@return the streams starting from {@code startIndex} with entires ordered
     * chronologically (in ascending order) up to and including {@code endIndex}}
     * 
     * @implNote If either {@code startIndex} or {@code endIndex} is not in the log,
     * this returns {@link #emptyStreams}.
     */
    default public Streams streamForward(int startIndex, int endIndex) {
        if (!this.contains(startIndex) || !this.contains(endIndex)) {
            return this.emptyStreams();
        }
        return this.stream(startIndex, currentIndex -> {
            if (currentIndex == endIndex) {
                return -1;
            }
            return this.getNextIndex(currentIndex);
        });
    }

    /**
     * {@return the streams starting from {@code startIndex}}
     * 
     * @implNote If {@code startIndex} is not in the log, this returns {@link #emptyStreams}.
     * 
     * @param nextIndexGetter a function that, when given an index, returns the next index
     * or {@value #MISSING_NEXT_INDEX} to indicate the end of the stream
     */
    default public Streams stream(final int startIndex, final IntUnaryOperator nextIndexGetter) {
        if (!this.contains(startIndex)) {
            return this.emptyStreams();
        }
        return new Streams(this, new PrimitiveIterator.OfInt(){
            private int nextIndex;
            {
                this.nextIndex = startIndex;
            }

            @Override
            public int nextInt() {
                int i = this.nextIndex;
                this.nextIndex = nextIndexGetter.applyAsInt(i);
                return i;
            }

            @Override
            public boolean hasNext() {
                return this.nextIndex != -1;
            }
        });
    }

    /**
     * {@return the empty stream}
     */
    private Streams emptyStreams() {
        return new Streams(this, IntList.of().iterator());
    }

    @Environment(value=EnvType.CLIENT)
    public record IndexedEntry<T extends ChatLogEntry>(int index, T entry) {
        @Nullable
        public <U extends ChatLogEntry> IndexedEntry<U> cast(Class<U> clazz) {
            if (clazz.isInstance(this.entry)) {
                return new IndexedEntry<ChatLogEntry>(this.index, (ChatLogEntry)clazz.cast(this.entry));
            }
            return null;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Streams {
        private static final int CHARACTERISTICS = 1041;
        private final ChatLog log;
        private final PrimitiveIterator.OfInt indicesIterator;

        Streams(ChatLog log, PrimitiveIterator.OfInt indicesIterator) {
            this.log = log;
            this.indicesIterator = indicesIterator;
        }

        public IntStream streamIndices() {
            return StreamSupport.intStream(Spliterators.spliteratorUnknownSize(this.indicesIterator, 1041), false);
        }

        public Stream<ChatLogEntry> streamLogEntries() {
            return this.streamIndices().mapToObj(this.log::get).filter(Objects::nonNull);
        }

        public Collection<GameProfile> collectSenderProfiles() {
            return this.streamLogEntries().map(message -> {
                ReceivedMessage.ChatMessage chatMessage;
                if (message instanceof ReceivedMessage.ChatMessage && (chatMessage = (ReceivedMessage.ChatMessage)message).isSentFrom(chatMessage.profile().getId())) {
                    return chatMessage.profile();
                }
                return null;
            }).filter(Objects::nonNull).distinct().toList();
        }

        public Stream<IndexedEntry<ChatLogEntry>> streamIndexedEntries() {
            return this.streamIndices().mapToObj(this.log::getIndexed).filter(Objects::nonNull);
        }
    }
}

