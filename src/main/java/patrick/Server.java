package patrick;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Сервер:
 * запускается и ждёт подключений от клиентов
 * в бесконечном цикле
 */
public class Server implements Runnable {
    // Порт, через который будет происходить обмен сообщениями
    // Другой порт - другая программа
    public static final int PORT = 9999;

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
                ClientCom clientCom = new ClientCom(clientCount, socket);
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
    private void sendToAll(String message) {
        for (ClientCom com : clientComs) {
            com.send(message);
        }
    }

    /**
     * Класс, который отвечает за обмен данными с одним клиентом
     */
    class ClientCom implements Runnable {
        private final int id;
        private final Socket socket;
        private final PrintWriter printWriter;

        /**
         * @param id     Идентификатор клиента (уникальное число)
         *               1, 2, 3 ...
         *               Будем его везде использовать для нумерации клиентов
         * @param socket Объект для обмена данными с данным конкретным клиентом
         *               inputStream - читаем из буфера сетевой карты
         *               outputStream - пишем конкретно этому клиенту
         * @throws IOException
         */
        ClientCom(int id, Socket socket) throws IOException {
            this.id = id;
            this.socket = socket;
            printWriter = new PrintWriter(
                    new BufferedOutputStream(socket.getOutputStream()));
        }

        @Override
        public void run() {
            try {
                // Открываем на чтение
                Scanner scanner = new Scanner(socket.getInputStream());
                while (scanner.hasNextLine()) {
                    // Что нам прислал клиент
                    String request = scanner.nextLine();
                    System.out.println("Клиент #" + id + " прислал: " + request);
                    sendToAll(request);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        void send(String message) {
            printWriter.println(message);
            printWriter.flush();
        }
    }
}
