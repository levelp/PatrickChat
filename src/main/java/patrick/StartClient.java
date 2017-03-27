package patrick;

import java.io.IOException;
import java.util.Scanner;

public class StartClient {
    public static void main(String... args) throws IOException {
        // 1. Функциональность
        // 2. Надёжность или Интерфейс (юзабилити)
        // ...
        // 3. Сопровождения
        // ...
        Scanner scanner = new Scanner(System.in, "UTF-8");
        String nickName;
        if (args.length < 1) {
            System.out.print("Введите никнейм: ");
            nickName = scanner.nextLine();
        } else {
            nickName = args[0];
        }
        System.out.println("Вы можете отправлять сообщения в чат вводя их и нажимая Enter");

        ClientData clientData = new ClientData(nickName);

        Client client = new Client(clientData);
//        ClientCommucation client = new ClientCommucation(args.length > 0 ? args[0] : "localhost");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("exit")) {
                System.exit(0);
            } else {
                client.send(line);
            }
        }
    }
}
