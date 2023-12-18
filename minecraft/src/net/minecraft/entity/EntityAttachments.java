package net.minecraft.entity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntityAttachments {
	private final Map<EntityAttachmentType, List<Vec3d>> points;

	EntityAttachments(Map<EntityAttachmentType, List<Vec3d>> points) {
		this.points = points;
	}

	public static EntityAttachments of(float width, float height) {
		return builder().build(width, height);
	}

	public static EntityAttachments.Builder builder() {
		return new EntityAttachments.Builder();
	}

	public EntityAttachments scale(float xScale, float yScale, float zScale) {
		Map<EntityAttachmentType, List<Vec3d>> map = new EnumMap(EntityAttachmentType.class);

		for (Entry<EntityAttachmentType, List<Vec3d>> entry : this.points.entrySet()) {
			map.put((EntityAttachmentType)entry.getKey(), scalePoints((List<Vec3d>)entry.getValue(), xScale, yScale, zScale));
		}

		return new EntityAttachments(map);
	}

	private static List<Vec3d> scalePoints(List<Vec3d> points, float xScale, float yScale, float zScale) {
		List<Vec3d> list = new ArrayList(points.size());

		for (Vec3d vec3d : points) {
			list.add(vec3d.multiply((double)xScale, (double)yScale, (double)zScale));
		}

		return list;
	}

	@Nullable
	public Vec3d getPointNullable(EntityAttachmentType type, int index, float yaw) {
		List<Vec3d> list = (List<Vec3d>)this.points.get(type);
		return index >= 0 && index < list.size() ? rotatePoint((Vec3d)list.get(index), yaw) : null;
	}

	public Vec3d getPoint(EntityAttachmentType type, int index, float yaw) {
		Vec3d vec3d = this.getPointNullable(type, index, yaw);
		if (vec3d == null) {
			throw new IllegalStateException("Had no attachment point of type: " + type + " for index: " + index);
		} else {
			return vec3d;
		}
	}

	public Vec3d getPointOrDefault(EntityAttachmentType type, int index, float yaw) {
		List<Vec3d> list = (List<Vec3d>)this.points.get(type);
		if (list.isEmpty()) {
			throw new IllegalStateException("Had no attachment points of type: " + type);
		} else {
			Vec3d vec3d = (Vec3d)list.get(MathHelper.clamp(index, 0, list.size() - 1));
			return rotatePoint(vec3d, yaw);
		}
	}

	private static Vec3d rotatePoint(Vec3d point, float yaw) {
		return point.rotateY(-yaw * (float) (Math.PI / 180.0));
	}

	public static class Builder {
		private final Map<EntityAttachmentType, List<Vec3d>> points = new EnumMap(EntityAttachmentType.class);

		Builder() {
		}

		public EntityAttachments.Builder add(EntityAttachmentType type, float x, float y, float z) {
			return this.add(type, new Vec3d((double)x, (double)y, (double)z));
		}

		public EntityAttachments.Builder add(EntityAttachmentType type, Vec3d point) {
			((List)this.points.computeIfAbsent(type, list -> new ArrayList(1))).add(point);
			return this;
		}

		public EntityAttachments build(float width, float height) {
			Map<EntityAttachmentType, List<Vec3d>> map = new EnumMap(EntityAttachmentType.class);

			for (EntityAttachmentType entityAttachmentType : EntityAttachmentType.values()) {
				List<Vec3d> list = (List<Vec3d>)this.points.get(entityAttachmentType);
				map.put(entityAttachmentType, list != null ? List.copyOf(list) : entityAttachmentType.createPoint(width, height));
			}

			return new EntityAttachments(map);
		}
	}
}
