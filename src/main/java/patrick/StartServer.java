package patrick;

public class StartServer {
    public static void main(String[] args) {
        System.out.println("StartServer.main");
        Server server = new Server();
        server.run();
    }
}
