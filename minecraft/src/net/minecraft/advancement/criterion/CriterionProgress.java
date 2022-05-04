package net.minecraft.advancement.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;

public class CriterionProgress {
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	@Nullable
	private Date obtainedDate;

	public boolean isObtained() {
		return this.obtainedDate != null;
	}

	public void obtain() {
		this.obtainedDate = new Date();
	}

	public void reset() {
		this.obtainedDate = null;
	}

	@Nullable
	public Date getObtainedDate() {
		return this.obtainedDate;
	}

	public String toString() {
		return "CriterionProgress{obtained=" + (this.obtainedDate == null ? "false" : this.obtainedDate) + "}";
	}

	public void toPacket(PacketByteBuf buf) {
		buf.writeNullable(this.obtainedDate, PacketByteBuf::writeDate);
	}

	public JsonElement toJson() {
		return (JsonElement)(this.obtainedDate != null ? new JsonPrimitive(FORMAT.format(this.obtainedDate)) : JsonNull.INSTANCE);
	}

	public static CriterionProgress fromPacket(PacketByteBuf buf) {
		CriterionProgress criterionProgress = new CriterionProgress();
		criterionProgress.obtainedDate = buf.readNullable(PacketByteBuf::readDate);
		return criterionProgress;
	}

	public static CriterionProgress obtainedAt(String datetime) {
		CriterionProgress criterionProgress = new CriterionProgress();

		try {
			criterionProgress.obtainedDate = FORMAT.parse(datetime);
			return criterionProgress;
		} catch (ParseException var3) {
			throw new JsonSyntaxException("Invalid datetime: " + datetime, var3);
		}
	}
}
