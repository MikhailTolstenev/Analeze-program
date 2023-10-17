import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    public static BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);
    public static Thread textMaker;

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static Thread getThread(BlockingQueue<String> queue, char letter) {
        return new Thread(() -> {
            int max = findMaxChar(queue, letter);
            System.out.println("Максимальное колическтво символа " + letter + " - " + max + " штук.");
        });
    }

    public static int findMaxChar(BlockingQueue<String> queue, char letter) {
        int count = 0;
        int max = 0;
        String text;
        try {
            while (textMaker.isAlive()) {
                text = queue.take();
                for (char c : text.toCharArray()) {
                    if (c == letter) count++;
                }
                if (count>max) max=count;
                count=0;
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " проблема");
            return -1;
        }
        return max;
    }

    public static void main(String[] args) throws InterruptedException {
        textMaker = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                String text = generateText("abc", 100_000);
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        textMaker.start();
        Thread a =getThread(queueA,'a');
        Thread b =getThread(queueB,'b');
        Thread c =getThread(queueC,'c');

        a.start();
        b.start();
        c.start();
        a.join();
        b.join();
        c.join();

    }
}