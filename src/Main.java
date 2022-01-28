// Варіант 9;
// Для директорії unsup N = 50000, для решти N = 12500;
// Початковий індекс згідно до варіанту: N / 50 * (V – 1) = 8000 (для unsup), = 2000 (для решти);
// Кінцевий індекс згідно до варіанту: N / 50 * V = 9000 (для unsup), = 2250 (для решти);

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Main {
    public static void main(String[] args) throws IOException {
        int NumberOfThreads;
        System.out.println("Enter num of Threads: ");
        Scanner in = new Scanner(System.in);
        NumberOfThreads = in.nextInt();

        String path = "C:\\Users\\olena\\course_work_parallel_computing\\datasets\\aclImdb";
        List<File> files = new ArrayList<>();
        Files.walk(Paths.get(path), FileVisitOption.FOLLOW_LINKS)
                .parallel()
                .map(Path::toFile)
                .forEach(f -> {
                    if(f.getAbsolutePath().contains("unsup")) {
                        // find names from 8k to 9k
                        if(f.getName().matches("8\\d{3}.*?"))
                            files.add(new File(f.getAbsolutePath()));
                    } else {
                        // find name from 2k to 2250
                        if(f.getName().matches("2(([0-1]\\d{2})|([2][0-4]\\d)).*?")) {
                            files.add(new File(f.getAbsolutePath()));
                        }
                    }
                });

        ParallelIndexer indexer = new ParallelIndexer(NumberOfThreads, path, files);

        System.out.println("Num of lexemes -> " + indexer.getDict().size());
        System.out.println("Time -> " + indexer.getResultTime());
        System.out.println("Number of threads -> " + NumberOfThreads);

        long start = System.currentTimeMillis();
        HashMap<String, HashSet<Integer>> map = new HashMap<>();
        for(File file: files) {
            String[] lexemes = Indexer.parse(file).split(" ");
            int FileID = Indexer.setFileID(file, path.length());
            for(String word: lexemes) {
                if(!map.containsKey(word)) {
                    map.put(word, new HashSet<>());
                }
                map.get(word).add(FileID);
            }
        }
        long end = System.currentTimeMillis();

        System.out.println();
        System.out.println("Num of lexemes -> " + map.size());
        System.out.println("Sequence time -> " + (end - start));

        System.out.println();
        System.out.println("Same amount of lexemes? " + map.equals(indexer.getDict()));
    }
}
