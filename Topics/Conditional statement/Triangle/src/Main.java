import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // start coding here

        int a = scanner.nextInt();
        int b = scanner.nextInt();
        int c = scanner.nextInt();


        System.out.println(c = a - b > 0 ? a + b : a - b);

        System.out.println(c = a <= b ? a + b : a - b);

        //System.out.println(c = a > b !! a + b : a - b);

        System.out.println(c = a - b > 0 ? a - b : a + b);

        //System.out.println(c = a - b : a + b ? a >= b};
    }
}