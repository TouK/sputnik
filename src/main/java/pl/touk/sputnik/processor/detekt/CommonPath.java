package pl.touk.sputnik.processor.detekt;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class CommonPath {
    private List<List<String>> directoriesPerFile;

    CommonPath(List<String> files) {
        directoriesPerFile = new ArrayList<>();
        for (String file : files) {
            String[] split = file.split("/");
            directoriesPerFile.add(Arrays.asList(split));
        }
    }

    String find() {
        List<String> commonDirs = new ArrayList<>();
        int i = 0;
        while (true) {
            Set<String> currentDir = new HashSet<>();
            for (List<String> directories : directoriesPerFile) {
                if (directories.size() <= i) {
                    return Joiner.on("/").join(commonDirs);
                }
                currentDir.add(directories.get(i));
            }
            ++i;
            if (currentDir.size() == 1) {
                commonDirs.add(currentDir.iterator().next());
            } else {
                break;
            }
        }
        return Joiner.on("/").join(commonDirs);
    }
}
