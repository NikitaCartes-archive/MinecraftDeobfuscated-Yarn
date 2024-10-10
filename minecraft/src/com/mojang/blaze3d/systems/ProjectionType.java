package com.mojang.blaze3d.systems;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public enum ProjectionType {
	PERSPECTIVE(VertexSorter.BY_DISTANCE, (matrix, f) -> matrix.scale(1.0F - f / 4096.0F)),
	ORTHOGRAPHIC(VertexSorter.BY_Z, (matrix, f) -> matrix.translate(0.0F, 0.0F, f / 512.0F));

	private final VertexSorter vertexSorter;
	private final ProjectionType.Applier applier;

	private ProjectionType(final VertexSorter vertexSorter, final ProjectionType.Applier applier) {
		this.vertexSorter = vertexSorter;
		this.applier = applier;
	}

	public VertexSorter getVertexSorter() {
		return this.vertexSorter;
	}

	public void apply(Matrix4f matrix, float f) {
		this.applier.apply(matrix, f);
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	interface Applier {
		void apply(Matrix4f matrix, float f);
	}
}
