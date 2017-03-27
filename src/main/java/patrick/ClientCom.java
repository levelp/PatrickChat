package patrick;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Класс, который отвечает за обмен данными с одним клиентом
 */
class ClientCom implements Runnable {
    private Server server;
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
    ClientCom(Server server, int id, Socket socket) throws IOException {
        this.server = server;
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
                server.sendToAll(request);
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
