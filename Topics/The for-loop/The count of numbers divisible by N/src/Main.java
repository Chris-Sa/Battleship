import java.util.Scanner;

class Main {

    
    public int a;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // start coding here
        int a = scanner.nextInt();
        int b = scanner.nextInt();
        int c = scanner.nextInt();
        int count = 0;
        for (int i = a; i < b + 1; i++) {
            if (i % c == 0) {
                count += 1;
            }
        }
        System.out.println(count);
    }

}