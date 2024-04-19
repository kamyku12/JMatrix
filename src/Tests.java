public class Tests {
    public static void main(String[] args) {
        Matrix m = Matrix.random(10, 10);
        Matrix m2 = new Matrix(10, 10);
        m2.fill(0);

        Matrix m3 = Matrix.multiply(m, m2);
        System.out.println(m3);
    }
}
