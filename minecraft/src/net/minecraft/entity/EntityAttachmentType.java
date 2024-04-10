package net.minecraft.entity;

import java.util.List;
import net.minecraft.util.math.Vec3d;

public enum EntityAttachmentType {
	PASSENGER(EntityAttachmentType.Point.AT_HEIGHT),
	VEHICLE(EntityAttachmentType.Point.ZERO),
	NAME_TAG(EntityAttachmentType.Point.AT_HEIGHT),
	WARDEN_CHEST(EntityAttachmentType.Point.WARDEN_CHEST);

	private final EntityAttachmentType.Point point;

	private EntityAttachmentType(final EntityAttachmentType.Point point) {
		this.point = point;
	}

	public List<Vec3d> createPoint(float width, float height) {
		return this.point.create(width, height);
	}

	public interface Point {
		List<Vec3d> NONE = List.of(Vec3d.ZERO);
		EntityAttachmentType.Point ZERO = (width, height) -> NONE;
		EntityAttachmentType.Point AT_HEIGHT = (width, height) -> List.of(new Vec3d(0.0, (double)height, 0.0));
		EntityAttachmentType.Point WARDEN_CHEST = (width, height) -> List.of(new Vec3d(0.0, (double)height / 2.0, 0.0));

		List<Vec3d> create(float width, float height);
	}
}
