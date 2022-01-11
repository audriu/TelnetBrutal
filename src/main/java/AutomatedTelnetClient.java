import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;

public class AutomatedTelnetClient {

    private TelnetClient telnet;
    private Socket socket;
    private final InputStream in;
    private final PrintStream out;
    private final String prompt;
    private long timeout = 10000L;

    //this constructor is for Apache commons TelnetClient socket
    public AutomatedTelnetClient(String server, String user, String password, String prompt, boolean mode) throws Exception {
        telnet = new TelnetClient();
        telnet.connect(server, 23);
        in = telnet.getInputStream();
        out = new PrintStream(telnet.getOutputStream());
        this.prompt = prompt;
        readUntil("Login:", true);
        write(user);
        readUntil("Password:", true);
        write(password);
//        write(user);
//        Thread.sleep(200L);
//        write(password);
        //readUntil(prompt);
    }

    //this constructor is for plain socket
    public AutomatedTelnetClient(String server, String user, String password, String prompt, Integer timeout) throws IOException, InterruptedException {
        socket = new Socket(server, 23);
        in = socket.getInputStream();
        out = new PrintStream(socket.getOutputStream());
        this.prompt = prompt;
        if (timeout != null) {
            this.timeout = timeout;
        }
//        readUntil(loginSTR);
//        write(user);
//        readUntil(passwdSTR);
//        write(password);
        Thread.sleep(200L);
        write(user);
        Thread.sleep(200L);
        write(password);
//        readUntil(prompt);
    }

    public String readUntil(String pattern, boolean throww) throws Exception {
        long lastTime = System.currentTimeMillis();
        String sb = "";
        while (true) {
            int c = -1;
            byte[] text;
            if (in.available() > 0) {
                c = in.read(text = new byte[in.available()]);
                sb = sb + new String(text);
            }
            long now = System.currentTimeMillis();
            if (c != -1) {
                lastTime = now;
            }
            if (now - lastTime > timeout) {
                if (throww)
                    throw new Exception("login timeout");
                else {
                    //System.out.println("timeout-----" + sb);
                    break;
                }
            }
            if (sb.toLowerCase().contains(pattern.toLowerCase())) {
                //System.out.println("found-----0");
                //System.out.println("found-----1" + sb);
                //System.out.println("found-----2" + pattern);
                break;
            }
            Thread.sleep(50);
        }
        //System.out.println("Read until: " + pattern + " - " + sb);
        return sb;
    }

    public void print(String value) {
        out.println(value + ";");
    }

    public void write(String value) {
        out.println(value);
        out.flush();
    }

    public String sendCommand(String command) throws Exception {
        write(command);
        return readUntil(prompt, false);
    }

    public void disconnect() throws IOException {
        if (socket != null) {
            socket.close();
        }
        if (telnet != null) {
            telnet.disconnect();
        }
    }

}
