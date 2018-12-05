package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javafx.scene.shape.Rectangle;

public class Cluster {

	  //clustering lists variables
	  static Vector<Point> VisitList = new Vector<Point>();
	  static Vector<Point> hset = new Vector<Point>();
	  static Vector<List> trl = new Vector<List>();
	  

	  
	  // bounding box display of clustering
	  private static ArrayList<Rectangle> DBBoxList = new ArrayList<>();
	  
	  public static void clusterData(LinkedList<Stroke> data) {
		  
			// TODO Auto-generated method stub  
	      trl.clear();
	      hset.clear();
	      for (Stroke s : data) {
	          hset.addAll(s.getPointsSet());
	      }
	      
	      trl.addAll(DBScan.applyDbscan());
	      System.out.println("CLUSTER DATA: " + DBScan.resultList.size());
	      for (int i = 0; i < data.size(); i++) {
	          for (int j = 0; j < DBScan.resultList.size(); j++)
	          {
	              List c = DBScan.resultList.get(j);
	              Stroke s = data.get(i);
	              //System.out.println(s.getFirstPoint());
	              if (c.contains(s.getFirstPoint())){
	                  data.get(i).setStrokeCluster(j); 
	              }
	          }
	      }
	  }
	  
	  public static ArrayList<Rectangle> boundClusterBox() {
		  // list for x and y arrays for bounding boxes
		  ArrayList<Double> xPointList = new ArrayList<>();
		  ArrayList<Double> yPointList = new ArrayList<>();
		  
		  double dbScanBoxMaxX = 0;
		  double dbScanBoxMaxY = 0;
		  double dbScanBoxMinX = 0;
		  double dbScanBoxMinY = 0;
		  
		  //variable to hold rectangle object ot 
		  Rectangle r = null;
	      
	      int index1 = 0;
	      for(List l : trl){
	    	  //System.out.println(l.toString());
	    	  //System.out.println("Cluster  :" + (index1 + 1));

	    	  // clear list for each stroke
	    	  xPointList.clear();
              yPointList.clear();

	    	  Iterator<Point> j = l.iterator();

	    	  while (j.hasNext()) {
	    		  Point w = j.next();
	    		  yPointList.add(w.getX());
	    		  xPointList.add(w.getY());

	    		  //System.out.println(w.getX() + "," + w.getY());
	    	  }
	    	  	//System.out.println("***************");

                dbScanBoxMaxX = Collections.max(xPointList);
                dbScanBoxMaxY = Collections.max(yPointList);
                dbScanBoxMinX = Collections.min(xPointList);
                dbScanBoxMinY = Collections.min(yPointList);

                index1++;

                r = new Rectangle(dbScanBoxMinX, dbScanBoxMinY,
                        dbScanBoxMaxX - dbScanBoxMinX,
                        dbScanBoxMaxY - dbScanBoxMinY);
                DBBoxList.add(r);
                }
		return DBBoxList;
		}
	  
	  
	  public static double getDistance (Point p, Point q)
	    {

	        double dx = p.getX()-q.getX();

	        double dy = p.getY()-q.getY();

	        double distance = Math.sqrt (dx * dx + dy * dy);

	        return distance;

	    }


	    /**
	     neighbourhood points of any point p
	     **/
	    public static Vector<Point> getNeighbours(Point p)
	    {
	        Vector<Point> neigh =new Vector<Point>();
	        Iterator<Point> points = DBScan.pointList.iterator();
	        while(points.hasNext()){
	            Point q = points.next();
	            if(getDistance(p,q)<= DBScan.e){
	                neigh.add(q);
	            }
	        }
	        return neigh;
	    }

	    public static void Visited(Point d){
	        VisitList.add(d);

	    }

	    public static boolean isVisited(Point c)
	    {
	        if (VisitList.contains(c))
	        {
	            return true;
	        }
	        else
	        {
	            return false;
	        }
	    }

	    public static Vector<Point> Merge(Vector<Point> a, Vector<Point> b)
	    {

	        Iterator<Point> it5 = b.iterator();
	        while(it5.hasNext()){
	            Point t = it5.next();
	            if (!a.contains(t) ){
	                a.add(t);
	            }
	        }
	        return a;
	    }

	    public static Vector<Point> getList() {

	        Vector<Point> newList =new Vector<Point>();
	        newList.clear();
	        newList.addAll(hset);
	        return newList;
	    }

	    public Boolean equalPoints(Point m , Point n) {
	        if((m.getX()==n.getX())&&(m.getY()==n.getY()))
	            return true;
	        else
	            return false;
	    }

		public static void clearData() {
			DBBoxList.clear();		
		}
}
