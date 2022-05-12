package net.minecraft.client.network;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.RequestChatPreviewC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.AbstractRandom;
import org.apache.commons.lang3.StringUtils;

/**
 * Chat previewer manages the chat preview. Chat previewer is created per
 * {@link net.minecraft.client.gui.screen.ChatScreen}, so closing the chat screen and
 * reopening it would create a new chat previewer.
 * 
 * <p>A <strong>query</strong> is a request to the server to send the chat message preview.
 * The previewer only sends the query if there is no query that is waiting for the response,
 * or if the last query took more than {@value #LATEST_NEXT_QUERY_DELAY} milliseconds to
 * respond. A query can be sent at most every {@value #EARLIEST_NEXT_QUERY_DELAY} milliseconds.
 * 
 * <p>The response to the query can be "consumed" by calling {@link #tryConsumeResponse}.
 * If the response is still valid (i.e. the input has not changed since the query was sent),
 * consuming the response will return the response and clear it. Note that to prevent race
 * condition between the player sending the chat message and the response's arrival, responses
 * can only be consumed after the cooldown (by default, {@value #CONSUME_COOLDOWN} milliseconds)
 * has passed. It is also possible to get the response text without consuming by calling
 * {@link #getPreviewText}.
 */
@Environment(EnvType.CLIENT)
public class ChatPreviewer {
	/**
	 * How long the previewer should wait at a minimum before sending the next
	 * query in milliseconds. Is {@value}.
	 * 
	 * @see #getEarliestNextQueryTime
	 */
	private static final long EARLIEST_NEXT_QUERY_DELAY = 100L;
	/**
	 * How long the previewer can wait for the response at most before sending the next
	 * query in milliseconds. Is {@value}.
	 * 
	 * @see #getLatestNextQueryTime
	 */
	private static final long LATEST_NEXT_QUERY_DELAY = 1000L;
	/**
	 * How long the previewer should wait before consuming the response since the response
	 * arrived at the client in milliseconds. Is {@value}.
	 * 
	 * @see #tryConsumeResponse
	 */
	private static final long CONSUME_COOLDOWN = 200L;
	private final MinecraftClient client;
	private final ChatPreviewer.IdIncrementor idIncrementor = new ChatPreviewer.IdIncrementor();
	/**
	 * The query that is waiting for the previewer to request (i.e. the next query to be sent).
	 * Can be {@code null} if there is no such query.
	 */
	@Nullable
	private ChatPreviewer.Query pendingRequestQuery;
	/**
	 * The query that is waiting for the server to respond (i.e. the last query).
	 * Can be {@code null} if there is no such query.
	 */
	@Nullable
	private ChatPreviewer.Query pendingResponseQuery;
	/**
	 * The last time a query was sent.
	 * 
	 * <p>The next query will be sent after {@value #EARLIEST_NEXT_QUERY_DELAY} to
	 * {@value #LATEST_NEXT_QUERY_DELAY} milliseconds; the actual delay depends on
	 * when the server responds to the query.
	 */
	private long queryTime;
	@Nullable
	private ChatPreviewer.Response lastResponse;

	public ChatPreviewer(MinecraftClient client) {
		this.client = client;
	}

	/**
	 * Sends the pending query, if it exists and the delay has passed.
	 */
	public void tryQuery() {
		ChatPreviewer.Query query = this.pendingRequestQuery;
		if (query != null) {
			long l = Util.getMeasuringTimeMs();
			if (this.shouldQuery(l)) {
				this.query(query, l);
				this.pendingRequestQuery = null;
			}
		}
	}

	/**
	 * Sends {@code query} to the server.
	 */
	private void query(ChatPreviewer.Query query, long currentTime) {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
		if (clientPlayNetworkHandler != null) {
			clientPlayNetworkHandler.sendPacket(new RequestChatPreviewC2SPacket(query.id(), query.message()));
			this.pendingResponseQuery = query;
		} else {
			this.pendingResponseQuery = null;
		}

		this.queryTime = currentTime;
	}

	/**
	 * {@return whether the delay for querying has passed}
	 * 
	 * <p>The previewer only sends the query if there is no query that is waiting for the response,
	 * or if the last query took more than {@value #LATEST_NEXT_QUERY_DELAY} milliseconds to
	 * respond. A query can be sent at most every {@value #EARLIEST_NEXT_QUERY_DELAY} milliseconds.
	 */
	private boolean shouldQuery(long currentTime) {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
		if (clientPlayNetworkHandler == null) {
			return true;
		} else {
			return currentTime < this.getEarliestNextQueryTime() ? false : this.pendingResponseQuery == null || currentTime >= this.getLatestNextQueryTime();
		}
	}

	/**
	 * {@return the earliest time the next query can be sent}
	 * 
	 * @implNote This is {@value #EARLIEST_NEXT_QUERY_DELAY} milliseconds after the last query.
	 */
	private long getEarliestNextQueryTime() {
		return this.queryTime + 100L;
	}

	/**
	 * {@return the latest time the next query should be sent}
	 * 
	 * @implNote This is {@value #LATEST_NEXT_QUERY_DELAY} milliseconds after the last query.
	 */
	private long getLatestNextQueryTime() {
		return this.queryTime + 1000L;
	}

	/**
	 * Clears the last response and the queries (but not the query time).
	 */
	public void clear() {
		this.lastResponse = null;
		this.pendingRequestQuery = null;
		this.pendingResponseQuery = null;
	}

	/**
	 * Called when {@code query} was input into the chat screen.
	 * 
	 * @implNote If the {@linkplain #normalize normalized} query is empty, the queries are cleared
	 * and the empty response is set by the client; otherwise it sets the pending query.
	 */
	public void onInput(String query) {
		query = normalize(query);
		if (query.isEmpty()) {
			this.lastResponse = new ChatPreviewer.Response(Util.getMeasuringTimeMs(), query, null);
			this.pendingRequestQuery = null;
			this.pendingResponseQuery = null;
		} else {
			ChatPreviewer.Query query2 = this.pendingRequestQuery != null ? this.pendingRequestQuery : this.pendingResponseQuery;
			if (query2 == null || !query2.messageEquals(query)) {
				this.pendingRequestQuery = new ChatPreviewer.Query(this.idIncrementor.next(), query);
			}
		}
	}

	/**
	 * Called when the preview response was received.
	 * 
	 * @implNote This sets the last response and clears {@link #pendingResponseQuery}
	 * if the pending query ID equals {@code id}
	 */
	public void onResponse(int id, @Nullable Text response) {
		if (this.pendingRequestQuery != null || this.pendingResponseQuery != null) {
			if (this.pendingResponseQuery != null && this.pendingResponseQuery.idEquals(id)) {
				Text text = (Text)(response != null ? response : Text.literal(this.pendingResponseQuery.message()));
				this.lastResponse = new ChatPreviewer.Response(Util.getMeasuringTimeMs(), this.pendingResponseQuery.message(), text);
				this.pendingResponseQuery = null;
			} else {
				this.lastResponse = null;
			}
		}
	}

	/**
	 * {@return the preview text (also known as the last response text), or {@code null}
	 * if the server responded as such}
	 * 
	 * <p>This does not consume the response.
	 */
	@Nullable
	public Text getPreviewText() {
		return Util.map(this.lastResponse, ChatPreviewer.Response::previewText);
	}

	/**
	 * {@return the consumed response text, or {@code null} if the server responded as such, or
	 * if the response could not be consumed}
	 * 
	 * <p>If the response is still valid (i.e. the input has not changed since the query was sent),
	 * consuming the response will return the response and clear it. Note that to prevent race
	 * condition between the player sending the chat message and the response's arrival, responses
	 * can only be consumed after the cooldown (by default, {@value #CONSUME_COOLDOWN} milliseconds)
	 * has passed. It is also possible to get the response text without consuming by calling
	 * {@link #getPreviewText}.
	 */
	@Nullable
	public Text tryConsumeResponse(String message) {
		if (this.lastResponse != null && this.lastResponse.canConsume(message)) {
			Text text = this.lastResponse.previewText();
			this.lastResponse = null;
			return text;
		} else {
			return null;
		}
	}

	/**
	 * {@return whether the preview should be rendered}
	 * 
	 * @implNote A preview should be rendered if there is a response, a pending query, or
	 * a query waiting for the response.
	 */
	public boolean shouldRenderPreview() {
		return this.lastResponse != null || this.pendingRequestQuery != null || this.pendingResponseQuery != null;
	}

	/**
	 * {@return the {@code message} normalized by trimming it and then normalizing spaces}
	 */
	static String normalize(String message) {
		return StringUtils.normalizeSpace(message.trim());
	}

	/**
	 * A utility class that increments the ID by a random number from 0 to 99.
	 */
	@Environment(EnvType.CLIENT)
	static class IdIncrementor {
		private static final int MAX_INCREMENT = 100;
		private final AbstractRandom random = AbstractRandom.create();
		private int current;

		/**
		 * {@return the next ID}
		 */
		public int next() {
			int i = this.current + this.random.nextInt(100);
			this.current = i;
			return i;
		}
	}

	/**
	 * A query, or a request, to the server to send the chat message preview.
	 */
	@Environment(EnvType.CLIENT)
	static record Query(int id, String message) {
		public boolean idEquals(int id) {
			return this.id == id;
		}

		/**
		 * {@return whether the query's queried message equals {@code message}}
		 */
		public boolean messageEquals(String message) {
			return this.message.equals(message);
		}
	}

	/**
	 * A response to the preview query.
	 */
	@Environment(EnvType.CLIENT)
	static record Response(long receivedTimeStamp, String query, @Nullable Text previewText) {
		public Response(long receivedTimeStamp, String query, @Nullable Text previewText) {
			query = ChatPreviewer.normalize(query);
			this.receivedTimeStamp = receivedTimeStamp;
			this.query = query;
			this.previewText = previewText;
		}

		/**
		 * {@return whether the response can be consumed for the {@code message}}
		 * 
		 * <p>This returns {@code true} if the {@code message} equals the queried message and
		 * the cooldown has passed.
		 */
		public boolean canConsume(String message) {
			if (this.query.equals(ChatPreviewer.normalize(message))) {
				long l = this.receivedTimeStamp + 200L;
				return Util.getMeasuringTimeMs() >= l;
			} else {
				return false;
			}
		}
	}
}
