import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Indexer extends Thread {
    private final String rootPath;
    private final List<File> fileList;
    private final HashMap<String, HashSet<Integer>> map;

    public Indexer (String path, List<File> list, int startIndex, int endIndex) {
        rootPath = path;
        fileList = new ArrayList<>(list.subList(startIndex, endIndex));
        map = new HashMap<>();
    }

    public static String parse(File file) {
        String line = "";
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String buff;
            while((buff = br.readLine()) != null) {
                line = line.concat(buff);
            }
            String[] chars = {"/", "\\", "<", ">", ".", ",", ":", "?", "!", "(", ")", "*", "[", "]", ";", "\"" , ""};
            for(String i: chars) {
                line = line.replace(i, "");
            }
        }
        catch(IOException e) {e.getMessage();}
        return line;
    }

    public static int setFileID(File dir, int pathLen) {
        String path = dir.getAbsolutePath().substring(pathLen);
        String[] folders = path.split("\\\\");
        folders[folders.length-1] = folders[folders.length-1].split("_")[0];

        HashMap<String, Integer> dict = new HashMap<>();
        dict.put("test", 1);
        dict.put("train", 2);
        dict.put("neg", 1);
        dict.put("pos", 2);
        dict.put("unsup", 3);

        return Integer.parseInt(String.valueOf(dict.get(folders[1])) + String.valueOf(dict.get(folders[2])) + String.valueOf(folders[3]));
    }

    public HashMap<String, HashSet<Integer>> getMap() {
        return map;
    }

    @Override
    public void run() {
        for(File file: fileList) {
            String[] lexemes = Indexer.parse(file).split(" ");
            int FileID = Indexer.setFileID(file, rootPath.length());
            for(String word: lexemes) {
                if(!map.containsKey(word)) {
                    map.put(word, new HashSet<>());
                }
                map.get(word).add(FileID);
            }
        }
    }
}
