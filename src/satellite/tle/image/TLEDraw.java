package satellite.tle.image;

import java.util.List;

import satellite.tle.utilities.LLA;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class TLEDraw {
	
	public static double degreeToRadian(double degree) {
		return (degree * Math.PI) / 180.0d;
	}
	public static void drawMapGround(Canvas canvas, List<LLA> gt,int cx, int cy, int width, int high,Paint paint) {

	    double longSpan = 360.0; // span of longitude
	    double latSpan = 180.0;
	        
	    int midX = (int) cx;//totWidth/2;
	    int midY = (int) cy;//totHeight/2;
	        
	    double leftX = midX - width;//imgWidth/2;
	    double rightX = midX + width;//imgWidth/2;
	        
	    double topY = midY - high;//imgHeight/2;
	    double botY = midY + high;//imgHeight/2;
	    
	    float oldx,oldy,x,y;
	    
		LLA first = gt.get(0);
		//gt.remove(0);
		
	    oldx  = (float) ( ((rightX-leftX)/longSpan)*(first.getLon()) + (rightX+leftX)/2.0 );
	    oldy =  (float)  ( ((topY-botY)/latSpan)*(first.getLat()) + (topY+botY)/2.0 );
		for (LLA lla : gt) {
			
		    x  =  (float) ( ((rightX-leftX)/longSpan)*(lla.getLon()) + (rightX+leftX)/2.0 );
		    y =   (float) ( ((topY-botY)/latSpan)*(lla.getLat()) + (topY+botY)/2.0 );
		    if((Math.abs(oldx-x) > width/2) || (Math.abs(oldy-y) > width/2))
		    {

		    }else
		    {
		    	canvas.drawLine(oldx, oldy, x, y, paint);
		    }
			oldx = x;
			oldy = y;
		}
	    /*
	    float pts[] = new float[1000];
	    int ptscnt = 0;
		for (LLA lla : gt) {
			
		    x  = ( ((rightX-leftX)/longSpan)*(lla.getLon()) + (rightX+leftX)/2.0 );
		    y =  ( ((topY-botY)/latSpan)*(lla.getLat()) + (topY+botY)/2.0 );
		    if((Math.abs(oldx-x) > width/2) || (Math.abs(oldy-y) > width/2))
		    {
		    	canvas.drawLines(pts,0,ptscnt,paint); 
		    	ptscnt =0;
		    }else
		    {
		    	pts[ptscnt+0] = (float)oldx;
		    	pts[ptscnt+1] = (float)oldy;
		    	pts[ptscnt+2] = (float)x;
		    	pts[ptscnt+3] = (float)y;
		    	ptscnt +=4;
		    }
			oldx = x;
			oldy = y;
		}
		canvas.drawLines(pts,0,ptscnt,paint); */
	}
	public static void drawMapSatellite(Canvas canvas, Bitmap satmap,String name, LLA lla,int cx, int cy, int width, int high,Paint paint) {
		
	    double longSpan = 360.0; // span of longitude
	    double latSpan = 180.0;
	        
	    int midX = (int) cx;//totWidth/2;
	    int midY = (int) cy;//totHeight/2;
	        
	    int leftX = midX - width;//imgWidth/2;
	    int rightX = midX + width;//imgWidth/2;
	        
	    int topY = midY - high;//imgHeight/2;
	    int botY = midY + high;//imgHeight/2;
	        
	    double x  = (int)( ((rightX-leftX)/longSpan)*(lla.getLon()) + (rightX+leftX)/2.0 );
	    double y =  (int)( ((topY-botY)/latSpan)*(lla.getLat()) + (topY+botY)/2.0 );
        //以x,y为中心绘制卫星图标
		//得到卫星图标的半径
		int sr = satmap.getWidth() / 2;	    
		canvas.drawBitmap(satmap,(float)(x - sr),(float) (y - sr),paint);

		String info = String.format(name);
		canvas.drawText(info, (float) (x), (float) (y-2*sr), paint);

	}	
	public static void drawPolarSatellite(Canvas canvas, Bitmap satmap,String name,LLA lla,int cx, int cy, int r,Paint paint) {


		//Azimuth [deg]: 237.38648192853677
		//Elevation [deg]: -77.56682949061964
		//Range [m]: 3.2812890918785054E7

		double elevation = lla.getLon();//-77.56682949061964;//satellite.getElevation();

		//double r2 = r * ((90.0f - elevation) / 90.0f);
		double[] elevationRange = new double[] {-90.0,90.0};
		double r2 = r *(1.0 - (elevation - elevationRange[0]) / (elevationRange[1] - elevationRange[0]));

		double azimuth = lla.getLat(); //237.38648192853677;//satellite.getAzimuth();
        
		double radian = degreeToRadian(azimuth);//360-azimuth + 90);
           
		double x = cx + Math.sin(radian) * r2;
		double y = cy - Math.cos(radian) * r2;

		int sr = satmap.getWidth() / 2;

		canvas.drawBitmap(satmap, (float) (x - sr), (float) (y - sr),paint);

		String info = String.format(name);
		canvas.drawText(info, (float) (x), (float) (y-2*sr), paint);

	}
	
	public static void drawPolarGround(Canvas canvas, List<LLA> gt,int cx, int cy, int r,Paint paint) {

		double[] elevationRange = new double[] {-90.0,90.0};
		double oldx,oldy;
		double x,y;
		double r2,radian;
		LLA first = gt.get(0);
		r2 = r *(1.0 - (first.getLon() - elevationRange[0]) / (elevationRange[1] - elevationRange[0]));

		radian = degreeToRadian(first.getLat());//360-azimuth + 90);
           
		oldx = cx + Math.sin(radian) * r2;
		oldy = cy - Math.cos(radian) * r2;		
		for (LLA lla : gt) {
		
			r2 = r *(1.0 - (lla.getLon() - elevationRange[0]) / (elevationRange[1] - elevationRange[0]));

			radian = degreeToRadian(lla.getLat());//360-azimuth + 90);
	           
			x = cx + Math.sin(radian) * r2;
			y = cy - Math.cos(radian) * r2;
			canvas.drawLine((float)oldx,(float)oldy,(float)x, (float)y, paint); 
			oldx = x;
			oldy = y;
		}
	}

}
