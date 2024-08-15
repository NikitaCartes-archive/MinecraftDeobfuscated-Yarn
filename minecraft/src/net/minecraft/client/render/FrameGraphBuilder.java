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
import net.minecraft.class_9916;
import net.minecraft.client.util.ClosableFactory;
import net.minecraft.client.util.ObjectAllocator;

@Environment(EnvType.CLIENT)
public class FrameGraphBuilder {
	private final List<FrameGraphBuilder.ResourceNode<?>> resourceNodes = new ArrayList();
	private final List<FrameGraphBuilder.ObjectNode<?>> objectNodes = new ArrayList();
	private final List<FrameGraphBuilder.StageNode> stageNodes = new ArrayList();

	public class_9916 createStageNode(String name) {
		FrameGraphBuilder.StageNode stageNode = new FrameGraphBuilder.StageNode(this.stageNodes.size(), name);
		this.stageNodes.add(stageNode);
		return stageNode;
	}

	public <T> net.minecraft.client.util.Handle<T> createObjectNode(String name, T object) {
		FrameGraphBuilder.ObjectNode<T> objectNode = new FrameGraphBuilder.ObjectNode<>(name, null, object);
		this.objectNodes.add(objectNode);
		return objectNode.handle;
	}

	public <T> net.minecraft.client.util.Handle<T> method_61912(String name, ClosableFactory<T> factory) {
		return this.createResourceNode(name, factory, null).handle;
	}

	<T> FrameGraphBuilder.ResourceNode<T> createResourceNode(String name, ClosableFactory<T> factory, @Nullable FrameGraphBuilder.StageNode stageNode) {
		int i = this.resourceNodes.size();
		FrameGraphBuilder.ResourceNode<T> resourceNode = new FrameGraphBuilder.ResourceNode<>(i, name, stageNode, factory);
		this.resourceNodes.add(resourceNode);
		return resourceNode;
	}

	public void method_61909(ObjectAllocator allocator) {
		this.method_61910(allocator, FrameGraphBuilder.class_9912.field_52707);
	}

	public void method_61910(ObjectAllocator allocator, FrameGraphBuilder.class_9912 arg) {
		BitSet bitSet = this.method_61905();
		List<FrameGraphBuilder.StageNode> list = new ArrayList(bitSet.cardinality());
		BitSet bitSet2 = new BitSet(this.stageNodes.size());

		for (FrameGraphBuilder.StageNode stageNode : this.stageNodes) {
			this.visit(stageNode, bitSet, bitSet2, list);
		}

		this.method_61915(list);

		for (FrameGraphBuilder.StageNode stageNode : list) {
			for (FrameGraphBuilder.ResourceNode<?> resourceNode : stageNode.field_52718) {
				arg.method_61918(resourceNode.name);
				resourceNode.acquire(allocator);
			}

			arg.push(stageNode.name);
			stageNode.field_52717.run();
			arg.pop(stageNode.name);

			for (int i = stageNode.field_52719.nextSetBit(0); i >= 0; i = stageNode.field_52719.nextSetBit(i + 1)) {
				FrameGraphBuilder.ResourceNode<?> resourceNode = (FrameGraphBuilder.ResourceNode<?>)this.resourceNodes.get(i);
				arg.method_61919(resourceNode.name);
				resourceNode.release(allocator);
			}
		}
	}

	private BitSet method_61905() {
		Deque<FrameGraphBuilder.StageNode> deque = new ArrayDeque(this.stageNodes.size());
		BitSet bitSet = new BitSet(this.stageNodes.size());

		for (FrameGraphBuilder.Node<?> node : this.objectNodes) {
			FrameGraphBuilder.StageNode stageNode = node.handle.from;
			if (stageNode != null) {
				this.method_61908(stageNode, bitSet, deque);
			}
		}

		for (FrameGraphBuilder.StageNode stageNode2 : this.stageNodes) {
			if (stageNode2.field_52720) {
				this.method_61908(stageNode2, bitSet, deque);
			}
		}

		return bitSet;
	}

	private void method_61908(FrameGraphBuilder.StageNode stageNode, BitSet bitSet, Deque<FrameGraphBuilder.StageNode> deque) {
		deque.add(stageNode);

		while (!deque.isEmpty()) {
			FrameGraphBuilder.StageNode stageNode2 = (FrameGraphBuilder.StageNode)deque.poll();
			if (!bitSet.get(stageNode2.id)) {
				bitSet.set(stageNode2.id);

				for (int i = stageNode2.childStageIds.nextSetBit(0); i >= 0; i = stageNode2.childStageIds.nextSetBit(i + 1)) {
					deque.add((FrameGraphBuilder.StageNode)this.stageNodes.get(i));
				}
			}
		}
	}

	private void visit(FrameGraphBuilder.StageNode node, BitSet unvisited, BitSet visiting, List<FrameGraphBuilder.StageNode> topologicalOrderOut) {
		if (visiting.get(node.id)) {
			String string = (String)visiting.stream().mapToObj(ix -> ((FrameGraphBuilder.StageNode)this.stageNodes.get(ix)).name).collect(Collectors.joining(", "));
			throw new IllegalStateException("Frame graph cycle detected between " + string);
		} else if (unvisited.get(node.id)) {
			visiting.set(node.id);
			unvisited.clear(node.id);

			for (int i = node.childStageIds.nextSetBit(0); i >= 0; i = node.childStageIds.nextSetBit(i + 1)) {
				this.visit((FrameGraphBuilder.StageNode)this.stageNodes.get(i), unvisited, visiting, topologicalOrderOut);
			}

			for (FrameGraphBuilder.Handle<?> handle : node.field_52714) {
				for (int j = handle.field_52705.nextSetBit(0); j >= 0; j = handle.field_52705.nextSetBit(j + 1)) {
					if (j != node.id) {
						this.visit((FrameGraphBuilder.StageNode)this.stageNodes.get(j), unvisited, visiting, topologicalOrderOut);
					}
				}
			}

			topologicalOrderOut.add(node);
			visiting.clear(node.id);
		}
	}

	private void method_61915(Collection<FrameGraphBuilder.StageNode> collection) {
		FrameGraphBuilder.StageNode[] stageNodes = new FrameGraphBuilder.StageNode[this.resourceNodes.size()];

		for (FrameGraphBuilder.StageNode stageNode : collection) {
			for (int i = stageNode.childResourceIds.nextSetBit(0); i >= 0; i = stageNode.childResourceIds.nextSetBit(i + 1)) {
				FrameGraphBuilder.ResourceNode<?> resourceNode = (FrameGraphBuilder.ResourceNode<?>)this.resourceNodes.get(i);
				FrameGraphBuilder.StageNode stageNode2 = stageNodes[i];
				stageNodes[i] = stageNode;
				if (stageNode2 == null) {
					stageNode.field_52718.add(resourceNode);
				} else {
					stageNode2.field_52719.clear(i);
				}

				stageNode.field_52719.set(i);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class Handle<T> implements net.minecraft.client.util.Handle<T> {
		final FrameGraphBuilder.Node<T> parent;
		private final int id;
		@Nullable
		final FrameGraphBuilder.StageNode from;
		final BitSet field_52705 = new BitSet();
		@Nullable
		private FrameGraphBuilder.Handle<T> movedTo;

		Handle(FrameGraphBuilder.Node<T> parent, int id, @Nullable FrameGraphBuilder.StageNode from) {
			this.parent = parent;
			this.id = id;
			this.from = from;
		}

		@Override
		public T get() {
			return this.parent.get();
		}

		FrameGraphBuilder.Handle<T> method_61917(FrameGraphBuilder.StageNode stageNode) {
			if (this.parent.handle != this) {
				throw new IllegalStateException("Handle " + this + " is no longer valid, as its contents were moved into " + this.movedTo);
			} else {
				FrameGraphBuilder.Handle<T> handle = new FrameGraphBuilder.Handle<>(this.parent, this.id + 1, stageNode);
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

		public Node(String name, @Nullable FrameGraphBuilder.StageNode stageNode) {
			this.name = name;
			this.handle = new FrameGraphBuilder.Handle<>(this, 0, stageNode);
		}

		public abstract T get();

		public String toString() {
			return this.name;
		}
	}

	@Environment(EnvType.CLIENT)
	static class ObjectNode<T> extends FrameGraphBuilder.Node<T> {
		private final T value;

		public ObjectNode(String string, @Nullable FrameGraphBuilder.StageNode parent, T value) {
			super(string, parent);
			this.value = value;
		}

		@Override
		public T get() {
			return this.value;
		}
	}

	@Environment(EnvType.CLIENT)
	static class ResourceNode<T> extends FrameGraphBuilder.Node<T> {
		final int id;
		private final ClosableFactory<T> factory;
		@Nullable
		private T resource;

		public ResourceNode(int id, String name, @Nullable FrameGraphBuilder.StageNode stageNode, ClosableFactory<T> factory) {
			super(name, stageNode);
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

	@Environment(EnvType.CLIENT)
	class StageNode implements class_9916 {
		final int id;
		final String name;
		final List<FrameGraphBuilder.Handle<?>> field_52714 = new ArrayList();
		final BitSet childResourceIds = new BitSet();
		final BitSet childStageIds = new BitSet();
		Runnable field_52717 = () -> {
		};
		final List<FrameGraphBuilder.ResourceNode<?>> field_52718 = new ArrayList();
		final BitSet field_52719 = new BitSet();
		boolean field_52720;

		public StageNode(final int id, final String name) {
			this.id = id;
			this.name = name;
		}

		private <T> void addChild(FrameGraphBuilder.Handle<T> handle) {
			if (handle.parent instanceof FrameGraphBuilder.ResourceNode<?> resourceNode) {
				this.childResourceIds.set(resourceNode.id);
			}
		}

		private void addChild(FrameGraphBuilder.StageNode child) {
			this.childStageIds.set(child.id);
		}

		@Override
		public <T> net.minecraft.client.util.Handle<T> addResource(String name, ClosableFactory<T> factory) {
			FrameGraphBuilder.ResourceNode<T> resourceNode = FrameGraphBuilder.this.createResourceNode(name, factory, this);
			this.childResourceIds.set(resourceNode.id);
			return resourceNode.handle;
		}

		@Override
		public <T> void method_61928(net.minecraft.client.util.Handle<T> handle) {
			this.method_61932((FrameGraphBuilder.Handle<T>)handle);
		}

		private <T> void method_61932(FrameGraphBuilder.Handle<T> handle) {
			this.addChild(handle);
			if (handle.from != null) {
				this.addChild(handle.from);
			}

			handle.field_52705.set(this.id);
		}

		@Override
		public <T> net.minecraft.client.util.Handle<T> method_61933(net.minecraft.client.util.Handle<T> handle) {
			return this.method_61934((FrameGraphBuilder.Handle<T>)handle);
		}

		@Override
		public void addChild(class_9916 child) {
			this.childStageIds.set(((FrameGraphBuilder.StageNode)child).id);
		}

		@Override
		public void method_61924() {
			this.field_52720 = true;
		}

		private <T> FrameGraphBuilder.Handle<T> method_61934(FrameGraphBuilder.Handle<T> handle) {
			this.field_52714.add(handle);
			this.method_61932(handle);
			return handle.method_61917(this);
		}

		@Override
		public void method_61929(Runnable runnable) {
			this.field_52717 = runnable;
		}

		public String toString() {
			return this.name;
		}
	}

	@Environment(EnvType.CLIENT)
	public interface class_9912 {
		FrameGraphBuilder.class_9912 field_52707 = new FrameGraphBuilder.class_9912() {
		};

		default void method_61918(String string) {
		}

		default void method_61919(String string) {
		}

		default void push(String string) {
		}

		default void pop(String string) {
		}
	}
}
