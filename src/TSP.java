import java.util.*;
import java.util.Random;                        // for random Function
import java.util.concurrent.ThreadLocalRandom; // for shuffle array
import java.io.*;                              // reading file 
import static java.lang.System.in;
/**
 *
 * @author Mohammad Shahzaib / Muhammad Asad
 */
class population {
        public int cityCount;
        public int size;
        public int samplesize;
        public double mutationRate;
        public int Array[][];
        public int weight[][];
        
        static void shuffleArray(int[] ar){
            // If running on Java 6 or older, use `new Random()` on RHS here
            Random rnd = ThreadLocalRandom.current();
            for (int i = ar.length - 1; i > 0; i--)
            {
              int index = rnd.nextInt(i + 1);
              // Simple swap
              int a = ar[index];
              ar[index] = ar[i];
              ar[i] = a;
            }
        }
        
        population(int size, int cityCount, int sampsize , double mutationrate, String filename){   //Creating Population
            this.size = size;
            this.cityCount = cityCount;
            this.mutationRate = mutationrate;
            this.samplesize = cityCount*(sampsize/100);
            weight = new int[cityCount][cityCount];
            readingweight(filename);
            Array = new int [size][cityCount+1];
            int temp[] = new int[cityCount-1];
            
            
            for(int a=1; a<cityCount; a++){
                temp[a-1] = a;
            }
            for(int i=0; i<size; i++){
                shuffleArray(temp);
                for(int j=0; j<cityCount+1; j++){
                    if(j == 0 || j == cityCount){
                        Array[i][j] = 0;
                    }
                    else{
                        Array[i][j] = temp[j-1];
                    }
                }
            }
        }
        
        void readingweight(String filename){
            Scanner file = null;
            try{
                file = new Scanner(new File(filename));
            }catch(Exception e){
                System.out.println("Error While Opening File\n");
            }
            int i=0;
            int j=0;
            while(file.hasNextInt()){
                if(i == cityCount){
                    break;
                }
                int a = file.nextInt();
                weight[i][j] = a;
                j++;
                if( j == cityCount){
                    j = 0;
                    i++;
                }
                
            }
        }
        
        void print_pop(){
            for(int i=0; i<size; i++){
                for(int j=0; j <=cityCount ; j++){
                    System.out.print(Array[i][j] + " ");
                }
                System.out.println();
            }
        }
        
        void print_weig(){
            for(int i=0; i< cityCount; i++){
                for(int j=0; j < cityCount ; j++){
                    System.out.print(weight[i][j] + " ");
                }
                System.out.println();
            }
        }
        
        int getFitness(int array[]){
            int sum = 0;
            for(int i=0; i<cityCount-1; i++){
                int a= array[i];
                int b= array[i+1];
                sum += weight[a][b];
            }
            return sum;
        }// Returns the fitness of each path
        
        int returnFittest(int sampArray[]){    //Return the fittest array/parent
            createSample(sampArray);
            int min = 0;
            for(int i=1; i<samplesize; i++){
                if(getFitness(Array[sampArray[min]]) > getFitness(Array[sampArray[i]])){
                    min = i;
                }
            }
            return min;
        }
        
        void createSample(int Sample[]){    // Generate the random sample 
            int sampArray[] = new int[size];
            Random shuffle = new Random();
            for(int i=0;i<size; i++){
                sampArray[i] = i;
            }
            shuffleArray(sampArray);
            int i=0;
            for(int j=1; j<samplesize; j++){
                Sample[j] = sampArray[i];
                j++;
                i = (shuffle.nextInt()%cityCount) + 1;
            }
        }
        
        int[] getChild(){
            int child[] = new int[cityCount+1];
            int parent1[] = new int[cityCount+1];
            int parent2[] = new int[cityCount+1];
//            do{
                int sampArray[] = new int[samplesize];
                int p1 = returnFittest(sampArray);
                int p2 = returnFittest(sampArray);
                for(int i=0; i<=cityCount; i++){
                    parent1[i]= Array[p1][i];
                    parent2[i]= Array[p2][i];
                }
                child = crossover(parent1,parent2);
//           }while(getFitness(child) > getFitness(parent2) || getFitness(child) > getFitness(parent1));
            return child;
        }
        
        int[] crossover(int p1[], int p2[]){
            int child[] = new int[cityCount+1];
            int stPos = new Random().nextInt(10)%(cityCount-1) + 1;
            int endPos = new Random().nextInt(10)%(cityCount-1) +1 ;
            if(stPos > endPos){
                int a=stPos;
                stPos = endPos;
                endPos = a;
            }          
            child[0] = 0;
            child[7] = 0;
            for(int i=stPos; i<= endPos; i++){
                child[i] = p1[i];
            } 
            int Parent2 = cityCount-1;
            int c = cityCount-1;
            for(int i=cityCount-1; c>0; i--){
                boolean swap = true;
                for(int j=stPos; j<=endPos; j++){
                    if(p2[Parent2] == p1[j] ){
                        swap = false;
                        break;
                    }
                }
                if(swap == true){
                    while(c>=stPos && c<=endPos){
                        c--;
                    }
                    child[c] = p2[Parent2];
                    c--;
                    Parent2--;
                }
                else{
                    Parent2--;
                }
            }
            return Mutate(child);
        }

        int[] Mutate(int child[]){
                int Pos2;
                double rnd = Math.floor(Math.random() * 1);
                Random rand = new Random();
                for(int Pos1=1; Pos1<cityCount; Pos1++){
                    if(rnd < mutationRate){
                        Pos2 = rand.nextInt(cityCount-1)+1;
                        int temp = child[Pos1];
                        child[Pos1] = child[Pos2];
                        child[Pos2] = temp;        
                    }    
                }
            return child;
        }
         
        
        int[][] nextgeneration(){
            int generation[][] = new int [size][cityCount+1];
            for(int i=0; i<size; i++){
                generation[i] = getChild();
            }
            return generation;
        }

}

public class TSP {
    
    public population p;
    int [][] generation;
    int generationCount;
    int best;
    int desire;
    
    TSP(int size, int cityCount, int sampsize , double mutationrate, int desire,String filename){
        this.p = new population(size, cityCount, sampsize, mutationrate, filename);
        this.generation = new int [size][cityCount+1];
        this.generation = p.Array;
        this.desire = desire;
        this.generationCount = 0;
    } 
    
    void output(){
        System.out.println("Generation# "+ generationCount);
        for(int i=0; i<=p.cityCount; i++){
            System.out.print(generation[best][i]+ " ");
        }
        System.out.println("\nValue: " + (p.getFitness(generation[best])));   
    }
    
    void bestChromosome(){
        int min = 0;
        for(int i=0; i<p.size; i++){
            if(p.getFitness(generation[i]) > p.getFitness(generation[min])){
                min = i;
            }
        }
        best = min;
    }
    
    void generates(){
        for(int i=0; i<desire; i++){
            generation = p.nextgeneration();
            //if(i%10 == 0){
                bestChromosome();
                output();
            //}
            p.Array = generation;
            generationCount++;
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner s = new Scanner(in);
        String path = "C:\\Users\\muham\\Documents\\NetBeansProjects\\TSP\\src\\gr21.txt"; 
//        String name;
//        name = s.
        
        TSP graph = new TSP(3500, 21, 5, 0.15, 10000, path);
        graph.generates();
    }
}

