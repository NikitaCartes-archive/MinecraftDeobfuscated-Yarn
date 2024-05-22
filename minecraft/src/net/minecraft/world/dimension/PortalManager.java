package net.minecraft.world.dimension;

import javax.annotation.Nullable;
import net.minecraft.block.Portal;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TeleportTarget;

public class PortalManager {
	private Portal portal;
	private BlockPos pos;
	private int ticksInPortal;
	private boolean inPortal;

	public PortalManager(Portal portal, BlockPos pos) {
		this.portal = portal;
		this.pos = pos;
		this.inPortal = true;
	}

	public boolean tick(ServerWorld world, Entity entity, boolean canUsePortals) {
		if (!this.inPortal) {
			this.decayTicksInPortal();
			return false;
		} else {
			this.inPortal = false;
			return canUsePortals && this.ticksInPortal++ >= this.portal.getPortalDelay(world, entity);
		}
	}

	@Nullable
	public TeleportTarget createTeleportTarget(ServerWorld world, Entity entity) {
		return this.portal.createTeleportTarget(world, entity, this.pos);
	}

	public Portal.Effect getEffect() {
		return this.portal.getPortalEffect();
	}

	private void decayTicksInPortal() {
		this.ticksInPortal = Math.max(this.ticksInPortal - 4, 0);
	}

	public boolean hasExpired() {
		return this.ticksInPortal <= 0;
	}

	public BlockPos getPortalPos() {
		return this.pos;
	}

	public void setPortalPos(BlockPos pos) {
		this.pos = pos;
	}

	public int getTicksInPortal() {
		return this.ticksInPortal;
	}

	public boolean isInPortal() {
		return this.inPortal;
	}

	public void setInPortal(boolean inPortal) {
		this.inPortal = inPortal;
	}

	public boolean portalMatches(Portal portal) {
		return this.portal == portal;
	}
}
