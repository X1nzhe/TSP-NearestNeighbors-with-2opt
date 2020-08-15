/**
 * Implements the Nearest Neighbour algorithm for the TSP, and
 * an iterative improvement method that uses 2-OPT greedily.
 * Results are returned as an array of indices into the table argument, 
 * e.g. if the table has length four, a valid result would be {2,0,1,3}. 
 */
import java.lang.reflect.Array;
import java.util.*;

public class NearestNeighbour
{
    private static boolean[] visited;//Set true iff a city is visited, otherwise

    private NearestNeighbour(){}

    //Reset the visited List to all false
    private static void resetVisited(){
        Arrays.fill(visited, false);
    }

    //Return true iff there is any city unvisited
    private static boolean hasUnvisited(){
        for(boolean each : visited)
            if(!each) return true;
        return false;
    }
    /**
     * Returns the shortest tour found by exercising the NN algorithm 
     * from each possible starting city in table.
     * table[i][j] == table[j][i] gives the cost of travel between City i and City j.
     */
    public static int[] tspnn(double[][] table)
    {
        int[] shortestTour  = new int[table.length];//The shortest tour we have found
        int[] currTour      = new int[table.length];//The current tour we are going through
        int next = -1, currentCity; // Indices of cities
        double lastCost = Double.POSITIVE_INFINITY, totalCost, currCost; //Costs of tours
        visited = new boolean[table.length];

        // Run NN algorithm for every possible city entry
        for(int city = 0; city < table.length; city++)
        {
            resetVisited();//Reset the visited List to all false as no city visited
            visited[city] = true; // Set the initial city as visited
            currentCity = city;//City0
            currTour[0] = currentCity;//Put the initial city into current tour list
            totalCost = 0;
            int index = 1;//Index of currTour

            while(hasUnvisited()) //When there is any city we have not visited yet
            {
                currCost = Double.POSITIVE_INFINITY;//Initializing current cost...

                for(int neighbour = 0; neighbour < table.length; neighbour++)
                {   //Find out the unvisited and nearest neighbour
                    if(!visited[neighbour] && (table[currentCity][neighbour] < currCost))//If a neighbour city is closer and we have not visited it yet
                    {
                        currCost = table[currentCity][neighbour];//Record the cost from current city to the nearest neighbour
                        next = neighbour;//Set the neighbour as the next city we may be going to
                    }
                }

                //Now, we have found the nearest and unvisited city for current city
                totalCost += currCost;//Count cost

                //Move on to the nearest city
                visited[next] = true;
                currentCity = next;
                currTour[index++] = currentCity;
            }

            // All cities have been visited
            totalCost += table[city][next];//Add the cost from source city to destination to form a circle tour
            if(totalCost < lastCost)//If the total cost of current tour is less than the cost of last tour, swap them
            {
                lastCost = totalCost;
                System.arraycopy(currTour,0,shortestTour,0,table.length);
            }
        }

        return shortestTour;
    }
    
    /**
     * Uses 2-OPT repeatedly to improve cs, choosing the shortest option in each iteration.
     * You can assume that cs is a valid tour initially.
     * table[i][j] == table[j][i] gives the cost of travel between City i and City j.
     */
    public static int[] tsp2opt(int[] cs, double[][] table)
    {   //Iinitialzing...
        int[] bestTour = new int[cs.length];
        boolean newSearch = false;
        //int[] currTour = new int[cs.length];
        System.arraycopy(cs,0,bestTour,0,cs.length);

        //if(cs.length == 40)System.out.println(Arrays.toString(cs));//test
        for(int maxCount = cs.length*25 ;maxCount>0;maxCount--)
        {
            double bestCost = totalCost(bestTour,table);//test
            for(int i = 0; i < cs.length-1; i++){
                for(int k = i+1; k <cs.length;k++){
                    int[] newTour = optSwap(bestTour,i,k);
                    //double newCost = ;
                    //if(cs.length == 40)System.out.println(bestCost);//test
                    //if(cs.length == 40)System.out.println(totalCost(newTour,table));//test
                    if(bestCost > totalCost(newTour,table))
                    {
                        if(cs.length == 40) System.out.println("True");//test
                        System.arraycopy(newTour,0,bestTour,0,newTour.length);
                        //bestCost = newCost;
                        if(cs.length == 40)System.out.println(Arrays.toString(bestTour));//test
                        newSearch= true;
                        break;
                    }
                }
                if(newSearch)break;

            }
            /*
            //System.arraycopy(bestTour,0,currTour,0,currTour.length);
            for(int i = 0; i < cs.length-1; i++){
                for(int k = i+1; k <cs.length;k++){
                    int[] newTour = optSwap(bestTour,i,k);
                    if(isImproved(bestTour,i,k,table)){
                       if(cs.length == 40) System.out.println("True");//test
                        System.arraycopy(newTour,0,bestTour,0,newTour.length);
                        if(cs.length == 40)System.out.println(Arrays.toString(bestTour));//test
                        break;
                    }
                }
               break;
            }

             */
        }


        return bestTour;
    }

    private static boolean isImproved(int[] bestTour, int i , int k, double[][]table)
    {
        if(i == 0){
            if(k == bestTour.length-1){
                return false;
            }
            return ((table[bestTour[0]][bestTour.length-1] +table[bestTour[k]][bestTour[k+1]]) >
                    (table[bestTour[0]][bestTour[k+1]] + table[bestTour[k]][bestTour.length-1]));
        }else
            {
                if( k == bestTour.length-1){
                    return ( (table[bestTour[0]][bestTour[k]] + table[bestTour[i-1]][bestTour[i]])>
                            (table[bestTour[0]][bestTour[i]]+ table[bestTour[i-1]][bestTour[k]]));
                }else
                    {
                        return ((table[bestTour[i-1]][bestTour[i]]+table[bestTour[k]][bestTour[k+1]])
                                > (table[bestTour[i-1]][bestTour[k]]+table[bestTour[i]][bestTour[k+1]]));
                    }

            }

    }

    private static int[] optSwap(int[] cs,int i, int k){
        int[] res = new int[cs.length];
        System.arraycopy(cs,0, res, 0,i+1);
        int []rev = reverse(Arrays.copyOfRange(cs,i,k + 1));
        System.arraycopy(rev,0,res, i, rev.length);
        System.arraycopy(cs,k + 1, res,k + 1,cs.length - k-1);
        //test
        //if(cs.length == 40) System.out.println("Swap："+Arrays.toString(res));
            //test
        return res;
}

private static int[] reverse(int[] arr )
{
        int n = arr.length;
        int[] res = new int[n];
        for (int k : arr) res[--n] = k;
        return res;
}
    private static double totalCost(int[]  cs, double[][]table){
        int n = table.length;
        double z = table[cs[0]][cs[n-1]];
        for (int k = 0; k < n-1; k++) z += table[cs[k]][cs[k+1]];
        return z;
    }
}
