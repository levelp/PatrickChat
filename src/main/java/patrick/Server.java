package patrick;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервер:
 * запускается и ждёт подключений от клиентов
 * в бесконечном цикле
 */
public class Server implements Runnable {
    // Порт, через который будет происходить обмен сообщениями
    // Другой порт - другая программа
    public static final int PORT = 12346;

    /**
     * Подключения клиентов
     */
    private List<ClientCom> clientComs = new ArrayList<>();

    @Override
    public void run() {
        try {
            // Открываем порт и слушаем его
            // Две программы не могут открыть на прослушивание
            // один и тот же порт одновременно
            // 0..2^16-1 = 65535
            ServerSocket serverSocket = new ServerSocket(PORT);
            // Пишем, что мы запустились на заданном порту
            System.out.println("Сервер запущен на порту: " + PORT);
            // Считаем клиентские подключения
            int clientCount = 0;
            // Принимаем подключения от клиентов
            while (true) {
                // Останавливаемся и ждём входящее подключение
                Socket socket = serverSocket.accept();
                clientCount++; // Новое подключение => увеличиваем счётчик
                System.out.println("Подключился клиент " +
                        clientCount + " " + socket.getInetAddress());
                ClientCom clientCom = new ClientCom(this, clientCount, socket);
                clientComs.add(clientCom);
                Thread read = new Thread(clientCom);
                read.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Сервер закончил работу!");
    }

    /**
     * Отправить всем клиентам сообщение
     *
     * @param message сообщение
     */
    void sendToAll(String message) {
        for (ClientCom com : clientComs) {
            com.send(message);
        }
    }

}
