import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunningWindow extends JFrame {


    public RunningWindow(List<File> runningPartitions) {
        //窗口属性
        this.setTitle("HDDKicker Running...");
        this.setSize(320, 200);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //正在运行的分区信息
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Running on\n");
        runningPartitions.forEach(v -> stringBuilder.append(v.getPath()).append(", "));
        this.add(new JTextArea(stringBuilder.toString()), BorderLayout.NORTH);

        //提示信息
        this.add(new JLabel("Close the window to stop!"), BorderLayout.SOUTH);

        //show
        this.setVisible(true);

        //启动线程
        ExecutorService executorService = Executors.newCachedThreadPool();
        runningPartitions.forEach(file -> executorService.submit(() -> {
            new FileWritingThreadWorker(file).run();
        }));
    }
}
