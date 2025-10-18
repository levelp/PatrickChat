package patrick;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    
    /**
     * Флаг для остановки сервера
     */
    private volatile boolean running = true;
    
    /**
     * ServerSocket для возможности закрытия
     */
    private ServerSocket serverSocket;

    @Override
    public void run() {
        try {
            // Открываем порт и слушаем его
            // Две программы не могут открыть на прослушивание
            // один и тот же порт одновременно
            // 0..2^16-1 = 65535
            serverSocket = new ServerSocket(PORT);
            // Пишем, что мы запустились на заданном порту
            System.out.println("Сервер запущен на порту: " + PORT);
            // Считаем клиентские подключения
            int clientCount = 0;
            // Принимаем подключения от клиентов
            while (running) {
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
            if (running) {
                e.printStackTrace();
            }
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

    /**
     * Обработка команд администратора
     *
     * @param command команда от администратора
     * @param adminId ID администратора
     */
    void handleAdminCommand(String command, int adminId) {
        // Проверяем, что это первый клиент (администратор)
        if (adminId != 1) {
            return;
        }

        // Разбираем команду
        String[] parts = command.split("\\s+");
        if (parts.length == 0) {
            return;
        }

        String cmd = parts[0].toLowerCase();

        switch (cmd) {
            case "/kick":
                if (parts.length < 2) {
                    sendToClient(adminId, "Использование: /kick <id клиента>");
                    return;
                }
                try {
                    int targetId = Integer.parseInt(parts[1]);
                    kickClient(targetId, adminId);
                } catch (NumberFormatException e) {
                    sendToClient(adminId, "Ошибка: неверный ID клиента");
                }
                break;

            case "/shutdown":
                if (parts.length < 2) {
                    sendToClient(adminId, "Использование: /shutdown <секунды>");
                    return;
                }
                try {
                    int seconds = Integer.parseInt(parts[1]);
                    scheduleShutdown(seconds);
                } catch (NumberFormatException e) {
                    sendToClient(adminId, "Ошибка: неверное количество секунд");
                }
                break;

            default:
                sendToClient(adminId, "Неизвестная команда: " + cmd);
        }
    }

    /**
     * Отправить сообщение конкретному клиенту
     *
     * @param clientId ID клиента
     * @param message  сообщение
     */
    private void sendToClient(int clientId, String message) {
        for (ClientCom com : clientComs) {
            if (com.getId() == clientId) {
                com.send(message);
                return;
            }
        }
    }

    /**
     * Исключить клиента из чата
     *
     * @param targetId ID клиента для исключения
     * @param adminId  ID администратора
     */
    private void kickClient(int targetId, int adminId) {
        ClientCom targetClient = null;
        for (ClientCom com : clientComs) {
            if (com.getId() == targetId) {
                targetClient = com;
                break;
            }
        }

        if (targetClient == null) {
            sendToClient(adminId, "Клиент #" + targetId + " не найден");
            return;
        }

        if (targetId == adminId) {
            sendToClient(adminId, "Нельзя исключить самого себя");
            return;
        }

        // Уведомляем всех о исключении
        sendToAll("Клиент #" + targetId + " был исключён из чата администратором");
        
        // Закрываем соединение
        targetClient.disconnect();
        clientComs.remove(targetClient);
        
        System.out.println("Клиент #" + targetId + " был исключён администратором #" + adminId);
    }

    /**
     * Запланировать выключение сервера
     *
     * @param seconds количество секунд до выключения
     */
    private void scheduleShutdown(int seconds) {
        if (seconds <= 0) {
            shutdownServer();
            return;
        }

        sendToAll("Сервер прекратит свою работу через " + seconds + " секунд(ы)");
        System.out.println("Запланировано выключение сервера через " + seconds + " секунд");

        Timer timer = new Timer();
        
        // Отправляем уведомления каждую секунду, если осталось меньше 10 секунд
        if (seconds <= 10) {
            for (int i = seconds - 1; i > 0; i--) {
                final int remaining = i;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        sendToAll("Сервер прекратит свою работу через " + remaining + " секунд(ы)");
                    }
                }, (seconds - i) * 1000L);
            }
        } else {
            // Для больших промежутков времени уведомляем реже
            int[] intervals = {seconds / 2, 10, 5, 3, 2, 1};
            for (int interval : intervals) {
                if (interval < seconds) {
                    final int remaining = interval;
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            sendToAll("Сервер прекратит свою работу через " + remaining + " секунд(ы)");
                        }
                    }, (seconds - interval) * 1000L);
                }
            }
        }

        // Запланировать само выключение
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                shutdownServer();
            }
        }, seconds * 1000L);
    }

    /**
     * Выключить сервер
     */
    private void shutdownServer() {
        sendToAll("Сервер завершает работу...");
        System.out.println("Выключение сервера...");
        
        running = false;
        
        // Закрываем все клиентские соединения
        for (ClientCom com : new ArrayList<>(clientComs)) {
            com.disconnect();
        }
        clientComs.clear();
        
        // Закрываем серверный сокет
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
