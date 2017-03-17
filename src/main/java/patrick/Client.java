package patrick;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Подключается к серверу:
 * - отдаёт свой никнейм (логин) на сервер
 * - отправляет сообщения по мере ввода
 * - получает сообщения из чата отправляемые остальными пользователями
 */
public class Client {

    private final PrintWriter printWriter;

    public Client() throws IOException {
        Socket socket = new Socket("localhost", Server.PORT);
        printWriter = new PrintWriter(socket.getOutputStream());
        final Scanner scanner = new Scanner(socket.getInputStream());
        Thread listenToServer = new Thread(() -> {
            while (scanner.hasNextLine()) {
                System.out.println("Получили: " + scanner.nextLine());
            }
        });
        listenToServer.start();
    }

    public void send(String message) {
        System.out.println("Отправляю сообщение: \"" + message + "\"");
        printWriter.println(message);
        printWriter.flush();
    }
}
