/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.report;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.report.log.ChatLog;
import net.minecraft.client.report.log.ReceivedMessage;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GroupedMessagesCollector<T extends ReceivedMessage> {
    private final Function<ChatLog.IndexedEntry<T>, ReportType> reportTypeGetter;
    private final List<ChatLog.IndexedEntry<T>> messages = new ArrayList<ChatLog.IndexedEntry<T>>();
    @Nullable
    private ReportType reportType;

    public GroupedMessagesCollector(Function<ChatLog.IndexedEntry<T>, ReportType> reportTypeGetter) {
        this.reportTypeGetter = reportTypeGetter;
    }

    public boolean add(ChatLog.IndexedEntry<T> message) {
        ReportType reportType = this.reportTypeGetter.apply(message);
        if (this.reportType == null || reportType == this.reportType) {
            this.reportType = reportType;
            this.messages.add(message);
            return true;
        }
        return false;
    }

    @Nullable
    public GroupedMessages<T> collect() {
        if (!this.messages.isEmpty() && this.reportType != null) {
            return new GroupedMessages<T>(this.messages, this.reportType);
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
    public record GroupedMessages<T extends ReceivedMessage>(List<ChatLog.IndexedEntry<T>> messages, ReportType type) {
    }
}

