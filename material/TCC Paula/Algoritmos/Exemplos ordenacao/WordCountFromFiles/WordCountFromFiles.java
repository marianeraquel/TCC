package org.karticks.mapreduce;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

/**
 * A class that counts the occurences of words from different files using Map/Reduce.
 * This class assumes that files just contain words separated by spaces, and do not
 * contain any punctuations or line-endings. The files should be located in the
 * classpath of the program.
 *  
 * @author Kartick Suriamoorthy
 *
 */
public class WordCountFromFiles
{
        public static void main(String[] args)
        {
                try
                {
                        String[] files = null;
                       
                        // Uncomment the following two lines if you want run the word count example
                       
                        // files = {"frankenstein-shelley-excerpt.txt", "iliad-homer-excerpt.txt", "metamorphosis-kafka-excerpt.txt",
                                        //"prince-machiavelli-excerpt.txt", "ulysses-joyce-excerpt.txt"};
                       
                        // Uncomment the following lines if you want to run the sorting example
                       
                        // files = {"integers_to_be_sorted1.txt", "integers_to_be_sorted2.txt", "integers_to_be_sorted3.txt",
                                        //"integers_to_be_sorted4.txt", "integers_to_be_sorted5.txt"};
                       
                        MapReduceWorker worker = new MapReduceWorker();
                       
                        for (String file : files)
                        {
                                Mapper mapper = new WordCountMapper();
                                InputStream is = worker.getClass().getResourceAsStream("/" + file);
                               
                                worker.addMapper(mapper, is);
                        }
                       
                        Map<String, Integer> result = worker.doWork();
                       
                        Iterator<String> iterator = result.keySet().iterator();
                       
                        StringBuffer buffer = new StringBuffer();
                        StringBuffer highCount = new StringBuffer();
                       
                        Integer totalCount = 0;
                       
                        while (iterator.hasNext())
                        {
                                String key = iterator.next();
                               
                                Integer value = result.get(key);
                               
                                 buffer.append(key + " (" + value + "), ");
                                 
                                 if (value >= 50)
                                 {
                                         highCount.append(key + " (" + value + "), ");
                                 }
                                 
                                 totalCount += value;
                        }
                       
                        String output = buffer.substring(0, buffer.length() - 2);
                       
                        System.out.println("=====================================================");
                        System.out.println("Final result : " + output + ".");
                       
                        String highFrequencyWords = highCount.substring(0, highCount.length() - 2);
                       
                        System.out.println("=====================================================");
                        System.out.println("High frequency (greater than or equal to 10) words : " + highFrequencyWords + ".");
                       
                        System.out.println("=====================================================");
                        System.out.println("Total count of words : " + totalCount + ".");
                        System.out.println("=====================================================");
                }
                catch (Throwable t)
                {
                        System.out.println("Caught an exception. Error message : " + t.getMessage());
                        t.printStackTrace();
                }
        }
}

