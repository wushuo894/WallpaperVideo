import cn.hutool.core.thread.ExecutorBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;

public class Test {
    public static final ExecutorService executor = ExecutorBuilder.create()
            .setCorePoolSize(1)
            .setMaxPoolSize(1)
            .setWorkQueue(new LinkedBlockingQueue<>(2))
            .build();

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            try {
                executor.submit(() -> {
                    System.out.println(finalI);
                });
            } catch (RejectedExecutionException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
