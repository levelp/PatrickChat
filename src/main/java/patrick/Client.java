package patrick;

import java.io.IOException;

public class Client {

    private final ClientData clientData;
    private final ClientCommucation client;

    public Client(ClientData clientData) throws IOException {

        this.clientData = clientData;

        client = new ClientCommucation("192.168.1.86");
    }

    public void send(String line) {
        client.send(clientData.nickName + ": " + line);
    }
}
