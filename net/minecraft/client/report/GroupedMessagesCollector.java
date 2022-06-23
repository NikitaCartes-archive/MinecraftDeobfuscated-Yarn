/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.report;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.report.ReceivedMessage;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GroupedMessagesCollector {
    private final Function<ReceivedMessage.IndexedMessage, ReportType> reportTypeGetter;
    private final List<ReceivedMessage.IndexedMessage> messages = new ArrayList<ReceivedMessage.IndexedMessage>();
    @Nullable
    private ReportType reportType;

    public GroupedMessagesCollector(Function<ReceivedMessage.IndexedMessage, ReportType> reportTypeGetter) {
        this.reportTypeGetter = reportTypeGetter;
    }

    public boolean add(ReceivedMessage.IndexedMessage message) {
        ReportType reportType = this.reportTypeGetter.apply(message);
        if (this.reportType == null || reportType == this.reportType) {
            this.reportType = reportType;
            this.messages.add(message);
            return true;
        }
        return false;
    }

    @Nullable
    public GroupedMessages collect() {
        if (!this.messages.isEmpty() && this.reportType != null) {
            return new GroupedMessages(this.messages, this.reportType);
        }
        return null;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum ReportType {
        REPORTABLE,
        CONTEXT;


        public boolean isContext() {
            return this == CONTEXT;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record GroupedMessages(List<ReceivedMessage.IndexedMessage> messages, ReportType type) {
    }
}

