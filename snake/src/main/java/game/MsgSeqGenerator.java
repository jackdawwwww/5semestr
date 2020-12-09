package game;

import java.util.concurrent.atomic.AtomicLong;

public class MsgSeqGenerator {
    private AtomicLong counter = new AtomicLong(0);

    public long getNextNum() {
        return counter.incrementAndGet();
    }
}
