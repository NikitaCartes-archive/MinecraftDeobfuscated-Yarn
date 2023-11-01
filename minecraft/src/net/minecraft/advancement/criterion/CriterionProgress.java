package net.minecraft.advancement.criterion;

import java.time.Instant;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;

public class CriterionProgress {
	@Nullable
	private Instant obtainedTime;

	public CriterionProgress() {
	}

	public CriterionProgress(Instant obtainedTime) {
		this.obtainedTime = obtainedTime;
	}

	public boolean isObtained() {
		return this.obtainedTime != null;
	}

	public void obtain() {
		this.obtainedTime = Instant.now();
	}

	public void reset() {
		this.obtainedTime = null;
	}

	@Nullable
	public Instant getObtainedTime() {
		return this.obtainedTime;
	}

	public String toString() {
		return "CriterionProgress{obtained=" + (this.obtainedTime == null ? "false" : this.obtainedTime) + "}";
	}

	public void toPacket(PacketByteBuf buf) {
		buf.writeNullable(this.obtainedTime, PacketByteBuf::writeInstant);
	}

	public static CriterionProgress fromPacket(PacketByteBuf buf) {
		CriterionProgress criterionProgress = new CriterionProgress();
		criterionProgress.obtainedTime = buf.readNullable(PacketByteBuf::readInstant);
		return criterionProgress;
	}
}
