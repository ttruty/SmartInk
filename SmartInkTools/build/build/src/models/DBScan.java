package models;
import java.util.List;
import java.util.Vector;


public class DBScan
{
    public static double minDist= 15; //min distance
    public static int minPt =2; //min number of points
    
    Cluster cluster = new Cluster();

    public static Vector<List> resultList = new Vector<List>();

    public static Vector<Point> pointList = Cluster.getList();

    public static Vector<Point> Neighbours ;

    public static Vector<List> applyDbscan()
    {
    	//clear the list for each run
        resultList.clear();
        pointList.clear();
        Cluster.VisitList.clear();
        pointList=Cluster.getList();

        int index2 =0;
        while (pointList.size()>index2){
        	//clustering algorithm 
            Point p =pointList.get(index2);
            if(!Cluster.isVisited(p)){ //do not run if the point is visited

                Cluster.Visited(p);

                Neighbours =Cluster.getNeighbours(p);


                if (Neighbours.size()>=minPt){


                    int ind=0;
                    while(Neighbours.size()>ind){

                        Point r = Neighbours.get(ind);
                        if(!Cluster.isVisited(r)){
                            Cluster.Visited(r);
                            Vector<Point> Neighbours2 = Cluster.getNeighbours(r);
                            if (Neighbours2.size() >= minPt){
                                Neighbours=Cluster.Merge(Neighbours, Neighbours2);
                            }
                        } ind++;
                    }

                    //System.out.println("N"+Neighbours.size());
                    resultList.add(Neighbours);}


            }index2++;
        }return resultList;
    }

}









