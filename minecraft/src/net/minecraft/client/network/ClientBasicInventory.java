package net.minecraft.client.network;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.Container;
import net.minecraft.container.LockContainer;
import net.minecraft.container.LockableContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.text.TextComponent;

@Environment(EnvType.CLIENT)
public class ClientBasicInventory extends BasicInventory implements LockableContainer {
	private final String id;
	private final Map<Integer, Integer> invFields = Maps.<Integer, Integer>newHashMap();

	public ClientBasicInventory(String string, TextComponent textComponent, int i) {
		super(textComponent, i);
		this.id = string;
	}

	@Override
	public int getInvProperty(int i) {
		return this.invFields.containsKey(i) ? (Integer)this.invFields.get(i) : 0;
	}

	@Override
	public void setInvProperty(int i, int j) {
		this.invFields.put(i, j);
	}

	@Override
	public int getInvPropertyCount() {
		return this.invFields.size();
	}

	@Override
	public boolean hasContainerLock() {
		return false;
	}

	@Override
	public void setContainerLock(LockContainer lockContainer) {
	}

	@Override
	public LockContainer getContainerLock() {
		return LockContainer.EMPTY;
	}

	@Override
	public String getContainerId() {
		return this.id;
	}

	@Override
	public Container createContainer(PlayerInventory playerInventory, PlayerEntity playerEntity) {
		throw new UnsupportedOperationException();
	}
}
