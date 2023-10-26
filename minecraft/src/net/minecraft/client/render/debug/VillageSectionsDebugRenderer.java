package net.minecraft.client.render.debug;

import com.google.common.collect.Sets;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

@Environment(EnvType.CLIENT)
public class VillageSectionsDebugRenderer implements DebugRenderer.Renderer {
	private static final int RANGE = 60;
	private final Set<ChunkSectionPos> sections = Sets.<ChunkSectionPos>newHashSet();

	VillageSectionsDebugRenderer() {
	}

	@Override
	public void clear() {
		this.sections.clear();
	}

	public void addSection(ChunkSectionPos pos) {
		this.sections.add(pos);
	}

	public void removeSection(ChunkSectionPos pos) {
		this.sections.remove(pos);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		BlockPos blockPos = BlockPos.ofFloored(cameraX, cameraY, cameraZ);
		this.sections.forEach(section -> {
			if (blockPos.isWithinDistance(section.getCenterPos(), 60.0)) {
				drawBoxAtCenterOf(matrices, vertexConsumers, section);
			}
		});
	}

	private static void drawBoxAtCenterOf(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ChunkSectionPos sectionPos) {
		DebugRenderer.drawBlockBox(matrices, vertexConsumers, sectionPos.getCenterPos(), 0.2F, 1.0F, 0.2F, 0.15F);
	}
}
