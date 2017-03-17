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
        Client client = new Client();
        Client client2 = new Client();
        client.send("Hello!");
        pause();
        client2.send("И тебе привет!");
        pause();
        client.send("И ещё хочу сказать...");
        pause();
    }

    private static void pause() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
