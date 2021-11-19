package net.minecraft.world.storage;

import java.util.concurrent.CompletableFuture;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.util.math.ChunkPos;

public interface NbtScannable {
	CompletableFuture<Void> scanChunk(ChunkPos pos, NbtScanner scanner);
}
