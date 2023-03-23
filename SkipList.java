import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * This class implements SkipList data structure and contains an inner SkipNode
 * class which the SkipList will make an array of to store data.
 * 
 * @author CS Staff
 * 
 * @version 2021-08-23
 * @param <K> Key
 * @param <V> Value
 */
public class SkipList<K extends Comparable<? super K>, V> implements Iterable<KVPair<K, V>> {
	private SkipNode head; // First element of the top level
	private int size; // number of entries in the Skip List

	/**
	 * Initializes the fields head, size and level
	 */
	public SkipList() {
		head = new SkipNode(null, 0);
		size = 0;
	}

	/**
	 * Returns a random level number which is used as the depth of the SkipNode
	 * 
	 * @return a random level number
	 */
	int randomLevel() {
		int lev;
		Random value = new Random();
		for (lev = 0; Math.abs(value.nextInt()) % 2 == 0; lev++) {
			// Do nothing
		}
		return lev; // returns a random level
	}

	/**
	 * Searches for the KVPair using the key which is a Comparable object.
	 * 
	 * @param key key to be searched for
	 */
	public ArrayList<KVPair<K, V>> search(K key) {
		ArrayList<KVPair<K, V>> searchArray = new ArrayList<KVPair<K, V>>();
		if(key == null)return searchArray;
		
		SkipNode x = head;
		for (int i = head.level; i >= 0; i--) {
			while (x.forward[i] != null && x.forward[i].element().getKey().compareTo(key) < 0) {
				x = x.forward[i];
			}
		}

		while (x.forward[0] != null && x.forward[0].element().getKey().compareTo(key) == 0) {
			searchArray.add(x.forward[0].element());
			x = x.forward[0];
		}

		return searchArray;
	}

	/**
	 * @return the size of the SkipList
	 */
	public int size() {
		return size;
	}

	/**
	 * Inserts the KVPair in the SkipList at its appropriate spot as designated by
	 * its lexicoragraphical order.
	 * 
	 * @param it the KVPair to be inserted
	 */
	@SuppressWarnings("unchecked")
	public void insert(KVPair<K, V> it) {
		if(it == null)return;
		int newLevel = randomLevel();

		if (newLevel > head.level) {
			adjustHead(newLevel);
		}

		SkipNode[] update = (SkipNode[]) Array.newInstance(SkipList.SkipNode.class, head.level + 1);
		// SkipList<K, V>.SkipNode[] update = new SkipList.SkipNode[head.level + 1];
		SkipNode x = head;

		for (int i = head.level; i >= 0; i--) {
			while (x.forward[i] != null && x.forward[i].element().compareTo(it) < 0) {
				x = x.forward[i];
			}
			update[i] = x;
		}

		SkipNode newNode = new SkipNode(it, newLevel);

		for (int i = 0; i <= newLevel; i++) {
			newNode.forward[i] = update[i].forward[i];
			update[i].forward[i] = newNode;
		}

		size++;
	}

	/**
	 * Increases the number of levels in head so that no element has more indices
	 * than the head.
	 * 
	 * @param newLevel the number of levels to be added to head
	 */
	private void adjustHead(int newLevel) {
		SkipNode temp = head;
		head = new SkipNode(null, newLevel);

		for (int i = 0; i <= temp.level; i++) {
			head.forward[i] = temp.forward[i];
		}
	}

	/**
	 * Removes the KVPair that is passed in as a parameter and returns true if the
	 * pair was valid and false if not.
	 * 
	 * @param pair the KVPair to be removed
	 * @return returns the removed pair if the pair was valid and null if not
	 */

	@SuppressWarnings("unchecked")
	public KVPair<K, V> remove(K key) {
		if(key == null)return null;
		
		SkipNode[] update = (SkipNode[]) Array.newInstance(SkipList.SkipNode.class, head.level + 1);
		// SkipList<K, V>.SkipNode[] update = new SkipList.SkipNode[head.level + 1];
		SkipNode x = head;

		for (int i = head.level; i >= 0; i--) {
			while (x.forward[i] != null && x.forward[i].element().getKey().compareTo(key) < 0) {
				x = x.forward[i];
			}
			update[i] = x;
		}

		x = x.forward[0];
		if (x != null && x.element().getKey().compareTo(key) == 0) {
			for (int i = 0; i <= x.level; i++) {
				update[i].forward[i] = x.forward[i];
			}

			size--;
			return x.element();

		} else {
			return null;
		}
	}

	/**
	 * Removes a KVPair with the specified value.
	 * 
	 * @param val the value of the KVPair to be removed
	 * @return returns true if the removal was successful
	 */
	@SuppressWarnings("unchecked")
	public KVPair<K, V> removeByValue(V val) {
		if(val == null)return null;
		
		SkipNode x = head;

		while (x.forward[0] != null && x.forward[0].element().getValue().equals(val) != true) {
			x = x.forward[0];
		}

		if (x.forward[0] != null) {

			SkipNode target = x.forward[0];
			SkipNode[] update = (SkipNode[]) Array.newInstance(SkipList.SkipNode.class, target.level + 1);
			// SkipList<K, V>.SkipNode[] update = new SkipList.SkipNode[head.level + 1];
			x = head;

			for (int i = target.level; i >= 0; i--) {
				while (x.forward[i] != null && x.forward[i].equals(target) != true) {
					x = x.forward[i];
				}
				update[i] = x;
			}
			x = x.forward[0];
			for (int i = 0; i <= target.level; i++) {
				update[i].forward[i] = x.forward[i];
			}

			size--;
			return x.element();

		} else {
			return null;
		}
	}

	/**
	 * Prints out the SkipList in a human readable format to the console.
	 */
	public void dump() {
		System.out.println("SkipList dump:");
		SkipNode x = head;
		while (x != null) {
			System.out.print("Node has depth " + Integer.toString(x.level + 1) + ", Value ");

			if (x.element() == null)
				System.out.println("(null)");
			else
				System.out.println(x.element().toString());

			x = x.forward[0];
		}
		System.out.println("SkipList size is: " + Integer.toString(size));
	}

	/**
	 * This class implements a SkipNode for the SkipList data structure.
	 * 
	 * @author CS Staff
	 * 
	 * @version 2016-01-30
	 */
	private class SkipNode {

		// the KVPair to hold
		private KVPair<K, V> pair;
		// what is this
		private SkipNode[] forward;
		// the number of levels
		private int level;

		/**
		 * Initializes the fields with the required KVPair and the number of levels from
		 * the random level method in the SkipList.
		 * 
		 * @param tempPair the KVPair to be inserted
		 * @param level    the number of levels that the SkipNode should have
		 */
		@SuppressWarnings("unchecked")
		public SkipNode(KVPair<K, V> tempPair, int level) {
			pair = tempPair;
			forward = (SkipNode[]) Array.newInstance(SkipList.SkipNode.class, level + 1);
			this.level = level;
		}

		/**
		 * Returns the KVPair stored in the SkipList.
		 * 
		 * @return the KVPair
		 */
		public KVPair<K, V> element() {
			return pair;
		}

	}

	private class SkipListIterator implements Iterator<KVPair<K, V>> {
		private SkipNode current;

		public SkipListIterator() {
			current = head;
		}

		@Override
		public boolean hasNext() {
			if (current.forward[0] != null) {
				return true;
			}
			return false;
		}

		@Override
		public KVPair<K, V> next() {
			if (current.forward[0] != null) {
				current = current.forward[0];
				return current.element();
			}
			return null;
		}

	}

	@Override
	public Iterator<KVPair<K, V>> iterator() {
		return new SkipListIterator();
	}

}
