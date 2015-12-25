package ${packagename};

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    private static final String LED0 = "/sys/class/leds/beaglebone:green:usr0";
    private static final String LED1 = "/sys/class/leds/beaglebone:green:usr1";
    private static final String LED2 = "/sys/class/leds/beaglebone:green:usr2";
    private static final String LED3 = "/sys/class/leds/beaglebone:green:usr3";

    private static final String HIGH = "1";
    private static final String LOW = "0";

    public static void main(String[] args) throws InterruptedException {
        try {
            digitalWrite(LED0, HIGH);
            Thread.sleep(1000);
            digitalWrite(LED1, HIGH);
            Thread.sleep(1000);
            digitalWrite(LED2, HIGH);
            Thread.sleep(1000);
            digitalWrite(LED3, HIGH);
            Thread.sleep(1000);
            digitalWrite(LED0, LOW);
            Thread.sleep(1000);
            digitalWrite(LED1, LOW);
            Thread.sleep(1000);
            digitalWrite(LED2, LOW);
            Thread.sleep(1000);
            digitalWrite(LED3, LOW);
            Thread.sleep(1000);
        } catch (IOException e) {
            System.out.println("Cannot access the Beaglebone black onboard LEDs");
        }
    }

    private static void digitalWrite(String gpioPort, String value) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(gpioPort + "/trigger"));
        bw.write("none");
        bw.close();
        bw = new BufferedWriter(new FileWriter(gpioPort + "/brightness"));
        bw.write(value);
        bw.close();
    }
}