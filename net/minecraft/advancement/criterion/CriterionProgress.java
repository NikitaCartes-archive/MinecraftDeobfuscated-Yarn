/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.network.PacketByteBuf;

public class CriterionProgress {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private Date obtained;

    public boolean isObtained() {
        return this.obtained != null;
    }

    public void obtain() {
        this.obtained = new Date();
    }

    public void reset() {
        this.obtained = null;
    }

    public Date getObtainedDate() {
        return this.obtained;
    }

    public String toString() {
        return "CriterionProgress{obtained=" + (this.obtained == null ? "false" : this.obtained) + '}';
    }

    public void toPacket(PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBoolean(this.obtained != null);
        if (this.obtained != null) {
            packetByteBuf.writeDate(this.obtained);
        }
    }

    public JsonElement toJson() {
        if (this.obtained != null) {
            return new JsonPrimitive(FORMAT.format(this.obtained));
        }
        return JsonNull.INSTANCE;
    }

    public static CriterionProgress fromPacket(PacketByteBuf packetByteBuf) {
        CriterionProgress criterionProgress = new CriterionProgress();
        if (packetByteBuf.readBoolean()) {
            criterionProgress.obtained = packetByteBuf.readDate();
        }
        return criterionProgress;
    }

    public static CriterionProgress obtainedAt(String datetime) {
        CriterionProgress criterionProgress = new CriterionProgress();
        try {
            criterionProgress.obtained = FORMAT.parse(datetime);
        } catch (ParseException parseException) {
            throw new JsonSyntaxException("Invalid datetime: " + datetime, parseException);
        }
        return criterionProgress;
    }
}

