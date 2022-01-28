import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ParallelIndexer {
    private Indexer[] IndexArray;
    private final HashMap<String, HashSet<Integer>> resultDict;
    private final long resultTime;

    public ParallelIndexer(int NumOfThreads, String path, List<File> fileList) {
        List<HashMap<String, HashSet<Integer>>> dictList;
        IndexArray = new Indexer[NumOfThreads];
        long start = System.currentTimeMillis();

        for(int i = 0; i < NumOfThreads; i++) {
            IndexArray[i] = new Indexer(path, fileList, (fileList.size() / NumOfThreads) * i,
                    i == (IndexArray.length - 1) ? fileList.size() : fileList.size() / NumOfThreads * (i + 1) );
            IndexArray[i].start();
        }

        try {
            for (int i = 0; i < NumOfThreads; i++) {
                IndexArray[i].join();
            }
        } catch (InterruptedException e) {e.getMessage();}

        dictList = new ArrayList<>();

        for(int i = 0; i < NumOfThreads; i++) {
            dictList.add(IndexArray[i].getMap());
        }

        resultDict = dictList.get(0);
        dictList.remove(0);

        for(HashMap<String, HashSet<Integer>> dict : dictList) {
            for (Map.Entry<String, HashSet<Integer>> entry : dict.entrySet()) {
                if(!resultDict.containsKey(entry.getKey())) {
                    resultDict.put(entry.getKey(), new HashSet<>());
                }
                for(Integer ID : entry.getValue()) {
                    resultDict.get(entry.getKey()).add(ID);
                }
            }
        }

        long end = System.currentTimeMillis();
        resultTime = end - start;
    }

    public long getResultTime() {
        return resultTime;
    }

    public HashMap<String, HashSet<Integer>> getDict() {
        return resultDict;
    }
}
