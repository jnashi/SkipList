
import java.util.*;
public class SkipListSet<T extends Comparable<T>> implements SortedSet<T> {
    public static void main(String[] args) {
        SkipListSet<Integer> testMe = new SkipListSet<>();
        SkipListSet<Integer> testhashCode1 = new SkipListSet<>();
        SkipListSet<Integer> testhashCode2 = new SkipListSet<>();
        testMe.add(3);
        testMe.add(6);
        testMe.add(7);
        testMe.add(9);
        testMe.add(12);
        testMe.add(19);
        testMe.add(21);
        testMe.add(25);
        testMe.add(26);
        testMe.add(30);
        testMe.add(42);
        testMe.add(48);
        testMe.add(53);
        testMe.add(56);
        testMe.add(58);
        testMe.add(69);
        testMe.add(75);
        testMe.add(81);
        testMe.add(83);
        testMe.add(86);

        // Adding more integers, with edgingf
        testMe.add(0);
        testMe.add(-5);
        testMe.add(100);
        testMe.add(42);
        testMe.add(50);
        testMe.add(65);
        testMe.add(70);
        testMe.add(85);
        testMe.add(90);


        //testing the equals/hashcode functios.
        testhashCode1.add(1);
        testhashCode1.add(2);
        testhashCode1.add(3);
        testhashCode1.add(4);
        testhashCode1.add(5);
        testhashCode1.add(6);

        testhashCode2.add(1);
        testhashCode2.add(2);
//        testhashCode2.add(3);
//        testhashCode2.add(4);
//        testhashCode2.add(5);
//        testhashCode2.add(10);



        testMe.remove(42);
        testMe.remove(43);
        testMe.remove(-5);
        testMe.remove(100);
        testMe.remove(3);



        System.out.println("adding elements:");
        testMe.print();

        System.out.println("\nremoving elements:");
        testMe.print();
        System.out.println("\nrebalaning");
        testMe.reBalance();
        testMe.print();

        System.out.println("Before clear()= " + testMe.size());
        testMe.clear();
        System.out.println("After clear() = " + testMe.size());
        //This should be true because of the same values
        System.out.println(testhashCode1.equals(testhashCode2));

        System.out.println("Hashcode");
        System.out.println(testhashCode1.hashCode());
        System.out.println(testhashCode2.hashCode());

    }
    //This prints out the test harness of how the skip list should look like (Hopefully)

    public void print() {
        int height = maximumHeight;

        while (height >= 0) {
            SkipListSetItem<T> walker = this.head;
            while (walker != null) {
                System.out.print(walker.value + " -> ");
                walker = walker.next[height];
            }
            System.out.println("NULL");
            height--;
        }
    }


    private static class SkipListSetItem<T extends Comparable<T>> {
        T value;
        //This is for the levels
        SkipListSetItem<T>[] next;

        public SkipListSetItem(T value, int levels) {
            this.value = value;
            this.next = new SkipListSetItem[levels + 1];
        }


    }
    private SkipListSetItem<T> head;
    private SkipListSetItem<T> tail;
    private int maximumHeight = 0;
    private int size = 0;


    //constructors for the skiplist
    public SkipListSet() {
        tail = head;
        maximumHeight = 0;
        size = 0;
    }

    private class SkipListSetIterator<T extends Comparable<T>> implements Iterator<T> {
        private SkipListSetItem<T> current;

        public SkipListSetIterator() {
            current = (SkipListSetItem<T>) (SkipListSetItem<T>) head.next[0];
        }


        //Checking if there is a next value
        @Override
        public boolean hasNext() {
            if (current != null) {
                return true;
            } else {
                return false;
            }
        }

        //goes to the next value
        @Override
        public T next() {
            if (!hasNext()) {
                return null;
            }
            T value = current.value;
            current = current.next[0];
            return value;
        }


    }
    @Override
    public int size() {

        return size;
    }
    @Override
    public boolean isEmpty() {

        return size == 0;
    }


    // e is considered the value
    @Override
    public boolean add(T e) {
        if(this.head == null){
            this.head = new SkipListSetItem<T>(e, maximumHeight);
            this.tail = new SkipListSetItem<>(e, maximumHeight);
            head.next[0] = tail;
            this.size++;

            return true;
        }

        calcForHeightIncrease();

        //  height of the node using the random calculator
        int theHeight = randomHeightCalc();


        SkipListSetItem<T> newNode = new SkipListSetItem<>(e, theHeight);
        SkipListSetItem<T>[] predes = new SkipListSetItem[maximumHeight + 1];
        SkipListSetItem<T> current = head;


        if (e.compareTo(head.value) <= 0) {
            // Resize head's next array if it is smaller than the new maximumHeight
            if (head.next.length <= maximumHeight) {
                SkipListSetItem<T> resizedHead = new SkipListSetItem<>(head.value, maximumHeight);
                System.arraycopy(head.next, 0, resizedHead.next, 0, head.next.length);
                head = resizedHead;
            }
            for (int i = 0; i <= theHeight; i++) {
                newNode.next[i] = head;
            }
        }

        // going through each level

        for (int i = maximumHeight ; i >= 0; i--) {
            while (current.next[i] != null && current.next[i].value.compareTo(e) < 0) {
                current = current.next[i];
                //checks for the dup in the levels
                if (current !=null && current.value.compareTo(e) == 0) {

                    return false;
                }

            }
            predes[i] = current;

        }

         //dup checker
        current = current.next[0];
        if (current != null && current.value.compareTo(e) == 0) {
            return false;
        }

        // TODO Relink the list at each level
        for (int i = 0; i <= theHeight; i++) {
            if (predes[i] != null && i < predes[i].next.length) {
                newNode.next[i] = predes[i].next[i];
            } else {
                newNode.next[i] = null;
            }

            if (predes[i] != null) {
                predes[i].next[i] = newNode;
            }
        }



        //to update the tail if
        if (newNode.next[0] == null) {
            tail = newNode;
        }

        // Updating the size of the skiplist
        size++;

        return true;
    }



    private void calcForHeightIncrease() {

        int currentSize = size + 1;
        int realHeight = (int) Math.ceil(Math.log(currentSize) / Math.log(2));

        realHeight = Math.max(1, realHeight);

        // using maf to find height for adjustment
        if (realHeight > maximumHeight) {
            int oldHeight = maximumHeight;
            //updating
            maximumHeight = realHeight;

            SkipListSetItem<T> newHead = new SkipListSetItem<>(head.value, maximumHeight);
            //Adding an array copy and placing all of its recent hehight
            System.arraycopy(head.next, 0, newHead.next, 0, oldHeight + 1);

            newHead.next[maximumHeight] = null;

            // updating da head
            head = newHead;
        }
    }
    private int randomHeightCalc() {
        int lvl = 0;

        while (Math.random() < 0.5 && lvl < maximumHeight) {
            lvl++;
        }
        return Math.min(lvl,maximumHeight);
    }

    //search
    @Override
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }

        SkipListSetItem<T> current = head;

        //going thorough each node from top to bottom (i is considered the level associated with maximum height)
        for (int i = maximumHeight; i >= 0; i--) {
            while (current.next[i] != null && current.next[i].value.compareTo((T) o) < 0) {
                current = current.next[i];
            }
            //this checks if this is the right value.
            if (current.next[i] != null && current.next[i].value.compareTo((T) o) == 0) {
                return true;
            }
        }
        return false;
    }
    public void reBalance() {
        if (isEmpty()) {
            return;
        }

        // Adjust maximumHeight
        int minHeight = 1;
        int newMaxHeight = Math.max(minHeight, (int) Math.ceil(Math.log(size) / Math.log(2)));
        newMaxHeight = (int) Math.pow(2, Math.ceil(Math.log(newMaxHeight) / Math.log(2)));
        maximumHeight = newMaxHeight;

        SkipListSetItem<T> newHead = new SkipListSetItem<>(null, maximumHeight);


        SkipListSetItem<T>[] currentLevel = new SkipListSetItem[maximumHeight + 1];
        for (int i = 0; i <= maximumHeight; i++) {
            currentLevel[i] = newHead;
        }

        // going through list and reassign node heights
        SkipListSetItem<T> current = head.next[0];
        while (current != null) {
            SkipListSetItem<T> nextNode = current.next[0];

            // Using the math I implemented on randomheight calc to make a new height for rebalance
            int newHeight = randomHeightCalc();
            newHeight = Math.min(newHeight, maximumHeight);

            // This is to resize it
            current.next = Arrays.copyOf(current.next, newHeight + 1);

            // Insert into new levels
            for (int level = 0; level <= newHeight; level++) {
                currentLevel[level].next[level] = current;
                currentLevel[level] = current;
            }

            // Move to the next node
            current = nextNode;
        }

        // trying to remove levels from the top
        for (int i = 0; i <= maximumHeight; i++) {
            currentLevel[i].next[i] = null;
        }

        // Update head and tail
        head = newHead;
        tail = currentLevel[0];

    }

    @Override
    public boolean remove(Object o) {
        if (o == null) {
            return false;
        }
        T e = (T) o;

        SkipListSetItem<T>[] predes = new SkipListSetItem[maximumHeight + 1];
        SkipListSetItem<T> current = head;

        // Findign the node to remove
        for (int i = maximumHeight; i >= 0; i--) {
            while (current.next[i] != null && current.next[i].value.compareTo(e) < 0) {
                current = current.next[i];
            }
            predes[i] = current;
        }

        // This moves it into the dele
        current = current.next[0];

        // node check
        if (current != null && current.value.compareTo(e) == 0) {
            // Update the next pointers of predes
            for (int i = 0; i <= maximumHeight; i++) {
                if (predes[i].next[i] != null && predes[i].next[i].value.compareTo(e) == 0) {
                    predes[i].next[i] = current.next[i];
                }
            }
            //updating the head and tail if necessary
            if(current == head ){
                head = current.next[0];
            }

            if (current == tail) {
                tail = predes[0];
            }

            //Adjusting the height
            while (maximumHeight > 0 && head.next[maximumHeight] == null) {
                maximumHeight--;
            }

            size--;
            return true;
        }

        return false;
    }
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }
    @Override
    public boolean addAll(Collection<? extends T> c) {
        for (T element : c) {
            this.add(element);
        }
        return true;
    }



    @Override
    public Iterator<T> iterator() {

        return new SkipListSetIterator<>();
    }
    @Override
    //this just returns the empty list
    public T first() {
        if (isEmpty()) {
            return null;
        }
        return head.value;
    }
    @Override
    public T last() {
        if (isEmpty()) {
            return null;
        }
        return tail.value;
    }


    @Override
    public SortedSet<T> headSet(T toElement) {
        throw new UnsupportedOperationException();
        //return null;
    }
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        throw new UnsupportedOperationException();
        //return null;
    }
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        throw new UnsupportedOperationException();
        //return null;
    }


    @Override
    public boolean retainAll(Collection<?> c) {

        boolean fixedRetain = false;
        Iterator<T> current = this.iterator();
        while (current.hasNext()) {
            T element = current.next();
            if (!c.contains(element)) {
                current.remove();
                fixedRetain = true;
            }
        }
        return fixedRetain;
    }
    @Override
    public boolean removeAll(Collection<?> c) {

        boolean fixRemove = false;
        Iterator<?> current = c.iterator();
        while (current.hasNext()) {
            Object o = current.next();
            boolean ifRemoved = remove(o);
            if (ifRemoved) {
                fixRemove = true;
            }
        }
        return fixRemove;
    }

    @Override
    public void clear() {
        head = new SkipListSetItem<>(null, maximumHeight);
        maximumHeight = 0;
        size = 0;
    }
    @Override
    public Comparator<? super T> comparator() {
        throw new UnsupportedOperationException();
        //return null
    }
    public boolean equals(Object o) {
        if (this == o) return true;


        SortedSet<?> different = (SortedSet<?>) o;

        // Check size
        if (size != different.size()){
            return false;
        }
        Iterator<T> currentIter = this.iterator();
        Iterator<?> otherIter = different.iterator();

        //This goes through and compares the 2
        while (currentIter.hasNext() && otherIter.hasNext()) {
            T currentElement = currentIter.next();
            Object otherElement = otherIter.next();

            //null checker
            if (currentElement == null) {
                if (otherElement != null) {
                    return false;
                }
            }
            else if (!currentElement.equals(otherElement)) {
                return false;
            }
        }


        if (currentIter.hasNext() || otherIter.hasNext()) {
            return false;
        }


        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;

        // goes through the set and checks
        SkipListSetItem<T> current = head.next[0];
        while (current!=null) {
            T value = current.value;

            if(value != null){
                hash += value.hashCode();
            }
            current = current.next[0];
        }

        return hash;
    }


    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        SkipListSetItem<T> current = head.next[0];
        int i = 0;
        while (current != null) {
            array[i++] = current.value;
            current = current.next[0];
        }
        return array;
    }
    @Override
    public <T1> T1[] toArray(T1[] a) {

        return (T1[]) toArray();
    }
}
