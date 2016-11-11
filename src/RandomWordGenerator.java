import java.io.BufferedReader;
import java.util.Random;
import java.util.Iterator;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RandomWordGenerator {

  private Random random = new Random();
  private int errCount = 0;
  private final String filePrefix = "../assets/words/";
  private final String[] categoryList = {"easy", "normal", "hard"};

  private HashMap<String, LinkedList<String>> categories = new HashMap<String, LinkedList<String>>();

  RandomWordGenerator(){

    for(String key : categoryList){

      String filename = filePrefix + key.concat(".txt");
      LinkedList<String> list = initializeWordList(filename);

      if(errCount==categoryList.length){
        System.out.println("Zero categories initialized, unable to proceed the game.");
        System.exit(-1);
      }

      categories.put(key, list);

    }

  }

  private LinkedList<String> initializeWordList(String filename){

    LinkedList<String> list = new LinkedList<String>();

    try{
      BufferedReader reader = new BufferedReader(new FileReader(filename));

      String line;

      // read while not end of file, read a line
      while( (line = reader.readLine()) != null ) {

          if( line.isEmpty() ) continue;

          list.add(line);
      }
      reader.close();

    } catch(FileNotFoundException ex) {
      System.out.println("Unable to open file '" + filename + "'");
      errCount+=1;
    } catch(IOException ex) {
      System.out.println("Error reading file '" + filename + "'");
      errCount+=1;
    }

      return list;

  }

  private int generateRandomInt(int max){

    return random.nextInt(Math.max(max,0));

  }

  private String generateRandomString(String category){

    if(!seededCategory(category)){
      return null;
    }

    LinkedList<String> list = categories.get(category);
    int index = generateRandomInt(list.size());

    return list.get(index);

  }

  public String getWordFromCategory(String category){

    return generateRandomString(category);

  }

  private boolean seededCategory(String category){

    return (categories.containsKey(category) &&
            !categories.get(category).isEmpty() &&
            categories.get(category) != null);

  }

  public static void main(String[] args) {
    RandomWordGenerator r = new RandomWordGenerator();
  }
}
