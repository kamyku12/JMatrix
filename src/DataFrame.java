/*
 * Jest to klasa która określi obiekt DataFrame, posiada on właściwości macierzy,
 * Posiada dodatkowe pole "label", które pozwala określić słownie daną kolumnę
 */
public class DataFrame extends Matrix{
    protected String[] labels;

    public DataFrame(){
        super();
        labels = null;
    }

    public DataFrame(int rows, int columns){
        super(rows, columns);
        labels = null;
    }

    public DataFrame(double[][] data){
        super(data);
        labels = null;
        refreshConstants();
    }

    public DataFrame(Matrix m){
        super(m);
        labels = null;
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

    public void changeLabel(int labelNumber, String newLabel){
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(labels != null) {
            for (int i = 0; i < labels.length; i++) {
                if (i != 0)
                    sb.append(" ").append(String.format("%" + maxLengthOfElement + "s", labels[i]));
                else
                    sb.append(" ").append(labels[i]);
            }
        }
        sb.append("\n");
        sb.append(super.toString());
        return sb.toString();
    }
}
