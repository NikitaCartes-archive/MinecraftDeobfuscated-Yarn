package net.minecraft.client.realms.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.dto.RealmsServer;

@Environment(EnvType.CLIENT)
public class RealmsServerFilterer implements Iterable<RealmsServer> {
	private final MinecraftClient client;
	private final Set<RealmsServer> removedServers = new HashSet();
	private List<RealmsServer> sortedServers = List.of();

	public RealmsServerFilterer(MinecraftClient client) {
		this.client = client;
	}

	public void filterAndSort(List<RealmsServer> servers) {
		List<RealmsServer> list = new ArrayList(servers);
		list.sort(new RealmsServer.McoServerComparator(this.client.getSession().getUsername()));
		boolean bl = list.removeAll(this.removedServers);
		if (!bl) {
			this.removedServers.clear();
		}

		this.sortedServers = list;
	}

	public void remove(RealmsServer server) {
		this.sortedServers.remove(server);
		this.removedServers.add(server);
	}

	public Iterator<RealmsServer> iterator() {
		return this.sortedServers.iterator();
	}

	public boolean isEmpty() {
		return this.sortedServers.isEmpty();
	}
}
