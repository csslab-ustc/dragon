package util;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// a work list is just a queue, but with restricted operations.
public class WorkList<X> {
    private final Queue<X> queue;

    public WorkList(){
        this.queue = new LinkedList<>();
    }

    public WorkList(List<X> list){
        this.queue = new LinkedList<>(list);
    }

    public void add(X x){
        if(!queue.contains(x)){
            this.queue.add(x);
        }
    }

    public X remove(){
        return this.queue.remove();
    }

    public boolean isEmpty(){
        return this.queue.isEmpty();
    }
}
