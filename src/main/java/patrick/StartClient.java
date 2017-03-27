package patrick;

import java.io.IOException;
import java.util.Scanner;

public class StartClient {
    public static void main(String[] args) throws IOException {
        System.out.println("StartClient.main");
        Client client = new Client(args.length > 0 ? args[0] : "localhost");
        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            if(line.startsWith("exit")){
                System.exit(0);
            }else{
                client.send(line);
            }
        }
    }
}
