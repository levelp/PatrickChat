package patrick;

import java.io.IOException;

public class Client {

    private final ClientData clientData;
    private final ClientCommucation client;

    public Client(ClientData clientData, String serverHost) throws IOException {

        this.clientData = clientData;

        client = new ClientCommucation(serverHost);
    }

    public void send(String line) {
        client.send(clientData.nickName + ": " + line);
    }
}
