import java.util.concurrent.locks.ReentrantLock;

public class Car implements Runnable {

    private static int CARS_COUNT;


    static {
        CARS_COUNT = 0;
    }

    private static ReentrantLock lock = new ReentrantLock();
    private Race race;
    private int speed;
    private String name;

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }

    public Car(Race race, int speed) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
    }

    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int) (Math.random() * 800));
            System.out.println(this.name + " готов");
            MainClass.cdlStart.countDown();
            MainClass.cdlStart.await();
            Thread.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);

            if (i == race.getStages().size() - 1) {
                if (lock.tryLock()) {
                    lock.lock();
                    System.out.println(this.name + " WIN");
                    MainClass.cdlFinish.countDown();
                    lock.unlock();
                } else {
                    System.out.println(this.name + " закончил гонку.");
                    MainClass.cdlFinish.countDown();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}