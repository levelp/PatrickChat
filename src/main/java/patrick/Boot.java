package patrick;

import java.io.IOException;

/**
 * Запуск чата: сервер и несколько клиентов
 */
public class Boot {
    public static void main(String[] args) throws IOException {
        // Создаем и запускаем сервер
        Thread serverThread = new Thread(new Server());
        serverThread.setDaemon(true);
        serverThread.start();

        // Создаём 2-х клиентов и запускаем его
        ClientCommucation client = new ClientCommucation("localhost");
        ClientCommucation client2 = new ClientCommucation("localhost");
        client.send("Hello!");
        pause(100);
        client2.send("И тебе привет!");
        pause(200);
        client.send("И ещё хочу сказать...");
        pause(100);
    }

    private static void pause(int pauseInMilliseconds) {
        try {
            Thread.sleep(pauseInMilliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
