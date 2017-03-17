package patrick;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Класс, который отвечает за обмен данными с одним клиентом
 */
class ClientCom implements Runnable {
    private final int id;
    private final Socket socket;
    private final PrintWriter printWriter;
    private Server server;
    private volatile boolean connected = true;
    private Scanner scanner;

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
                new OutputStreamWriter(new BufferedOutputStream(socket.getOutputStream()), "UTF-8"));
    }

    @Override
    public void run() {
        try {
            // Открываем на чтение
            scanner = new Scanner(socket.getInputStream(), "UTF-8");
            
            // Приветственное сообщение для администратора
            if (id == 1) {
                send("Вы вошли как администратор. Доступные команды:");
                send("/kick <id> - исключить клиента");
                send("/shutdown <секунды> - выключить сервер через заданное время");
            }
            
            while (connected && scanner.hasNextLine()) {
                // Что нам прислал клиент
                String request = scanner.nextLine();
                System.out.println("Клиент #" + id + " прислал: " + request);
                
                // Проверяем, является ли это командой администратора
                if (request.startsWith("/") && id == 1) {
                    server.handleAdminCommand(request, id);
                } else {
                    server.sendToAll(request);
                }
            }
        } catch (Exception ex) {
            if (connected) {
                ex.printStackTrace();
            }
        }
    }

    void send(String message) {
        if (connected) {
            printWriter.println(message);
            printWriter.flush();
        }
    }
    
    /**
     * Получить ID клиента
     */
    int getId() {
        return id;
    }
    
    /**
     * Отключить клиента
     */
    void disconnect() {
        connected = false;
        try {
            if (scanner != null) {
                scanner.close();
            }
            if (printWriter != null) {
                printWriter.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
