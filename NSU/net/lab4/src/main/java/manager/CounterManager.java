package manager;

import java.util.concurrent.atomic.AtomicLong;

public class CounterManager {
    private final AtomicLong counter = new AtomicLong(0);

    public long getNextNum() {
        return counter.incrementAndGet();
    }
}