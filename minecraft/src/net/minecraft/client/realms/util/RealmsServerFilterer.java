package net.minecraft.client.realms.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.dto.RealmsServer;

@Environment(EnvType.CLIENT)
public class RealmsServerFilterer {
	private final MinecraftClient client;
	private final Set<RealmsServer> removedServers = Sets.<RealmsServer>newHashSet();
	private List<RealmsServer> sortedServers = Lists.<RealmsServer>newArrayList();

	public RealmsServerFilterer(MinecraftClient client) {
		this.client = client;
	}

	public List<RealmsServer> filterAndSort(List<RealmsServer> servers) {
		List<RealmsServer> list = new ArrayList(servers);
		list.sort(new RealmsServer.McoServerComparator(this.client.getSession().getUsername()));
		boolean bl = list.removeAll(this.removedServers);
		if (!bl) {
			this.removedServers.clear();
		}

		this.sortedServers = list;
		return List.copyOf(this.sortedServers);
	}

	public synchronized List<RealmsServer> remove(RealmsServer server) {
		this.sortedServers.remove(server);
		this.removedServers.add(server);
		return List.copyOf(this.sortedServers);
	}
}
