public class Shape{
    private int height;
    private int length;

    /**
     * Makes shape with given height and length
     * @param height - height of shape
     * @param length - length of shape
     */
    public Shape(int height, int length){
        this.height = height;
        this.length = length;
    }

    /**
     * Makes shape from shape given as param
     * @param shape - shape to make object from
     */
    public Shape(Shape shape)
    {
        this.height = shape.height;
        this.length = shape.length;
    }

    /**
     * Sets height with given param
     * @param height - value to set height with
     */
    public void setHeight(int height){
        this.height = height;
    }

    /**
     * Returns height of shape
     * @return height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets length with given param
     * @param length - value to set length with
     */
    public void setLength(int length){
        this.length = length;
    }

    /**
     * Returns length of shape
     * @return length
     */
    public int getLength() {
        return length;
    }

    public String toString(){
        return "(" + height + ", " + length + ")";
    }

    /**
     * Returns shape as array
     * @return shape as array
     */
    public int[] getShape(){
        return new int[]{height, length};
    }
}
