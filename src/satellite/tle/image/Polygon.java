package satellite.tle.image;

//import java.util.Vector;

public class Polygon {
	public double[] xpoints;
	public double[] ypoints;
	public int npoints = 0;
	public Polygon() {
		npoints = 0;
	}
	public void addPoint(double x,double y)
	{
		xpoints[npoints] = x;
		ypoints[npoints] = y;
		npoints ++;
	}
}
