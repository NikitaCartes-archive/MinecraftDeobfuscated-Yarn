package net.minecraft.client.report;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.report.log.ChatLog;
import net.minecraft.client.report.log.ReceivedMessage;

@Environment(EnvType.CLIENT)
public class GroupedMessagesCollector {
	private final Function<ChatLog.IndexedEntry<ReceivedMessage>, GroupedMessagesCollector.ReportType> reportTypeGetter;
	private final List<ChatLog.IndexedEntry<ReceivedMessage>> messages = new ArrayList();
	@Nullable
	private GroupedMessagesCollector.ReportType reportType;

	public GroupedMessagesCollector(Function<ChatLog.IndexedEntry<ReceivedMessage>, GroupedMessagesCollector.ReportType> reportTypeGetter) {
		this.reportTypeGetter = reportTypeGetter;
	}

	public boolean add(ChatLog.IndexedEntry<ReceivedMessage> message) {
		GroupedMessagesCollector.ReportType reportType = (GroupedMessagesCollector.ReportType)this.reportTypeGetter.apply(message);
		if (this.reportType != null && reportType != this.reportType) {
			return false;
		} else {
			this.reportType = reportType;
			this.messages.add(message);
			return true;
		}
	}

	@Nullable
	public GroupedMessagesCollector.GroupedMessages collect() {
		return !this.messages.isEmpty() && this.reportType != null ? new GroupedMessagesCollector.GroupedMessages(this.messages, this.reportType) : null;
	}

	@Environment(EnvType.CLIENT)
	public static record GroupedMessages(List<ChatLog.IndexedEntry<ReceivedMessage>> messages, GroupedMessagesCollector.ReportType type) {
	}

	@Environment(EnvType.CLIENT)
	public static enum ReportType {
		REPORTABLE,
		CONTEXT;

		public boolean isContext() {
			return this == CONTEXT;
		}
	}
}
