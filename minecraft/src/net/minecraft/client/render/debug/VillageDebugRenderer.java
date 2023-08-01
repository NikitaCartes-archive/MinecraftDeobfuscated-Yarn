package net.minecraft.client.render.debug;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.custom.DebugBrainCustomPayload;
import net.minecraft.util.NameGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class VillageDebugRenderer implements DebugRenderer.Renderer {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final boolean field_32874 = true;
	private static final boolean field_32875 = false;
	private static final boolean field_32876 = false;
	private static final boolean field_32877 = false;
	private static final boolean field_32878 = false;
	private static final boolean field_32879 = false;
	private static final boolean field_32880 = false;
	private static final boolean field_32881 = false;
	private static final boolean field_32882 = true;
	private static final boolean field_38346 = false;
	private static final boolean field_32883 = true;
	private static final boolean field_32884 = true;
	private static final boolean field_32885 = true;
	private static final boolean field_32886 = true;
	private static final boolean field_32887 = true;
	private static final boolean field_32888 = true;
	private static final boolean field_32889 = true;
	private static final boolean field_32890 = true;
	private static final boolean field_32891 = true;
	private static final boolean field_32892 = true;
	private static final boolean field_38347 = true;
	private static final boolean field_32893 = true;
	private static final int POI_RANGE = 30;
	private static final int BRAIN_RANGE = 30;
	private static final int TARGET_ENTITY_RANGE = 8;
	private static final float DEFAULT_DRAWN_STRING_SIZE = 0.02F;
	private static final int WHITE = -1;
	private static final int YELLOW = -256;
	private static final int AQUA = -16711681;
	private static final int GREEN = -16711936;
	private static final int GRAY = -3355444;
	private static final int PINK = -98404;
	private static final int RED = -65536;
	private static final int ORANGE = -23296;
	private final MinecraftClient client;
	private final Map<BlockPos, VillageDebugRenderer.PointOfInterest> pointsOfInterest = Maps.<BlockPos, VillageDebugRenderer.PointOfInterest>newHashMap();
	private final Map<UUID, DebugBrainCustomPayload.Brain> brains = Maps.<UUID, DebugBrainCustomPayload.Brain>newHashMap();
	@Nullable
	private UUID targetedEntity;

	public VillageDebugRenderer(MinecraftClient client) {
		this.client = client;
	}

	@Override
	public void clear() {
		this.pointsOfInterest.clear();
		this.brains.clear();
		this.targetedEntity = null;
	}

	public void addPointOfInterest(VillageDebugRenderer.PointOfInterest poi) {
		this.pointsOfInterest.put(poi.pos, poi);
	}

	public void removePointOfInterest(BlockPos pos) {
		this.pointsOfInterest.remove(pos);
	}

	public void setFreeTicketCount(BlockPos pos, int freeTicketCount) {
		VillageDebugRenderer.PointOfInterest pointOfInterest = (VillageDebugRenderer.PointOfInterest)this.pointsOfInterest.get(pos);
		if (pointOfInterest == null) {
			LOGGER.warn("Strange, setFreeTicketCount was called for an unknown POI: {}", pos);
		} else {
			pointOfInterest.freeTicketCount = freeTicketCount;
		}
	}

	public void addBrain(DebugBrainCustomPayload.Brain brain) {
		this.brains.put(brain.uuid(), brain);
	}

	public void removeBrain(int entityId) {
		this.brains.values().removeIf(brain -> brain.entityId() == entityId);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		this.removeRemovedBrains();
		this.draw(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
		if (!this.client.player.isSpectator()) {
			this.updateTargetedEntity();
		}
	}

	private void removeRemovedBrains() {
		this.brains.entrySet().removeIf(entry -> {
			Entity entity = this.client.world.getEntityById(((DebugBrainCustomPayload.Brain)entry.getValue()).entityId());
			return entity == null || entity.isRemoved();
		});
	}

	private void draw(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double x, double y, double z) {
		BlockPos blockPos = BlockPos.ofFloored(x, y, z);
		this.brains.values().forEach(brain -> {
			if (this.isClose(brain)) {
				this.drawBrain(matrices, vertexConsumers, brain, x, y, z);
			}
		});

		for (BlockPos blockPos2 : this.pointsOfInterest.keySet()) {
			if (blockPos.isWithinDistance(blockPos2, 30.0)) {
				drawPointOfInterest(matrices, vertexConsumers, blockPos2);
			}
		}

		this.pointsOfInterest.values().forEach(poi -> {
			if (blockPos.isWithinDistance(poi.pos, 30.0)) {
				this.drawPointOfInterestInfo(matrices, vertexConsumers, poi);
			}
		});
		this.getGhostPointsOfInterest().forEach((pos, brains) -> {
			if (blockPos.isWithinDistance(pos, 30.0)) {
				this.drawGhostPointOfInterest(matrices, vertexConsumers, pos, brains);
			}
		});
	}

	private static void drawPointOfInterest(MatrixStack matrices, VertexConsumerProvider vertexConsumers, BlockPos pos) {
		float f = 0.05F;
		DebugRenderer.drawBox(matrices, vertexConsumers, pos, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
	}

	private void drawGhostPointOfInterest(MatrixStack matrices, VertexConsumerProvider vertexConsumers, BlockPos pos, List<String> brains) {
		float f = 0.05F;
		DebugRenderer.drawBox(matrices, vertexConsumers, pos, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
		drawString(matrices, vertexConsumers, brains + "", pos, 0, -256);
		drawString(matrices, vertexConsumers, "Ghost POI", pos, 1, -65536);
	}

	private void drawPointOfInterestInfo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, VillageDebugRenderer.PointOfInterest pointOfInterest) {
		int i = 0;
		Set<String> set = this.getNamesOfPointOfInterestTicketHolders(pointOfInterest);
		if (set.size() < 4) {
			drawString(matrices, vertexConsumers, "Owners: " + set, pointOfInterest, i, -256);
		} else {
			drawString(matrices, vertexConsumers, set.size() + " ticket holders", pointOfInterest, i, -256);
		}

		i++;
		Set<String> set2 = this.getNamesOfJobSitePotentialOwners(pointOfInterest);
		if (set2.size() < 4) {
			drawString(matrices, vertexConsumers, "Candidates: " + set2, pointOfInterest, i, -23296);
		} else {
			drawString(matrices, vertexConsumers, set2.size() + " potential owners", pointOfInterest, i, -23296);
		}

		drawString(matrices, vertexConsumers, "Free tickets: " + pointOfInterest.freeTicketCount, pointOfInterest, ++i, -256);
		drawString(matrices, vertexConsumers, pointOfInterest.type, pointOfInterest, ++i, -1);
	}

	private void drawPath(
		MatrixStack matrices, VertexConsumerProvider vertexConsumers, DebugBrainCustomPayload.Brain brain, double cameraX, double cameraY, double cameraZ
	) {
		if (brain.path() != null) {
			PathfindingDebugRenderer.drawPath(matrices, vertexConsumers, brain.path(), 0.5F, false, false, cameraX, cameraY, cameraZ);
		}
	}

	private void drawBrain(
		MatrixStack matrices, VertexConsumerProvider vertexConsumers, DebugBrainCustomPayload.Brain brain, double cameraX, double cameraY, double cameraZ
	) {
		boolean bl = this.isTargeted(brain);
		int i = 0;
		drawString(matrices, vertexConsumers, brain.pos(), i, brain.name(), -1, 0.03F);
		i++;
		if (bl) {
			drawString(matrices, vertexConsumers, brain.pos(), i, brain.profession() + " " + brain.xp() + " xp", -1, 0.02F);
			i++;
		}

		if (bl) {
			int j = brain.health() < brain.maxHealth() ? -23296 : -1;
			drawString(
				matrices,
				vertexConsumers,
				brain.pos(),
				i,
				"health: " + String.format(Locale.ROOT, "%.1f", brain.health()) + " / " + String.format(Locale.ROOT, "%.1f", brain.maxHealth()),
				j,
				0.02F
			);
			i++;
		}

		if (bl && !brain.inventory().equals("")) {
			drawString(matrices, vertexConsumers, brain.pos(), i, brain.inventory(), -98404, 0.02F);
			i++;
		}

		if (bl) {
			for (String string : brain.runningTasks()) {
				drawString(matrices, vertexConsumers, brain.pos(), i, string, -16711681, 0.02F);
				i++;
			}
		}

		if (bl) {
			for (String string : brain.possibleActivities()) {
				drawString(matrices, vertexConsumers, brain.pos(), i, string, -16711936, 0.02F);
				i++;
			}
		}

		if (brain.wantsGolem()) {
			drawString(matrices, vertexConsumers, brain.pos(), i, "Wants Golem", -23296, 0.02F);
			i++;
		}

		if (bl && brain.angerLevel() != -1) {
			drawString(matrices, vertexConsumers, brain.pos(), i, "Anger Level: " + brain.angerLevel(), -98404, 0.02F);
			i++;
		}

		if (bl) {
			for (String string : brain.gossips()) {
				if (string.startsWith(brain.name())) {
					drawString(matrices, vertexConsumers, brain.pos(), i, string, -1, 0.02F);
				} else {
					drawString(matrices, vertexConsumers, brain.pos(), i, string, -23296, 0.02F);
				}

				i++;
			}
		}

		if (bl) {
			for (String string : Lists.reverse(brain.memories())) {
				drawString(matrices, vertexConsumers, brain.pos(), i, string, -3355444, 0.02F);
				i++;
			}
		}

		if (bl) {
			this.drawPath(matrices, vertexConsumers, brain, cameraX, cameraY, cameraZ);
		}
	}

	private static void drawString(
		MatrixStack matrices, VertexConsumerProvider vertexConsumers, String string, VillageDebugRenderer.PointOfInterest pointOfInterest, int offsetY, int color
	) {
		drawString(matrices, vertexConsumers, string, pointOfInterest.pos, offsetY, color);
	}

	private static void drawString(MatrixStack matrices, VertexConsumerProvider vertexConsumers, String string, BlockPos pos, int offsetY, int color) {
		double d = 1.3;
		double e = 0.2;
		double f = (double)pos.getX() + 0.5;
		double g = (double)pos.getY() + 1.3 + (double)offsetY * 0.2;
		double h = (double)pos.getZ() + 0.5;
		DebugRenderer.drawString(matrices, vertexConsumers, string, f, g, h, color, 0.02F, true, 0.0F, true);
	}

	private static void drawString(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Position pos, int offsetY, String string, int color, float size) {
		double d = 2.4;
		double e = 0.25;
		BlockPos blockPos = BlockPos.ofFloored(pos);
		double f = (double)blockPos.getX() + 0.5;
		double g = pos.getY() + 2.4 + (double)offsetY * 0.25;
		double h = (double)blockPos.getZ() + 0.5;
		float i = 0.5F;
		DebugRenderer.drawString(matrices, vertexConsumers, string, f, g, h, color, size, false, 0.5F, true);
	}

	private Set<String> getNamesOfPointOfInterestTicketHolders(VillageDebugRenderer.PointOfInterest pointOfInterest) {
		return (Set<String>)this.getBrainsContainingPointOfInterest(pointOfInterest.pos).stream().map(NameGenerator::name).collect(Collectors.toSet());
	}

	private Set<String> getNamesOfJobSitePotentialOwners(VillageDebugRenderer.PointOfInterest potentialJobSite) {
		return (Set<String>)this.getBrainsContainingPotentialJobSite(potentialJobSite.pos).stream().map(NameGenerator::name).collect(Collectors.toSet());
	}

	private boolean isTargeted(DebugBrainCustomPayload.Brain brain) {
		return Objects.equals(this.targetedEntity, brain.uuid());
	}

	private boolean isClose(DebugBrainCustomPayload.Brain brain) {
		PlayerEntity playerEntity = this.client.player;
		BlockPos blockPos = BlockPos.ofFloored(playerEntity.getX(), brain.pos().getY(), playerEntity.getZ());
		BlockPos blockPos2 = BlockPos.ofFloored(brain.pos());
		return blockPos.isWithinDistance(blockPos2, 30.0);
	}

	private Collection<UUID> getBrainsContainingPointOfInterest(BlockPos pointOfInterest) {
		return (Collection<UUID>)this.brains
			.values()
			.stream()
			.filter(brain -> brain.isPointOfInterest(pointOfInterest))
			.map(DebugBrainCustomPayload.Brain::uuid)
			.collect(Collectors.toSet());
	}

	private Collection<UUID> getBrainsContainingPotentialJobSite(BlockPos potentialJobSite) {
		return (Collection<UUID>)this.brains
			.values()
			.stream()
			.filter(brain -> brain.isPotentialJobSite(potentialJobSite))
			.map(DebugBrainCustomPayload.Brain::uuid)
			.collect(Collectors.toSet());
	}

	private Map<BlockPos, List<String>> getGhostPointsOfInterest() {
		Map<BlockPos, List<String>> map = Maps.<BlockPos, List<String>>newHashMap();

		for (DebugBrainCustomPayload.Brain brain : this.brains.values()) {
			for (BlockPos blockPos : Iterables.concat(brain.pois(), brain.potentialPois())) {
				if (!this.pointsOfInterest.containsKey(blockPos)) {
					((List)map.computeIfAbsent(blockPos, pos -> Lists.newArrayList())).add(brain.name());
				}
			}
		}

		return map;
	}

	private void updateTargetedEntity() {
		DebugRenderer.getTargetedEntity(this.client.getCameraEntity(), 8).ifPresent(entity -> this.targetedEntity = entity.getUuid());
	}

	@Environment(EnvType.CLIENT)
	public static class PointOfInterest {
		public final BlockPos pos;
		public final String type;
		public int freeTicketCount;

		public PointOfInterest(BlockPos pos, String type, int freeTicketCount) {
			this.pos = pos;
			this.type = type;
			this.freeTicketCount = freeTicketCount;
		}
	}
}
