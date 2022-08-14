import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Application {


    public static void main(String[] args) {
        //设置主题为系统
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //加载上次使用的分区
        //创建分区选择窗口，开始工作
        new PartitionSelectingWindow(readLastTimeFile());
    }


    /**
     * 从文件中读取上次使用的分区
     *
     * @return 上次使用的分区根目录路径列表，如果不存在上次，则返回NULL
     */
    public static List<String> readLastTimeFile() {
        //
        File file = new File("./LastTimePartition.txt");
        if (!file.exists()) return null;
        //
        List<String> partitions = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while (bufferedReader.ready()) partitions.add(bufferedReader.readLine());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return partitions;
    }
}
