import java.util.Arrays;

/*
 * Jest to klasa która określi obiekt DataFrame, posiada on właściwości macierzy,
 * Posiada dodatkowe pole "label", które pozwala określić słownie daną kolumnę
 */
public class DataFrame extends Matrix{
    private String[] labels;

    public DataFrame(){
        super();
        labels = null;
    }

    public DataFrame(int rows, int columns){
        super(rows, columns);
        labels = new String[columns];
        Arrays.fill(labels, "");
    }

    public DataFrame(double[][] data){
        super(data);
        labels = new String[data[0].length];
        Arrays.fill(labels, "");
        refreshConstants();
    }

    public DataFrame(Matrix m){
        super(m);
        labels = new String[m.getLength()];
        Arrays.fill(labels, "");
        refreshConstants();
    }

    public DataFrame(DataFrame df){
        this.data = df.getData();
        this.labels = df.getLabels();
        this.max = df.max;
        this.min = df.min;
        this.shape = new Shape(df.shape.getHeight(), df.shape.getLength());
        this.maxLengthOfElement = df.maxLengthOfElement;
    }

    public DataFrame(Shape s){
        super(s);
        labels = new String[s.getLength()];
        Arrays.fill(labels, "");
    }

    @Override
    public void refreshConstants(){
        refreshMaxLengthOfElement();
        refreshMaxMinValue();
    }

    @Override
    protected void refreshMaxLengthOfElement(){
        if(data != null) {
            for (int i = 0; i < shape.getHeight(); i++) {
                for (int j = 0; j < shape.getLength(); j++) {
                    int lengthOfValue = String.valueOf(data[i][j]).length();
                    if (lengthOfValue > maxLengthOfElement)
                        maxLengthOfElement = lengthOfValue;
                }
            }
        }

        if(labels != null) {
            for (int i = 0; i < labels.length; i++) {
                int lenthOfLabel = labels[i].length();
                if (lenthOfLabel > maxLengthOfElement)
                    maxLengthOfElement = lenthOfLabel;
            }
        }
    }

    public String[] getLabels() {
        return labels;
    }

    public void setLabels(String[] labels) {
        if(this.labels == null){
            this.labels = new String[labels.length];
            System.arraycopy(labels, 0, this.labels, 0, labels.length);
        }
        else{
            System.arraycopy(labels, 0, this.labels, 0, labels.length);
        }
        refreshMaxLengthOfElement();
    }

    public void setLabel(int labelNumber, String newLabel){
        if(labels == null){
            throw new IllegalArgumentException("Etykiety są puste");
        }

        if(labelNumber >= labels.length || labelNumber < 0){
            throw new IllegalArgumentException("Nieprawidłowy numer etykiety");
        }

        labels[labelNumber] = newLabel;
        refreshMaxLengthOfElement();
    }

    public void deleteLabel(int labelNumber){
        if(labels != null) {
            String[] tmpLabels = new String[shape.getLength() - 1];
            for(int i = 0; i < shape.getLength(); i++){
                if(i == labelNumber)
                    continue;

                tmpLabels[i] = labels[i];
            }
            setLabels(tmpLabels);
        }
    }

    @Override
    public double[][] popColumn(int colNumber){
        if(data == null){
            throw new IllegalArgumentException("Macierz jest pusta");
        }

        if(colNumber >= shape.getLength() || colNumber < 0){
            throw new IllegalArgumentException("Nieprawidłowa kolumna");
        }

        double[][] toReturn = new double[shape.getHeight()][1];
        for(int i = 0; i < shape.getHeight(); i++){
            toReturn[i][0] = data[i][colNumber];
        }


        deleteLabel(colNumber);
        deleteColumnFromMatrix(colNumber);
        return toReturn;
    }

    public void fillLabels(String stringToFill)
    {
        Arrays.fill(this.labels, stringToFill);
        refreshMaxLengthOfElement();
    }

    public double[] getColumn(String columnName)
    {
        int i = 0;
        for(; i < labels.length; i++)
            if(labels[i].equals(columnName))
                break;
        return getColumn(i);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(labels != null) {
            for (int i = 0; i < labels.length; i++) {
                if(!showAll) {
                    if (shape.getHeight() > 6 && i == 3) {
                        sb.append("| ").append(String.format("%" + maxLengthOfElement + "s", ". . .")).append("\n");
                        i = shape.getHeight() - 3;
                    }
                }
                sb.append(" ").append(String.format("%" + maxLengthOfElement + "s", labels[i]));
            }
        }
        sb.append("\n");
        sb.append(super.toString());
        return sb.toString();
    }
}
