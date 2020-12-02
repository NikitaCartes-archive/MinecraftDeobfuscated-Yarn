package net.minecraft;

import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.event.listener.GameEventListener;

public class class_5715 {
	private final GameEventListener field_28182;
	@Nullable
	private ChunkSectionPos field_28183;

	public class_5715(GameEventListener gameEventListener) {
		this.field_28182 = gameEventListener;
	}

	public void method_32949(World world) {
		this.method_32950(world, this.field_28183, arg -> arg.removeListener(this.field_28182));
	}

	public void method_32952(World world) {
		Optional<BlockPos> optional = this.field_28182.getPositionSource().getPos(world);
		if (optional.isPresent()) {
			long l = ChunkSectionPos.fromBlockPos(((BlockPos)optional.get()).asLong());
			if (this.field_28183 == null || this.field_28183.asLong() != l) {
				ChunkSectionPos chunkSectionPos = this.field_28183;
				this.field_28183 = ChunkSectionPos.from(l);
				this.method_32950(world, chunkSectionPos, arg -> arg.removeListener(this.field_28182));
				this.method_32950(world, this.field_28183, arg -> arg.addListener(this.field_28182));
			}
		}
	}

	private void method_32950(World world, @Nullable ChunkSectionPos chunkSectionPos, Consumer<class_5713> consumer) {
		if (chunkSectionPos != null) {
			Chunk chunk = world.getChunk(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), ChunkStatus.FULL, false);
			if (chunk != null) {
				consumer.accept(chunk.method_32914(chunkSectionPos.getSectionY()));
			}
		}
	}
}
