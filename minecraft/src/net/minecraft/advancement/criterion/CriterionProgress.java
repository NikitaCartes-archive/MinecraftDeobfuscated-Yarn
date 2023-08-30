package net.minecraft.advancement.criterion;

import java.time.Instant;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;

public class CriterionProgress {
	@Nullable
	private Instant obtainedDate;

	public CriterionProgress() {
	}

	public CriterionProgress(Instant instant) {
		this.obtainedDate = instant;
	}

	public boolean isObtained() {
		return this.obtainedDate != null;
	}

	public void obtain() {
		this.obtainedDate = Instant.now();
	}

	public void reset() {
		this.obtainedDate = null;
	}

	@Nullable
	public Instant getObtainedDate() {
		return this.obtainedDate;
	}

	public String toString() {
		return "CriterionProgress{obtained=" + (this.obtainedDate == null ? "false" : this.obtainedDate) + "}";
	}

	public void toPacket(PacketByteBuf buf) {
		buf.writeNullable(this.obtainedDate, PacketByteBuf::writeInstant);
	}

	public static CriterionProgress fromPacket(PacketByteBuf buf) {
		CriterionProgress criterionProgress = new CriterionProgress();
		criterionProgress.obtainedDate = buf.readNullable(PacketByteBuf::readInstant);
		return criterionProgress;
	}
}
