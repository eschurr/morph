public class JTriangle
{

    private JControlPoint tri[];

    public JTriangle(int x1, int y1, int x2, int y2, int x3, int y3)
    {
        tri = new JControlPoint[3];
        tri[0] = new JControlPoint(x1, y1);
        tri[1] = new JControlPoint(x2, y2);
        tri[2] = new JControlPoint(x3, y3);
    }

    public double getX(int index)
    {
        if ((index >= 0) && (index < 6)) {
            return (tri[index].getX());
        }
        System.out.println("Index out of bounds in getX()");
        return (0.0);
    }

    public double getY(int index)
    {
        if ((index >= 0) && (index < 6))
            return (tri[index].getY());
        System.out.println("Index out of bounds in getY()");
        return (0.0);
    }

}
