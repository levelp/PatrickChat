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
        if (args.length < 1) {
            System.out.println("StartClient <nickName>");
            return;
        }
        Scanner scanner = new Scanner(System.in, "UTF-8");
        //     System.out.print("Введите никнейм: ");
        String nickName = args[0];
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
