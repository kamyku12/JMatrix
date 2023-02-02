import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.Scanner;

/**
 * Representation of matrices in Java
 * @author Jakub Kamiński
 * @version 1.0
 */
public class Matrix<T extends  > {

    protected Shape shape;
    protected T[][] data;
    protected double max = 0;
    protected double min = 0;
    protected boolean showAll = false;
    protected String maxElement;

    protected int maxLengthOfElement = 0;

    /**
     * Makes an empty matrix
     */
    public Matrix(){
        data = null;
        shape = new Shape(0, 0);
    }

    /**
     * Makes an empty matrix with given shape given by params
     * @param rows - number of rows for matrix
     * @param columns - number of columns for matrix
     */
    public Matrix(int rows, int columns){
        data = new T[rows][columns];
        shape = new Shape(rows, columns);
    }

    /**
     * Makes an empty matrix with given shape
     * @param shape - shape of matrix
     */
    public Matrix(Shape shape){
        data = new double[shape.getHeight()][shape.getLength()];
        this.shape = new Shape(shape);
    }

    /**
     * Makes a matrix from 2d array with condition if constants should be refreshed or not
     * @param data - 2d array to make matrix from
     * @param refresh - boolean value saying if constant should be refreshed
     * @throws IllegalArgumentException when 2d array is null
     */
    public Matrix(double[][] data, boolean refresh){
        if(data == null){
            throw new IllegalArgumentException("Given array is null");
        }

        this.data = new double[data.length][data[0].length];
        for(int i = 0; i < data.length; i++){
            for(int j = 0; j < data[0].length; j++){
                this.data[i][j] = data[i][j];
            }
        }
        shape = new Shape(data.length, data[0].length);
        if(refresh)
            refreshConstants();
    }

    /**
     * Makes a 2d array with values of param
     * @param data - 2d array with values of double
     * @throws IllegalArgumentException when 2d array is null
     */
    public Matrix(double[][] data){
        this(data, true);
    }

    /**
     * Makes a 1d matrix with values of param
     * @param data - 1d array with values of double
     * @throws IllegalArgumentException when given array is null
     */
    public Matrix(double[] data) {
        if(data == null){
            throw new IllegalArgumentException("Given array is null");
        }

        shape = new Shape(1, data.length);

        this.data = new double[shape.getHeight()][shape.getLength()];
        for(int j = 0; j < shape.getLength(); j++){
            this.data[0][j] = data[j];
        }

        refreshConstants();
        transpose();
    }


    /**
     * Makes and copy of a matrix given as param
     * @param m - matrix to copy from
     */
    public Matrix(Matrix m){
        this(m.getData());
    }

    /**
     * Reads data from file and makes matrix from those data
     * @param filePath - file to read from
     * @param separator - separator which divides values in file
     * @throws RuntimeException when there is word instead of value in file
     */
    public void getDataFromTxt(String filePath, char separator){
        File f = new File(filePath);

        int rows = countRowsFromTxt(f);
        int cols = countColumnsFromTxt(f, separator);
        boolean changeToNormal = false;
        shape = new Shape(rows, cols);

        data = new double[rows][cols];

        try(Scanner input = new Scanner(f)) {
            int x = 0;
            while(input.hasNextLine()){
                String line = input.nextLine();
                StringBuilder sb = new StringBuilder();
                int c = 0;
                int y = 0;
                while(c <= line.length()) {
                    if (c == line.length() || line.charAt(c) == ',') {
                        double value;
                        try{
                            value = Double.parseDouble(sb.toString());
                        }
                        catch (NumberFormatException e){
                            if(sb.toString().length() > 1)
                                throw new RuntimeException("Cannot make number from word");
                            changeToNormal = true;
                            value = returnStringAsDouble(sb.toString());
                        }
                        data[x][y] = value;
                        y++;
                        sb.delete(0, sb.length());
                        c++;
                        continue;
                    }
                    sb.append(line.charAt(c));
                    c++;
                }
                x++;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        if(changeToNormal)
            changeToNormalData();
        else
            refreshConstants();
    }

    /**
     * Reads data from file and makes matrix from those data
     * @param fileName - file to read from
     * @param separator - separator which divides values in file
     * @return Matrix
     */
    public static Matrix getMatrixFromTxt(String fileName, char separator){
        Matrix c = new Matrix();
        c.getDataFromTxt(fileName, separator);
        return c;
    }

    //Method changes data from char to double
    private void changeToNormalData(){
        for(int i = 0; i < shape.getLength(); i++){
            double[] unique = getUniqueValues(getColumn(i));
            row: for(int j = 0; j < shape.getHeight(); j++)
                for(int un = 0; un < unique.length; un++) {
                    if (data[j][i] == unique[un]) {
                        data[j][i] = un;
                        break;
                    }
                }
        }
    }

    private static double returnStringAsDouble(String s){
        return s.charAt(0);
    }

    /**
     * Returns data from matrix
     * @return double[][]
     */
    public double[][] getData() {
        return data.clone();
    }

    private static int countRowsFromTxt(File f){
        int rows = 0;
        try(Scanner input = new Scanner(f)){
            while (input.hasNextLine()){
                input.nextLine();
                rows++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return rows;
    }

    private static int countColumnsFromTxt(File f, char separator){
        int cols = 0;
        try(Scanner input = new Scanner(f)){
            String line = input.nextLine();
            int c = 0;
            while(c <= line.length()){
                if(c == line.length() || line.charAt(c) == separator){
                    cols++;
                }
                c++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return cols;
    }

    /**
     * Sets data of matrix to data from param
     * @param data - 2d array with values of double
     */
    public void setData(double[][] data) {
        this.data = data;
        shape.setHeight(data.length);
        shape.setLength(data[0].length);
        refreshConstants();
    }

    /**
     * Refreshes constants from data
     */
    protected void refreshConstants(){
        refreshMaxLengthOfElement();
        refreshMaxMinValue();
    }

    /**
     * Refreshes max length of value from data
     */
    //Metoda do odświeżenia długości tekstu, aby wyświetlany format był bardziej kompatkowy i czytelny
    protected void refreshMaxLengthOfElement(){
        if(data != null) {
            maxLengthOfElement = String.valueOf(data[0][0]).length();
            for (int i = 0; i < shape.getHeight(); i++) {
                if(!showAll && i == 3)
                    i = shape.getHeight() - 3;
                for (int j = 0; j < shape.getLength(); j++) {
                    if(!showAll && j == 3)
                        j = shape.getLength() - 3;
                    int lengthOfValue = String.valueOf(data[i][j]).length();
                    if (lengthOfValue > maxLengthOfElement) {
                        maxLengthOfElement = lengthOfValue;
                        maxElement = String.valueOf(data[i][j]);
                    }
                }
            }
        }
    }

    /**
     * Refreshes max and min value from data
     */
    protected void refreshMaxMinValue(){
        for(int i = 0; i < shape.getHeight(); i++){
            for(int j = 0; j < shape.getLength(); j++){
                if(data[i][j] > max)
                    max = data[i][j];

                if(data[i][j] < min)
                    min = data[i][j];
            }
        }
    }


    /**
     * Pops column from matrix and returns it
     * @param colNumber - number of column to pop - <0;length)
     * @return double[][] - popped column
     * @throws IllegalArgumentException when matrix is empty or given number exceeds matrix shape
     */
    public double[][] popColumn(int colNumber){
        if(data == null){
            throw new IllegalArgumentException("Matrix is empty");
        }

        if(colNumber >= shape.getLength() || colNumber < 0){
            throw new IllegalArgumentException("Column does not exist");
        }

        double[][] toReturn = new double[shape.getHeight()][1];
        for(int i = 0; i < shape.getHeight(); i++){
            toReturn[i][0] = data[i][colNumber];
        }

        deleteColumnFromMatrix(colNumber);
        return toReturn;
    }

    /**
     * Pops row from matrix and returns it
     * @param rowNumber - number of row to pop - <0;height)
     * @return double[][] - popped row
     * @throws IllegalArgumentException when matrix is empty or row number exceeds matrix shape
     */
    public double[][] popRow(int rowNumber){
        if(data == null){
            throw new IllegalArgumentException("Matrix is empty");
        }

        if(rowNumber >= shape.getHeight() || shape.getHeight() < 0){
            throw new IllegalArgumentException("Row does not exist");
        }

        double[][] toReturn = new double[1][shape.getLength()];
        for(int i = 0; i < shape.getLength(); i++){
            toReturn[i][0] = data[rowNumber][i];
        }

        deleteRowFromMatrix(rowNumber);
        return toReturn;
    }


    /**
     * Deletes column from matrix
     * @param colNumber - number of column - <0;length)
     * @throws IllegalArgumentException when matrix is empty or column number exceeds matrix shape
     */
    //Metoda usuwa kolumnę z tablicy
    public void deleteColumnFromMatrix(int colNumber){
        if(data == null){
            throw new IllegalArgumentException("Matrix is empty");
        }

        if(colNumber >= shape.getLength() || colNumber < 0){
            throw new IllegalArgumentException("Column does not exist");
        }

        double[][] tmp = new double[shape.getHeight()][shape.getLength() - 1];
        for(int i = 0; i < shape.getHeight(); i++){
            int tmpj = 0;
            for(int j = 0; j < shape.getLength(); j++){
                if(j == colNumber)
                    continue;

                tmp[i][tmpj++] = data[i][j];
            }
        }

        setData(tmp);
    }

    /**
     * Deletes row from matrix
     * @param rowNumber - number of row to delete - <0;height)
     * @throws IllegalArgumentException when matrix is empty or row number exceeds matrix shape
     */
    public void deleteRowFromMatrix(int rowNumber){
        if(data == null){
            throw new IllegalArgumentException("Matrix is empty");
        }

        if(rowNumber >= shape.getHeight() || shape.getHeight() < 0){
            throw new IllegalArgumentException("Row does not exist");
        }

        double[][] tmp = new double[shape.getHeight() - 1][shape.getLength()];

        int tmpi = 0;
        for(int i = 0; i < shape.getHeight(); i++){
            if(i == rowNumber)
                continue;
            for(int j = 0; j < shape.getLength(); j++){
                tmp[tmpi][j] = data[i][j];
            }
            tmpi++;
        }

        setData(tmp);
    }

    /**
     * Returns max value of column
     * @param columnNumber - number of column
     * @return max value of column
     * @throws RuntimeException when there is no value to read
     */
    public double maxFromColumn(int columnNumber){
        OptionalDouble maxC = Arrays.stream(getColumn(columnNumber)).max();
        if(maxC.isPresent())
            return maxC.getAsDouble();
        else
            throw new RuntimeException("Cannot read value");
    }

    /**
     * Return min value of column
     * @param columnNumber - number of column
     * @return min value of column
     * @throws RuntimeException when there is no value to read
     */
    public double minFromColumn(int columnNumber){
        OptionalDouble minC = Arrays.stream(getColumn(columnNumber)).min();
        if(minC.isPresent())
            return minC.getAsDouble();
        else
            throw new RuntimeException("Cannot read value");
    }

    /**
     * Return max value from row
     * @param rowNumber - number of row
     * @return max value from row
     * @throws RuntimeException when there is no value to read
     */
    public double maxFromRow(int rowNumber){
        OptionalDouble maxR = Arrays.stream(getRow(rowNumber)).max();
        if(maxR.isPresent())
            return maxR.getAsDouble();
        else
            throw new RuntimeException("Cannot read value");
    }

    /**
     * Return min number from row
     * @param rowNumber - number of row
     * @return min value from row
     * @throws RuntimeException when there is no value to read
     */
    public double minFromRow(int rowNumber){
        OptionalDouble minR = Arrays.stream(getRow(rowNumber)).min();
        if(minR.isPresent())
            return minR.getAsDouble();
        else
            throw new RuntimeException("Cannot read value");
    }

    /**
     * Returns row from matrix as 1d array
     * @param rowNumber - number of row
     * @return double[] - data of row
     */
    public double[] getRow(int rowNumber){
        double[] tmp = new double[shape.getLength()];
        for(int i = 0; i < shape.getLength(); i++)
            tmp[i] = data[rowNumber][i];

        return tmp;
    }

    /**
     * Returns column from matrix as 1d array
     * @param colNumber - number of column
     * @return double[] - data from column
     */
    public double[] getColumn(int colNumber){
        double[] tmp = new double[shape.getHeight()];
        for(int i = 0; i < shape.getHeight(); i++)
            tmp[i] = data[i][colNumber];

        return tmp;
    }

    /**
     * Sets data of given record from matrix
     * @param row - number of row to choose
     * @param column - number of column to choose
     * @param value - value to put in
     */
    public void setIndividualData(int row, int column, double value){
        this.data[row][column] = value;
        if(value > max)
            max = value;
        if(value < min)
            min = value;
        if(String.valueOf(value).length() > maxLengthOfElement)
            maxLengthOfElement = String.valueOf(value).length();
    }

    /**
     * Addition of two matrices
     * @param m2 - matrix to add
     * @throws IllegalArgumentException when matrices do not have exact shapes
     */
    public void add(Matrix m2){
        if((m2.shape.getHeight() != shape.getHeight()) || (m2.shape.getLength() != shape.getLength()) )
            throw new IllegalArgumentException("Matrices need to have the same shape");

        setData(add(getData(), m2.getData()));
    }

    /**
     * Addition of two matrices
     * @param m1 - matrix to add
     * @param m2 - matrix to add
     * @return Result of addition as matrix
     * @throws IllegalArgumentException when matrices do not have exact shapes
     */
    public static Matrix add(Matrix m1, Matrix m2){
        if((m1.shape.getHeight() != m2.shape.getHeight()) || (m1.shape.getLength() != m2.shape.getLength()))
            throw new IllegalArgumentException("Matrices need to have the same shape");

        return new Matrix(add(m1.getData(), m2.getData()));
    }


    /**
     * Addition of two 2d array
     * @param t1 - 2d array
     * @param t2 - 2d array
     * @return Result of addition as matrix
     */
    public static double[][] add(double[][] t1, double[][] t2){
        double[][] tmp = new double[t1.length][t1[0].length];
        for(int i = 0; i < tmp.length; i++)
            for(int j = 0; j < tmp[0].length; j++)
                tmp[i][j] = t1[i][j] + t2[i][j];

        return tmp;
    }

    /**
     * Subtraction of two matrices
     * @param m2 - matrix to subtract
     * @throws IllegalArgumentException when matrices do not have exact shapes
     */
    public void substract(Matrix m2){
        if((m2.shape.getHeight() != shape.getHeight()) || (m2.shape.getLength() != shape.getLength()) )
            throw new IllegalArgumentException("Matrices need to have the same shape");

        setData(substract(getData(), m2.getData()));
    }

    /**
     * Subtraction of two matrices
     * @param m1 - matrix to subtract from
     * @param m2 - matrix to subtract
     * @return Result of subtraction as matrix
     * @throws IllegalArgumentException when matrices do not have exact shapes
     */
    public static Matrix substract(Matrix m1, Matrix m2){
        if((m1.shape.getHeight() != m2.shape.getHeight()) || (m1.shape.getLength() != m2.shape.getLength()))
            throw new IllegalArgumentException("Matrices need to have the same shape");

        return new Matrix(substract(m1.getData(), m2.getData()));
    }

    /**
     * Subtraction of two 2d arrays
     * @param t1 - 2d array to subtract from
     * @param t2 - 2d array to subtract
     * @return Result of subtraction as 2d array
     */
    //Metoda zwraca tablice zawierajaca roznice podanych w argumentach tablic
    public static double[][] substract(double[][] t1, double[][] t2){
        double[][] tmp = new double[t1.length][t1[0].length];
        for(int i = 0; i < tmp.length; i++)
            for(int j = 0; j < tmp[0].length; j++)
                tmp[i][j] = t1[i][j] - t2[i][j];

        return tmp;
    }

    /**
     * Multiplies matrix by scalar
     * @param a - scalar to multiply matrix with
     */
    public void multiply(double a){
        setData(multiply(getData(), a));
    }

    /**
     * Multiplies matrix by scalar and returns that result
     * @param m - matrix to multiply
     * @param a - scalar to multiply matrix with
     * @return Result of multiplication as matrix
     */
    public static Matrix multiply(Matrix m, double a){
        return new Matrix(multiply(m.getData(), a));
    }

    /**
     * Multiplies 2d array by scalar and returns that result
     * @param t - 2d array to multiply
     * @param a - scalar to multiply array with
     * @return Result of multiplication as 2d array
     */
    public static double[][] multiply(double[][] t, double a){
        double[][] tmp = new double[t.length][t[0].length];
        for(int i = 0; i < tmp.length; i++)
            for(int j = 0; j < tmp[0].length; j++)
                tmp[i][j] = t[i][j] * a;

        return tmp;
    }

    /**
     * Matrix multiplication
     * @throws IllegalArgumentException when number of rows from second matrix is not equal to number of columns from
     * first matrix
     * @param m - matrix to multiply by
     */
    public void multiply(Matrix m){
        if(shape.getLength() != m.shape.getHeight())
            throw new IllegalArgumentException(
                    "number of rows from second matrix is not equal to number of columns from first matrix");

        setData(multiply(getData(), m.getData()));
    }

    /**
     * Multiplies matrix by themselves and returns result
     * @param m1 - first matrix to multiply
     * @param m2 - second matrix to multiply
     * @return Result of multiplication as matrix
     * @throws IllegalArgumentException when height of second matrix is not equal to length of first matrix
     */
    public static Matrix multiply(Matrix m1, Matrix m2){
        if(m1.shape.getLength() != m2.shape.getHeight())
            throw new IllegalArgumentException("Height of second matrix must equal length of first matrix");

        return new Matrix(multiply(m1.getData(), m2.getData()));
    }

    /**
     * Multiplies array by themselves as matrix multiplication and returns result
     * @param t1 - first 2d array to multiply
     * @param t2 - second matrix to multiply
     * @return Result of multiplication as 2d array
     * @throws IllegalArgumentException when height of second matrix is not equal to length of first matrix
     */
    public static double[][] multiply(double[][] t1, double[][] t2){
        if(t1[0].length != t2.length)
            throw new IllegalArgumentException("Height of second matrix must equal length of first matrix");

        double[][] tmp = new double[t1.length][t2[0].length];
        for(int i = 0; i < tmp.length; i++)
            for(int j = 0; j < tmp[0].length; j++)
                for(int m = 0; m < t2.length; m++)
                    tmp[i][j] += t1[i][m] * t2[m][j];

        return tmp;
    }

    /**
     * Divides matrix by scalar
     * @param a - scalar to divide matrix with
     */
    public void divide(double a){
        setData(divide(getData(), a));
    }

    /**
     * Divides matrix by scalar
     * @param m - matrix to divide
     * @param a - scalar to divide matrix with
     * @return Result of division as matrix
     */
    public static Matrix divide(Matrix m, double a){
        return new Matrix(divide(m.getData(), a));
    }

    /**
     * Divides 2d array by scalar
     * @param t - 2d array to divide
     * @param a - scalar to divide array with
     * @return Result of division as 2d array
     */
    public static double[][] divide(double[][] t, double a){
        double[][] tmp = new double[t.length][t[0].length];
        for(int i = 0; i < tmp.length; i++)
            for(int j = 0; j < tmp[0].length; j++)
                tmp[i][j] = t[i][j] / a;

        return tmp;
    }

    /**
     * Divides matrix by matrix
     * @param m2 - matrix to divide by
     */
    public void divide(Matrix m2){
        m2.inv();
        multiply(m2);
    }

    /**
     * Divides matrix by matrix
     * @param m1 - matrix to divide
     * @param m2 - matrix to divide by
     * @return Result of division as matrix
     */
    public static Matrix divide(Matrix m1, Matrix m2){
        Matrix toReturn = new Matrix(m1);
        toReturn.divide(m2);

        return toReturn;
    }

    /**
     * Power of matrix
     * @param p - exponent of power
     */
    public void pow(int p){
        for(int i = 0; i < p - 1; i++)
            multiply(this);
    }

    /**
     * Power of matrix
     * @param m - matrix to power
     * @param p - exponent of power
     * @return Result of power as matrix
     */
    public static Matrix pow(Matrix m, int p){
        Matrix c = new Matrix(m);
        c.pow(p);

        return c;
    }

    /**
     * Transposition of matrix
     */
    public void transpose(){
        double[][] tmp = new double[shape.getLength()][shape.getHeight()];
        for(int i = 0; i < shape.getLength(); i++){
            for(int j = 0; j < shape.getHeight(); j++)
                tmp[i][j] = data[j][i];
        }

        setData(tmp);
    }

    /**
     * Determinant of matrix
     * @throws IllegalArgumentException when matrix is not square matrix
     * @return determinant of matrix
     */
    //Metoda zwraca wyznacznik macierzy
    public double det(){
        if(shape.getHeight() != shape.getLength())
            throw new IllegalArgumentException("Matrix is not square");

        if(shape.getHeight() == 1)
            return data[0][0];

        if (shape.getHeight() == 2)
            return data[0][0] * data[1][1] - data[1][0] * data[0][1];


        double det = 0;
        int i = 0;
        for(int j = 0; j < shape.getLength(); j++){
            Matrix minor = new Matrix(this);
            minor.deleteRowFromMatrix(i);
            minor.deleteColumnFromMatrix(j);
            det += Math.pow(-1, i + j) * data[i][j] * minor.det();
        }
        return det;
    }

    /**
     * Inverts matrix
     * @throws IllegalArgumentException when determinant of matrix is equal to 0
     */
    public void inv(){
        double det = det();
        if(det == 0)
            throw new IllegalArgumentException("Cannot inverse matrix, determinant is equal to 0");

        for(int i = 0; i < shape.getHeight(); i++){
            for(int j = 0; j < shape.getLength(); j++){
                Matrix minor = new Matrix(this);
                minor.deleteRowFromMatrix(i);
                minor.deleteColumnFromMatrix(j);
                setIndividualData(i, j, Math.pow(-1,i+j) * minor.det());
            }
        }
        transpose();
        divide(det);
    }

    /**
     * Inverts matrix m
     * @param m - matrix to invert
     * @return Result of inversion as matrix
     */
    public static Matrix inv(Matrix m){
        Matrix inverted = new Matrix(m);
        inverted.inv();

        return inverted;
    }

    /**
     * Makes identity matrix
     * @param k - dimension of matrix
     * @return identity matrix
     * @throws IllegalArgumentException when k is less than 1
     */
    //Metoda zwraca macierz jednostkową o wymiarach k x k
    public static Matrix identity(int k){
        if(k < 1)
            throw new IllegalArgumentException("Matrix cannot have shape less than 1");

        Matrix I = new Matrix(k, k);
        int x = 0, y = 0;
        while(x < k && y < k){
            I.setIndividualData(x++, y++, 1);
        }
        return I;
    }

    /**
     * Sum of matrix
     * @return sum of matrix
     */
    public double sum(){
        double sum = 0;
        for(int i = 0; i < shape.getHeight(); i++){
            for(int j = 0; j < shape.getLength(); j++){
                sum += data[i][j];
            }
        }
        return sum;
    }

    /**
     * Sum of matrix
     * @param m - matrix to sum
     * @return sum of matrix m
     */
    public static double sum(Matrix m){
        return m.sum();
    }

    /**
     * Mean value of matrix
     * @return mean value of matrix
     */
    public double mean(){
        return sum()/(shape.getHeight() * shape.getLength());
    }

    /**
     * Mean value of matrix
     * @param m - matrix to calculate mean
     * @return mean of matrix m
     */
    public static double mean(Matrix m){
        return m.mean();
    }

    /**
     * Unique values from matrix
     * @return unique values from matrix as 1d array
     */
    public double[] getUniqueValues(){
        ArrayList<Double> unique = new ArrayList<>();
        for(int i = 0; i < shape.getHeight(); i++){
            for(int j = 0; j < shape.getLength(); j++){
                if(!unique.contains(data[i][j]))
                    unique.add(data[i][j]);
            }
        }
        double[] toReturn = new double[unique.size()];
        int i = 0;
        for(Double D : unique)
            toReturn[i++] = D;

        return toReturn;
    }

    /**
     * Unique values from 1d array
     * @param t - 1d array
     * @return unique values as 1d array
     */
    public static double[] getUniqueValues(double[] t){
        ArrayList<Double> unique = new ArrayList<>();
        for (double v : t) {
            if (!unique.contains(v))
                unique.add(v);
        }
        double[] toReturn = new double[unique.size()];
        int i = 0;
        for(Double D : unique)
            toReturn[i++] = D;

        return toReturn;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < shape.getHeight(); i++){
            if(!showAll) {
                if (shape.getHeight() > 6 && i == 3) {
                    sb.append("| ").append(String.format("%" + maxLengthOfElement + "s", ". . .")).append("\n");
                    i = shape.getHeight() - 3;
                }
            }

            if((i == 0) || (i == shape.getHeight() - 1))
                sb.append("[ ");
            else
                sb.append("| ");

            for(int j = 0; j < shape.getLength(); j++){
                if(!showAll) {
                    if (shape.getLength() > 6 && j == 3) {
                        sb.append(String.format("%" + maxLengthOfElement + "s", ". . .")).append(' ');
                        j = shape.getLength() - 3;
                    }
                }
                sb.append(String.format("%" + maxLengthOfElement + "s", data[i][j])).append(" ");
            }

            if((i == 0) || (i == shape.getHeight() - 1))
                sb.append("]\n");
            else
                sb.append("|\n");
        }

        return sb.toString();
    }

    /**
     * Sets boolean value if show shortened version of matrix on toString method or not
     * @param showAll - value to set
     */
    public void showAll(boolean showAll){
        this.showAll = showAll;
    }

    /**
     * @return number of rows in matrix
     */
    public int getHeight(){
        return shape.getHeight();
    }

    /**
     * @return number of columns in matrix
     */
    public int getLength(){
        return shape.getLength();
    }

    /**
     * @return shape of matrix as array of int
     */
    public int[] getShape() {
        return shape.getShape();
    }

    /**
     * Concatenation of matrix and 1d array by columns
     * @param data - 1d array to concatenate
     * @throws IllegalArgumentException when number of rows of matrix and array is not equal
     */
    public void colsConcatenate(double[] data){
        if(data.length != shape.getHeight())
            throw new IllegalArgumentException("Number of rows are not equal");

        double[][] oldData = getData();
        this.data = new double[shape.getHeight()][shape.getLength() + 1];
        shape.setLength(shape.getLength() + 1);

        for(int i = 0; i < oldData.length; i++)
            for(int j = 0; j < oldData[0].length; j++)
                setIndividualData(i, j, oldData[i][j]);

        for(int i = 0; i < shape.getHeight(); i++)
            setIndividualData(i, shape.getLength() - 1, data[i]);
    }

    /**
     * Concatenation of matrix and 2d array by columns
     * @param data - 2d array to concatenate
     * @throws IllegalArgumentException when number of rows of matrix and array is not equal
     */
    //metoda laczy macierz kolumnowo z talica dwuwymiarowa
    public void colsConcatenate(double[][] data){
        if(data.length != shape.getHeight())
            throw new IllegalArgumentException("Number of rows are not equal");

        double[][] oldData = getData();
        this.data = new double[shape.getHeight()][shape.getLength() + data[0].length];
        shape.setLength(shape.getLength() + data[0].length);

        for(int i = 0; i < oldData.length; i++)
            for(int j = 0; j < oldData[0].length; j++)
                setIndividualData(i, j, oldData[i][j]);

        int k = 0;
        for(int i = 0; i < shape.getHeight(); i++)
            for(int j = oldData[0].length; j < shape.getLength(); j++) {
                setIndividualData(i, j, data[i][k]);
                k = (k + 1) % data[0].length;
            }
    }

    /**
     * Concatenation of matrices by column
     * @param m1 - matrix to concatenate
     * @param m2 - matrix to concatenate
     * @return Result of concatenation as matrix
     */
    public static Matrix colsConcatenate(Matrix m1, Matrix m2){
        Matrix toReturn = new Matrix(m1);
        toReturn.colsConcatenate(m2.getData());
        return toReturn;
    }

    /**
     * Fills matrix with given value
     * @param value - value to fill with
     */
    public void fill(double value){
        if( data == null)
            throw new RuntimeException("Cannot fill beacuse array is null");
        for(double[] data : data){
            Arrays.fill(data, value);
        }
        refreshConstants();
    }
}
