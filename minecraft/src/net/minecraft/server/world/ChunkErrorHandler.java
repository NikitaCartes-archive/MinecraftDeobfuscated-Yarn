package net.minecraft.server.world;

import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.StorageKey;

public interface ChunkErrorHandler {
	void onChunkLoadFailure(Throwable exception, StorageKey key, ChunkPos chunkPos);

	void onChunkSaveFailure(Throwable exception, StorageKey key, ChunkPos chunkPos);

	static CrashException createMisplacementException(ChunkPos actualPos, ChunkPos expectedPos) {
		CrashReport crashReport = CrashReport.create(
			new IllegalStateException("Retrieved chunk position " + actualPos + " does not match requested " + expectedPos), "Chunk found in invalid location"
		);
		CrashReportSection crashReportSection = crashReport.addElement("Misplaced Chunk");
		crashReportSection.add("Stored Position", actualPos::toString);
		return new CrashException(crashReport);
	}

	default void onChunkMisplacement(ChunkPos actualPos, ChunkPos expectedPos, StorageKey key) {
		this.onChunkLoadFailure(createMisplacementException(actualPos, expectedPos), key, expectedPos);
	}
}
