import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ParallelIndexer {
    private final ConcurrentHashMap<String, HashSet<Integer>> resultDict = new ConcurrentHashMap<>();
    private final long resultTime;

    public ParallelIndexer(int NumOfThreads, String path, List<File> fileList) {
        Indexer[] indexArray = new Indexer[NumOfThreads];
        long start = System.currentTimeMillis();

        //create and start threads
        for(int i = 0; i < NumOfThreads; i++) {
            indexArray[i] = new Indexer(path, fileList, (fileList.size() / NumOfThreads) * i,
                    i == (indexArray.length - 1) ? fileList.size() : fileList.size() / NumOfThreads * (i + 1), resultDict );
            indexArray[i].start();
        }

        try {
            for (int i = 0; i < NumOfThreads; i++) {
                indexArray[i].join();
            }
        } catch (InterruptedException e) {e.getMessage();}

        long end = System.currentTimeMillis();
        resultTime = end - start;
    }

    public long getResultTime() {
        return resultTime;
    }

    public ConcurrentHashMap<String, HashSet<Integer>> getDict() {
        return resultDict;
    }
}
