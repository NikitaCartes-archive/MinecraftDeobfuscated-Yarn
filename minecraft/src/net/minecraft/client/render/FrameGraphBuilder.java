package net.minecraft.client.render;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.ClosableFactory;
import net.minecraft.client.util.ObjectAllocator;

@Environment(EnvType.CLIENT)
public class FrameGraphBuilder {
	private final List<FrameGraphBuilder.ResourceNode<?>> resourceNodes = new ArrayList();
	private final List<FrameGraphBuilder.ObjectNode<?>> objectNodes = new ArrayList();
	private final List<FrameGraphBuilder.FramePass> passes = new ArrayList();

	public RenderPass createPass(String name) {
		FrameGraphBuilder.FramePass framePass = new FrameGraphBuilder.FramePass(this.passes.size(), name);
		this.passes.add(framePass);
		return framePass;
	}

	public <T> net.minecraft.client.util.Handle<T> createObjectNode(String name, T object) {
		FrameGraphBuilder.ObjectNode<T> objectNode = new FrameGraphBuilder.ObjectNode<>(name, null, object);
		this.objectNodes.add(objectNode);
		return objectNode.handle;
	}

	public <T> net.minecraft.client.util.Handle<T> createResourceHandle(String name, ClosableFactory<T> factory) {
		return this.createResourceNode(name, factory, null).handle;
	}

	<T> FrameGraphBuilder.ResourceNode<T> createResourceNode(String name, ClosableFactory<T> factory, @Nullable FrameGraphBuilder.FramePass stageNode) {
		int i = this.resourceNodes.size();
		FrameGraphBuilder.ResourceNode<T> resourceNode = new FrameGraphBuilder.ResourceNode<>(i, name, stageNode, factory);
		this.resourceNodes.add(resourceNode);
		return resourceNode;
	}

	public void run(ObjectAllocator allocator) {
		this.run(allocator, FrameGraphBuilder.Profiler.NONE);
	}

	public void run(ObjectAllocator allocator, FrameGraphBuilder.Profiler profiler) {
		BitSet bitSet = this.collectPassesToVisit();
		List<FrameGraphBuilder.FramePass> list = new ArrayList(bitSet.cardinality());
		BitSet bitSet2 = new BitSet(this.passes.size());

		for (FrameGraphBuilder.FramePass framePass : this.passes) {
			this.visit(framePass, bitSet, bitSet2, list);
		}

		this.checkResources(list);

		for (FrameGraphBuilder.FramePass framePass : list) {
			for (FrameGraphBuilder.ResourceNode<?> resourceNode : framePass.resourcesToAcquire) {
				profiler.acquire(resourceNode.name);
				resourceNode.acquire(allocator);
			}

			profiler.push(framePass.name);
			framePass.renderer.run();
			profiler.pop(framePass.name);

			for (int i = framePass.resourcesToRelease.nextSetBit(0); i >= 0; i = framePass.resourcesToRelease.nextSetBit(i + 1)) {
				FrameGraphBuilder.ResourceNode<?> resourceNode = (FrameGraphBuilder.ResourceNode<?>)this.resourceNodes.get(i);
				profiler.release(resourceNode.name);
				resourceNode.release(allocator);
			}
		}
	}

	private BitSet collectPassesToVisit() {
		Deque<FrameGraphBuilder.FramePass> deque = new ArrayDeque(this.passes.size());
		BitSet bitSet = new BitSet(this.passes.size());

		for (FrameGraphBuilder.Node<?> node : this.objectNodes) {
			FrameGraphBuilder.FramePass framePass = node.handle.from;
			if (framePass != null) {
				this.markForVisit(framePass, bitSet, deque);
			}
		}

		for (FrameGraphBuilder.FramePass framePass2 : this.passes) {
			if (framePass2.toBeVisited) {
				this.markForVisit(framePass2, bitSet, deque);
			}
		}

		return bitSet;
	}

	private void markForVisit(FrameGraphBuilder.FramePass pass, BitSet result, Deque<FrameGraphBuilder.FramePass> deque) {
		deque.add(pass);

		while (!deque.isEmpty()) {
			FrameGraphBuilder.FramePass framePass = (FrameGraphBuilder.FramePass)deque.poll();
			if (!result.get(framePass.id)) {
				result.set(framePass.id);

				for (int i = framePass.requiredPassIds.nextSetBit(0); i >= 0; i = framePass.requiredPassIds.nextSetBit(i + 1)) {
					deque.add((FrameGraphBuilder.FramePass)this.passes.get(i));
				}
			}
		}
	}

	private void visit(FrameGraphBuilder.FramePass node, BitSet unvisited, BitSet visiting, List<FrameGraphBuilder.FramePass> topologicalOrderOut) {
		if (visiting.get(node.id)) {
			String string = (String)visiting.stream().mapToObj(id -> ((FrameGraphBuilder.FramePass)this.passes.get(id)).name).collect(Collectors.joining(", "));
			throw new IllegalStateException("Frame graph cycle detected between " + string);
		} else if (unvisited.get(node.id)) {
			visiting.set(node.id);
			unvisited.clear(node.id);

			for (int i = node.requiredPassIds.nextSetBit(0); i >= 0; i = node.requiredPassIds.nextSetBit(i + 1)) {
				this.visit((FrameGraphBuilder.FramePass)this.passes.get(i), unvisited, visiting, topologicalOrderOut);
			}

			for (FrameGraphBuilder.Handle<?> handle : node.transferredHandles) {
				for (int j = handle.dependents.nextSetBit(0); j >= 0; j = handle.dependents.nextSetBit(j + 1)) {
					if (j != node.id) {
						this.visit((FrameGraphBuilder.FramePass)this.passes.get(j), unvisited, visiting, topologicalOrderOut);
					}
				}
			}

			topologicalOrderOut.add(node);
			visiting.clear(node.id);
		}
	}

	private void checkResources(Collection<FrameGraphBuilder.FramePass> passes) {
		FrameGraphBuilder.FramePass[] framePasss = new FrameGraphBuilder.FramePass[this.resourceNodes.size()];

		for (FrameGraphBuilder.FramePass framePass : passes) {
			for (int i = framePass.requiredResourceIds.nextSetBit(0); i >= 0; i = framePass.requiredResourceIds.nextSetBit(i + 1)) {
				FrameGraphBuilder.ResourceNode<?> resourceNode = (FrameGraphBuilder.ResourceNode<?>)this.resourceNodes.get(i);
				FrameGraphBuilder.FramePass framePass2 = framePasss[i];
				framePasss[i] = framePass;
				if (framePass2 == null) {
					framePass.resourcesToAcquire.add(resourceNode);
				} else {
					framePass2.resourcesToRelease.clear(i);
				}

				framePass.resourcesToRelease.set(i);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class FramePass implements RenderPass {
		final int id;
		final String name;
		final List<FrameGraphBuilder.Handle<?>> transferredHandles = new ArrayList();
		final BitSet requiredResourceIds = new BitSet();
		final BitSet requiredPassIds = new BitSet();
		Runnable renderer = () -> {
		};
		final List<FrameGraphBuilder.ResourceNode<?>> resourcesToAcquire = new ArrayList();
		final BitSet resourcesToRelease = new BitSet();
		boolean toBeVisited;

		public FramePass(final int id, final String name) {
			this.id = id;
			this.name = name;
		}

		private <T> void addRequired(FrameGraphBuilder.Handle<T> handle) {
			if (handle.parent instanceof FrameGraphBuilder.ResourceNode<?> resourceNode) {
				this.requiredResourceIds.set(resourceNode.id);
			}
		}

		private void addRequired(FrameGraphBuilder.FramePass child) {
			this.requiredPassIds.set(child.id);
		}

		@Override
		public <T> net.minecraft.client.util.Handle<T> addRequiredResource(String name, ClosableFactory<T> factory) {
			FrameGraphBuilder.ResourceNode<T> resourceNode = FrameGraphBuilder.this.createResourceNode(name, factory, this);
			this.requiredResourceIds.set(resourceNode.id);
			return resourceNode.handle;
		}

		@Override
		public <T> void dependsOn(net.minecraft.client.util.Handle<T> handle) {
			this.dependsOn((FrameGraphBuilder.Handle<T>)handle);
		}

		private <T> void dependsOn(FrameGraphBuilder.Handle<T> handle) {
			this.addRequired(handle);
			if (handle.from != null) {
				this.addRequired(handle.from);
			}

			handle.dependents.set(this.id);
		}

		@Override
		public <T> net.minecraft.client.util.Handle<T> transfer(net.minecraft.client.util.Handle<T> handle) {
			return this.transfer((FrameGraphBuilder.Handle<T>)handle);
		}

		@Override
		public void addRequired(RenderPass pass) {
			this.requiredPassIds.set(((FrameGraphBuilder.FramePass)pass).id);
		}

		@Override
		public void markToBeVisited() {
			this.toBeVisited = true;
		}

		private <T> FrameGraphBuilder.Handle<T> transfer(FrameGraphBuilder.Handle<T> handle) {
			this.transferredHandles.add(handle);
			this.dependsOn(handle);
			return handle.moveTo(this);
		}

		@Override
		public void setRenderer(Runnable renderer) {
			this.renderer = renderer;
		}

		public String toString() {
			return this.name;
		}
	}

	@Environment(EnvType.CLIENT)
	static class Handle<T> implements net.minecraft.client.util.Handle<T> {
		final FrameGraphBuilder.Node<T> parent;
		private final int id;
		@Nullable
		final FrameGraphBuilder.FramePass from;
		final BitSet dependents = new BitSet();
		@Nullable
		private FrameGraphBuilder.Handle<T> movedTo;

		Handle(FrameGraphBuilder.Node<T> parent, int id, @Nullable FrameGraphBuilder.FramePass from) {
			this.parent = parent;
			this.id = id;
			this.from = from;
		}

		@Override
		public T get() {
			return this.parent.get();
		}

		FrameGraphBuilder.Handle<T> moveTo(FrameGraphBuilder.FramePass pass) {
			if (this.parent.handle != this) {
				throw new IllegalStateException("Handle " + this + " is no longer valid, as its contents were moved into " + this.movedTo);
			} else {
				FrameGraphBuilder.Handle<T> handle = new FrameGraphBuilder.Handle<>(this.parent, this.id + 1, pass);
				this.parent.handle = handle;
				this.movedTo = handle;
				return handle;
			}
		}

		public String toString() {
			return this.from != null ? this.parent + "#" + this.id + " (from " + this.from + ")" : this.parent + "#" + this.id;
		}
	}

	@Environment(EnvType.CLIENT)
	abstract static class Node<T> {
		public final String name;
		public FrameGraphBuilder.Handle<T> handle;

		public Node(String name, @Nullable FrameGraphBuilder.FramePass from) {
			this.name = name;
			this.handle = new FrameGraphBuilder.Handle<>(this, 0, from);
		}

		public abstract T get();

		public String toString() {
			return this.name;
		}
	}

	@Environment(EnvType.CLIENT)
	static class ObjectNode<T> extends FrameGraphBuilder.Node<T> {
		private final T value;

		public ObjectNode(String name, @Nullable FrameGraphBuilder.FramePass parent, T value) {
			super(name, parent);
			this.value = value;
		}

		@Override
		public T get() {
			return this.value;
		}
	}

	@Environment(EnvType.CLIENT)
	public interface Profiler {
		FrameGraphBuilder.Profiler NONE = new FrameGraphBuilder.Profiler() {
		};

		default void acquire(String name) {
		}

		default void release(String name) {
		}

		default void push(String location) {
		}

		default void pop(String location) {
		}
	}

	@Environment(EnvType.CLIENT)
	static class ResourceNode<T> extends FrameGraphBuilder.Node<T> {
		final int id;
		private final ClosableFactory<T> factory;
		@Nullable
		private T resource;

		public ResourceNode(int id, String name, @Nullable FrameGraphBuilder.FramePass from, ClosableFactory<T> factory) {
			super(name, from);
			this.id = id;
			this.factory = factory;
		}

		@Override
		public T get() {
			return (T)Objects.requireNonNull(this.resource, "Resource is not currently available");
		}

		public void acquire(ObjectAllocator allocator) {
			if (this.resource != null) {
				throw new IllegalStateException("Tried to acquire physical resource, but it was already assigned");
			} else {
				this.resource = allocator.acquire(this.factory);
			}
		}

		public void release(ObjectAllocator allocator) {
			if (this.resource == null) {
				throw new IllegalStateException("Tried to release physical resource that was not allocated");
			} else {
				allocator.release(this.factory, this.resource);
				this.resource = null;
			}
		}
	}
}
