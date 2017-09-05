package satellite.tle.image;

import java.util.ArrayList;
import java.util.List;

import name.gano.astro.AstroConst;
import name.gano.astro.GeoFunctions;

import satellite.tle.utilities.LLA;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;

public class TLEDraw {
	
	
	
	public static double degreeToRadian(double degree) {
		return (degree * Math.PI) / 180.0d;
	}
    public static double GetAzimuth(double lon1, double lat1, double lon2,  
            double lat2) {  
        lat1 = degreeToRadian(lat1);  
        lat2 = degreeToRadian(lat2);  
        lon1 = degreeToRadian(lon1);  
        lon2 = degreeToRadian(lon2);  
        double azimuth = Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1)  
                * Math.cos(lat2) * Math.cos(lon2 - lon1);  
        azimuth = Math.sqrt(1 - azimuth * azimuth);  
        azimuth = Math.cos(lat2) * Math.sin(lon2 - lon1) / azimuth;  
        azimuth = Math.asin(azimuth) * 180 / Math.PI;  
        if (Double.isNaN(azimuth)) {  
            if (lon1 < lon2) {  
                azimuth = 90.0;  
            } else {  
                azimuth = 270.0;  
            }  
        }  
        return azimuth;  
    }	
	public static void drawMapGround(Canvas canvas, List<LLA> gt,int cx, int cy, int width, int high,Paint paint) {

		if(gt.size() > 1)
		{
			Double nanDbl =  Double.valueOf(Double.NaN);
			
		    float oldx,oldy,x,y;
		    
	
			LLA oldlla = gt.get(0);
		    oldx  = (float) ( oldlla.getLon()*width/180.0 + cx );
		    oldy =  (float) ( -oldlla.getLat()*high/90.0  + cy );
		    
		    
		    
	        float[] xyPts = new float[2*gt.size()];
	        int ptsCount = 0; // points to draw stored up (reset when discontinutiy is hit)
	        
	        // first point
	        xyPts[ptsCount] = oldx;
	        ptsCount++;
	        xyPts[ptsCount] = oldy;
	        ptsCount++;
	        LLA lla;
	        for(int j=1;j<gt.size();j++)
	        {
	            lla = gt.get(j);
	            //System.out.println(lla.getLon()+","+lla.getLat());
	            
	    	    x  = (float) ( lla.getLon()*width/180.0 + cx );
	    	    y =  (float) ( -lla.getLat()*high/90.0  + cy );
	            //check to see if Longitude flipped sign But not near origin
	            //if( (LLA[1] > 0 && LLA_old[1] > 0) || (LLA[1] < 0 && LLA_old[1] < 0) || Math.abs(LLA[1]) < 1.0  )
	    	    
	            if ( !(nanDbl.equals(oldlla.getLon()) || nanDbl.equals(lla.getLon()))) // make sure they are not NAN (not in time)
	            {
	
	                // line segment is normal (doesn't span map disconnect)
	                if (Math.abs(lla.getLon() - oldlla.getLon()) < 185.0)//4.0*180.0/Math.PI)
	                {
	                    // old slow way:
	                    //g2.drawLine(xy_old[0], xy_old[1], xy[0], xy[1]); // draw a line across the map
	                    
	                    // add points to the array (after NaN check)
	                	
	                    xyPts[ptsCount] = x;
	                    ptsCount++;
	                    xyPts[ptsCount] = y;
	                    ptsCount++;
	                
	                }
	                else
	                {
	                	//System.out.println(lla.getLon()+","+lla.getLat()+","+oldlla.getLon()+","+oldlla.getLat());
	                    // draw this line segment next time, jump from side to side
	                    double newLat = linearInterpDiscontLat(oldlla.getLat(),oldlla.getLon(), lla.getLat(),  lla.getLon());
	
	                    // get xy points for both the old and new side (positive and negative long)
	            	    
	        		    float posx  =  (float) (width + cx );//(float) ( ((rightX-leftX)/longSpan)*(180.0) + (rightX+leftX)/2.0 );
	        		    float posy =   (float) (-newLat*high/90.0  + cy );//(float) ( ((topY-botY)/latSpan)*(newLat) + (topY+botY)/2.0 );
	        		    
	        		    float negx  =  (float) (-width + cx );//(float) ( ((rightX-leftX)/longSpan)*(-180.0) + (rightX+leftX)/2.0 );
	        		    float negy =   posy;//(float) ( ((topY-botY)/latSpan)*(newLat) + (topY+botY)/2.0 );
	                    // draw 2 lines - one for each side of the date line
	                    if (oldlla.getLon() > 0) // then the old one is on the positive side
	                    {
	                        // old slow way
	                        //g2.drawLine(xy_old[0], xy_old[1], xyMid_pos[0], xyMid_pos[1]);
	                        //g2.drawLine(xy[0], xy[1], xyMid_neg[0], xyMid_neg[1]);
	                        
	                        // new way
	                        // add final point to positive side
	                        xyPts[ptsCount] = posx;//xyMid_pos[0];
	                        ptsCount++;
	                        xyPts[ptsCount] = posy;//xyMid_pos[1];
	                        ptsCount++;
	                        // draw the polyline
	                        //canvas.drawLines(xyPts,paint);//g2.drawPolyline(xPts, yPts, ptsCount);
	                        //Path path = new Path() ;
	                        //path.moveTo(xyPts[0], xyPts[1]);
	                        for(int k =1 ; k <  ptsCount/2; k++)
	                        {
	                        	canvas.drawLine(xyPts[2*(k-1)], xyPts[2*(k-1)+1], xyPts[2*k], xyPts[2*k+1], paint);;//path.lineTo(xyPts[2*k], xyPts[2*k+1]);
	                        }
	                        //canvas.drawPath(path, paint);
	                        // clear the arrays (just reset the counter)
	                        ptsCount = 0;
	                        // add the new points to the cleared array
	                        xyPts[ptsCount] = negx;//xyMid_neg[0];
	                        ptsCount++;
	                        xyPts[ptsCount] = negy;//xyMid_neg[1];
	                        ptsCount++;
	                        xyPts[ptsCount] = x;//xy[0];
	                        ptsCount++;
	                        xyPts[ptsCount] = y;//xy[1];
	                        ptsCount++;
	                        
	                    }
	                    else // the new one is on the positive side
	                    {
	                        // old slow way
	                        //g2.drawLine(xy[0], xy[1], xyMid_pos[0], xyMid_pos[1]);
	                        //g2.drawLine(xy_old[0], xy_old[1], xyMid_neg[0], xyMid_neg[1]);
	                        
	                         // new way
	                        // add final point to neg side
	                        xyPts[ptsCount] = negx;//xyMid_neg[0];
	                        ptsCount++;
	                        xyPts[ptsCount] = negy;//xyMid_neg[1];
	                        ptsCount++;
	                        // draw the polyline
	                        //canvas.drawLines(xyPts,paint);//g2.drawPolyline(xPts, yPts, ptsCount);
	                        //Path path = new Path() ;
	                        //path.moveTo(xyPts[0], xyPts[1]);
	                        for(int k =1 ; k <  ptsCount/2; k++)
	                        {
	                        	canvas.drawLine(xyPts[2*(k-1)], xyPts[2*(k-1)+1], xyPts[2*k], xyPts[2*k+1], paint);//path.lineTo(xyPts[2*k], xyPts[2*k+1]);
	                        }
	                        //canvas.drawPath(path, paint);
	                        // clear the arrays (just reset the counter)
	                        ptsCount = 0;
	                        // add the new points to the cleared array
	                        xyPts[ptsCount] = posx;//xyMid_pos[0];
	                        ptsCount++;
	                        xyPts[ptsCount] = posy;//xyMid_pos[1];
	                        ptsCount++;
	                        xyPts[ptsCount] = x;//xy[0];
	                        ptsCount++;
	                        xyPts[ptsCount] = y;//xy[1];
	                        ptsCount++;
	                    }
	
	                } // jump in footprint
	            } // NaN check
	            
	            
	            oldx = x;
	            oldy = y;
	            oldlla = lla;
	            
	        } // lead track drawing
	         
	        // draw remainder of lead segment
	        for(int k =1 ; k <  ptsCount/2; k++)
	        {
	        	canvas.drawLine(xyPts[2*(k-1)], xyPts[2*(k-1)+1], xyPts[2*k], xyPts[2*k+1], paint);
	        }
		}
	}
	
	public static void drawFootprint(Canvas canvas, List<LLA> gt,LLA sat,int cx, int cy, int width, int high,Paint paint) {

		if(gt.size() > 1)
		{
			List<LLA> footprint = new ArrayList<LLA>();
			Double nanDbl =  Double.valueOf(Double.NaN);
			
		    float oldx,oldy,x,y,x0,y0;
		    
	
			LLA oldlla = gt.get(0);
		    oldx  = (float) ( oldlla.getLon()*width/180.0 + cx );
		    oldy =  (float) ( -oldlla.getLat()*high/90.0  + cy );
		    
		    
		    
	        float[] xyPts = new float[2*gt.size()+2];
	        int ptsCount = 0; // points to draw stored up (reset when discontinutiy is hit)
	        
	        // first point
	        xyPts[ptsCount] = oldx;
	        ptsCount++;
	        xyPts[ptsCount] = oldy;
	        ptsCount++;
	        LLA lla,lla0;
	        lla0 = oldlla;
	        footprint.add(new LLA(oldx,oldy,0));
	        x0 = oldx;
	        y0 = oldy;
	        int disconnectCount = 0;
	        int disconnectIndex1 = 0; // first index of disconnect
	        int disconnectIndex2 = 0; // second index of disconnect
	        float[] disconnect1pos = new float[2]; // corrected x,y point at each discontinutiy
	        float[] disconnect1neg = new float[2];
	        float[] disconnect2pos = new float[2];
	        float[] disconnect2neg = new float[2];
	        
	        for(int j=1;j<gt.size();j++)
	        {
	            lla = gt.get(j);
	            //System.out.println(lla.getLon()+","+lla.getLat());
	            
	    	    x  = (float) ( lla.getLon()*width/180.0 + cx );
	    	    y =  (float) ( -lla.getLat()*high/90.0  + cy );
	            //check to see if Longitude flipped sign But not near origin
	            //if( (LLA[1] > 0 && LLA_old[1] > 0) || (LLA[1] < 0 && LLA_old[1] < 0) || Math.abs(LLA[1]) < 1.0  )
	    	    
	            if ( !(nanDbl.equals(oldlla.getLon()) || nanDbl.equals(lla.getLon()))) // make sure they are not NAN (not in time)
	            {
	
	                // line segment is normal (doesn't span map disconnect)
	                if (Math.abs(lla.getLon() - oldlla.getLon()) < 185.0)//4.0*180.0/Math.PI)
	                {
	                    // old slow way:
	                    //g2.drawLine(xy_old[0], xy_old[1], xy[0], xy[1]); // draw a line across the map
	                    
	                    // add points to the array (after NaN check)
	                	
	                    xyPts[ptsCount] = x;
	                    ptsCount++;
	                    xyPts[ptsCount] = y;
	                    ptsCount++;
	                
	                }
	                else
	                {
	                	//System.out.println(lla.getLon()+","+lla.getLat()+","+oldlla.getLon()+","+oldlla.getLat());
	                    // draw this line segment next time, jump from side to side
	                    double newLat = linearInterpDiscontLat(oldlla.getLat(),oldlla.getLon(), lla.getLat(),  lla.getLon());
	
	                    // get xy points for both the old and new side (positive and negative long)
	            	    
	        		    float posx  =  (float) (width + cx );//(float) ( ((rightX-leftX)/longSpan)*(180.0) + (rightX+leftX)/2.0 );
	        		    float posy =   (float) (-newLat*high/90.0  + cy );//(float) ( ((topY-botY)/latSpan)*(newLat) + (topY+botY)/2.0 );
	        		    
	        		    float negx  =  (float) (-width + cx );//(float) ( ((rightX-leftX)/longSpan)*(-180.0) + (rightX+leftX)/2.0 );
	        		    float negy =   posy;//(float) ( ((topY-botY)/latSpan)*(newLat) + (topY+botY)/2.0 );
	                    // draw 2 lines - one for each side of the date line
	                    if (oldlla.getLon() > 0) // then the old one is on the positive side
	                    {
	                        // old slow way
	                        //g2.drawLine(xy_old[0], xy_old[1], xyMid_pos[0], xyMid_pos[1]);
	                        //g2.drawLine(xy[0], xy[1], xyMid_neg[0], xyMid_neg[1]);
	                        
	                        // new way
	                        // add final point to positive side
	                        xyPts[ptsCount] = posx;//xyMid_pos[0];
	                        ptsCount++;
	                        xyPts[ptsCount] = posy;//xyMid_pos[1];
	                        ptsCount++;
	                        // draw the polyline
	                        //canvas.drawLines(xyPts,paint);//g2.drawPolyline(xPts, yPts, ptsCount);
	                        //Path path = new Path() ;
	                        //path.moveTo(xyPts[0], xyPts[1]);
	                        for(int k =1 ; k <  ptsCount/2; k++)
	                        {
	                        	//canvas.drawLine(xyPts[2*(k-1)], xyPts[2*(k-1)+1], xyPts[2*k], xyPts[2*k+1], paint);;//path.lineTo(xyPts[2*k], xyPts[2*k+1]);
	                        }
	                        //canvas.drawPath(path, paint);
	                        // clear the arrays (just reset the counter)
	                        ptsCount = 0;
	                        // add the new points to the cleared array
	                        xyPts[ptsCount] = negx;//xyMid_neg[0];
	                        ptsCount++;
	                        xyPts[ptsCount] = negy;//xyMid_neg[1];
	                        ptsCount++;
	                        xyPts[ptsCount] = x;//xy[0];
	                        ptsCount++;
	                        xyPts[ptsCount] = y;//xy[1];
	                        ptsCount++;
	                        
	                    }
	                    else // the new one is on the positive side
	                    {
	                        // old slow way
	                        //g2.drawLine(xy[0], xy[1], xyMid_pos[0], xyMid_pos[1]);
	                        //g2.drawLine(xy_old[0], xy_old[1], xyMid_neg[0], xyMid_neg[1]);
	                        
	                         // new way
	                        // add final point to neg side
	                        xyPts[ptsCount] = negx;//xyMid_neg[0];
	                        ptsCount++;
	                        xyPts[ptsCount] = negy;//xyMid_neg[1];
	                        ptsCount++;
	                        // draw the polyline
	                        //canvas.drawLines(xyPts,paint);//g2.drawPolyline(xPts, yPts, ptsCount);
	                        //Path path = new Path() ;
	                        //path.moveTo(xyPts[0], xyPts[1]);
	                        for(int k =1 ; k <  ptsCount/2; k++)
	                        {
	                        	//canvas.drawLine(xyPts[2*(k-1)], xyPts[2*(k-1)+1], xyPts[2*k], xyPts[2*k+1], paint);//path.lineTo(xyPts[2*k], xyPts[2*k+1]);
	                        }
	                        //canvas.drawPath(path, paint);
	                        // clear the arrays (just reset the counter)
	                        ptsCount = 0;
	                        // add the new points to the cleared array
	                        xyPts[ptsCount] = posx;//xyMid_pos[0];
	                        ptsCount++;
	                        xyPts[ptsCount] = posy;//xyMid_pos[1];
	                        ptsCount++;
	                        xyPts[ptsCount] = x;//xy[0];
	                        ptsCount++;
	                        xyPts[ptsCount] = y;//xy[1];
	                        ptsCount++;
	                    }
	                    
	                    disconnectCount++;
	                    
	                    if(disconnectCount == 1)
	                    {
	                        disconnectIndex1 = j;
	                        //System.out.println("Disconnect 1 found:"+disconnectIndex1);
	                        disconnect1pos[0] = posx; // save this point
	                        disconnect1pos[1] = posy;
	                        disconnect1neg[0] = negx;
	                        disconnect1neg[1] = negy;
	                    }
	                    else if(disconnectCount == 2)
	                    {
	                        disconnectIndex2 = j;
	                        //System.out.println("Disconnect 2 found:"+disconnectIndex2);
	                        disconnect2pos[0] = posx; // save this point
	                        disconnect2pos[1] = posy;
	                        disconnect2neg[0] = negx;
	                        disconnect2neg[1] = negy;
	                    }
	                } // jump in footprint
	            } // NaN check
	            
	            
	            oldx = x;
	            oldy = y;
	            oldlla = lla;
	            footprint.add(new LLA(oldx,oldy,0));
	        } // lead track drawing
	        
	        if (Math.abs(oldlla.getLon() - lla0.getLon()) < 185.0)//4.0*180.0/Math.PI)
            {
	        	xyPts[ptsCount] = x0;//xyMid_pos[0];
                ptsCount++;
                xyPts[ptsCount] = y0;//xyMid_pos[1];
                ptsCount++;
		        for(int k =1 ; k <  ptsCount/2; k++)
	            {
	            	//canvas.drawLine(xyPts[2*(k-1)], xyPts[2*(k-1)+1], xyPts[2*k], xyPts[2*k+1], paint);//path.lineTo(xyPts[2*k], xyPts[2*k+1]);
	            }
            }
	        // draw remainder of lead segment
	        //for(int k =1 ; k <  ptsCount/2; k++)
	        //{
	        //	canvas.drawLine(xyPts[2*(k-1)], xyPts[2*(k-1)+1], xyPts[2*k], xyPts[2*k+1], paint);
	        //}
	        //System.out.println("disconnectCount:"+disconnectCount);
	        paint.setAlpha(0x40);
	        if(disconnectCount == 0)
            {
                // no disconnects fill like normal
	        	fill(footprint,canvas,paint);  
	        	
            }else if(disconnectCount == 1)
            {
                // okay this is at a pole, add in edges and fill in
                // figure out N or S based on sat position ( lat > 0 or < 0)
                boolean northPoleVisible = ( sat.getLat() > 0);
                
                int[] ptPos = new int[2];
                int[] ptNeg = new int[2];
                
                //Polygon fullFootPrint = new Polygon();
                List<LLA> fullFootPrint = new ArrayList<LLA>();
                if(northPoleVisible)
                {
                    
                    //ptPos = findXYfromLL(90.0, 180.0, w, h, imageWidth, imageHeight);
                    //ptNeg = findXYfromLL(90.0, -180.0, w, h, imageWidth, imageHeight);
                    
                    float ptPosx  = (float) ( 180*width/180.0 + cx );
                    float ptPosy =  (float) ( -90*high/90.0  + cy );
                    float ptNegx  = (float) ( -180*width/180.0 + cx );
                    float ptNegy =  (float) ( -90*high/90.0  + cy );
                    // counter clockwise - add points
                    for(int k=0; k<disconnectIndex1;k++)
                    {
                    	//fullFootPrint.addPoint(footprint.xpoints[k],footprint.ypoints[k]);
                        fullFootPrint.add(footprint.get(k));
                    }
                    fullFootPrint.add(new LLA(disconnect1pos[0],disconnect1pos[1],0));
                    fullFootPrint.add(new LLA(ptPosx,ptPosy,0));
                    fullFootPrint.add(new LLA(ptNegx,ptNegy,0));
                    fullFootPrint.add(new LLA(disconnect1neg[0],disconnect1neg[1],0));
                    for(int k=disconnectIndex1; k<footprint.size();k++)
                    {
                    	fullFootPrint.add(footprint.get(k));
                    }
                    
                }
                else
                {
                    // counter clockwise - add points
                    //ptPos = findXYfromLL(-90.0, 180.0, w, h, imageWidth, imageHeight);
                    //ptNeg = findXYfromLL(-90.0, -180.0, w, h, imageWidth, imageHeight);
                    float ptPosx  = (float) ( 180*width/180.0 + cx );
                    float ptPosy =  (float) ( 90*high/90.0  + cy );
                    float ptNegx  = (float) ( -180*width/180.0 + cx );
                    float ptNegy =  (float) ( 90*high/90.0  + cy );
                    
                    for(int k=0; k<disconnectIndex1;k++)
                    {
                        //fullFootPrint.addPoint(footprint.xpoints[k],footprint.ypoints[k]);
                        fullFootPrint.add(footprint.get(k));
                    }
                    
                    fullFootPrint.add(new LLA(disconnect1neg[0],disconnect1neg[1],0));
                    fullFootPrint.add(new LLA(ptNegx,ptNegy,0));
                    fullFootPrint.add(new LLA(ptPosx,ptPosy,0));
                    fullFootPrint.add(new LLA(disconnect1pos[0],disconnect1pos[1],0));
                    
                    for(int k=disconnectIndex1; k<footprint.size();k++)
                    {
                    	fullFootPrint.add(footprint.get(k));
                    }
                    
                }// south pole visible
                
                // fill full print
                //g2.fill(fullFootPrint);
                fill(fullFootPrint,canvas,paint); 
                
            }
            else if(disconnectCount == 2)
            {
                // this is the case when a sat spans the international dateline
                // make two polygons to fill
                
                // polygon starts left of center and goes counter clockwise
                
                // check to see if this is a standard case (discontinuity
                // 1 is vertically lower than discon #2) (y is backwards in java)
                if( disconnect1neg[1]  >= disconnect2neg[1])
                {
                    // STANDARD drawing case for 2 discontinuities
                    
                    
                    
                    // new polygon - part on left side
                    //Polygon footprintPartLeft = new Polygon();
                    List<LLA> footprintPartLeft = new ArrayList<LLA>();
                    // get both sides of discontinuity 1
                    footprintPartLeft.add( new LLA(disconnect1neg[0], disconnect1neg[1],0)); // first point at left edge
                    for(int k=disconnectIndex1;k<disconnectIndex2;k++)
                    {
                        
                        footprintPartLeft.add(footprint.get(k));
                    }
                    footprintPartLeft.add(new LLA( disconnect2neg[0], disconnect2neg[1],0));
                    
                    fill(footprintPartLeft,canvas,paint);
                    
                    // now create Right side of the polygon
                    //Polygon footprintPartRight = new Polygon();
                    List<LLA> footprintPartRight = new ArrayList<LLA>();
                    // fill in first part
                    for(int k=0;k<disconnectIndex1;k++)
                    {
                        
                        footprintPartRight.add(footprint.get(k));
                    }
                    // add disconnect points
                    footprintPartRight.add(new LLA( disconnect1pos[0], disconnect1pos[1],0));
                    footprintPartRight.add(new LLA( disconnect2pos[0], disconnect2pos[1],0));
                    // fill in last part
                    for(int k=disconnectIndex2;k<footprint.size();k++)
                    {
                        
                    	footprintPartRight.add(footprint.get(k));
                    }
                    fill(footprintPartRight,canvas,paint);
                }// standard 2-discont drawing
                else  // non standard discont 1 is above discont 2
                {
                    // new polygon - part on left side
                    //Polygon footprintPartLeft = new Polygon();
                    List<LLA> footprintPartLeft = new ArrayList<LLA>();
                    for(int k=0;k<disconnectIndex1;k++)
                    {
                        
                    	footprintPartLeft.add(footprint.get(k));
                    }
                    footprintPartLeft.add( new LLA(disconnect1neg[0], disconnect1neg[1],0)); // left edge top
                    footprintPartLeft.add( new LLA(disconnect2neg[0], disconnect2neg[1],0)); // left edge bottom
                    for(int k=disconnectIndex2;k<footprint.size();k++)
                    {
                        
                    	footprintPartLeft.add(footprint.get(k));
                    }
                    fill(footprintPartLeft,canvas,paint);
                    
                    // Right part of foot print
                    //Polygon footprintPartRight = new Polygon();
                    List<LLA> footprintPartRight = new ArrayList<LLA>();
                    footprintPartRight.add( new LLA(disconnect1pos[0], disconnect1pos[1],0));
                    for(int k=disconnectIndex1;k<disconnectIndex2;k++)
                    {
                        
                        footprintPartRight.add(footprint.get(k));
                    }
                    footprintPartRight.add( new LLA (disconnect2pos[0], disconnect2pos[1],0));
                    fill(footprintPartRight,canvas,paint);
                    
                    
                }// non standard 2-discont drawing
            }
	        paint.setAlpha(0xff);
		}
	}
	
	public static void fill(List<LLA> footprint,Canvas canvas, Paint paint)
	{
		Path path = new Path(); 
    	path.moveTo((float)footprint.get(0).getLat(), (float)footprint.get(0).getLon()); 
    	for(int k =1 ; k < footprint.size(); k++)
        {
        	path.lineTo((float)footprint.get(k).getLat(), (float)footprint.get(k).getLon());
        }
    	path.lineTo((float)footprint.get(0).getLat(), (float)footprint.get(0).getLon());
    	canvas.drawPath(path, paint);
	}
	public static float[] getxyfromLL(LLA lla,int cx, int cy, int width, int high)
	{
		float[] xy = new float[2];
		//double longSpan = 360.0; // span of longitude
	    //double latSpan = 180.0;
	        
	    int midX = (int) cx;//totWidth/2;
	    int midY = (int) cy;//totHeight/2;
	        
	    double leftX = midX - width;//imgWidth/2;
	    double rightX = midX + width;//imgWidth/2;
	        
	    double topY = midY - high;//imgHeight/2;
	    double botY = midY + high;//imgHeight/2;
	    
	    //float oldx,oldy,x,y;

		
	    xy[0]  = (float) ( ((rightX-leftX)/360.0)*(lla.getLon()) + (rightX+leftX)/2.0 );
	    xy[1] =  (float)  ( ((topY-botY)/180)*(lla.getLat()) + (topY+botY)/2.0 );
	    
		return xy;
	}	
	public static void drawMapGroundOLD(Canvas canvas, List<LLA> gt,int cx, int cy, int width, int high,Paint paint) {

	    
	    float oldx,oldy,x,y;
	    
	    
		LLA first = gt.get(0);
		
	    oldx  = (float) ( first.getLon()*width/180.0 + cx );
	    oldy =  (float) ( -first.getLat()*high/90.0  + cy );
	    
		for (LLA lla : gt) {
			
		    x  =  (float) ( lla.getLon()*width/180.0 + cx );;
		    y =   (float) ( -lla.getLat()*high/90.0  + cy );
		    
		    if((Math.abs(oldx-x) > width/2) || (Math.abs(oldy-y) > width/2))
		    {

		    }else
		    {
		    	canvas.drawLine(oldx, oldy, x, y, paint);
		    }
			oldx = x;
			oldy = y;
		}
	}	
	public static void drawMapSatellite(Canvas canvas, String name, LLA lla,int cx, int cy, int width, int high,Paint paint,boolean cur,int DisSatName) {
		
	    double longSpan = 360.0; // span of longitude
	    double latSpan = 180.0;
	        
	    int midX = (int) cx;//totWidth/2;
	    int midY = (int) cy;//totHeight/2;
	        
	    int leftX = midX - width;//imgWidth/2;
	    int rightX = midX + width;//imgWidth/2;
	        
	    int topY = midY - high;//imgHeight/2;
	    int botY = midY + high;//imgHeight/2;
	        
	    float x  = (int)( ((rightX-leftX)/longSpan)*(lla.getLon()) + (rightX+leftX)/2.0 );
	    float y =  (int)( ((topY-botY)/latSpan)*(lla.getLat()) + (topY+botY)/2.0 );
        //以x,y为中心绘制卫星图标
		//得到卫星图标的半径
		int sr = 6;//satmap.getWidth() / 2;	
		if(cur)
		{
			sr = 2*sr;

		}
		//canvas.drawBitmap(satmap,(float)(x - sr),(float) (y - sr),paint);
		canvas.drawCircle((float)x, (float)y, sr, paint);
		if(cur || DisSatName == 0)
		{
			String info = String.format(name);
			canvas.drawText(info, (float) (x), (float) (y-1*sr-2), paint);
		}
		

	}	
	public static void drawPolarSatellite(Canvas canvas, String name,LLA lla,int cx, int cy, int r,Paint paint,boolean cur,int DisSatName) {


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

		int sr = 6;//satmap.getWidth() / 2;
		//System.out.println(elevation+","+azimuth+","+cx+"," + cy+","+x+","+y+","+r2);
		//canvas.drawBitmap(satmap, (float) (x - sr), (float) (y - sr),paint);
		if(cur || DisSatName == 0)
		{
			sr = 2*sr;
			String info = String.format(name);
			canvas.drawText(info, (float) (x), (float) (y-2*sr), paint);
		}
		canvas.drawCircle((float)x, (float)y, sr, paint);


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
	public static void drawPolarBackdrop(Canvas canvas, int cx, int cy, int r,Paint paint) {
		
		paint.setARGB(255,0,0,119);
		paint.setAntiAlias(true);
		canvas.drawCircle((float)cx, (float)cy, (float)r, paint);
		
		DashPathEffect pathEffect = new DashPathEffect(new float[] {8, 8}, 1);
		paint.setPathEffect(pathEffect);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(1); 
		paint.setColor(Color.WHITE);
		canvas.drawCircle((float)cx, (float)cy, (float)r, paint);
		canvas.drawCircle((float)cx, (float)cy, (float)r/4, paint);
		canvas.drawCircle((float)cx, (float)cy, (float)r/2, paint);
		canvas.drawCircle((float)cx, (float)cy, (float)r*0.75f, paint);
		canvas.drawLine(cx-r, cy, cx+r, cy, paint);
		canvas.drawLine(cx, cy-r, cx, cy+r, paint);
		float rcos30 = (float)Math.cos(Math.PI/6.0)*r;
		float rsin30 = (float)Math.sin(Math.PI/6.0)*r;
		canvas.drawLine(cx-rsin30, cy-rcos30, cx+rsin30, cy+rcos30, paint);
		canvas.drawLine(cx-rcos30, cy-rsin30, cx+rcos30, cy+rsin30, paint);
		canvas.drawLine(cx-rsin30, cy+rcos30, cx+rsin30, cy-rcos30, paint);
		canvas.drawLine(cx-rcos30, cy+rsin30, cx+rcos30, cy-rsin30, paint);		
		paint.setStyle(Paint.Style.FILL);
		paint.setPathEffect(null);
		canvas.drawText("0", cx, cy-r+25, paint);
		canvas.drawText("90", cx+r-25, cy, paint);
		canvas.drawText("180", cx, cy+r, paint);
		canvas.drawText("270", cx-r+30, cy, paint);
		canvas.drawText("30",cx+rsin30+10, cy-rcos30, paint);
		canvas.drawText("60",cx+rcos30+10, cy-rsin30, paint);
		canvas.drawText("120",cx+rcos30+10, cy+rsin30, paint);
		canvas.drawText("150",cx+rsin30+10, cy+rcos30, paint);
		canvas.drawText("210",cx-rsin30+10, cy+rcos30, paint);
		canvas.drawText("240",cx-rcos30+10, cy+rsin30, paint);
		canvas.drawText("300",cx-rcos30+10, cy-rsin30, paint);
		canvas.drawText("330",cx-rsin30+10, cy-rcos30, paint);
		
	}	
	public static void drawPolarGroundrs(Canvas canvas, List<LLA> gt,LLA raisella,int cx, int cy, int r,Paint paint) {

		double[] elevationRange = new double[] {-90.0,90.0};
		double oldx,oldy;
		double x,y;
		double r2,radian;
		int redp = paint.getColor();
		LLA first = gt.get(0);
		r2 = r *(1.0 - (first.getLon() - elevationRange[0]) / (elevationRange[1] - elevationRange[0]));

		radian = degreeToRadian(first.getLat());//360-azimuth + 90);
           
		oldx = cx + Math.sin(radian) * r2;
		oldy = cy - Math.cos(radian) * r2;
		for(int i =1; i<gt.size(); i++){
		//for (LLA lla : gt) {
			LLA lla = gt.get(i);
			r2 = r *(1.0 - (lla.getLon() - elevationRange[0]) / (elevationRange[1] - elevationRange[0]));

			radian = degreeToRadian(lla.getLat());//360-azimuth + 90);
	           
			x = cx + Math.sin(radian) * r2;
			y = cy - Math.cos(radian) * r2;
			if(lla.getLon() < raisella.getLon())
			{
				paint.setColor(Color.GRAY);
			}else
			{
				paint.setColor(Color.RED);
			}
			canvas.drawLine((float)oldx,(float)oldy,(float)x, (float)y, paint); 
			oldx = x;
			oldy = y;
		}
		paint.setColor(Color.RED);
	}
	/*
	// ==============================================================================================
	///  FUNCTION TO DRAW A FOOT PRINT ==============================================================
	     // alpha = 0-1 amount of transparency in footprint (~0.2f)
	    // Assumes a spherical Earth? - maybe not see ecef2lla()
	    private void drawFootPrint(Graphics2D g2, double lat, double lon, double alt,int cx, int cy, int imageWidth, int imageHeight, boolean fillFootPrint, Color outlineColor, Color FillColor, float alpha, int numPtsFootPrint)
	    {
	        
	        // vars: ===============================
	        // draw footprints of satellites
	        Polygon footprint;
	    	//List<LLA> footprint = new ArrayList<LLA>();
	        // varaibles for fixing disconnect around international date line (for filled footprints)
	        int disconnectCount = 0; // how many disconnect points, 0=good, 1= far away sat, sees north/south pole, 2= spans date line
	        int disconnectIndex1 = 0; // first index of disconnect
	        int disconnectIndex2 = 0; // second index of disconnect
	        int[] disconnect1pos = new int[2]; // corrected x,y point at each discontinutiy
	        int[] disconnect1neg = new int[2];
	        int[] disconnect2pos = new int[2];
	        int[] disconnect2neg = new int[2];
	        
	        // size
	        // save last width/height
	        int w = imageWidth;
	        int h = imageHeight;
	        
	        //========================================
	        
	        //disconnectCount = 0; // reset disconnect count
	        
	        // set color -- outline
	        g2.setPaint( outlineColor  );
	        
	        // Polygon shape -- for filling?
	        footprint = new Polygon();

	        // correction to convert latitude from geographic to geo-centric because foot print is created and rotated about center so it uses geocentric rotations
	        // for conversion see: http://en.wikipedia.org/wiki/Latitude
	        lat = Math.atan( Math.pow(AstroConst.R_Earth_minor/AstroConst.R_Earth_major,2.0) * Math.tan(lat) );

	        // assumes a spherical Earth - cone half angle from the center of the earth
	        // an improvement could find the radius of the earth at the given latitude
	        //double lambda0 = Math.acos(AstroConst.R_Earth/(AstroConst.R_Earth+alt));
	        // improvement - 31 March 2009 - SEG (though still assumes that the visible area is a perfect circle, but that is very close
	        // improves accuracy by about 0.02 deg at extremes of a 51.6 deg inclination LEO 
	        double earthRadiusAtLat = AstroConst.R_Earth_major - (AstroConst.R_Earth_major-AstroConst.R_Earth_minor)*Math.sin(lat);
	        double lambda0 = Math.acos(earthRadiusAtLat/(earthRadiusAtLat+alt));

	        
	        double beta = (90*Math.PI/180.0-lat); // latitude center (pitch)
	        double gamma = -lon+180.0*Math.PI/180.0; // longitude (yaw)
	        
	        // rotation matrix - 20-March 2009 SEG -- may want to convert LLA from geographic to geocentric -- See correction Above
	        // for conversion see: http://en.wikipedia.org/wiki/Latitude
	        double[][] M = new double[][] {{Math.cos(beta)*Math.cos(gamma), Math.sin(gamma), -Math.sin(beta)*Math.cos(gamma)},
	        {-Math.cos(beta)*Math.sin(gamma),Math.cos(gamma), Math.sin(beta)*Math.sin(gamma)},
	        {Math.sin(beta), 0.0, Math.cos(beta)}};
	        double theta = 0+Math.PI/2.0; // with extra offset of pi/2 so circle starts left of center going counter clockwise
	        double phi = lambda0;
	        
	        // position - on the surface of the Earth (spherical approx) - depending on how LLA is calculated geodetic or geographic
	        // uses the parametric equation of a sphere, around( varies by theta) the bottom of the sphere with the radius of the earth up an angle phi 
	        // maybe should use mean radius here instead of equatorial?? - doesn't make much difference in the pixel noise
	        double[] pos = new double[3];
	        pos[0] = AstroConst.R_Earth*Math.cos(theta)*Math.sin(phi);
	        pos[1] = AstroConst.R_Earth*Math.sin(theta)*Math.sin(phi);
	        pos[2] = AstroConst.R_Earth*Math.cos(phi);
	        
	        // rotate to center around satellite sub point
	        pos = mult(M,pos);
	        
	        // calculate Lat Long of point (first time save it)
	        double[] llaOld = GeoFunctions.ecef2lla_Fast(pos);
	        //llaOld[1] = llaOld[1] - 90.0*Math.PI/180.0;
	        double[] lla = new double[3]; // prepare array
	        
	        // copy of orginal point
	        double[] lla0 = new double[3];
	        int[] xy0 = new int[2];
	        lla0[0] = llaOld[0];
	        lla0[1] = llaOld[1];
	        lla0[2] = llaOld[2];
	        
	        // point
	        int[] xy;
	        
	        // original point
	        int[] xy_old = findXYfromLL(lla0[0]*180.0/Math.PI, lla0[1]*180.0/Math.PI, w, h, imageWidth, imageHeight,1,cx,cy);
	        xy0[0] = xy_old[0];
	        xy0[1] = xy_old[1];
	        
	        // add to polygon
	        if( fillFootPrint )
	        {
	            footprint.addPoint(xy_old[0],xy_old[1]);
	        }
	        
	        // footprint parameters
	        double dt = 2.0*Math.PI/(numPtsFootPrint-1.0);
	        
	        // faster performance to draw allpoints at once useing drawPolyLine
	        int[] xPts = new int[numPtsFootPrint+1];
	        int[] yPts = new int[numPtsFootPrint+1];
	        int ptsCount = 0; // points to draw stored up (reset when discontinutiy is hit)
	        // first point
	        xPts[ptsCount] = xy_old[0];
	        yPts[ptsCount] = xy_old[1];
	        ptsCount++;
	        
	        for(int j=1;j<numPtsFootPrint;j++)
	        {
	            theta = j*dt+Math.PI/2.0; // +Math.PI/2.0 // offset so it starts at the side
	            //phi = lambda0;
	            
	            // find position - unrotated about north pole
	            pos[0] = AstroConst.R_Earth*Math.cos(theta)*Math.sin(phi);
	            pos[1] = AstroConst.R_Earth*Math.sin(theta)*Math.sin(phi);
	            pos[2] = AstroConst.R_Earth*Math.cos(phi);
	            
	            // rotate to center around satellite sub point
	            pos = mult(M,pos);
	            
	            // find lla
	            lla = GeoFunctions.ecef2lla_Fast(pos);
	            //lla[1] = lla[1]-90.0*Math.PI/180.0;
	            //System.out.println("ll=" +lla[0]*180.0/Math.PI + "," + (lla[1]*180.0/Math.PI));
	            
	            xy = findXYfromLL(lla[0]*180.0/Math.PI, lla[1]*180.0/Math.PI, w, h, imageWidth, imageHeight,1,cx,cy);
	            
	            // draw line (only if not accross the screen)
	            if( Math.abs(lla[1] - llaOld[1]) < 4.0)
	            {
	                // old slow way
	                //g2.drawLine(xy_old[0],xy_old[1],xy[0],xy[1]);
	                
	                // add points to the array (after NaN check)
	                xPts[ptsCount] = xy[0];
	                yPts[ptsCount] = xy[1];
	                ptsCount++;
	                
	                //System.out.println("" +lla[0] + " " + lla[1]);
	            }
	            else
	            {
	                // draw line in correctly ======================
	                double newLat = linearInterpDiscontLat(llaOld[0], llaOld[1], lla[0], lla[1]);
	                
	                // get xy points for both the old and new side (positive and negative long)
	                int[] xyMid_pos = findXYfromLL(newLat*180.0/Math.PI, 180.0, w, h, imageWidth, imageHeight,1,cx,cy);
	                int[] xyMid_neg = findXYfromLL(newLat*180.0/Math.PI, -180.0, w, h, imageWidth, imageHeight,1,cx,cy);
	                
	                // draw 2 lines - one for each side of the date line
	                if(llaOld[1] > 0) // then the old one is on the positive side
	                {
	                    // old slow way
	                    //g2.drawLine(xy_old[0],xy_old[1],xyMid_pos[0],xyMid_pos[1]);
	                    //g2.drawLine(xy[0],xy[1],xyMid_neg[0],xyMid_neg[1]);
	                    
	                    // new way
	                    // add final point to positive side
	                    xPts[ptsCount] = xyMid_pos[0];
	                    yPts[ptsCount] = xyMid_pos[1];
	                    ptsCount++;
	                    // draw the polyline
	                    g2.drawPolyline(xPts, yPts, ptsCount);
	                    // clear the arrays (just reset the counter)
	                    ptsCount = 0;
	                    // add the new points to the cleared array
	                    xPts[ptsCount] = xyMid_neg[0];
	                    yPts[ptsCount] = xyMid_neg[1];
	                    ptsCount++;
	                    xPts[ptsCount] = xy[0];
	                    yPts[ptsCount] = xy[1];
	                    ptsCount++;
	                }
	                else // the new one is on the positive side
	                {
	                    // old slow way
	                    //g2.drawLine(xy[0],xy[1],xyMid_pos[0],xyMid_pos[1]);
	                    //g2.drawLine(xy_old[0],xy_old[1],xyMid_neg[0],xyMid_neg[1]);
	                    
	                    // new way
	                    // add final point to neg side
	                    xPts[ptsCount] = xyMid_neg[0];
	                    yPts[ptsCount] = xyMid_neg[1];
	                    ptsCount++;
	                    // draw the polyline
	                    g2.drawPolyline(xPts, yPts, ptsCount);
	                    // clear the arrays (just reset the counter)
	                    ptsCount = 0;
	                    // add the new points to the cleared array
	                    xPts[ptsCount] = xyMid_pos[0];
	                    yPts[ptsCount] = xyMid_pos[1];
	                    ptsCount++;
	                    xPts[ptsCount] = xy[0];
	                    yPts[ptsCount] = xy[1];
	                    ptsCount++;
	                }
	                //==============================================
	                
	                // save info about disconnect
	                disconnectCount++;
	                if(disconnectCount == 1)
	                {
	                    disconnectIndex1 = j;
	                    //System.out.println("Disconnect 1 found:"+disconnectIndex1);
	                    disconnect1pos = xyMid_pos; // save this point
	                    disconnect1neg = xyMid_neg;
	                }
	                else if(disconnectCount == 2)
	                {
	                    disconnectIndex2 = j;
	                    //System.out.println("Disconnect 2 found:"+disconnectIndex2);
	                    disconnect2pos = xyMid_pos; // save this point
	                    disconnect2neg = xyMid_neg;
	                }
	                
	            } // disconnect in line
	            
	            // save point
	            xy_old[0]=xy[0];
	            xy_old[1]=xy[1];
	            llaOld[0] = lla[0];
	            llaOld[1] = lla[1];
	            llaOld[2] = lla[2];
	            
	            // add point to polygon
	            if( fillFootPrint )
	            {
	                footprint.addPoint(xy_old[0],xy_old[1]);
	            }
	            
	        } // for each point around footprint
	        
	        // draw point from last point back to first
	        if( Math.abs(llaOld[1]-lla0[1]) < 4.0)
	        {
	            // old slow way
	            //g2.drawLine(xy_old[0],xy_old[1],xy0[0],xy0[1]);
	           
	            // new way
	             xPts[ptsCount] = xy0[0];
	            yPts[ptsCount] = xy0[1];
	            ptsCount++;
	            
	            // draw remainder of lead segment
	            g2.drawPolyline(xPts, yPts, ptsCount);
	        }
	        
	        // draw polygon -- won't work when part of the foot print is on other side
	        // seems not to work for GEO sats too, fills wrong side at times
	        // see if polygon should be split into two polygons
	        // -- could be divited up 4 seperate peices!
	        // NEED to make this very transparent (works now)
	        // still some issues with fill side: see sun /- one minute around - 23 Mar 2009 01:14:12.000 MDT
	        if( fillFootPrint )
	        {
	            Color satCol = FillColor;
	            Color transColor = new Color();//Color.argb(127,255,0,0);
	            //transColor.alpha(color)Color.argb(127,255,0,0);
	            g2.setPaint( transColor );
	            
	            if(disconnectCount == 0)
	            {
	                // no disconnects fill like normal
	                g2.fill(footprint);
	            }
	            else if(disconnectCount == 1)
	            {
	                // okay this is at a pole, add in edges and fill in
	                // figure out N or S based on sat position ( lat > 0 or < 0)
	                boolean northPoleVisible = ( lat > 0);
	                
	                int[] ptPos = new int[2];
	                int[] ptNeg = new int[2];
	                
	                Polygon fullFootPrint = new Polygon();
	                
	                if(northPoleVisible)
	                {
	                    
	                    ptPos = findXYfromLL(90.0, 180.0, w, h, imageWidth, imageHeight,1,cx,cy);
	                    ptNeg = findXYfromLL(90.0, -180.0, w, h, imageWidth, imageHeight,1,cx,cy);
	                    
	                    // counter clockwise - add points
	                    for(int k=0; k<disconnectIndex1;k++)
	                    {
	                        fullFootPrint.addPoint(footprint.xpoints[k],footprint.ypoints[k]);
	                    }
	                    fullFootPrint.addPoint(disconnect1pos[0],disconnect1pos[1]);
	                    fullFootPrint.addPoint(ptPos[0],ptPos[1]);
	                    fullFootPrint.addPoint(ptNeg[0],ptNeg[1]);
	                    fullFootPrint.addPoint(disconnect1neg[0],disconnect1neg[1]);
	                    for(int k=disconnectIndex1; k<footprint.npoints;k++)
	                    {
	                        fullFootPrint.addPoint(footprint.xpoints[k],footprint.ypoints[k]);
	                    }
	                    
	                }
	                else
	                {
	                    // counter clockwise - add points
	                    ptPos = findXYfromLL(-90.0, 180.0, w, h, imageWidth, imageHeight,1,cx,cy);
	                    ptNeg = findXYfromLL(-90.0, -180.0, w, h, imageWidth, imageHeight,1,cx,cy);
	                    
	                    for(int k=0; k<disconnectIndex1;k++)
	                    {
	                        fullFootPrint.addPoint(footprint.xpoints[k],footprint.ypoints[k]);
	                    }
	                    
	                    fullFootPrint.addPoint(disconnect1neg[0],disconnect1neg[1]);
	                    fullFootPrint.addPoint(ptNeg[0],ptNeg[1]);
	                    fullFootPrint.addPoint(ptPos[0],ptPos[1]);
	                    fullFootPrint.addPoint(disconnect1pos[0],disconnect1pos[1]);
	                    
	                    for(int k=disconnectIndex1; k<footprint.npoints;k++)
	                    {
	                        fullFootPrint.addPoint(footprint.xpoints[k],footprint.ypoints[k]);
	                    }
	                    
	                }// south pole visible
	                
	                // fill full print
	                g2.fill(fullFootPrint);
	                
	                
	            }
	            else if(disconnectCount == 2)
	            {
	                // this is the case when a sat spans the international dateline
	                // make two polygons to fill
	                
	                // polygon starts left of center and goes counter clockwise
	                
	                // check to see if this is a standard case (discontinuity
	                // 1 is vertically lower than discon #2) (y is backwards in java)
	                if( disconnect1neg[1]  >= disconnect2neg[1])
	                {
	                    // STANDARD drawing case for 2 discontinuities
	                    
	                    
	                    
	                    // new polygon - part on left side
	                    Polygon footprintPartLeft = new Polygon();
	                    
	                    // get both sides of discontinuity 1
	                    footprintPartLeft.addPoint( disconnect1neg[0], disconnect1neg[1]); // first point at left edge
	                    for(int k=disconnectIndex1;k<disconnectIndex2;k++)
	                    {
	                        
	                        footprintPartLeft.addPoint(footprint.xpoints[k],footprint.ypoints[k]);
	                    }
	                    footprintPartLeft.addPoint( disconnect2neg[0], disconnect2neg[1]);
	                    
	                    g2.fill(footprintPartLeft);
	                    
	                    // now create Right side of the polygon
	                    Polygon footprintPartRight = new Polygon();
	                    // fill in first part
	                    for(int k=0;k<disconnectIndex1;k++)
	                    {
	                        
	                        footprintPartRight.addPoint(footprint.xpoints[k],footprint.ypoints[k]);
	                    }
	                    // add disconnect points
	                    footprintPartRight.addPoint( disconnect1pos[0], disconnect1pos[1]);
	                    footprintPartRight.addPoint( disconnect2pos[0], disconnect2pos[1]);
	                    // fill in last part
	                    for(int k=disconnectIndex2;k<footprint.npoints;k++)
	                    {
	                        
	                        footprintPartRight.addPoint(footprint.xpoints[k],footprint.ypoints[k]);
	                    }
	                    g2.fill(footprintPartRight);
	                }// standard 2-discont drawing
	                else  // non standard discont 1 is above discont 2
	                {
	                    // new polygon - part on left side
	                    Polygon footprintPartLeft = new Polygon();
	                    
	                    for(int k=0;k<disconnectIndex1;k++)
	                    {
	                        
	                        footprintPartLeft.addPoint(footprint.xpoints[k],footprint.ypoints[k]);
	                    }
	                    footprintPartLeft.addPoint( disconnect1neg[0], disconnect1neg[1]); // left edge top
	                    footprintPartLeft.addPoint( disconnect2neg[0], disconnect2neg[1]); // left edge bottom
	                    for(int k=disconnectIndex2;k<footprint.npoints;k++)
	                    {
	                        
	                        footprintPartLeft.addPoint(footprint.xpoints[k],footprint.ypoints[k]);
	                    }
	                    g2.fill(footprintPartLeft);
	                    
	                    // Right part of foot print
	                    Polygon footprintPartRight = new Polygon();
	                    footprintPartRight.addPoint( disconnect1pos[0], disconnect1pos[1]);
	                    for(int k=disconnectIndex1;k<disconnectIndex2;k++)
	                    {
	                        
	                        footprintPartRight.addPoint(footprint.xpoints[k],footprint.ypoints[k]);
	                    }
	                    footprintPartRight.addPoint( disconnect2pos[0], disconnect2pos[1]);
	                    g2.fill(footprintPartRight);
	                    
	                    
	                }// non standard 2-discont drawing
	                
	            } // 2 disconnect points
	            
	        } // fill foot print
	        
	    } // drawFootPrint
	    // assumes local zoom factor centered centerLat, centerLong
	    //public int[] findXYfromLL(double lat, double lon, int totWidth, int totHeight, int imgWidth, int imgHeight)
	    //{
	    //    return findXYfromLL(lat, lon, totWidth, totHeight, imgWidth, imgHeight, zoomFactor, centerLat, centerLong);
	    //}
	    
	    // includs calculations for zoom Facter, central lat and central longitude- input degrees
	     
	     */
	    public int[] findXYfromLL(double lat, double lon, int totWidth, int totHeight, int imgWidth, int imgHeight, double zoomFac, double cLat, double cLong)
	    {
	        int[] xy = new int[2];
	        
	        // scale to take into account zoom factor
	        //totWidth = (int)Math.round( totWidth*zoomFac );
	        //totHeight = (int)Math.round( totHeight*zoomFac );
	        
	        double longSpan = 360.0/zoomFac; // span of longitude
	        double latSpan = 180.0/zoomFac;
	        
	        int midX = (int) totWidth/2;
	        int midY = (int) totHeight/2;
	        
	        int leftX = midX - imgWidth/2;
	        int rightX = midX + imgWidth/2;
	        
	        int topY = midY - imgHeight/2;
	        int botY = midY + imgHeight/2;
	        
	        xy[0] = (int)( ((rightX-leftX)/longSpan)*(lon-cLong) + (rightX+leftX)/2.0 );
	        xy[1] = (int)( ((topY-botY)/latSpan)*(lat-cLat) + (topY+botY)/2.0 );
	        
	        return xy;
	    }
	    
	    // assumes local zoom factor centered centerLat, centerLong
	    //public double[] findLLfromXY(int x, int y, int totWidth, int totHeight, int imgWidth, int  imgHeight)
	    //{
	     //   return findLLfromXY(x, y, totWidth, totHeight, imgWidth,  imgHeight, zoomFactor, centerLat, centerLong);
	    //}
	    
	    // find LL from xy position - used for clicking on map: - returns in degrees
	    public double[] findLLfromXY(int x, int y, int totWidth, int totHeight, int imgWidth, int  imgHeight, double zoomFac, double cLat, double cLong)
	    {
	        double[] ll = new double[2]; // lat and longitude 
	        
	        double longSpan = 360.0/zoomFac; // span of longitude
	        double latSpan = 180.0/zoomFac;
	        
	        int midX = (int) totWidth/2;
	        int midY = (int) totHeight/2;
	        
	        int leftX = midX - imgWidth/2;
	        int rightX = midX + imgWidth/2;
	        
	        int topY = midY - imgHeight/2;
	        int botY = midY + imgHeight/2;
	        
	        ll[1] = (x - (rightX+leftX)/2.0) /  ((rightX-leftX)/longSpan) + cLong; // longitude
	        ll[0] = (y - (topY+botY)/2.0) / ((topY-botY)/latSpan) + cLat; // latitude
	        
	        // do range tests
	        if(ll[0] > 90)
	            ll[0] = 90;
	        else if(ll[0] < -90)
	            ll[0] = -90;
	        
	        if(ll[1] > 180)
	            ll[1] = 180;
	        else if(ll[1] < -180)
	            ll[1] = -180;
	        
	        return ll; 
	    }	    
	    private static double linearInterpDiscontLat(double lat1, double long1, double lat2, double long2)
	    {
	        // one longitude should be negative one positive, make them both positive
	        if(long1 > long2)
	        {
	            long2 += 360.0;//2*Math.PI; // in radians
	        }
	        else
	        {
	            long1 += 360.0;//2*Math.PI;
	        }
	        
	        return  ( lat1+(180.0 - long1)*(lat2-lat1)/(long2-long1) );
	    }
	    // math utilities
	    // multiply two matrices 3x3
	    public static double[][] mult(double[][] a, double[][] b)
	    {
	        double[][] c = new double[3][3];
	        
	        for (int i = 0; i < 3; i++) // row
	        {
	            for (int j = 0; j < 3; j++) // col
	            {
	                c[i][j] = 0.0;
	                for (int k = 0; k < 3; k++)
	                {
	                    c[i][j] += a[i][k] * b[k][j];
	                }
	            }
	        }
	        
	        return c;
	        
	    } // mult 3x3 matrices
	    
	    // multiply matrix 3x3 by vector 3x1
	    public static double[] mult(double[][] a, double[] b)
	    {
	        double[] c = new double[3];
	        
	        for (int i = 0; i < 3; i++) // row
	        {
	            c[i] = 0.0;
	            for (int k = 0; k < 3; k++)
	            {
	                c[i] += a[i][k] * b[k];
	            }
	        }
	        
	        return c;
	        
	    } // mult 3x3 matrices	    
	    
}
