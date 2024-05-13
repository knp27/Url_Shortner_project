package in.co.codeplanet.urlshortner.utility;

import java.util.Random;

public class Otp {
    public static String generateOtp(int noOfDigit) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i <= noOfDigit; i++) {
            int r = random.nextInt(0, 10);
            sb.append(r);
        }
        //System.out.println(sb);
        return sb.toString();
    }
}
