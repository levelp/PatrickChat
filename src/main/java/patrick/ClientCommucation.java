package patrick;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

/**
 * Подключается к серверу:
 * - отдаёт свой никнейм (логин) на сервер
 * - отправляет сообщения по мере ввода
 * - получает сообщения из чата отправляемые остальными пользователями
 */
public class ClientCommucation {

    private final PrintWriter printWriter;

    public ClientCommucation(String serverHost) throws IOException {
        Socket socket = new Socket(serverHost, Server.PORT);
        printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
        final Scanner scanner = new Scanner(socket.getInputStream(), "UTF-8");
        Thread listenToServer = new Thread(() -> {
            while (scanner.hasNextLine()) {
                System.out.println((new Date()) + " " + scanner.nextLine());
            }
        });
        listenToServer.start();
    }

    // varargs - переменное количество аргументов
    public static void main(String... args) throws IOException {
        System.out.println("ClientCommucation.main");
        if (args.length <= 2) {
            System.out.println("ClientCommucation <host IP> <nickname>");
            return;
        }
        String hostName = args[0];
        String nickName = args[1];
        System.out.println("Connect to " + hostName +
                " nickName = " + nickName);
        ClientCommucation client = new ClientCommucation(hostName);

    }

    static void myTest() throws IOException {
        main("localhost", "param2");
    }

    public void send(String message) {
        System.out.println("Отправляю сообщение: \"" + message + "\"");
        printWriter.println(message);
        printWriter.flush();
    }
}
